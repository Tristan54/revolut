FROM maven:3.8.4-openjdk-17-slim AS BUILDER
WORKDIR /app
COPY pom.xml .
COPY /src ./src
RUN mvn -e -B package -DskipTests

FROM gcr.io/distroless/java17-debian11
COPY --from=BUILDER /app/target/revolut-0.0.1-SNAPSHOT.jar /app/target/revolut-0.0.1-SNAPSHOT.jar
EXPOSE 8080
CMD ["/app/target/revolut-0.0.1-SNAPSHOT.jar"]