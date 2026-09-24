package com.ctma.prestamolab.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * Notificaciones contextuales (Semana 9). El permiso POST_NOTIFICATIONS
 * (obligatorio desde Android 13 / API 33) se solicita SOLO desde la
 * pantalla donde tiene sentido — justo antes de registrar una
 * devolución — no al abrir la app (actividad 33: "solicitar permisos
 * solo cuando la funcionalidad los necesita"). Ver el composable
 * que pide el permiso en SolicitudDetalleScreen.kt.
 */
object NotificacionesHelper {

    private const val CANAL_ID = "prestamolab_devoluciones"
    private const val NOTIFICACION_ID = 1001

    fun crearCanalSiHaceFalta(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Devoluciones",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Avisos cuando se registra la devolución de un equipo."
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(canal)
        }
    }

    /**
     * No verifica el permiso aquí a propósito: quien llama (la UI) ya
     * debió confirmar que el permiso está concedido antes de invocar
     * esto, así el flujo de solicitud de permiso queda visible y
     * explícito en la pantalla, no escondido dentro de este helper.
     */
    fun mostrarNotificacionDevolucion(context: Context, nombreEquipo: String) {
        crearCanalSiHaceFalta(context)
        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Devolución registrada")
            .setContentText("Se registró la devolución de \"$nombreEquipo\".")
            .setAutoCancel(true)
            .build()

        // NotificationManagerCompat.notify ya verifica internamente el
        // permiso en API 33+; si no está concedido, simplemente no
        // muestra nada en vez de lanzar una excepción sin control.
        NotificationManagerCompat.from(context).notify(NOTIFICACION_ID, notificacion)
    }
}
