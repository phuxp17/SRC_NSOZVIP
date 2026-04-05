FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /build

COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY src src

RUN sed -i 's/\r$//' mvnw \
    && chmod +x mvnw \
    && MAVEN_CONFIG='' ./mvnw -q -DskipTests package

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=build /build/target/Nso-jar-with-dependencies.jar /app/app.jar
COPY scripts/docker-entrypoint.sh /app/scripts/docker-entrypoint.sh

RUN sed -i 's/\r$//' /app/scripts/docker-entrypoint.sh \
    && chmod +x /app/scripts/docker-entrypoint.sh \
    && mkdir -p /app/logs/ipblock /app/logs/trade /app/logs/sqlbackup \
        /app/chongddos /app/diemdanh /app/nhanquamocnap /app/nhanquaviptuan /app/new

EXPOSE 14444

ENTRYPOINT ["/app/scripts/docker-entrypoint.sh"]
