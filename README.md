# PréstamoLab CTMA

**PréstamoLab CTMA** es un prototipo educativo diseñado para la gestión de préstamos de equipos y herramientas dentro de un ambiente de aprendizaje (SENA). Esta aplicación permite a los usuarios consultar un catálogo, realizar solicitudes de préstamo y gestionar sus solicitudes activas.

## 🏗️ Arquitectura del Proyecto

La aplicación sigue una arquitectura limpia con separación de responsabilidades, facilitando su mantenimiento y comprensión:

*   **Model (`model/`)**: Define las entidades del dominio (`Equipo`, `SolicitudPrestamo`) y sus estados (`Estados.kt`). Son clases de datos puras sin lógica de negocio compleja.
*   **Data/Repository (`data/repository/`)**: Implementa el patrón Repository. `PrestamoRepository` define las operaciones, mientras que `InMemoryPrestamoRepository` almacena los datos en memoria durante la ejecución del programa. No se requiere base de datos externa.
*   **Lógica de Presentación (`ui/viewmodel/`)**: 
    *   **ViewModel**: Gestiona el estado de la UI y coordina con el repositorio.
    *   **UiState**: Representa el estado actual de la pantalla de forma reactiva mediante `StateFlow`.
*   **UI (`ui/`)**: Desarrollada íntegramente con **Jetpack Compose** y **Material 3**. Se encarga únicamente de mostrar los datos del `UiState` y enviar eventos al ViewModel.

## 📋 Reglas de Negocio (RN) Implementadas

1.  **RN-01**: Solo se permite solicitar equipos en estado **DISPONIBLE**.
2.  **RN-02**: El campo **Ambiente o Destino** es obligatorio en el formulario.
3.  **RN-03**: El **Propósito** de la solicitud debe tener entre 10 y 180 caracteres.
4.  **RN-04**: La **Duración** estimada debe estar entre 1 y 8 horas.
5.  **RN-05**: Protección contra **Doble Guardado**. Se deshabilita el botón de guardado mientras la operación está en curso.
6.  **RN-06**: Al crear una solicitud exitosa, el equipo cambia automáticamente a estado **RESERVADO**.
7.  **RN-07**: Solo las solicitudes en estado **SOLICITADA** pueden ser canceladas por el usuario.
8.  **RN-08**: Manejo robusto de **IDs inexistentes** para evitar cierres inesperados (crash).

## 🚀 Tecnologías Utilizadas

*   **Lenguaje**: Kotlin
*   **Interfaz de Usuario**: Jetpack Compose (Material 3)
*   **Arquitectura**: MVVM (Model-View-ViewModel) + Repository
*   **Navegación**: Navigation Compose (paso de parámetros por ID)
*   **Manejo de Estado**: StateFlow y LiveData (vía UI State)

## 🧪 Escenarios de Prueba (MVP)

La aplicación está preparada para verificar:
- Carga de datos sintéticos en el catálogo.
- Validaciones de campos en el formulario (valores límite: 9, 10, 180, 181 caracteres).
- Transición de estados: `DISPONIBLE` -> `RESERVADO` (al solicitar) y `RESERVADO` -> `DISPONIBLE` (al cancelar).
- Navegación fluida y manejo del Back Stack.

---
*Este proyecto es de carácter académico para el SENA.*
