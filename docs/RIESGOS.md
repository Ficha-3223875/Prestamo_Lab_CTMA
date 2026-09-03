| ID   | Riesgo                                                               | Probabilidad | Impacto | Nivel | Tratamiento                                          |
| ---- | -------------------------------------------------------------------- | -----------: | ------: | ----- | ---------------------------------------------------- |
| R-01 | Falta de consistencia en nombres o visibilidad de enums entre módulos |        Media |   Media | Medio | Definición estricta de tipos e inmutabilidad         |
| R-02 | Incompatibilidad de paquetes al refactorizar componentes             |         Baja |    Alta | Alto  | Organización modular siguiendo la guía de paquetes   |
|      |                                                                      |              |         |       |                                                      |
| R-03 | Pérdida de estado al reiniciar o cerrar la aplicación                    |        Media |   Media | Medio | Manejo de persistencia temporal en memoria sin depender de red |
| R-04 | Mutación indebida o acceso concurrente a la lista de equipos sintéticos  |         Baja |   Media | Medio | Uso de colecciones inmutables o thread-safe en el repositorio |
|      |                                                                      |              |         |       |                                                      |
| R-05 | Fuga de memoria o acoplamiento por referencias a Context/Activity        |        Media |    Alta | Alto  | Desacoplado total: ViewModel independiente de Context y Compose|
| R-06 | Estado de UI inconsistente al recibir múltiples peticiones concurrentes |         Baja |   Media | Medio | Uso de StateFlow con inmutabilidad en las emisiones de UiState |
|      |                                                                      |              |         |       |                                                      |
| R-07 | Lentitud o desbordamiento visual en la lista por alta cantidad de elementos |        Media |   Media | Medio | Uso de componentes optimizados como `LazyColumn` en Compose   |
| R-08 | Visualización errónea o incompleta de atributos en las tarjetas Material 3  |         Baja |   Media | Medio | Pruebas de renderizado UI y validación de atributos nulos     |
|      |                                                                      |              |         |       |                                                      |
| R-09 | Permisión de solicitud en equipos no disponibles por fallo de estado en UI  |        Media |    Alta | Alto  | Deshabilitar el botón desde la lógica de estado (UiState) y validar RN-01 |
| R-10 | Error al cargar la vista de detalle por paso de ID inexistente o nulo       |         Baja |   Media | Medio | Manejar estados de fallback o pantalla de error si el equipo no se encuentra  |
|      |                                                                      |              |         |       |                                                      |
| R-11 | Envío de solicitudes con datos no válidos o incompletos por fallo en frontend |        Media |    Alta | Alto  | Validaciones síncronas en ViewModel antes de permitir la acción de envío |
| R-12 | Mensajes de error poco claros o ausentes que confundan al aprendiz            |         Baja |   Media | Medio | Asignación de mensajes de error descriptivos asociados a cada regla (RN) |
|      |                                                                      |              |         |       |                                                      |
| R-13 | Cancelación no permitida ejecutada sobre solicitudes aprobadas o cerradas   |        Media |    Alta | Alto  | Validar restricción por estado del modelo (RN-07) previo a la acción |
| R-14 | Inconsistencia de datos entre el estado de la solicitud y el del equipo     |         Baja |    Alta | Alto  | Ejecución atómica del cambio de estado (solicitud a CANCELADA, equipo a DISPONIBLE) |
|      |                                                                      |              |         |       |                                                      |
| R-15 | Cuelgue o inconsistencia al deserializar objetos en argumentos de navegación |        Media |   Media | Medio | Restringir argumentos de rutas únicamente a IDs de tipos primitivos   |
| R-16 | Acumulación indebida de destinos en la pila de navegación (back stack)        |         Baja |   Media | Medio | Configurar apropiadamente los estallidos (`popUpTo`) e intenciones de la pila |
|      |                                                                      |              |         |       |                                                      |
| R-17 | Creación de registros duplicados en el repositorio por doble pulsación simultánea |        Media |    Alta | Alto  | Deshabilitar el control de envío en el primer evento de pulsación (RN-05) |
| R-18 | Bloqueo permanente del formulario si ocurre un error en el repositorio             |         Baja |   Media | Medio | Restablecer el flag `guardando` a `false` en el bloque de manejo de errores |
|      |                                                                      |              |         |       |                                                      |
| R-19 | Cierre abrupto (crash) de la app al intentar cargar un ID no existente   |        Media |    Alta | Alto  | Captura de excepciones y renderizado de pantalla de error recuperable (RN-08) |
| R-20 | Inaccesibilidad visual o ruptura del diseño al aumentar el tamaño del texto |         Baja |   Media | Medio | Uso de componentes scrollables y etiquetas descriptivas independientes del color |
|      |                                                                      |              |         |       |                                                      |
| R-21 | Aceptación indebida de valores fuera de los límites en los formularios por falla en el ViewModel |        Media |    Alta | Alto  | Pruebas de valores frontera en límites de caracteres y horas |
| R-22 | Inconsistencia o degradación en flujos ya implementados durante la adición de nuevas validaciones |         Baja |   Media | Medio | Ejecución de checklist de regresión manual antes de liberar entrega |
|      |                                                                      |              |         |       |                                                      |
| R-23 | Violación del patrón MVVM por llamadas directas desde Composables al Repositorio |        Media |    Alta | Alto  | Revisiones de código e inspección de dependencias en las vistas UI |
| R-24 | Fallos de compilación o archivos no rastreados en el repositorio antes de la entrega|         Baja |    Alta | Alto  | Verificación de build limpio y comandos de estado en Git previa sustentación |