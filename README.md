## 📄Assignment

Create a reactive Spring Boot API for a Blackjack game.

## 💻Technologies

- Spring WebFlux
- MongoDB
- MySQL
- Maven
- Postman
- Swagger

## 📋Requirements

- JDK 21
- Maven
- MongoDB
- MySQL

## 🛠️Installation

- Install MySQL and create a database named `s5_blackjack`, plus execute the following script to create the tables:
```
CREATE TABLE IF NOT EXISTS `s5_blackjack`.`player` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `money` INT NOT NULL DEFAULT 0,
  `games_played` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE)
ENGINE = InnoDB;
```
- Install MongoDB.
- If needed, launch MongoDB as a service by launching it in a console window by running the command: `mongod`

## ▶️Execution

### Run the project
- Open a command prompt window.
- Navigate to the project’s root directory.
- Execute the following command to start the application: `mvn spring-boot:run`

## 🌐Deployment

Not deployed.

## 🤝Contributions

No contributions.

## 📄Documentation

Access <http://localhost:8080/swagger-ui.html> and <http://localhost:8080/v3/api-docs> endpoint to access the api documentation.
