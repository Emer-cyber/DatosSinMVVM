package com.example.datossinmvvm

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MicroorganismoDao {
    @Query("SELECT * FROM microorganismos ORDER BY id DESC")
    suspend fun obtenerTodos(): List<Microorganismo>

    @Insert
    suspend fun insertar(microorganismo: Microorganismo)

    @Update
    suspend fun actualizar(microorganismo: Microorganismo)

    @Delete
    suspend fun eliminar(microorganismo: Microorganismo)
}