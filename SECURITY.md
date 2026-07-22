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

* Developer bad practices and inadequate uses of Thymeleaf that effectively _create_ the
  vulnerability in the applications being developed with Thymeleaf - in particular, the
  use of unvalidated user input in expression-evaluation contexts (see
  [Expression Evaluation Security Model](#expression-evaluation-security-model)).
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


# Expression Evaluation Security Model

Thymeleaf evaluates Standard Expressions (OGNL in non-Spring environments, SpEL in
Spring environments) as an intrinsic part of template processing. To reduce the impact
of certain classes of injection, Thymeleaf applies two mechanisms:

* A **general class-access denylist**, forbidding static use (instantiation, static
  method/field access) of most classes from Thymeleaf's, Spring's and Java/Jakarta's
  base infrastructure, plus a number of well-known third-party framework packages. This
  is explicitly **not an exhaustive filter**, but a helper that bans a set of the most
  obvious classes that could be misused.
* A **restricted expression evaluation mode**, applied automatically in specific
  contexts (preprocessing `__...__`, unescaped output `th:utext`, `th:on*` event
  handlers, arbitrary-name attribute creation, template/fragment names and fragment
  parameters, URL bases, and all expressions in `TEXT` template mode), which additionally
  forbids object instantiation, static access, request-parameter access via `${param.*}`,
  bean access via `@beanName`, and access to certain context-level utility objects.

## These mechanisms are defense-in-depth, not a primary security boundary

**The primary and indispensable security boundary affecting web applications using Thymeleaf
is validation and sanitisation of untrusted input performed by the application itself.** Thymeleaf's
expression restrictions are an _additional_ layer designed to reduce impact when that
primary boundary has already failed. They are not, and are not intended to be, a substitute
for input validation.

It is always the responsibility of the application, not of Thymeleaf, to validate and sanitise
all data originating from users or any other external source -request parameters, path
segments, headers, cookies, request bodies, session attributes, database contents, third-party
service responses, and so on- **before such data is used in any way, and regardless of
where or how it will eventually appear in a template**, including template names, view
names, fragment names, fragment parameters, expression fragments, or template content.

Thymeleaf is a template engine, not an application framework. It **does not have the
technical capability to determine whether a value it is asked to process originated as
direct user input** in the current or a previous request or interaction, and it must
never be assumed capable of verifying such origin. Whatever the origin of an expression,
**it will be executed** if it does not match one of the restriction filters, in the
contexts in which those filters apply. Any best-effort request-inspection hardening that
Thymeleaf may perform (for example, checks that a computed view name does not appear
in the observable request metadata) operates only over data the engine can
safely observe after controller execution, cannot cover data the engine does not or can
no longer access, and must never be relied upon as a validation mechanism.

## Severity assessment of expression-restriction bypasses

Because these mechanisms are defense-in-depth, Thymeleaf assesses the severity of
reported bypasses in the expression evaluation security model as follows:

* A defect that allows a Thymeleaf restricted-mode check or the class denylist to be
  circumvented is a genuine vulnerability in that defense-in-depth layer, and will be
  fixed. Where a CVE is issued, it will be scored **as a defense-in-depth bypass whose
  exploitation is conditional on an application-side precondition** - namely, that the
  application has passed unvalidated external input into an expression-evaluation context.

* Such a defect will **not** be scored as a standalone, unauthenticated, network-reachable
  framework Remote Code Execution. This is a statement about **attribution and attack
  complexity, not about technical impact**: the code-execution potential of a bypass is
  acknowledged, but the condition that makes it reachable -unvalidated user input flowing
  into an expression sink- is an application-level vulnerability that Thymeleaf's
  documentation -and general web application best practices- explicitly instruct developers
  to prevent, and is outside Thymeleaf's control.

* An application that does **not** feed unvalidated external input into any
  expression-evaluation context is not exposed to this kind of bypasses, irrespective of the
  bypass's existence.

This policy is prospective and applies to reports assessed after its publication. It does
not retroactively re-score previously published advisories, which were assessed under the
understanding in effect at the time of their publication.
