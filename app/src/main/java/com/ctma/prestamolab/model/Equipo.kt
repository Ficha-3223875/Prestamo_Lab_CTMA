package com.ctma.prestamolab.model

/**
 * Representa un equipo o herramienta de formación del catálogo simulado.
 * Los datos son sintéticos (RN-09): no corresponden a un inventario real.
 */
data class Equipo(
    val id: Int,
    val nombre: String,
    val categoria: CategoriaEquipo,
    val estado: EstadoEquipo
)
