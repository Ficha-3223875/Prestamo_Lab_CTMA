package com.ctma.prestamolab.model

/**
 * Evidencia fotográfica asociada a una solicitud (Semana 9, actividad
 * 30). Se conserva la URI (no la imagen completa en memoria) y
 * metadatos: cuándo se capturó/seleccionó y el nivel de luz ambiental
 * medido en ese momento (sensor adicional, actividad 31).
 */
data class EvidenciaFoto(
    val id: Int,
    val solicitudId: Int,
    val uri: String,
    val fechaCapturaMillis: Long,
    val luxAlCapturar: Float?,
    val estado: EstadoEvidencia
)
