package com.example.datossinmvvm

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenMicroorganismo() {
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, "microbiologia_db").build()
    }
    val dao = db.microorganismoDao()
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    var listaMuestras by remember { mutableStateOf(emptyList<Microorganismo>()) }
    var muestraSeleccionada by remember { mutableStateOf<Microorganismo?>(null) }

    fun recargarLista() {
        scope.launch {
            listaMuestras = dao.obtenerTodos()
        }
    }

    LaunchedEffect(Unit) {
        recargarLista()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detección de Microorganismos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    IconButton(onClick = { recargarLista() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Formulario
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del Microorganismo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = tipo,
                onValueChange = { tipo = it },
                label = { Text("Tipo (Bacteria, Hongo, Protozoo...)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción / Detalles") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (nombre.isNotBlank() && tipo.isNotBlank()) {
                            scope.launch {
                                if (muestraSeleccionada == null) {
                                    // CREAR
                                    dao.insertar(
                                        Microorganismo(
                                            nombre = nombre,
                                            tipo = tipo,
                                            descripcion = descripcion
                                        )
                                    )
                                } else {
                                    // ACTUALIZAR
                                    dao.actualizar(
                                        muestraSeleccionada!!.copy(
                                            nombre = nombre,
                                            tipo = tipo,
                                            descripcion = descripcion
                                        )
                                    )
                                    muestraSeleccionada = null
                                }
                                nombre = ""
                                tipo = ""
                                descripcion = ""
                                recargarLista()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (muestraSeleccionada == null) "Guardar Registro" else "Actualizar")
                }

                if (muestraSeleccionada != null) {
                    Button(
                        onClick = {
                            muestraSeleccionada = null
                            nombre = ""
                            tipo = ""
                            descripcion = ""
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Registros en Base de Datos (Toca uno para editar):", fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))

            // Lista (Lectura y Eliminación)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listaMuestras) { muestra ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                muestraSeleccionada = muestra
                                nombre = muestra.nombre
                                tipo = muestra.tipo
                                descripcion = muestra.descripcion
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = muestra.nombre,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Tipo: ${muestra.tipo}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                if (muestra.descripcion.isNotBlank()) {
                                    Text(
                                        text = muestra.descripcion,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        dao.eliminar(muestra)
                                        if (muestraSeleccionada?.id == muestra.id) {
                                            muestraSeleccionada = null
                                            nombre = ""
                                            tipo = ""
                                            descripcion = ""
                                        }
                                        recargarLista()
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}