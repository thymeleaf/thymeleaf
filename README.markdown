
Thymeleaf Testing Library
=========================

-------------------------------------------------------------

Status
------

This is an auxiliary testing library, not directly a part of the Thymeleaf core but part of the project, developed and supported by the [Thymeleaf Team](http://www.thymeleaf.org/team.html).

Current versions: 

  * **Version 2.0.0-beta1** - for Thymeleaf 2.0 (requires 2.0.16+) 


License
-------

This software is licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).


Requirements
------------

  *   Thymeleaf **2.0.16+**
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

Test structures at the `org.thymeleaf.testing.templateengine.testable` package:

| Interface                  | Description |Base Impl| Default Impl|
|----------------------------|-------------|------------------|-------------|
|`ITestable`                 | Implemented by objects designed for being *tested*, be it a simple test or an aggregating structure of any kind. Every other interface in this package extends this one. | `AbstractTestable` | |
|`ITest`                 | Represents tests, the basic unit for testing and simplest `ITestable` implementation. Tests are in charge not only of containing test data, but also of evaluating/checking test results. | `AbstractTest` | `Test` |
|`ITestSequence`                 | Represents sequences of tests, test structures or any combination of theses (sequences of objects implementing the `ITestable` interface). |  | `TestSequence` |
|`ITestIterator`                 | Represents objects capable of iterating (executing a number of times) other test structures. |  | `TestIterator` |
|`ITestParallelizer`                 | Represents objects capable of using several threads for executing the same test structure in each thread, concurrently. |  | `TestParallelizer` |
|`ITestResult`                 | Represents the results of executing a test and evaluating its results. |  | `TestResult` |


Interfaces at the `org.thymeleaf.testing.templateengine.resolver` package:

| Interface                  | Description |
|----------------------------|-------------|
|`ITestableResolver`                 | Implemented by objects in charge of *resolving testables*, this is, of creating the `ITestable` objects and structures that will be executed. A *standard test resolution* implementation is provided out of the box that builds these testable structures from text files and their containing folders in disk. |


Interfaces at the `org.thymeleaf.testing.templateengine.report` package:

| Interface                  | Description |
|----------------------------|-------------|
|`ITestReporter`                 | Implemented by objects in charge of reporting the results of executing tests, sequences, etc. along with their associated execution times. |


In addition to these interfaces, this testing API also includes the `org.thymeleaf.testing.templateengine.engine.TestExecutor` class, in charge of executing the test structures.


## Test Reporters ##

Test Reporters implement the `org.thymeleaf.testing.templateengine.report.ITestReporter` interface and allow the engine to report when a test has been executed, the execution result, and also the execution time (aggregated in the case of a structure).

Out of the box, thymeleaf-testing provides two implementations at the `org.thymeleaf.testing.templateengine.report` package:
   * `AbstractTextualTestReporter`, an abstract text-based implementation suitable for easily creating reporters that output text.
   * `ConsoleTestReporter`, extending the former, which writes these *text report items* to the console.

Console reporting looks like this:

```
[2013-04-19 02:23:29][KE1OMC][main] [sequence:start][maintests]
[2013-04-19 02:23:29][KE1OMC][main]   [test:end][text.test][175729614][OK] Test executed OK.  Time: 175729614ns (175ms).
[2013-04-19 02:23:29][KE1OMC][main]   [test:end][utext.test][3365839][OK] Test executed OK.  Time: 3365839ns (3ms).
[2013-04-19 02:23:29][KE1OMC][main] [sequence:end][maintests][2][2][179095453] Tests OK: 2 of 2. Sequence executed in 179095453ns (179ms)
```


It's easy to create new reporters that could write test results to different formats like CSV, Excel, etc. or even write results to a database.


## Testable Resolvers ##

Standard test resolution is provided by means of two implementations of the `org.thymeleaf.testing.templateengine.resolver.ITestableResolver` interface, both living at the `org.thymeleaf.testing.templateengine.resolver` package. They are basically two flavors of the same resolution mechanism:

   * `StandardClassPathTestableResolver` for resolving tests from the classpath.
   * `StandardFileTestableResolver` for resolving tests from anywhere in the file system.


### The Standard Resolution mechanism ###

The standard test resolution mechanism works like this:

   * Tests are specified in text files, following a specific directive-based format.
   * Folders can be used for grouping tests into sequences, iterators or parallelizers.
   * Test ordering and sequencing can be configured through the use of *index files*.

Let's see each topic separately.


#### Test file format ####

A test file is a text file with a name ending in `.test` It can look like this:

```
%CONTEXT
onevar = 'Goodbye!'
%TEMPLATE_MODE HTML5
%INPUT
<!DOCTYPE html>
<html>
  <body>
      <h1 th:text="${onevar}">Hello!</h1>
  </body>
</html>
%OUTPUT 
<!DOCTYPE html>
<html>
  <body>
      <h1>Goodbye!</h1>
  </body>
</html>
```

We can see there that tests are configured by means of *directives*, and that this directives are specified in the form of `%NAME`. The available directives are:

*Test Configuration:*

| Name                       | Description |
|----------------------------|-------------|
|`%NAME`                     | Name of the test, in order to make it identifiable in reports/logs. This is *optional*. If not specified, the file name will be used as test name. |
|`%CONTEXT`                  | Context variables to be made available to the tested template. These variables should be specified in the form of *properties* (same syntax as Java `.properties` files), and property values will be considered OGNL expressions. You can read more about context specification below.<br />Also note that defining context variables is *optional*. |

*Test input:*

