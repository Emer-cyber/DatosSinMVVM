package com.example.datossinmvvm

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "microorganismos")
data class Microorganismo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,        // Ej: Escherichia coli
    val tipo: String,          // Ej: Bacteria, Protozoo, Hongo
    val descripcion: String    // Ej: Forma de bacilo, Gram negativa
)