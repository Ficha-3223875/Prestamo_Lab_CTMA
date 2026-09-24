# Reporte no funcional — Revisión de seguridad (Semana 9)

Semana 9, actividad 37: prueba no funcional acotada. Se eligió
**seguridad** por encima de rendimiento (no hay servidor real que
medir) o accesibilidad (ya cubierta parcialmente en la sección 15 del
README desde la Semana 5).

## Alcance de la revisión

Revisión manual + prueba automatizada (`SeguridadRedTest.kt`) sobre:

1. **Tráfico de red cifrado.** `network_security_config.xml` fuerza
   `cleartextTrafficPermitted="false"`: la app no puede enviar tráfico
   HTTP sin cifrar a ningún host, solo HTTPS. Verificado también con
   `todasLasUrlsDeAmbienteUsanHttps()`, que confirma que las 3 URLs de
   ambiente (dev/stage/prod) empiezan por `https://`.
2. **Secretos en el código.** El interceptor de token usa un valor
   literal `"token-demo-no-real"`, explícitamente marcado como no
   funcional — no hay ninguna clave de API, contraseña ni token real
   en el repositorio. Verificado con
   `elTokenDeAutorizacionEsExplicitamenteDemoNoReal()`.
3. **Mínimo privilegio en permisos.** Revisión manual de
   `AndroidManifest.xml`: solo se declaran `INTERNET` y
   `POST_NOTIFICATIONS` — ningún permiso de ubicación, cámara,
   almacenamiento ni contactos, porque ninguno se usa (Photo Picker no
   los requiere; el sensor de luz tampoco).
4. **Solicitud de permisos en el momento correcto.** `POST_NOTIFICATIONS`
   se pide justo antes de registrar una devolución
   (`SolicitudDetalleScreen.kt`), no al abrir la app.
5. **Datos sensibles en logs.** Revisión manual: ningún `Log.d`/`println`
   del proyecto imprime el propósito, destino o datos personales de
   una solicitud completa; los mensajes de error son genéricos.

## Hallazgos

Ninguno crítico. Como mejora pendiente para un futuro incremento: si
alguna vez existe un backend real, el token debería guardarse cifrado
(Android Keystore / EncryptedSharedPreferences) en vez de quedar como
constante en el código, incluso siendo un valor de prueba — es el
patrón correcto a seguir desde ya.

## Herramientas

Se usó revisión manual guiada por la lista de chequeo de la guía (OWASP
Mobile Top 10 como referencia conceptual) en vez de OWASP ZAP, porque
ZAP necesita un endpoint HTTP accesible real para escanear, y
PréstamoLab no tiene ninguno desplegado.
