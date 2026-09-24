package com.ctma.prestamolab.data.remote

import com.ctma.prestamolab.data.remote.dto.EquipoDto
import java.io.IOException

/**
 * Traduce las respuestas de Retrofit a ResultadoRed (Semana 8,
 * actividad 28). Recibe el PrestamoLabApiService por constructor (no
 * usa el singleton RetrofitConfig.apiService directamente) para poder
 * sustituirlo por uno apuntando a MockWebServer en las pruebas —
 * ver EquipoRemoteDataSourceTest.kt.
 */
class EquipoRemoteDataSource(
    private val api: PrestamoLabApiService
) {
    suspend fun obtenerEquipos(): ResultadoRed<List<EquipoDto>> {
        return try {
            val respuesta = api.obtenerEquipos()
            if (respuesta.isSuccessful) {
                val cuerpo = respuesta.body()
                if (cuerpo != null) {
                    ResultadoRed.Exito(cuerpo)
                } else {
                    ResultadoRed.ErrorHttp(respuesta.code(), "Respuesta vacía del servidor.")
                }
            } else {
                ResultadoRed.ErrorHttp(respuesta.code(), mensajeParaCodigo(respuesta.code()))
            }
        } catch (e: IOException) {
            // Sin conexión, timeout (3s configurados en RetrofitConfig), o host inexistente.
            ResultadoRed.SinConexion(e.message ?: "Sin conexión con el servidor.")
        }
    }

    private fun mensajeParaCodigo(codigo: Int): String = when (codigo) {
        401 -> "No autorizado (token inválido o ausente)."
        404 -> "El recurso solicitado no existe en el servidor."
        409 -> "Conflicto: la operación no es válida en el estado actual."
        in 500..599 -> "El servidor tuvo un error interno."
        else -> "Error HTTP $codigo."
    }
}
