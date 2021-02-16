A Modern Java/JVM templating engine
========================

Thymeleaf is a modern server-side Java template engine for both web and standalone environments. 
Thymeleaf's main goal is to bring elegant *natural templates* to your development workflow — HTML that can be correctly displayed in browsers and also work as static prototypes, allowing for stronger collaboration in development teams.

With modules for Spring Framework, a host of integrations with your favourite tools, and the ability to plug in your own functionality, Thymeleaf is ideal for modern-day HTML5 JVM web development — although there is much more it can do.

Natural templates
----------
HTML templates written in Thymeleaf still look and work like HTML. The actual templates in your application keep working as useful design artifacts.

```html
<table>
  <thead>
    <tr>
      <th th:text="#{msgs.headers.name}">Name</th>
      <th th:text="#{msgs.headers.price}">Price</th>
    </tr>
  </thead>
  <tbody>
    <tr th:each="prod: ${allProducts}">
      <td th:text="${prod.name}">Oranges</td>
      <td th:text="${#numbers.formatDecimal(prod.price, 1, 2)}">0.99</td>
    </tr>
  </tbody>
</table>
```

Integrations galore
---------
Eclipse, IntelliJ IDEA, Spring, Play, and even the Model-View-Controller API for Java EE 8 are supported. You can write Thymeleaf templates with your favourite tools and web-development frameworks.

Check out our [Ecosystem](https://www.thymeleaf.org/ecosystem.html) to learn about more integrations, including community-written plugins to speed-up development with Thymeleaf.

Let's go!
---------
Looking for how to get started? Check out our [Download Section](https://www.thymeleaf.org/download.html) for getting Thymeleaf, then go to our [Docs Pages](https://www.thymeleaf.org/documentation.html) for tutorials that will ease you into using Thymeleaf.

Found a bug, or itching to contribute? You can find details in our [Issue Tracking Page](https://www.thymeleaf.org/issuetracking.html).
