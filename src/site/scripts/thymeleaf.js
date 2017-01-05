/* 
 * Copyright 2015, The Thymeleaf Project (http://www.thymeleaf.org/)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

(function() {
	'use strict';

	var $ = DumbQuerySelector.$;
	var $$ = DumbQuerySelector.$$;


	/**
	 * Swaps HTML characters for their entity codes so that they can be displayed
	 * in `<code>` sections.
	 * 
	 * @param {String} code String to escape.
	 * @return {String} Escaped version of `code`.
	 */
	function escapeHtml(code) {
		return code
			.replace(/&/g, '&amp;')
			.replace(/'/g, '&apos;')
			.replace(/"/g, '&quot;')
			.replace(/</g, '&lt;')
			.replace(/>/g, '&gt;');
	}

	// These code samples should really be inside the HTML file that they belong
	// to.  It's just that you can't put unescaped HTML code inside an HTML code
	// block :/
	var CODE_SAMPLES = {

		'template-example': [
			'<table>',
			'  <thead>',
			'    <tr>',
			'      <th th:text="#{msgs.headers.name}">Name</th>',
			'      <th th:text="#{msgs.headers.price}">Price</th>',
			'    </tr>',
			'  </thead>',
			'  <tbody>',
			'    <tr th:each="prod: ${allProducts}">',
			'      <td th:text="${prod.name}">Oranges</td>',
			'      <td th:text="${#numbers.formatDecimal(prod.price, 1, 2)}">0.99</td>',
			'    </tr>',
			'  </tbody>',
			'</table>'
		].join('\n'),

		'maven-example': [
			'<dependency>',
			'    <groupId>org.thymeleaf</groupId>',
			'    <artifactId>thymeleaf</artifactId>',
			'    <version>3.0.3.RELEASE</version>',
			'</dependency>'
		].join('\n'),

		'testing-example': [
			'%TEMPLATE_MODE HTML5',
			'# ------------ separator comment -----------',
			'%CONTEXT',
			'onevar = "Goodbye,"',
			'# ------------------------------------------',
			'%MESSAGES',
			'one.msg = Crisis',
			'# ------------------------------------------',
			'%INPUT',
			'<!DOCTYPE html>',
			'<html>',
			'  <body>',
			'    <span th:text="${onevar}">Hello,</span>',
			'    <span th:text="#{one.msg}">World!</span>',
			'  </body>',
			'</html>',
			'# ------------------------------------------',
			'%OUTPUT',
			'<!DOCTYPE html>',
			'<html>',
			'<body>',
			'  <span>Goodbye,</span>',
			'  <span>Crisis</span>',
			'</body>',
			'</html>'
		].join('\n'),

		'tiles-example-1': [
			'<tiles-definitions>',
			'  ...',
			'  <definition name="main" template="basic_layout">',
			'    <put-attribute name="content">',
			'      <definition template="basic_contentlayout :: content">',
			'        <put-attribute name="text" value="main :: text" />',
			'      </definition>',
			'    </put-attribute>',
			'    <put-attribute name="side" value="${config.sideColumnTemplate}" />',
			'  </definition>',
			'  ...',
			'</tiles-definitions>'
		].join('\n'),

		'tiles-example-2': [
			'<html xmlns:th="http://www.thymeleaf.org" xmlns:tiles="http://www.thymeleaf.org">',
			'...',
			'<body>',
			'  ...',
			'  <div tiles:include="side">',
			'    some prototyping markup over here...',
			'  </div>',
			'  ...',
			'</body>',
			'</html>'
		].join('\n'),

		'springsecurity-example-1': [
			'<div sec:authorize="hasRole(\'ROLE_ADMIN\')">',
			'  This will only be displayed if authenticated user has role ROLE_ADMIN.',
			'</div>'
		].join('\n'),

		'springsecurity-example-2': [
			'<div th:text="${#authentication.name}">',
			'  The value of the "name" property of the authentication object should appear here.',
			'</div>'
		].join('\n'),

		'conditionalcomments-example-1': [
			'<!--[if lt IE 8]>',
			'<link rel="stylesheet" th:href="@{/resources/blueprint/ie.css}"',
			'  type="text/css" media="screen, projection">',
			'<![endif]-->'
		].join('\n'),

		'conditionalcomments-example-2': [
			'<!--[if lt IE 8]>',
			'<link rel="stylesheet" href="/myapp/resources/blueprint/ie.css"',
			'  type="text/css" media="screen, projection">',
			'<![endif]-->'
		].join('\n'),

		'snapshot-example-1': [
			'<repositories>',
			'  <repository>',
			'    <id>sonatype-nexus-snapshots</id>',
			'    <name>Sonatype Nexus Snapshots</name>',
			'    <url>https://oss.sonatype.org/content/repositories/snapshots</url>',
			'    <snapshots>',
			'      <enabled>true</enabled>',
			'    </snapshots>',
			'  </repository>',
			'</repositories>'
		].join('\n'),

		'snapshot-example-2': [
			'<dependency>',
			'  <groupId>org.thymeleaf</groupId>',
			'  <artifactId>thymeleaf</artifactId>',
			'  <version>3.0.4-SNAPSHOT</version>',
			'  <scope>compile</scope>',
			'</dependency>'
		].join('\n')
	};


	// Match any code samples on the page with those in this file
	$$('code[data-src]').forEach(function(codeBlock) {
		var sourceId = codeBlock.dataset.src;
		var codeSample = CODE_SAMPLES[sourceId];
		if (codeSample) {
			codeBlock.innerHTML = escapeHtml(codeSample);
		}
	});

	// Run the Prism syntax highlighter
	Prism.highlightAll();

	// Have the site menu button reveal the site menu on click
	$('#site-menu-button').addEventListener('click', function(event) {
		$('#site-menu').classList.toggle('show-menu');
	});

})();
