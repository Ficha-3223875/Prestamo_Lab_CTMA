package com.ctma.prestamolab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ctma.prestamolab.data.repository.PrestamoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Coordina el estado y las acciones de pantalla (sección 10.2 de la guía).
 * No guarda Activity, Context ni NavController: solo expone UiState/StateFlow
 * de solo lectura y funciones que la UI puede invocar.
 */
class PrestamoViewModel(
    private val repository: PrestamoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamoUiState())
    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        _uiState.update {
            it.copy(
                equipos = repository.obtenerEquipos(),
                solicitudes = repository.obtenerSolicitudes()
            )
        }
    }

    fun obtenerEquipo(id: Int) = repository.obtenerEquipo(id)

    fun obtenerSolicitud(id: Int) = repository.obtenerSolicitud(id)

    /**
     * Crea una solicitud. El flag "guardando" es la primera barrera contra
     * doble pulsación (RN-05/BUG-03): mientras es true, se ignoran nuevos
     * intentos aunque el usuario pulse Guardar varias veces.
     */
    fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int,
        onExito: () -> Unit
    ) {
        if (_uiState.value.guardando) return

        _uiState.update { it.copy(guardando = true, mensaje = null) }
        viewModelScope.launch {
            // Pequeña espera para simular una operación real y hacer visible
            // en pruebas manuales el efecto del bloqueo por doble pulsación.
            delay(150)
            val resultado = repository.crearSolicitud(
                equipoId, ambienteDestino, proposito, duracionHoras
            )
            resultado.onSuccess {
                cargarDatos()
                _uiState.update { estado -> estado.copy(guardando = false, mensaje = "Solicitud registrada.") }
                onExito()
            }.onFailure { error ->
                _uiState.update { estado ->
                    estado.copy(guardando = false, mensaje = error.message ?: "No fue posible registrar la solicitud.")
                }
            }
        }
    }

    fun cancelarSolicitud(id: Int) {
        viewModelScope.launch {
            try {
                repository.cancelarSolicitud(id)
                    .onSuccess {
                        cargarDatos()
                        _uiState.update { it.copy(mensaje = "Solicitud cancelada.") }
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(mensaje = error.message ?: "No fue posible cancelar la solicitud.") }
                    }
            } catch (e: Exception) {
                // HU-10: cualquier fallo inesperado (no solo los previstos con
                // Result) se comunica de forma recuperable, sin cerrar la app.
                _uiState.update { it.copy(mensaje = "Ocurrió un problema al cancelar la solicitud. Intenta de nuevo.") }
            }
        }
    }

    /** HU-07: transición genérica reutilizada por las 4 acciones de gestión. */
    private fun gestionarSolicitud(
        id: Int,
        accion: (Int) -> Result<Unit>,
        mensajeExito: String
    ) {
        viewModelScope.launch {
            try {
                accion(id)
                    .onSuccess {
                        cargarDatos()
                        _uiState.update { it.copy(mensaje = mensajeExito) }
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(mensaje = error.message ?: "No fue posible completar la acción.") }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensaje = "Ocurrió un problema inesperado. Intenta de nuevo.") }
            }
        }
    }

    fun aprobarSolicitud(id: Int) =
        gestionarSolicitud(id, repository::aprobarSolicitud, "Solicitud aprobada.")

    fun rechazarSolicitud(id: Int) =
        gestionarSolicitud(id, repository::rechazarSolicitud, "Solicitud rechazada.")

    fun entregarSolicitud(id: Int) =
        gestionarSolicitud(id, repository::entregarSolicitud, "Equipo marcado como entregado.")

    fun devolverSolicitud(id: Int) =
        gestionarSolicitud(id, repository::devolverSolicitud, "Devolución registrada.")

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensaje = null) }
    }
}
