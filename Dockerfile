FROM maven:3-openjdk-11 as build
COPY . /usr/src/app
COPY settings.xml /root/.m2/
WORKDIR /usr/src/app
RUN mvn -DskipTests install

FROM ubuntu:20.04
RUN apt-get update && apt-get install -y nginx openjdk-11-jdk clamdscan
RUN service nginx restart

COPY --from=build /usr/src/app/target/cwa-registration.jar /opt/app/
COPY truststore /opt/app
WORKDIR /opt/app
CMD ["java", "-Djavax.net.ssl.trustStore=truststore", "-jar", "cwa-registration.jar"]