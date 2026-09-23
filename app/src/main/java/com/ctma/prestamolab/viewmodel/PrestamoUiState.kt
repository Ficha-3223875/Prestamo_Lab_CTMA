package com.ctma.prestamolab.viewmodel

import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.SolicitudPrestamo

/**
 * Estados posibles de una pantalla que muestra una colección
 * (Semana 7, actividad 19: "Modelar UiState para carga, contenido,
 * vacío y error"). Se usa para el catálogo; el mismo patrón se puede
 * reutilizar para Mis solicitudes si el equipo lo necesita.
 */
sealed interface CargaEstado {
    data object Cargando : CargaEstado
    data object Contenido : CargaEstado
    data object Vacio : CargaEstado
    data class Error(val mensaje: String) : CargaEstado
}

data class PrestamoUiState(
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val categoriaFiltro: CategoriaEquipo? = null,
    val mensaje: String? = null,
    val guardando: Boolean = false,
    val cargandoInicial: Boolean = true,
    val errorCarga: String? = null
) {
    val equiposFiltrados: List<Equipo>
        get() = if (categoriaFiltro == null) equipos else equipos.filter { it.categoria == categoriaFiltro }

    /** Estado consolidado para que la UI del catálogo pinte un solo caso a la vez. */
    val estadoCatalogo: CargaEstado
        get() = when {
            errorCarga != null -> CargaEstado.Error(errorCarga)
            cargandoInicial -> CargaEstado.Cargando
            equiposFiltrados.isEmpty() -> CargaEstado.Vacio
            else -> CargaEstado.Contenido
        }
}
