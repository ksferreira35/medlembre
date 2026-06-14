FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve --no-transfer-progress
COPY src ./src
RUN mvn package -DskipTests --no-transfer-progress

FROM eclipse-temurin:21-jre AS cli
WORKDIR /app
COPY --from=build /app/target/medlembre-cli-1.2.1-jar-with-dependencies.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:21-jre AS gui
WORKDIR /app
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        fontconfig \
        libfreetype6 \
        libx11-6 \
        libxext6 \
        libxi6 \
        libxrender1 \
        libxtst6 \
    && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/target/medlembre-gui-1.2.1-jar-with-dependencies.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM cli AS final
