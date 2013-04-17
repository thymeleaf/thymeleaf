
Thymeleaf Testing Library
=========================

-------------------------------------------------------------

Status
------

This is an auxiliary testing library, not directly a part of the Thymeleaf core but part of the project, developed and supported by the [Thymeleaf Team](http://www.thymeleaf.org/team.html).

Current versions: 

  * **Version 2.0.0** - for Thymeleaf 2.0 (requires 2.0.16+) 


License
-------

This software is licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).


Requirements
------------

  *   Thymeleaf **2.0.14+**
  *   Attoparser **1.2+**


Maven info
----------

  *   groupId: `org.thymeleaf`
  *   artifactId: `thymeleaf-testing`


Features
--------

  *   Works as an independent library, callable from multiple testing frameworks like e.g. JUnit.
  *   Tests only the view layer: template processing and its result.
  *   Includes benchmarking facilities: all tests are timed, times are aggregated.
  *   Highly extensible and configurable
  *   Versatile testing structures: test sequences, iteration, concurrent execution.
  *   Based on interfaces, out-of-the-box *standard test resolution* allows:
      * Easy specification of tests as simple text files, sequences as folders.
	  * Advanced test configuration.
	  * Test inheritance.
	  * *Lenient result matching*, ignoring excess unneeded whitespace etc.

------------------------------------------------------------------------------


## Usage ##

The testing framework can be used with just two lines of code:

```java
final TestExecutor executor = new TestExecutor();
executor.execute("test");
```

Note how here we are only specifying the name of the *testable* to be resolved: `"test"` (more on testables later). But anyway this is only two lines, and therefore we are accepting some defaults, namely:

   * Dialects. By default only the *Standard Dialect* will be enabled.
   * Messages. By default no internationalization messages will be available.
   * Resolvers. By default the *standard test resolution* mechanism will be used (more on it later).
   * Reporters. By default a console reporter will be used.

Let's see the whole `TestExecutor` configuration process:

```java
final List<IDialect> dialects = ...
final Map<Locale,Properties> messages = ...
final ITestableResolver resolver = ...
final ITestReporter reporter = ...

final TestExecutor executor = new TestExecutor();
executor.setDialects(dialects);
executor.setMessages(messages);
executor.setTestableResolver(resolver);
executor.setReporter(reporter);
executor.execute("test");
```

The meaning and working of the *dialects* an *messages* properties is pretty obvious and straightforward. As for the *resolvers* and *reporters*, we will see more on them in next sections.

## API ##

The Thymeleaf testing framework is based on interfaces, and therefore defines an API that specifies the diverse structures involved in the testing process:

   * Test structures at the `org.thymeleaf.testing.templateengine.testable` package:
      * `ITestable` implemented by objects designed for being *tested*, be it a simple test or an aggregating structure of any kind. Every other interface in this package extends this one.
         * Abstract base implementation: `AbstractTestable` class.
      * `ITest` represents tests, the basic unit for testing and simplest `ITestable` implementation. Tests are in charge not only of containing test data, but also of evaluating/checking test results.
         * Abstract base implementation: `AbstractTest` class.
         * Default implementation (including result evaluation): `Test` class.
      * `ITestSequence` represents sequences of tests, test structures or any combination of theses (sequences of objects implementing the `ITestable` interface).
         * Default implementation: `TestSequence` class.
      * `ITestIterator` represents objects capable of iterating (executing a number of times) other test structures.
         * Default implementation: `TestIterator` class.
      * `ITestParallelizer` represents objects capable of using several threads for executing the same test structure in each thread, concurrently.
         * Default implementation: `TestParallelizer` class.
      * `ITestResult` represents the results of executing a test and evaluating its results.
         * Default implementation: `TestResult` class.
   * Interfaces at the `org.thymeleaf.testing.templateengine.resolver` package:
      * `ITestableResolver` implemented by objects in charge of *resolving testables*, this is, of creating the `ITestable` objects and structures that will be executed. A *standard test resolution* implementation is provided out of the box that builds these testable structures from text files and their containing folders in disk.
   * Interfaces at the `org.thymeleaf.testing.templateengine.report` package:
      * `ITestReporter` implemented by objects in charge of reporting the results of executing tests, sequences, etc. along with their associated execution times.

In addition to these interfaces, this testing API also includes the `org.thymeleaf.testing.templateengine.engine.TestExecutor` class, in charge of executing the test structures.


## Test Reporters ##

Test Reporters implement the `org.thymeleaf.testing.templateengine.report.ITestReporter` interface and allow the engine to report when a test has been executed, the execution result, and also the execution time (aggregated in the case of a structure).

Out of the box, thymeleaf-testing provides two implementations at the `org.thymeleaf.testing.templateengine.report` package:
   * `AbstractTextualTestReporter`, an abstract text-based implementation suitable for easily creating reporters that output text.
   * `ConsoleTestReporter`, extending the former, which writes these *text report items* to the console.

It's easy to create new reporters that could write test results to different formats like CSV, Excel, etc. or even write results to a database.


## Testable Resolvers ##

Standard test resolution is provided by means of two implementations of the `org.thymeleaf.testing.templateengine.resolver.ITestableResolver` interface, both living at the `org.thymeleaf.testing.templateengine.resolver` package:

   * `StandardClassPathTestableResolver`
   * `StandardFileTestableResolver`




