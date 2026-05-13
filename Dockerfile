# ── Etapa 1: build ────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Descargar dependencias primero (capa cacheada)
COPY pom.xml ./
RUN mvn dependency:go-offline -q

# Compilar sin tests
COPY src ./src
RUN mvn clean package -DskipTests -q

# ── Etapa 2: runtime ──────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
