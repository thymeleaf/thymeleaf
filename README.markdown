
 Thymeleaf Sandbox: Spring + Spring Reactive
--------------------------------------------
 
 This is a sandbox application, only used internally for development.

 **Thymeleaf users should just ignore this repository**.



## Building

 To build this project you will need Maven 3. You can get it at:
 
     http://maven.apache.org

 Clean compilation:
 
     mvn clean compile
     
 Run the application (embedded NETTY TcpServer):
 
     mvn exec:java -Dexec.mainClass="thymeleafsandbox.springreactive.application.Application"

 Or, from an IDE, just launch the class:

     thymeleafsandbox.springreactive.application.Application

 Once started, the application should be available at:
 
     http://localhost:8080