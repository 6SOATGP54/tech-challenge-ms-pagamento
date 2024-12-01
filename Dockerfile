FROM alpine:3.19
LABEL maintainer="https://github.com/6SOATGP54"

WORKDIR /home

#Envoiroments
ENV HOST_MONGO="" \
    DATABASE_MONGO="" \
    MONGO_USER="" \
    MONGO_PASSWORD="" \
    MONGO_PORT="" \
    RABBIT_HOST=""  \
    RABBIT_PORT="" \
    RABBIT_USER="" \
    RABBIT_PASSWORD=""

# updates source list
RUN apk update

# install required tools
RUN apk add --no-cache git bash openjdk17-jdk maven

# Project's setup
COPY . /home/tech-challenge-ms-pagamento
WORKDIR /home/tech-challenge-ms-pagamento

# Start Project
RUN mvn -DskipTests install
EXPOSE 8091

CMD ["java", "-jar", "/home/tech-challenge-ms-pagamento/target/ms-pagamento-0.0.1-SNAPSHOT.jar","&"]