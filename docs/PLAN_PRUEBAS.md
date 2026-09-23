# Plan de pruebas — PréstamoLab CTMA

Trasladado desde el README para cumplir la ubicación recomendada en
la sección 9 de la guía (`docs/PLAN_PRUEBAS.md` + código de tests).
Se conserva la suite heredada de la primera guía integradora y se
amplía con los casos de la gestión completa del ciclo de vida
(HU-07 ampliada) agregados durante la Semana 5.

## 1. Alcance del plan

| Campo | Definición |
|-------|------------|
| Objetivo | Verificar que el incremento cumple las reglas RN-01 a RN-12 y las historias HU-01 a HU-08 (numeración interna del equipo). |
| Versión / build | v0.2.0 (auditoría y consolidación, Semana 5) |
| Alcance incluido | Catálogo, detalle de equipo, solicitud, validaciones, Mis solicitudes, cancelación, gestión completa (aprobar/rechazar/entregar/devolver), navegación por ID, accesibilidad básica. |
| Exclusiones de esta versión | Persistencia real (Room llega en Semana 6), sincronización remota (Retrofit llega en Semana 8), capacidades físicas del dispositivo (Semana 9). |
| Ambiente | Emulador Android API 24+, Android Studio, Gradle 9.1 / AGP 9.0.1. |
| Datos | Catálogo sintético de 6 equipos (`InMemoryPrestamoRepository`). |
| Criterios de entrada | Build compila; catálogo semilla cargado. |
| Criterios de salida | Sin defectos altos abiertos sin decisión; casos críticos en PASS. |
| Convención | PASS / FAIL / BLOCKED. |

## 2. Suite de casos (mínimo 10 exigido por la guía; esta suite tiene 21)

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
| TC-19 | Aprobar una SOLICITADA | Pasa a APROBADA. | Transición (RN-10) |
| TC-20 | Entregar una APROBADA | Pasa a ENTREGADA, equipo PRESTADO. | Transición (RN-11) |
| TC-21 | Devolver una ENTREGADA | Pasa a DEVUELTA, equipo DISPONIBLE. | Transición (RN-12) |

## 3. Pruebas automatizadas existentes

- **Unitarias (JUnit)**: `app/src/test/.../ValidacionesTest.kt` — 18 pruebas sobre límites de propósito/duración y transiciones de estado (RN-01, RN-03, RN-04, RN-10 a RN-12).
- **Instrumentadas (Compose UI Test)**: `app/src/androidTest/.../CatalogoScreenTest.kt` — verifica TC-01 desde la UI real, no solo la lógica.

## 4. Ejecución de Semana 5

Ver `MATRIZ_TRAZABILIDAD.md` para el detalle de qué caso valida qué
historia/riesgo, y la bitácora completa en el README (sección 8.1),
que se conserva ahí por ahora dado su tamaño.
