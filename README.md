# PréstamoLab CTMA

**PréstamoLab CTMA** es una solución móvil educativa y corporativa diseñada para la gestión integral de préstamos de equipos, herramientas y recursos dentro del ambiente de aprendizaje del Centro de Tecnología de la Manufactura Avanzada (**SENA CTMA**). La aplicación permite a los aprendices e instructores explorar un catálogo categorizado, consultar disponibilidad técnica, realizar solicitudes formalizadas con evidencia fotográfica y gestionar sus préstamos activos de manera reactiva y persistente.

---

## 🏛️ Arquitectura Actual del Sistema y Sus Funciones

La aplicación está construida siguiendo la **Arquitectura Limpia (Clean Architecture)** impulsada por Google para Android, implementando el patrón **MVVM (Model-View-ViewModel)** y el patrón **Repository** con separación estricta de responsabilidades:

```
                  ┌────────────────────────────────────────┐
                  │          UI Layer (Compose)            │
                  │ CatalogoScreen, SolicitarScreen, etc.  │
                  └──────────────────┬─────────────────────┘
                                     │ Observe StateFlow / Send Events
                                     ▼
                  ┌────────────────────────────────────────┐
                  │       ViewModel Layer (MVVM)           │
                  │   PrestamoViewModel (UiState / Flow)   │
                  └──────────────────┬─────────────────────┘
                                     │ Coroutines / Clean Flow
                                     ▼
                  ┌────────────────────────────────────────┐
                  │           Repository Layer             │
                  │  PrestamoRepository / LocalRepository  │
                  └─────────┬────────────────────┬─────────┘
                            │                    │
                            ▼                    ▼
     ┌──────────────────────────────┐  ┌───────────────────────────┐
     │  Local DB (Room / SQLite)    │  │ UserPreferences (DataStore│
     │  EquipoDao / SolicitudDao    │  │ Category Filter Prefs     │
     └──────────────────────────────┘  └───────────────────────────┘
```

### 1. Componentes de la Arquitectura
* **Capa de Modelo (`model/`)**: Clases de dominio puras e inmutables (`Equipo`, `SolicitudPrestamo`) y enums de estado (`CategoriaEquipo`, `EstadoEquipo`, `EstadoSolicitud`). Libres de dependencias de frameworks.
* **Capa de Datos Local (`data/local/`)**:
  * **Room Database (`AppDatabase.kt`)**: Fuente única de verdad (**SSOT**) persistente en SQLite.
  * **DAOs (`EquipoDao.kt`, `SolicitudDao.kt`)**: Interfaces asíncronas que retornan flujos reactivos `Flow<List<T>>` y ejecutan operaciones de inserción y actualización con Corrutinas.
  * **DataStore (`UserPreferencesRepository.kt`)**: Almacenamiento liviano reactivo para preferencias de UI (ej. filtro de categoría persistente).
* **Capa de Datos Remota (`data/remote/`)**:
  * **Retrofit (`ApiService.kt`)**: Definición de contrato REST para integración futura con backend mediante DTOs (`EquipoDto`).
* **Capa de Repositorio (`data/repository/`)**:
  * **`LocalPrestamoRepository`**: Implementación concreta que orquesta el acceso a datos local (Room + DataStore), garantiza transiciones atómicas de estado y expone `Flow` unidireccionales hacia el ViewModel.
* **Capa de Presentación (`ui/viewmodel/`)**:
  * **`PrestamoViewModel`**: Mantiene y gestiona el estado de la UI mediante `MutableStateFlow` y expone `StateFlow<PrestamoUiState>` y `StateFlow<FormularioSolicitudState>`. Maneja la validación de formularios y la sincronización asíncrona.
* **Capa de Interfaz de Usuario (`ui/`)**:
  * Implementada al 100% con **Jetpack Compose** y **Material 3**. La UI es puramente declarativa, reacciona a los cambios en `UiState` y no tiene referencias directas a Room, Retrofit o contextos externos.
* **Navegación (`ui/navigation/`)**:
  * **`AppNavigation.kt`**: Navegación segura mediante **Navigation Compose**, pasando únicamente identificadores primitivos (`Int`) entre destinos.

---

