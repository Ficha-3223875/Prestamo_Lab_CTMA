package com.ctma.prestamolab.validation

import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo
import com.ctma.prestamolab.model.duracionValida
import com.ctma.prestamolab.model.equipoDisponibleParaSolicitud
import com.ctma.prestamolab.model.propositoValido
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias de las reglas de negocio desacopladas (RN-01, RN-03,
 * RN-04). Corresponden a los casos de análisis de valores límite TC-04 a
 * TC-11 de la suite (sección 11.3), llevados a JUnit para ejecutarse en
 * GitHub Actions (testDebugUnitTest).
 */
class ValidacionesTest {

    // ---- RN-03: propósito entre 10 y 180 caracteres ----

    @Test
    fun `proposito de 9 caracteres es invalido`() {
        assertFalse(propositoValido("123456789"))
    }

    @Test
    fun `proposito de 10 caracteres es valido`() {
        assertTrue(propositoValido("1234567890"))
    }

    @Test
    fun `proposito de 180 caracteres es valido`() {
        assertTrue(propositoValido("a".repeat(180)))
    }

    @Test
    fun `proposito de 181 caracteres es invalido`() {
        assertFalse(propositoValido("a".repeat(181)))
    }

    // ---- RN-04: duración entre 1 y 8 horas ----

    @Test
    fun `duracion de 0 horas es invalida`() {
        assertFalse(duracionValida(0))
    }

    @Test
    fun `duracion de 1 hora es valida`() {
        assertTrue(duracionValida(1))
    }

    @Test
    fun `duracion de 8 horas es valida`() {
        assertTrue(duracionValida(8))
    }

    @Test
    fun `duracion de 9 horas es invalida`() {
        assertFalse(duracionValida(9))
    }

    // ---- RN-01: solo un equipo DISPONIBLE puede solicitarse ----

    @Test
    fun `equipo reservado no puede solicitarse`() {
        val equipo = Equipo(1, "Multimetro", CategoriaEquipo.ELECTRONICA, EstadoEquipo.RESERVADO)
        assertFalse(equipoDisponibleParaSolicitud(equipo))
    }

    @Test
    fun `equipo disponible puede solicitarse`() {
        val equipo = Equipo(1, "Multimetro", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE)
        assertTrue(equipoDisponibleParaSolicitud(equipo))
    }

    @Test
    fun `equipo nulo (id inexistente) no puede solicitarse`() {
        assertFalse(equipoDisponibleParaSolicitud(null))
    }
}
