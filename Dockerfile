# ── Etapa 1: build ────────────────────────────────────────────
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Descargar dependencias primero (capa cacheada)
COPY pom.xml ./
RUN mvn dependency:go-offline -q

# Compilar sin tests
COPY src ./src
RUN mvn clean package -DskipTests -q

# ── Etapa 2: runtime ──────────────────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