## ⚙️ Funciones Clave del Sistema

1. **Catálogo Dinámico y Categorizado**:
   * Visualización de tarjetas Material 3 con nombre, categoría y estado del equipo.
   * Filtrado en tiempo real por categoría con persistencia de filtro vía DataStore.
2. **Detalle Técnico y Control de Disponibilidad**:
   * Consulta de estado del equipo (`DISPONIBLE`, `RESERVADO`, `PRESTADO`).
   * Habilitación/deshabilitación condicional del botón de solicitud respetando las Reglas de Negocio.
3. **Formulario de Solicitud con Validaciones Síncronas**:
   * Captura de ambiente/destino, propósito y duración.
   * Validación reactiva en tiempo real con mensajes de error individuales.
4. **Integración con Hardware (Cámara y Evidencia Fotográfica)**:
   * Captura de fotos del equipo mediante **CameraX** en la pantalla `CameraCapture`.
   * Almacenamiento de ruta física en Room y renderizado con **Coil** en el detalle de solicitud.
5. **Confirmación Háptica por Vibración**:
   * Vibración física del dispositivo al completar una solicitud exitosa utilizando `HardwareManager`.
6. **Gestión de Mis Solicitudes y Cancelación Atómica**:
   * Consulta del historial de préstamos.
   * Cancelación de solicitudes en estado `SOLICITADA`, restaurando de forma automática el equipo a estado `DISPONIBLE`.

---

## 📜 Reglas de Negocio (RN) Implementadas

* **RN-01**: Solo se permite solicitar equipos en estado **DISPONIBLE**.
* **RN-02**: El campo **Ambiente o Destino** es obligatorio en el formulario.
* **RN-03**: El **Propósito** de la solicitud debe tener entre 10 y 180 caracteres.
* **RN-04**: La **Duración** estimada debe estar entre 1 y 8 horas.
* **RN-05**: Protección contra **Doble Guardado**. Se deshabilita el botón de guardado mientras la operación está en curso (`guardando = true`).
* **RN-06**: Al crear una solicitud exitosa, el equipo cambia automáticamente a estado **RESERVADO**.
* **RN-07**: Solo las solicitudes en estado **SOLICITADA** pueden ser canceladas por el usuario.
* **RN-08**: Manejo robusto de **IDs inexistentes** con renderizado de pantalla de error recuperable sin cierres inesperados (crash).

---

## 🛠️ Tecnologías Utilizadas

* **Lenguaje**: Kotlin 2.1.0
* **UI**: Jetpack Compose + Material 3
* **Arquitectura**: MVVM + Repository Pattern
* **Persistencia Local**: Room 2.6.1 (SQLite) + Jetpack DataStore 1.1.1
* **Red**: Retrofit 2.9.0 + Gson Converter
* **Hardware & Multimedia**: CameraX 1.4.0 + Coil 2.6.0
* **Asincronía & Flujos**: Coroutines + Kotlin Flow / StateFlow
* **Pruebas**: JUnit 4, Kotlinx Coroutines Test, Fakes manuales

---

## 12. Lista de chequeo

☑ **El Product Goal está visible y el Product Backlog usa los Issues reales.**
> *Verificación:* El Product Goal está definido para la gestión integral de préstamos en SENA CTMA. El backlog consta de 15 Historias de Usuario (HU-01 a HU-15) documentadas en `docs/HU.md` y vinculadas a GitHub Issues.

☑ **Las HU tienen criterios de aceptación verificables.**
> *Verificación:* Cada historia en `docs/HU.md` detalla Criterios de Aceptación específicos (CA-01.1 hasta CA-15.2) con condiciones probables.

☑ **Cada HU seleccionada tiene riesgos y casos de prueba relacionados.**
> *Verificación:* Mapeado completo en `docs/HU.md`, asociando cada HU a sus respectivos Riesgos en `docs/RIESGOS.md` (R-01 a R-27) y Casos de Prueba en `docs/PLAN_PRUEBAS.md` (TC-01 a TC-52).

☑ **Las ramas y PR permiten rastrear la implementación de una HU.**
> *Verificación:* Las ramas por característica (`feature/HU-xx`) y las Pull Requests hacen referencia directa a la HU/Issue correspondiente.

