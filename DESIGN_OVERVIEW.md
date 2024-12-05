# Messaging Application

## Overview
This project implements a RESTful web service for a messaging application where users can send and receive messages. The application is optimized for high volumes of data and adheres to best practices for database design and API implementation.

---

## Features

- Users can send messages to up to 5 recipients.
- Messages include a title, body, and timestamp.
- Retrieve messages addressed to a specific user.
- Statistics endpoint to view the top 10 users by sent message count within the last 30 days.

---

## Project Setup

### Prerequisites

- Maven
- Docker
- Docker Compose (installed separately from Docker)

### Running / Redeploying

#### Linux / macOS

```bash
make
```

Refer to the Makefile and run equivalent commands in the project’s root directory.

#### Accessing the Services

##### REST Server

- URL: [http://localhost:8080](http://localhost:8080)
- A congratulatory message confirms a successful setup.

##### MariaDB Database

- Host: `localhost:3306`
- Username: `root`
- Password: `root_password`

---

#### Architecture & Design

##### Database Schema

1. **Users Table**: Stores user details.
    - Optimized with `AUTO_INCREMENT` for primary keys.
    - Timestamp columns for tracking creation times.

2. **Messages Table**: Stores message details.
    - Relates to `Users` via a foreign key for the sender.
    - Includes indices for performance optimization (e.g., `sent_at`).

3. **Message Recipients Table**: Establishes a many-to-many relationship.
    - Enforces a maximum of 5 recipients via validation.
    - Uses a composite unique index (`message_id`, `recipient_id`) to prevent duplicates.

##### API Endpoints

1. **Send a Message**
    - **POST** `/messages`
    - Validates input for constraints like recipient limit and field lengths.
    - Stores messages and associates them with recipients.

2. **Retrieve Messages**
    - **GET** `/messages/users/{id}`
    - Pagination support for large datasets.

3. **Statistics: Top Senders**
    - **GET** `/messages/statistics/top-senders`
    - Provides top 10 senders in the last 30 days, sorted by sent message count.

---

#### Validation

- **DTO Layer**: Ensures input constraints, e.g.,
    - Max 255 characters for titles.
    - Positive IDs for users and recipients.
    - No more than 5 recipients.

- **Exception Handling**: Centralized using `@ControllerAdvice` to provide meaningful responses for validation errors or unexpected issues.

---

#### Testing

- Comprehensive tests ensure:
    - Input validation works as expected.
    - Service methods handle edge cases like nonexistent users or duplicate recipients.

---

### Debugging

#### REST Server

- Remote debug port: `localhost:5005`
- Logs: `docker logs codingassignment`

#### MariaDB Database

- Logs: `docker logs codingassignment-db`

---

### Future Improvements

1. **Authentication and Authorization**:
    - Add user authentication using JWT.
    - Implement role-based access controls.

2. **Scalability**:
    - Shard the `messages` table to handle high-volume workloads.

3. **Advanced Analytics**:
    - Add endpoints for message delivery and read statistics.

4. **Cloud Deployment**:
    - Automate deployment with CI/CD pipelines.

---

### Author

**Rishabh Garg**

If you have any questions, please feel free to contact me.
