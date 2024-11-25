## 📄Assignment

Create a reactive Spring Boot API for a Blackjack game.

## 💻Technologies

- Spring WebFlux
- MongoDB
- MongoDB Atlas
- MySQL
- Maven
- Postman
- Swagger
- Docker
- Docker Compose
- Maven
- Github actions

## 📋Requirements

- Docker Desktop (for Windows and macOS) or Docker Engine (for Linux) must be installed.

## 🛠️Installation

- Install Docker according to you system features.

## ▶️Execution

### Run the project

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

## 🌐Deployment

Deployed with Render (access via <https://blackjack-ld1x.onrender.com>).
Note: When querying for first time, the system might take a couple of minutes to respond.

## 🤝Contributions

Personal project.

## 📄Documentation

Access <http://localhost:8080/swagger-ui.html> and <http://localhost:8080/v3/api-docs> endpoint to access the api documentation.