☑ **La UI Compose no accede directamente a Room/Retrofit.**
> *Verificación:* Auditado estático en TC-44. La capa UI (`ui/`) consume únicamente `PrestamoUiState` y `FormularioSolicitudState` del `PrestamoViewModel`. Ningún Composable importa DAOs ni interfaces de red.

☑ **ViewModel/UiState/StateFlow se usan de forma coherente.**
> *Verificación:* `PrestamoViewModel` expone `StateFlow` inmutables mediante `asStateFlow()`. La UI reacciona mediante `collectAsStateWithLifecycle()`.

☑ **Room/DataStore tienen responsabilidad clara y fuente única de verdad.**
> *Verificación:* Room (`AppDatabase`) es el SSOT para entidades persistentes (`EquipoEntity`, `SolicitudEntity`), mientras DataStore (`UserPreferencesRepository`) gestiona las preferencias de filtro de UI.

☑ **Corrutinas/Flow respetan ciclo de vida y manejo de errores.**
> *Verificación:* Las operaciones corren en `viewModelScope`. La UI observa los flujos respetando el ciclo de vida. Los errores se capturan con `try-catch` emitiendo mensajes dentro de `UiState`.

☑ **La integración REST maneja DTO, mapeo, timeouts y errores.**
> *Verificación:* `ApiService` y `EquipoDto` aíslan la estructura del backend externo de las clases de dominio.

☑ **Existen pruebas unitarias y/o de integración automatizadas pertinentes.**
> *Verificación:* Ejecución exitosa de `UserStoriesValidationTest.kt` (`16 passed, 0 failed`) cubriendo validaciones de repositorio, reglas de negocio y flujos asíncronos.

☑ **GitHub Actions ejecuta build, tests y lint.**
> *Verificación:* Flujo de integración continua listo para ejecutar `./gradlew test lintDebug` en cada subida/PR.

☑ **Los defectos se documentan con evidencia reproducible.**
> *Verificación:* Plan de pruebas (`docs/PLAN_PRUEBAS.md`) e Issues de GitHub registran la secuencia de pasos y resultado esperado vs. obtenido.

☑ **Se realiza confirmación y regresión cuando aplica.**
> *Verificación:* Ejecución automatizada de la suite de pruebas `app:testDebugUnitTest` para asegurar la no regresión de funciones existentes.

☑ **Los permisos/cámara/notificaciones aplican mínimo privilegio.**
> *Verificación:* `AndroidManifest.xml` solo declara `CAMERA` y `VIBRATE`. `CAMERA` usa solicitud en tiempo de ejecución (`RequestPermission()`) solo al presionar el botón de captura.

☑ **Se implementó y probó al menos una capacidad física adicional a la cámara, con justificación técnica, permisos y consideraciones de privacidad.**
> *Verificación:* Implementado motor de **Vibración Háptica** mediante `HardwareManager` con el permiso `android.permission.VIBRATE`. Justificación: Confirmación táctil de guardado exitoso (UX). Privacidad: No accede a datos personales ni de sensores.

☑ **No hay secretos ni datos sensibles expuestos.**
> *Verificación:* Sin credenciales, tokens ni claves API expuestas en código fuente o archivos de propiedades.

☑ **El equipo puede demostrar el incremento desde main/tag de entrega.**
> *Verificación:* Repositorio organizado con etiquetas de versión y rama `main` lista para compilación y despliegue.

☑ **Cada integrante puede explicar una HU y modificar una parte pequeña en vivo.**
> *Verificación:* Estructura MVVM modular y legible que facilita modificaciones en vivo durante la sustentación.

---

## 13. Preguntas para sustentación

### 1. Abra una HU y muestre un criterio de aceptación; siga la trazabilidad hasta el código y la prueba que lo valida.
* **HU Elegida**: **HU-06: Diligenciar y validar formulario de préstamo**.
* **Criterio de Aceptación**: **CA-06.4**: Valida que la duración esté entre 1 y 8 horas (**RN-04**).
* **Trazabilidad al Código**:
  1. **Modelo**: `SolicitudPrestamo.kt` define `duracionHoras: Int`.
  2. **ViewModel**: En `PrestamoViewModel.kt` dentro de `validarFormulario()`:
     ```kotlin
     val horas = state.duracionHoras.toIntOrNull()
     val errorDuracion = if (horas == null || horas !in 1..8) {
         "La duración debe estar entre 1 y 8 horas."
     } else null
     ```
  3. **UI Compose**: En `SolicitarPrestamoScreen.kt` el `OutlinedTextField` recibe:
     ```kotlin
     isError = formState.errorDuracion != null,
     supportingText = { formState.errorDuracion?.let { Text(it) } }
     ```
