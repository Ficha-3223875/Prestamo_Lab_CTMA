# Historias de Usuario - PréstamoLab CTMA

Este documento detalla las Historias de Usuario (HU) que guían el desarrollo del proyecto, organizadas por etapas de implementación y vinculadas con sus riesgos y casos de prueba.

---

## Etapa 1: Estructura del proyecto y modelos

### HU-01: Definir modelos de dominio y estructura base
**Como** desarrollador del sistema,  
**quiero** crear los paquetes de arquitectura y las clases de datos (Equipo, SolicitudPrestamo, y sus Enums),  
**para** tener una base sólida que represente la información del negocio sin depender de frameworks externos.

**Criterios de Aceptación**
*   CA-01.1: La estructura de carpetas incluye `model/`, `data/repository/`, `ui/`, `viewmodel/`, `navigation/` y `theme/`.
*   CA-01.2: Existen las data classes `Equipo` y `SolicitudPrestamo` con los atributos definidos en la guía.
*   CA-01.3: Existen los enums `CategoriaEquipo`, `EstadoEquipo` (DISPONIBLE, RESERVADO, PRESTADO) y `EstadoSolicitud` con la transición mínima SOLICITADA -> CANCELADA.

**Riesgos relacionados**
*   R-01
*   R-02

**Casos de prueba relacionados**
*   TC-01 - Inspección de árbol de directorios en el IDE
*   TC-02 - Instanciación de clase Equipo con datos válidos
*   TC-03 - Instanciación de clase SolicitudPrestamo
*   TC-04 - Mapeo correcto de Enums del sistema

---

## Etapa 2: Repository e InMemoryRepository

### HU-02: Implementar persistencia inicial y contrato de repositorio
**Como** sistema,  
**quiero** almacenar la información en un repositorio (inicialmente en memoria),  
**para** proveer datos sintéticos y mantener el estado durante la ejecución de la app.

**Criterios de Aceptación**
*   CA-02.1: Existe una interfaz `PrestamoRepository` con las operaciones de lectura y escritura básicas.
*   CA-02.2: Se implementa un repositorio cargado inicialmente con al menos 8 equipos sintéticos de prueba.
*   CA-02.3: No se utiliza información real ni llamadas a red externa (RN-09).

**Riesgos relacionados**
*   R-03
*   R-04

**Casos de prueba relacionados**
*   TC-05 - Revisión de interfaz PrestamoRepository
*   TC-06 - Validación de implementación de repositorio
*   TC-07 - Consulta de lista inicial de equipos (8 sintéticos)
*   TC-08 - Inspección de ausencia de dependencias de red/BD externas

---

## Etapa 3: ViewModel y UiState

### HU-03: Gestionar el estado unidireccional (StateFlow)
**Como** interfaz de usuario,  
**quiero** suscribirme a un `UiState` emitido por el ViewModel,  
**para** reaccionar a los cambios de datos sin modificar directamente el repositorio.

**Criterios de Aceptación**
*   CA-03.1: El `PrestamoViewModel` expone un estado `PrestamoUiState` (equipos, solicitudes, mensajes, guardando).
*   CA-03.2: El ViewModel no tiene referencias a contextos, activities ni componentes de Jetpack Compose.
*   CA-03.3: Los cambios en los datos se reflejan automáticamente en la UI mediante flujos reactivos.

**Riesgos relacionados**
*   R-05
*   R-06

**Casos de prueba relacionados**
*   TC-09 - Inspección de estructura PrestamoUiState
*   TC-10 - Suscripción a StateFlow y emisión de estado inicial
*   TC-11 - Actualización de UI tras acciones en ViewModel
*   TC-12 - Análisis de dependencias en el código del ViewModel

---

## Etapa 4: Catálogo

### HU-04: Visualizar el catálogo de equipos
**Como** aprendiz,  
**quiero** ver una lista de equipos disponibles en el catálogo,  
**para** conocer las herramientas que ofrece el CTMA.

**Criterios de Aceptación**
*   CA-04.1: La pantalla muestra tarjetas de Material 3 con nombre, categoría y estado del equipo.
*   CA-04.2: Existe una acción de "Ver detalle" funcional en cada tarjeta.

**Riesgos relacionados**
*   R-07
*   R-08

**Casos de prueba relacionados**
*   TC-13 - Renderizado de CatalogoScreen
*   TC-14 - Verificación de diseño de tarjetas Material 3
*   TC-15 - Presencia de acción "Ver detalle"
*   TC-16 - Despliegue de lista con datos del repositorio

---

## Etapa 5: Detalle de equipo

### HU-05: Visualizar la disponibilidad de un equipo
**Como** aprendiz,  
**quiero** ver los detalles técnicos y estado de un equipo seleccionado,  
**para** decidir si puedo iniciar una solicitud de préstamo.

