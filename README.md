# Taller de Citas — TDD y BDD

Aplicación de referencia del curso 2424 (Pruebas de Software, Cibertec) para el tema T1: TDD con JUnit 5 y Mockito, BDD con Cucumber. El dominio es la gestión de citas de un taller mecánico: agendar, cancelar y buscar mecánicos disponibles, con reglas de negocio que se prueban de forma aislada, sin base de datos ni contexto de Spring.

## Requisitos

- Java 21
- Maven (se incluye el wrapper `./mvnw`)
- MySQL en local o accesible por red (solo para levantar la aplicación; los tests no lo necesitan)

## Configuración de la base de datos

Las credenciales se leen desde un archivo `.env` con [dotenv-java](https://github.com/cdimascio/dotenv-java). Copia el ejemplo y ajústalo:

```bash
cp .env.example .env
```

El `.env` queda fuera del repositorio (está en `.gitignore`). Contenido esperado:

```
DB_URL=jdbc:mysql://localhost:3306/taller_db?createDatabaseIfNotExist=true
DB_USERNAME=root
DB_PASSWORD=tu_password
```

El parámetro `createDatabaseIfNotExist=true` crea el esquema si aún no existe, y `spring.jpa.hibernate.ddl-auto=update` genera las tablas en el primer arranque. La data de inicio (4 mecánicos y 2 citas de ejemplo) se carga desde `src/main/resources/data.sql`.

## Cómo levantar la aplicación

```bash
./mvnw spring-boot:run
```

Queda escuchando en `http://localhost:8080`.

## Cómo ejecutar los tests

```bash
./mvnw clean verify
```

Corre los tests unitarios (`ServicioCitasImplTest`) y los escenarios de Cucumber (`EjecutorPruebasCucumber`). No requiere MySQL: los repositorios se mockean.

Los tests unitarios y los escenarios Gherkin están como plantilla con `TODO`: completarlos es parte del trabajo del curso. La cobertura esperada es un test por regla de negocio y los 4 escenarios indicados en el `.feature`.

## Reglas de negocio

| Regla | Descripción | Resultado si falla |
|-------|-------------|--------------------|
| Mecánico existente | El mecánico debe existir | `MecanicoNoEncontradoException` (404) |
| Especialidad | La especialidad del mecánico coincide con el tipo de servicio | `EspecialidadIncorrectaException` (400) |
| Horario de servicios pesados | Reparación de motor y de transmisión solo entre 08:00 y antes de las 12:00 | `HorarioNoPermitidoException` (400) |
| Fecha futura | La fecha de la cita debe ser posterior al momento actual | `FechaInvalidaException` (400) |
| Sin superposición | El mecánico no debe tener otra cita programada que se cruce ese día (una cita que empieza justo cuando otra termina sí es válida) | `HorarioOcupadoException` (409) |
| Cancelación | Solo se cancelan citas en estado `PROGRAMADA` | `CitaNoCancelableException` (409) |
| Penalidad | Cancelar con menos de 24 horas de anticipación cuesta S/ 50.00; con 24 horas o más, S/ 0.00 | — |
| Disponibilidad | Se retorna el primer mecánico de la especialidad sin superposición | `SinDisponibilidadException` (400) |

Duraciones por tipo de servicio: mantenimiento ligero 2 h, cambio de aceite 1 h, reparación de motor 4 h, reparación de transmisión 4 h.

## Flujo de ejemplo con curl

```bash
# 1. Listar mecánicos
curl http://localhost:8080/api/mecanicos

# 2. Agendar una cita (201)
curl -X POST http://localhost:8080/api/citas \
  -H "Content-Type: application/json" \
  -d '{"idMecanico":2,"placaVehiculo":"AAA-111","tipoServicio":"CAMBIO_ACEITE","fechaHoraInicio":"2026-08-02T14:00:00"}'

# 3. Provocar una superposición (409)
curl -X POST http://localhost:8080/api/citas \
  -H "Content-Type: application/json" \
  -d '{"idMecanico":2,"placaVehiculo":"BBB-222","tipoServicio":"CAMBIO_ACEITE","fechaHoraInicio":"2026-08-02T14:00:00"}'

# 4. Consultar disponibilidad
curl "http://localhost:8080/api/citas/disponibilidad?tipoServicio=REPARACION_MOTOR&fechaHoraInicio=2026-08-02T09:00:00"

# 5. Cancelar una cita (200)
curl -X DELETE http://localhost:8080/api/citas/1
```

## Swagger

Documentación interactiva de la API en:

```
http://localhost:8080/swagger-ui.html
```