| Name                       | Description |
|----------------------------|-------------|
|`%INPUT`                    | Test input, in the form of an HTML template or fragment. This parameter is *required*. |
|`%INPUT[qualif]`              | Additional inputs can be specified by adding a *qualifier* to its name. These additional inputs can be used as external template fragments in `th:include`, `th:substituteby`, etc. |
|`%FRAGMENT`                 | Fragment specification (in the same format as used in `th:include` attributes) to be applied on the test input before processing. *Optional*. |
|`%TEMPLATE_MODE`            | Template mode to be used: `HTML5`, `XHTML`, etc. |
|`%CACHE`                    | Whether template cache should be `on` or `off`. If cache is *on*, the input for this test will be parsed only the first time it is processed.|

*Test expected output:*

| Name                       | Description |
|----------------------------|-------------|
|`%OUTPUT`                   | Test output to be expected, if we expect template execution to finish successfully. Either this or the `%EXCEPTION` directive must be specified. |
|`%EXACT_MATCH`              | Whether *exact matching* should be used. By default, *lenient matching* is used, which means excess whitespace (*ignorable whitespace*) will not be taken into account for matching test results. Setting this flag to `true` will perform exact *character-by-character* matching. |
|`%EXCEPTION`                | Exception to be expected, if we expect template execution to raise an exception. Either this or the `%OUTPUT` directive must be specified.  |
|`%EXCEPTION_MESSAGE_PATTERN`| Pattern (in `java.util.regex.Pattern` syntax) expected to match the message of the exception raised during template execution. This directive needs the `%EXCEPTION` directive to be specified too. |

*Inheritance:*

| Name                       | Description |
|----------------------------|-------------|
|`%EXTENDS`                  | Test specification (in a format understandable by the implementation of `ITestableResolver` being used) from which this test must inherit all its directives, overriding only those that are explicitly specified in the current test along with this `%EXTENDS` directive.<br />Example: `%EXTENDS test/bases/base1.test` |

Also, any line starting by `#` in a test file will be considered **a comment** and simply ignored.



##### More on context specification #####

As already said, context is specified like:

```
%CONTEXT
onevar = 'Goodbye!'
twovar = 'Hello!'
```

And those literals are specified between commas because all context values are in fact OGNL expressions, so we could in fact use previous variables in new ones:

```
%CONTEXT
onevar = 'Hello, '
twovar = onevar + 'World!'
```

We can also create objects, and set its properties:

```
%CONTEXT
user = new com.myapp.User()
user.firstName = 'John'
user.lastName = 'Apricot'
```

Also maps:

```
%CONTEXT
user = new java.util.HashMap()
user['firstName'] = 'John'
user['lastName'] = 'Apricot'
```

We can set request parameters (multivalued), request attributes, session attributes and servlet context attributes using the `param`, `request`, `session` and `application` prefixes:
```
%CONTEXT
session.userLogin = 'japricot'
param.selection = 'admin'
param.selection = 'manager'
```

Utility objects like `#strings`, `#dates`, `#lists`, etc. can be used:
```
%CONTEXT
request.timestamp = #calendars.createNow()
```

Finally, note that **context variables are inherited** when a test is set as an extension of another one by means of the `%EXTENDS` directive.


#### Test folder format ####

The folders that contain tests will themselves be resolved as test structures, and their names will be used to indicate the existence of *iterations* or *parallelizers*.

Imagine we have this folder structure at our classpath:

    /tests
     |
     +->/warmup
     |   |
     |   +->testw1.test
     |   |
     |   +->testw2.test
     |
     +->/expressions-iter-10
         |
         +->expression1.test
         |
         +->/expression-stress-parallel-3
             |
             +->expression21.test
             |
             +->expression22.test

When we ask the standard test resolver to resolve `"tests"`, it will create the following *testable* structure:

   * A *Test Sequence* called `tests`, containing:
     * A *Test Sequence* called `warmup` containing:
       * Two tests: `testw1.test` and `testw2.test`.
     * A *Test Iterator* called `expressions`, iterated 10 times, containing:
       * One test: `expression1.test`.
       * A *Test Parallelizer* called `expression-stress`, executed by 3 concurrent threads, containing:
         * Two tests: `expression21.test` and `expression22.test`.

So, as can be extracted from the example above:

   * Any folder will create a *test sequence*
   * A folder with a name ending in `-iter-X` will create a *test iterator* iterating `X` times.
   * A folder with a name ending in `-parallel-X` will create a *test parallelizer* executing its contents with `X` concurrent threads.


#### Test index files ####

Folders can contain *index files*. These files have to be named `test.index` and allow developers to specify which tests and in which order they want to be executed. They also allow the specification of iteration or parallelization without having to change the name of a folder.

Example contents for a `test.index` file:

```
exp.test
include.test
text.test [iter-20]
test2 [parallel-3]
```

According to the above index, the `text.test` file will be executed in third position, 20 times. And the `test2` folder will be considered a parallelizer, just as if it was called `test2-parallel-3` instead.


### Extending the standard test resolution mechanism ###

The standard resolution mechanism can be extended in several ways, by means of a series of *setter* methods in the `StandardClassPathTestableResolver` and `StandardFileTestableResolver` classes which allow developers to configure how each step of test resolution is performed:

| Setter                       | Description |
|----------------------------|-------------|
|`setTestReader(IStandardTestReader)`      | Specifies the implementation that will be in charge of reading test files and return its raw data. Default implementation is the `StandardTestReader` class. |
|`setTestEvaluator(IStandardTestEvaluator)` | Specifies the implementation that will be in charge of evaluating the *raw data* returned by the test reader into the different values that will be used for building the test object. Default implementation is `StandardTestEvaluator`. |
|`setTestBuilder(IStandardTestBuilder)` | Specifies the implementation that will be in charge of actually building test objects from the values evaluated by the *test evaluator* in the previous step. Default implementation is `StandardTestBuilder`. |


