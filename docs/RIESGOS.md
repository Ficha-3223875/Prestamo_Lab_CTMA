# Matriz de riesgos — PréstamoLab CTMA

Auditoría de Semana 5 (guía integradora, semanas 5-9). Este documento
se traslada desde el README para cumplir la ubicación recomendada en
la sección 9 de la guía (`docs/RIESGOS.md`). El contenido es el mismo
que ya existía; no se recrea desde cero, solo se reubica y se amplía.

| ID | Riesgo | Prob. | Impacto | Nivel | Cobertura (TC) |
|----|--------|-------|---------|-------|-----------------|
| R-01 | Dos solicitudes activas reservan el mismo equipo. | Alta | Alta | Crítico | TC-12, TC-13 |
| R-02 | Datos fuera de rango son aceptados (propósito o duración). | Alta | Media | Alto | TC-04 a TC-11 |
| R-03 | Un ID inexistente provoca cierre abrupto de la app. | Media | Alta | Alto | TC-03 |
| R-04 | El catálogo no refleja el cambio de estado tras crear o cancelar. | Media | Alta | Alto | TC-14, TC-15 |
| R-05 | Con fuente aumentada 1.5×, botones o textos esenciales desaparecen. | Media | Media | Medio | TC-18 |
| R-06 | Se permite cancelar una solicitud que ya no está en SOLICITADA. | Baja | Alta | Medio | TC-16 |
| R-07 | Una transición de estado (aprobar/entregar/devolver) se aplica sobre un estado que no la admite. | Media | Alta | Alto | TC-19 a TC-21 (ver PLAN_PRUEBAS.md) |
| R-08 | Un elemento interactivo sin `contentDescription` queda inaccesible para lectores de pantalla. | Baja | Media | Bajo | Auditoría manual de accesibilidad (sección 15 del README) |

## Priorización para Semana 5

Los riesgos R-01 a R-06 ya estaban cubiertos por la suite heredada.
R-07 se agregó al incorporar la gestión completa del ciclo de vida
(aprobar/rechazar/entregar/devolver). R-08 se agregó tras el hallazgo
del ícono de "Mis solicitudes" sin `onClick` conectado, documentado
como defecto real en la ejecución de pruebas.

Ningún riesgo Crítico permanece sin cobertura de prueba al cierre de
esta auditoría.
