
Thymeleaf Testing Library
=========================

-------------------------------------------------------------

Status
------

This is an auxiliary testing library, not directly a part of the Thymeleaf core but part of the project, developed and supported by the [Thymeleaf Team](http://www.thymeleaf.org/team.html).

Current versions: 

  * **Version 2.0.2** - for Thymeleaf 2.0 (requires thymeleaf 2.0.16+) 


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

  *   Works as an independent library, **callable from multiple testing frameworks** like e.g. JUnit.
  *   **Tests only the view layer**: template processing and its result.
  *   Includes **benchmarking** facilities: all tests are timed, times are aggregated.
  *   Highly extensible and configurable
  *   Versatile testing artifacts: test sequences, iteration, concurrent execution...
  *   Based on interfaces. Out-of-the-box *standard test resolution* allows:
      * Easy specification of tests as simple text files, sequences as folders.
	  * Advanced test configuration.
	  * Test inheritance.
	  * *Lenient* result matching, ignoring excess unneeded whitespace etc.
  * **Spring Framework** and **Spring Security** integration.

------------------------------------------------------------------------------


## Usage ##

The testing framework can be used with just two lines of code:

```java
final TestExecutor executor = new TestExecutor();
executor.execute("classpath:test");
```

Note how here we are only specifying the name of the *testable* to be resolved: `"classpath:test"`, which is a folder called `test` in classpath. (more on testables later). But anyway this is only two lines, and therefore we are accepting some defaults, namely:

   * Dialects. By default only the *Standard Dialect* will be enabled.
   * Resolvers. By default the *standard test resolution* mechanism will be used (more on it later).
   * Reporters. By default a console reporter will be used.

Let's see the whole `TestExecutor` configuration process:

```java
final List<IDialect> dialects = ...
final ITestableResolver resolver = ...
final ITestReporter reporter = ...

final TestExecutor executor = new TestExecutor();
executor.setDialects(dialects);
executor.setTestableResolver(resolver);
executor.setReporter(reporter);
executor.execute("classpath:test");
```

The meaning and working of the *dialects* property is pretty obvious and straightforward. As for the *resolvers* and *reporters*, we will see more on them in next sections.

## API ##

The Thymeleaf testing framework is based on interfaces, and therefore defines an API that specifies the diverse artifacts involved in the testing process:

Test artifacts at the `org.thymeleaf.testing.templateengine.testable` package:

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
|`ITestableResolver`         | Implemented by objects in charge of *resolving testables*, this is, of creating the `ITestable` objects and structures that will be executed. A *standard test resolution* implementation is provided out of the box that builds these testable structures from text files and their containing folders in disk. |


Interfaces at the `org.thymeleaf.testing.templateengine.resource` package:

| Interface                  | Description |
|----------------------------|-------------|
|`ITestResource`             | Implemented by objects representing test resources like testable artifact locations, test input text, test output text, etc. |
|`ITestResourceResolver`     | Implemented by objects in charge of resolving test resources, used by *testable resolvers*. For example, a *testable resolver* (`ITestableResolver`) will use an `ITestResourceResolver` implementation for converting the resource name `"classpath:test"` into a valid `ITestResource` object. |


Interfaces at the `org.thymeleaf.testing.templateengine.report` package:

| Interface                  | Description |
|----------------------------|-------------|
|`ITestReporter`             | Implemented by objects in charge of reporting the results of executing tests, sequences, etc. along with their associated execution times. |


Interfaces at the `org.thymeleaf.testing.templateengine.context` package:

| Interface                  | Description |
|----------------------------|-------------|
|`ITestContext`              | Implemented by objects representing the context (variables, locale, etc.) to be used for executing tests. |
|`IProcessingContextBuilder` | Implemented by objects in charge of creating the `IContext` to be used for test excecution from the resolved `ITestContext`. |


Interfaces at the `org.thymeleaf.testing.templateengine.messages` package:

| Interface                  | Description |
|----------------------------|-------------|
|`ITestMessages`             | Implemented by objects representing the set of externalized (or *internationalized*) messages to be used for executing tests. |


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


### Integrating with JUnit ###

The easiest way to integrate with JUnit is to use JUnit tests to launch sets of tests, using afterwards JUnit's assertion mechanism to check results:

```java
final TestExecutor executor = new TestExecutor();
executor.execute("mytestset");
Assert.assertTrue(executor.getReporter().isAllOK());
```

...or the equivalent, more convenient:

```java
final TestExecutor executor = new TestExecutor();
executor.execute("mytestset");
Assert.assertTrue(executor.isAllOK());
```

Note that this *test set* can be a single test:

```java
final TestExecutor executor = new TestExecutor();
executor.execute("mytestset/onetest.thtest");
Assert.assertTrue(executor.isAllOK());
```

You can reuse your test executor by resetting its reporter after each execution:

```java
final TestExecutor executor = new TestExecutor();
executor.execute("mytestset/onetest.thtest");
Assert.assertTrue(executor.isAllOK());
executor.getReporter().reset();
```

...or the equivalent, more convenient:

```java
final TestExecutor executor = new TestExecutor();
executor.execute("mytestset/onetest.thtest");
Assert.assertTrue(executor.isAllOK());
executor.reset();
```

So you can use your executor for executing several *test sets* (or single tests) in the same JUnit test method:

```java
final TestExecutor executor = new TestExecutor();
...
executor.execute("mytestset/onetest.thtest");
Assert.assertTrue(executor.isAllOK());
executor.reset();
...
executor.execute("mytestset/twotest.thtest");
Assert.assertTrue(executor.isAllOK());
executor.reset();
...
executor.execute("anothertestset");
Assert.assertTrue(executor.isAllOK());
executor.reset();
```

Note that each execution of `executor.execute(...)` will create its own `TemplateEngine` instance and resolvers, an so no templates will be cached from one execution to the next.


## Testable Resolvers ##

Standard test resolution is provided by means of an implementation of the `org.thymeleaf.testing.templateengine.resolver.ITestableResolver` interface called `org.thymeleaf.testing.templateengine.resolver.StandardTestableResolver`.


### The Standard Resolution mechanism ###

The standard test resolution mechanism works like this:

   * Tests are specified in text files, following a specific directive-based format.
   * Folders can be used for grouping tests into sequences, iterators or parallelizers.
   * Test ordering and sequencing can be configured through the use of *index files*.

Let's see each topic separately.


#### Test file format ####

A test file is a text file with a name ending in `.thtest` It can look like this:

```
%TEMPLATE_MODE HTML5
# ------------ separator comment -----------
%CONTEXT
onevar = 'Goodbye,'
# ------------------------------------------
%MESSAGES
one.msg = Crisis
# ------------------------------------------
%INPUT
<!DOCTYPE html>
<html>
  <body>
      <span th:text="${onevar}">Hello,</span>
      <span th:text="#{one.msg}">World!</span>
  </body>
</html>
# ------------------------------------------
%OUTPUT 
<!DOCTYPE html>
<html>
  <body>
      <span>Goodbye,</span>
      <span>Crisis</span>
  </body>
</html>
```

We can see there that tests are configured by means of *directives*, and that these directives are specified in the form of `%DIRECTIVENAME`. The available directives are:

*Test Configuration:*

| Name                       | Description |
|----------------------------|-------------|
|`%NAME`                     | Name of the test, in order to make it identifiable in reports/logs. This is *optional*. If not specified, the file name will be used as test name. |
|`%CONTEXT`                  | Context variables to be made available to the tested template. These variables should be specified in the form of *properties* (same syntax as Java `.properties` files), and **property values are parsed and executed as OGNL expressions**. Specifying context variables is *optional* and they can be inherited from parent tests.<br />You can read more about the specification of context variables below.|
|`%MESSAGES`                 | Default (no locale-specific) externalized/internationalized messages to be made available to the tested template. These variables should be specified in the form of *properties* (same syntax as Java `.properties` files). Specifying messages is *optional* and they can be inherited from parent tests. |
|`%MESSAGES[es]`             | Same as `%MESSAGES`, but specifying messages for a specific locale: `es`, `en_US`, `gl_ES`, etc. |

*Test input:*

| Name                       | Description |
|----------------------------|-------------|
|`%INPUT`                    | Test input, in the form of an HTML template or fragment. A resource name can also be specified between parenthesis, like `%INPUT (file:/home/user/myproject/src/main/resources/templates/mytemplate.html)`. This parameter is *required*. |
|`%INPUT[qualif]`              | Additional inputs can be specified by adding a *qualifier* to its name. These additional inputs can be used as external template fragments in `th:include="qualif"`, `th:substituteby="qualif :: frag"`, etc. |
|`%FRAGMENT`                 | Fragment specification (in the same format as used in `th:include` attributes) to be applied on the test input before processing. *Optional*. |
|`%TEMPLATE_MODE`            | Template mode to be used: `HTML5`, `XHTML`, etc. |
|`%CACHE`                    | Whether template cache should be `on` or `off`. If cache is *on*, the input for this test will be parsed only the first time it is processed.|

*Test expected output:*

| Name                       | Description |
|----------------------------|-------------|
|`%OUTPUT`                   | Test output to be expected, if we expect template execution to finish successfully. Either this or the `%EXCEPTION` directive must be specified. A resource name can also be specified between parenthesis, like `%OUTPUT (file:/home/user/myproject/src/test/resources/results/mytemplate-res.html)` |
|`%EXACT_MATCH`              | Whether *exact matching* should be used. By default, *lenient matching* is used, which means excess whitespace (*ignorable whitespace*) will not be taken into account for matching test results. Setting this flag to `true` will perform exact *character-by-character* matching. |
|`%EXCEPTION`                | Exception to be expected, if we expect template execution to raise an exception. Either this or the `%OUTPUT` directive must be specified.  |
|`%EXCEPTION_MESSAGE_PATTERN`| Pattern (in `java.util.regex.Pattern` syntax) expected to match the message of the exception raised during template execution. This directive needs the `%EXCEPTION` directive to be specified too. |

*Inheritance:*

| Name                       | Description |
|----------------------------|-------------|
|`%EXTENDS`                  | Test specification (in a format understandable by the implementations of `ITestableResolver` and `ITestResourceResolver` being used) from which this test must inherit all its directives, overriding only those that are explicitly specified in the current test along with this `%EXTENDS` directive.<br />Examples: `%EXTENDS classpath:test/bases/base1.test` |

Also, any line starting by `#` in a test file will be considered **a comment** and simply ignored.


##### More on resource resolution #####

The standard mechanism for resource resolution allows you to:

   * Specify resources in classpath: `classpath:tests/mytest.thtest`
   * Specify resources in the filesystem: `file:/home/myuser/tests/mytest.thtest` or `file:C\Users\myser\tests\mytest.thtest`.

Also, in some scenarios (like the `%EXTENDS` directive in test files) resource resolution can be relative to the current resource:

```
%EXTENDS ../../base-tests/base.thtest
```

Note that, when using resource resolution in an `%INPUT` or `%OUTPUT` directive, resource names must be specified between parenthesis:

```
%INPUT (file:/home/user/myproject/src/main/resources/templates/mytemplate.html)
```

##### More on context specification #####

As already said, context is specified like:

```properties
%CONTEXT
onevar = 'Goodbye!'
twovar = 'Hello!'
```

And those literals are specified between commas because all context values are in fact **OGNL** expressions, so we could in fact use previous variables in new ones:

```properties
%CONTEXT
onevar = 'Hello, '
twovar = onevar + 'World!'
```

We can also create objects, and set its properties:

```properties
%CONTEXT
user = new com.myapp.User()
user.firstName = 'John'
user.lastName = 'Apricot'
```

Also maps:

```properties
%CONTEXT
user = #{ 'firstName' : 'John', 'lastName' : 'Apricot' }
```

We can set request parameters (multivalued), request attributes, session attributes and servlet context attributes using the `param`, `request`, `session` and `application` prefixes:

```properties
%CONTEXT
session.userLogin = 'japricot'
param.selection = 'admin'
param.selection = 'manager'
```

Utility objects like `#strings`, `#dates`, `#lists`, etc. can be used:

```properties
%CONTEXT
request.timestamp = #calendars.createNow()
```

Finally, note that **context variables are inherited** when a test is set as an extension of another one by means of the `%EXTENDS` directive.

##### Selecting locale for execution #####

The locale to be used for execution can be selected by giving value to a context variable called `locale`:

```properties
%CONTEXT
locale = 'gl_ES'
```


#### Test folder format ####

The folders that contain tests will themselves be resolved as test structures, and their names will be used to indicate the existence of *iterations* or *parallelizers*.

Imagine we have this folder structure at our classpath:

    /tests
     |
     +->/warmup
     |   |
     |   +->testw1.thtest
     |   |
     |   +->testw2.thtest
     |
     +->/expressions-iter-10
         |
         +->expression1.thtest
         |
         +->/expression-stress-parallel-3
             |
             +->expression21.thtest
             |
             +->expression22.thtest

When we ask the standard test resolver to resolve that folder called `tests` (with `"classpath:tests"`), it will create the following *testable artifact* structure:

   * A *Test Sequence* called `tests`, containing:
     * A *Test Sequence* called `warmup` containing:
       * Two tests: `testw1.thtest` and `testw2.thtest`.
     * A *Test Iterator* called `expressions`, iterated 10 times, containing:
       * One test: `expression1.thtest`.
       * A *Test Parallelizer* called `expression-stress`, executed by 3 concurrent threads, containing:
         * Two tests: `expression21.thtest` and `expression22.thtest`.

So, as can be extracted from the example above:

   * In general, folders will be resolved as *test sequence*
   * Folders with a name ending in `-iter-X` will be resolved as a *test iterator* iterating `X` times.
   * Folders with a name ending in `-parallel-X` will be resolved as a *test parallelizer* executing its contents with `X` concurrent threads.


#### Test index files ####

Index files are files with a name ending with `.thindex`. They allow developers to specify which tests and in which order they want to be executed, this is, in practice, create *a test sequence*. They also allow the specification of iteration or parallelization without having to change the name of a folder.

Example contents for a `sample.thindex` file:

```
exp.thtest
include.thtest
text.thtest [iter-20]
test2 [parallel-3]
```

According to the above index, the `text.thtest` file will be executed in third position, 20 times. And the `test2` folder will be considered a parallelizer, just as if it was called `test2-parallel-3` instead.

Also, note that when a folder includes a special file named `folder.thindex`, this will be considered to specify the sequence in which the folder files have to be executed, even if this `folder.thindex` file isn't explicitly called.


### Extending the standard test resolution mechanism ###

The standard resolution mechanism can be extended in several ways, by means of a series of *setter* methods in the `StandardTestableResolver` class which allow developers to configure how each step of test resolution is performed:

| Setter                       | Description |
|----------------------------|-------------|
|`setTestReader(IStandardTestReader)`      | Specifies the implementation that will be in charge of reading test files and return its raw data. Default implementation is the `StandardTestReader` class. |
|`setTestEvaluator(IStandardTestEvaluator)` | Specifies the implementation that will be in charge of evaluating the *raw data* returned by the test reader into the different values that will be used for building the test object. Default implementation is `StandardTestEvaluator`. |
|`setTestBuilder(IStandardTestBuilder)` | Specifies the implementation that will be in charge of actually building test objects from the values evaluated by the *test evaluator* in the previous step. Default implementation is `StandardTestBuilder`. |
|`setTestResourceResolver(ITestResourceResolver)` | Specifies the implementation that will be in charge of actually resolving resources (including testable artifacts, `%EXTENDS` directives, `.thindex` entries, etc.). |



## Spring integration ##

In order to execute thymeleaf tests using the **SpringStandard** dialect in its entirety, we need to activate certain Spring mechanisms that support some Spring-integrated processors included in this dialect (like `th:field`).

Initialization would look like this:

```java
final List<IDialect> dialects = new ArrayList<IDialect>();
dialects.add(new SpringStandardDialect());

final SpringWebProcessingContextBuilder springPCBuilder = new SpringWebProcessingContextBuilder();

final TestExecutor executor = new TestExecutor();
executor.setProcessingContextBuilder(springPCBuilder);
executor.setDialects(dialects);

executor.execute("tests");
```

Pay special attention to that instantiation of `org.thymeleaf.testing.templateengine.context.web.SpringWebProcessContextBuilder`. That is the class which will activate the needed Spring mechanisms.

This Spring-based context builder will try to initialize a Spring application context from an `applicationContext.xml` file present in the classpath. The name of this file can be overridden or even set to `null` if we do not wish to initialize any beans:

```java
final IProcessingContextBuilder springPCBuilder = new SpringWebProcessingContextBuilder();
springPCBuilder.setApplicationContextConfigLocation("classpath:springConfig/spring.xml");
```

### Model binding ###

If we want to test a page including bindings like, for example, a form with a *form-backing bean* (or *command*), we can specify the context variables on which we desire to create bindings by means of the specification of the `binding` variable:

```properties
%CONTEXT
user = new my.company.User()
user.name = 'John'
user.surname = 'Apricot'
# We will create a binding for the 'user' variable
binding = 'user'
# Could be multivalued:
# binding = {'user','configuration'}
```

We could directly use the `model` variable name. This would be equivalent to the previous context definition:

```properties
%CONTEXT
model = new my.company.User()
model.name = 'John'
model.surname = 'Apricot'
```


#### Initializing bindings: property editors ####

The `SpringWebProcessContextBuilder` class can be overridden if we need to initialize bindings in our own way, for example for registering *property editors*.


Let's see an example of this extension:

```java

public class STSMWebProcessingContextBuilder 
        extends SpringWebProcessingContextBuilder {

    public STSMWebProcessingContextBuilder() {
        super();
    }
    
    protected void initBinder(
            final String bindingVariableName, final Object bindingObject,
            final ITest test, final DataBinder dataBinder, final Locale locale, 
            final Map<String,Object> variables) {
        
        final String dateformat = test.getMessages().computeMessage(locale, "date.format", null);
        final SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
        sdf.setLenient(false);
        dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, false));
        
    }
    
}
```


### Spring Security ###

The testing library can also be integrated with Spring Security. This allows the use of the `thymeleaf-extras-springsecurity3` dialect and testing template rendering in different authentication/authorization scenarios.

Example of usage:

```java
final SpringSecurityWebProcessingContextBuilder processingContextBuilder =
        new SpringSecurityWebProcessingContextBuilder();
processingContextBuilder.setApplicationContextConfigLocation(
        "classpath:springsecurity/applicationContext-security.xml");
        
final TestExecutor executor = new TestExecutor();
executor.setProcessingContextBuilder(processingContextBuilder);
executor.setDialects(
        Arrays.asList(new IDialect[] { new SpringStandardDialect(), new SpringSecurityDialect()}));
executor.execute("springsecurity");
```

By default, authentication will be specified in a user/password basis, by means of the `j_username` and `j_password`.

```properties
%CONTEXT
j_username = 'ted'
j_password = 'demo'
```








