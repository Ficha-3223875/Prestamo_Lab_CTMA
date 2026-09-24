package com.ctma.prestamolab.data.repository

import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo
import com.ctma.prestamolab.model.EstadoEvidencia
import com.ctma.prestamolab.model.EstadoSolicitud
import com.ctma.prestamolab.model.EvidenciaFoto
import com.ctma.prestamolab.model.SolicitudPrestamo
import com.ctma.prestamolab.model.puedeAprobarse
import com.ctma.prestamolab.model.puedeDevolverse
import com.ctma.prestamolab.model.puedeEntregarse
import com.ctma.prestamolab.model.puedeRechazarse
import com.ctma.prestamolab.model.validarFormularioSolicitud
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class InMemoryPrestamoRepository : PrestamoRepository {

    private val equipoIdGenerator = AtomicInteger(1)
    private val solicitudIdGenerator = AtomicInteger(1)
    private val evidenciaIdGenerator = AtomicInteger(1)

    private val _equipos = MutableStateFlow(
        listOf(
            Equipo(sig(equipoIdGenerator), "Multímetro digital", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
            Equipo(sig(equipoIdGenerator), "Kit de electrónica básica", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
            Equipo(sig(equipoIdGenerator), "Tableta Android (demo)", CategoriaEquipo.INFORMATICA, EstadoEquipo.DISPONIBLE),
            Equipo(sig(equipoIdGenerator), "Cámara réflex", CategoriaEquipo.AUDIOVISUAL, EstadoEquipo.DISPONIBLE),
            Equipo(sig(equipoIdGenerator), "Kit de destornilladores", CategoriaEquipo.HERRAMIENTA_MANUAL, EstadoEquipo.PRESTADO),
            Equipo(sig(equipoIdGenerator), "Adaptador HDMI-VGA", CategoriaEquipo.OTRO, EstadoEquipo.DISPONIBLE)
        )
    )
    private val _solicitudes = MutableStateFlow<List<SolicitudPrestamo>>(emptyList())
    private val _evidencias = MutableStateFlow<List<EvidenciaFoto>>(emptyList())

    private fun sig(counter: AtomicInteger) = counter.getAndIncrement()

    override fun observarEquipos(): Flow<List<Equipo>> = _equipos.asStateFlow()
    override fun observarSolicitudes(): Flow<List<SolicitudPrestamo>> = _solicitudes.asStateFlow()

    override suspend fun obtenerEquipo(id: Int): Equipo? = _equipos.value.find { it.id == id }
    override suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo? = _solicitudes.value.find { it.id == id }

    override suspend fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ): Result<SolicitudPrestamo> = synchronized(this) {
        val equipo = _equipos.value.find { it.id == equipoId }
        val mensajeError = validarFormularioSolicitud(equipo, ambienteDestino, proposito, duracionHoras)
        if (mensajeError != null) {
            return Result.failure(IllegalArgumentException(mensajeError))
        }

        _equipos.value = _equipos.value.map {
            if (it.id == equipoId) it.copy(estado = EstadoEquipo.RESERVADO) else it
        }

        val nueva = SolicitudPrestamo(
            id = sig(solicitudIdGenerator),
            equipoId = equipoId,
            ambienteDestino = ambienteDestino.trim(),
            proposito = proposito.trim(),
            duracionHoras = duracionHoras,
            estado = EstadoSolicitud.SOLICITADA
        )
        _solicitudes.value = _solicitudes.value + nueva
        Result.success(nueva)
    }

    override suspend fun cancelarSolicitud(id: Int): Result<Unit> =
        transicionar(id, { it != null && it.estado == EstadoSolicitud.SOLICITADA }, EstadoSolicitud.CANCELADA, EstadoEquipo.DISPONIBLE, "Solo una solicitud SOLICITADA puede cancelarse.")

    override suspend fun aprobarSolicitud(id: Int): Result<Unit> =
        transicionar(id, ::puedeAprobarse, EstadoSolicitud.APROBADA, null, "Solo una solicitud SOLICITADA puede aprobarse.")

    override suspend fun rechazarSolicitud(id: Int): Result<Unit> =
        transicionar(id, ::puedeRechazarse, EstadoSolicitud.RECHAZADA, EstadoEquipo.DISPONIBLE, "Solo una solicitud SOLICITADA puede rechazarse.")

    override suspend fun entregarSolicitud(id: Int): Result<Unit> =
        transicionar(id, ::puedeEntregarse, EstadoSolicitud.ENTREGADA, EstadoEquipo.PRESTADO, "Solo una solicitud APROBADA puede marcarse como entregada.")

    override suspend fun devolverSolicitud(id: Int): Result<Unit> =
        transicionar(id, ::puedeDevolverse, EstadoSolicitud.DEVUELTA, EstadoEquipo.DISPONIBLE, "Solo una solicitud ENTREGADA puede marcarse como devuelta.")

    override suspend fun sincronizarCatalogoRemoto(): Result<Unit> =
        Result.failure(UnsupportedOperationException("InMemoryPrestamoRepository no tiene capa de red; se usa solo en pruebas unitarias."))

    override fun observarEvidencias(solicitudId: Int): Flow<List<EvidenciaFoto>> =
        _evidencias.map { lista -> lista.filter { it.solicitudId == solicitudId } }

    override suspend fun agregarEvidencia(
        solicitudId: Int,
        uri: String,
        luxAlCapturar: Float?
    ): Result<EvidenciaFoto> = synchronized(this) {
        val nueva = EvidenciaFoto(
            id = sig(evidenciaIdGenerator),
            solicitudId = solicitudId,
            uri = uri,
            fechaCapturaMillis = System.currentTimeMillis(),
            luxAlCapturar = luxAlCapturar,
            estado = EstadoEvidencia.LOCAL
        )
        _evidencias.value = _evidencias.value + nueva
        Result.success(nueva)
    }

    override suspend fun sincronizarEvidencia(evidenciaId: Int): Result<Unit> = synchronized(this) {
        val existe = _evidencias.value.any { it.id == evidenciaId }
        if (!existe) return Result.failure(NoSuchElementException("La evidencia $evidenciaId no existe."))
        Result.failure(UnsupportedOperationException("InMemoryPrestamoRepository no tiene capa de red; se usa solo en pruebas unitarias."))
    }

    private fun transicionar(
        id: Int,
        condicion: (SolicitudPrestamo?) -> Boolean,
        nuevoEstadoSolicitud: EstadoSolicitud,
        nuevoEstadoEquipo: EstadoEquipo?,
        mensajeError: String
    ): Result<Unit> = synchronized(this) {
        val solicitud = _solicitudes.value.find { it.id == id }
        if (!condicion(solicitud)) {
            return Result.failure(IllegalStateException(mensajeError))
        }
        _solicitudes.value = _solicitudes.value.map {
            if (it.id == id) it.copy(estado = nuevoEstadoSolicitud) else it
        }
        if (nuevoEstadoEquipo != null) {
            _equipos.value = _equipos.value.map {
                if (it.id == solicitud!!.equipoId) it.copy(estado = nuevoEstadoEquipo) else it
            }
        }
        Result.success(Unit)
    }
}
