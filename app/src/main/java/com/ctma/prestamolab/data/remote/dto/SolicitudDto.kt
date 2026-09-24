package com.ctma.prestamolab.data.remote.dto

data class SolicitudDto(
    val id: Int,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: String
)
