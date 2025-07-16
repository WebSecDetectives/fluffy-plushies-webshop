FROM docker.io/library/maven:3.9-eclipse-temurin-24 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn package -DskipTests

FROM docker.io/library/eclipse-temurin:24-jre

WORKDIR /app

ENV JAVA_OPTS=""

COPY --from=build /app/target/*.jar app.jar

EXPOSE 4567

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]