package com.ctma.prestamolab.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ctma.prestamolab.data.local.dao.EquipoDao
import com.ctma.prestamolab.data.local.dao.EvidenciaDao
import com.ctma.prestamolab.data.local.dao.SolicitudDao
import com.ctma.prestamolab.data.local.entity.EquipoEntity
import com.ctma.prestamolab.data.local.entity.EvidenciaEntity
import com.ctma.prestamolab.data.local.entity.SolicitudEntity

/**
 * Base de datos Room. version = 2 desde la Semana 9: se agregó la
 * tabla "evidencias" (fotos de devolución) sin borrar los datos que
 * ya existían de "equipos" y "solicitudes" — MIGRATION_1_2 crea la
 * tabla nueva en vez de recrear todo desde cero, tal como pide la
 * guía (Semana 6, "Migraciones y pruebas de persistencia").
 */
@Database(
    entities = [EquipoEntity::class, SolicitudEntity::class, EvidenciaEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun equipoDao(): EquipoDao
    abstract fun solicitudDao(): SolicitudDao
    abstract fun evidenciaDao(): EvidenciaDao

    companion object {

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `evidencias` (
                        `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        `solicitudId` INTEGER NOT NULL,
                        `uri` TEXT NOT NULL,
                        `fechaCapturaMillis` INTEGER NOT NULL,
                        `luxAlCapturar` REAL,
                        `estado` TEXT NOT NULL,
                        FOREIGN KEY(`solicitudId`) REFERENCES `solicitudes`(`id`) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_evidencias_solicitudId` ON `evidencias` (`solicitudId`)")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun obtenerInstancia(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "prestamolab.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instancia
                instancia
            }
        }
    }
}
