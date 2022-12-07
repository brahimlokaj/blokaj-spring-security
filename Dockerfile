FROM maven:3.8.6 AS build
COPY src /home/apps/security/src
COPY pom.xml /home/apps/security

# Run build without tests
RUN mvn -f /home/apps/security/pom.xml clean compile package -DskipTests

#
# Package stage
#
FROM openjdk:17-jdk-slim-buster
COPY --from=build /home/apps/security/target/blokaj-spring-security-1.0.0-SNAPSHOT.jar /usr/local/lib/security-app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/security-app.jar"]
