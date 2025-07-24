FROM docker.io/library/maven:3.9-eclipse-temurin-24 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn package -DskipTests

FROM docker.io/library/eclipse-temurin:24-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/target/classes/*.properties application.properties
ENV SPRING_CONFIG_ADDITIONAL_LOCATION=/app/application.properties

EXPOSE 8082

ENTRYPOINT ["java","-jar","/app/app.jar"]