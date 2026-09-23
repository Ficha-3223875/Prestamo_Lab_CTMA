# Matriz de trazabilidad — PréstamoLab CTMA

Cadena completa exigida por la guía (sección 7): HU → CA → Riesgo →
TC → PR → Resultado → Bug.

> **Nota de auditoría (Semana 5):** hasta esta semana el equipo integró
> el código mediante commits directos a `main`/rama de trabajo, sin
> abrir Pull Requests formales. La columna **PR** queda marcada como
> "commit directo" donde aplica. A partir de la Semana 8 la guía exige
> abrir PR y verificar GitHub Actions antes de fusionar (sección 8,
> actividad 29), así que de ahí en adelante esta columna sí tendrá
> números de PR reales.

| HU | Criterio de aceptación | Riesgo | Caso | PR | Resultado | Defecto |
|----|--------------------------|--------|------|----|-----------|---------|
| HU-03 | Una sola solicitud por Guardado (RN-05) | R-01 | TC-13 | commit directo | FAIL → PASS tras corrección | BUG-03 |
| HU-03 | Solo equipo DISPONIBLE puede solicitarse (RN-01) | R-01 | TC-12 | commit directo | PASS | — |
| HU-04 | Límites de propósito (RN-03) | R-02 | TC-04 a TC-07 | commit directo | PASS | — |
| HU-04 | Límites de duración (RN-04) | R-02 | TC-08 a TC-11 | commit directo | PASS | — |
| HU-05 | Un ID inexistente no cierra la app (RN-08) | R-03 | TC-03 | commit directo | PASS | — |
| HU-07 | Solo SOLICITADA se cancela (RN-07) | R-06 | TC-15, TC-16 | commit directo | PASS | — |
| HU-07 | SOLICITADA → APROBADA (RN-10) | R-07 | TC-19 | commit directo | PASS | — |
| HU-07 | APROBADA → ENTREGADA (RN-11) | R-07 | TC-20 | commit directo | PASS | — |
| HU-07 | ENTREGADA → DEVUELTA (RN-12) | R-07 | TC-21 | commit directo | PASS | — |
| HU-10 | Tarjetas anuncian nombre+estado para lectores de pantalla | R-08 | Auditoría manual | commit directo | PASS | BUG (ícono "Mis solicitudes" sin acción, corregido) |

## Cobertura

- 10 de 10 filas con caso de prueba asociado.
- 2 defectos reales encontrados y corregidos con confirmación/regresión
  (ver README, secciones 8.2/8.3, y el commit `fix: conectar botón Ver
  mis solicitudes y remover ícono de app inexistente`).
- 0 riesgos Crítico o Alto sin cobertura al cierre de la auditoría de
  Semana 5.
