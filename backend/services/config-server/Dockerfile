# Stage 1: Build
#FROM maven:3.9-eclipse-temurin-17 AS build

#WORKDIR /app

# Copier les fichiers de configuration Maven
#COPY pom.xml .
#COPY src ./src

# Build l'application (skip tests pour accélérer)
#RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
LABEL ci=true
WORKDIR /app

# Copier le JAR depuis le stage de build
COPY target/*.jar app.jar

# IMPORTANT : donner les droits de lecture
RUN chmod 644 /app/app.jar

# Variables d'environnement
ENV SPRING_PROFILES_ACTIVE=prod

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]