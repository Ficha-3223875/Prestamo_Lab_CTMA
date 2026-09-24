package com.ctma.prestamolab.data.remote

import com.ctma.prestamolab.data.remote.dto.EquipoDto
import com.ctma.prestamolab.data.remote.dto.SolicitudDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Contrato Retrofit del API remota (Semana 8, actividad 25). Coincide
 * exactamente con lo documentado en docs/CONTRATO_API.md. Devuelve
 * Response<T> (no el tipo directo) para poder inspeccionar el código
 * HTTP exacto en el RemoteDataSource, en vez de que Retrofit lance una
 * excepción genérica en cualquier código distinto de 2xx.
 */
interface PrestamoLabApiService {

    @GET("equipos")
    suspend fun obtenerEquipos(): Response<List<EquipoDto>>

    @GET("equipos/{id}")
    suspend fun obtenerEquipo(@Path("id") id: Int): Response<EquipoDto>

    @GET("solicitudes")
    suspend fun obtenerSolicitudes(): Response<List<SolicitudDto>>

    @POST("solicitudes")
    suspend fun crearSolicitud(@Body solicitud: SolicitudDto): Response<SolicitudDto>

    @PATCH("solicitudes/{id}/estado")
    suspend fun cambiarEstadoSolicitud(
        @Path("id") id: Int,
        @Body cuerpo: Map<String, String>
    ): Response<SolicitudDto>
}
