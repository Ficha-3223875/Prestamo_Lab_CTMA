package com.example.prestamo_lab_ctma.ui.viewmodel

import com.example.prestamo_lab_ctma.model.Equipo
import com.example.prestamo_lab_ctma.model.SolicitudPrestamo

data class PrestamoUiState(
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filterCategory: String? = null,
    val mensaje: String? = null,
    val guardando: Boolean = false,
    val operacionExitosa: Boolean = false
)

data class FormularioSolicitudState(
    val ambienteDestino: String = "",
    val proposito: String = "",
    val duracionHoras: String = "",
    val photoPath: String? = null,
    val errorAmbiente: String? = null,
    val errorProposito: String? = null,
    val errorDuracion: String? = null,
    val puedeGuardar: Boolean = false
)
