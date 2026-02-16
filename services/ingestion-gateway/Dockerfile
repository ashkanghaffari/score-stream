# ingestion-gateway/Dockerfile
FROM eclipse-temurin:25-jre-alpine

# Create user/group up front
RUN addgroup -S app && adduser -S app -G app

WORKDIR /app
# Copy tested jar produced by CI and assign to non-root
COPY --chown=app:app target/app.jar /app/app.jar

USER app

EXPOSE 8080 8081
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-XX:+ExitOnOutOfMemoryError","-jar","/app/app.jar"]
