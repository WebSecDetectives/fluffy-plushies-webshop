# Fluffy Plushies Web Shop - Identity Service
## System Context
Fluffy Plushies Web Shop is an e-commerce platform for selling plush toys, built using a microservices architecture consisting of four key services:
1. **Identity Service** (current scope): Manages user authentication, authorization, and account management
2. **Inventory Service**: Handles product catalog and stock management
3. **Email Service**: Manages email notifications and communications
4. **Order Service**: Processes customer orders and payments

This document focuses specifically on the Identity Service architecture. The other microservices have their own dedicated documentation covering their specific implementations.

## Technology Stack
- **Java**: Version 24
- **Spring Framework**: Spring Boot 3.4.4, Spring MVC, Spring Data JPA
- **Security**: Spring Security 6.4.4, JWT (JSON Web Token)
- **Database**: MySQL for production, H2 for testing
- **Build Tool**: Maven
- **Additional Libraries**: Lombok, Hibernate Validator, Passay (password validation)

## Core Modules
### 1. Identity Service
Manages user authentication, authorization, and account management.
#### Components:
- **API Layer**:
    - `AuthController`: Exposes REST endpoint for user login
    - `UserController`: Exposes REST endpoints for user management
    - `RestAccessDeniedHandler`: Custom handler for access denied scenarios
    - `RestAuthenticationEntryPoint`: Entry point for authentication
    - `GlobalExceptionHandler`: Custom handler for standardizing error responses, including validation failures, security exceptions, and business logic errors with appropriate HTTP status codes

- **Service Layer**:
    - `UserService`: Business logic for user operations, including registration, profile updates, and user management
    - `AuthService`: Authentication services including login validation and JWT token generation
    - `CustomUserDetailsService`: Implementation of Spring Security's UserDetailsService that integrates with the application's user repository

- **Data Layer**:
    - **Base Classes**:
        - `AbstractIdentifiable`: Superclass providing ID field management for all entities
        - `BaseEntity`: Superclass implementing soft deletion functionality with a deleted flag

    - **Entities**:
        - `User`: Entity representing user accounts
        - `UserHistory`: Entity tracking user account changes
        - `Address`: Entity for user addresses

- **DTOs**:
    - `CreateUserRequest`: For user registration with validation rules
    - `UpdateUserRequest`: For updating user profile information
    - `UpdatePasswordRequest`: For password changes with strong password validation
    - `UserResponse`: For returning user profile data
    - `AddressRequest`:  For address creation/updates with validation for required fields
    - `AddressResponse`: For returning address information
    - `AuthRequest`: For authentication with username and password validation
    - `AuthResponse`: For returning authentication token upon successful login

### 2. Data Persistence
The application uses JPA repositories to interact with the database, supporting:
- User account storage and retrieval
- Implicit transaction handling through Spring Data JPA's default transaction behavior
- Data validation
- **Tombstone pattern** using a deleted flag for soft deletion of user accounts instead of hard deletion
- **Snapshot pattern** implemented through UserHistory repository to maintain historical records of user account states

### 3. Security Framework
- **Authentication**:
    - JWT-based authentication with secure token handling
    - Custom JWT filter integrated in Spring Security filter chain
    - Token-based stateless authentication for scalability and microservices compatibility
    - Protected endpoints requiring valid JWT token
    - Custom authentication entry point for unauthorized access handling

- **Authorization**:
    - Role-based access control (RBAC) with user and admin roles
    - Method-level security using Spring's @EnableMethodSecurity
    - Custom access denied handler for proper error responses
    - Fine-grained permissions for user management operations

- **Password Security**:
    - BCrypt password encoding with appropriate strength factor
    - Strong password validation using Passay for enforcing security policies
    - Password complexity requirements (length, mixed case, numbers, special characters)
    - Secure password update mechanisms

- **API Security**:
    - CORS configuration for controlled cross-origin resource sharing
    - Security headers configuration
    - Centralized exception handling for security-related errors

- **Integration**:
    - Custom UserDetailsService implementation for database-backed authentication
    - Spring Security integration with JPA repositories
    - Security context management

## Communication Flow
1. Client requests reach API controllers through HTTP endpoints
2. Controllers delegate to service layer for business logic processing
3. Services interact with repositories for data persistence
4. Repositories communicate with the database layer

## Testing
- Integration tests for API endpoints using Spring Boot Test and MockMvc
- Parameterized testing with JUnit 5 for thorough validation of input scenarios
- Data-driven test approach with dedicated test data providers for email validation, password strength, and field validation
- Test data utilities using Datafaker for generating consistent and realistic test data
- Comprehensive boundary testing for validating field constraints (length, format, etc.)
- Transaction management in tests to ensure database state isolation between test cases
- H2 in-memory database for testing with custom test properties configuration
- Role-based authorization testing for admin vs. regular user operations
- Token-based authentication testing with proper JWT validation scenarios
- Mock MVC configuration for simulating HTTP requests without requiring a running server

## Deployment
The application is packaged as a JAR file and designed to work within a containerized environment.
