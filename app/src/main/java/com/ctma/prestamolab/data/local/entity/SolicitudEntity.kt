package com.ctma.prestamolab.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representación de una SolicitudPrestamo en Room (Semana 6).
 * La relación mínima exigida por la guía (sección 8, Semana 6,
 * "Modelar EquipmentEntity, LoanEntity y relaciones mínimas") se
 * implementa con una ForeignKey hacia EquipoEntity: una solicitud
 * siempre pertenece a un equipo existente.
 */
@Entity(
    tableName = "solicitudes",
    foreignKeys = [
        ForeignKey(
            entity = EquipoEntity::class,
            parentColumns = ["id"],
            childColumns = ["equipoId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("equipoId")]
)
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: String
)
