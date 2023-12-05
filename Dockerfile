
FROM maven:3.6.3-jdk-11 as build
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
RUN mvn clean package

FROM openjdk:11-jre-slim
COPY --from=build /usr/src/myapp/target/CodewaveService-1.0-SNAPSHOT.jar /usr/app/CodewaveService-1.0-SNAPSHOT.jar
WORKDIR /usr/app
EXPOSE 8080
CMD ["java", "-jar", "CodewaveService-1.0-SNAPSHOT.jar"]
