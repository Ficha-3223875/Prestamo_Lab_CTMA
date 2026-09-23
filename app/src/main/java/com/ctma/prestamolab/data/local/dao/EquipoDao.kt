package com.ctma.prestamolab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ctma.prestamolab.data.local.entity.EquipoEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO del catálogo (actualizado en Semana 7, actividad 18:
 * "Convertir las consultas Room a Flow"). observarTodos() ya NO es
 * suspend: Room genera código que emite automáticamente cada vez que
 * la tabla "equipos" cambia (un INSERT/UPDATE en cualquier parte de
 * la app), sin que nadie tenga que acordarse de recargar manualmente.
 * Las escrituras (insertar/actualizar) siguen siendo suspend, porque
 * son acciones puntuales, no algo que se "observa" en el tiempo.
 */
@Dao
interface EquipoDao {

    @Query("SELECT * FROM equipos ORDER BY id")
    fun observarTodos(): Flow<List<EquipoEntity>>

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