**Criterios de Aceptación**
*   CA-05.1: Muestra nombre, categoría y estado actual del equipo.
*   CA-05.2: Si el equipo está DISPONIBLE, habilita el botón "Solicitar préstamo".
*   CA-05.3: Si el equipo está RESERVADO o PRESTADO, el botón permanece inactivo (RN-01).

**Riesgos relacionados**
*   R-09
*   R-10

**Casos de prueba relacionados**
*   TC-17 - Carga de EquipoDetalleScreen
*   TC-18 - Visualización de información técnica completa
*   TC-19 - Habilitación de botón para equipos DISPONIBLES
*   TC-20 - Deshabilitación de botón para equipos no disponibles

---

## Etapa 6: Formulario de solicitud y validaciones

### HU-06: Diligenciar y validar formulario de préstamo
**Como** aprendiz,  
**quiero** llenar un formulario con mi necesidad específica,  
**para** formalizar el préstamo de una herramienta disponible.

**Criterios de Aceptación**
*   CA-06.1: Contiene campos para: ambiente/destino, propósito y duración.
*   CA-06.2: Valida que el destino sea obligatorio (RN-02).
*   CA-06.3: Valida propósito entre 10-180 caracteres (RN-03).
*   CA-06.4: Valida duración entre 1-8 horas (RN-04).
*   CA-06.5: Muestra mensajes de error claros debajo de cada campo inválido.

**Riesgos relacionados**
*   R-11
*   R-12
*   R-21

**Casos de prueba relacionados**
*   TC-21 - Carga de SolicitarPrestamoScreen
*   TC-22 - Validación de campo ambiente requerido (RN-02)
*   TC-23 - Validación de longitud de propósito (RN-03)
*   TC-24 - Validación de rango de duración (RN-04)
*   TC-25 - Visualización de mensajes de error en la UI

---

## Etapa 7: Mis Solicitudes y detalle de solicitud

### HU-07: Consultar y gestionar mis préstamos activos
**Como** aprendiz,  
**quiero** ver una lista de mis solicitudes y poder cancelarlas si están en estado "SOLICITADA",  
**para** liberar la herramienta si ya no la necesito.

**Criterios de Aceptación**
*   CA-07.1: La lista muestra un resumen claro de las solicitudes realizadas.
*   CA-07.2: El detalle permite cancelar únicamente si el estado es SOLICITADA (RN-07).
*   CA-07.3: Al cancelar, el equipo asociado vuelve a estar DISPONIBLE automáticamente.

**Riesgos relacionados**
*   R-13
*   R-14

**Casos de prueba relacionados**
*   TC-26 - Despliegue de lista en MisSolicitudesScreen
*   TC-27 - Visualización de detalles en SolicitudDetalleScreen
*   TC-28 - Visibilidad de botón cancelar para estado SOLICITADA
*   TC-29 - Restricción de cancelación para otros estados (RN-07)
*   TC-30 - Integración de cancelación y liberación de equipo

---

## Etapa 8: Navegación

### HU-08: Navegar de forma segura entre pantallas
**Como** usuario de la aplicación,  
**quiero** fluir entre el catálogo, los detalles y el formulario sin perder el rastro de dónde estoy,  
**para** tener una experiencia de uso intuitiva.

**Criterios de Aceptación**
*   CA-08.1: Implementada con Navigation Compose pasando únicamente IDs primitivos.
*   CA-08.2: El manejo del back stack permite usar el botón atrás nativo correctamente.
*   CA-08.3: Existe una barra de navegación inferior para acceso rápido.

**Riesgos relacionados**
*   R-15
*   R-16

**Casos de prueba relacionados**
*   TC-31 - Transiciones entre destinos de AppNavigation
*   TC-32 - Inspección de firma de rutas y paso de parámetros
*   TC-33 - Comprobación de navegación hacia atrás y Back Stack

---

## Etapa 9: Protección contra doble pulsación

### HU-09: Prevenir duplicidad en las solicitudes
**Como** sistema,  
**quiero** bloquear la acción de guardar una vez presionada,  
**para** evitar crear múltiples solicitudes por pulsaciones rápidas repetidas.

**Criterios de Aceptación**
*   CA-09.1: Al dar "Guardar", el estado `guardando` cambia a true (RN-05).
*   CA-09.2: La interfaz deshabilita el botón e indica carga mientras se procesa.

**Riesgos relacionados**
*   R-17
*   R-18

**Casos de prueba relacionados**
*   TC-34 - Presencia de flag guardando en UiState
*   TC-35 - Deshabilitación de botón Guardar tras el clic (RN-05)
*   TC-36 - Restauración de estado tras guardado exitoso
*   TC-37 - Prueba de estrés por múltiples pulsaciones rápidas

---

## Etapa 10: Accesibilidad y manejo de errores

### HU-10: Garantizar la estabilidad y accesibilidad de la UI
**Como** usuario,  
**quiero** que la aplicación sea tolerante a fallos y fácil de leer,  
**para** no sufrir cierres abruptos y entender la información visual.

