# Stack Overflow Clone API

A RESTful API built with Spring Boot that implements core features of Stack Overflow, including user authentication, question and answer management.

## Technologies Used

- Java 17
- Spring Boot 3.x
- MySQL
- Docker
- JWT Authentication
- Swagger/OpenAPI
- Flyway Migration
- Lombok

## Prerequisites

- Docker and Docker Compose
- Java 17 or higher
- Maven/Gradle (optional, if running without Docker)

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/yourusername/stackoverflow-clone.git
cd stackoverflow-clone
```

### Running with Docker

1. Build and start the containers:
```bash
docker-compose up --build
```

2. Stop the containers:
```bash
docker-compose down
```

3. Stop and remove all containers, networks, and volumes:
```bash
docker-compose down -v
```

4. View logs:
```bash
docker-compose logs -f
```

5. Rebuild a specific service:
```bash
docker-compose up -d --build app
```

### API Documentation

Once the application is running, you can access the Swagger UI documentation at:

http://localhost:8080/api/swagger-ui.html

The OpenAPI specification is available at:
http://localhost:8080/api/api-docs


## API Authentication

The API uses JWT tokens for authentication. To use protected endpoints:

1. Register a new user:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "your_username",
    "password": "your_password",
    "email": "your_email@example.com"
  }'
```

2. Login to get a token:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "your_username",
    "password": "your_password"
  }'
```

3. Use the token in subsequent requests:
```bash
curl -X POST http://localhost:8080/api/questions \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Your Question Title",
    "description": "Your Question Description"
  }'
```

## Main Features

- User Authentication (Register/Login)
- JWT Token Based Security
- Question Management (Create, Read, Update, Delete)
- Answer Management (Create, Read, Update, Delete)
- Error Handling with Custom Responses
- Database Migration with Flyway
- API Documentation with Swagger/OpenAPI


## Database Migrations

Migrations are handled automatically by Flyway when the application starts. Migration files are located in:

src/main/resources/db/migration/

## Environment Variables

The following environment variables can be configured in your `.env` file:

```env
MYSQL_DATABASE=stackoverflow
MYSQL_USER=user
MYSQL_PASSWORD=password
MYSQL_ROOT_PASSWORD=rootpassword
JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=86400000
```

## Error Handling

The API uses a standardized error response format:

```json
{
    "success": false,
    "message": "Error message here",
    "data": null,
    "errorCode": "ERROR_CODE"
}
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

- Spring Boot Documentation
- Stack Overflow API Documentation
- Docker Documentation
