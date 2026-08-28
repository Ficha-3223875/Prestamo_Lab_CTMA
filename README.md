# PréstamoLab CTMA

Prototipo educativo Android para consultar un catálogo simulado de equipos y herramientas de formación, registrar solicitudes de préstamo y hacer seguimiento a su estado. Desarrollado como caso integrador de Scrum, desarrollo móvil Android y pruebas de software (programa ADSO, CTMA).

No reemplaza ningún sistema institucional real, no maneja inventario oficial y todos los datos del catálogo son sintéticos: no debe usarse con información personal real.

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
15. [Uso responsable de inteligencia artificial](#15-uso-responsable-de-inteligencia-artificial)

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

## 5. Diseño: modelo de dominio y arquitectura

Se modelaron dos entidades principales, `Equipo` y `SolicitudPrestamo`, y dos enumeraciones de estado. El modelo se mantuvo simple a propósito: para este incremento solo se implementaron completamente los estados que se usan en el flujo (SOLICITADA y CANCELADA); APROBADA, ENTREGADA y DEVUELTA quedan modeladas para un siguiente Sprint.

| Elemento | Campos / valores |
|----------|-------------------|
| `Equipo` | id, nombre, categoria (`CategoriaEquipo`), estado (`DISPONIBLE` / `RESERVADO` / `PRESTADO`) |
| `SolicitudPrestamo` | id, equipoId, ambienteDestino, proposito, duracionHoras, estado (`SOLICITADA` / `APROBADA` / `ENTREGADA` / `DEVUELTA` / `CANCELADA` / `RECHAZADA`) |

**Arquitectura y flujo unidireccional (UDF)**

```
UI (Compose) --eventos--> ViewModel --> Repository --> InMemory data
UI (Compose) <--UiState-- ViewModel
```

| Capa | Responsabilidad | No hace |
|------|------------------|---------|
| UI Compose | Renderizar UiState y emitir eventos/callbacks. | Modificar listas internas del Repository. |
| ViewModel | Coordinar estado, validación y acciones de pantalla. | Guardar Activity, Context o NavController. |
| Repository | Definir operaciones del dominio y ser la fuente de verdad. | Decidir mensajes o comportamiento visual. |
| InMemoryRepository | Simular catálogo y solicitudes compartidas durante la ejecución. | Prometer persistencia real. |
| Navigation | Conectar destinos y pasar IDs (equipoId, solicitudId). | Pasar entidades completas. |

Esta separación es la que permite probar las reglas de negocio (propósito, duración, disponibilidad) con pruebas unitarias, sin depender de la interfaz.

**Navegación implementada**

```
Catalogo -> EquipoDetalle/{equipoId} -> Solicitar/{equipoId}
MisSolicitudes -> SolicitudDetalle/{solicitudId}
```

Un identificador inexistente no cierra la app: la pantalla de destino recibe `null` y muestra un mensaje recuperable.

**Reglas de negocio implementadas**

| ID | Regla | Dónde se aplica |
|----|-------|------------------|
| RN-01 | Solo un equipo DISPONIBLE puede solicitarse | `Validaciones.kt`, `InMemoryPrestamoRepository` |
| RN-02 | Ambiente/destino obligatorio | `Validaciones.kt` |
| RN-03 | Propósito entre 10 y 180 caracteres | `Validaciones.kt` |
| RN-04 | Duración entre 1 y 8 horas | `Validaciones.kt` |
| RN-05 | Una acción de Guardado crea una sola solicitud | flag `guardando` en `PrestamoViewModel` + `synchronized` en el Repository |
| RN-06 | Una solicitud activa reserva el equipo | `InMemoryPrestamoRepository.crearSolicitud` |
| RN-07 | Solo SOLICITADA puede cancelarse en el MVP | `Validaciones.kt`, `InMemoryPrestamoRepository.cancelarSolicitud` |
| RN-08 | ID inexistente produce estado recuperable | `PrestamoNavGraph` + pantallas de detalle |
| RN-09 | Datos sintéticos | catálogo semilla en `InMemoryPrestamoRepository` |

## 6. Desarrollo del incremento Android

El incremento se construyó en Kotlin + Jetpack Compose, con Material 3, ViewModel, StateFlow y Navigation Compose:

- `model/`: `Equipo`, `SolicitudPrestamo`, `Estados` y `Validaciones` (reglas de negocio desacopladas de la UI).
- `data/repository/`: contrato `PrestamoRepository` y su implementación `InMemoryPrestamoRepository`.
- `viewmodel/`: `PrestamoViewModel` + `PrestamoUiState` + fábrica de ViewModel.
- `ui/`: pantallas de Catálogo, Detalle de equipo, Solicitar, Mis solicitudes y Detalle de solicitud.
- `navigation/`: `PrestamoNavGraph` con las rutas por ID.
- Pruebas unitarias JUnit sobre las reglas de negocio (`app/src/test`).

La regla de doble pulsación (RN-05) se protege en dos capas: el ViewModel ignora una segunda solicitud mientras `guardando` es verdadero, y el Repository sincroniza la creación de la solicitud para que dos hilos no puedan reservar el mismo equipo al mismo tiempo. Esta decisión fue justamente la causa del defecto BUG-03 descrito en la sección 8.

## 7. Plan y diseño de pruebas

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

**7.2 Suite de casos (18 casos, al menos 3 técnicas de caja negra)**

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

**Defecto adicional encontrado en pruebas manuales post-entrega**: el ícono de acceso rápido a "Mis solicitudes" en la barra superior del catálogo no tenía ninguna acción conectada (`Icon` sin `onClick`). Se corrigió envolviéndolo en un `IconButton(onClick = onVerMisSolicitudes)`. Ver commit `fix: conectar botón Ver mis solicitudes y remover ícono de app inexistente`.

## 9. Sprint Review y Retrospective

**Sprint Review**: se demostró el flujo completo — catálogo con disponibilidad real, solicitud válida sobre un equipo DISPONIBLE, rechazo de una solicitud sobre un equipo no disponible, consulta de Mis solicitudes, cancelación de una solicitud SOLICITADA y los resultados de la suite de pruebas, incluyendo BUG-03 ya corregido. El Sprint Goal se alcanzó: se puede consultar un equipo disponible y registrar una solicitud válida, con disponibilidad coherente y evidencia real de calidad. Quedó pendiente para el Product Backlog habilitar las transiciones APROBADA/ENTREGADA/DEVUELTA en un futuro incremento.

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

- Repositorio Git: `prestamolab-ctma-android` (código Android completo, `.github/workflows` y este README).
- Aplicación Android ejecutable con el alcance mínimo descrito en esta guía.
- Este README, con propósito, instalación, arquitectura, navegación, reglas de negocio, pruebas y limitaciones.
- Product Goal, Product Backlog, Sprint Goal, Sprint Backlog y Definition of Done (secciones 1 y 4).
- Matriz de riesgos y matriz de trazabilidad (secciones 3 y 7.3).
- Suite de 18 casos y datos sintéticos (sección 7.2).
- Bitácora con PASS/FAIL/BLOCKED y evidencia (sección 8.1).
- Registro del defecto real encontrado, BUG-03 (sección 8.2).
- Confirmación y regresión tras la corrección (sección 8.3).
- Sprint Review y acción de Retrospective (sección 9).
- Informe ejecutivo de calidad (sección 12).

## 12. Informe ejecutivo de calidad

| Sección | Respuesta |
|---------|-----------|
| Alcance | Se construyó y probó el catálogo, el detalle de equipo, el registro y cancelación de solicitudes y la navegación por ID. Quedaron fuera las transiciones APROBADA/ENTREGADA/DEVUELTA. |
| Ejecución | 18 casos planificados, 18 ejecutados: 17 PASS y 1 FAIL inicial (TC-13), resuelto y confirmado; 1 caso quedó BLOCKED en su primer intento por falta de dato de prueba y se re-ejecutó como PASS. |
| Defectos | BUG-03 (doble pulsación duplicaba solicitudes) fue el defecto alto encontrado en pruebas planificadas; ya está corregido, confirmado y con regresión ejecutada. Se encontró y corrigió además un defecto menor en pruebas manuales post-entrega (ícono "Mis solicitudes" sin acción). |
| Riesgo residual | Las transiciones APROBADA/ENTREGADA/DEVUELTA no tienen UI ni pruebas todavía; si se habilitan en un próximo Sprint, necesitan su propia suite. |
| Limitaciones | Repository en memoria (sin persistencia real), sin autenticación de usuarios, ambiente de emulador únicamente. |
| Definition of Done | 9 de 10 criterios cumplidos en este incremento; el pendiente es ampliar la cobertura de pruebas instrumentadas de UI. |
| Recomendación | **ACEPTABLE**. El Sprint Goal se cumplió con evidencia real y el único defecto alto encontrado quedó corregido y confirmado. |

## 13. Instalación y ejecución

1. Abrir la carpeta del proyecto en Android Studio (Koala o superior).
2. Dejar que Gradle sincronice las dependencias (requiere conexión a internet la primera vez).
3. Ejecutar la configuración `app` sobre un emulador o dispositivo con Android 7.0 (API 24) o superior.

También puede compilarse por línea de comandos:

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
```

## 14. Limitaciones conocidas

- No hay persistencia real: los datos se pierden al cerrar la app (Repository en memoria, punto 11 del alcance mínimo).
- Los estados APROBADA, ENTREGADA y DEVUELTA están modelados pero no tienen una transición disponible desde la UI en este primer incremento; quedan como base para un siguiente Sprint.
- No hay autenticación real de usuarios ni control de roles.

## 15. Uso responsable de inteligencia artificial

Se utilizó IA como apoyo para redactar y revisar código base y documentación del proyecto, siguiendo la guía "Uso responsable de inteligencia artificial" del curso. Toda sugerencia fue comprendida, adaptada y verificada por el equipo antes de incorporarse; ningún resultado de prueba fue inventado.
