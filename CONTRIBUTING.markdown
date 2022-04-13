# Contributing to Thymeleaf

Thymeleaf is released under the Apache 2.0 license. If you would like to
contribute something, or want to hack on the code this document should help you
get started.


## Code of Conduct

This project adheres to the Contributor Covenant
[code of conduct][code-of-coduct].
By participating, you are expected to uphold this code. Please report
unacceptable behavior to [the project leads][thymeleaf-team].


## Using GitHub Issues

We use GitHub issues to track bugs and enhancements.
If you have a general usage question please ask on
[Stack Overflow][stackoverflow].
The Thymeleaf team and the broader community monitor the 
[`thymeleaf`][stackoverflow-thymeleaf] tag.

If you are reporting a bug, please help to speed up problem diagnosis by
providing as much information as possible.
Ideally, that would include a small sample project that reproduces the problem.


## Before submitting a Contribution

Before submitting a contribution that is not an obvious or trivial fix, 
get in contact with the [the project leads][thymeleaf-team] about your
ideas (an email should do). Let us discuss the possibilities with you so that
we make sure your contribution goes in the right direction and aligns with the
project's standards, intentions and roadmap.

Please understand that *not all contributions will be accepted and merged into
the project's repositories*. Talking about your planned contributions with the
project maintainers before creating pull requests can maximize the possibility
of your contributions being accepted.



## Signing the Contributor License Agreement

Before we accept a non-trivial patch or pull request we will need you to
sign a **Contributor License Agreement**.

There are two versions of the CLA:

   * **Individual CLA**: For individuals acting on their own behalf, i.e. not
     being backed by any company or government, and not making their
     contributions potentially under the effect of any contracts, agreements or
     laws that could cause their employeer (or any other entities) claim
     any rights on their contribution.
   * **Corporate CLA**: For corporate entities allowing some of their employees
     to contribute to Thymeleaf on the entity's behalf.

For more information on the CLA and the (very easy) process involving this
step, please have a look at the [Thymeleaf CLA repository][cla].



## Conventions and Housekeeping

### General Guidelines:

  - Obviously, **your code must both compile and work correctly**.
  - All your code should be easy to read and understand by a human. The same
    requirement applies to documentation.
  - Unless for specific artifacts such as documentation translations, all
    code, comments, documentation, names of classes and variables,
    log messages, etc. must be **in English**.
  - All contribured files must include the standard Thymeleaf copyright header.
  - Maximum recommended line length is 120 characters. This is not strictly
    enforced.
  - Indentation should be made with 4 spaces, not tabs. Line feeds should be
    UNIX-like (`\n`).
  - All source files should be pure ASCII, except `.properties` files which
    should be ISO-8859-1.
  - You shall add yourself as _author_ (e.g. Javadoc `@author`) to any files
    that you create or modify substantially (more than cosmetic changes).

### Specific Java Code Gudelines:

  - All your code should compile and run in the current minimum Java version
    of the project.
  - All your code should follow the Java Code Conventions regarding
    variable/method/class naming.
  - Number autoboxing and/or autounboxing is forbidden.
  - Every class should define a constructor, even if it is the no-argument
    constructor, and include a call to `super()`.
  - All method parameters should be declared as `final` so that they cannot be
    changed or reassigned in the method.
  - All non-nullable parameters in public methods should be first validated for
    non-nullity inside the code.
  - Existing Javadoc must be maintained along with performed changes. Addition
    of new Javadoc for public methods or code comments for any non-trivial
    algorithms is always welcome.
  - Writing unit tests for new, existing and modified code is always welcome
    too. For any new algorithms or functionality contributed, or substantial
    modifications made to existing ones, the team might consider these a
    requirement.




[cla]: https://github.com/thymeleaf/thymeleaf-org/blob/CLA_CURRENT/CLA/
[code-of-coduct]: https://github.com/thymeleaf/thymeleaf-org/blob/CoC_CURRENT/CoC/THYMELEAF_CODE_OF_CONDUCT.markdown
[thymeleaf-team]: https://www.thymeleaf.org/team.html
[stackoverflow]: https://stackoverflow.com
[stackoverflow-thymeleaf]: https://stackoverflow.com/tags/thymeleaf
