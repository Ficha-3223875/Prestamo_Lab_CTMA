package com.ctma.prestamolab.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representación de un Equipo en la base de datos Room (Semana 6).
 * Room necesita tipos primitivos/String, por eso categoria y estado
 * se guardan como String (ver Converters.kt) en vez del enum directo.
 */
@Entity(tableName = "equipos")
data class EquipoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val categoria: String,
    val estado: String
)
