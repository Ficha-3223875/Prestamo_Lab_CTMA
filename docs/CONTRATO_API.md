# Contrato de API remota — PréstamoLab CTMA (Semana 8)

PréstamoLab no cuenta con un backend real desplegado (siempre ha sido
un prototipo con datos sintéticos). Este contrato define cómo SERÍA
la API si existiera, y es exactamente el que usa `PrestamoLabApiService`
y las pruebas con MockWebServer — la app está preparada para
consumirlo tan pronto exista un servidor real, siguiendo el patrón
local-first que pide la guía (sección 8, Semana 8, actividad 26):
Room sigue siendo la fuente que se ve en pantalla; la red solo
sincroniza cuando puede.

## Base URL (placeholder, sin servidor real detrás)

```
https://api.prestamolab.ctma.example/v1/
```

## Endpoints

| Método | Ruta | Descripción | Respuesta éxito | Errores esperados |
|--------|------|--------------|-------------------|---------------------|
| GET | `/equipos` | Lista completa del catálogo | 200 + `List<EquipoDto>` | 500 (servidor caído) |
| GET | `/equipos/{id}` | Detalle de un equipo | 200 + `EquipoDto` | 404 (no existe) |
| GET | `/solicitudes` | Lista de solicitudes del usuario | 200 + `List<SolicitudDto>` | 401 (sin token) |
| POST | `/solicitudes` | Crear una solicitud | 201 + `SolicitudDto` | 400 (datos inválidos), 409 (equipo no disponible) |
| PATCH | `/solicitudes/{id}/estado` | Cambiar estado (aprobar/rechazar/entregar/devolver/cancelar) | 200 + `SolicitudDto` | 404, 409 (transición inválida) |

## Autenticación (conceptual)

Cabecera `Authorization: Bearer <token>` en cada request. El token es
**conceptual únicamente** — no hay ningún secreto real en el código
(ver `RetrofitConfig.kt`, el interceptor agrega un valor de ejemplo
`"token-demo-no-real"` explícitamente marcado como no funcional).

## DTOs

```json
// EquipoDto
{ "id": 1, "nombre": "Multímetro digital", "categoria": "ELECTRONICA", "estado": "DISPONIBLE" }

// SolicitudDto
{ "id": 1, "equipoId": 1, "ambienteDestino": "Ambiente 302", "proposito": "...", "duracionHoras": 2, "estado": "SOLICITADA" }
```

## Manejo de errores

| Código | Significado | Cómo lo maneja la app |
|--------|--------------|--------------------------|
| 401 | Token inválido/ausente | `ResultadoRed.ErrorHttp(401, ...)`, la app sigue con datos locales |
| 404 | Recurso no existe | `ResultadoRed.ErrorHttp(404, ...)` |
| 409 | Conflicto (transición inválida, equipo no disponible) | `ResultadoRed.ErrorHttp(409, ...)` |
| 5xx | Error de servidor | `ResultadoRed.ErrorHttp(código, ...)` |
| Sin conexión / timeout | No hay red o el servidor no responde en 3s | `ResultadoRed.SinConexion` |

En todos los casos, la UI nunca se bloquea esperando: el timeout de
conexión y lectura está configurado en 3 segundos (`RetrofitConfig.kt`),
y cualquier fallo cae de vuelta a los datos ya cargados desde Room.
