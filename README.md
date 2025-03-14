# Spribe Booking System

A robust accommodation booking system built with Spring Boot that allows users to browse, search, and book various types of accommodation units.

## Technology Stack

- **Java 21**
- **Spring Boot 3.4.3**
- **Spring Data JPA**
- **PostgreSQL**
- **Liquibase**
- **MapStruct**
- **Lombok**
- **Spring Cache**
- **SpringDoc OpenAPI UI**
- **Testcontainers**
- **JUnit 5**

## API Features

### Unit Management
- Create and update accommodation units (HOME, FLAT, APARTMENT types)
- Search for available units with filtering by date range and cost
- Get count of all available units (cached for performance)

### Booking Management
- Book available units for specific date ranges
- Confirm bookings (simulates payment processing)
- Cancel existing bookings
- Automatic handling of payment timeouts (scheduled job)

## Local Development

### Prerequisites
- Java 21
- Docker (for running PostgreSQL and integration tests)

### Running the Application
1. Start the PostgreSQL database: `docker-compose up -d`
2. Build and run the application: `./gradlew bootRun`
3. Access the API documentation: [Swagger UI](http://localhost:8080/swagger-ui/index.html)

### Running Tests
```
./gradlew test
```
