package com.ctma.prestamolab.data.repository

import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow

interface PrestamoRepository {
    fun observarEquipos(): Flow<List<Equipo>>
    fun observarSolicitudes(): Flow<List<SolicitudPrestamo>>

    suspend fun obtenerEquipo(id: Int): Equipo?
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

    /**
     * Semana 8: intenta traer el catálogo del servidor remoto y
     * fusionarlo con Room (estrategia local-first, actividad 26).
     * Nunca lanza una excepción sin controlar: siempre devuelve
     * Result, para que el ViewModel decida qué mostrar sin arriesgar
     * la app si no hay backend real disponible.
     */
    suspend fun sincronizarCatalogoRemoto(): Result<Unit>
}
