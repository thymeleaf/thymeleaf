
Thymeleaf Examples: Spring Mail
-------------------------------
 
 To learn more:
 
     http://www.thymeleaf.org


Building
--------
 
 To build this project you will need Maven 2. You can get it at:
 
     http://maven.apache.org

 Clean compilation products:
 
     mvn clean
     
 Compile:
 
     mvn compile
     
 Run in a tomcat server:
 
     mvn tomcat7:run

 Once started, the application should be available at:
 
     http://localhost:8080/springmail

 In order to run the application to should configure your SMTP server correctly.
 You can do this by modifying the values on `src/main/resources/configuration.properties`
 and `src/main/resources/javamail.properties`

