# Playlist Management API

API RESTful para gestión de listas de reproducción con autenticación JWT y integración con Spotify para géneros musicales.

## Tecnologías utilizadas

- Java 21
- Spring Boot 3.5.0
- Spring Security con JWT
- Spring Data JPA
- H2 Database
- JUnit 5 para pruebas unitarias
- WebClient para integración con Spotify API

## Estructura del proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── canciones/
│   │           ├── config/                  # Configuraciones
│   │           ├── controller/              # Controladores REST
│   │           ├── dto/                     # Data Transfer Objects
│   │           ├── exception/               # Manejo de excepciones
│   │           ├── model/                   # Entidades JPA
│   │           ├── repository/              # Repositorios JPA
│   │           ├── security/                # Configuración de seguridad y JWT
│   │           ├── service/                 # Lógica de negocio
│   │           └── DemoApplication.java     # Clase principal
│   └── resources/
│       └── application.properties           # Configuración de la aplicación
└── test/
    └── java/
        └── com/
            └── canciones/
                ├── controller/              # Pruebas de controladores
                └── service/                 # Pruebas de servicios
```

## Endpoints de la API

### Autenticación

- `POST /api/auth/register` - Registrar un nuevo usuario
- `POST /api/auth/login` - Autenticar un usuario y obtener token JWT

### Listas de reproducción

- `GET /lists` - Ver todas las listas de reproducción
- `GET /lists/{listName}` - Ver una lista de reproducción específica
- `POST /lists` - Crear una nueva lista de reproducción
- `DELETE /lists/{listName}` - Eliminar una lista de reproducción

## Cómo probar la API

1. Ejecuta la aplicación Spring Boot con: `mvn spring-boot:run`
2. Accede a la consola H2 en: http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:mem:playlistdb`
   - Usuario: `sa`
   - Contraseña: `password`

3. Registra un usuario:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "usuario1", "password": "password", "email": "usuario1@example.com"}'
```

4. Inicia sesión para obtener un token JWT:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "usuario1", "password": "password"}'
```

5. Crea una lista de reproducción:
```bash
curl -X POST http://localhost:8080/lists \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer [TU_TOKEN_JWT]" \
  -d '{
    "nombre": "Lista 1",
    "descripcion": "Lista de canciones de Spotify",
    "canciones": [
      {
        "titulo": "Bohemian Rhapsody",
        "artista": "Queen",
        "album": "A Night at the Opera",
        "anno": "1975",
        "genero": "rock"
      }
    ]
  }'
```

6. Obtén todas las listas de reproducción:
```bash
curl -X GET http://localhost:8080/lists \
  -H "Authorization: Bearer [TU_TOKEN_JWT]"
```

7. Obtén una lista de reproducción específica:
```bash
curl -X GET http://localhost:8080/lists/Lista%201 \
  -H "Authorization: Bearer [TU_TOKEN_JWT]"
```

8. Elimina una lista de reproducción:
```bash
curl -X DELETE http://localhost:8080/lists/Lista%201 \
  -H "Authorization: Bearer [TU_TOKEN_JWT]"
```

## Notas

- La aplicación incluye un usuario administrador por defecto:
  - Usuario: `admin`
  - Contraseña: `password`
  
- Para la integración con Spotify, se utiliza un token temporal configurado en las propiedades de la aplicación. En un entorno de producción, se debería implementar el flujo OAuth completo.
