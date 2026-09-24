package com.ctma.prestamolab.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.ctma.prestamolab.data.preferences.PreferenciasFiltro
import com.ctma.prestamolab.data.repository.InMemoryPrestamoRepository
import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.navigation.PrestamoNavGraph
import com.ctma.prestamolab.viewmodel.PrestamoViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FlujoRegresionE2ETest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Concede el permiso de notificaciones de antemano: sin esto, al
    // tocar "Registrar devolución" el sistema abriría una ventana de
    // permiso REAL fuera de la app, que Compose Test no sabe manejar
    // (causa del error "No compose hierarchies found in the app").
    @get:Rule
    val permisoNotificaciones: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS)

    private class PreferenciasFiltroDePrueba : PreferenciasFiltro {
        private val estado = MutableStateFlow<CategoriaEquipo?>(null)
        override val categoriaFiltro: Flow<CategoriaEquipo?> get() = estado
        override suspend fun guardarCategoriaFiltro(categoria: CategoriaEquipo?) {
            estado.value = categoria
        }
    }

    @Test
    fun flujoCompletoCatalogoSolicitudMisPrestamosDevolucion() {
        val repository = InMemoryPrestamoRepository()
        val viewModel = PrestamoViewModel(repository, PreferenciasFiltroDePrueba())

        composeTestRule.setContent {
            PrestamoNavGraph(viewModel = viewModel)
        }

        // 1) Catálogo: espera a que cargue y toca un equipo disponible.
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Multímetro digital").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Multímetro digital").performClick()

        // 2) Detalle de equipo -> Solicitar
        composeTestRule.onNodeWithText("Solicitar préstamo").performClick()

        // 3) Formulario de solicitud
        composeTestRule.onNodeWithText("Ambiente o destino").performTextInput("Ambiente 302")
        composeTestRule.onNodeWithText("Propósito (10 a 180 caracteres)")
            .performTextInput("Práctica de laboratorio de electrónica")
        composeTestRule.onNodeWithText("Duración estimada en horas (1 a 8)").performTextInput("2")
        composeTestRule.onNodeWithText("Guardar").performClick()

        // 4) Debería llegar a Mis solicitudes con la nueva solicitud (#1)
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Solicitud #1").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Solicitud #1").performClick()

        // 5) Regresión del ciclo de vida completo: Aprobar -> Entregar -> Devolver
        composeTestRule.onNodeWithText("Aprobar solicitud").performClick()
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onAllNodesWithText("Marcar como entregada").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Marcar como entregada").performClick()
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onAllNodesWithText("Registrar devolución").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Registrar devolución").performClick()

        // 6) Estado final esperado: DEVUELTA
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onAllNodesWithText("Estado: DEVUELTA").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Estado: DEVUELTA").assertIsDisplayed()
    }
}