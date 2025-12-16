##########################
# Step 1: Frontend build #
##########################

# Base image for the build container
FROM node:20 AS frontend-builder

# Set working directory where all subsequent commands will be executed
WORKDIR /app/frontend

# Copy code from frontend folder
COPY ./frontend/ /app/frontend

# Build frontend
RUN npm install && npm run build

#########################
# Step 2: Backend build #
#########################

# Base image for the build container
FROM maven:3.9.9-eclipse-temurin-21-jammy AS backend-builder

# Set working directory where all subsequent commands will be executed
WORKDIR /app/backend

# Copy dependencies from backend project
COPY ./backend/pom.xml /app/backend/

# Download dependencies from backend project
RUN mvn dependency:go-offline

# Copy code from backend project
COPY ./backend/src /backend/src

# Build backend
RUN mvn -B package -DskipTests

#####################################
# Step 3: Production runtime image  #
#####################################

# Base image for the application runtime container
FROM eclipse-temurin:21-jre

# Set working directory where JAR is
WORKDIR /usr/src/app/

# Copy JAR from backend container
COPY --from=backend-builder /app/backend/target/*.jar /usr/src/app/app.jar

# Copy keystore for HTTPS
COPY ./backend/src/main/resources/keystore.jks /app/keystore.jks

# Copy frontend build
COPY --from=frontend-builder /app/frontend/dist/frontend /app/static

EXPOSE 8443

ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql-container:3306/dbeasymatch
ARG SPRING_DATASOURCE_USERNAME=
ARG SPRING_DATASOURCE_PASSWORD=

ENTRYPOINT ["java", "-jar", "app.jar"]