FROM openjdk:8-jre
WORKDIR /opt/app
COPY target/producer-integers-kpl.jar /opt/app/producer-integers-kpl.jar
ENTRYPOINT ["java", "-Xmx128m", "-Xms128m", "-jar", "producer-integers-kpl.jar"]