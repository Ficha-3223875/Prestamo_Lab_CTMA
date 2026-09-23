package com.ctma.prestamolab.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo
import com.ctma.prestamolab.ui.catalogo.CatalogoScreen
import com.ctma.prestamolab.viewmodel.CargaEstado
import org.junit.Rule
import org.junit.Test

/**
 * Actualizado en Semana 7: CatalogoScreen ahora recibe estadoCatalogo
 * (CargaEstado) en vez de un booleano cargandoInicial suelto.
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
                estadoCatalogo = CargaEstado.Contenido,
                onEquipoClick = {},
                onVerMisSolicitudes = {}
            )
        }

        composeTestRule.onNodeWithText("Multímetro digital").assertIsDisplayed()
        composeTestRule.onNodeWithText("Estado: Disponible").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kit de destornilladores").assertIsDisplayed()
        composeTestRule.onNodeWithText("Estado: Prestado").assertIsDisplayed()
    }

    @Test
    fun estadoCargandoMuestraIndicadorDeProgreso() {
        composeTestRule.setContent {
            CatalogoScreen(
                equipos = emptyList(),
                categoriaFiltro = null,
                estadoCatalogo = CargaEstado.Cargando,
                onEquipoClick = {},
                onVerMisSolicitudes = {}
            )
        }
        composeTestRule.onNodeWithText("Cargando catálogo...").assertIsDisplayed()
    }

    @Test
    fun estadoErrorMuestraMensaje() {
        composeTestRule.setContent {
            CatalogoScreen(
                equipos = emptyList(),
                categoriaFiltro = null,
                estadoCatalogo = CargaEstado.Error("fallo simulado"),
                onEquipoClick = {},
                onVerMisSolicitudes = {}
            )
        }
        composeTestRule.onNodeWithText("No fue posible cargar el catálogo: fallo simulado").assertIsDisplayed()
    }
}
