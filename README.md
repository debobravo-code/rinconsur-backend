# Probadores RinconSur – Backend (microservicio)

**Integrantes:** Debora Bravo y Mikela Plama
**Asignatura:** Desarrollo de Aplicaciones Móviles (DSY1105) – Evaluación Parcial 4, Duoc UC

Microservicio REST que guarda los datos de la app Android **Probadores RinconSur** en MySQL.

## Tecnologías

Spring Boot, Java 21, Spring Data JPA (Hibernate), MySQL, BCrypt (`spring-security-crypto`) y Maven.

## Estructura (`src/main/java/com/rinconsur/rinconsur_backend/`)

- `controller/`: endpoints REST.
- `model/`: entidades (tablas de MySQL).
- `repository/`: acceso a la base de datos.
- `service/`: lógica de fotos y registros de ocupación.
- `dto/`: cuerpos de las peticiones.
- `exception/`: respuestas de error (400, 401, 404, 409).

## Base de datos

El script `database/rinconsur_db.sql` crea la base `rinconsur_db`, las 7 tablas y los datos de ejemplo. **No borra nada**: crea solo lo que falta y agrega los datos de ejemplo únicamente si no existen.

Tablas: `tipo_usuarios`, `users`, `sedes`, `probadores`, `registros`, `reportes_prenda` y `usuario_sede` (sedes asignadas a cada usuario).

## Cómo ejecutar

1. Abrir **Laragon** e iniciar MySQL.
2. Ejecutar `database/rinconsur_db.sql` (HeidiSQL o MySQL Workbench).
3. Revisar `src/main/resources/application.properties`. Por defecto usa `jdbc:mysql://localhost:3306/rinconsur_db`, usuario `root` y clave vacía. Se puede cambiar con las variables de entorno `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`.
4. Iniciar el backend: `./mvnw spring-boot:run` (en Windows `mvnw.cmd spring-boot:run`) o ejecutar `RinconsurBackendApplication` desde el IDE.
5. Queda disponible en `http://localhost:8080`.

Las fotos de los reportes se guardan en la carpeta `uploads/reportes` (se puede cambiar con la variable `UPLOAD_DIR`).

## Endpoints

| Recurso | Endpoints |
|---|---|
| Login | `POST /api/auth/login` |
| Sedes | `GET /api/sedes`, `GET /api/sedes/{id}`, `POST /api/sedes`, `PUT /api/sedes/{id}`, `DELETE /api/sedes/{id}` |
| Probadores | `GET /api/probadores`, `GET /api/probadores/{id}`, `GET /api/probadores/sede/{sedeId}`, `GET /api/probadores/sede/{sedeId}/estado/{estado}`, `POST /api/probadores`, `PUT /api/probadores/{id}`, `DELETE /api/probadores/{id}`, `PATCH /api/probadores/{id}/ocupar`, `PATCH /api/probadores/{id}/liberar`, `PATCH /api/probadores/{id}/estado` |
| Registros de ocupación | `GET /api/registros`, `GET /api/registros/{id}`, `GET /api/registros/sede/{sedeId}`, `GET /api/registros/sede/{sedeId}/probador/{numero}`, `GET /api/registros/sede/{sedeId}/prenda-olvidada`, `GET /api/registros/sede/{sedeId}/fechas`, `PUT /api/registros/{id}` |
| Reportes de prenda | `GET /api/reportes-prenda`, `GET /api/reportes-prenda/{id}`, `GET /api/reportes-prenda/probador/{probadorId}`, `GET /api/reportes-prenda/fechas`, `POST /api/reportes-prenda`, `PUT /api/reportes-prenda/{id}`, `DELETE /api/reportes-prenda/{id}` |
| Fotos | `POST /api/fotos` (multipart, campo `archivo`), `GET /api/fotos/{nombreArchivo}` |
| Usuarios | `GET /api/users`, `GET /api/users/{id}`, `POST /api/users`, `PUT /api/users/{id}`, `DELETE /api/users/{id}`, `GET /api/users/{id}/sedes`, `PUT /api/users/{id}/sedes` |
| Tipos de usuario | `GET /api/tipos-usuario`, `GET /api/tipos-usuario/{id}`, `POST /api/tipos-usuario`, `PUT /api/tipos-usuario/{id}`, `DELETE /api/tipos-usuario/{id}` |

## Pruebas

`./mvnw test` ejecuta `RinconsurBackendApplicationTests.contextLoads`, que necesita MySQL encendido y la base creada.

## App Android

El proyecto de la app (Kotlin + Compose + Room + Retrofit) está en el repositorio del frontend.
