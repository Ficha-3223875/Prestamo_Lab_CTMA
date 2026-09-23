# Refinamiento, estimación y métricas — Semana 7

## Nota sobre la modalidad de trabajo real

La guía sugiere Planning Poker en equipo (varias personas votando el
mismo Product Backlog Item al mismo tiempo). En la práctica, cada
integrante de este equipo trabaja su parte de forma individual y solo
se coordina compartiendo el repositorio, sin sesiones de refinamiento
conjuntas. Por eso lo que sigue es una **estimación y refinamiento
individual**, hecha en retrospectiva sobre el trabajo ya construido —
no una sesión de Planning Poker real con varias personas votando.
Esto se documenta así, tal cual pasó, en vez de simular un consenso
de equipo que no existió: es lo que la guía pide evitar en la sección
14 ("no presentar código o proceso que el equipo no pueda explicar").

## 1. Refinamiento de HU (actividad 16)

Revisión con criterio INVEST (Independiente, Negociable, Valiosa,
Estimable, Small/pequeña, Testeable) sobre las 12 HU del backlog:

| HU | ¿Cumple INVEST? | Observación |
|----|-------------------|-------------|
| HU-01 a HU-06 | Sí | Alcance claro, una acción por historia, ya implementadas y probadas. |
| HU-07 (Consultar y gestionar mis préstamos activos) | **No del todo** | "Gestionar" resultó ser 4 acciones distintas (aprobar, rechazar, entregar, devolver), cada una con su propia regla de negocio (RN-10 a RN-12). Candidata a dividirse en el futuro en HU-07a (consultar) y HU-07b/c/d (cada transición), aunque por ahora se mantiene unificada porque ya está completamente implementada y probada como conjunto. |
| HU-08, HU-09 | Sí | Alcance técnico puntual (navegación, prevención de duplicados). |
| HU-10 (Accesibilidad y manejo de errores) | **No del todo** | Mezcla dos preocupaciones distintas (accesibilidad visual/semántica y manejo de errores de ejecución). Se resolvieron ambas, pero como HU quedaría más clara dividida en dos para un backlog futuro. |
| HU-11, HU-12 | Sí | Alcance de "entorno de pruebas" y "documentación de arquitectura", respectivamente, ambos verificables de forma directa. |

**Conclusión del refinamiento**: 10 de 12 HU cumplen INVEST sin
cambios. HU-07 y HU-10 son las más grandes del backlog y, si el
equipo tuviera que reabrir trabajo sobre ellas, se recomienda
dividirlas primero.

## 2. Estimación individual (actividad 17)

Escala relativa tipo Fibonacci (1, 2, 3, 5, 8), estimada en
retrospectiva sobre la dificultad real de cada HU, no antes de
empezar:

| HU | Story points | Justificación breve |
|----|----------------|------------------------|
| HU-01 | 2 | Modelo de dominio simple, sin lógica compleja. |
| HU-02 | 2 | Repository + implementación InMemory directa. |
| HU-03 | 3 | ViewModel + StateFlow, requiere entender flujo unidireccional. |
| HU-04 | 2 | Pantalla de lista simple. |
| HU-05 | 1 | Pantalla de detalle, sin lógica nueva. |
| HU-06 | 3 | Validaciones con varios casos límite (propósito, duración). |
| HU-07 | 8 | La más grande: 4 transiciones de estado, cada una con su regla. |
| HU-08 | 3 | Navegación con paso de IDs entre 5 pantallas. |
| HU-09 | 3 | Requirió depurar una condición de carrera real (BUG-03). |
| HU-10 | 5 | Accesibilidad + manejo de errores, alcance amplio y menos concreto. |
| HU-11 | 5 | Pruebas unitarias e instrumentadas, dos entornos distintos. |
| HU-12 | 2 | Documentación de arquitectura y DoD. |

**Total del backlog: 39 story points.**

## 3. Capacidad y Sprint Planning (actividad 17)

Capacidad individual declarada: **menos de 5 horas por semana**. Con
esa disponibilidad, la velocidad realista para una sola persona
principiante ronda 5-8 puntos por semana, lo que explica por qué la
guía reparte el trabajo en 5 semanas (Semana 5 a 9) en vez de
esperarlo en una sola entrega: 39 puntos ÷ ~7 puntos/semana ≈ 5-6
semanas, coherente con la duración sugerida de la guía.

## 4. Métricas básicas del Sprint (actividad 21)

Con las fechas reales de trabajo sobre este repositorio:

| Hito | Fecha aproximada | Lead time desde el hito anterior |
|------|---------------------|-------------------------------------|
| Primer incremento (v0.1.0, guía 1) | 25 de agosto | — |
| Corrección de defectos + Gradle Wrapper | 3 de septiembre | ~9 días |
| Migración AGP 9 / Gradle 9.1 (JDK 25) | 8 de septiembre | ~5 días |
| Semana 5 (auditoría, v0.2.0) | 23 de septiembre | ~15 días |
| Semana 6 (Room, v0.3.0) | 23 de septiembre | mismo día |
| Semana 7 (Flow reactivo, v0.4.0) | 23 de septiembre | mismo día |

**Análisis de cuello de botella**: el tiempo más largo NO estuvo en
escribir las HU o el código de negocio, sino en la **configuración
del entorno** (SDK de Android Studio, compatibilidad de Gradle con la
versión de Java del computador, migración a AGP 9). Una vez el
entorno quedó estable, las Semanas 5, 6 y 7 se implementaron en una
sola sesión de trabajo. Recomendación para futuros incrementos:
resolver y documentar el entorno de desarrollo ANTES de empezar a
programar, no sobre la marcha.
