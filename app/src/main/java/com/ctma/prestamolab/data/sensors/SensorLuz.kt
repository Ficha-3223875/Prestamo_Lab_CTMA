package com.ctma.prestamolab.data.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Capacidad física adicional (Semana 9, actividad 31): sensor de luz
 * ambiental. Se eligió sobre GPS/Bluetooth/biometría porque:
 *  - No requiere ningún permiso peligroso (a diferencia de ubicación
 *    o biometría), cumpliendo mínimo privilegio (actividad 33) casi
 *    "gratis".
 *  - Se puede simular en el emulador (Extended Controls > Virtual
 *    sensors > Light), así que es reproducible sin dispositivo físico.
 *  - Tiene un uso real y justificado en PréstamoLab: advertir si la
 *    foto de evidencia de devolución se va a tomar con poca luz,
 *    antes de que quede una evidencia inútil guardada.
 *
 * callbackFlow envuelve el listener basado en callbacks de Android en
 * un Flow, para que el resto de la app lo consuma igual que cualquier
 * otro flujo reactivo (Room, DataStore).
 */
class SensorLuz(context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensorDeLuz: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    val disponible: Boolean get() = sensorDeLuz != null

    /** Umbral bajo el cual se considera "poca luz" para una foto de evidencia. */
    companion object {
        const val UMBRAL_LUZ_BAJA_LUX = 20f
    }

    fun observarLux(): Flow<Float> = callbackFlow {
        if (sensorDeLuz == null) {
            close()
            return@callbackFlow
        }
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                trySend(event.values[0])
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        sensorManager.registerListener(listener, sensorDeLuz, SensorManager.SENSOR_DELAY_NORMAL)
        awaitClose { sensorManager.unregisterListener(listener) }
    }
}
