package com.example.prestamo_lab_ctma.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.prestamo_lab_ctma.model.CategoriaEquipo
import com.example.prestamo_lab_ctma.model.EstadoEquipo

@Entity(tableName = "equipos")
data class EquipoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val categoria: CategoriaEquipo,
    val estado: EstadoEquipo
)
