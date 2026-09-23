package com.ctma.prestamolab.data.repository

import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo
import com.ctma.prestamolab.model.EstadoSolicitud
import com.ctma.prestamolab.model.SolicitudPrestamo
import com.ctma.prestamolab.model.puedeAprobarse
import com.ctma.prestamolab.model.puedeDevolverse
import com.ctma.prestamolab.model.puedeEntregarse
import com.ctma.prestamolab.model.puedeRechazarse
import com.ctma.prestamolab.model.validarFormularioSolicitud
import java.util.concurrent.atomic.AtomicInteger

/**
 * Implementación en memoria de la línea base (primera guía). Ya NO es
 * la fuente de datos real de la app (ese rol lo tiene ahora
 * RoomPrestamoRepository) — se conserva porque:
 *  1) las pruebas unitarias (JVM) la usan para no depender de
 *     Android/SQLite y correr en milisegundos;
 *  2) documenta la línea base heredada, tal como pide la guía
 *     (sección 3: "los artefactos heredados se revisan, refinan,
 *     versionan y amplían", no se borran).
 *
 * Las funciones ahora son suspend para cumplir el contrato de
 * PrestamoRepository, aunque en memoria no haya espera real.
 */
class InMemoryPrestamoRepository : PrestamoRepository {

    private val equipoIdGenerator = AtomicInteger(1)
    private val solicitudIdGenerator = AtomicInteger(1)

    private val equipos = mutableListOf(
        Equipo(sig(equipoIdGenerator), "Multímetro digital", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
        Equipo(sig(equipoIdGenerator), "Kit de electrónica básica", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
        Equipo(sig(equipoIdGenerator), "Tableta Android (demo)", CategoriaEquipo.INFORMATICA, EstadoEquipo.DISPONIBLE),
        Equipo(sig(equipoIdGenerator), "Cámara réflex", CategoriaEquipo.AUDIOVISUAL, EstadoEquipo.DISPONIBLE),
        Equipo(sig(equipoIdGenerator), "Kit de destornilladores", CategoriaEquipo.HERRAMIENTA_MANUAL, EstadoEquipo.PRESTADO),
        Equipo(sig(equipoIdGenerator), "Adaptador HDMI-VGA", CategoriaEquipo.OTRO, EstadoEquipo.DISPONIBLE)
    )

    private val solicitudes = mutableListOf<SolicitudPrestamo>()

    private fun sig(counter: AtomicInteger) = counter.getAndIncrement()

    override suspend fun obtenerEquipos(): List<Equipo> = equipos.toList()

    override suspend fun obtenerEquipo(id: Int): Equipo? = equipos.find { it.id == id }

    override suspend fun obtenerSolicitudes(): List<SolicitudPrestamo> = solicitudes.toList()

    override suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo? = solicitudes.find { it.id == id }

    override suspend fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ): Result<SolicitudPrestamo> = synchronized(this) {
        val equipo = equipos.find { it.id == equipoId }
        val mensajeError = validarFormularioSolicitud(equipo, ambienteDestino, proposito, duracionHoras)
        if (mensajeError != null) {
            return Result.failure(IllegalArgumentException(mensajeError))
        }

        val index = equipos.indexOfFirst { it.id == equipoId }
        equipos[index] = equipos[index].copy(estado = EstadoEquipo.RESERVADO)

        val nueva = SolicitudPrestamo(
            id = sig(solicitudIdGenerator),
            equipoId = equipoId,
            ambienteDestino = ambienteDestino.trim(),
            proposito = proposito.trim(),
            duracionHoras = duracionHoras,
            estado = EstadoSolicitud.SOLICITADA
        )
        solicitudes.add(nueva)
        Result.success(nueva)
    }

    override suspend fun cancelarSolicitud(id: Int): Result<Unit> = synchronized(this) {
        val index = solicitudes.indexOfFirst { it.id == id }
        if (index == -1) {
            return Result.failure(NoSuchElementException("La solicitud $id no existe."))
        }
        val solicitud = solicitudes[index]
        if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
            return Result.failure(IllegalStateException("Solo una solicitud SOLICITADA puede cancelarse."))
        }
        solicitudes[index] = solicitud.copy(estado = EstadoSolicitud.CANCELADA)
        val equipoIndex = equipos.indexOfFirst { it.id == solicitud.equipoId }
        if (equipoIndex != -1) {
            equipos[equipoIndex] = equipos[equipoIndex].copy(estado = EstadoEquipo.DISPONIBLE)
        }
        Result.success(Unit)
    }

    private fun transicionar(
        id: Int,
        condicion: (SolicitudPrestamo?) -> Boolean,
        nuevoEstadoSolicitud: EstadoSolicitud,
        nuevoEstadoEquipo: EstadoEquipo?,
        mensajeError: String
    ): Result<Unit> = synchronized(this) {
        val index = solicitudes.indexOfFirst { it.id == id }
        val solicitud = index.takeIf { it != -1 }?.let { solicitudes[it] }
        if (!condicion(solicitud)) {
            return Result.failure(IllegalStateException(mensajeError))
        }
        solicitudes[index] = solicitud!!.copy(estado = nuevoEstadoSolicitud)
        if (nuevoEstadoEquipo != null) {
            val equipoIndex = equipos.indexOfFirst { it.id == solicitud.equipoId }
            if (equipoIndex != -1) {
                equipos[equipoIndex] = equipos[equipoIndex].copy(estado = nuevoEstadoEquipo)
            }
        }
        Result.success(Unit)
    }

    override suspend fun aprobarSolicitud(id: Int): Result<Unit> = transicionar(
        id, ::puedeAprobarse, EstadoSolicitud.APROBADA, null,
        "Solo una solicitud SOLICITADA puede aprobarse."
    )

    override suspend fun rechazarSolicitud(id: Int): Result<Unit> = transicionar(
        id, ::puedeRechazarse, EstadoSolicitud.RECHAZADA, EstadoEquipo.DISPONIBLE,
        "Solo una solicitud SOLICITADA puede rechazarse."
    )

    override suspend fun entregarSolicitud(id: Int): Result<Unit> = transicionar(
        id, ::puedeEntregarse, EstadoSolicitud.ENTREGADA, EstadoEquipo.PRESTADO,
        "Solo una solicitud APROBADA puede marcarse como entregada."
    )

    override suspend fun devolverSolicitud(id: Int): Result<Unit> = transicionar(
        id, ::puedeDevolverse, EstadoSolicitud.DEVUELTA, EstadoEquipo.DISPONIBLE,
        "Solo una solicitud ENTREGADA puede marcarse como devuelta."
    )
}
