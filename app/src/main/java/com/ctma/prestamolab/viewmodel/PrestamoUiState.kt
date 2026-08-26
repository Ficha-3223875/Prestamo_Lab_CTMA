package com.ctma.prestamolab.viewmodel

import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.SolicitudPrestamo

/**
 * Estado de solo lectura que expone el ViewModel a la UI (sección 7.2 de la
 * guía). Compose observa este UiState; nunca modifica listas internas del
 * Repository directamente.
 */
data class PrestamoUiState(
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val mensaje: String? = null,
    val guardando: Boolean = false
)
