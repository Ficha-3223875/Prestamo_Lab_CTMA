package com.example.prestamo_lab_ctma.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.prestamo_lab_ctma.model.EstadoSolicitud

@Entity(tableName = "solicitudes")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: EstadoSolicitud,
    val photoPath: String? = null // For Camera feature
)
