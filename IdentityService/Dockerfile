# Hardened build of the upstream IdentityService (context = mirror's IdentityService/).
FROM docker.io/library/maven:3.9-eclipse-temurin-24@sha256:a137a467ec89b5713d0be817b55bdba6b4d6ef16e3d05565a79bc08d8e775a1c AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

FROM docker.io/library/eclipse-temurin:24-jre@sha256:8cb2387a28af84cf0db0948d9c67d4480192f4e567027a3963f145d218e8b4f2
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/target/classes/*.properties application.properties
ENV SPRING_CONFIG_ADDITIONAL_LOCATION=/app/application.properties
EXPOSE 8081
# Numeric non-root so k8s runAsNonRoot validates; Spring temp uses world-writable /tmp (RO-rootfs friendly).
USER 1001:1001
ENTRYPOINT ["java","-jar","/app/app.jar"]