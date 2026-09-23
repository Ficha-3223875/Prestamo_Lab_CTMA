package com.ctma.prestamolab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ctma.prestamolab.data.local.entity.EquipoEntity

/**
 * DAO del catálogo de equipos (Semana 6, actividad 11).
 * Todas las funciones son suspend: Room genera el código para
 * ejecutarlas fuera del hilo principal automáticamente.
 */
@Dao
interface EquipoDao {

    @Query("SELECT * FROM equipos ORDER BY id")
    suspend fun obtenerTodos(): List<EquipoEntity>

    @Query("SELECT * FROM equipos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): EquipoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(equipo: EquipoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(equipos: List<EquipoEntity>)

    @Update
    suspend fun actualizar(equipo: EquipoEntity)

    @Query("SELECT COUNT(*) FROM equipos")
    suspend fun contar(): Int
}
