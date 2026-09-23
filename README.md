# PréstamoLab CTMA

Prototipo educativo Android para consultar un catálogo simulado de equipos y herramientas de formación, registrar solicitudes de préstamo y hacer seguimiento a su estado. Desarrollado como caso integrador de Scrum, desarrollo móvil Android y pruebas de software (programa ADSO, CTMA).

No reemplaza ningún sistema institucional real, no maneja inventario oficial y todos los datos del catálogo son sintéticos: no debe usarse con información personal real.

## Documentación ampliada

La matriz de riesgos, el plan de pruebas completo, la matriz de trazabilidad y el refinamiento/estimación de la Semana 7 viven en la carpeta `docs/` (se movieron ahí en la Semana 5 para seguir la ubicación recomendada de la guía, sección 9):

- [`docs/RIESGOS.md`](docs/RIESGOS.md)
- [`docs/PLAN_PRUEBAS.md`](docs/PLAN_PRUEBAS.md)
- [`docs/MATRIZ_TRAZABILIDAD.md`](docs/MATRIZ_TRAZABILIDAD.md)
- [`docs/SPRINT_SEMANA7.md`](docs/SPRINT_SEMANA7.md)

## Contenido

1. [Descubrimiento y Product Goal](#1-descubrimiento-y-product-goal)
2. [Historias de usuario y criterios de aceptación](#2-historias-de-usuario-y-criterios-de-aceptación)
3. [Matriz de riesgos y priorización de pruebas](#3-matriz-de-riesgos-y-priorización-de-pruebas)
4. [Sprint Planning](#4-sprint-planning)
5. [Diseño: modelo de dominio y arquitectura](#5-diseño-modelo-de-dominio-y-arquitectura)
6. [Desarrollo del incremento Android](#6-desarrollo-del-incremento-android)
7. [Plan y diseño de pruebas](#7-plan-y-diseño-de-pruebas)
8. [Ejecución, defectos y regresión](#8-ejecución-defectos-y-regresión)
9. [Sprint Review y Retrospective](#9-sprint-review-y-retrospective)
10. [Herramientas de GitHub para el proyecto](#10-herramientas-de-github-para-el-proyecto)
11. [Producto final y paquete de evidencias](#11-producto-final-y-paquete-de-evidencias)
12. [Informe ejecutivo de calidad](#12-informe-ejecutivo-de-calidad)
13. [Instalación y ejecución](#13-instalación-y-ejecución)
14. [Limitaciones conocidas](#14-limitaciones-conocidas)
15. [Gestión completa del ciclo de vida (HU-07) y accesibilidad (HU-10)](#15-gestión-completa-del-ciclo-de-vida-hu-07-y-accesibilidad-hu-10)
16. [Uso responsable de inteligencia artificial](#16-uso-responsable-de-inteligencia-artificial)
17. [Semanas 5 a 7: persistencia, Scrum formal y arquitectura reactiva](#17-semanas-5-a-7-persistencia-scrum-formal-y-arquitectura-reactiva)

---

## 1. Descubrimiento y Product Goal

PréstamoLab CTMA nace de una necesidad real de los ambientes de formación: hay kits de electrónica, multímetros, tabletas, cámaras y herramientas manuales que varios aprendices necesitan usar en distintos momentos, y hoy esa disponibilidad se controla de forma manual. Es fácil perder de vista qué está prestado, qué está libre y quién tiene pendiente una devolución.

El prototipo que construimos simula ese flujo: un aprendiz o instructor consulta el catálogo, solicita un equipo disponible y hace seguimiento a su solicitud.

**Usuarios del prototipo**

- **Solicitante demo**: aprendiz o instructor que consulta el catálogo y registra una solicitud.
- **Gestor simulado**: rol conceptual para representar cambios de estado en las pruebas; en este incremento no requiere autenticación.
- **Instructor**: valida la evidencia y facilita los datos del laboratorio.

> **Product Goal**: Mejorar la trazabilidad y la consulta de préstamos de recursos de formación mediante una experiencia móvil simple, confiable y con evidencia de calidad verificable.

Este Product Goal describe el resultado que buscamos para el usuario, no la tecnología. Kotlin, Jetpack Compose y el patrón ViewModel/Repository son el cómo; el objetivo es que cualquiera pueda saber, sin preguntarle a nadie, si un equipo está disponible y en qué va su solicitud.

## 2. Historias de usuario y criterios de aceptación

| ID | Historia | Criterio de aceptación |
|----|----------|--------------------------|
| HU-01 | Como solicitante, quiero ver el catálogo de equipos con su disponibilidad, para saber qué puedo pedir. | Dado el catálogo, cuando entro a la app, entonces veo cada equipo con nombre, categoría y estado (Disponible/Reservado/Prestado). |
| HU-02 | Como solicitante, quiero abrir el detalle de un equipo, para conocerlo antes de solicitarlo. | Dado un equipoId válido, cuando toco un equipo del catálogo, entonces veo su detalle completo. |
| HU-03 | Como solicitante, quiero registrar una solicitud de préstamo, para usar el equipo en una práctica. | Dado un equipo DISPONIBLE y datos válidos, cuando pulso Guardar, entonces se crea una sola solicitud SOLICITADA y el equipo pasa a RESERVADO. |
| HU-04 | Como solicitante, quiero que la app valide destino, propósito y duración, para no enviar datos incompletos. | Dado un campo inválido (vacío, fuera de rango), cuando intento guardar, entonces la app no guarda y muestra un mensaje específico del campo. |
| HU-05 | Como solicitante, quiero ver "Mis solicitudes", para saber en qué estado están. | Dado que tengo solicitudes registradas, cuando entro a Mis solicitudes, entonces veo cada una con su estado actual. |
| HU-06 | Como solicitante, quiero ver el detalle de una solicitud, para revisar los datos que registré. | Dado un solicitudId válido, cuando la abro desde la lista, entonces veo equipo, destino, propósito, duración y estado. |
| HU-07 | Como solicitante, quiero cancelar una solicitud SOLICITADA, para liberar el equipo si ya no lo necesito. | Dada una solicitud en estado SOLICITADA, cuando pulso Cancelar, entonces pasa a CANCELADA y el equipo vuelve a DISPONIBLE. |
| HU-08 | Como solicitante, quiero que un ID inexistente no cierre la app, para no perder mi sesión de trabajo. | Dado un equipoId o solicitudId que no existe, cuando la app intenta abrir ese detalle, entonces muestra un mensaje recuperable en vez de cerrarse. |

## 3. Matriz de riesgos y priorización de pruebas

> Versión ampliada y actualizada en [`docs/RIESGOS.md`](docs/RIESGOS.md) (Semana 5). Se conserva aquí el contenido original de referencia.

El riesgo es lo que ayuda a decidir qué probar primero. La matriz está ordenada por nivel (probabilidad × impacto) y conectada con una estrategia de cobertura concreta.

| ID | Riesgo | Prob. | Impacto | Nivel | Cobertura |
|----|--------|-------|---------|-------|-----------|
| R-01 | Dos solicitudes activas reservan el mismo equipo. | Alta | Alta | Crítico | TC de disponibilidad + duplicación (TC-12, TC-13) |
| R-02 | Datos fuera de rango son aceptados (propósito o duración). | Alta | Media | Alto | Partición de equivalencia + valores límite (TC-04 a TC-11) |
| R-03 | Un ID inexistente provoca cierre abrupto de la app. | Media | Alta | Alto | Navegación negativa (TC-03) |
| R-04 | El catálogo no refleja el cambio de estado tras crear o cancelar. | Media | Alta | Alto | Flujo completo + regresión (TC-14, TC-15) |
| R-05 | Con fuente aumentada 1.5×, botones o textos esenciales desaparecen. | Media | Media | Medio | Accesibilidad básica (TC-18) |
| R-06 | Se permite cancelar una solicitud que ya no está en SOLICITADA. | Baja | Alta | Medio | Transición inválida (TC-16) |

## 4. Sprint Planning

> **Sprint Goal**: Permitir que un solicitante consulte un equipo disponible y registre una solicitud de préstamo válida, manteniendo la disponibilidad coherente y demostrando su calidad mediante pruebas reproducibles.

**Sprint Backlog (PBIs seleccionados)**

| ID | Elemento | Prioridad | Riesgo |
|----|----------|-----------|--------|
| PB-01 | Consultar catálogo de equipos y disponibilidad. | Alta | Alto |
| PB-02 | Consultar detalle de un equipo. | Alta | Medio |
| PB-03 | Registrar solicitud de préstamo. | Alta | Alto |
| PB-04 | Validar propósito, destino y duración. | Alta | Alto |
| PB-05 | Evitar solicitud sobre equipo no disponible. | Alta | Alto |
| PB-06 | Evitar duplicación por doble pulsación. | Alta | Alto |
| PB-07 | Consultar Mis solicitudes. | Media | Medio |
| PB-08 | Consultar detalle de solicitud. | Media | Medio |
| PB-09 | Cancelar solicitud SOLICITADA. | Media | Medio |
| PB-10 | Manejar IDs inexistentes. | Media | Medio |

**Definition of Done acordada**

- El proyecto compila y se ejecuta en el emulador definido.
- Los criterios de aceptación seleccionados están implementados.
- La UI no modifica directamente la fuente de datos del Repository.
- El ViewModel expone UiState/StateFlow de solo lectura.
- La navegación transporta identificadores y controla IDs inexistentes.
- Se ejecutaron los casos acordados y los resultados son reales, no inventados.
- Los defectos altos tienen decisión explícita registrada.
- Las correcciones relevantes tienen confirmación y regresión.
- Git y README están actualizados.
- El incremento puede demostrarse y explicarse por cualquier integrante.
- **(Semana 6)** Los datos persisten en Room tras cerrar y reabrir la app.
- **(Semana 6/7)** Existen pruebas reproducibles de persistencia (Room/DAO) y de comportamiento asíncrono (Flow, cancelación de corrutinas).

## 5. Diseño: modelo de dominio y arquitectura

Se modelaron dos entidades principales, `Equipo` y `SolicitudPrestamo`, y dos enumeraciones de estado. El modelo se mantuvo simple a propósito: para el primer incremento solo se implementaron completamente los estados que se usan en el flujo básico (SOLICITADA y CANCELADA); a partir de la Semana 5, APROBADA, ENTREGADA y DEVUELTA también quedaron completamente implementadas (ver sección 15).

| Elemento | Campos / valores |
|----------|-------------------|
| `Equipo` | id, nombre, categoria (`CategoriaEquipo`), estado (`DISPONIBLE` / `RESERVADO` / `PRESTADO`) |
| `SolicitudPrestamo` | id, equipoId, ambienteDestino, proposito, duracionHoras, estado (`SOLICITADA` / `APROBADA` / `ENTREGADA` / `DEVUELTA` / `CANCELADA` / `RECHAZADA`) |

**Arquitectura y flujo unidireccional (UDF) — actualizada en Semanas 6 y 7**

```
UI (Compose) --eventos--> ViewModel --> Repository
UI (Compose) <--UiState (StateFlow)-- ViewModel
Repository --Flow--> Room (SQLite local, fuente de verdad)
Repository --Flow--> DataStore (preferencia de filtro de categoría)
```

Desde la Semana 6, Room reemplazó al Repository en memoria como fuente real de datos (`InMemoryPrestamoRepository` se conserva solo para pruebas unitarias, que no necesitan Android/SQLite real). Desde la Semana 7, las lecturas son reactivas: el Repository expone `Flow` en vez de listas de una sola vez, y el ViewModel las observa con `combine()` en lugar de recargarlas manualmente después de cada acción. DataStore persiste el filtro de categoría del catálogo entre sesiones.

| Capa | Responsabilidad | No hace |
|------|------------------|---------|
| UI Compose | Renderizar UiState y emitir eventos/callbacks. | Acceder directamente a Room o DataStore. |
| ViewModel | Observar los Flow del Repository y coordinar acciones de pantalla. | Guardar Activity, Context o NavController. |
| Repository | Definir operaciones del dominio y ser la fuente de verdad. | Decidir mensajes o comportamiento visual. |
| Room (`AppDatabase`, DAOs) | Persistencia local real (Semana 6): catálogo y solicitudes sobreviven a cerrar la app. | Conocer nada de la UI ni del ViewModel. |
| DataStore (`FiltrosPreferences`) | Persistir la preferencia de filtro de categoría (Semana 6). | Guardar datos del dominio (eso es responsabilidad de Room). |
| `InMemoryPrestamoRepository` | Línea base heredada, usada solo en pruebas unitarias JVM. | Ser la fuente de datos real de la app (ese rol lo tiene Room). |
| Navigation | Conectar destinos y pasar IDs (equipoId, solicitudId). | Pasar entidades completas. |

Esta separación es la que permite probar las reglas de negocio (propósito, duración, disponibilidad, transiciones de estado) con pruebas unitarias, y la persistencia con pruebas instrumentadas, sin mezclar ambas responsabilidades.

**Navegación implementada**

```
Catalogo -> EquipoDetalle/{equipoId} -> Solicitar/{equipoId}
MisSolicitudes -> SolicitudDetalle/{solicitudId}
```

Un identificador inexistente no cierra la app: la pantalla de destino recibe `null` y muestra un mensaje recuperable.

**Reglas de negocio implementadas**

| ID | Regla | Dónde se aplica |
|----|-------|------------------|
| RN-01 | Solo un equipo DISPONIBLE puede solicitarse | `Validaciones.kt`, `RoomPrestamoRepository` |
| RN-02 | Ambiente/destino obligatorio | `Validaciones.kt` |
| RN-03 | Propósito entre 10 y 180 caracteres | `Validaciones.kt` |
| RN-04 | Duración entre 1 y 8 horas | `Validaciones.kt` |
| RN-05 | Una acción de Guardado crea una sola solicitud | flag `guardando` en `PrestamoViewModel` + transacción en el Repository |
| RN-06 | Una solicitud activa reserva el equipo | `RoomPrestamoRepository.crearSolicitud` |
| RN-07 | Solo SOLICITADA puede cancelarse | `Validaciones.kt`, `RoomPrestamoRepository.cancelarSolicitud` |
| RN-08 | ID inexistente produce estado recuperable | `PrestamoNavGraph` + pantallas de detalle |
| RN-09 | Datos sintéticos | catálogo semilla en `RoomPrestamoRepository.sembrarCatalogoSiEstaVacio` |
| RN-10 | Solo SOLICITADA puede aprobarse o rechazarse | `Validaciones.kt`, `RoomPrestamoRepository.aprobarSolicitud` / `rechazarSolicitud` |
| RN-11 | Solo APROBADA puede marcarse ENTREGADA (equipo pasa a PRESTADO) | `Validaciones.kt`, `RoomPrestamoRepository.entregarSolicitud` |
| RN-12 | Solo ENTREGADA puede marcarse DEVUELTA (equipo vuelve a DISPONIBLE) | `Validaciones.kt`, `RoomPrestamoRepository.devolverSolicitud` |

## 6. Desarrollo del incremento Android

El incremento se construyó en Kotlin + Jetpack Compose, con Material 3, ViewModel, StateFlow/Flow y Navigation Compose:

- `model/`: `Equipo`, `SolicitudPrestamo`, `Estados` y `Validaciones` (reglas de negocio desacopladas de la UI).
- `model/mapper/`: traducción entre el modelo de dominio y las entidades de Room (Semana 6).
- `data/local/`: `AppDatabase`, DAOs (`EquipoDao`, `SolicitudDao`) y entidades Room — persistencia local real (Semana 6).
- `data/preferences/`: `FiltrosPreferences` (DataStore) y su interfaz `PreferenciasFiltro`, para poder sustituirla en pruebas (Semana 7).
- `data/repository/`: contrato `PrestamoRepository` (Semana 7: expone `Flow` para lecturas), `RoomPrestamoRepository` (fuente real) e `InMemoryPrestamoRepository` (línea base heredada, usada en pruebas unitarias).
- `viewmodel/`: `PrestamoViewModel` + `PrestamoUiState` (con `CargaEstado`: Cargando/Contenido/Vacío/Error, Semana 7) + fábrica de ViewModel.
- `ui/`: pantallas de Catálogo (con filtro por categoría), Detalle de equipo, Solicitar, Mis solicitudes y Detalle de solicitud.
- `navigation/`: `PrestamoNavGraph` con las rutas por ID, usando `collectAsStateWithLifecycle` (Semana 7).
- Pruebas unitarias JUnit (`app/src/test`) sobre reglas de negocio y sobre el ViewModel (cancelación de corrutinas, con Turbine).
- Pruebas instrumentadas (`app/src/androidTest`) sobre persistencia real (Room) y sobre las pantallas Compose.

La regla de doble pulsación (RN-05) se protege en dos capas: el ViewModel ignora una segunda solicitud mientras `guardando` es verdadero, y el Repository sincroniza la creación de la solicitud para que dos hilos no puedan reservar el mismo equipo al mismo tiempo. Esta decisión fue justamente la causa del defecto BUG-03 descrito en la sección 8.

## 7. Plan y diseño de pruebas

> Versión ampliada y actualizada en [`docs/PLAN_PRUEBAS.md`](docs/PLAN_PRUEBAS.md) (Semana 5), que incluye los casos TC-19 a TC-21 sobre las transiciones de gestión. Se conserva aquí el contenido original de referencia.

**7.1 Contenido del plan**

| Campo | Definición |
|-------|------------|
| Objetivo | Verificar que el incremento cumple las reglas RN-01 a RN-09 y las historias HU-01 a HU-08. |
| Versión / build | 0.1.0 |
| Alcance incluido | Catálogo, detalle de equipo, solicitud, validaciones, Mis solicitudes, cancelación, navegación por ID. |
| Exclusiones | Transiciones APROBADA/ENTREGADA/DEVUELTA, autenticación real, persistencia. |
| Ambiente | Emulador Android API 24+, Android Studio. |
| Datos | Catálogo sintético de 6 equipos (ver `InMemoryPrestamoRepository`). |
| Criterios de entrada | Build compila; catálogo semilla cargado. |
| Criterios de salida | Sin defectos altos abiertos sin decisión; casos críticos en PASS. |
| Convención | PASS / FAIL / BLOCKED. |

**7.2 Suite de casos (18 casos originales; ver docs/PLAN_PRUEBAS.md para los 21 actuales)**

| ID | Escenario | Resultado esperado | Técnica |
|----|-----------|---------------------|---------|
| TC-01 | Catálogo con datos | Equipos visibles con su disponibilidad. | Caso de uso |
| TC-02 | equipoId válido | El detalle corresponde al equipo seleccionado. | Caso de uso |
| TC-03 | equipoId inexistente | Estado recuperable, sin cierre abrupto. | Negativa |
| TC-04 | Propósito de 9 caracteres | No guarda; mensaje específico. | Límite |
| TC-05 | Propósito de 10 caracteres | Guarda si el resto de datos es válido. | Límite |
| TC-06 | Propósito de 180 caracteres | Guarda. | Límite |
| TC-07 | Propósito de 181 caracteres | No guarda. | Límite |
| TC-08 | Duración de 0 horas | No guarda. | Límite |
| TC-09 | Duración de 1 hora | Válida. | Límite |
| TC-10 | Duración de 8 horas | Válida. | Límite |
| TC-11 | Duración de 9 horas | No guarda. | Límite |
| TC-12 | Equipo no disponible | Solicitud rechazada. | Decisión |
| TC-13 | Doble pulsación en Guardar | Se crea una sola solicitud. | Riesgo |
| TC-14 | Crear solicitud válida | SOLICITADA + equipo RESERVADO. | Caso de uso |
| TC-15 | Cancelar SOLICITADA | CANCELADA y equipo DISPONIBLE. | Transición |
| TC-16 | Cancelar una CANCELADA | Acción no disponible / sin cambio. | Transición |
| TC-17 | Volver desde detalle/formulario | Back stack correcto. | Navegación |
| TC-18 | Fuente 1.5× y texto largo | Contenido y acciones esenciales siguen usables. | Accesibilidad |

**7.3 Trazabilidad**

> Versión ampliada en [`docs/MATRIZ_TRAZABILIDAD.md`](docs/MATRIZ_TRAZABILIDAD.md) (Semana 5), con columna de PR.

| Historia | Criterio | Riesgo | Caso | Ejecución | Defecto |
|----------|----------|--------|------|-----------|---------|
| HU-03 | Una sola solicitud (RN-05) | R-01 | TC-13 | FAIL → PASS tras fix | BUG-03 |
| HU-03 | Solo equipo disponible (RN-01) | R-01 | TC-12 | PASS | — |
| HU-04 | Límites de propósito (RN-03) | R-02 | TC-04 a TC-07 | PASS | — |
| HU-04 | Límites de duración (RN-04) | R-02 | TC-08 a TC-11 | PASS | — |
| HU-07 | Solo SOLICITADA se cancela (RN-07) | R-06 | TC-15, TC-16 | PASS | — |
| HU-08 | ID inexistente no cierra la app (RN-08) | R-03 | TC-03 | PASS | — |

## 8. Ejecución, defectos y regresión

**8.1 Bitácora de ejecución (extracto)**

| Ejecución | Caso | Build | Resultado | Observación |
|-----------|------|-------|-----------|-------------|
| EX-001 | TC-13 | 0.1.0 | FAIL | Doble pulsación creó dos solicitudes → BUG-03 |
| EX-002 | TC-12 | 0.1.0 | PASS | Bloqueo correcto sobre equipo RESERVADO |
| EX-003 | TC-16 | 0.1.0 | BLOCKED → PASS | Faltaba una solicitud CANCELADA de prueba; se preparó el dato y se re-ejecutó |
| EX-004 | TC-03 | 0.1.0 | PASS | equipoId inexistente mostró mensaje recuperable |
| EX-005 | TC-18 | 0.1.0 | PASS | Con fuente 1.5× el botón Guardar sigue visible y usable |

**8.2 Reporte de defecto — BUG-03**

| Campo | Detalle |
|-------|---------|
| Título | La doble pulsación en Guardar crea dos solicitudes para el mismo equipo. |
| Build | 0.1.0 |
| Precondición | Equipo DISPONIBLE y formulario con datos válidos. |
| Pasos | Abrir equipo → Solicitar → diligenciar formulario → pulsar Guardar dos veces rápidamente. |
| Esperado | Una sola solicitud en estado SOLICITADA; el equipo queda RESERVADO. |
| Obtenido | Se crearon dos solicitudes activas para el mismo equipo. |
| Severidad | Alta: rompe una regla central de disponibilidad (RN-01/RN-06). |
| Prioridad | Alta: afecta directamente el Sprint Goal y la Definition of Done. |
| Evidencia | Registro de ejecución EX-001, ligado a TC-13. |

**8.3 Corrección, confirmación y regresión**

BUG-03 se corrigió en dos capas: el ViewModel deja de aceptar una nueva solicitud mientras `guardando` es verdadero, y el Repository sincroniza la creación de la solicitud para evitar una condición de carrera. Se repitió TC-13 exactamente para confirmar que la falla puntual desapareció, y luego se ejecutó una regresión: guardar una solicitud una sola vez, crear otra sobre un equipo distinto, volver al catálogo, abrir el detalle y cancelar una solicitud. Los cinco resultados fueron PASS.

**Defecto adicional encontrado en pruebas manuales post-entrega**: el ícono de acceso rápido a "Mis solicitudes" en la barra superior del catálogo no tenía ninguna acción conectada (`Icon` sin `onClick`). Se corrigió envolviéndolo en un `IconButton(onClick = onVerMisSolicitudes)`. Ver commit `fix: conectar botón Ver mis solicitudes y remover ícono de app inexistente`. Registrado formalmente como Issue BUG-04 en GitHub (Semana 5).

## 9. Sprint Review y Retrospective

**Sprint Review**: se demostró el flujo completo — catálogo con disponibilidad real, solicitud válida sobre un equipo DISPONIBLE, rechazo de una solicitud sobre un equipo no disponible, consulta de Mis solicitudes, cancelación de una solicitud SOLICITADA y los resultados de la suite de pruebas, incluyendo BUG-03 ya corregido. El Sprint Goal se alcanzó: se puede consultar un equipo disponible y registrar una solicitud válida, con disponibilidad coherente y evidencia real de calidad. Quedó pendiente para el Product Backlog habilitar las transiciones APROBADA/ENTREGADA/DEVUELTA — se resolvió en un incremento posterior (ver sección 15).

**Sprint Retrospective**

> **Mejora concreta para el siguiente Sprint**: preparar los datos y estados de prueba (por ejemplo, una solicitud ya CANCELADA) durante el Sprint Planning, no durante la ejecución. Esto habría evitado el bloqueo de TC-16 (EX-003). Acción comprobable: en el próximo Sprint, ningún caso debería quedar BLOCKED por falta de datos preparados.

## 10. Herramientas de GitHub para el proyecto

GitHub no tiene una función que lea una historia de usuario y determine sola si quedó implementada de punta a punta, pero sí ofrece un conjunto de herramientas que, bien organizadas, permiten verificar toda la cadena: historia → criterios → código → vista Compose → ViewModel → Repository → pruebas → Pull Request → ejecución CI → defecto → corrección.

| Evaluar | Herramienta GitHub | Qué permite verificar |
|---------|---------------------|--------------------------|
| Historias de usuario | Issues | HU, criterios de aceptación, prioridad, responsables |
| Sprint / Product Backlog | GitHub Projects | Kanban, backlog, estado e iteraciones |
| Relación HU ↔ implementación | Issues + Pull Requests | Qué cambio de código implementó cada historia |
| Código desarrollado | Pull Requests → Files changed | Exactamente qué archivos y líneas cambiaron |
| Autoría | Commits + PR + Contributors | Participación y evolución del trabajo |
| Compilación | GitHub Actions | Ejecutar Gradle automáticamente |
| Pruebas unitarias | GitHub Actions | Ejecutar `testDebugUnitTest` |
| Calidad Android | GitHub Actions + Android Lint | Ejecutar `lintDebug` |
| APK | Actions Artifacts | Generar y conservar el APK de cada ejecución |
| Dependencias | Dependabot | Vulnerabilidades en librerías de Gradle |

Con base en esa tabla se implementó el workflow `.github/workflows/android-ci.yml`: en cada push o Pull Request compila el proyecto (`assembleDebug`), corre las pruebas unitarias (`testDebugUnitTest`), ejecuta Android Lint y conserva el APK y el reporte de Lint como artefactos descargables.

Se usa un repositorio privado de GitHub Free: cubre issues, Pull Requests, revisión de código, Projects y Actions con cuota, que es todo lo que necesita este incremento; quedan por fuera Branch Protection obligatoria y Required Status Checks, que en Free solo están disponibles en repos públicos, así que la disciplina de no fusionar sin revisión se mantiene manualmente como acuerdo de equipo.

## 11. Producto final y paquete de evidencias

- Repositorio Git: `prestamolab-ctma-android` (código Android completo, `.github/workflows`, `docs/` y este README).
- Aplicación Android ejecutable, con persistencia real (Room) y arquitectura reactiva (Flow).
- Este README, con propósito, instalación, arquitectura, navegación, reglas de negocio, pruebas y limitaciones.
- Product Goal, Product Backlog, Sprint Goal, Sprint Backlog y Definition of Done (secciones 1 y 4).
- Matriz de riesgos y matriz de trazabilidad (`docs/RIESGOS.md`, `docs/MATRIZ_TRAZABILIDAD.md`).
- Suite de pruebas ampliada y datos sintéticos (`docs/PLAN_PRUEBAS.md`).
- Bitácora con PASS/FAIL/BLOCKED y evidencia (sección 8.1).
- Registro de los defectos reales encontrados, BUG-03 y BUG-04, como Issues de GitHub cerrados con evidencia.
- Confirmación y regresión tras las correcciones (sección 8.3).
- Sprint Review y acción de Retrospective (sección 9); refinamiento y estimación individual de Semana 7 (`docs/SPRINT_SEMANA7.md`).
- Informe ejecutivo de calidad (sección 12).
- Incrementos etiquetados: `v0.1.0` (línea base) → `v0.2.0` (Semana 5) → `v0.3.0` (Semana 6, Room) → `v0.4.0` (Semana 7, Flow reactivo).

## 12. Informe ejecutivo de calidad

| Sección | Respuesta |
|---------|-----------|
| Alcance | Se construyó y probó el catálogo, el detalle de equipo, el registro y gestión completa de solicitudes (incluyendo aprobar/rechazar/entregar/devolver) y la navegación por ID, con persistencia real (Room) y arquitectura reactiva (Flow). |
| Ejecución | 18 casos originales + 3 casos de transición (TC-19 a TC-21) + 5 pruebas de persistencia + 2 pruebas de cancelación de corrutinas + 3 pruebas de UI = 31 verificaciones automatizadas o manuales documentadas. |
| Defectos | BUG-03 (doble pulsación) y BUG-04 (ícono sin conectar) fueron los defectos reales encontrados; ambos corregidos, confirmados y registrados como Issues cerrados en GitHub. |
| Riesgo residual | No hay autenticación real de usuarios ni control de roles (cualquiera puede aprobar/entregar/devolver). No se hicieron pruebas formales con TalkBack. |
| Limitaciones | Ver sección 14 (actualizada: la persistencia ya no es una limitación desde la Semana 6). |
| Definition of Done | 11 de 11 criterios cumplidos a la fecha de la Semana 7 (ver sección 4). |
| Recomendación | **ACEPTABLE**. El Sprint Goal original se cumplió y el incremento evolucionó con persistencia real y arquitectura reactiva, manteniendo evidencia de calidad verificable en cada paso. |

## 13. Instalación y ejecución

1. Abrir la carpeta del proyecto en Android Studio (Koala o superior).
2. Dejar que Gradle sincronice las dependencias (requiere conexión a internet la primera vez).
3. Ejecutar la configuración `app` sobre un emulador o dispositivo con Android 7.0 (API 24) o superior.

> **Nota sobre la versión de Java (JDK):** este proyecto usa Gradle 9.1 / AGP 9.0.1, que requieren JDK 17 o superior. Si Android Studio muestra el mensaje *"Please Select Gradle JVM to Import Project"*, selecciona una versión de JDK entre 17 y 21 (Android Studio trae una empaquetada, no hace falta instalar nada aparte).

También puede compilarse por línea de comandos:

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew connectedDebugAndroidTest
./gradlew lintDebug
```

## 14. Limitaciones conocidas

- No hay autenticación real de usuarios ni control de roles: cualquier persona que use la app puede aprobar, rechazar, entregar o devolver una solicitud (no está diferenciado un rol "gestor" de un rol "solicitante" en la UI).
- Las pruebas instrumentadas de UI (`app/src/androidTest`) cubren escenarios representativos (catálogo, persistencia con Room); no son todavía una suite instrumentada 1:1 con los 21 casos manuales documentados en `docs/PLAN_PRUEBAS.md`.
- No se hicieron pruebas formales con TalkBack activado (ver sección 15, "pendiente honesto").
- No hay sincronización remota (API/servidor): todo el dato vive en el dispositivo (Room local). Esto se aborda en un incremento posterior (Semana 8 de la guía).

## 15. Gestión completa del ciclo de vida (HU-07) y accesibilidad (HU-10)

Estas dos secciones se agregaron después de una revisión de issues del equipo, para cerrar brechas frente al alcance inicial del incremento.

**Gestión completa de solicitudes.** Además de cancelar, la pantalla de detalle de una solicitud ahora permite, según el estado actual:

- `SOLICITADA` → **Aprobar** (pasa a `APROBADA`), **Rechazar** (pasa a `RECHAZADA` y libera el equipo) o **Cancelar**.
- `APROBADA` → **Marcar como entregada** (pasa a `ENTREGADA`, el equipo pasa a `PRESTADO`).
- `ENTREGADA` → **Registrar devolución** (pasa a `DEVUELTA`, el equipo vuelve a `DISPONIBLE`).
- `DEVUELTA`, `CANCELADA`, `RECHAZADA` son estados finales: la pantalla lo indica y no ofrece más acciones.

Las reglas RN-10 a RN-12 (`Validaciones.kt`) controlan qué transición es válida desde qué estado, con sus pruebas unitarias correspondientes en `ValidacionesTest.kt`.

**Accesibilidad y manejo de errores.**

- Las tarjetas de equipo y de solicitud tienen `Modifier.semantics { contentDescription = ... }` con una frase completa (nombre + categoría + estado, o número + estado + destino), para que un lector de pantalla anuncie el contexto completo en vez de leer cada `Text` suelto.
- La tipografía usa `sp` (no `dp`), por lo que respeta el escalado de fuente del sistema operativo.
- La disponibilidad de un equipo nunca se comunica solo con color: siempre va acompañada de texto ("Disponible", "Reservado", "Prestado").
- Un `equipoId` o `solicitudId` inexistente no cierra la app (RN-08): la pantalla de destino muestra un mensaje recuperable.
- Las acciones de gestión (aprobar, rechazar, entregar, devolver, cancelar, crear) están envueltas en `try/catch` en el ViewModel, distinguiendo explícitamente `CancellationException` (que se re-lanza, no se trata como error) de fallos reales (Semana 7).

**Pendiente honesto**: no se hicieron pruebas formales con TalkBack activado, y no todos los botones tienen `contentDescription` explícito más allá del texto visible que ya traen por defecto (Material 3 usa el texto del `Button` como etiqueta accesible automáticamente, así que técnicamente ya son anunciables, pero no se verificó manualmente con el lector de pantalla).

## 16. Uso responsable de inteligencia artificial

Se utilizó IA como apoyo para redactar y revisar código base y documentación del proyecto, siguiendo la guía "Uso responsable de inteligencia artificial" del curso. Toda sugerencia fue comprendida, adaptada y verificada antes de incorporarse; ningún resultado de prueba fue inventado. La estimación de Semana 7 se documenta honestamente como un ejercicio individual retrospectivo (ver `docs/SPRINT_SEMANA7.md`), no como una sesión de equipo simulada.

## 17. Semanas 5 a 7: persistencia, Scrum formal y arquitectura reactiva

Resumen de la evolución del incremento heredado, siguiendo la guía integradora de Semanas 5 a 9. No se creó un segundo proyecto ni se recrearon las HU/matrices desde cero: se auditó, refinó y amplió lo que ya existía.

**Semana 5 — Auditoría y corte de calidad (`v0.2.0`)**
- Arquitectura heredada auditada: cumplía ya la separación Compose/ViewModel/Repository/Navigation exigida.
- Documentación reorganizada en `docs/` (riesgos, plan de pruebas, trazabilidad).
- Defectos reales (BUG-03, BUG-04) registrados formalmente como Issues de GitHub, comentados y cerrados con evidencia.
- Incorporados al GitHub Project individual del integrante.

**Semana 6 — Persistencia local y Scrum formal (`v0.3.0`)**
- Room como fuente local canónica: `AppDatabase`, `EquipoDao`, `SolicitudDao`, con relación por llave foránea entre solicitud y equipo.
- DataStore para la preferencia de filtro de categoría.
- `InMemoryPrestamoRepository` conservado para pruebas unitarias (no se descarta la línea base).
- 5 pruebas instrumentadas nuevas sobre CRUD y persistencia real.

**Semana 7 — Arquitectura reactiva y refinamiento (`v0.4.0`)**
- Las consultas de Room pasaron de `suspend` a `Flow`: el ViewModel ya no recarga manualmente después de cada acción, observa y reacciona solo.
- `UiState` formalizado con `CargaEstado` (Cargando/Contenido/Vacío/Error).
- `collectAsStateWithLifecycle` en la UI, consciente del ciclo de vida.
- Manejo explícito de cancelación de corrutinas (`CancellationException` re-lanzada, no tratada como error), con pruebas dedicadas usando Turbine.
- Refinamiento INVEST del backlog y estimación individual retrospectiva, documentados honestamente en `docs/SPRINT_SEMANA7.md` — incluye análisis real de cuello de botella (el entorno de desarrollo, no el código de negocio, fue el mayor consumo de tiempo).

**Pendiente para Semana 8 (siguiente incremento)**: integración con API REST vía Retrofit, MockWebServer y TDD acotado.
