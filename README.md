# Card Payment Simulator

A Spring Boot application that simulates card payment processing with various test scenarios. This project provides a RESTful API for testing different payment card scenarios including approvals, declines, processing errors, and more.

## Features

- Simulate different payment card scenarios
- Test card validation and processing
- RESTful API endpoints for card management
- Swagger/OpenAPI documentation
- H2 in-memory database
- Comprehensive test card scenarios

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Spring Boot 3.2.3

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/yourusername/card-pay-simulator.git
cd card-pay-simulator
```

### Build the Project

```bash
mvn clean install
```

### Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

Once the application is running, you can access the Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## Available Endpoints

### Card Management

#### Initialize Test Cards
```bash
POST /api/cards/initialize-test-scenarios
```
Creates a set of test cards with different scenarios.

#### Get All Cards
```bash
GET /api/cards
```
Retrieves all cards in the system.

#### Get Card by Number
```bash
GET /api/cards/{cardNumber}
```
Retrieves a specific card by its card number.

#### Get Test Scenarios Information
```bash
GET /api/cards/test-scenarios
```
Retrieves information about available test card scenarios.

### Payment Processing

#### Process Payment
```bash
POST /api/payments/process
```
Processes a payment with the provided card details.

## Test Card Scenarios

The application supports the following test card scenarios:

### Always Approved Cards
- 4242424242424242 (Visa)
- 5555555555554444 (Mastercard)

### Always Declined Cards
- 4000000000000002
- 4000000000000010

### Processing Error Cards
- 4000000000000341
- 4000000000000119

### Insufficient Funds Cards
- 4000000000009995
- 4000000000009987

### Expired Cards
- 4000000000000069
- 4000000000000127

## Database

The application uses H2 in-memory database. You can access the H2 console at:
```
http://localhost:8080/h2-console
```

Database credentials:
- JDBC URL: `jdbc:h2:mem:cardpaydb`
- Username: `sa`
- Password: (empty)

## Development

### Running in Development Mode

The application includes Spring Boot DevTools for automatic reloading during development. To run in development mode:

```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

### Configuration

The application can be configured through `application.properties`. Key configurations include:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:h2:mem:cardpaydb
spring.h2.console.enabled=true

# OpenAPI Configuration
springdoc.swagger-ui.path=/swagger-ui.html
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Spring Boot
- H2 Database
- OpenAPI/Swagger
- Lombok 