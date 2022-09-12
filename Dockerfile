FROM maven:3-openjdk-17 as build
COPY . /usr/src/app
COPY settings.xml /root/.m2/
WORKDIR /usr/src/app
RUN mvn -DskipTests install

FROM openjdk:17

COPY --from=build /usr/src/app/target/cwa-registration.jar /opt/app/
COPY truststore /opt/app
WORKDIR /opt/app
CMD ["java", "-Djavax.net.ssl.trustStore=truststore", "-Djavax.net.ssl.trustStorePassword=changeit", "-jar", "cwa-registration.jar"]
