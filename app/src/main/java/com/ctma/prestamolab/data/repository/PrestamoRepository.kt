package com.ctma.prestamolab.data.repository

import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.SolicitudPrestamo

/**
 * Contrato del dominio (sección 10.3 de la guía). El Repository define QUÉ
 * operaciones existen; no decide mensajes ni nada visual: esa
 * responsabilidad es del ViewModel/UI.
 */
interface PrestamoRepository {
    fun obtenerEquipos(): List<Equipo>
    fun obtenerEquipo(id: Int): Equipo?
    fun obtenerSolicitudes(): List<SolicitudPrestamo>
    fun obtenerSolicitud(id: Int): SolicitudPrestamo?
    fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ): Result<SolicitudPrestamo>
    fun cancelarSolicitud(id: Int): Result<Unit>
}
