package com.ctma.prestamolab.data.repository

import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EvidenciaFoto
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

    suspend fun sincronizarCatalogoRemoto(): Result<Unit>

    // --- Semana 9: evidencia fotográfica ---
    fun observarEvidencias(solicitudId: Int): Flow<List<EvidenciaFoto>>
    suspend fun agregarEvidencia(solicitudId: Int, uri: String, luxAlCapturar: Float?): Result<EvidenciaFoto>
    suspend fun sincronizarEvidencia(evidenciaId: Int): Result<Unit>
}
