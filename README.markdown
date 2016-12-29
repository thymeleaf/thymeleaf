
 Thymeleaf Sandbox: BigList, Spring Web Reactive
------------------------------------------------
 
 This is a sandbox application, only used internally for development.

     
## Contents

 This sandbox repository contains an example application meant to test the use Thymeleaf rendering
 for very large amounts of markup using Spring Boot and Spring Web Reactive (in comparison to Spring Web MVC).

 See also: https://github.com/thymeleaf/thymeleafsandbox-biglist-mvc


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
     * `/smalllist.json`: *Small* listing in JSON.
   * *Big* listing (same 8,715 elements repeated 300 times = 2,614,500 elements):
     * `/biglist-full.thymeleaf`: *Big* listing using Thymeleaf, without limiting the size of output chunks and
       requiring the full model to be fully resolved in memory before template execution. All output will be created
       in memory as a single `DataBuffer`, then sent to the server's output channels.
     * `/biglist-chunked.thymeleaf`: *Big* listing using Thymeleaf, limiting the size of output chunks so that
       they will be sent to the output channels as they are filled, letting these output channels ask for more
       in a form of (single-threaded) back-pressure. Model required to be fully resolved in memory
       before template execution.
     * `/biglist-datadriven.thymeleaf`: *Big* listing using Thymeleaf, working in *data-driven* mode so that one
       of the context variables is allowed to be an `org.reactivestreams.Publisher<X>` object and the Thymeleaf engine will
       bind itself to this `Publisher` and work in a *reactive* way as a part of the data flow itself, grouping the
       data output by the publisher in *buffers* before processing their corresponding part of the template and sending
       output chunks (of a maximum size and generated as data is streamed from the *data-driver*) to the output channels.
     * `/biglist.freemarker`: *Big* listing using FreeMarker. No limit to output buffers can be set. Model has to
       be fully resolved before template execution.
     * `/biglist.json`: *Big* listing in JSON.

 
 
 