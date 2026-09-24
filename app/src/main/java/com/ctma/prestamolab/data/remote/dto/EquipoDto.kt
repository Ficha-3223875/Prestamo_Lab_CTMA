package com.ctma.prestamolab.data.remote.dto

/**
 * DTO (Data Transfer Object) del contrato remoto (Semana 8, actividad
 * 24). Representa el JSON tal como lo devolvería el servidor — no es
 * el modelo de dominio (Equipo) ni la entidad de Room (EquipoEntity).
 * Mantener los tres separados es lo que permite que un cambio en el
 * contrato del API no obligue a tocar la base de datos local ni la UI.
 *
 * Se usa Gson (reflexión, sin anotaciones ni generación de código) en
 * vez de Moshi/kotlinx.serialization, para no sumar otra herramienta
 * de compilación (KSP) encima de la que ya usa Room.
 */
data class EquipoDto(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val estado: String
)
