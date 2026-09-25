# Cierre del proyecto — Sprint Review, Retrospective e informe final

Semana 9, actividad 39: cierre de todo el incremento (Semanas 5 a 9).

## Sprint Review final

Se demostró el recorrido completo: catálogo con filtro por categoría
persistente (DataStore), creación de solicitud con validaciones,
gestión completa del ciclo de vida (aprobar → entregar → devolver),
todo persistido en Room y sobreviviendo al cierre de la app,
sincronización local-first con el catálogo remoto (falla de forma
seguro sin backend real), evidencia fotográfica con Photo Picker y
sensor de luz ambiental, notificación de devolución con permiso
solicitado en el momento correcto, y el pipeline de CI corriendo en
un Pull Request real.

El Product Goal original ("mejorar la trazabilidad y la consulta de
préstamos... con evidencia de calidad verificable") se cumplió de
punta a punta: cada incremento quedó etiquetado (`v0.2.0` a `v0.6.0`),
documentado, y con pruebas reales — nunca resultados inventados.

## Retrospective final

**Qué funcionó bien:** dividir cada semana en su propia rama de
experimento antes de fusionar a `main`/`MAPU` evitó que un problema de
una semana (por ejemplo, la migración de AGP 9 o el conflicto de KSP)
pusiera en riesgo el trabajo ya entregado de semanas anteriores.

**Qué se puede mejorar:** varias semanas revelaron que el mayor
consumo de tiempo fue la configuración del entorno (SDK, versiones de
Gradle/AGP, JDK), no la lógica de negocio en sí — un patrón que ya se
había detectado desde la Semana 7 (ver `docs/SPRINT_SEMANA7.md`) y que
se mantuvo. Para un futuro proyecto, documentar el entorno exacto
(versión de JDK, Android Studio) antes de empezar a programar
ahorraría tiempo real.

**Acción concreta:** si este proyecto continúa, la siguiente prioridad
sería un backend real (aunque sea mínimo, tipo JSON Server o un
endpoint serverless), para que la sincronización que ya está
completamente implementada y probada (Semana 8) deje de fallar por
diseño y pase a `SINCRONIZADA` de verdad.

## Informe de calidad final

| Aspecto | Estado al cierre |
|---------|--------------------|
| Incrementos entregados | v0.1.0 (línea base) → v0.6.0 (Semana 9), todos etiquetados en Git |
| Historias de usuario | 12 HU (equipo) + gestión completa, accesibilidad, evidencia fotográfica y sensor adicional |
| Persistencia | Room (2 versiones de esquema, con migración real) + DataStore |
| Arquitectura | Reactiva de punta a punta (Flow, StateFlow, `collectAsStateWithLifecycle`) |
| Red | Retrofit/OkHttp completo, probado con MockWebServer, local-first sin depender de backend real |
| Pruebas automatizadas | Unitarias (reglas de negocio, ViewModel, seguridad), instrumentadas (persistencia, UI, regresión E2E) |
| CI/CD | GitHub Actions verde en Pull Request real (`build`, `test`, `lint`) |
| Seguridad | HTTPS forzado, sin secretos reales, mínimo privilegio en permisos |
| Defectos | BUG-03 y BUG-04, ambos reales, documentados, corregidos y confirmados |
| Documentación | README + 7 archivos en `docs/`, todos enlazados y actualizados |

**Recomendación final: ACEPTABLE.** El proyecto cumple el alcance
integrador completo de la guía (Semanas 5 a 9), con evidencia
verificable en cada paso y ningún resultado inventado.
