package com.ctma.prestamolab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ctma.prestamolab.data.preferences.PreferenciasFiltro
import com.ctma.prestamolab.data.repository.PrestamoRepository
import com.ctma.prestamolab.model.CategoriaEquipo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PrestamoViewModel(
    private val repository: PrestamoRepository,
    private val preferencias: PreferenciasFiltro
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamoUiState())
    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val filtroGuardado = preferencias.categoriaFiltro.first()
            _uiState.update { it.copy(categoriaFiltro = filtroGuardado) }
        }

        combine(
            repository.observarEquipos(),
            repository.observarSolicitudes()
        ) { equipos, solicitudes -> equipos to solicitudes }
            .catch { error ->
                _uiState.update {
                    it.copy(cargandoInicial = false, errorCarga = error.message ?: "No fue posible cargar los datos.")
                }
            }
            .onEach { (equipos, solicitudes) ->
                _uiState.update {
                    it.copy(equipos = equipos, solicitudes = solicitudes, cargandoInicial = false, errorCarga = null)
                }
            }
            .launchIn(viewModelScope)
    }

    fun obtenerEquipo(id: Int) = _uiState.value.equipos.find { it.id == id }
    fun obtenerSolicitud(id: Int) = _uiState.value.solicitudes.find { it.id == id }

    fun cambiarFiltroCategoria(categoria: CategoriaEquipo?) {
        viewModelScope.launch {
            preferencias.guardarCategoriaFiltro(categoria)
            _uiState.update { it.copy(categoriaFiltro = categoria) }
        }
    }

    /**
     * Semana 8: sincronización manual (botón, no automática) contra el
     * servidor remoto. Room sigue mostrando datos de inmediato sin
     * esperar esto — si falla (lo más probable, dado que no hay
     * backend real), simplemente se informa y la app sigue funcionando
     * igual con lo que ya tenía en Room.
     */
    fun sincronizarConServidor() {
        if (_uiState.value.sincronizando) return
        _uiState.update { it.copy(sincronizando = true, mensaje = null) }
        viewModelScope.launch {
            try {
                repository.sincronizarCatalogoRemoto()
                    .onSuccess {
                        _uiState.update { it.copy(sincronizando = false, mensaje = "Catálogo sincronizado con el servidor.") }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                sincronizando = false,
                                mensaje = (error.message ?: "No fue posible sincronizar.") + " Se sigue mostrando el catálogo local."
                            )
                        }
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(sincronizando = false, mensaje = "No fue posible sincronizar. Se sigue mostrando el catálogo local.")
                }
            }
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
                    _uiState.update { estado -> estado.copy(guardando = false, mensaje = "Solicitud registrada.") }
                    onExito()
                }.onFailure { error ->
                    _uiState.update { estado ->
                        estado.copy(guardando = false, mensaje = error.message ?: "No fue posible registrar la solicitud.")
                    }
                }
            } catch (e: CancellationException) {
                throw e
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
                    .onSuccess { _uiState.update { it.copy(mensaje = mensajeExito) } }
                    .onFailure { error -> _uiState.update { it.copy(mensaje = error.message ?: "No fue posible completar la acción.") } }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(mensaje = "Ocurrió un problema inesperado. Intenta de nuevo.") }
            }
        }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensaje = null) }
    }
}
