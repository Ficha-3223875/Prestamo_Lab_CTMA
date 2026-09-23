package com.ctma.prestamolab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ctma.prestamolab.data.local.entity.SolicitudEntity

/**
 * DAO de solicitudes (préstamos), incluyendo préstamos activos y
 * devoluciones (Semana 6, actividad 11).
 */
@Dao
interface SolicitudDao {

    @Query("SELECT * FROM solicitudes ORDER BY id")
    suspend fun obtenerTodas(): List<SolicitudEntity>

    @Query("SELECT * FROM solicitudes WHERE id = :id")
    suspend fun obtenerPorId(id: Int): SolicitudEntity?

    // "Préstamos activos": solicitudes que ya fueron entregadas y
    // aún no devueltas.
    @Query("SELECT * FROM solicitudes WHERE estado = 'ENTREGADA' ORDER BY id")
    suspend fun obtenerActivas(): List<SolicitudEntity>

    @Insert
    suspend fun insertar(solicitud: SolicitudEntity): Long

    @Update
    suspend fun actualizar(solicitud: SolicitudEntity)
}
