#1: Build Stage

FROM eclipse-temurin:21-jdk AS builder
WORKDIR /build

COPY pom.xml mvnw ./
COPY .mvn .mvn

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package -DskipTests


#2: Runtime Stage

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /build/target/task-manager.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
