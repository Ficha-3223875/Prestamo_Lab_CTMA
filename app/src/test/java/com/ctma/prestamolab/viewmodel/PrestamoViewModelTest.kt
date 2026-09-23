package com.ctma.prestamolab.viewmodel

import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import com.ctma.prestamolab.data.preferences.PreferenciasFiltro
import com.ctma.prestamolab.data.repository.InMemoryPrestamoRepository
import com.ctma.prestamolab.model.CategoriaEquipo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Semana 7, actividad 20: "Simular operaciones lentas y verificar
 * cancelación/manejo de excepciones". Usa InMemoryPrestamoRepository
 * (línea base heredada) como Fake, y una implementación de prueba de
 * PreferenciasFiltro que no depende de Android real.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PrestamoViewModelTest {

    private val dispatcherDePrueba = StandardTestDispatcher()

    private class PreferenciasFiltroDePrueba : PreferenciasFiltro {
        private val estado = MutableStateFlow<CategoriaEquipo?>(null)
        override val categoriaFiltro: Flow<CategoriaEquipo?> get() = estado
        override suspend fun guardarCategoriaFiltro(categoria: CategoriaEquipo?) {
            estado.value = categoria
        }
    }

    @Before
    fun configurarDispatcher() {
        Dispatchers.setMain(dispatcherDePrueba)
    }

    @After
    fun restaurarDispatcher() {
        Dispatchers.resetMain()
    }

    @Test
    fun crearSolicitud_actualizaEstadoReactivamenteSinLlamarCargarDatos() = runTest {
        val repository = InMemoryPrestamoRepository()
        val viewModel = PrestamoViewModel(repository, PreferenciasFiltroDePrueba())

        viewModel.uiState.test {
            // Emisión inicial (posiblemente con cargandoInicial=true un instante)
            awaitItem()

            val equipoDisponibleId = repository.observarEquipos().first()
                .first { it.estado.name == "DISPONIBLE" }.id

            viewModel.crearSolicitud(
                equipoId = equipoDisponibleId,
                ambienteDestino = "Ambiente 302",
                proposito = "Práctica de laboratorio de electrónica",
                duracionHoras = 2,
                onExito = {}
            )

            // Avanza el tiempo virtual para que pase el delay(150) simulado.
            dispatcherDePrueba.scheduler.advanceUntilIdle()

            val estadoFinal = expectMostRecentItem()
            assertFalse("guardando debería volver a false", estadoFinal.guardando)
            assertTrue("la nueva solicitud debe reflejarse sola, sin recarga manual", estadoFinal.solicitudes.isNotEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun cancelarViewModelScopeMientrasGuarda_noLanzaExcepcionSinManejar() = runTest {
        val repository = InMemoryPrestamoRepository()
        val viewModel = PrestamoViewModel(repository, PreferenciasFiltroDePrueba())

        val equipoId = repository.observarEquipos().first()
            .first { it.estado.name == "DISPONIBLE" }.id

        viewModel.crearSolicitud(
            equipoId = equipoId,
            ambienteDestino = "Ambiente 302",
            proposito = "Práctica de laboratorio de electrónica",
            duracionHoras = 2,
            onExito = {}
        )
        assertTrue("mientras corre, guardando debe ser true", viewModel.uiState.value.guardando)

        // Cancela viewModelScope a medio camino del delay(150) simulado,
        // ANTES de que la corrutina termine. Esto es lo que pasa en la
        // app real si el usuario cierra la pantalla o el proceso muere
        // mientras se está guardando. El punto de esta prueba es que el
        // catch (e: CancellationException) { throw e } del ViewModel es
        // necesario: sin él, catch (e: Exception) atraparía también la
        // cancelación (CancellationException hereda de Exception) y
        // rompería la cooperación de corrutinas de forma silenciosa.
        viewModel.viewModelScope.cancel()

        // Si la cancelación no se propaga correctamente, este
        // advanceUntilIdle() dejaría una corrutina "zombie" corriendo
        // o el test fallaría con una excepción no controlada.
        dispatcherDePrueba.scheduler.advanceUntilIdle()
    }
}
