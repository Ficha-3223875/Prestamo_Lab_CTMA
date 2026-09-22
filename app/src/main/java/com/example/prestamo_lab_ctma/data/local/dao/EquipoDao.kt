package com.example.prestamo_lab_ctma.data.local.dao

import androidx.room.*
import com.example.prestamo_lab_ctma.data.local.entities.EquipoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipoDao {
    @Query("SELECT * FROM equipos")
    fun obtenerTodos(): Flow<List<EquipoEntity>>

    @Query("SELECT * FROM equipos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): EquipoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEquipos(equipos: List<EquipoEntity>)

    @Update
    suspend fun actualizarEquipo(equipo: EquipoEntity)
}
