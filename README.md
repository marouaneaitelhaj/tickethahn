# TicketHahn

TicketHahn is a ticket management system built with Spring Boot and Java Swing. It allows users to create, update, and manage tickets, comments, and audit logs.

## Table of Contents

- [Code Coverage](#code-coverage)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
- [Build the Project](#build-the-project)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Contributing](#contributing)
- [License](#license)

## Code Coverage
![alt text](image.png)
To generate code coverage reports using JaCoCo, run the following command:

```sh
mvn clean test
```

The code coverage report will be generated in the `target/site/jacoco` directory. Open the `index.html` file in a web browser to view the report.

## Demonstration Video

Watch the demonstration video to see TicketHahn in action:

[![Watch the video](https://img.youtube.com/vi/VIDEO_ID/0.jpg)](https://www.canva.com/design/DAGgEUgca0c/aMQq4AuAnRtQpyheg4MktQ/edit?utm_content=DAGgEUgca0c&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton)

## Features

- Create, update, and delete tickets
- Add comments to tickets
- Track changes with audit logs
- User authentication and authorization
- Java Swing client for managing tickets

## Technologies Used

- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate
- Oracle Database
- H2 Database (for testing)
- ModelMapper
- Lombok
- Java Swing
- Docker
- Maven

## Getting Started

### Prerequisites

- Java 17
- Maven
- Docker

### Clone the Repository

```sh
git clone https://github.com/your-username/tickethahn.git
cd tickethahn
```

## Build the Project

```sh
mvn clean install
```

## Running the Application

### Using Docker

Start the services using Docker Compose:

```sh
docker-compose up -d --build
```

The application will be available at [http://localhost:8080](http://localhost:8080).

## API Endpoints

![image](https://github.com/user-attachments/assets/2f4277be-756e-4090-b498-d78645a58137)

![image](https://github.com/user-attachments/assets/0598f71d-0f95-44a6-a4c9-fb1fabbc7eab)



### User Endpoints

- `POST /api/v1/auth` - Sign up a new user
- `GET /api/v1/auth/all` - Get all users

### Ticket Endpoints

- `POST /api/v1/tickets` - Create a new ticket
- `GET /api/v1/tickets` - Get all tickets
- `GET /api/v1/tickets/{id}` - Get a ticket by ID
- `PUT /api/v1/tickets/{id}` - Update a ticket
- `DELETE /api/v1/tickets/{id}` - Delete a ticket
- `POST /api/v1/tickets/change-status` - Change the status of a ticket
- `GET /api/v1/tickets/status/{status}` - Get tickets by status

### Comment Endpoints

- `POST /api/v1/comments` - Create a new comment
- `GET /api/v1/comments` - Get all comments

### Audit Log Endpoints

- `GET /api/v1/audit/{ticketId}` - Get audit logs for a ticket
- `GET /api/v1/audit` - Get all audit logs
