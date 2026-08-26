package com.ctma.prestamolab.model

/**
 * Solicitud de préstamo registrada por el solicitante demo.
 * equipoId conecta la solicitud con el equipo mediante navegación por ID,
 * nunca transportando el objeto Equipo completo (sección 7.2 de la guía).
 */
data class SolicitudPrestamo(
    val id: Int,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: EstadoSolicitud
)
