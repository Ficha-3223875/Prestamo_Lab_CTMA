package com.ctma.prestamolab.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ctma.prestamolab.data.local.dao.EquipoDao
import com.ctma.prestamolab.data.local.dao.SolicitudDao
import com.ctma.prestamolab.data.local.entity.EquipoEntity
import com.ctma.prestamolab.data.local.entity.SolicitudEntity

/**
 * Base de datos Room de PréstamoLab CTMA (Semana 6).
 * version = 1 porque es la primera vez que se persiste localmente;
 * cualquier cambio futuro a las entidades requerirá subir la versión
 * y escribir una Migration (sección 6, Semana 6: "Migraciones y
 * pruebas de persistencia").
 */
@Database(
    entities = [EquipoEntity::class, SolicitudEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun equipoDao(): EquipoDao
    abstract fun solicitudDao(): SolicitudDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun obtenerInstancia(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "prestamolab.db"
                ).build()
                INSTANCE = instancia
                instancia
            }
        }
    }
}
