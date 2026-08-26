# PréstamoLab CTMA

Prototipo educativo Android para consultar un catálogo simulado de equipos
y herramientas de formación, registrar solicitudes de préstamo y hacer
seguimiento a su estado. Desarrollado como caso integrador de Scrum,
desarrollo móvil Android y pruebas de software (programa ADSO, CTMA).

## Propósito y alcance

La app permite:

- Consultar el catálogo de equipos con su disponibilidad.
- Ver el detalle de un equipo y solicitarlo si está DISPONIBLE.
- Registrar una solicitud de préstamo con ambiente/destino, propósito
  (10 a 180 caracteres) y duración estimada (1 a 8 horas).
- Consultar "Mis solicitudes" y el detalle de cada una.
- Cancelar una solicitud mientras esté en estado SOLICITADA.

No reemplaza ningún sistema institucional real, no maneja inventario
oficial y todos los datos del catálogo son sintéticos: no debe usarse
con información personal real.

## Instalación y ejecución

1. Abrir la carpeta del proyecto en Android Studio (Koala o superior).
2. Dejar que Gradle sincronice las dependencias (requiere conexión a
   internet la primera vez).
3. Ejecutar la configuración `app` sobre un emulador o dispositivo con
   Android 7.0 (API 24) o superior.

También puede compilarse por línea de comandos:

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
```

## Arquitectura

La app sigue flujo unidireccional (UDF):

```
UI (Compose) --eventos--> ViewModel --> Repository --> InMemory data
UI (Compose) <--UiState-- ViewModel
```

- `model/`: `Equipo`, `SolicitudPrestamo`, enumeraciones de estado y las
  reglas de negocio desacopladas (`Validaciones.kt`).
- `data/repository/`: contrato `PrestamoRepository` y su implementación
  `InMemoryPrestamoRepository`, fuente única de verdad durante la
  ejecución.
- `viewmodel/`: `PrestamoViewModel` expone `PrestamoUiState` de solo
  lectura vía `StateFlow` y coordina las acciones de pantalla.
- `ui/`: pantallas Compose (Catálogo, Detalle de equipo, Solicitar,
  Mis solicitudes, Detalle de solicitud).
- `navigation/`: `PrestamoNavGraph`, que transporta `equipoId` y
  `solicitudId` en lugar de objetos completos.

## Navegación

```
Catalogo -> EquipoDetalle/{equipoId} -> Solicitar/{equipoId}
MisSolicitudes -> SolicitudDetalle/{solicitudId}
```

Un identificador inexistente no cierra la app: la pantalla de destino
recibe `null` y muestra un mensaje recuperable.

## Reglas de negocio implementadas

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

## Pruebas

`app/src/test/.../ValidacionesTest.kt` contiene pruebas unitarias
(JUnit) sobre los límites de propósito (9/10/180/181), duración
(0/1/8/9) y disponibilidad del equipo (RN-01). El plan y la suite
manual completa (16+ casos, matriz de riesgos, trazabilidad,
bitácora de ejecución y defectos) están en el informe entregado junto
con este repositorio.

## Integración continua

`.github/workflows/android-ci.yml` compila el proyecto, ejecuta las
pruebas unitarias y Android Lint, y conserva el APK y el reporte de
Lint como artefactos en cada ejecución (ver documento "Herramientas
para Producto PréstamoLab CTMA").

## Limitaciones conocidas

- No hay persistencia real: los datos se pierden al cerrar la app
  (Repository en memoria, punto 11 del alcance mínimo).
- Los estados APROBADA, ENTREGADA y DEVUELTA están modelados pero no
  tienen una transición disponible desde la UI en este primer
  incremento; quedan como base para un siguiente Sprint.
- No hay autenticación real de usuarios ni control de roles.

## Uso de inteligencia artificial

Se utilizó IA como apoyo para redactar y revisar código base y
documentación del proyecto, siguiendo la guía "Uso responsable de
inteligencia artificial" del curso. Toda sugerencia fue comprendida,
adaptada y verificada por el equipo antes de incorporarse; ningún
resultado de prueba fue inventado.
