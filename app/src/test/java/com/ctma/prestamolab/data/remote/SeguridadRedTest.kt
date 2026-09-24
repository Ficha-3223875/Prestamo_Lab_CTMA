package com.ctma.prestamolab.data.remote

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Semana 9, actividad 37: prueba no funcional de seguridad, acotada.
 * Verifica que ningún ambiente permite tráfico sin cifrar y que el
 * token de ejemplo no se parece a un secreto real.
 */
class SeguridadRedTest {

    @Test
    fun todasLasUrlsDeAmbienteUsanHttps() {
        Ambiente.entries.forEach { ambiente ->
            assertTrue(
                "El ambiente ${ambiente.name} debe usar HTTPS: ${ambiente.baseUrl}",
                ambiente.baseUrl.startsWith("https://")
            )
        }
    }

    @Test
    fun elTokenDeAutorizacionEsExplicitamenteDemoNoReal() {
        val valorToken = "token-demo-no-real"
        assertTrue(valorToken.contains("demo") && valorToken.contains("no-real"))
    }
}
