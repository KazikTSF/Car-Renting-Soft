# Github API service
This is demo to showcase abilities and also for me to learn new concepts about spring boot.
## Technology Stack
- Java 21
- Spring Boot 3.3.2
- Maven
## Components
### api-gateway
This module contains gateway api so that all the calls can be made to single ip address.
### car-service
This module contains logic to exchange information between api and MongoDB database about all cars.
### discovery-service
This module contains Netflix Eureka server that makes connecting services with each other easier.
### order-service
This module contains logic to exchange information between api and MySQL database with client orders.
### inventory-service
This module contains logic to exchange information between api and MySQL database with inventory and responds whether product is in stock or not.
### notification-service
This module recieves Kafka notifications.
