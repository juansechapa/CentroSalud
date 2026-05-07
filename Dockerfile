FROM maven:3.9.6-eclipse-temurin-11 AS build

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

FROM tomcat:10.1-jdk11

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

RUN sed -i 's/port="8080"/port="8087"/' /usr/local/tomcat/conf/server.xml

EXPOSE 8087

CMD ["catalina.sh", "run"]