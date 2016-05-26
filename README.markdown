
Thymeleaf - Spring Security integration modules
===============================================

------------------------------------------------------------------------------

**[Please make sure to select the branch corresponding to the version of Thymeleaf you are using]**

Status
------

This is a *thymeleaf extras* module, not a part of the Thymeleaf core (and as
such following its own versioning schema), but fully supported by the 
Thymeleaf team.

This repository contains two projects:

  * **thymeleaf-extras-springsecurity3** for integration with Spring Security 3.x
  * **thymeleaf-extras-springsecurity4** for integration with Spring Security 4.x

Current versions: 

  * **Version 3.0.0.RELEASE** - for Thymeleaf 3.0 (requires Thymeleaf 3.0.0+)
  * **Version 2.1.2.RELEASE** - for Thymeleaf 2.1 (requires Thymeleaf 2.1.2+)


License
-------

This software is licensed under the [Apache License 2.0]
(http://www.apache.org/licenses/LICENSE-2.0.html).


Requirements (3.0.x)
--------------------

  *   Thymeleaf **3.0.0+**
  *   Spring Framework version **3.0.x** to **4.3.x**
  *   Spring Security version **3.0.x** to **4.1.x**
  *   Web environment (Spring Security integration cannot work offline)


Maven info
----------

  *   groupId: `org.thymeleaf.extras`   
  *   artifactId: 
    *   Spring Security 3 integration package: `thymeleaf-extras-springsecurity3`
    *   Spring Security 4 integration package: `thymeleaf-extras-springsecurity4`


Distribution packages
---------------------

Distribution packages (binaries + sources + javadoc) can be downloaded from [SourceForge](http://sourceforge.net/projects/thymeleaf/files/thymeleaf-extras-springsecurity/).


Features
--------

This module provides a new dialect called `org.thymeleaf.extras.springsecurity3.dialect.SpringSecurityDialect` or `org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect` (depending on the Spring Security version),
with default prefix `sec`. It includes:
  
  *   New expression utility objects:
    *   `#authentication` representing the Spring Security authentication object
	    (an object implementing the `org.springframework.security.core.Authentication` interface).
	*   `#authorization`: a expression utility object with methods for checking authorization
	    based on expressions, URLs and Access Control Lists.
  *   New attributes:
    *   `sec:authentication="prop"` outputs a `prop` property of the authentication object, similar to the
	    Spring Security `<sec:authentication/>` JSP tag.
    *   `sec:authorize="expr"` or `sec:authorize-expr="expr"` renders the element children (*tag content*)
	    if the authenticated user is authorized to see it according to the specified *Spring Security expression*.
    *   `sec:authorize-url="url"` renders the element children (*tag content*)
	    if the authenticated user is authorized to see the specified URL.
    *   `sec:authorize-acl="object :: permissions"` renders the element children (*tag content*)
	    if the authenticated user has the specified permissions on the specified domain object, according
	    to Spring Source's Access Control List system.

------------------------------------------------------------------------------

	
Configuration
-------------

In order to use the thymeleaf-extras-springsecurity3 or thymeleaf-extras-springsecurity4 
modules in our Spring MVC application,
we will first need to configure our application in the usual way for
Spring + Thymeleaf applications (*TemplateEngine* bean, *template resolvers*, 
etc.), and add the SpringSecurity dialect to our Template Engine so that we
can use the `sec:*` attributes and special expression utility objects:

```xml
    <bean id="templateEngine" class="org.thymeleaf.spring4.SpringTemplateEngine">
      ...
      <property name="additionalDialects">
        <set>
          <!-- Note the package would change to 'springsecurity3' if you are using that version -->
          <bean class="org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect"/>
        </set>
      </property>
	  ...
    </bean>
```

And that's all!




	
Using the expression utility objects
------------------------------------

The `#authentication` object can be easily used, like this:

```html
    <div th:text="${#authentication.name}">
        The value of the "name" property of the authentication object should appear here.
    </div>
```

The `#authorization` object can be used in a similar way, normally in `th:if` or `th:unless`tags:


```html
    <div th:if="${#authorization.expression('hasRole(''ROLE_ADMIN'')')}">
        This will only be displayed if authenticated user has role ROLE_ADMIN.
    </div>
```

The `#authorization` object is an instance of `org.thymeleaf.extras.springsecurity[3|4].auth.Authorization`, see
this class and its documentation to understand all the methods offered.

	
	
Using the attributes
--------------------


Using the `sec:authentication` attribute is equivalent to using the `#authentication` object, but using its
own attribute:

```html
    <div sec:authentication="name">
        The value of the "name" property of the authentication object should appear here.
    </div>
```

The `sec:authorize` and `sec:authorize-expr` attributes are exactly the same. They work equivalently
to a `th:if` that evaluated an `#authorization.expression(...)` expression, by evaluating a 
*Spring Security Expression*:


```html
    <div sec:authorize="hasRole('ROLE_ADMIN')">
        This will only be displayed if authenticated user has role ROLE_ADMIN.
    </div>
```

These *Spring Security Expressions* in `sec:authorize` attributes are in fact Spring EL expressions
evaluated on a SpringSecurity-specific root object containing methods such as `hasRole(...)`, 
`getPrincipal()`, etc.

As with normal Spring EL expressions, Thymeleaf allows you to access a series of objects from them including
the context variables map (the `#vars` object). In fact, you are allowed to surround your access
expression with `${...}` if it makes you feel more comfortable:


```html
    <div sec:authorize="${hasRole(#vars.expectedRole)}">
        This will only be displayed if authenticated user has a role computed by the controller.
    </div>
```

Remember that Spring Security sets a special security-oriented object as expression root, which is why
you would not be able to access the `expectedRole` variable directly in the above expression. 


Another way of checking authorization is `sec:authorize-url`, which allows you to check whether a user
is authorized to visit a specific URL or not:


```html
    <div sec:authorize-url="/admin">
        This will only be displayed if authenticated user can call the "/admin" URL.
    </div>
```

For specifying a specific HTTP method, do:

```html
    <div sec:authorize-url="POST /admin">
        This will only be displayed if authenticated user can call the "/admin" URL
        using the POST HTTP method.
    </div>
```

Finally, there is an attribute for checking authorization using Spring Security's
*Access Control Lists*, which needs the specification of a domain object and the
*permissions* defined on it that we are asking for.


```html
    <div sec:authorize-acl="${obj} :: '1,3'">
        This will only be displayed if authenticated user has permissions "1" and "3"
        on domain object referenced by context variable "obj".
    </div>
```

In this attribute, both domain object and permission specifications are considered
to be thymeleaf *Standard Expressions*.
