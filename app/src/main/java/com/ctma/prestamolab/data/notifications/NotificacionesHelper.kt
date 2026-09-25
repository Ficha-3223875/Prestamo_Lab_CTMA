package com.ctma.prestamolab.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

/**
 * Notificaciones contextuales (Semana 9). El permiso POST_NOTIFICATIONS
 * se solicita SOLO desde la pantalla donde tiene sentido — justo antes
 * de registrar una devolución (ver SolicitudDetalleScreen.kt) — pero
 * ADEMÁS se verifica aquí mismo, justo al lado de la llamada real a
 * notify(), porque Android Lint no puede saber que ya se comprobó en
 * otro archivo: exige la verificación explícita en el mismo punto
 * donde ocurre la llamada que requiere el permiso (hallazgo real de
 * `lintDebug` en CI, corregido aquí, no silenciado con @SuppressLint).
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

    fun mostrarNotificacionDevolucion(context: Context, nombreEquipo: String) {
        crearCanalSiHaceFalta(context)

        val permisoConcedido = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

        if (!permisoConcedido) {
            return
        }

        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Devolución registrada")
            .setContentText("Se registró la devolución de \"$nombreEquipo\".")
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICACION_ID, notificacion)
    }
}
