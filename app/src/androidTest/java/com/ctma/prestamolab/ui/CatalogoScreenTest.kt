package com.ctma.prestamolab.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo
import com.ctma.prestamolab.ui.catalogo.CatalogoScreen
import org.junit.Rule
import org.junit.Test

/**
 * HU-11: entorno de pruebas instrumentadas (Compose UI Test / Espresso),
 * complementario a las pruebas unitarias de ValidacionesTest. Corre sobre
 * un emulador o dispositivo real (./gradlew connectedDebugAndroidTest),
 * a diferencia de las unitarias que corren en la JVM.
 *
 * Cubre TC-01 (catálogo con datos) desde la capa de UI real, no solo la
 * lógica de negocio.
 */
class CatalogoScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val equiposDePrueba = listOf(
        Equipo(1, "Multímetro digital", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
        Equipo(2, "Kit de destornilladores", CategoriaEquipo.HERRAMIENTA_MANUAL, EstadoEquipo.PRESTADO)
    )

    @Test
    fun catalogoMuestraNombreYEstadoDeCadaEquipo() {
        composeTestRule.setContent {
            CatalogoScreen(
                equipos = equiposDePrueba,
                onEquipoClick = {},
                onVerMisSolicitudes = {}
            )
        }

        composeTestRule.onNodeWithText("Multímetro digital").assertIsDisplayed()
        composeTestRule.onNodeWithText("Estado: Disponible").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kit de destornilladores").assertIsDisplayed()
        composeTestRule.onNodeWithText("Estado: Prestado").assertIsDisplayed()
    }
}
