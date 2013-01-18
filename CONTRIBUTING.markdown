Contributing to Thymeleaf: Terms and Conditions
===============================================

------------------------------------------------------------------------------


Do you want to contribute your work to thymeleaf? Well, then first and most important: **THANK YOU!** 


Now, in order to accept your contribution, there are some terms you must expressly agree with, so please
read them carefully. They might look a bit cumbersome, but they are here just in order to protect 
you, your contribution, and especially the project's future.

**Important**: submitting any contributions to the Thymeleaf project implies your **full acceptance of these terms**,
including the *"Thymeleaf Individual Contributor License Agreement"* detailed at the end. 

Who can contribute?
-------------------

Anyone, with the unique condition that he/she must be a **private individual**, acting in 
his/her own name, and not being endorsed in their contributed work by any company or government.

Note that this condition will not only refer to the ownership of the effort invested in contributing 
to the project, but also to the fact that *no private or public company will be mentioned as a a part 
of your contribution on the project's website or code*, including but not limited to web/email addresses 
or package names.


What is the first step to be taken?
-----------------------------------

First of all, **talk to the [project members](http://www.thymeleaf.org/team.html)** (an email should do) about 
your ideas: new features, fixes, documentation... whatever you would like to contribute to the project. Let we 
discuss the possibilities with you so that we make sure your contribution goes in the right direction and aligns 
with the project's standards, intentions and roadmap. 



How will your relation with the Thymeleaf project be?
-----------------------------------------------------

Your contributions will have the form of GitHub *pull requests*. Note that contributors do not 
have read+write (or *pull+push*) access to the project repositories, only project *members* do. 

Also, please understand that *not all pull requests will be accepted and merged into the project's 
repositories*. Talking about your planned contributions with the project members before creating pull requests
will maximize the possibilities of your contributions being accepted.

Once your contribution is approved, you will be listed as a *contributor* at the 
[Thymeleaf Team page](http://www.thymeleaf.org/team.html). You can opt-out of this if you want. 
Also, you will be `@author` for any new Java classes that you write and also co-`@author` to any existing classes to 
which you make significant changes. You can also opt-out of this if you want.  




About the code you contribute
-----------------------------

### General guidelines:
  
  - Obviously, **your code must both compile and work correctly**. Also, the addition of any new patches to the 
    codebase should not render it unstable in any way.
  - All your code should be easy to read and understand by a human.
  - There should be no compilation warnings at all.

### Detailed Java code quality standards:
  
  - All your code should compile and run in **Java 5.0**.
  - All comments, names of classes and variables, log messages, etc. must be **in English**.
  - All `.java` files must include the standard Thymeleaf copyright header.
  - All your code should follow the Java Code Conventions regarding variable/method/class naming.
  - Maximum line size is 120 characters.
  - Indentation should be made with 4 spaces, not tabs.
  - Line feeds should be UNIX-like (`\n`).
  - All .java source files should be pure ASCII. All .properties files should be ISO-8859-1. 
  - Number autoboxing and/or autounboxing is forbidden.
  - Every class should define a constructor, even if it is the default one, and include a call to `super()`.
  - Every non-nullable argument in a public method should be first validated with a `Validate.notNull(...)` call.
  - All method arguments should include the `final` modifier, so that their value is never changed.
  - Include `/* ... */` comments for every algorithm you develop with a minimum of complexity. *"Minimum 
    of complexity"* usually means you had to take some design decisions in order to write it the way you did. Do 
    not write obvious comments.
  - All public methods and classes directly available to users (i.e. public) should have comprehensive javadoc.
  
### Detailed HTML/XML code quality standards:

  - All tags, CSS styles, file names, etc. must be **in English**.
  - Lower case should be prefered for HTML/XML artifacts. The only exceptions are `DOCTYPE` and `CDATA` clauses.
  - All HTML code should be XML-valid (i.e. all tags should be closed, attributes surrounded by commas, etc.)
  - Maximum line size is 120 characters.
  - Indentation should be made with 4 spaces, not tabs.
  - Line feeds should be UNIX-like (`\n`).
  - All .html and .xml source files should be pure ASCII, even if _content-type_ is set to a different encoding. 
  - All XHTML self-closing (minimized) tags should have a space before `/>` (the XHTML standards say so!).
  - All inline scripts must be enclosed inside a commented `<![CDATA[...]]>` block.
  

About the documentation/articles you contribute
-----------------------------------------------

Note the following only applies to documentation/articles meant to be published at the Thymeleaf website.

  - All documentation artifacts, including articles, must be written **in correct English**.
  - Your name and email will be displayed as *"author"* of any documentation artifacts you create.
  - Topic and text structure must be first discussed and agreed upon with the project members.
  - Project members may edit and make small changes to your texts --of which you will be informed-- before 
    publishing them.
  - Format and visual styles must adhere to the thymeleaf website standards, of which you will be informed
    by the project members.



Pay special attention to this
-----------------------------

All Thymeleaf software is distributed under the **Apache License 2.0** open source license, and your contributions
will be licensed in the same way.

If you work for a company which, by the way or place in which your code was written, by your contract terms 
or by the laws in your country, could claim any rights (including but not limited to intellectual or industrial 
property) over your contributed code, you will have to send the project members (either by email from your 
authorised superiors or by signed fax), a statement indicating that your company agrees with the terms 
explained in this page, and that it both authorises your contribution to Thymeleaf and states that will 
never claim any kind of rights over it.



Thymeleaf Individual Contributor License Agreement
--------------------------------------------------

This contributor agreement ("Agreement") documents the rights granted by contributors to the Thymeleaf Project. 

This is a legally binding document, so please read it carefully before agreeing to it. The Agreement
may cover more than one software project managed by Thymeleaf.

###1. Definitions

  * _"Thymeleaf"_ means the "Thymeleaf Project organization and members".
  * _"You"_ means the individual who submits a Contribution to Thymeleaf.
  * _"Contribution"_ means any work of authorship that is submitted by you to Thymeleaf in which you own
    or assert ownership of the Copyright.
  * _"Copyright"_ means all rights protecting works of authorship owned or controlled by you,
    including copyright, moral and neighboring rights, as appropriate, for the full term of their
    existence including any extensions by you.
  * _"Material"_ means the work of authorship which is made available by Thymeleaf to third parties. When
    this Agreement covers more than one software project, the Material means the work of authorship
    to which the Contribution was submitted. After you submit the Contribution, it may be included
    in the Material.
  * _"Submit"_ means any form of electronic, verbal, or written communication sent to Thymeleaf or its
    representatives, including but not limited to electronic mailing lists, source code control systems,
    and issue tracking systems that are managed by, or on behalf of, Thymeleaf for the purpose of discussing
    and improving the Material, but excluding communication that is conspicuously marked or
    otherwise designated in writing by you as _"Not a Contribution."
  * _"Submission Date"_ means the date on which you submit a Contribution to Thymeleaf.
  * _"Effective Date"_ means the date you execute this agreement or the date You first submit a
    Contribution to Thymeleaf, whichever is earlier.
  
###2. Grant of Rights

####2.1. Copyright License

  * (a) You retain ownership of the copyright in your Contribution and have the same rights to use or
        license the Contribution which you would have had without entering into the agreement.
  * (b) To the maximum extent permitted by the relevant law, you grant to Thymeleaf a perpetual, worldwide,
        non-exclusive, transferable, royalty-free, irrevocable license under the copyright covering the
        Contribution, with the right to sublicense such rights through multiple tiers of sublicensees, to
        reproduce, modify, display, perform and distribute the Contribution as part of the Material; provided
        that this license is conditioned upon compliance with Section 2.3.

####2.2 Patent License

For patent claims including, without limitation, method, process, and apparatus claims which you
own, control or have the right to grant, now or in the future, you grant to Thymeleaf a perpetual, worldwide,
non-exclusive, transferable, royalty-free, irrevocable patent license, with the right to sublicense these
rights to multiple tiers of sublicensees, to make, have made, use, sell, offer for sale, import and
otherwise transfer the Contribution and the Contribution in combination with the Material (and
portions of such combination). This license is granted only to the extent that the exercise of the
licensed rights infringes such patent claims; and provided that this license is conditioned upon
compliance with Section 2.3.


####2.3 Outbound License

As a condition on the grant of rights in Sections 2.1 and 2.2, Thymeleaf agrees to license the Contribution only
under the terms of the Apache License 2.0 (including any right to adopt any future version of this license if 
permitted).


####2.4 Moral Rights 

If moral rights apply to the Contribution, to the maximum extent permitted by law, you waive and agree not 
to assert such moral rights against Thymeleaf or its successors in interest, or any of our licensees, either 
direct or indirect.


####2.5 Thymeleaf Rights 

You acknowledge that Thymeleaf is not obligated to use your Contribution as part of the
Material and may decide to include any Contributions Thymeleaf considers appropriate.

####2.6 Reservation of Rights

Any rights not expressly assigned or licensed under this section are expressly reserved by you.



###3. Agreement

You confirm that:

  * (a) You have the legal authority to enter into this Agreement.
  * (b) You own the Copyright and patent claims covering the Contribution which are required to grant
        the rights under Section 2.
  * (c) The grant of rights under Section 2 does not violate any grant of rights which you have made to
        third parties, including your employer. If you are an employee, you have had your employer approve
        this Agreement. If you are less than eighteen years old, please have your parents or guardian 
        sign the Agreement.



###4. Disclaimer

EXCEPT FOR THE EXPRESS WARRANTIES IN SECTION 3, THE CONTRIBUTION IS PROVIDED "AS IS". MORE PARTICULARLY, 
ALL EXPRESS OR IMPLIED WARRANTIES INCLUDING, WITHOUT LIMITATION, ANY IMPLIED WARRANTY OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT ARE EXPRESSLY DISCLAIMED BY YOU TO THYMELEAF AND BY
THYMELEAF TO YOU. TO THE EXTENT THAT ANY SUCH WARRANTIES CANNOT BE DISCLAIMED, SUCH WARRANTY IS LIMITED IN 
DURATION TO THE MINIMUM PERIOD PERMITTED BY LAW.



###5. Consequential Damage Waiver

TO THE MAXIMUM EXTENT PERMITTED BY APPLICABLE LAW, IN NO EVENT WILL YOU OR THYMELEAF BE LIABLE FOR ANY LOSS OF 
PROFITS, LOSS OF ANTICIPATED SAVINGS, LOSS OF DATA, INDIRECT, SPECIAL, INCIDENTAL, CONSEQUENTIAL AND EXEMPLARY
DAMAGES ARISING OUT OF THIS AGREEMENT REGARDLESS OF THE LEGAL OR EQUITABLE THEORY (CONTRACT, TORT OR OTHERWISE) 
UPON WHICH THE CLAIM IS BASED.



###6. Miscellaneous

  * 6.1 This Agreement will be governed by and construed in accordance with the laws of Spain excluding its
        conflicts of law provisions. Under certain circumstances, the governing law in this section might be
        superseded by the United Nations Convention on Contracts for the International Sale of Goods ("UN
        Convention") and the parties intend to avoid the application of the UN Convention to this Agreement
        and, thus, exclude the application of the UN Convention in its entirety to this Agreement.
  * 6.2 This Agreement sets out the entire agreement between you and Thymeleaf for your Contributions to Thymeleaf
        and overrides all other agreements or understandings.
  * 6.3 If You or Thymeleaf assign the rights or obligations received through this Agreement to a third party, as a
        condition of the assignment, that third party must agree in writing to abide by all the rights and
        obligations in the Agreement.
  * 6.4 The failure of either party to require performance by the other party of any provision of this
        Agreement in one situation shall not affect the right of a party to require such performance at any time
        in the future. A waiver of performance under a provision in one situation shall not be considered a
        waiver of the performance of the provision in the future or a waiver of the provision in its entirety.
  * 6.5 If any provision of this Agreement is found void and unenforceable, such provision will be
        replaced to the extent possible with a provision that comes closest to the meaning of the original
        provision and which is enforceable. The terms and conditions set forth in this Agreement shall apply
        notwithstanding any failure of essential purpose of this Agreement or any limited remedy to the
        maximum extent possible under law.



