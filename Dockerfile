FROM openjdk:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/clojule-api-0.0.1-SNAPSHOT-standalone.jar /clojule-api/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/clojule-api/app.jar"]
