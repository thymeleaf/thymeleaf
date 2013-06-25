
Thymeleaf Test Set
==================

This repository contains a set of tests designed for thymeleaf maintenance. Tests included cover expressions, standard dialect attributes, Spring integration and also most of the example applications, among other topics.

Tests are designed for use with the **thymeleaf-testing library** and are launched using JUnit. The JUnit test classes are contained at the `src/main/java` folder, one for each group of functionality, and might be accompanied by model or configuration classes. Test files are contained at the `src/main/resources` folder and each JUnit test class resolves and executes a subset of these resources.



Launching tests
---------------

In order to launch tests from maven, execute:

```
mvn -P [profile] clean compile test
```

Where `[profile]` is one of:

   * `2.0` for Thymeleaf 2.0.18-SNAPSHOT
   * `2.1` for Thymeleaf 2.1.0-SNAPSHOT

Each test reported as *successful* or *failed* refers in fact to a set of thymeleaf tests launched by a test method in a JUnit class. Refer to the specific report for that JUnit test class in order to examine which test file or files failed and under which circumstances.

 
 
