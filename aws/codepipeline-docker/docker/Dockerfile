FROM openjdk:8-jre
WORKDIR /opt/app
EXPOSE 8000
COPY app.jar /opt/app/app.jar
CMD ["java", "-Dserver.port=8000", "-jar", "app.jar"]
