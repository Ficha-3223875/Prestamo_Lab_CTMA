package com.ctma.prestamolab.viewmodel

import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.SolicitudPrestamo

/**
 * Estado de solo lectura que expone el ViewModel a la UI.
 * categoriaFiltro (Semana 6) es la preferencia persistida en
 * DataStore; equiposFiltrados aplica ese filtro sobre la lista
 * completa sin duplicar la fuente de verdad.
 */
data class PrestamoUiState(
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val categoriaFiltro: CategoriaEquipo? = null,
    val mensaje: String? = null,
    val guardando: Boolean = false,
    val cargandoInicial: Boolean = true
) {
    val equiposFiltrados: List<Equipo>
        get() = if (categoriaFiltro == null) equipos else equipos.filter { it.categoria == categoriaFiltro }
}
