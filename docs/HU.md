# Historias de Usuario - PréstamoLab CTMA

Este documento detalla las Historias de Usuario (HU) que guían el desarrollo del proyecto, organizadas por etapas de implementación.

---

## Etapa 1: Estructura del proyecto y modelos

### HU-01: Definir modelos de dominio y estructura base
**Como** desarrollador del sistema,  
**Quiero** crear los paquetes de arquitectura y las clases de datos (Equipo, SolicitudPrestamo, y sus Enums),  
**Para** tener una base sólida que represente la información del negocio sin depender de frameworks externos.

**Criterios de Aceptación:**
*   La estructura de carpetas incluye `model/`, `data/repository/`, `ui/`, `viewmodel/`, `navigation/` y `theme/`.
*   Existen las data classes `Equipo` y `SolicitudPrestamo` con los atributos definidos en la guía.
*   Existen los enums `CategoriaEquipo`, `EstadoEquipo` (DISPONIBLE, RESERVADO, PRESTADO) y `EstadoSolicitud` con la transición mínima SOLICITADA -> CANCELADA.

---

## Etapa 2: Repository e InMemoryRepository

### HU-02: Implementar persistencia en memoria
**Como** sistema,  
**Quiero** almacenar la información en un repositorio en memoria (`InMemoryPrestamoRepository`),  
**Para** proveer datos sintéticos y mantener el estado durante la ejecución de la app sin usar bases de datos reales.

**Criterios de Aceptación:**
*   Existe una interfaz `PrestamoRepository` con las operaciones CRUD básicas.
*   Se implementa `InMemoryPrestamoRepository` cargado inicialmente con al menos 8 equipos sintéticos de prueba.
*   No se utiliza información real, bases de datos ni llamadas a red (RN-09).

---

## Etapa 3: ViewModel y UiState

### HU-03: Gestionar el estado unidireccional (StateFlow)
**Como** interfaz de usuario,  
**Quiero** suscribirme a un `UiState` emitido por el ViewModel,  
**Para** reaccionar a los cambios de datos sin modificar directamente el repositorio.

**Criterios de Aceptación:**
*   El `PrestamoViewModel` expone un estado `PrestamoUiState` (equipos, solicitudes, mensajes de error, estado de guardado).
*   El ViewModel no tiene referencias a contextos, activities ni componentes de Jetpack Compose.

---

## Etapa 4: Catálogo

### HU-04: Visualizar el catálogo de equipos
**Como** aprendiz,  
**Quiero** ver una lista de equipos disponibles en el catálogo,  
**Para** conocer las herramientas que ofrece el CTMA.

**Criterios de Aceptación:**
*   La pantalla muestra tarjetas de Material 3 con nombre, categoría y estado del equipo.
*   Existe una acción de "Ver detalle" en cada tarjeta.
*   Cumple el caso de prueba TC-01 (Catálogo con datos).

---

## Etapa 5: Detalle de equipo

### HU-05: Visualizar la disponibilidad de un equipo
**Como** aprendiz,  
**Quiero** ver los detalles técnicos y estado de un equipo seleccionado,  
**Para** decidir si puedo iniciar una solicitud de préstamo.

**Criterios de Aceptación:**
*   Muestra nombre, categoría y estado actual.
*   Si el equipo está DISPONIBLE, habilita el botón "Solicitar préstamo". Si está RESERVADO o PRESTADO, el botón está inactivo (RN-01, TC-12).

---

## Etapa 6: Formulario de solicitud y validaciones

### HU-06: Diligenciar y validar formulario de préstamo
**Como** aprendiz,  
**Quiero** llenar un formulario con mi necesidad específica,  
**Para** formalizar el préstamo de una herramienta disponible.

**Criterios de Aceptación:**
*   Contiene campos para: ambiente/destino, propósito y duración.
*   Validaciones desacopladas: destino obligatorio (RN-02), propósito entre 10-180 caracteres (RN-03), duración 1-8 horas (RN-04).
*   Muestra mensajes de error claros debajo de cada campo que no cumpla las reglas.

---

## Etapa 7: Mis Solicitudes y detalle de solicitud

### HU-07: Consultar y gestionar mis préstamos activos
**Como** aprendiz,  
**Quiero** ver una lista de mis solicitudes y poder cancelarlas si están en estado "SOLICITADA",  
**Para** liberar la herramienta si ya no la necesito.

**Criterios de Aceptación:**
*   La lista muestra un resumen de las solicitudes.
*   El detalle permite cancelar únicamente si el estado es SOLICITADA (RN-07).
*   Al cancelar, la solicitud pasa a CANCELADA y el equipo asociado vuelve a estar DISPONIBLE (TC-15, TC-16).

---

## Etapa 8: Navegación

### HU-08: Navegar de forma segura entre pantallas
**Como** usuario de la aplicación,  
**Quiero** fluir entre el catálogo, los detalles y el formulario sin perder el rastro de dónde estoy,  
**Para** tener una experiencia de uso intuitiva.

**Criterios de Aceptación:**
*   Implementada con Navigation Compose pasando únicamente `equipoId` o `solicitudId`, no objetos completos.
*   El manejo del back stack es correcto (el usuario puede usar el botón atrás nativo del dispositivo).

---

## Etapa 9: Protección contra doble pulsación

### HU-09: Prevenir duplicidad en las solicitudes
**Como** sistema,  
**Quiero** bloquear la acción de guardar una vez presionada,  
**Para** evitar crear múltiples solicitudes por errores de capa 8 (pulsaciones rápidas repetidas).

**Criterios de Aceptación:**
*   Al dar "Guardar", el estado guardando cambia a true (RN-05).
*   La interfaz deshabilita el botón mientras `guardando == true`.
*   Supera el caso de prueba TC-13 (Doble pulsación).

---

## Etapa 10: Accesibilidad y manejo de errores

### HU-10: Garantizar la estabilidad y accesibilidad de la UI
**Como** usuario,  
**Quiero** que la aplicación sea tolerante a fallos y fácil de leer,  
**Para** no sufrir cierres abruptos (crashes) y entender la información visual.

**Criterios de Aceptación:**
*   Si se pasa un ID inexistente en la navegación, se muestra un mensaje de error recuperable (RN-08, TC-03).
*   La disponibilidad no se indica únicamente con colores; se apoya en texto claro (ej. Estado: DISPONIBLE).
*   Soporta el aumento de fuente del sistema sin romper severamente la vista (TC-18).

---

## Etapa 11: Preparar pruebas

### HU-11: Validar las reglas de negocio (QA)
**Como** evaluador / instructor,  
**Quiero** contar con el sistema preparado para ejecutar los 18 casos de prueba,  
**Para** verificar que el MVP cumple estrictamente con las reglas de negocio exigidas en la guía.

**Criterios de Aceptación:**
*   El código permite probar desde TC-01 hasta TC-18 mediante manipulación de la interfaz.
*   Los límites de los campos (ej. 9 vs 10 caracteres, 0 vs 1 horas) reaccionan según las reglas RN-03 y RN-04.

---

## Etapa 12: Revisión de la arquitectura completa

### HU-12: Asegurar el cumplimiento técnico (Definition of Done)
**Como** desarrollador junior,  
**Quiero** verificar que todo el código sigue los lineamientos de arquitectura solicitados,  
**Para** poder sustentar exitosamente el proyecto ante el evaluador.

**Criterios de Aceptación:**
*   La UI solamente representa estados y emite eventos (no toca el repo directamente).
*   El proyecto compila y ejecuta correctamente.
*   Todas las transiciones de estado de los equipos (DISPONIBLE -> RESERVADO) y solicitudes se cumplen (RN-06).
