
# JWT Token Generation in Spring Boot

This project demonstrates how to create a JWT generation endpoint using Spring Boot. It includes key management, JWT creation, and authentication. The application uses PostgreSQL for storing JWT keys and leverages the `jjwt` library for JWT operations.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Setup and Installation](#setup-and-installation)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [JWT Key Management](#jwt-key-management)
- [Authentication Process](#authentication-process)
- [Technologies Used](#technologies-used)
- [Contributing](#contributing)
- [License](#license)
- [Further Reading](#further-reading)

## Prerequisites

- Java 17 or higher
- Maven 3.6.0 or higher
- PostgreSQL 13 or higher
- An IDE such as IntelliJ IDEA

## Setup and Installation

1. **Clone the Repository**

    ```bash
    git clone https://github.com/patzu/jwt_token_gen_springboot.git
    cd jwt_token_gen_springboot
    ```

2. **Database Configuration**

    Ensure PostgreSQL is running and create the database:

    ```sql
    CREATE DATABASE jwtdemodb;
    ```

    Update `application.properties` with your PostgreSQL credentials:

    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/jwtdemodb
    spring.datasource.username=postgres
    spring.datasource.password=yourpassword
    spring.datasource.driver-class-name=org.postgresql.Driver

    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    ```

3. **Build the Project**

    Use Maven to build the project:

    ```bash
    mvn clean install
    ```

## Running the Application

Run the Spring Boot application using the following command:

```bash
mvn spring-boot:run
```

The application will start and be accessible at `http://localhost:8080`.

## API Endpoints

### 1. Login Endpoint

- **URL:** `/login`
- **Method:** `POST`
- **Request Body:**

    ```json
    {
        "username": "user",
        "password": "password"
    }
    ```

- **Response:**

    ```json
    {
        "token": "your.jwt.token.here"
    }
    ```

## JWT Key Management

### Key Generation

The application generates and manages JWT keys using the `JwtKeyService`. The keys are stored in the PostgreSQL database.

### Key Rotation

Keys are rotated periodically using a scheduled task. This enhances security by ensuring that keys are not reused indefinitely.

```java
@Scheduled(fixedDelay = 86400000) // Rotate daily
public void rotateKey() throws NoSuchAlgorithmException {
    SecretKey key = KeyGenerator.getInstance("HmacSHA256").generateKey();
    storeKeyInDatabase(key); // Pseudocode for storing the key securely
}
```

### Key Retrieval

Keys are retrieved and cached on application startup.

```java
@PostConstruct
public void init() throws NoSuchAlgorithmException {
    SecretKey key = retrieveLatestKeyFromDatabase(); // Pseudocode for key retrieval
    cacheKey(key); // Store key in a secure, application-wide cache
}
```

## Authentication Process

### User Authentication

The `/login` endpoint validates user credentials and generates a JWT token if the credentials are correct.

```java
@PostMapping("/login")
public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) throws NoSuchAlgorithmException {
    if ("user".equals(loginRequest.getUsername()) && "password".equals(loginRequest.getPassword())) {
        String secretKeyBase64 = jwtKeyService.getLatestKey();
        byte[] decodedKey = Base64.getDecoder().decode(secretKeyBase64);
        SecretKey key = Keys.hmacShaKeyFor(decodedKey);

        String token = Jwts.builder()
                .setSubject(loginRequest.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day expiration
                .signWith(key).compact();

        return ResponseEntity.ok(new JwtResponse(token));
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}
```

## Technologies Used

- **Spring Boot**: Application framework
- **Spring Data JPA**: Data access
- **PostgreSQL**: Database
- **JJWT**: JWT library
- **Maven**: Build tool

## Contributing

Contributions are welcome! Please fork the repository and create a pull request with your changes.

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature-branch`).
3. Commit your changes (`git commit -m 'Add new feature'`).
4. Push to the branch (`git push origin feature-branch`).
5. Create a new Pull Request.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

## Further Reading

For a comprehensive guide on securing JWTs, generating, and managing secret keys using Spring Boot, check out this [Medium article](https://medium.com/@davoud.badamchi/securing-jwts-comprehensive-guide-to-generating-and-managing-secret-keys-using-spring-boot-0f943186f4b0) by Davoud Badamchi.
