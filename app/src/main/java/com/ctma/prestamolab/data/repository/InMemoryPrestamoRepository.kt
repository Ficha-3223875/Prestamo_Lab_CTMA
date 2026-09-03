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
 * Implementación en memoria, compartida durante la ejecución de la app
 * (sección 4.3, punto 11). No hay persistencia real: al cerrar la app se
 * pierde el estado, algo aceptado y documentado como limitación del MVP.
 *
 * synchronized() en crearSolicitud/cancelarSolicitud protege la
 * consistencia de catálogo + solicitudes ante escrituras concurrentes
 * (segunda barrera para RN-05, además del control de "guardando" en el
 * ViewModel).
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

    override fun obtenerEquipos(): List<Equipo> = equipos.toList()

    override fun obtenerEquipo(id: Int): Equipo? = equipos.find { it.id == id }

    override fun obtenerSolicitudes(): List<SolicitudPrestamo> = solicitudes.toList()

    override fun obtenerSolicitud(id: Int): SolicitudPrestamo? = solicitudes.find { it.id == id }

    override fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ): Result<SolicitudPrestamo> = synchronized(this) {
        val equipo = obtenerEquipo(equipoId)
        val mensajeError = validarFormularioSolicitud(equipo, ambienteDestino, proposito, duracionHoras)
        if (mensajeError != null) {
            return@synchronized Result.failure(IllegalArgumentException(mensajeError))
        }

        // RN-06: la solicitud reserva el equipo de inmediato.
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

    override fun cancelarSolicitud(id: Int): Result<Unit> = synchronized(this) {
        val index = solicitudes.indexOfFirst { it.id == id }
        if (index == -1) {
            return@synchronized Result.failure(NoSuchElementException("La solicitud $id no existe."))
        }
        val solicitud = solicitudes[index]
        if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
            return@synchronized Result.failure(
                IllegalStateException("Solo una solicitud SOLICITADA puede cancelarse.")
            )
        }
        solicitudes[index] = solicitud.copy(estado = EstadoSolicitud.CANCELADA)

        // RN-06 (liberación): al cancelar, el equipo vuelve a estar DISPONIBLE.
        val equipoIndex = equipos.indexOfFirst { it.id == solicitud.equipoId }
        if (equipoIndex != -1) {
            equipos[equipoIndex] = equipos[equipoIndex].copy(estado = EstadoEquipo.DISPONIBLE)
        }
        Result.success(Unit)
    }

    /**
     * Transición genérica reutilizada por aprobar/rechazar/entregar/devolver
     * (HU-07): valida con [condicion], cambia el estado de la solicitud y,
     * si aplica, el estado del equipo asociado.
     */
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
            return@synchronized Result.failure(IllegalStateException(mensajeError))
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

    override fun aprobarSolicitud(id: Int): Result<Unit> = transicionar(
        id = id,
        condicion = ::puedeAprobarse,
        nuevoEstadoSolicitud = EstadoSolicitud.APROBADA,
        nuevoEstadoEquipo = null, // el equipo sigue RESERVADO hasta la entrega
        mensajeError = "Solo una solicitud SOLICITADA puede aprobarse."
    )

    override fun rechazarSolicitud(id: Int): Result<Unit> = transicionar(
        id = id,
        condicion = ::puedeRechazarse,
        nuevoEstadoSolicitud = EstadoSolicitud.RECHAZADA,
        nuevoEstadoEquipo = EstadoEquipo.DISPONIBLE, // se libera al rechazar
        mensajeError = "Solo una solicitud SOLICITADA puede rechazarse."
    )

    override fun entregarSolicitud(id: Int): Result<Unit> = transicionar(
        id = id,
        condicion = ::puedeEntregarse,
        nuevoEstadoSolicitud = EstadoSolicitud.ENTREGADA,
        nuevoEstadoEquipo = EstadoEquipo.PRESTADO,
        mensajeError = "Solo una solicitud APROBADA puede marcarse como entregada."
    )

    override fun devolverSolicitud(id: Int): Result<Unit> = transicionar(
        id = id,
        condicion = ::puedeDevolverse,
        nuevoEstadoSolicitud = EstadoSolicitud.DEVUELTA,
        nuevoEstadoEquipo = EstadoEquipo.DISPONIBLE,
        mensajeError = "Solo una solicitud ENTREGADA puede marcarse como devuelta."
    )
}
