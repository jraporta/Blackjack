## 📄Assignment

Create a reactive Spring Boot API for a Blackjack game.

## ♠️♥️Description♦️♣️

This API enables **multiplayer Blackjack games**, bringing the excitement of the casino to your application. Players can:

- Place **bets** before the croupier deals the cards.
- Interact using classic game actions like **hit**, **stand**, **double**, **split**, and **surrender**, following the widely accepted rules of Blackjack.
- Play in a dynamic, real-time environment with other players.

Whether you're simulating a casino or integrating a card game into your platform, this API provides the core mechanics needed to manage and play Blackjack seamlessly.

## 💻Technologies

- Frameworks & Libraries: Spring WebFlux, Swagger, Mockito, JUnit 
- Databases: MongoDB, MongoDB Atlas, MySQL 
- Tools: Maven, Postman, Docker, Docker Compose 
- CI/CD: GitHub Actions

## 📋Requirements

- Docker: Docker Desktop (Windows and macOS) or Docker Engine (Linux).

## 🛠️Installation

- Install Docker according to you system features.

## ▶️Execution

### MySQL and MongoDB version (Main branch)

To run the project:
- Download the `compose.yaml` file.
- Make sure **Docker** is running in the local machine.
- Open a command prompt window and navigate to the project directory where the `compose.yaml` file is located.
- Execute the following command: `docker-compose up --build`
- Docker will automatically pull the images from docker hub, build them and create and start the containers as specified in the `compose.yaml` file.
- The service will be available at `http://localhost:8081`.
- To stop the running containers press `Ctrl+C` in the terminal were the `docker-compose` command is running or execute the command `docker-compose down`.

Alternatively, you can build the docker image locally instead of pulling it from Docker Hub by following these steps:
- clone the github repository in your local machine running the command `https://github.com/jraporta/Blackjack.git`
- Build the Docker image locally by executing the command `docker build --tag=jraporta/blackjack:latest .`
- Start the services as defined in `docker-compose.yaml` by executing the command `docker-compose up --build`
- Once the containers are up, follow the instructions in the above section to interact with the service.

### Mongo Atlas version

- Ensure docker is installed and running in your system.
- Pull the image from Docker Hub: `docker pull jraporta/blackjack:latest`
- Run the docker container: `docker run -e MONGODB_URI="{uri}" -p 8080:8080 jraporta/blackjack:latest`
  Replace {uri} with a valid MongoDB URI to connect to Atlas.

## 🌐Deployment

Deployed with Render on <https://blackjack-ld1x.onrender.com>.
Note: When querying for first time, the system might take a couple of minutes to respond due to Render's cold starts.

## 🤝Contributions

Personal project.

## 📄Documentation

API documentation is available via Swagger:
- Swagger UI: <http://localhost:8080/swagger-ui.html> or <https://blackjack-ld1x.onrender.com/swagger-ui.html>
- OpenAPI spec: <http://localhost:8080/v3/api-docs> or <https://blackjack-ld1x.onrender.com/api-docs>.
