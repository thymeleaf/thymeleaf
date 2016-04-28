
 Thymeleaf Sandbox: Spring + Spring Reactive
--------------------------------------------
 
 This is a sandbox application, only used internally for development.

     
## Contents

 This sandbox repository contains:
 
   * A first implementation of the artifacts required for integrating Thymeleaf as a template engine for 
     [Spring Reactive](https://github.com/spring-projects/spring-reactive). Package
     `thymeleafsandbox.springreactive.thymeleaf`
   * A sample Spring Reactive application using both Thymeleaf and FreeMarker in order to test several parts
     of this integration.


## Building

 To build this project you will need Maven 3. You can get it at:
 
     http://maven.apache.org

 Clean compilation:
 
     mvn -U clean compile
     
 Run the application (embedded NETTY TcpServer):
 
     mvn exec:java -Dexec.mainClass="thymeleafsandbox.springreactive.application.Application"

 Or, from an IDE, just launch the class:

     thymeleafsandbox.springreactive.application.Application

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
   * *Big* listing (same 8,715 elements repeated 50 times = 435,750 elements):
     * `/biglist-normal.thymeleaf`: *Big* listing using Thymeleaf, without limiting the size of output buffers and
       requiring the full model to be fully resolved in memory before template execution. All output will be created
       in memory, then sent to the output channels.
     * `/biglist-buffered.thymeleaf`: *Big* listing using Thymeleaf, limiting the size of output buffers so that
       they will be sent to the output channels as they are filled, letting these output channels ask for more
       in a form of (single-threaded) back-pressure. Model required to be fully resolved in memory
       before template execution.
     * `/biglist-datadriven.thymeleaf`: *Big* listing using Thymeleaf, working in *data-driven* mode so that one
       of the context variables is allowed to be an `org.reactivestreams.Publisher<X>` object and the Thymeleaf engine will
       bind itself to this `Publisher` and work in a *reactive* way as a part of the data flow itself, grouping the
       data output by the publisher in *chunks* before processing their corresponding part of the template and sending
       output to the output channels.
     * `/biglist.freemarker`: *Big* listing using FreeMarker. No limit to output buffers can be set. Model has to
       be fully resolved before template execution.
     
## Results

 Observed results for the *Big* listing:
 
#### FreeMarker (`/biglist.freemarker`)
 
```
$ curl http://localhost:8080/biglist.freemarker > output
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100 76.4M    0 76.4M    0     0   701k      0 --:--:--  0:01:51 --:--:-- 18.6M
```

 Latency: nothing received until full output was available (1m51s).
 
#### Thymeleaf NORMAL (`/biglist-normal.thymeleaf`)
 
```
$ curl http://localhost:8080/biglist-normal.thymeleaf > output
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100 76.6M    0 76.6M    0     0   687k      0 --:--:--  0:01:54 --:--:-- 16.6M
```

 Latency: nothing received until full output was available (1m54s).
 
 
#### Thymeleaf BUFFERED (`/biglist-buffered.thymeleaf`)
 
```
$ curl http://localhost:8080/biglist-buffered.thymeleaf > output
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100 76.6M    0 76.6M    0     0  22.3M      0 --:--:--  0:00:03 --:--:-- 22.3M
```

 Latency: started receiving output almost instantly, finished receiving the whole 76M in 3secs.
 
 
#### Thymeleaf DATA-DRIVEN (`/biglist-datadriven.thymeleaf`)
 
```
$ curl http://localhost:8080/biglist-datadriven.thymeleaf > output
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100 76.6M    0 76.6M    0     0  23.6M      0 --:--:--  0:00:03 --:--:-- 23.6M
```

 Latency: started receiving output almost instantly, finished receiving the whole 76M in 3secs.
 
 
 
 