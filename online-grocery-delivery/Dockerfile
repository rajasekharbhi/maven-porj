FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/online-grocery-delivery-1.0-SNAPSHOT.jar app.jar
VOLUME ["/app/data"]
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]
