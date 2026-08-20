# PréstamoLab CTMA

Aplicación académica para la gestión de préstamos de equipos y herramientas en ambientes de aprendizaje.

## Estructura del Proyecto

- `model/`: Contiene las clases de datos (`Equipo`, `SolicitudPrestamo`) y los enums de estado.
- `data/repository/`: Implementación del patrón Repository para el acceso a datos (en este caso, una base de datos en memoria).
- `ui/viewmodel/`: Lógica de presentación que conecta la UI con los datos, manejando el estado de la aplicación.
- `ui/screens/`: Pantallas de la aplicación desarrolladas con Jetpack Compose.
- `ui/navigation/`: Configuración de la navegación entre pantallas.

## Reglas de Negocio Implementadas

- **RN-01:** Solo equipos disponibles pueden ser solicitados.
- **RN-02-04:** Validaciones de ambiente, duración (1-8h) y propósito (10-180 chars).
- **RN-05:** Protección contra doble pulsación en el botón guardar.
- **RN-06:** Reserva automática del equipo al crear la solicitud.
- **RN-07:** Cancelación permitida solo para solicitudes pendientes.

## Tecnologías Utilizadas

- Kotlin
- Jetpack Compose (Material 3)
- ViewModel & StateFlow
- Navigation Compose
# Prestamo_Lab_CTMA
