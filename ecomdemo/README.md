# E-Commerce Inventory Management System

This is a backend system to manage item inventory for an e-commerce platform using Spring Boot, MySQL, and Redis.

## Features

- Add inventory supply for items
- Reserve stock for customers 
- Cancel reservations
- Query real-time item availability
- Caching with Redis to reduce DB hits

---

## Tech Stack

| Layer        | Technology                      |
|--------------|----------------------------------|
| Language     | Java                             |
| Framework    | Spring Boot, Spring Data JPA     |
| Database     | PostgreSQL                       |
| Caching      | Redis                            |
| Build Tool   | Maven                            |
| Testing      | JUnit, Mockito                   |
| API Tool     | Postman                          |

---

## Modules

- `Item`: Represents an e-commerce product
- `Inventory`: Tracks available and reserved quantities
- `Reservation`: Manages reserved stock per user
- `Redis`: Caches available quantities for performance

---

## Setup Instructions

1. Clone the repo
   ```bash
   git clone https://github.com/your-username/ecommerce-inventory-backend.git
   cd ecommerce-inventory-backend
