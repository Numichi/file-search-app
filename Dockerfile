FROM gradle:8.14.3-jdk21 AS builder
WORKDIR /home/gradle/src

COPY build.gradle settings.gradle gradlew gradlew.bat ./
COPY gradle ./gradle
COPY src ./src

RUN ./gradlew -x test build javadoc --no-daemon

FROM eclipse-temurin:21-jre-alpine

RUN adduser -D user1 && \
    adduser -D user2

EXPOSE 8080
COPY --from=builder /home/gradle/src/build/libs/*.jar /app.jar
COPY --from=builder /home/gradle/src/build/docs/javadoc /app/javadoc
COPY --from=builder /home/gradle/src/src/main/resources/openapi /app/openapi
ENTRYPOINT ["java", "-jar", "/app.jar"]
