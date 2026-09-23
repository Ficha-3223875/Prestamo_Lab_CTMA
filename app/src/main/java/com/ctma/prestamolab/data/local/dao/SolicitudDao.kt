package com.ctma.prestamolab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ctma.prestamolab.data.local.entity.SolicitudEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudDao {

    @Query("SELECT * FROM solicitudes ORDER BY id")
    fun observarTodas(): Flow<List<SolicitudEntity>>

    @Query("SELECT * FROM solicitudes WHERE id = :id")
    suspend fun obtenerPorId(id: Int): SolicitudEntity?

    @Query("SELECT * FROM solicitudes WHERE estado = 'ENTREGADA' ORDER BY id")
    fun observarActivas(): Flow<List<SolicitudEntity>>

    @Insert
    suspend fun insertar(solicitud: SolicitudEntity): Long

    @Update
    suspend fun actualizar(solicitud: SolicitudEntity)
}
