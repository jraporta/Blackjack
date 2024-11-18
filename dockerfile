FROM openjdk:23
LABEL maintainer="jraporta"
ADD target/blackjack.jar blackjack.jar
ENTRYPOINT ["java","-jar","blackjack.jar"]