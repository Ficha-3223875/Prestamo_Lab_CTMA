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
 * Actualizado en Semana 6: CatalogoScreen ahora recibe categoriaFiltro
 * (obligatorio) y cargandoInicial (opcional). Se pasa categoriaFiltro
 * = null para simular "sin filtro aplicado", el caso TC-01 original.
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
                categoriaFiltro = null,
                cargandoInicial = false,
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