**Criterios de Aceptación**
*   CA-10.1: Si se pasa un ID inexistente, se muestra un mensaje de error recuperable (RN-08).
*   CA-10.2: La disponibilidad se indica con texto claro además de colores.
*   CA-10.3: Soporta el aumento de fuente del sistema sin romper el diseño.

**Riesgos relacionados**
*   R-19
*   R-20

**Casos de prueba relacionados**
*   TC-38 - Manejo de IDs inexistentes (RN-08)
*   TC-39 - Presentación visual de estado (Texto + Color)
*   TC-40 - Prueba de escalado de fuente al 150%

---

## Etapa 11: Preparar pruebas

### HU-11: Validar las reglas de negocio (QA)
**Como** evaluador / instructor,  
**quiero** contar con el sistema preparado para ejecutar los casos de prueba,  
**para** verificar que el MVP cumple con los requisitos exigidos.

**Criterios de Aceptación**
*   CA-11.1: El código permite probar límites de caracteres en propósito (RN-03).
*   CA-11.2: El código permite probar límites de horas en duración (RN-04).

**Riesgos relacionados**
*   R-21
*   R-22

**Casos de prueba relacionados**
*   TC-41 - Valores frontera: Propósito (9, 10, 180, 181 chars)
*   TC-42 - Valores frontera: Duración (0, 1, 8, 9 horas)
*   TC-43 - Ejecución de flujo completo de regresión

---

## Etapa 12: Revisión de la arquitectura completa

### HU-12: Auditado de arquitectura y preparación de sustentación del proyecto
**Como** desarrollador junior,  
**quiero** verificar que todo el código sigue los lineamientos de arquitectura solicitados,  
**para** poder sustentar exitosamente el proyecto ante el evaluador.

**Criterios de Aceptación**
*   CA-12.1: Verificar que la interfaz de usuario (UI) no modifique directamente el repositorio en ninguna pantalla del aplicativo.
*   CA-12.2: Confirmar que todas las transiciones de estado de los equipos (DISPONIBLE → RESERVADO) operen correctamente según la regla de negocio (RN-06).
*   CA-12.3: Garantizar que el proyecto compila de manera limpia y sin errores de construcción en el entorno de desarrollo.
*   CA-12.4: Comprobar que el repositorio de Git se encuentra limpio, actualizado con la última versión del código y organizado en la estructura requerida.

**Riesgos relacionados**
*   R-23
*   R-24

**Casos de prueba relacionados**
*   TC-44 - Verificación de desacoplamiento entre las pantallas UI y el Repositorio
*   TC-45 - Validación de transiciones de estado de equipos (DISPONIBLE a RESERVADO) (RN-06)
*   TC-46 - Comprobación de compilación limpia del proyecto
*   TC-47 - Auditoría de estado y organización del repositorio Git

---

## Etapa 13: Persistencia de Datos Local (SQLite)

### HU-13: Implementar persistencia local con Room
**Como** desarrollador,  
**quiero** utilizar una base de datos local SQLite mediante la librería Room,  
**para** asegurar que la información persista tras cerrar la aplicación.

**Criterios de Aceptación**
*   CA-13.1: Se definen entidades `EquipoEntity` y `SolicitudEntity` con mappers a dominio.
*   CA-13.2: Se implementa `AppDatabase` con soporte para Enums via `TypeConverters`.
*   CA-13.3: Las consultas son asíncronas utilizando Coroutines y Flow.

**Riesgos relacionados**
*   R-26

**Casos de prueba relacionados**
*   TC-48 - Persistencia de datos tras reinicio de app
*   TC-49 - Fluidez de UI usando Coroutines para acceso a disco

---

## Etapa 14: Integración de Hardware (Cámara)

### HU-14: Capturar evidencia fotográfica del equipo
**Como** aprendiz,  
**quiero** tomar una foto del equipo al realizar la solicitud,  
**para** dejar registro del estado físico de la herramienta.

**Criterios de Aceptación**
*   CA-14.1: Acceso a la cámara mediante CameraX con previsualización.
*   CA-14.2: La ruta de la foto se guarda en SQLite asociada a la solicitud.
*   CA-14.3: La foto se visualiza en la pantalla de detalle de solicitud usando Coil.

**Riesgos relacionados**
*   R-25

**Casos de prueba relacionados**
*   TC-50 - Captura de fotografía y guardado en caché
*   TC-51 - Vinculación de imagen en entidad de solicitud

---

## Etapa 15: Integración de Hardware (Vibración)

### HU-15: Retroalimentación táctil del sistema
**Como** usuario,  
**quiero** recibir una vibración del dispositivo al completar una acción,  
**para** confirmar de manera táctil el éxito de la operación.

**Criterios de Aceptación**
*   CA-15.1: El dispositivo vibra brevemente al guardar una solicitud.
*   CA-15.2: Se manejan los permisos de vibración en el Manifiesto.

**Riesgos relacionados**
*   R-27

**Casos de prueba relacionados**
*   TC-52 - Verificación de vibración física en operación exitosa
