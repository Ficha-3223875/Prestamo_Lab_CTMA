package com.example.prestamo_lab_ctma.data.local

import android.content.Context
import androidx.room.*
import com.example.prestamo_lab_ctma.data.local.dao.EquipoDao
import com.example.prestamo_lab_ctma.data.local.dao.SolicitudDao
import com.example.prestamo_lab_ctma.data.local.entities.EquipoEntity
import com.example.prestamo_lab_ctma.data.local.entities.SolicitudEntity

@Database(entities = [EquipoEntity::class, SolicitudEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun equipoDao(): EquipoDao
    abstract fun solicitudDao(): SolicitudDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "prestamo_database"
                )
                .fallbackToDestructiveMigration() // Simple for development
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
