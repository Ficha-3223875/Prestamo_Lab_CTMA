package com.ctma.prestamolab.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "evidencias",
    foreignKeys = [
        ForeignKey(
            entity = SolicitudEntity::class,
            parentColumns = ["id"],
            childColumns = ["solicitudId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("solicitudId")]
)
data class EvidenciaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val solicitudId: Int,
    val uri: String,
    val fechaCapturaMillis: Long,
    val luxAlCapturar: Float?,
    val estado: String
)
