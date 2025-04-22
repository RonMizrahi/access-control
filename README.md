# Access Control

A Spring Boot application implementing JWT-based authentication and role-based access control, with caching support.

## Features

- **JWT Authentication**: Secure login with JSON Web Tokens.
- **Role-Based Access Control**: Restrict endpoints to users with specific roles (e.g., admin, user).
- **Custom Annotations**: Use `@IAdminRole` and `@IUserRole` to protect controller methods.
- **Caching**: Improve performance with caching mechanisms.
- **RESTful APIs**: Endpoints for login and role inspection.

## Technologies

- Java
- Spring Boot
- Spring Security
- JWT (JSON Web Token)
- Caching (e.g., Spring Cache)
- Maven

## Endpoints

- `POST /auth/login`: Authenticate user and receive a JWT token.
- `GET /auth/admin-roles`: Accessible only by users with admin role.
- `GET /auth/user-roles`: Accessible only by users with user role.

## Getting Started

### Prerequisites

- Java 17+
- Maven

### Setup

1. Clone the repository:
   ```sh
   git clone <repo-url>
   cd access-control
   ```

2. Build the project:
   ```sh
   mvn clean install
   ```

3. Run the application:
   ```sh
   mvn spring-boot:run
   ```

### Usage

1. **Login**  
   Send a POST request to `/auth/login` with JSON body:
   ```json
   {
     "username": "your-username",
     "password": "your-password"
   }
   ```
   The response will include a JWT token.

2. **Access Protected Endpoints**  
   Use the JWT token in the `Authorization: Bearer <token>` header to access `/auth/admin-roles` or `/auth/user-roles` as appropriate.

## License

MIT License
