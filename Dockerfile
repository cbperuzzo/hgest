# ---- build ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn
# mvnw may have Windows line endings if checked out on Windows
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
# cached unless pom.xml changes
RUN ./mvnw -q dependency:go-offline

COPY src src
RUN ./mvnw -q package -DskipTests

# ---- run ----
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd --system --uid 1001 hgest
COPY --from=build /app/target/*.jar app.jar
USER hgest

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
