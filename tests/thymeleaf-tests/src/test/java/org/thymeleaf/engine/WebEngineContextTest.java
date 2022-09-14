/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 * =============================================================================
 */
package org.thymeleaf.engine;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.TestTemplateEngineConfigurationBuilder;
import org.thymeleaf.context.WebEngineContext;
import org.thymeleaf.standard.inline.StandardTextInliner;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.testing.templateengine.util.JakartaServletMockUtils;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;


public final class WebEngineContextTest {


    private static final Locale LOCALE = Locale.US;



    @Test
    public void test01() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);

        final Map<String,Object> requestAttributes = new LinkedHashMap<String, Object>();
        final Map<String,String[]> requestParameters = new LinkedHashMap<String, String[]>();
        final Map<String,Object> servletContextAttributes = new LinkedHashMap<String, Object>();
        
        final ServletContext mockServletContext = 
                JakartaServletMockUtils.buildServletContext().attributeMap(servletContextAttributes).build();
        final HttpServletRequest mockRequest =
                JakartaServletMockUtils.buildRequest(mockServletContext, "WebVariablesMap")
                        .attributeMap(requestAttributes)
                        .parameterMap(requestParameters)
                        .locale(LOCALE)
                        .build();
        final HttpServletResponse mockResponse = JakartaServletMockUtils.buildResponse().build();
        
        final IServletWebExchange webExchange =
                JakartaServletWebApplication.buildApplication(mockServletContext).buildExchange(mockRequest, mockResponse);

        final WebEngineContext vm = new WebEngineContext(configuration, templateData1, null, webExchange, LOCALE, null);

        vm.setVariable("one", "a value");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("a value", vm.getVariable("one"));

        vm.setVariable("one", "two values");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("two values", vm.getVariable("one"));

        vm.increaseLevel();
        vm.setVariable("one", "hello");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("hello", vm.getVariable("one"));

        vm.setVariable("two", "twello");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertEquals("hello", vm.getVariable("one"));
        Assertions.assertEquals("twello", vm.getVariable("two"));

        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertEquals("two values", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));

        vm.increaseLevel();
        vm.setVariable("two", "twellor");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertEquals("two values", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));

        vm.increaseLevel();
        vm.setVariable("three", "twelloree");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertEquals("two values", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));

        vm.setVariable("one", "atwe");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));

        vm.increaseLevel();
        vm.increaseLevel();
        vm.increaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));

        vm.setVariable("four", "lotwss");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertTrue(vm.containsVariable("four"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertEquals("lotwss", vm.getVariable("four"));

        vm.setVariable("two", "itwiii");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertTrue(vm.containsVariable("four"));
        Assertions.assertFalse(vm.containsVariable("five"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertEquals("itwiii", vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertEquals("lotwss", vm.getVariable("four"));

        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertFalse(vm.containsVariable("four"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertNull(vm.getVariable("four"));

        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertFalse(vm.containsVariable("four"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertNull(vm.getVariable("four"));

        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertFalse(vm.containsVariable("four"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertNull(vm.getVariable("four"));

        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertFalse(vm.containsVariable("three"));
        Assertions.assertFalse(vm.containsVariable("four"));
        Assertions.assertEquals("two values", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));
        Assertions.assertNull(vm.getVariable("three"));
        Assertions.assertNull(vm.getVariable("four"));

        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertFalse(vm.containsVariable("three"));
        Assertions.assertFalse(vm.containsVariable("four"));
        Assertions.assertEquals("two values", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertNull(vm.getVariable("three"));
        Assertions.assertNull(vm.getVariable("four"));

    }




    @Test
    public void test02() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);

        final Map<String,Object> starting = new LinkedHashMap<String, Object>();
        starting.put("one", "ha");
        starting.put("ten", "tieen");

        final Map<String,Object> servletContextAttributes = new LinkedHashMap<String, Object>();
        final Map<String,Object> requestAttributes = new LinkedHashMap<String, Object>();
        final Map<String,String[]> requestParameters = new LinkedHashMap<String, String[]>();

        final ServletContext mockServletContext =
                JakartaServletMockUtils.buildServletContext().attributeMap(servletContextAttributes).build();
        final HttpServletRequest mockRequest =
                JakartaServletMockUtils.buildRequest(mockServletContext, "WebVariablesMap")
                        .attributeMap(requestAttributes)
                        .parameterMap(requestParameters)
                        .locale(LOCALE)
                        .build();
        final HttpServletResponse mockResponse = JakartaServletMockUtils.buildResponse().build();

        final IServletWebExchange webExchange =
                JakartaServletWebApplication.buildApplication(mockServletContext).buildExchange(mockRequest, mockResponse);

        final WebEngineContext vm = new WebEngineContext(configuration, templateData1, null, webExchange, LOCALE, starting);

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("ten"));
        Assertions.assertEquals("ha", vm.getVariable("one"));
        Assertions.assertEquals("tieen", vm.getVariable("ten"));

        vm.setVariable("one", "a value");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("ten"));
        Assertions.assertEquals("a value", vm.getVariable("one"));
        Assertions.assertEquals("tieen", vm.getVariable("ten"));

        vm.increaseLevel();
        vm.setVariable("one", "hello");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("ten"));
        Assertions.assertEquals("hello", vm.getVariable("one"));
        Assertions.assertEquals("tieen", vm.getVariable("ten"));

        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("ten"));
        Assertions.assertEquals("a value", vm.getVariable("one"));
        Assertions.assertEquals("tieen", vm.getVariable("ten"));

    }




    @Test
    public void test03() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);

        final Map<String,Object> servletContextAttributes = new LinkedHashMap<String, Object>();
        final Map<String,Object> requestAttributes = new LinkedHashMap<String, Object>();
        final Map<String,String[]> requestParameters = new LinkedHashMap<String, String[]>();

        final ServletContext mockServletContext =
                JakartaServletMockUtils.buildServletContext().attributeMap(servletContextAttributes).build();
        final HttpServletRequest mockRequest =
                JakartaServletMockUtils.buildRequest(mockServletContext, "WebVariablesMap")
                        .attributeMap(requestAttributes)
                        .parameterMap(requestParameters)
                        .locale(LOCALE)
                        .build();
        final HttpServletResponse mockResponse = JakartaServletMockUtils.buildResponse().build();

        final IServletWebExchange webExchange =
                JakartaServletWebApplication.buildApplication(mockServletContext).buildExchange(mockRequest, mockResponse);


        final WebEngineContext vm = new WebEngineContext(configuration, templateData1, null, webExchange, LOCALE, null);

        vm.setVariable("one", "a value");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("a value", vm.getVariable("one"));
        Assertions.assertEquals("a value", mockRequest.getAttribute("one"));

        vm.setVariable("one", "two values");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("two values", vm.getVariable("one"));
        Assertions.assertEquals("two values", mockRequest.getAttribute("one"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "one"));

        vm.removeVariable("one");

        Assertions.assertFalse(vm.containsVariable("one"));
        Assertions.assertNull(vm.getVariable("one"));
        Assertions.assertNull(mockRequest.getAttribute("one"));
        Assertions.assertFalse(enumerationContains(mockRequest.getAttributeNames(), "one"));

        vm.setVariable("one", "two values");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("two values", vm.getVariable("one"));
        Assertions.assertEquals("two values", mockRequest.getAttribute("one"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "one"));

        vm.increaseLevel();

        vm.setVariable("one", "hello");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("hello", vm.getVariable("one"));
        Assertions.assertEquals("hello", mockRequest.getAttribute("one"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "one"));

        vm.removeVariable("one");

        Assertions.assertFalse(vm.containsVariable("one"));
        Assertions.assertNull(vm.getVariable("one"));
        Assertions.assertNull(mockRequest.getAttribute("one"));
        Assertions.assertFalse(enumerationContains(mockRequest.getAttributeNames(), "one"));

        vm.setVariable("one", "hello");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("hello", vm.getVariable("one"));
        Assertions.assertEquals("hello", mockRequest.getAttribute("one"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "one"));

        vm.removeVariable("two");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertEquals("hello", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("twello"));
        Assertions.assertNull(mockRequest.getAttribute("two"));
        Assertions.assertFalse(enumerationContains(mockRequest.getAttributeNames(), "two"));

        vm.setVariable("two", "twello");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertEquals("hello", vm.getVariable("one"));
        Assertions.assertEquals("twello", vm.getVariable("two"));
        Assertions.assertEquals("twello", mockRequest.getAttribute("two"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "two"));

        vm.removeVariable("two");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertEquals("hello", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("twello"));

        vm.removeVariable("one");

        Assertions.assertFalse(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertNull(vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("twello"));

        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertEquals("two values", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));

        vm.increaseLevel();
        vm.setVariable("two", "twellor");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertEquals("two values", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));
        Assertions.assertEquals("twellor", mockRequest.getAttribute("two"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "two"));

        vm.increaseLevel();
        vm.setVariable("three", "twelloree");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertEquals("two values", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));

        vm.setVariable("one", "atwe");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertEquals("atwe", mockRequest.getAttribute("one"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "one"));

        vm.increaseLevel();

        vm.removeVariable("two");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertNull(mockRequest.getAttribute("two"));
        Assertions.assertFalse(enumerationContains(mockRequest.getAttributeNames(), "two"));

        vm.increaseLevel();

        vm.removeVariable("two");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));

        vm.increaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertNull(mockRequest.getAttribute("two"));
        Assertions.assertFalse(enumerationContains(mockRequest.getAttributeNames(), "two"));

        vm.setVariable("four", "lotwss");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertTrue(vm.containsVariable("four"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertEquals("lotwss", vm.getVariable("four"));

        vm.setVariable("two", "itwiii");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertTrue(vm.containsVariable("four"));
        Assertions.assertFalse(vm.containsVariable("five"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertEquals("itwiii", vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertEquals("lotwss", vm.getVariable("four"));
        Assertions.assertEquals("itwiii", mockRequest.getAttribute("two"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "two"));

        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertFalse(vm.containsVariable("four"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertNull(vm.getVariable("four"));
        Assertions.assertNull(mockRequest.getAttribute("two"));
        Assertions.assertFalse(enumerationContains(mockRequest.getAttributeNames(), "two"));

        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertFalse(vm.containsVariable("four"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertNull(vm.getVariable("four"));

        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertFalse(vm.containsVariable("four"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertNull(vm.getVariable("four"));
        Assertions.assertEquals("atwe", mockRequest.getAttribute("one"));
        Assertions.assertEquals("twellor", mockRequest.getAttribute("two"));
        Assertions.assertEquals("twelloree", mockRequest.getAttribute("three"));
        Assertions.assertNull(mockRequest.getAttribute("four"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "one"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "two"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "three"));
        Assertions.assertFalse(enumerationContains(mockRequest.getAttributeNames(), "four"));

        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertFalse(vm.containsVariable("three"));
        Assertions.assertFalse(vm.containsVariable("four"));
        Assertions.assertEquals("two values", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));
        Assertions.assertNull(vm.getVariable("three"));
        Assertions.assertNull(vm.getVariable("four"));

        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertFalse(vm.containsVariable("three"));
        Assertions.assertFalse(vm.containsVariable("four"));
        Assertions.assertEquals("two values", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertNull(vm.getVariable("three"));
        Assertions.assertNull(vm.getVariable("four"));
        Assertions.assertEquals("two values", mockRequest.getAttribute("one"));
        Assertions.assertNull(mockRequest.getAttribute("two"));
        Assertions.assertNull(mockRequest.getAttribute("three"));
        Assertions.assertNull(mockRequest.getAttribute("four"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "one"));
        Assertions.assertFalse(enumerationContains(mockRequest.getAttributeNames(), "two"));
        Assertions.assertFalse(enumerationContains(mockRequest.getAttributeNames(), "three"));
        Assertions.assertFalse(enumerationContains(mockRequest.getAttributeNames(), "four"));

    }




    @Test
    public void test04() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);

        final Map<String,Object> starting = new LinkedHashMap<String, Object>();
        starting.put("one", "ha");
        starting.put("ten", "tieen");

        final Map<String,Object> servletContextAttributes = new LinkedHashMap<String, Object>();
        final Map<String,Object> requestAttributes = new LinkedHashMap<String, Object>();
        final Map<String,String[]> requestParameters = new LinkedHashMap<String, String[]>();

        final ServletContext mockServletContext =
                JakartaServletMockUtils.buildServletContext().attributeMap(servletContextAttributes).build();
        final HttpServletRequest mockRequest =
                JakartaServletMockUtils.buildRequest(mockServletContext, "WebVariablesMap")
                        .attributeMap(requestAttributes)
                        .parameterMap(requestParameters)
                        .locale(LOCALE)
                        .build();
        final HttpServletResponse mockResponse = JakartaServletMockUtils.buildResponse().build();

        final IServletWebExchange webExchange =
                JakartaServletWebApplication.buildApplication(mockServletContext).buildExchange(mockRequest, mockResponse);


        final WebEngineContext vm = new WebEngineContext(configuration, templateData1, null, webExchange, LOCALE, starting);

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("ten"));
        Assertions.assertEquals("ha", vm.getVariable("one"));
        Assertions.assertEquals("tieen", vm.getVariable("ten"));

        vm.setVariable("one", "a value");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("ten"));
        Assertions.assertEquals("a value", vm.getVariable("one"));
        Assertions.assertEquals("tieen", vm.getVariable("ten"));

        vm.increaseLevel();
        vm.setVariable("one", "hello");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("ten"));
        Assertions.assertEquals("hello", vm.getVariable("one"));
        Assertions.assertEquals("tieen", vm.getVariable("ten"));

        vm.setVariable("two", "twello");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("ten"));
        Assertions.assertEquals("hello", vm.getVariable("one"));
        Assertions.assertEquals("twello", vm.getVariable("two"));
        Assertions.assertEquals("tieen", vm.getVariable("ten"));

        // This are directly set into the request, so they should only be affected by higher levels, never by decreasing levels
        mockRequest.setAttribute("one", "outer1");
        mockRequest.setAttribute("six", "outer6");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("ten"));
        Assertions.assertTrue(vm.containsVariable("six"));
        Assertions.assertEquals("outer1", vm.getVariable("one"));
        Assertions.assertEquals("twello", vm.getVariable("two"));
        Assertions.assertEquals("tieen", vm.getVariable("ten"));
        Assertions.assertEquals("outer6", vm.getVariable("six"));
        Assertions.assertEquals("outer1", mockRequest.getAttribute("one"));
        Assertions.assertEquals("twello", mockRequest.getAttribute("two"));
        Assertions.assertEquals("tieen", mockRequest.getAttribute("ten"));
        Assertions.assertEquals("outer6", mockRequest.getAttribute("six"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "one"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "two"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "ten"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "six"));

        vm.increaseLevel();

        vm.setVariable("one", "helloz");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("ten"));
        Assertions.assertTrue(vm.containsVariable("six"));
        Assertions.assertEquals("helloz", vm.getVariable("one"));
        Assertions.assertEquals("twello", vm.getVariable("two"));
        Assertions.assertEquals("tieen", vm.getVariable("ten"));
        Assertions.assertEquals("outer6", vm.getVariable("six"));
        Assertions.assertEquals("helloz", mockRequest.getAttribute("one"));
        Assertions.assertEquals("twello", mockRequest.getAttribute("two"));
        Assertions.assertEquals("tieen", mockRequest.getAttribute("ten"));
        Assertions.assertEquals("outer6", mockRequest.getAttribute("six"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "one"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "two"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "ten"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "six"));


        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("ten"));
        Assertions.assertTrue(vm.containsVariable("six"));
        Assertions.assertEquals("outer1", vm.getVariable("one"));
        Assertions.assertEquals("twello", vm.getVariable("two"));
        Assertions.assertEquals("tieen", vm.getVariable("ten"));
        Assertions.assertEquals("outer6", vm.getVariable("six"));
        Assertions.assertEquals("outer1", mockRequest.getAttribute("one"));
        Assertions.assertEquals("twello", mockRequest.getAttribute("two"));
        Assertions.assertEquals("tieen", mockRequest.getAttribute("ten"));
        Assertions.assertEquals("outer6", mockRequest.getAttribute("six"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "one"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "two"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "ten"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "six"));


        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("ten"));
        Assertions.assertTrue(vm.containsVariable("six"));
        Assertions.assertEquals("outer1", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertEquals("tieen", vm.getVariable("ten"));
        Assertions.assertEquals("outer6", vm.getVariable("six"));
        Assertions.assertEquals("outer1", mockRequest.getAttribute("one"));
        Assertions.assertNull(mockRequest.getAttribute("two"));
        Assertions.assertEquals("tieen", mockRequest.getAttribute("ten"));
        Assertions.assertEquals("outer6", mockRequest.getAttribute("six"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "one"));
        Assertions.assertFalse(enumerationContains(mockRequest.getAttributeNames(), "two"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "ten"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "six"));

    }




    @Test
    public void test05() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);

        final Map<String,Object> starting = new LinkedHashMap<String, Object>();
        starting.put("one", "ha");
        starting.put("ten", "tieen");

        final Map<String,Object> servletContextAttributes = new LinkedHashMap<String, Object>();
        final Map<String,Object> requestAttributes = new LinkedHashMap<String, Object>();
        final Map<String,String[]> requestParameters = new LinkedHashMap<String, String[]>();

        final ServletContext mockServletContext =
                JakartaServletMockUtils.buildServletContext().attributeMap(servletContextAttributes).build();
        final HttpServletRequest mockRequest =
                JakartaServletMockUtils.buildRequest(mockServletContext, "WebVariablesMap")
                        .attributeMap(requestAttributes)
                        .parameterMap(requestParameters)
                        .locale(LOCALE)
                        .build();
        final HttpServletResponse mockResponse = JakartaServletMockUtils.buildResponse().build();

        final IServletWebExchange webExchange =
                JakartaServletWebApplication.buildApplication(mockServletContext).buildExchange(mockRequest, mockResponse);


        final WebEngineContext vm = new WebEngineContext(configuration, templateData1, null, webExchange, LOCALE, starting);

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("ten"));
        Assertions.assertEquals("ha", vm.getVariable("one"));
        Assertions.assertEquals("tieen", vm.getVariable("ten"));

        vm.setVariable("one", "a value");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("ten"));
        Assertions.assertEquals("a value", vm.getVariable("one"));
        Assertions.assertEquals("tieen", vm.getVariable("ten"));

        vm.increaseLevel();
        vm.setVariable("one", "hello");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("ten"));
        Assertions.assertEquals("hello", vm.getVariable("one"));
        Assertions.assertEquals("tieen", vm.getVariable("ten"));

        vm.setVariable("two", "twello");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("ten"));
        Assertions.assertEquals("hello", vm.getVariable("one"));
        Assertions.assertEquals("twello", vm.getVariable("two"));
        Assertions.assertEquals("tieen", vm.getVariable("ten"));

        mockRequest.removeAttribute("one");

        Assertions.assertFalse(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("ten"));
        Assertions.assertNull(vm.getVariable("one"));
        Assertions.assertEquals("twello", vm.getVariable("two"));
        Assertions.assertEquals("tieen", vm.getVariable("ten"));
        Assertions.assertNull(mockRequest.getAttribute("one"));
        Assertions.assertEquals("twello", mockRequest.getAttribute("two"));
        Assertions.assertEquals("tieen", mockRequest.getAttribute("ten"));
        Assertions.assertFalse(enumerationContains(mockRequest.getAttributeNames(), "one"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "two"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "ten"));

        vm.increaseLevel();

        vm.setVariable("one", "helloz");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("ten"));
        Assertions.assertEquals("helloz", vm.getVariable("one"));
        Assertions.assertEquals("twello", vm.getVariable("two"));
        Assertions.assertEquals("tieen", vm.getVariable("ten"));
        Assertions.assertEquals("helloz", mockRequest.getAttribute("one"));
        Assertions.assertEquals("twello", mockRequest.getAttribute("two"));
        Assertions.assertEquals("tieen", mockRequest.getAttribute("ten"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "one"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "two"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "ten"));


        vm.decreaseLevel();

        Assertions.assertFalse(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("ten"));
        Assertions.assertNull(vm.getVariable("one"));
        Assertions.assertEquals("twello", vm.getVariable("two"));
        Assertions.assertEquals("tieen", vm.getVariable("ten"));
        Assertions.assertNull(mockRequest.getAttribute("one"));
        Assertions.assertEquals("twello", mockRequest.getAttribute("two"));
        Assertions.assertEquals("tieen", mockRequest.getAttribute("ten"));
        Assertions.assertFalse(enumerationContains(mockRequest.getAttributeNames(), "one"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "two"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "ten"));


        vm.decreaseLevel();

        Assertions.assertFalse(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("ten"));
        Assertions.assertNull(vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertEquals("tieen", vm.getVariable("ten"));
        Assertions.assertNull(mockRequest.getAttribute("one"));
        Assertions.assertNull(mockRequest.getAttribute("two"));
        Assertions.assertEquals("tieen", mockRequest.getAttribute("ten"));
        Assertions.assertFalse(enumerationContains(mockRequest.getAttributeNames(), "one"));
        Assertions.assertFalse(enumerationContains(mockRequest.getAttributeNames(), "two"));
        Assertions.assertTrue(enumerationContains(mockRequest.getAttributeNames(), "ten"));

    }


    @Test
    public void test06() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);

        final Map<String,Object> servletContextAttributes = new LinkedHashMap<String, Object>();
        final Map<String,Object> requestAttributes = new LinkedHashMap<String, Object>();
        final Map<String,String[]> requestParameters = new LinkedHashMap<String, String[]>();

        final ServletContext mockServletContext =
                JakartaServletMockUtils.buildServletContext().attributeMap(servletContextAttributes).build();
        final HttpServletRequest mockRequest =
                JakartaServletMockUtils.buildRequest(mockServletContext, "WebVariablesMap")
                        .attributeMap(requestAttributes)
                        .parameterMap(requestParameters)
                        .locale(LOCALE)
                        .build();
        final HttpServletResponse mockResponse = JakartaServletMockUtils.buildResponse().build();

        final IServletWebExchange webExchange =
                JakartaServletWebApplication.buildApplication(mockServletContext).buildExchange(mockRequest, mockResponse);


        final WebEngineContext vm = new WebEngineContext(configuration, templateData1, null, webExchange, LOCALE, null);

        vm.setVariable("one", "a value");

        Assertions.assertEquals("{0:{one=a value}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=a value}(test01)", vm.toString());

        vm.setVariable("one", "two values");

        Assertions.assertEquals("{0:{one=two values}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values}(test01)", vm.toString());

        vm.increaseLevel();
        vm.setVariable("one", "hello");

        Assertions.assertEquals("{1:{one=hello},0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=hello}(test01)", vm.toString());

        vm.setVariable("two", "twello");

        Assertions.assertEquals("{1:{one=hello, two=twello},0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=hello, two=twello}(test01)", vm.toString());

        vm.decreaseLevel();

        Assertions.assertEquals("{0:{one=two values}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values}(test01)", vm.toString());

        vm.increaseLevel();
        vm.setVariable("two", "twellor");
        vm.setInliner(new StandardTextInliner(configuration));

        Assertions.assertEquals("{1:{two=twellor}[StandardTextInliner],0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values, two=twellor}[StandardTextInliner](test01)", vm.toString());

        vm.increaseLevel();
        vm.setVariable("three", "twelloree");

        Assertions.assertEquals("{2:{three=twelloree},1:{two=twellor}[StandardTextInliner],0:{one=two values}(test01)}[2]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values, two=twellor, three=twelloree}[StandardTextInliner](test01)", vm.toString());

        vm.setVariable("one", "atwe");

        Assertions.assertEquals("{2:{three=twelloree, one=atwe},1:{two=twellor}[StandardTextInliner],0:{one=two values}(test01)}[2]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, two=twellor, three=twelloree}[StandardTextInliner](test01)", vm.toString());

        vm.increaseLevel();
        vm.increaseLevel();
        vm.increaseLevel();

        Assertions.assertEquals("{2:{three=twelloree, one=atwe},1:{two=twellor}[StandardTextInliner],0:{one=two values}(test01)}[5]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, two=twellor, three=twelloree}[StandardTextInliner](test01)", vm.toString());

        vm.setVariable("four", "lotwss");

        Assertions.assertEquals("{5:{four=lotwss},2:{three=twelloree, one=atwe},1:{two=twellor}[StandardTextInliner],0:{one=two values}(test01)}[5]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, two=twellor, three=twelloree, four=lotwss}[StandardTextInliner](test01)", vm.toString());

        vm.setVariable("two", "itwiii");

        Assertions.assertEquals("{5:{four=lotwss, two=itwiii},2:{three=twelloree, one=atwe},1:{two=twellor}[StandardTextInliner],0:{one=two values}(test01)}[5]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, two=itwiii, three=twelloree, four=lotwss}[StandardTextInliner](test01)", vm.toString());

        vm.decreaseLevel();

        Assertions.assertEquals("{2:{three=twelloree, one=atwe},1:{two=twellor}[StandardTextInliner],0:{one=two values}(test01)}[4]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, two=twellor, three=twelloree}[StandardTextInliner](test01)", vm.toString());

        vm.decreaseLevel();

        Assertions.assertEquals("{2:{three=twelloree, one=atwe},1:{two=twellor}[StandardTextInliner],0:{one=two values}(test01)}[3]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, two=twellor, three=twelloree}[StandardTextInliner](test01)", vm.toString());

        vm.decreaseLevel();

        Assertions.assertEquals("{2:{three=twelloree, one=atwe},1:{two=twellor}[StandardTextInliner],0:{one=two values}(test01)}[2]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, two=twellor, three=twelloree}[StandardTextInliner](test01)", vm.toString());

        vm.decreaseLevel();

        Assertions.assertEquals("{1:{two=twellor}[StandardTextInliner],0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values, two=twellor}[StandardTextInliner](test01)", vm.toString());

        vm.decreaseLevel();

        Assertions.assertEquals("{0:{one=two values}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values}(test01)", vm.toString());

    }




    @Test
    public void test07() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);

        final Map<String,Object> starting = new LinkedHashMap<String, Object>();
        starting.put("one", "ha");
        starting.put("ten", "tieen");

        final Map<String,Object> servletContextAttributes = new LinkedHashMap<String, Object>();
        final Map<String,Object> requestAttributes = new LinkedHashMap<String, Object>();
        final Map<String,String[]> requestParameters = new LinkedHashMap<String, String[]>();

        final ServletContext mockServletContext =
                JakartaServletMockUtils.buildServletContext().attributeMap(servletContextAttributes).build();
        final HttpServletRequest mockRequest =
                JakartaServletMockUtils.buildRequest(mockServletContext, "WebVariablesMap")
                        .attributeMap(requestAttributes)
                        .parameterMap(requestParameters)
                        .locale(LOCALE)
                        .build();
        final HttpServletResponse mockResponse = JakartaServletMockUtils.buildResponse().build();

        final IServletWebExchange webExchange =
                JakartaServletWebApplication.buildApplication(mockServletContext).buildExchange(mockRequest, mockResponse);


        final WebEngineContext vm = new WebEngineContext(configuration, templateData1, null, webExchange, LOCALE, starting);

        Assertions.assertEquals("{0:{one=ha, ten=tieen}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=ha, ten=tieen}(test01)", vm.toString());

        vm.setVariable("one", "a value");

        Assertions.assertEquals("{0:{one=a value, ten=tieen}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=a value, ten=tieen}(test01)", vm.toString());

        vm.increaseLevel();
        vm.setVariable("one", "hello");

        Assertions.assertEquals("{1:{one=hello},0:{one=a value, ten=tieen}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=hello, ten=tieen}(test01)", vm.toString());

        vm.setVariable("two", "twello");

        Assertions.assertEquals("{1:{one=hello, two=twello},0:{one=a value, ten=tieen}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=hello, ten=tieen, two=twello}(test01)", vm.toString());

        mockRequest.removeAttribute("one");

        Assertions.assertEquals("{1:{two=twello},0:{ten=tieen}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{ten=tieen, two=twello}(test01)", vm.toString());

        vm.increaseLevel();

        vm.setVariable("one", "helloz");

        Assertions.assertEquals("{2:{one=helloz},1:{two=twello},0:{ten=tieen}(test01)}[2]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{ten=tieen, two=twello, one=helloz}(test01)", vm.toString());

        vm.decreaseLevel();

        Assertions.assertEquals("{1:{two=twello},0:{ten=tieen}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{ten=tieen, two=twello}(test01)", vm.toString());

        vm.decreaseLevel();

        Assertions.assertEquals("{0:{ten=tieen}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{ten=tieen}(test01)", vm.toString());

    }





    @Test
    public void test08() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);

        final Map<String,Object> servletContextAttributes = new LinkedHashMap<String, Object>();
        final Map<String,Object> requestAttributes = new LinkedHashMap<String, Object>();
        final Map<String,String[]> requestParameters = new LinkedHashMap<String, String[]>();

        final ServletContext mockServletContext =
                JakartaServletMockUtils.buildServletContext().attributeMap(servletContextAttributes).build();
        final HttpServletRequest mockRequest =
                JakartaServletMockUtils.buildRequest(mockServletContext, "WebVariablesMap")
                        .attributeMap(requestAttributes)
                        .parameterMap(requestParameters)
                        .locale(LOCALE)
                        .build();
        final HttpServletResponse mockResponse = JakartaServletMockUtils.buildResponse().build();

        final IServletWebExchange webExchange =
                JakartaServletWebApplication.buildApplication(mockServletContext).buildExchange(mockRequest, mockResponse);

        final WebEngineContext vm = new WebEngineContext(configuration, templateData1, null, webExchange, LOCALE, null);

        vm.setVariable("one", "a value");

        Assertions.assertEquals("{0:{one=a value}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=a value}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one"), vm.getVariableNames());
        Assertions.assertFalse(vm.isVariableLocal("one"));

        vm.setVariable("one", "two values");

        Assertions.assertEquals("{0:{one=two values}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one"), vm.getVariableNames());

        vm.removeVariable("one");

        Assertions.assertEquals("{0:{}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{}(test01)", vm.toString());
        Assertions.assertEquals(Collections.emptySet(), vm.getVariableNames());

        vm.setVariable("one", "two values");

        Assertions.assertFalse(vm.isVariableLocal("one"));
        Assertions.assertEquals("{0:{one=two values}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one"), vm.getVariableNames());

        vm.increaseLevel();

        Assertions.assertFalse(vm.isVariableLocal("one"));

        vm.setVariable("one", "hello");

        Assertions.assertTrue(vm.isVariableLocal("one"));

        vm.decreaseLevel();

        Assertions.assertFalse(vm.isVariableLocal("one"));

        vm.increaseLevel();

        vm.setVariable("one", "hello");

        Assertions.assertTrue(vm.isVariableLocal("one"));
        Assertions.assertEquals("{1:{one=hello},0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=hello}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one"), vm.getVariableNames());

        vm.removeVariable("one");

        Assertions.assertEquals("{1:{one=null},0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{}(test01)", vm.toString());
        Assertions.assertEquals(Collections.emptySet(), vm.getVariableNames());

        vm.setVariable("one", "hello");

        Assertions.assertEquals("{1:{one=hello},0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=hello}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one"), vm.getVariableNames());

        vm.removeVariable("two");

        Assertions.assertEquals("{1:{one=hello},0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=hello}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one"), vm.getVariableNames());

        vm.setVariable("two", "twello");

        Assertions.assertEquals("{1:{one=hello, two=twello},0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=hello, two=twello}(test01)", vm.toString());
        Assertions.assertEquals(createSet("two","one"), vm.getVariableNames());

        vm.removeVariable("two");

        Assertions.assertEquals("{1:{one=hello},0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=hello}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one"), vm.getVariableNames());

        vm.removeVariable("one");

        Assertions.assertEquals("{1:{one=null},0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{}(test01)", vm.toString());
        Assertions.assertEquals(Collections.emptySet(), vm.getVariableNames());

        vm.decreaseLevel();

        Assertions.assertEquals("{0:{one=two values}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one"), vm.getVariableNames());

        vm.increaseLevel();
        vm.setVariable("two", "twellor");

        Assertions.assertEquals("{1:{two=twellor},0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values, two=twellor}(test01)", vm.toString());
        Assertions.assertEquals(createSet("two","one"), vm.getVariableNames());

        vm.increaseLevel();
        vm.setVariable("three", "twelloree");

        Assertions.assertEquals("{2:{three=twelloree},1:{two=twellor},0:{one=two values}(test01)}[2]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values, two=twellor, three=twelloree}(test01)", vm.toString());
        Assertions.assertEquals(createSet("two","one","three"), vm.getVariableNames());

        vm.setVariable("one", "atwe");

        Assertions.assertEquals("{2:{three=twelloree, one=atwe},1:{two=twellor},0:{one=two values}(test01)}[2]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, two=twellor, three=twelloree}(test01)", vm.toString());
        Assertions.assertEquals(createSet("two","one","three"), vm.getVariableNames());

        vm.increaseLevel();

        Assertions.assertTrue(vm.isVariableLocal("two"));

        vm.removeVariable("two");

        Assertions.assertEquals("{3:{two=null},2:{three=twelloree, one=atwe},1:{two=twellor},0:{one=two values}(test01)}[3]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one","three"), vm.getVariableNames());
        Assertions.assertFalse(vm.isVariableLocal("two"));

        vm.increaseLevel();

        vm.removeVariable("two");

        Assertions.assertEquals("{3:{two=null},2:{three=twelloree, one=atwe},1:{two=twellor},0:{one=two values}(test01)}[4]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one","three"), vm.getVariableNames());

        vm.increaseLevel();

        Assertions.assertEquals("{3:{two=null},2:{three=twelloree, one=atwe},1:{two=twellor},0:{one=two values}(test01)}[5]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one","three"), vm.getVariableNames());

        vm.setVariable("four", "lotwss");

        Assertions.assertEquals("{5:{four=lotwss},3:{two=null},2:{three=twelloree, one=atwe},1:{two=twellor},0:{one=two values}(test01)}[5]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree, four=lotwss}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one","three","four"), vm.getVariableNames());

        vm.setVariable("two", "itwiii");

        Assertions.assertEquals("{5:{four=lotwss, two=itwiii},3:{two=null},2:{three=twelloree, one=atwe},1:{two=twellor},0:{one=two values}(test01)}[5]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree, four=lotwss, two=itwiii}(test01)", vm.toString());
        Assertions.assertEquals(createSet("two","one","three","four"), vm.getVariableNames());

        vm.decreaseLevel();

        Assertions.assertEquals("{3:{two=null},2:{three=twelloree, one=atwe},1:{two=twellor},0:{one=two values}(test01)}[4]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one","three"), vm.getVariableNames());

        vm.decreaseLevel();

        Assertions.assertEquals("{3:{two=null},2:{three=twelloree, one=atwe},1:{two=twellor},0:{one=two values}(test01)}[3]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one","three"), vm.getVariableNames());

        vm.decreaseLevel();

        Assertions.assertEquals("{2:{three=twelloree, one=atwe},1:{two=twellor},0:{one=two values}(test01)}[2]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree, two=twellor}(test01)", vm.toString());
        Assertions.assertEquals(createSet("two","one","three"), vm.getVariableNames());

        vm.decreaseLevel();

        Assertions.assertEquals("{1:{two=twellor},0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values, two=twellor}(test01)", vm.toString());
        Assertions.assertEquals(createSet("two","one"), vm.getVariableNames());

        vm.decreaseLevel();

        Assertions.assertEquals("{0:{one=two values}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one"), vm.getVariableNames());

    }


    @Test
    public void test09() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);

        final Map<String,Object> servletContextAttributes = new LinkedHashMap<String, Object>();
        final Map<String,Object> requestAttributes = new LinkedHashMap<String, Object>();
        final Map<String,String[]> requestParameters = new LinkedHashMap<String, String[]>();

        final ServletContext mockServletContext =
                JakartaServletMockUtils.buildServletContext().attributeMap(servletContextAttributes).build();
        final HttpServletRequest mockRequest =
                JakartaServletMockUtils.buildRequest(mockServletContext, "WebVariablesMap")
                        .attributeMap(requestAttributes)
                        .parameterMap(requestParameters)
                        .locale(LOCALE)
                        .build();
        final HttpServletResponse mockResponse = JakartaServletMockUtils.buildResponse().build();

        final IServletWebExchange webExchange =
                JakartaServletWebApplication.buildApplication(mockServletContext).buildExchange(mockRequest, mockResponse);


        final WebEngineContext vm = new WebEngineContext(configuration, templateData1, null, webExchange, LOCALE, null);

        vm.setVariable("one", "a value");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("a value", vm.getVariable("one"));

        vm.increaseLevel();
        vm.setVariable("one", "hello");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("hello", vm.getVariable("one"));

        vm.setVariable("two", "twello");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertEquals("hello", vm.getVariable("one"));
        Assertions.assertEquals("twello", vm.getVariable("two"));

        vm.setVariable("three", "trwello");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertEquals("hello", vm.getVariable("one"));
        Assertions.assertEquals("twello", vm.getVariable("two"));
        Assertions.assertEquals("trwello", vm.getVariable("three"));

        vm.setVariable("four", "fwello");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertTrue(vm.containsVariable("four"));
        Assertions.assertEquals("hello", vm.getVariable("one"));
        Assertions.assertEquals("twello", vm.getVariable("two"));
        Assertions.assertEquals("trwello", vm.getVariable("three"));
        Assertions.assertEquals("fwello", vm.getVariable("four"));

        vm.setVariable("five", "vwello");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertTrue(vm.containsVariable("four"));
        Assertions.assertTrue(vm.containsVariable("five"));
        Assertions.assertEquals("hello", vm.getVariable("one"));
        Assertions.assertEquals("twello", vm.getVariable("two"));
        Assertions.assertEquals("trwello", vm.getVariable("three"));
        Assertions.assertEquals("fwello", vm.getVariable("four"));
        Assertions.assertEquals("vwello", vm.getVariable("five"));

        vm.setVariable("six", "swello");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertTrue(vm.containsVariable("four"));
        Assertions.assertTrue(vm.containsVariable("five"));
        Assertions.assertTrue(vm.containsVariable("six"));
        Assertions.assertEquals("hello", vm.getVariable("one"));
        Assertions.assertEquals("twello", vm.getVariable("two"));
        Assertions.assertEquals("trwello", vm.getVariable("three"));
        Assertions.assertEquals("fwello", vm.getVariable("four"));
        Assertions.assertEquals("vwello", vm.getVariable("five"));
        Assertions.assertEquals("swello", vm.getVariable("six"));

        vm.setVariable("seven", "svwello");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertTrue(vm.containsVariable("four"));
        Assertions.assertTrue(vm.containsVariable("five"));
        Assertions.assertTrue(vm.containsVariable("six"));
        Assertions.assertTrue(vm.containsVariable("seven"));
        Assertions.assertEquals("hello", vm.getVariable("one"));
        Assertions.assertEquals("twello", vm.getVariable("two"));
        Assertions.assertEquals("trwello", vm.getVariable("three"));
        Assertions.assertEquals("fwello", vm.getVariable("four"));
        Assertions.assertEquals("vwello", vm.getVariable("five"));
        Assertions.assertEquals("swello", vm.getVariable("six"));
        Assertions.assertEquals("svwello", vm.getVariable("seven"));

    }




    @Test
    public void test10() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);

        final Map<String,Object> starting = new LinkedHashMap<String, Object>();
        starting.put("one", "ha");
        starting.put("ten", "tieen");

        final Map<String,Object> servletContextAttributes = new LinkedHashMap<String, Object>();
        final Map<String,Object> requestAttributes = new LinkedHashMap<String, Object>();
        final Map<String,String[]> requestParameters = new LinkedHashMap<String, String[]>();

        final ServletContext mockServletContext =
                JakartaServletMockUtils.buildServletContext().attributeMap(servletContextAttributes).build();
        final HttpServletRequest mockRequest =
                JakartaServletMockUtils.buildRequest(mockServletContext, "WebVariablesMap")
                        .attributeMap(requestAttributes)
                        .parameterMap(requestParameters)
                        .locale(LOCALE)
                        .build();
        final HttpServletResponse mockResponse = JakartaServletMockUtils.buildResponse().build();

        final IServletWebExchange webExchange =
                JakartaServletWebApplication.buildApplication(mockServletContext).buildExchange(mockRequest, mockResponse);


        final WebEngineContext vm = new WebEngineContext(configuration, templateData1, null, webExchange, LOCALE, starting);

        Assertions.assertEquals("{0:{one=ha, ten=tieen}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=ha, ten=tieen}(test01)", vm.toString());
        Assertions.assertEquals(createSet("ten","one"), vm.getVariableNames());
        Assertions.assertFalse(vm.isVariableLocal("ten"));

        vm.setVariable("one", "a value");
        Assertions.assertFalse(vm.isVariableLocal("one"));

        Assertions.assertEquals("{0:{one=a value, ten=tieen}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=a value, ten=tieen}(test01)", vm.toString());
        Assertions.assertEquals(createSet("ten","one"), vm.getVariableNames());

        vm.increaseLevel();
        vm.setVariable("one", "hello");
        Assertions.assertTrue(vm.isVariableLocal("one"));

        Assertions.assertEquals("{1:{one=hello},0:{one=a value, ten=tieen}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=hello, ten=tieen}(test01)", vm.toString());
        Assertions.assertEquals(createSet("ten","one"), vm.getVariableNames());

        vm.setVariable("two", "twello");
        Assertions.assertTrue(vm.isVariableLocal("two"));

        Assertions.assertEquals("{1:{one=hello, two=twello},0:{one=a value, ten=tieen}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=hello, ten=tieen, two=twello}(test01)", vm.toString());
        Assertions.assertEquals(createSet("two","ten","one"), vm.getVariableNames());

        // This are directly set into the request, so they should only be affected by higher levels, never by decreasing levels
        mockRequest.setAttribute("one", "outer1");
        mockRequest.setAttribute("six", "outer6");

        Assertions.assertEquals("{1:{two=twello},0:{one=outer1, ten=tieen, six=outer6}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=outer1, ten=tieen, two=twello, six=outer6}(test01)", vm.toString());
        Assertions.assertEquals(createSet("two","ten","one","six"), vm.getVariableNames());

        vm.increaseLevel();

        vm.setVariable("one", "helloz");

        Assertions.assertEquals("{2:{one=helloz},1:{two=twello},0:{one=outer1, ten=tieen, six=outer6}(test01)}[2]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=helloz, ten=tieen, two=twello, six=outer6}(test01)", vm.toString());
        Assertions.assertEquals(createSet("two","ten","one","six"), vm.getVariableNames());

        vm.decreaseLevel();

        Assertions.assertEquals("{1:{two=twello},0:{one=outer1, ten=tieen, six=outer6}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=outer1, ten=tieen, two=twello, six=outer6}(test01)", vm.toString());
        Assertions.assertEquals(createSet("two","ten","one","six"), vm.getVariableNames());

        vm.decreaseLevel();

        Assertions.assertEquals("{0:{one=outer1, ten=tieen, six=outer6}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=outer1, ten=tieen, six=outer6}(test01)", vm.toString());
        Assertions.assertEquals(createSet("ten","one","six"), vm.getVariableNames());

    }


    @Test
    public void test11() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);

        final Map<String,Object> servletContextAttributes = new LinkedHashMap<String, Object>();
        final Map<String,Object> requestAttributes = new LinkedHashMap<String, Object>();
        final Map<String,String[]> requestParameters = new LinkedHashMap<String, String[]>();

        final ServletContext mockServletContext =
                JakartaServletMockUtils.buildServletContext().attributeMap(servletContextAttributes).build();
        final HttpServletRequest mockRequest =
                JakartaServletMockUtils.buildRequest(mockServletContext, "WebVariablesMap")
                        .attributeMap(requestAttributes)
                        .parameterMap(requestParameters)
                        .locale(LOCALE)
                        .build();
        final HttpServletResponse mockResponse = JakartaServletMockUtils.buildResponse().build();

        final IServletWebExchange webExchange =
                JakartaServletWebApplication.buildApplication(mockServletContext).buildExchange(mockRequest, mockResponse);


        final WebEngineContext vm = new WebEngineContext(configuration, templateData1, null, webExchange, LOCALE, null);

        vm.setVariable("one", "a value");
        Assertions.assertFalse(vm.hasSelectionTarget());
        Assertions.assertNull(vm.getSelectionTarget());
        Assertions.assertEquals("{0:{one=a value}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=a value}(test01)", vm.toString());

        vm.increaseLevel();

        vm.setVariable("one", "hello");
        vm.removeVariable("one");
        vm.setVariable("one", "hello");
        vm.removeVariable("two");
        vm.setVariable("two", "twello");
        vm.setVariable("two", "twellor");
        Assertions.assertFalse(vm.hasSelectionTarget());
        Assertions.assertNull(vm.getSelectionTarget());
        Assertions.assertEquals("{1:{one=hello, two=twellor},0:{one=a value}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=hello, two=twellor}(test01)", vm.toString());

        vm.increaseLevel();

        vm.setVariable("three", "twelloree");
        vm.setVariable("one", "atwe");
        Assertions.assertFalse(vm.hasSelectionTarget());
        Assertions.assertNull(vm.getSelectionTarget());
        Assertions.assertEquals("{2:{three=twelloree, one=atwe},1:{one=hello, two=twellor},0:{one=a value}(test01)}[2]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, two=twellor, three=twelloree}(test01)", vm.toString());

        vm.setSelectionTarget("BIGFORM");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertTrue(vm.hasSelectionTarget());
        Assertions.assertEquals("BIGFORM", vm.getSelectionTarget());
        Assertions.assertEquals("{2:{three=twelloree, one=atwe}<BIGFORM>,1:{one=hello, two=twellor},0:{one=a value}(test01)}[2]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, two=twellor, three=twelloree}<BIGFORM>(test01)", vm.toString());

        vm.increaseLevel();

        vm.removeVariable("two");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertTrue(vm.hasSelectionTarget());
        Assertions.assertEquals("BIGFORM", vm.getSelectionTarget());
        Assertions.assertEquals("{3:{two=null},2:{three=twelloree, one=atwe}<BIGFORM>,1:{one=hello, two=twellor},0:{one=a value}(test01)}[3]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree}<BIGFORM>(test01)", vm.toString());

        vm.increaseLevel();

        vm.removeVariable("two");
        vm.setSelectionTarget("SMALLFORM");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertTrue(vm.hasSelectionTarget());
        Assertions.assertEquals("SMALLFORM", vm.getSelectionTarget());
        Assertions.assertEquals("{4:<SMALLFORM>,3:{two=null},2:{three=twelloree, one=atwe}<BIGFORM>,1:{one=hello, two=twellor},0:{one=a value}(test01)}[4]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree}<SMALLFORM>(test01)", vm.toString());


        vm.increaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertTrue(vm.hasSelectionTarget());
        Assertions.assertEquals("SMALLFORM", vm.getSelectionTarget());
        Assertions.assertEquals("{4:<SMALLFORM>,3:{two=null},2:{three=twelloree, one=atwe}<BIGFORM>,1:{one=hello, two=twellor},0:{one=a value}(test01)}[5]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree}<SMALLFORM>(test01)", vm.toString());

        vm.setVariable("four", "lotwss");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertTrue(vm.containsVariable("four"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertEquals("lotwss", vm.getVariable("four"));
        Assertions.assertTrue(vm.hasSelectionTarget());
        Assertions.assertEquals("SMALLFORM", vm.getSelectionTarget());
        Assertions.assertEquals("{5:{four=lotwss},4:<SMALLFORM>,3:{two=null},2:{three=twelloree, one=atwe}<BIGFORM>,1:{one=hello, two=twellor},0:{one=a value}(test01)}[5]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree, four=lotwss}<SMALLFORM>(test01)", vm.toString());

        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertFalse(vm.containsVariable("four"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertNull(vm.getVariable("four"));
        Assertions.assertTrue(vm.hasSelectionTarget());
        Assertions.assertEquals("SMALLFORM", vm.getSelectionTarget());
        Assertions.assertEquals("{4:<SMALLFORM>,3:{two=null},2:{three=twelloree, one=atwe}<BIGFORM>,1:{one=hello, two=twellor},0:{one=a value}(test01)}[4]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree}<SMALLFORM>(test01)", vm.toString());

        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertFalse(vm.containsVariable("four"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertNull(vm.getVariable("four"));
        Assertions.assertTrue(vm.hasSelectionTarget());
        Assertions.assertEquals("BIGFORM", vm.getSelectionTarget());
        Assertions.assertEquals("{3:{two=null},2:{three=twelloree, one=atwe}<BIGFORM>,1:{one=hello, two=twellor},0:{one=a value}(test01)}[3]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree}<BIGFORM>(test01)", vm.toString());

        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertFalse(vm.containsVariable("four"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertNull(vm.getVariable("four"));
        Assertions.assertTrue(vm.hasSelectionTarget());
        Assertions.assertEquals("BIGFORM", vm.getSelectionTarget());
        Assertions.assertEquals("{2:{three=twelloree, one=atwe}<BIGFORM>,1:{one=hello, two=twellor},0:{one=a value}(test01)}[2]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree, two=twellor}<BIGFORM>(test01)", vm.toString());

        vm.setSelectionTarget("MEDIUMFORM");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertFalse(vm.containsVariable("four"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertNull(vm.getVariable("four"));
        Assertions.assertTrue(vm.hasSelectionTarget());
        Assertions.assertEquals("MEDIUMFORM", vm.getSelectionTarget());
        Assertions.assertEquals("{2:{three=twelloree, one=atwe}<MEDIUMFORM>,1:{one=hello, two=twellor},0:{one=a value}(test01)}[2]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree, two=twellor}<MEDIUMFORM>(test01)", vm.toString());


        vm.decreaseLevel();

        Assertions.assertFalse(vm.hasSelectionTarget());
        Assertions.assertNull(vm.getSelectionTarget());
        Assertions.assertEquals("{1:{one=hello, two=twellor},0:{one=a value}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=hello, two=twellor}(test01)", vm.toString());


        vm.decreaseLevel();

        Assertions.assertFalse(vm.hasSelectionTarget());
        Assertions.assertNull(vm.getSelectionTarget());
        Assertions.assertEquals("{0:{one=a value}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=a value}(test01)", vm.toString());

        vm.setSelectionTarget("TOTALFORM");

        Assertions.assertTrue(vm.hasSelectionTarget());
        Assertions.assertEquals("TOTALFORM", vm.getSelectionTarget());
        Assertions.assertEquals("{0:{one=a value}<TOTALFORM>(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=a value}<TOTALFORM>(test01)", vm.toString());


        vm.increaseLevel();

        Assertions.assertTrue(vm.hasSelectionTarget());
        Assertions.assertEquals("TOTALFORM", vm.getSelectionTarget());
        Assertions.assertEquals("{0:{one=a value}<TOTALFORM>(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=a value}<TOTALFORM>(test01)", vm.toString());

        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertFalse(vm.containsVariable("three"));
        Assertions.assertFalse(vm.containsVariable("four"));
        Assertions.assertEquals("a value", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertNull(vm.getVariable("three"));
        Assertions.assertNull(vm.getVariable("four"));
        Assertions.assertTrue(vm.hasSelectionTarget());
        Assertions.assertEquals("TOTALFORM", vm.getSelectionTarget());
        Assertions.assertEquals("{0:{one=a value}<TOTALFORM>(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=a value}<TOTALFORM>(test01)", vm.toString());

    }



    @Test
    public void test12() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);

        final Map<String,Object> servletContextAttributes = new LinkedHashMap<String, Object>();
        final Map<String,Object> requestAttributes = new LinkedHashMap<String, Object>();
        final Map<String,String[]> requestParameters = new LinkedHashMap<String, String[]>();

        final ServletContext mockServletContext =
                JakartaServletMockUtils.buildServletContext().attributeMap(servletContextAttributes).build();
        final HttpServletRequest mockRequest =
                JakartaServletMockUtils.buildRequest(mockServletContext, "WebVariablesMap")
                        .attributeMap(requestAttributes)
                        .parameterMap(requestParameters)
                        .locale(LOCALE)
                        .build();
        final HttpServletResponse mockResponse = JakartaServletMockUtils.buildResponse().build();

        final IServletWebExchange webExchange =
                JakartaServletWebApplication.buildApplication(mockServletContext).buildExchange(mockRequest, mockResponse);


        final WebEngineContext vm = new WebEngineContext(configuration, templateData1, null, webExchange, LOCALE, null);

        vm.setVariable("one", "a val1");

        vm.increaseLevel();

        vm.setVariable("one", "a val2");
        vm.setSelectionTarget("FORM");

        Assertions.assertTrue(vm.hasSelectionTarget());
        Assertions.assertEquals("FORM", vm.getSelectionTarget());

        vm.decreaseLevel();

        vm.setVariable("one", "a val3");
        Assertions.assertFalse(vm.hasSelectionTarget());
        Assertions.assertNull(vm.getSelectionTarget());

        vm.increaseLevel();

        vm.setVariable("one", "a val4");
        Assertions.assertFalse(vm.hasSelectionTarget());
        Assertions.assertNull(vm.getSelectionTarget());

        vm.increaseLevel();

        vm.setVariable("one", "a val5");
        Assertions.assertFalse(vm.hasSelectionTarget());
        Assertions.assertNull(vm.getSelectionTarget());

    }




    @Test
    public void test13() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);
        final TemplateData templateData2 = TestTemplateDataConfigurationBuilder.build("test02", TemplateMode.HTML);
        final TemplateData templateData3 = TestTemplateDataConfigurationBuilder.build("test03", TemplateMode.XML);
        final TemplateData templateData4 = TestTemplateDataConfigurationBuilder.build("test04", TemplateMode.TEXT);

        final Map<String,Object> servletContextAttributes = new LinkedHashMap<String, Object>();
        final Map<String,Object> requestAttributes = new LinkedHashMap<String, Object>();
        final Map<String,String[]> requestParameters = new LinkedHashMap<String, String[]>();

        final ServletContext mockServletContext =
                JakartaServletMockUtils.buildServletContext().attributeMap(servletContextAttributes).build();
        final HttpServletRequest mockRequest =
                JakartaServletMockUtils.buildRequest(mockServletContext, "WebVariablesMap")
                        .attributeMap(requestAttributes)
                        .parameterMap(requestParameters)
                        .locale(LOCALE)
                        .build();
        final HttpServletResponse mockResponse = JakartaServletMockUtils.buildResponse().build();

        final IServletWebExchange webExchange =
                JakartaServletWebApplication.buildApplication(mockServletContext).buildExchange(mockRequest, mockResponse);


        final WebEngineContext vm = new WebEngineContext(configuration, templateData1, null, webExchange, LOCALE, null);

        Assertions.assertEquals(TemplateMode.HTML, vm.getTemplateMode());
        Assertions.assertSame(templateData1, vm.getTemplateData());
        Assertions.assertEquals(Arrays.asList(templateData1), vm.getTemplateStack());

        vm.setVariable("one", "a value");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("a value", vm.getVariable("one"));

        Assertions.assertEquals(TemplateMode.HTML, vm.getTemplateMode());
        Assertions.assertSame(templateData1, vm.getTemplateData());
        Assertions.assertEquals(Arrays.asList(templateData1), vm.getTemplateStack());


        vm.setVariable("one", "two values");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("two values", vm.getVariable("one"));

        vm.removeVariable("one");

        Assertions.assertFalse(vm.containsVariable("one"));
        Assertions.assertNull(vm.getVariable("one"));

        vm.setVariable("one", "two values");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("two values", vm.getVariable("one"));

        vm.increaseLevel();

        Assertions.assertEquals(TemplateMode.HTML, vm.getTemplateMode());
        Assertions.assertSame(templateData1, vm.getTemplateData());

        vm.setTemplateData(templateData2);

        Assertions.assertEquals(TemplateMode.HTML, vm.getTemplateMode());
        Assertions.assertSame(templateData2, vm.getTemplateData());
        Assertions.assertEquals(Arrays.asList(templateData1, templateData2), vm.getTemplateStack());

        vm.setVariable("one", "hello");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("hello", vm.getVariable("one"));

        vm.removeVariable("one");

        Assertions.assertEquals(TemplateMode.HTML, vm.getTemplateMode());
        Assertions.assertSame(templateData2, vm.getTemplateData());
        Assertions.assertEquals(Arrays.asList(templateData1, templateData2), vm.getTemplateStack());

        Assertions.assertFalse(vm.containsVariable("one"));
        Assertions.assertNull(vm.getVariable("one"));

        vm.setVariable("one", "hello");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("hello", vm.getVariable("one"));

        vm.removeVariable("two");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertEquals("hello", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("twello"));

        vm.setVariable("two", "twello");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertEquals("hello", vm.getVariable("one"));
        Assertions.assertEquals("twello", vm.getVariable("two"));

        vm.removeVariable("two");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertEquals("hello", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("twello"));

        vm.removeVariable("one");

        Assertions.assertFalse(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertNull(vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("twello"));

        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertEquals("two values", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));

        vm.increaseLevel();

        Assertions.assertEquals(TemplateMode.HTML, vm.getTemplateMode());
        Assertions.assertSame(templateData1, vm.getTemplateData());
        Assertions.assertEquals(Arrays.asList(templateData1), vm.getTemplateStack());

        vm.setVariable("two", "twellor");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertEquals("two values", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));

        vm.setTemplateData(templateData2);

        vm.increaseLevel();

        Assertions.assertEquals(TemplateMode.HTML, vm.getTemplateMode());
        Assertions.assertSame(templateData2, vm.getTemplateData());
        Assertions.assertEquals(Arrays.asList(templateData1,templateData2), vm.getTemplateStack());

        vm.setTemplateData(templateData3);

        Assertions.assertEquals(TemplateMode.XML, vm.getTemplateMode());
        Assertions.assertSame(templateData3, vm.getTemplateData());
        Assertions.assertEquals(Arrays.asList(templateData1,templateData2,templateData3), vm.getTemplateStack());

        vm.setVariable("three", "twelloree");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertEquals("two values", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));

        vm.setVariable("one", "atwe");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));

        Assertions.assertEquals(TemplateMode.XML, vm.getTemplateMode());
        Assertions.assertSame(templateData3, vm.getTemplateData());
        Assertions.assertEquals(Arrays.asList(templateData1,templateData2,templateData3), vm.getTemplateStack());

        vm.increaseLevel();

        Assertions.assertEquals(TemplateMode.XML, vm.getTemplateMode());
        Assertions.assertSame(templateData3, vm.getTemplateData());
        Assertions.assertEquals(Arrays.asList(templateData1,templateData2,templateData3), vm.getTemplateStack());

        vm.removeVariable("two");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));

        vm.increaseLevel();

        Assertions.assertEquals(TemplateMode.XML, vm.getTemplateMode());
        Assertions.assertSame(templateData3, vm.getTemplateData());
        Assertions.assertEquals(Arrays.asList(templateData1,templateData2,templateData3), vm.getTemplateStack());

        vm.removeVariable("two");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));

        vm.increaseLevel();

        Assertions.assertEquals(TemplateMode.XML, vm.getTemplateMode());
        Assertions.assertSame(templateData3, vm.getTemplateData());
        Assertions.assertEquals(Arrays.asList(templateData1,templateData2,templateData3), vm.getTemplateStack());

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));

        vm.setVariable("four", "lotwss");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertTrue(vm.containsVariable("four"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertEquals("lotwss", vm.getVariable("four"));

        vm.setVariable("two", "itwiii");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertTrue(vm.containsVariable("four"));
        Assertions.assertFalse(vm.containsVariable("five"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertEquals("itwiii", vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertEquals("lotwss", vm.getVariable("four"));

        vm.decreaseLevel();

        Assertions.assertEquals(TemplateMode.XML, vm.getTemplateMode());
        Assertions.assertSame(templateData3, vm.getTemplateData());
        Assertions.assertEquals(Arrays.asList(templateData1,templateData2,templateData3), vm.getTemplateStack());

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertFalse(vm.containsVariable("four"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertNull(vm.getVariable("four"));

        vm.decreaseLevel();

        Assertions.assertEquals(TemplateMode.XML, vm.getTemplateMode());
        Assertions.assertSame(templateData3, vm.getTemplateData());
        Assertions.assertEquals(Arrays.asList(templateData1,templateData2,templateData3), vm.getTemplateStack());

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertFalse(vm.containsVariable("four"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertNull(vm.getVariable("four"));

        vm.decreaseLevel();

        Assertions.assertEquals(TemplateMode.XML, vm.getTemplateMode());
        Assertions.assertSame(templateData3, vm.getTemplateData());
        Assertions.assertEquals(Arrays.asList(templateData1,templateData2,templateData3), vm.getTemplateStack());

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertFalse(vm.containsVariable("four"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));
        Assertions.assertNull(vm.getVariable("four"));

        vm.decreaseLevel();

        Assertions.assertEquals(TemplateMode.HTML, vm.getTemplateMode());
        Assertions.assertSame(templateData2, vm.getTemplateData());
        Assertions.assertEquals(Arrays.asList(templateData1,templateData2), vm.getTemplateStack());

        vm.setTemplateData(templateData4);

        Assertions.assertEquals(TemplateMode.TEXT, vm.getTemplateMode());
        Assertions.assertSame(templateData4, vm.getTemplateData());
        Assertions.assertEquals(Arrays.asList(templateData1,templateData4), vm.getTemplateStack());

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertTrue(vm.containsVariable("two"));
        Assertions.assertFalse(vm.containsVariable("three"));
        Assertions.assertFalse(vm.containsVariable("four"));
        Assertions.assertEquals("two values", vm.getVariable("one"));
        Assertions.assertEquals("twellor", vm.getVariable("two"));
        Assertions.assertNull(vm.getVariable("three"));
        Assertions.assertNull(vm.getVariable("four"));

        vm.decreaseLevel();

        Assertions.assertEquals(TemplateMode.HTML, vm.getTemplateMode());
        Assertions.assertSame(templateData1, vm.getTemplateData());
        Assertions.assertEquals(Arrays.asList(templateData1), vm.getTemplateStack());

        vm.setTemplateData(templateData4);

        Assertions.assertEquals(TemplateMode.TEXT, vm.getTemplateMode());
        Assertions.assertSame(templateData4, vm.getTemplateData());
        Assertions.assertEquals(Arrays.asList(templateData4), vm.getTemplateStack());

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertFalse(vm.containsVariable("three"));
        Assertions.assertFalse(vm.containsVariable("four"));
        Assertions.assertEquals("two values", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertNull(vm.getVariable("three"));
        Assertions.assertNull(vm.getVariable("four"));

    }


    @Test
    public void test14() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);

        final Map<String,Object> servletContextAttributes = new LinkedHashMap<String, Object>();
        final Map<String,Object> requestAttributes = new LinkedHashMap<String, Object>();
        final Map<String,String[]> requestParameters = new LinkedHashMap<String, String[]>();

        final ServletContext mockServletContext =
                JakartaServletMockUtils.buildServletContext().attributeMap(servletContextAttributes).build();
        final HttpServletRequest mockRequest =
                JakartaServletMockUtils.buildRequest(mockServletContext, "WebVariablesMap")
                        .attributeMap(requestAttributes)
                        .parameterMap(requestParameters)
                        .locale(LOCALE)
                        .build();
        final HttpServletResponse mockResponse = JakartaServletMockUtils.buildResponse().build();

        final IServletWebExchange webExchange =
                JakartaServletWebApplication.buildApplication(mockServletContext).buildExchange(mockRequest, mockResponse);


        final WebEngineContext vm = new WebEngineContext(configuration, templateData1, null, webExchange, LOCALE, null);

        /*
         * Note JavaxWebEngineContext works in a different way to EngineContext because it is based on the
         * HttpServletRequest, and the request considers setting an attribute to null the exact same thing as
         * removing it.
         */

        Assertions.assertFalse(vm.containsVariable("one"));
        Assertions.assertNull(vm.getVariable("one"));

        vm.setVariable("one", null);

        Assertions.assertFalse(vm.containsVariable("one"));
        Assertions.assertNull(vm.getVariable("one"));

        vm.setVariable("one", "a value");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("a value", vm.getVariable("one"));

        vm.increaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("a value", vm.getVariable("one"));

        vm.setVariable("one", null);

        Assertions.assertFalse(vm.containsVariable("one"));
        Assertions.assertNull(vm.getVariable("one"));

        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("a value", vm.getVariable("one"));

    }




    private static boolean enumerationContains(final Enumeration<String> enumeration, final String value) {
        while (enumeration.hasMoreElements()) {
            final String enumValue = enumeration.nextElement();
            if (enumValue == null) {
                if (value == null) {
                    return true;
                }
            } else {
                if (enumValue.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }



    private static Set<String> createSet(final String... elements) {
        final Set<String> result = new LinkedHashSet<String>();
        for (final String element : elements) {
            result.add(element);
        }
        return result;
    }


}
