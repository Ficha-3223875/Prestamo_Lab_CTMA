package com.ctma.prestamolab.data.repository

import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.SolicitudPrestamo

/**
 * Contrato del dominio (sección 10.3 de la primera guía, ampliado en
 * Semana 6). A partir de esta semana todas las operaciones son
 * suspend: ya no hay garantía de que los datos vivan en memoria, así
 * que el Repository puede tardar (acceso a disco vía Room). La UI y
 * el ViewModel siguen sin saber CÓMO se guardan los datos, solo que
 * hay que esperarlos.
 */
interface PrestamoRepository {
    suspend fun obtenerEquipos(): List<Equipo>
    suspend fun obtenerEquipo(id: Int): Equipo?
    suspend fun obtenerSolicitudes(): List<SolicitudPrestamo>
    suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo?
    suspend fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ): Result<SolicitudPrestamo>
    suspend fun cancelarSolicitud(id: Int): Result<Unit>

    suspend fun aprobarSolicitud(id: Int): Result<Unit>
    suspend fun rechazarSolicitud(id: Int): Result<Unit>
    suspend fun entregarSolicitud(id: Int): Result<Unit>
    suspend fun devolverSolicitud(id: Int): Result<Unit>
}
