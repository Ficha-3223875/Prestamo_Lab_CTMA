package com.ctma.prestamolab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ctma.prestamolab.data.local.entity.EvidenciaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EvidenciaDao {

    @Query("SELECT * FROM evidencias WHERE solicitudId = :solicitudId ORDER BY id")
    fun observarPorSolicitud(solicitudId: Int): Flow<List<EvidenciaEntity>>

    @Query("SELECT * FROM evidencias WHERE id = :id")
    suspend fun obtenerPorId(id: Int): EvidenciaEntity?

    @Insert
    suspend fun insertar(evidencia: EvidenciaEntity): Long

    @Update
    suspend fun actualizar(evidencia: EvidenciaEntity)
}
