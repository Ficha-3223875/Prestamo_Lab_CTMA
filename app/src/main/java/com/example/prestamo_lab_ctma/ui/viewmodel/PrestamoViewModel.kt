package com.example.prestamo_lab_ctma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestamo_lab_ctma.data.repository.InMemoryPrestamoRepository
import com.example.prestamo_lab_ctma.data.repository.PrestamoRepository
import com.example.prestamo_lab_ctma.model.Equipo
import com.example.prestamo_lab_ctma.model.EstadoSolicitud
import com.example.prestamo_lab_ctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PrestamoViewModel(
    private val repository: PrestamoRepository = InMemoryPrestamoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamoUiState())
    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    private val _formularioState = MutableStateFlow(FormularioSolicitudState())
    val formularioState: StateFlow<FormularioSolicitudState> = _formularioState.asStateFlow()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        _uiState.update {
            it.copy(
                equipos = repository.obtenerEquipos(),
                solicitudes = repository.obtenerSolicitudes()
            )
        }
    }

    fun obtenerEquipoPorId(id: Int): Equipo? = repository.obtenerEquipo(id)

    fun obtenerSolicitudPorId(id: Int): SolicitudPrestamo? = repository.obtenerSolicitud(id)

    fun onAmbienteChange(nuevoAmbiente: String) {
        _formularioState.update { it.copy(ambienteDestino = nuevoAmbiente) }
        validarFormulario()
    }

    fun onPropositoChange(nuevoProposito: String) {
        _formularioState.update { it.copy(proposito = nuevoProposito) }
        validarFormulario()
    }

    fun onDuracionChange(nuevaDuracion: String) {
        _formularioState.update { it.copy(duracionHoras = nuevaDuracion) }
        validarFormulario()
    }

    private fun validarFormulario() {
        val state = _formularioState.value
        
        val errorAmbiente = if (state.ambienteDestino.isBlank()) "El ambiente o destino es obligatorio." else null
        
        val errorProposito = when {
            state.proposito.length < 10 -> "El propósito debe tener al menos 10 caracteres."
            state.proposito.length > 180 -> "El propósito no debe exceder los 180 caracteres."
            else -> null
        }
        
        val horas = state.duracionHoras.toIntOrNull()
        val errorDuracion = if (horas == null || horas !in 1..8) {
            "La duración debe estar entre 1 y 8 horas."
        } else null

        _formularioState.update {
            it.copy(
                errorAmbiente = errorAmbiente,
                errorProposito = errorProposito,
                errorDuracion = errorDuracion,
                puedeGuardar = errorAmbiente == null && errorProposito == null && errorDuracion == null
            )
        }
    }

    fun guardarSolicitud(equipoId: Int) {
        if (_uiState.value.guardando) return

        val state = _formularioState.value
        if (!state.puedeGuardar) return

        _uiState.update { it.copy(guardando = true, mensaje = null, operacionExitosa = false) }

        viewModelScope.launch {
            val solicitud = SolicitudPrestamo(
                id = 0,
                equipoId = equipoId,
                ambienteDestino = state.ambienteDestino,
                proposito = state.proposito,
                duracionHoras = state.duracionHoras.toInt(),
                estado = EstadoSolicitud.SOLICITADA
            )

            val result = repository.crearSolicitud(solicitud)
            
            _uiState.update {
                it.copy(
                    guardando = false,
                    mensaje = if (result.isSuccess) "Solicitud creada con éxito" else result.exceptionOrNull()?.message,
                    operacionExitosa = result.isSuccess
                )
            }
            if (result.isSuccess) {
                limpiarFormulario()
                cargarDatos()
            }
        }
    }

    fun cancelarSolicitud(solicitudId: Int) {
        viewModelScope.launch {
            val result = repository.cancelarSolicitud(solicitudId)
            _uiState.update {
                it.copy(
                    mensaje = if (result.isSuccess) "Solicitud cancelada" else result.exceptionOrNull()?.message
                )
            }
            if (result.isSuccess) {
                cargarDatos()
            }
        }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensaje = null, operacionExitosa = false) }
    }

    private fun limpiarFormulario() {
        _formularioState.update { FormularioSolicitudState() }
    }
}
