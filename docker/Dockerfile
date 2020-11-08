# Alpine Linux with OpenJDK JRE
FROM openjdk:8-jre-alpine

# Copy war file
COPY currency-converter-0.0.1-SNAPSHOT.jar /currency-converter.war

# run the app
CMD ["/usr/bin/java", "-jar", "/currency-converter.war"]