
Thymeleaf - Spring Security 3 integration module
================================================

------------------------------------------------------------------------------

Status
------

This is a *thymeleaf extras* module, not a part of the Thymeleaf core (and as
such following its own versioning schema), but fully supported by the 
Thymeleaf team.

Current version: **1.0.0-SNAPSHOT**


License
-------

This software is licensed under the [Apache License 2.0]
(http://www.apache.org/licenses/LICENSE-2.0.html).


Requirements
------------

  *   Thymeleaf **2.0.12+**
  *   Spring Security version **3.0.x+**
  *   Web environment (Spring Security integration cannot work offline)


Maven info
----------

  *   groupId: `org.thymeleaf.extras`   
  *   artifactId: `thymeleaf-extras-springsecurity3`


Features
--------

  This module provides a new dialect called `org.thymeleaf.extras.springsecurity3.dialect.SpringSecurityDialect`,
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
