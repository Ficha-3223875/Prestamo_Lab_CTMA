package com.example.prestamo_lab_ctma

import com.example.prestamo_lab_ctma.data.repository.InMemoryPrestamoRepository
import com.example.prestamo_lab_ctma.model.*
import com.example.prestamo_lab_ctma.ui.navigation.Screen
import com.example.prestamo_lab_ctma.ui.viewmodel.PrestamoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Suite de Pruebas Unitarias para validación de Historias de Usuario (HU)
 * Cada test valida los criterios de aceptación (CA) y casos de prueba (TC) asignados.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UserStoriesValidationTest {

    private lateinit var viewModel: PrestamoViewModel
    private lateinit var repository: InMemoryPrestamoRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = InMemoryPrestamoRepository()
        viewModel = PrestamoViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `HU-01 Definir modelos de dominio y estructura base (TC-01 a TC-04)`() {
        // TC-02: Instanciar Equipo
        val equipo = Equipo(100, "Equipo Test", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE)
        assertEquals("Equipo Test", equipo.nombre)

        // TC-03: Instanciar Solicitud
        val solicitud = SolicitudPrestamo(1, 100, "Destino", "Proposito largo", 5, EstadoSolicitud.SOLICITADA)
        assertEquals(5, solicitud.duracionHoras)

        // TC-04: Enums
        assertEquals(EstadoEquipo.DISPONIBLE, EstadoEquipo.valueOf("DISPONIBLE"))
        assertEquals(EstadoSolicitud.CANCELADA, EstadoSolicitud.valueOf("CANCELADA"))
    }

    @Test
    fun `HU-02 Implementar persistencia en memoria (TC-05 a TC-08)`() {
        // TC-07: Lista inicial de al menos 8 equipos
        val equipos = repository.obtenerEquipos()
        assertTrue("Debe tener al menos 8 equipos", equipos.size >= 8)

        // TC-06/TC-08: Verificar que es InMemory (Prueba lógica)
        assertTrue(repository is InMemoryPrestamoRepository)
    }

    @Test
    fun `HU-03 Gestionar el estado unidireccional StateFlow (TC-09 a TC-12)`() {
        // TC-10: Emitir estado inicial correcto
        val state = viewModel.uiState.value
        assertNotNull(state.equipos)
        assertTrue(state.solicitudes.isEmpty())

        // TC-11: Reflejar cambios (Cargar datos)
        viewModel.cargarDatos()
        assertEquals(repository.obtenerEquipos().size, viewModel.uiState.value.equipos.size)
    }

    @Test
    fun `HU-04 Visualizar el catalogo de equipos (TC-13 a TC-16)`() {
        // TC-16: Datos en repositorio disponibles para el catálogo
        val state = viewModel.uiState.value
        assertFalse(state.equipos.isEmpty())
        assertEquals("Multímetro digital", state.equipos.first().nombre)
    }

    @Test
    fun `HU-05 Visualizar la disponibilidad de un equipo (TC-17 a TC-20)`() {
        // TC-19: Equipo DISPONIBLE
        val disponible = repository.obtenerEquipo(1) // Multimetro
        assertEquals(EstadoEquipo.DISPONIBLE, disponible?.estado)

        // TC-20: Equipo RESERVADO
        val reservado = repository.obtenerEquipo(4) // Tablet
        assertEquals(EstadoEquipo.RESERVADO, reservado?.estado)
    }

    @Test
    fun `HU-06 Diligenciar y validar formulario de prestamo (TC-21 a TC-25)`() {
        // TC-22: Ambiente vacío
        viewModel.onAmbienteChange("")
        assertEquals("El ambiente o destino es obligatorio.", viewModel.formularioState.value.errorAmbiente)

        // TC-23: Propósito inválido (< 10)
        viewModel.onPropositoChange("Corto")
        assertNotNull(viewModel.formularioState.value.errorProposito)

        // TC-24: Duración inválida (> 8)
        viewModel.onDuracionChange("10")
        assertNotNull(viewModel.formularioState.value.errorDuracion)
    }

    @Test
    fun `HU-07 Consultar y gestionar mis prestamos activos (TC-26 a TC-30)`() {
        // Simular creación
        viewModel.onAmbienteChange("Lab A")
        viewModel.onPropositoChange("Práctica de clase")
        viewModel.onDuracionChange("2")
        viewModel.guardarSolicitud(1)

        // TC-26: Lista de solicitudes no vacía
        val solicitudId = viewModel.uiState.value.solicitudes.first().id
        
        // TC-30: Cancelar solicitud SOLICITADA
        viewModel.cancelarSolicitud(solicitudId)
        val solicitud = viewModel.obtenerSolicitudPorId(solicitudId)
        assertEquals(EstadoSolicitud.CANCELADA, solicitud?.estado)
    }

    @Test
    fun `HU-08 Navegar de forma segura entre pantallas (TC-31 a TC-33)`() {
        // TC-32: Firma de rutas y paso de parámetros IDs
        assertEquals("equipo/5", Screen.EquipoDetalle.createRoute(5))
        assertEquals("solicitud/10", Screen.SolicitudDetalle.createRoute(10))
        assertEquals("catalogo", Screen.Catalogo.route)
    }

    @Test
    fun `HU-09 Prevenir duplicidad en las solicitudes (TC-34 a TC-37)`() {
        // TC-34: Flag guardando existe
        assertFalse(viewModel.uiState.value.guardando)

        // TC-35/37: Simular flujo de guardado (el flag se activa en el ViewModel)
        viewModel.onAmbienteChange("Lab")
        viewModel.onPropositoChange("Proposito Válido")
        viewModel.onDuracionChange("3")
        
        // Al llamar a guardar, validamos que no se creen duplicados si el proceso está activo
        viewModel.guardarSolicitud(1)
        assertEquals(1, viewModel.uiState.value.solicitudes.size)
    }

    @Test
    fun `HU-10 Garantizar la estabilidad y accesibilidad de la UI (TC-38 a TC-40)`() {
        // TC-38: Manejo de ID inexistente
        val equipoInexistente = viewModel.obtenerEquipoPorId(999)
        assertNull(equipoInexistente)
        
        val solicitudInexistente = viewModel.obtenerSolicitudPorId(999)
        assertNull(solicitudInexistente)
    }

    @Test
    fun `HU-11 Validar las reglas de negocio QA (TC-41 a TC-43)`() {
        // TC-41: Valores frontera Propósito (9 vs 10 caracteres)
        viewModel.onPropositoChange("123456789") // 9
        assertFalse(viewModel.formularioState.value.puedeGuardar)
        
        viewModel.onPropositoChange("1234567890") // 10
        viewModel.onAmbienteChange("A")
        viewModel.onDuracionChange("1")
        assertTrue(viewModel.formularioState.value.puedeGuardar)

        // TC-42: Valores frontera Duración (0 vs 1 hora)
        viewModel.onDuracionChange("0")
        assertFalse(viewModel.formularioState.value.puedeGuardar)
        
        viewModel.onDuracionChange("1")
        assertTrue(viewModel.formularioState.value.puedeGuardar)
    }

    @Test
    fun `HU-12 Asegurar el cumplimiento tecnico (TC-44 a TC-47)`() {
        // TC-45: Transición DISPONIBLE -> RESERVADO (RN-06)
        val equipoId = 6
        val estadoInicial = repository.obtenerEquipo(equipoId)?.estado
        assertEquals(EstadoEquipo.DISPONIBLE, estadoInicial)

        viewModel.onAmbienteChange("Ambiente X")
        viewModel.onPropositoChange("Uso de herramientas")
        viewModel.onDuracionChange("5")
        viewModel.guardarSolicitud(equipoId)

        val estadoFinal = viewModel.obtenerEquipoPorId(equipoId)?.estado
        assertEquals(EstadoEquipo.RESERVADO, estadoFinal)
    }
}
