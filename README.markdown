
Thymeleaf Test Set
==================

This repository contains a set of tests designed for thymeleaf maintenance. Tests included cover expressions, standard dialect attributes, Spring integration and also most of the example applications, among other topics.

Tests under the `org.thymeleaf.engine` package are designed for use with the **thymeleaf-testing library**.

All tests and are launched using JUnit. The JUnit test classes are contained at several folders, depending on the thymelaef version they refer to (activated with the corresponding profile):

   * `src/test-common/java` contains tests executed for all thymeleaf versions.
   * `src/test-20/java` for thymeleaf 2.0.x
   * `src/test-21/java` for thymeleaf 2.1.x

Inside these folders, tests are divided into subfolders, one for each group of functionality, and might be accompanied by model or configuration classes.

Test files (among them, `*.thtest` files for the *engine tests*) are contained in similarly organized folders:

   * `src/test-common/resources`
   * `src/test-20/resources`
   * `src/test-21/resources`


Launching tests
---------------

In order to launch tests from maven, execute:

```
mvn -P [profile] clean compile test
```

Where `[profile]` is one of:

   * `2.0` for the latest SNAPSHOT in the Thymeleaf 2.0 branch
   * `2.1` for the latest SNAPSHOT in the Thymeleaf 2.1 branch

Each test reported as *successful* or *failed* refers in fact to a set of thymeleaf tests launched by a test method in a JUnit class. Refer to the specific report for that JUnit test class in order to examine which test file or files failed and under which circumstances.

 
 
