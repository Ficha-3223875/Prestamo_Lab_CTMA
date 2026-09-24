package com.ctma.prestamolab.data.remote

/**
 * Representa el resultado de una llamada de red (Semana 8, actividad
 * 28: "manejar 401/404/5xx y conectividad"). En vez de dejar que una
 * excepción de red se propague sin control hasta el ViewModel, cada
 * llamada del RemoteDataSource devuelve uno de estos tres casos, que
 * el ViewModel puede manejar de forma exhaustiva (when sin rama else).
 */
sealed interface ResultadoRed<out T> {
    data class Exito<T>(val datos: T) : ResultadoRed<T>
    data class ErrorHttp(val codigo: Int, val mensaje: String) : ResultadoRed<Nothing>
    data class SinConexion(val causa: String) : ResultadoRed<Nothing>
}
