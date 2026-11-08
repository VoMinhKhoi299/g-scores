FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /build

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
RUN apk add --no-cache curl \
    && addgroup -S app \
    && adduser -S app -G app

COPY --from=build /build/target/g-scores-backend-*.jar /app/app.jar
COPY --chown=app:app dataset/diem_thi_thpt_2024.csv /app/dataset/diem_thi_thpt_2024.csv

USER app
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
