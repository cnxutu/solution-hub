FROM eclipse-temurin:17-jre

RUN apt-get update \
    && apt-get install -y --no-install-recommends ffmpeg \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY target/video-osd-simulator-sample-1.0.0.jar /app/app.jar

EXPOSE 18083
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
