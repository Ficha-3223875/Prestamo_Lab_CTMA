package com.ctma.prestamolab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ctma.prestamolab.data.preferences.FiltrosPreferences
import com.ctma.prestamolab.data.repository.PrestamoRepository
import com.ctma.prestamolab.model.CategoriaEquipo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Coordina el estado y las acciones de pantalla. Desde la Semana 6,
 * el Repository es suspend (puede tardar, lee de Room), así que TODO
 * acceso a datos pasa por viewModelScope.launch. La conversión a
 * Flow/StateFlow reactivo de punta a punta llega en la Semana 7
 * (sección 8 de la guía); por ahora se recarga explícitamente tras
 * cada operación, que es el paso intermedio correcto según la
 * progresión de la guía.
 */
class PrestamoViewModel(
    private val repository: PrestamoRepository,
    private val preferencias: FiltrosPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamoUiState())
    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val filtroGuardado = preferencias.categoriaFiltro.first()
            _uiState.update { it.copy(categoriaFiltro = filtroGuardado) }
            cargarDatos()
            _uiState.update { it.copy(cargandoInicial = false) }
        }
    }

    private suspend fun cargarDatos() {
        val equipos = repository.obtenerEquipos()
        val solicitudes = repository.obtenerSolicitudes()
        _uiState.update { it.copy(equipos = equipos, solicitudes = solicitudes) }
    }

    /** Búsqueda rápida en memoria sobre lo ya cargado (evita otra consulta a disco). */
    fun obtenerEquipo(id: Int) = _uiState.value.equipos.find { it.id == id }

    fun obtenerSolicitud(id: Int) = _uiState.value.solicitudes.find { it.id == id }

    fun cambiarFiltroCategoria(categoria: CategoriaEquipo?) {
        viewModelScope.launch {
            preferencias.guardarCategoriaFiltro(categoria)
            _uiState.update { it.copy(categoriaFiltro = categoria) }
        }
    }

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
            try {
                delay(150)
                val resultado = repository.crearSolicitud(equipoId, ambienteDestino, proposito, duracionHoras)
                resultado.onSuccess {
                    cargarDatos()
                    _uiState.update { estado -> estado.copy(guardando = false, mensaje = "Solicitud registrada.") }
                    onExito()
                }.onFailure { error ->
                    _uiState.update { estado ->
                        estado.copy(guardando = false, mensaje = error.message ?: "No fue posible registrar la solicitud.")
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(guardando = false, mensaje = "Ocurrió un problema al guardar. Intenta de nuevo.") }
            }
        }
    }

    fun cancelarSolicitud(id: Int) = gestionar(id, repository::cancelarSolicitud, "Solicitud cancelada.")
    fun aprobarSolicitud(id: Int) = gestionar(id, repository::aprobarSolicitud, "Solicitud aprobada.")
    fun rechazarSolicitud(id: Int) = gestionar(id, repository::rechazarSolicitud, "Solicitud rechazada.")
    fun entregarSolicitud(id: Int) = gestionar(id, repository::entregarSolicitud, "Equipo marcado como entregado.")
    fun devolverSolicitud(id: Int) = gestionar(id, repository::devolverSolicitud, "Devolución registrada.")

    private fun gestionar(id: Int, accion: suspend (Int) -> Result<Unit>, mensajeExito: String) {
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

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensaje = null) }
    }
}
