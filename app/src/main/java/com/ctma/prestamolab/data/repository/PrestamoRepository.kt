package com.ctma.prestamolab.data.repository

import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del dominio, actualizado en Semana 7 a flujo reactivo
 * (sección 8, Semana 7). Las lecturas de colecciones ahora son Flow:
 * el ViewModel las "observa" en vez de pedirlas y volver a pedirlas
 * después de cada acción. obtenerEquipo/obtenerSolicitud (por ID)
 * siguen siendo suspend porque son consultas puntuales, no algo que
 * tenga sentido observar indefinidamente.
 */
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
}
