✓ Working configuration with locally deployed app

### MySQL official image: <https://hub.docker.com/_/mysql>

Start a mySQL instance:
`docker run --name mysql-test -e MYSQL_ROOT_PASSWORD=1234 -d mysql:latest`
`--name`: container name
`-d`: version tag

Expose port `3306` (mySQL default) to `3307`:
`docker run --name mysql-test -p 3307:3306 -e MYSQL_ROOT_PASSWORD=1234 -d mysql:latest`

✓ Create database `s5_blackjack` on startup (default configuration to work with locally deployed app):
`docker run --name mysql-test -p 3307:3306 -e MYSQL_ROOT_PASSWORD=1234 -e MYSQL_DATABASE=s5_blackjack -d mysql:latest`

Access terminal of the running container: `docker exec -it mysql-test bash`
Once in the terminal start mysql: `mysql -u root -p`

Stop the container: `docker stop mysql-test`
Start the container: `docker start mysql-test`

---

### MongoDB official image <https://hub.docker.com/_/mongo>

Start a mongoDB instance:
`docker run --name mongo-test -d mongo:latest`
`--name`: container name
`-d`: version tag

Open mongo shell in running container:
`docker exec -it mongo-test mongosh`

✓ Expose port `27017` (mySQL default) to `27018`:
`docker run --name mongo-test -p 27018:27017 -d mongo:latest`

---

### Dockerize the spring boot application

Create the jar file:
`mvn clean package`

Create the docker image:
`docker build --tag=blackjack:latest .`

Run the container from the image (can't get it to communicate with the previous containers:
`docker run -p 8080:8080 blackjack:latest`

//TODO: get it to communicate with the exposed ports of the other containers

### Docker compose

(Check buildfile syntax for errors: `docker-compose config`)

Build our images, create the defined containers, and start it in one command:
`docker-compose up --build`