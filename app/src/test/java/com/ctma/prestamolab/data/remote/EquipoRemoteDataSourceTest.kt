package com.ctma.prestamolab.data.remote

import com.google.gson.Gson
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Semana 8, actividades 26 y 27: pruebas de integración del cliente
 * HTTP con MockWebServer (un servidor HTTP real, pero local y de
 * mentira, que corre en el mismo proceso de prueba — no depende de
 * internet ni de un backend real).
 *
 * CASO PILOTO DE TDD (actividad 27, ciclo Red -> Green -> Refactor):
 * `debeDevolverErrorHttpCuandoElServidorResponde404` fue la primera
 * prueba escrita para EquipoRemoteDataSource, ANTES de que existiera
 * el manejo de códigos HTTP no exitosos en la clase (Red: la prueba
 * fallaba porque el método no distinguía 404 de un error genérico).
 * Se completó el ciclo agregando `mensajeParaCodigo()` en
 * EquipoRemoteDataSource hasta que la prueba pasó (Green), y luego se
 * extrajo esa función aparte para que fuera reutilizable en los demás
 * códigos de error (Refactor).
 */
class EquipoRemoteDataSourceTest {

    private lateinit var servidor: MockWebServer
    private lateinit var dataSource: EquipoRemoteDataSource

    @Before
    fun iniciarServidorFalso() {
        servidor = MockWebServer()
        servidor.start()

        val api = Retrofit.Builder()
            .baseUrl(servidor.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PrestamoLabApiService::class.java)

        dataSource = EquipoRemoteDataSource(api)
    }

    @After
    fun apagarServidorFalso() {
        servidor.shutdown()
    }

    @Test
    fun debeDevolverExitoCuandoElServidorResponde200ConDatosValidos() = runTest {
        val json = Gson().toJson(
            listOf(mapOf("id" to 1, "nombre" to "Multímetro digital", "categoria" to "ELECTRONICA", "estado" to "DISPONIBLE"))
        )
        servidor.enqueue(MockResponse().setResponseCode(200).setBody(json))

        val resultado = dataSource.obtenerEquipos()

        assertTrue(resultado is ResultadoRed.Exito)
        val equipos = (resultado as ResultadoRed.Exito).datos
        assertEquals(1, equipos.size)
        assertEquals("Multímetro digital", equipos[0].nombre)
    }

    @Test
    fun debeDevolverErrorHttpCuandoElServidorResponde404() = runTest {
        servidor.enqueue(MockResponse().setResponseCode(404).setBody("{}"))

        val resultado = dataSource.obtenerEquipos()

        assertTrue(resultado is ResultadoRed.ErrorHttp)
        assertEquals(404, (resultado as ResultadoRed.ErrorHttp).codigo)
    }

    @Test
    fun debeDevolverErrorHttpCuandoElServidorResponde500() = runTest {
        servidor.enqueue(MockResponse().setResponseCode(500).setBody("{}"))

        val resultado = dataSource.obtenerEquipos()

        assertTrue(resultado is ResultadoRed.ErrorHttp)
        assertEquals(500, (resultado as ResultadoRed.ErrorHttp).codigo)
    }

    @Test
    fun debeDevolverSinConexionCuandoElServidorNoResponde() = runTest {
        servidor.shutdown() // el servidor ya no existe: simula "sin conexión"

        val resultado = dataSource.obtenerEquipos()

        assertTrue(resultado is ResultadoRed.SinConexion)
    }
}
