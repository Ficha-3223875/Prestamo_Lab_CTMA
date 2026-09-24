# Capacidad física adicional — Sensor de luz ambiental

Semana 9, actividad 31: justificación técnica de la capacidad física
adicional a la cámara, según lo exige la guía.

## Capacidad elegida

**Sensor de luz ambiental** (`Sensor.TYPE_LIGHT`, `SensorLuz.kt`).

## Por qué esta y no otra

| Opción | Por qué se descartó / eligió |
|--------|-------------------------------|
| GPS/mapas | Requiere permiso de ubicación (dato sensible) sin un caso de uso claro en PréstamoLab: los préstamos son dentro del mismo centro de formación. |
| Bluetooth/BLE | No es simulable de forma confiable en el emulador estándar de Android Studio sin configuración adicional. |
| Biometría | Requiere huella/rostro enrolado en el dispositivo de prueba, difícil de reproducir en una demo o en el equipo del profesor. |
| Gestión de energía/almacenamiento | Alcance más amplio de lo necesario para una sola HU. |
| **Sensor de luz** ✅ | No requiere ningún permiso peligroso, es 100% simulable desde el emulador (Extended Controls > Virtual sensors > Light) y tiene un caso de uso real y acotado. |

## Caso de uso

Al momento de agregar una foto de evidencia para una devolución, la
app lee el sensor de luz en tiempo real y advierte si la luz
ambiental es baja (`< 20 lux`), para que la persona sepa que la foto
podría no quedar clara antes de guardarla como evidencia.

## API utilizada

`SensorManager` + `SensorEventListener` sobre `Sensor.TYPE_LIGHT`,
envuelto en un `Flow` con `callbackFlow` (`SensorLuz.kt`) para
integrarse igual que cualquier otro flujo reactivo de la app (Room,
DataStore).

## Permisos requeridos

**Ninguno.** Los sensores de movimiento/ambiente (luz, acelerómetro,
proximidad) no están en la lista de "dangerous permissions" de
Android — se leen sin pedir autorización al usuario.

## Manejo de errores

Si el dispositivo no tiene sensor de luz (`sensorDeLuz == null`), el
`Flow` se cierra sin emitir nada y la UI muestra "Sensor de luz no
disponible en este dispositivo" en vez de fallar o mostrar un dato
falso.

## Privacidad

El valor de lux se guarda junto con cada evidencia (`luxAlCapturar`
en `EvidenciaFoto`/`EvidenciaEntity`) únicamente como metadato de
calidad de la foto — no identifica a ninguna persona ni revela
ubicación.

## Pruebas realizadas

- Manual: en el emulador, Extended Controls > Virtual sensors > Light,
  bajar el valor por debajo de 20 lux y confirmar que aparece la
  advertencia en `SolicitudDetalleScreen`.
- Manual: subir el valor por encima de 20 lux y confirmar que el
  mensaje cambia a informativo, sin advertencia.
