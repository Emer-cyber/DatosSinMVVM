package com.example.datossinmvvm

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Microorganismo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun microorganismoDao(): MicroorganismoDao
}