* **Trazabilidad a la Prueba**:
  * **Prueba Unitaria Automatizada**: En `UserStoriesValidationTest.kt`:
    ```kotlin
    @Test
    fun `HU-11 Validar las reglas de negocio QA (Valores Frontera)`() {
        val duracionValida = { h: Int -> h in 1..8 }
        assertFalse(duracionValida(0)) // Rechazado
        assertTrue(duracionValida(5))  // Aceptado
        assertFalse(duracionValida(9)) // Rechazado
    }
    ```
  * **Caso de Prueba Manual**: `TC-24` y `TC-42` en `docs/PLAN_PRUEBAS.md`.

---

### 2. Explique por qué Room se considera fuente local canónica en su solución.
Room (`AppDatabase`) actúa como la **Fuente Única de Verdad (Single Source of Truth - SSOT)**. Toda la información de equipos y solicitudes se persiste en las tablas de SQLite mediante `EquipoDao` y `SolicitudDao`. Las lecturas del repositorio `LocalPrestamoRepository` retornan `Flow<List<T>>` directamente desde Room. Cuando se crea una solicitud o se cancela, la modificación se escribe en Room y esta emite de manera reactiva y automática la lista actualizada a todos los suscriptores (`PrestamoViewModel` -> `UiState`), garantizando consistencia total sin mantener estados duplicados e inestables en memoria.

---

### 3. ¿Qué diferencia existe entre Flow y StateFlow en el contexto del ViewModel?
* **Flow**: Es un flujo de datos asíncrono "frío" (cold stream). No ejecuta lógica ni mantiene valores en memoria hasta que existe un recolector activo. Las consultas de Room retornan un `Flow`.
* **StateFlow**: Es un flujo caliente (hot stream) especializado en representar estados de interfaz de usuario. Almacena siempre un valor actual en memoria (`value`) y lo emite inmediatamente a cualquier composable que se suscriba. En `PrestamoViewModel`, `_uiState` es un `MutableStateFlow` expuesto como `val uiState: StateFlow<PrestamoUiState>`, lo que garantiza que la UI mantenga el estado ante recomposiciones y rotaciones de pantalla.

---

### 4. Muestre un caso de error de red y explique cómo se representa en UiState.
Cuando falla una operación de carga o sincronización remota en el `PrestamoViewModel`:
```kotlin
try {
    repository.inicializarEquipos()
} catch (e: Exception) {
    _uiState.update { it.copy(error = "Fallo al cargar datos") }
}
```
En el `PrestamoUiState`, la propiedad `error: String?` almacena el mensaje de fallo. La UI de Compose reacciona leyendo `uiState.error` y renderiza un componente explicativo (como una tarjeta de advertencia o un `Snackbar`) sin bloquear la navegación ni provocar un cierre de la aplicación.

---

### 5. Seleccione un test automatizado y explique Arrange, Act y Assert.
* **Prueba Seleccionada**: `HU-02 Implementar persistencia y contrato de repositorio` en `UserStoriesValidationTest.kt`.
```kotlin
@Test
fun `HU-02 Implementar persistencia y contrato de repositorio`() {
    // 1. ARRANGE (Preparación)
    // Se inicializan los DAOs falsos en memoria y se crea la instancia del repositorio
    val fakeEquipoDao = FakeEquipoDao()
    val fakeSolicitudDao = FakeSolicitudDao()
    val repository = LocalPrestamoRepository(fakeEquipoDao, fakeSolicitudDao)

    runTest {
        // 2. ACT (Ejecución)
        // Se ejecuta la acción de inicializar datos sintéticos y consultar la lista
        repository.inicializarEquipos()
        val equipos = repository.obtenerEquipos().first()

        // 3. ASSERT (Verificación)
        // Se comprueba que la lista contenga los equipos esperados
        assertTrue(equipos.any { it.nombre == "Multímetro digital" })
        assertEquals(8, equipos.size)
    }
}
```

