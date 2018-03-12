FROM openjdk:8-jre
WORKDIR /opt/app
COPY target/consumer-integers-kcl.jar /opt/app/consumer-integers-kcl.jar
ENTRYPOINT ["java", "-Xmx128m", "-Xms128m", "-jar", "consumer-integers-kcl.jar"]