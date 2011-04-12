<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@ 
taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ 
page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><!DOCTYPE html>

<html>

  <head>
    <title>Spring MVC view layer: Thymeleaf vs. JSP</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet" type="text/css" media="all" href="<s:url value='/css/thvsjsp.css' />"/>
  </head>

  <body>

    <h2>This is a JSP</h2>

    <s:url var="formUrl" value="/subscribejsp" />
    <sf:form modelAttribute="subscription" action="${formUrl}">
      
      <fieldset>
      
        <div>
          <label for="email"><s:message code="subscription.email" />: </label>
          <sf:input path="email" />
        </div>
        <div>
          <label><s:message code="subscription.type" />: </label>
          <ul>
            <c:forEach var="type" items="${allTypes}">
              <li>
                <sf:radiobutton path="subscriptionType" value="${type}" />
                <sf:label path="subscriptionType"><s:message code="subscriptionType.${type}" /></sf:label>
              </li>
            </c:forEach>
          </ul>
        </div>

        <div class="submit">
          <button type="submit" name="save"><s:message code="subscription.submit" /></button>
        </div>
      
      </fieldset>
      
    </sf:form>

  </body>
  
</html>