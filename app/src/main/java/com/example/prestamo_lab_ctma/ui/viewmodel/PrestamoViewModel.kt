package com.example.prestamo_lab_ctma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestamo_lab_ctma.data.repository.PrestamoRepository
import com.example.prestamo_lab_ctma.data.local.UserPreferencesRepository
import com.example.prestamo_lab_ctma.model.Equipo
import com.example.prestamo_lab_ctma.model.EstadoSolicitud
import com.example.prestamo_lab_ctma.model.SolicitudPrestamo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PrestamoViewModel(
    private val repository: PrestamoRepository,
    private val userPrefs: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamoUiState())
    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    private val _formularioState = MutableStateFlow(FormularioSolicitudState())
    val formularioState: StateFlow<FormularioSolicitudState> = _formularioState.asStateFlow()

    init {
        // Inicializar datos asincrónicamente
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.inicializarEquipos()
                observarDatos()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Fallo al cargar datos") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun observarDatos() {
        // Combinar datos del repositorio con las preferencias de DataStore
        combine(
            repository.obtenerEquipos(),
            repository.obtenerSolicitudes(),
            userPrefs.categoryFilter
        ) { equipos, solicitudes, filter ->
            val filteredEquipos = if (filter == null) equipos else equipos.filter { it.categoria.name == filter }
            _uiState.update { 
                it.copy(
                    equipos = filteredEquipos, 
                    solicitudes = solicitudes,
                    filterCategory = filter
                ) 
            }
        }.launchIn(viewModelScope)
    }

    fun updateFilter(category: String?) {
        viewModelScope.launch {
            userPrefs.saveCategoryFilter(category)
        }
    }

    fun obtenerEquipoPorId(id: Int): Equipo? {
        // Como tenemos los equipos en el uiState, podemos buscarlos ahí de forma síncrona para la UI
        return _uiState.value.equipos.find { it.id == id }
    }

    fun obtenerSolicitudPorId(id: Int): SolicitudPrestamo? {
        return _uiState.value.solicitudes.find { it.id == id }
    }

    // Lógica del Formulario
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

    fun onPhotoCaptured(path: String) {
        _formularioState.update { it.copy(photoPath = path) }
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
                estado = EstadoSolicitud.SOLICITADA,
                photoPath = state.photoPath
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
                // No hace falta cargarDatos() manualmente porque el Flow observa cambios
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
        }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensaje = null, operacionExitosa = false) }
    }

    private fun limpiarFormulario() {
        _formularioState.update { FormularioSolicitudState() }
    }
}
