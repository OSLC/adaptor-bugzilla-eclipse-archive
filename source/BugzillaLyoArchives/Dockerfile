FROM docker.io/library/maven:3-eclipse-temurin-25-alpine AS build

WORKDIR /src
# Copy pom.xml first for better Docker layer caching
COPY pom.xml pom.xml

# Download dependencies (will be cached if pom.xml doesn't change)
RUN mvn -B --no-transfer-progress -f pom.xml dependency:go-offline

# Copy the rest of the source code
COPY . .

# Build the application
RUN mvn -B --no-transfer-progress -f pom.xml -DskipTests clean package -am

# JRE25 variant does not exist yet; Alpine does not have ARM64 variant
FROM docker.io/library/jetty:12.1-jdk25-eclipse-temurin

# WARNING DO NOT CHANGE WORKDIR or set it back to what it was before
# $JETTY_BASE must be correct before starting Jetty

COPY --from=build /src/target/*.war /var/lib/jetty/webapps/ROOT.war

RUN java -jar "$JETTY_HOME/start.jar" --add-modules=ee9-deploy,ee9-jsp,ee9-jstl

EXPOSE 8080
