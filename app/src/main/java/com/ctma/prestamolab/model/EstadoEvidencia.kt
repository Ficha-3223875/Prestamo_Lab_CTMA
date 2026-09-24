package com.ctma.prestamolab.model

/**
 * Estados de sincronización de una evidencia fotográfica (Semana 9,
 * actividad 32: "Modelar la sincronización de evidencia y sus
 * estados"). Coincide exactamente con los 4 estados que pide la guía.
 */
enum class EstadoEvidencia {
    LOCAL,       // Recién capturada/seleccionada, solo existe en el dispositivo.
    SUBIENDO,    // Intentando sincronizar con el servidor.
    SINCRONIZADA,// Confirmada por el servidor (no ocurre en este prototipo sin backend real).
    FALLIDA      // El intento de sincronización falló; se conserva localmente.
}
