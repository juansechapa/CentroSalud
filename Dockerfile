FROM tomcat:10.1-jdk11

RUN rm -rf /usr/local/tomcat/webapps/*

COPY target/*.war /usr/local/tomcat/webapps/ROOT.war

RUN sed -i 's/port="8080"/port="8087"/' /usr/local/tomcat/conf/server.xml

EXPOSE 8087

CMD ["catalina.sh", "run"]