FROM amazoncorretto:17-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ENV SPRING_OUTPUT_ANSI_ENABLED="ALWAYS"
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]