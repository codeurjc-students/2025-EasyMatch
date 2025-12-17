##########################
# Step 1: Frontend build #
##########################

# Base image for the build container
FROM node:20 AS frontend-builder

# Set working directory where all subsequent commands will be executed
WORKDIR /app

COPY frontend/package*.json ./
COPY frontend/angular.json ./
COPY frontend/tsconfig*.json ./
COPY frontend/src ./src

RUN npm install
RUN npm run build -- --configuration=production --base-href=/

#########################
# Step 2: Backend build #
#########################

# Base image for the build container
FROM maven:3.9.9-eclipse-temurin-21-jammy AS backend-builder

# Set working directory where all subsequent commands will be executed
WORKDIR /app

# Copy dependencies from backend project
COPY ./backend/pom.xml ./

# Download dependencies from backend project
RUN mvn dependency:go-offline

# Copy code from backend project
COPY ./backend/src ./src


# Copy frontend build
COPY --from=frontend-builder /app/dist/frontend/browser/ ./src/main/resources/static/

# Build backend
RUN mvn -B package -DskipTests

#####################################
# Step 3: Production runtime image  #
#####################################

# Base image for the application runtime container
FROM eclipse-temurin:21-jre

# Set working directory where JAR is
WORKDIR /app

# Copy JAR from backend container
COPY --from=backend-builder /app/target/*.jar app.jar

# Copy keystore for HTTPS
COPY ./backend/src/main/resources/keystore.jks /app/keystore.jks

EXPOSE 8443

ENTRYPOINT ["java","-jar","app.jar"]