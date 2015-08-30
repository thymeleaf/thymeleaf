Thymeleaf 3 examples: Spring Mail
=================================

This is an example application showing how to compose and send dynamic e-mails using Spring and Thymeleaf.

With Thymeleaf you can compose text and HTML emails easily.

To learn more about Thymeleaf and download the latest version visit
     http://www.thymeleaf.org

In order to run the application to should configure your SMTP server correctly.
You can do this by modifying the values on `src/main/resources/configuration.properties`
and `src/main/resources/javamail.properties`

You can deploy the application any Java servlet container or executing the application 
on an embedded Tomcat 7 with `mvn tomcat7:run` (the application will be at http://localhost:8080/springmail/).
