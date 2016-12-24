
 Thymeleaf Sandbox: BigList, Spring Web MVC
-------------------------------------------
 
 This is a sandbox application, only used internally for development.

     
## Contents

 This sandbox repository contains an example application meant to test the use Thymeleaf rendering
 for very large amounts of markup using Spring Boot and Spring Web MVC (in comparison to Spring Web Reactive).

 See also: https://github.com/thymeleaf/thymeleafsandbox-biglist-reactive


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
     * `/` or `/thymeleaf`: index page using Thymeleaf.
     * `/freemarker`: index page using FreeMarker.
   * *Small* listing (8,715 elements, based on the MIT-licensed *Chinook* database for SQLite):
     * `/smalllist.thymeleaf`: *Small* listing using Thymeleaf.
     * `/smalllist.freemarker`: *Small* listing using FreeMarker.
   * *Big* listing (same 8,715 elements repeated 300 times = 2,614,500 elements):
     * `/biglist.thymeleaf`: *Big* listing using Thymeleaf.
     * `/biglist.freemarker`: *Big* listing using FreeMarker.

 
 
 