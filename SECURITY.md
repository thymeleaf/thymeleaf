# Security Policy

## Reporting Vulnerabilities

Thank you for your collaboration keeping Thymeleaf safe and secure. If you believe you have found a security
issue in Thymeleaf, please notify us so that we can work with you in its prompt resolution.

Also, before reporting a vulnerability related to the execution of expressions, make sure you read the
official Thymeleaf documentation, especially the sections on Expression Restrictions.

### Disclosure Policy

* Let us know as soon as possible by sending an email to [security@thymeleaf.org][security-email].
* Provide us a reasonable amount of time to resolve the issue before any disclosure to the public or a
  third-party. Especially, **do not** create a GitHub issue ticket yourself talking about the
  vulnerability. We may publicly disclose the issue _before_ resolving it, but only if appropriate.

### Credit

We will credit the reporter of a confirmed vulnerability in the GitHub ticket created for publishing it (typically
once it is fixed).

### Exclusions

We reserve the right to consider out of the scope of Thymeleaf's security:

* Developer bad practices and inadequate uses of Thymeleaf that effectively _create_ the vulnerability in
  the applications being developed with Thymeleaf.
* Attacks requiring physical access to the machine Thymeleaf is running on.
* Issues in Thymeleaf's software dependencies which should be reported to these dependencies' maintainers.


# Supported Versions

Versions of Thymeleaf look like X.Y.Z, meaning X = Major, Y = Minor and Z = patch. We recommend 
keeping Thymeleaf dependencies in applications upgraded to the latest patch version.

Also please note that, in general, only the latest minor version will be considered supported
and will receive timely updates and security patches.


# Artifact Signatures

Published artifacts for Thymeleaf, such as JARs released on Maven Central, are signed with GPG keys that
can be verified at the [Thymeleaf Organization Repository][gpg-keys]

[security-email]: mailto:security@thymeleaf.org
[gpg-keys]: https://github.com/thymeleaf/thymeleaf-org/blob/main/GPG-KEYS-thymeleaf.txt