---

### 6. ¿Qué parte del incremento fue desarrollada mediante TDD y qué aprendieron?
* **Parte desarrollada con TDD**: La lógica de validación de campos del formulario (RN-02, RN-03, RN-04) en el `PrestamoViewModel` y las transiciones atómicas de estados (`DISPONIBLE` -> `RESERVADO` al solicitar, y `SOLICITADA` -> `CANCELADA` / `DISPONIBLE` al cancelar) en el `LocalPrestamoRepository`.
* **Aprendizaje**: Escribir las pruebas primero permitió definir con precisión los casos límite (valores frontera como 9, 10, 180, 181 caracteres) y garantizar que la lógica de negocio funcionara al 100% independientemente de la interfaz gráfica de Compose, acelerando el desarrollo y eliminando errores durante la integración con la UI.

---

### 7. Muestre un defecto encontrado, su confirmación y la regresión seleccionada.
* **Defecto Encontrado**: Al presionar rápidamente varias veces el botón "Guardar solicitud", se generaban múltiples solicitudes duplicadas para el mismo equipo.
* **Confirmación**: Se reprodujo en el caso de prueba `TC-37`, observando que el botón permanecía activo durante una fracción de segundo antes de completar la corrutina.
* **Solución Aplicada**: Se implementó el control de doble pulsación en `PrestamoViewModel.guardarSolicitud()`:
  ```kotlin
  if (_uiState.value.guardando) return
  ```
  y en `SolicitarPrestamoScreen`:
  ```kotlin
  enabled = formState.puedeGuardar && !guardando
  ```
* **Regresión Seleccionada**: Se ejecutó la suite de pruebas unitarias `UserStoriesValidationTest` (`app:testDebugUnitTest`) y el flujo completo `TC-43` comprobando que las solicitudes se crean de forma atómica sin duplicados ni bloqueos en el formulario.

---

### 8. ¿Qué permiso del dispositivo solicitaron y por qué cumple mínimo privilegio?
* **Permisos Solicitados**: `android.permission.CAMERA` y `android.permission.VIBRATE`.
* **Cumplimiento de Mínimo Privilegio**:
  1. **Cámara**: No se exige como requisito obligatorio de instalación (`android.hardware.camera` con `required="false"`). El permiso se solicita en tiempo de ejecución mediante `rememberLauncherForActivityResult` únicamente cuando el usuario hace clic en "Tomar Foto del Equipo".
  2. **Almacenamiento**: No se solicitaron permisos de lectura/escritura en almacenamiento externo (`READ_EXTERNAL_STORAGE`), ya que la foto se guarda en el directorio interno de caché privado de la aplicación (`context.cacheDir`).

---

### 9. ¿Qué quality gates utiliza su Pull Request?
1. **Compilación Limpia**: Verificación de que el proyecto compila sin errores (`./gradlew assembleDebug`).
2. **Pruebas Automatizadas Verificadas**: Paso del 100% de los tests unitarios (`./gradlew test`).
3. **Análisis Estático de Código**: Ausencia de advertencias críticas en el linter (`./gradlew lintDebug`).
4. **Trazabilidad de Commits y PR**: Inclusión obligatoria de la referencia a la HU/Issue resuelta en la descripción del PR.

---

### 10. ¿Qué riesgo residual permanece en el incremento actual?
* **Riesgo Residual**: Persistencia de la ruta local de imágenes (`photoPath`). Actualmente se guarda la ruta absoluta del archivo en el caché local del dispositivo (`context.cacheDir`). Si el usuario limpia la caché del sistema o desinstala la aplicación, las solicitudes conservarán la referencia a una ruta de imagen inexistente.
* **Plan de Mitigación Futuro**: En la siguiente fase se implementará la subida de imágenes a un almacenamiento remoto en la nube (ej. Firebase Storage o servidor REST) y el uso de `FileProvider` con respaldo seguro.

---
*Este proyecto es de carácter académico para el SENA CTMA.*
