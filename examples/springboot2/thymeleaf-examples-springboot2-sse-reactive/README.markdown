
 Thymeleaf Sandbox: SSE (Server-Sent Events)
--------------------------------------------
 
 This is a sandbox application, only used internally for development.

     
## Contents

 This sandbox repository contains an example application meant to test the use of Thymeleaf for
 rendering Server-Sent Events (SSE), using Spring 5 WebFlux


## Building

 To build this project you will need Maven 3. You can get it at:
 
     http://maven.apache.org

 Clean compilation:
 
     mvn -U clean compile
     
 Run the application (Spring Boot based, using **netty** as a web server):
 
     mvn spring-boot:run

 Once started, the application should be available at:
 
     http://localhost:8080
     
## Executing

 This application offers several URLs:
 
   * Index page:
     * `/`: HTML interface requesting SSE events via an `EventSource` JavaScript object
     * `/events`: Returns the events being used (needs `Accept: text/event-stream` header)

 
 
 