FROM maven:3.8.4-openjdk-17-slim AS BUILDER
WORKDIR /app
COPY pom.xml .
COPY /src ./src
RUN mvn -e -B clean package -DskipTests

FROM gcr.io/distroless/java17-debian11
COPY --from=BUILDER /app/target/commercant-service-1.0.jar /app/target/commercant-service-1.0.jar
EXPOSE 8083
CMD ["/app/target/commercant-service-1.0.jar"]