package com.ctma.prestamolab.data.repository

import com.ctma.prestamolab.data.local.converter.aTexto
import com.ctma.prestamolab.data.local.dao.EquipoDao
import com.ctma.prestamolab.data.local.dao.SolicitudDao
import com.ctma.prestamolab.data.local.entity.EquipoEntity
import com.ctma.prestamolab.data.remote.EquipoRemoteDataSource
import com.ctma.prestamolab.data.remote.ResultadoRed
import com.ctma.prestamolab.data.remote.dto.aEntidad
import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo
import com.ctma.prestamolab.model.EstadoSolicitud
import com.ctma.prestamolab.model.SolicitudPrestamo
import com.ctma.prestamolab.model.mapper.aDominio
import com.ctma.prestamolab.model.mapper.aEntidad
import com.ctma.prestamolab.model.puedeAprobarse
import com.ctma.prestamolab.model.puedeDevolverse
import com.ctma.prestamolab.model.puedeEntregarse
import com.ctma.prestamolab.model.puedeRechazarse
import com.ctma.prestamolab.model.validarFormularioSolicitud
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Implementación real con Room + Retrofit (Semana 6 y 8). Room sigue
 * siendo la fuente de verdad visible (observarEquipos/observarSolicitudes
 * son Flow sobre la base de datos local); sincronizarCatalogoRemoto()
 * es la única operación que toca la red, y solo actualiza Room si tiene
 * éxito — la UI nunca observa el Repository remoto directamente.
 */
class RoomPrestamoRepository(
    private val equipoDao: EquipoDao,
    private val solicitudDao: SolicitudDao,
    private val equipoRemoteDataSource: EquipoRemoteDataSource
) : PrestamoRepository {

    suspend fun sembrarCatalogoSiEstaVacio() {
        if (equipoDao.contar() == 0) {
            val semilla = listOf(
                EquipoEntity(nombre = "Multímetro digital", categoria = CategoriaEquipo.ELECTRONICA.aTexto(), estado = EstadoEquipo.DISPONIBLE.aTexto()),
                EquipoEntity(nombre = "Kit de electrónica básica", categoria = CategoriaEquipo.ELECTRONICA.aTexto(), estado = EstadoEquipo.DISPONIBLE.aTexto()),
                EquipoEntity(nombre = "Tableta Android (demo)", categoria = CategoriaEquipo.INFORMATICA.aTexto(), estado = EstadoEquipo.DISPONIBLE.aTexto()),
                EquipoEntity(nombre = "Cámara réflex", categoria = CategoriaEquipo.AUDIOVISUAL.aTexto(), estado = EstadoEquipo.DISPONIBLE.aTexto()),
                EquipoEntity(nombre = "Kit de destornilladores", categoria = CategoriaEquipo.HERRAMIENTA_MANUAL.aTexto(), estado = EstadoEquipo.PRESTADO.aTexto()),
                EquipoEntity(nombre = "Adaptador HDMI-VGA", categoria = CategoriaEquipo.OTRO.aTexto(), estado = EstadoEquipo.DISPONIBLE.aTexto())
            )
            equipoDao.insertarTodos(semilla)
        }
    }

    override fun observarEquipos(): Flow<List<Equipo>> =
        equipoDao.observarTodos()
            .map { entidades -> entidades.map { it.aDominio() } }
            .onStart { sembrarCatalogoSiEstaVacio() }

    override fun observarSolicitudes(): Flow<List<SolicitudPrestamo>> =
        solicitudDao.observarTodas().map { entidades -> entidades.map { it.aDominio() } }

    override suspend fun obtenerEquipo(id: Int): Equipo? =
        equipoDao.obtenerPorId(id)?.aDominio()

    override suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo? =
        solicitudDao.obtenerPorId(id)?.aDominio()

    override suspend fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ): Result<SolicitudPrestamo> {
        val equipoEntity = equipoDao.obtenerPorId(equipoId)
        val equipo = equipoEntity?.aDominio()
        val mensajeError = validarFormularioSolicitud(equipo, ambienteDestino, proposito, duracionHoras)
        if (mensajeError != null) {
            return Result.failure(IllegalArgumentException(mensajeError))
        }

        equipoDao.actualizar(equipoEntity!!.copy(estado = EstadoEquipo.RESERVADO.aTexto()))

        val nuevaEntity = SolicitudPrestamo(
            id = 0,
            equipoId = equipoId,
            ambienteDestino = ambienteDestino.trim(),
            proposito = proposito.trim(),
            duracionHoras = duracionHoras,
            estado = EstadoSolicitud.SOLICITADA
        ).aEntidad()

        val nuevoId = solicitudDao.insertar(nuevaEntity)
        val creada = solicitudDao.obtenerPorId(nuevoId.toInt())!!.aDominio()
        return Result.success(creada)
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

    /**
     * Semana 8: intenta sincronizar el catálogo con el servidor remoto.
     * Como PréstamoLab no tiene backend real desplegado (ver
     * docs/CONTRATO_API.md), esto va a fallar con SinConexion en la
     * práctica — y eso es exactamente lo que se espera: demuestra que
     * el patrón local-first protege la app incluso cuando la red no
     * está disponible, sin bloquear ni dañar los datos que ya existen.
     */
    override suspend fun sincronizarCatalogoRemoto(): Result<Unit> {
        return when (val resultado = equipoRemoteDataSource.obtenerEquipos()) {
            is ResultadoRed.Exito -> {
                val entidades = resultado.datos.map { it.aEntidad() }
                equipoDao.insertarTodos(entidades)
                Result.success(Unit)
            }
            is ResultadoRed.ErrorHttp -> Result.failure(
                IllegalStateException("El servidor respondió ${resultado.codigo}: ${resultado.mensaje}")
            )
            is ResultadoRed.SinConexion -> Result.failure(
                IllegalStateException("Sin conexión con el servidor. Mostrando datos locales.")
            )
        }
    }

    private suspend fun transicionar(
        id: Int,
        condicion: (SolicitudPrestamo?) -> Boolean,
        nuevoEstadoSolicitud: EstadoSolicitud,
        nuevoEstadoEquipo: EstadoEquipo?,
        mensajeError: String
    ): Result<Unit> {
        val entidad = solicitudDao.obtenerPorId(id)
        val solicitud = entidad?.aDominio()
        if (!condicion(solicitud)) {
            return Result.failure(IllegalStateException(mensajeError))
        }
        solicitudDao.actualizar(entidad!!.copy(estado = nuevoEstadoSolicitud.aTexto()))

        if (nuevoEstadoEquipo != null) {
            val equipoEntity = equipoDao.obtenerPorId(entidad.equipoId)
            if (equipoEntity != null) {
                equipoDao.actualizar(equipoEntity.copy(estado = nuevoEstadoEquipo.aTexto()))
            }
        }
        return Result.success(Unit)
    }
}
