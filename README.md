# Citas Médicas - Spring Boot Demo

Este proyecto es un backend para una aplicación de citas médicas. Está construido con **Spring Boot**, y ofrece funcionalidades básicas como autenticación con JWT, gestión de citas, y validación de horarios.

## Tecnologías utilizadas

- Java 17
- Spring Boot
- Spring Data JPA
- MySQL
- JWT (Json Web Tokens)
- Maven

## Características principales

- Registro y autenticación de usuarios con JWT.
- Gestión de citas médicas (crear, confirmar, cancelar, actualizar).
- Validaciones de horario, disponibilidad de médicos y reglas de negocio.
- Patrón Strategy para cambios de cita flexibles.
- hashing de contraseñas con BCcrypt


---

## 🧪 Endpoints principales

| Método | Ruta                  | Descripción               |
|--------|-----------------------|---------------------------|
| POST   | `/auth/login`         | Autenticación con JWT     |
| POST   | `/citas/reservar`     | Reservar una cita         |
| GET    | `/citas`              | Obtener citas del usuario |
| PUT    | `/citas/{id}`         | Actualizar una cita       |
| PUT    | `/citas/{id}/estado`  | Cambiar estado de la cita |
| DELETE | `/citas/{id}`         | Cancelar una cita         |

---

## 📄 Diagrama de clases (UML)

Este diagrama resume las principales entidades del sistema y sus relaciones:

![Diagrama UML](docs/Editor%20_%20Mermaid%20Chart-2025-06-19-165728.png)


- `Usuario` tiene una relación uno a uno con `Paciente` o `Doctor`, definidos por su `Rol`.
- `Doctor` crea muchas `Cita` y tiene muchos `HorarioDisponible`.
- `Paciente` solicita muchas `Cita`.
- `Cita` tiene estado (`EstadoCita`) y está relacionada a un `Doctor` y un `Paciente`.
- `PasswordResetToken` y `EmailVerificationCode` están vinculados al `Usuario`.

---


