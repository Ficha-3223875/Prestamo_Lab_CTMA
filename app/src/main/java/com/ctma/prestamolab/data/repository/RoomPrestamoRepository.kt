package com.ctma.prestamolab.data.repository

import com.ctma.prestamolab.data.local.converter.aTexto
import com.ctma.prestamolab.data.local.dao.EquipoDao
import com.ctma.prestamolab.data.local.dao.EvidenciaDao
import com.ctma.prestamolab.data.local.dao.SolicitudDao
import com.ctma.prestamolab.data.local.entity.EquipoEntity
import com.ctma.prestamolab.data.remote.EquipoRemoteDataSource
import com.ctma.prestamolab.data.remote.ResultadoRed
import com.ctma.prestamolab.data.remote.dto.aEntidad
import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo
import com.ctma.prestamolab.model.EstadoEvidencia
import com.ctma.prestamolab.model.EstadoSolicitud
import com.ctma.prestamolab.model.EvidenciaFoto
import com.ctma.prestamolab.model.SolicitudPrestamo
import com.ctma.prestamolab.model.mapper.aDominio
import com.ctma.prestamolab.model.mapper.aEntidad
import com.ctma.prestamolab.model.puedeAprobarse
import com.ctma.prestamolab.model.puedeDevolverse
import com.ctma.prestamolab.model.puedeEntregarse
import com.ctma.prestamolab.model.puedeRechazarse
import com.ctma.prestamolab.model.validarFormularioSolicitud
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class RoomPrestamoRepository(
    private val equipoDao: EquipoDao,
    private val solicitudDao: SolicitudDao,
    private val evidenciaDao: EvidenciaDao,
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

    // --- Semana 9: evidencia fotográfica ---

    override fun observarEvidencias(solicitudId: Int): Flow<List<EvidenciaFoto>> =
        evidenciaDao.observarPorSolicitud(solicitudId).map { entidades -> entidades.map { it.aDominio() } }

    override suspend fun agregarEvidencia(
        solicitudId: Int,
        uri: String,
        luxAlCapturar: Float?
    ): Result<EvidenciaFoto> {
        val nueva = EvidenciaFoto(
            id = 0,
            solicitudId = solicitudId,
            uri = uri,
            fechaCapturaMillis = System.currentTimeMillis(),
            luxAlCapturar = luxAlCapturar,
            estado = EstadoEvidencia.LOCAL
        ).aEntidad()
        val nuevoId = evidenciaDao.insertar(nueva)
        val creada = evidenciaDao.obtenerPorId(nuevoId.toInt())!!.aDominio()
        return Result.success(creada)
    }

    /**
     * Simula el ciclo LOCAL -> SUBIENDO -> FALLIDA (no hay backend
     * real para llegar a SINCRONIZADA, igual que sincronizarCatalogoRemoto
     * en Semana 8 — es el mismo patrón local-first aplicado a evidencias).
     */
    override suspend fun sincronizarEvidencia(evidenciaId: Int): Result<Unit> {
        val entidad = evidenciaDao.obtenerPorId(evidenciaId)
            ?: return Result.failure(NoSuchElementException("La evidencia $evidenciaId no existe."))

        evidenciaDao.actualizar(entidad.copy(estado = EstadoEvidencia.SUBIENDO.name))
        delay(500) // simula el tiempo de subida antes de fallar

        evidenciaDao.actualizar(entidad.copy(estado = EstadoEvidencia.FALLIDA.name))
        return Result.failure(IllegalStateException("Sin conexión con el servidor. La evidencia queda guardada localmente."))
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
