FROM maven:3.8.4-openjdk-17-slim AS BUILDER
WORKDIR /app
COPY pom.xml .
COPY /src ./src
RUN mvn -e -B clean package -DskipTests

FROM gcr.io/distroless/java17-debian11
COPY --from=BUILDER /app/target/conversion-service-1.1.jar /app/target/conversion-service-1.1.jar
EXPOSE 8081
CMD ["/app/target/conversion-service-1.1.jar"]