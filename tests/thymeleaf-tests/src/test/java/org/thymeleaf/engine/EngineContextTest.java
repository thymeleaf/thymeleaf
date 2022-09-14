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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.EngineContext;
import org.thymeleaf.context.TestTemplateEngineConfigurationBuilder;
import org.thymeleaf.standard.inline.StandardTextInliner;
import org.thymeleaf.templatemode.TemplateMode;


public final class EngineContextTest {


    private static final Locale LOCALE = Locale.US;



    @Test
    public void test01() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);

        final EngineContext vm = new EngineContext(configuration, templateData1, null, LOCALE, null);

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

        final EngineContext vm = new EngineContext(configuration, templateData1, null, LOCALE, starting);

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

        final EngineContext vm = new EngineContext(configuration, templateData1, null, LOCALE, null);

        vm.setVariable("one", "a value");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("a value", vm.getVariable("one"));

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

        vm.setVariable("one", "hello");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("hello", vm.getVariable("one"));

        vm.removeVariable("one");

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

        vm.removeVariable("two");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertFalse(vm.containsVariable("two"));
        Assertions.assertTrue(vm.containsVariable("three"));
        Assertions.assertEquals("atwe", vm.getVariable("one"));
        Assertions.assertNull(vm.getVariable("two"));
        Assertions.assertEquals("twelloree", vm.getVariable("three"));

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
    public void test04() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);

        final EngineContext vm = new EngineContext(configuration, templateData1, null, LOCALE, null);

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

        Assertions.assertEquals("{1:{two=twellor},0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values, two=twellor}(test01)", vm.toString());

        vm.increaseLevel();
        vm.setVariable("three", "twelloree");

        Assertions.assertEquals("{2:{three=twelloree},1:{two=twellor},0:{one=two values}(test01)}[2]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values, two=twellor, three=twelloree}(test01)", vm.toString());

        vm.setVariable("one", "atwe");

        Assertions.assertEquals("{2:{one=atwe, three=twelloree},1:{two=twellor},0:{one=two values}(test01)}[2]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, two=twellor, three=twelloree}(test01)", vm.toString());

        vm.increaseLevel();
        vm.increaseLevel();
        vm.increaseLevel();

        Assertions.assertEquals("{2:{one=atwe, three=twelloree},1:{two=twellor},0:{one=two values}(test01)}[5]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, two=twellor, three=twelloree}(test01)", vm.toString());

        vm.setVariable("four", "lotwss");

        Assertions.assertEquals("{5:{four=lotwss},2:{one=atwe, three=twelloree},1:{two=twellor},0:{one=two values}(test01)}[5]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, two=twellor, three=twelloree, four=lotwss}(test01)", vm.toString());

        vm.setVariable("two", "itwiii");

        Assertions.assertEquals("{5:{four=lotwss, two=itwiii},2:{one=atwe, three=twelloree},1:{two=twellor},0:{one=two values}(test01)}[5]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, two=itwiii, three=twelloree, four=lotwss}(test01)", vm.toString());

        vm.decreaseLevel();

        Assertions.assertEquals("{2:{one=atwe, three=twelloree},1:{two=twellor},0:{one=two values}(test01)}[4]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, two=twellor, three=twelloree}(test01)", vm.toString());

        vm.decreaseLevel();

        Assertions.assertEquals("{2:{one=atwe, three=twelloree},1:{two=twellor},0:{one=two values}(test01)}[3]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, two=twellor, three=twelloree}(test01)", vm.toString());

        vm.decreaseLevel();

        Assertions.assertEquals("{2:{one=atwe, three=twelloree},1:{two=twellor},0:{one=two values}(test01)}[2]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, two=twellor, three=twelloree}(test01)", vm.toString());

        vm.decreaseLevel();

        Assertions.assertEquals("{1:{two=twellor},0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values, two=twellor}(test01)", vm.toString());

        vm.decreaseLevel();

        Assertions.assertEquals("{0:{one=two values}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values}(test01)", vm.toString());

    }





    @Test
    public void test05() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);

        final EngineContext vm = new EngineContext(configuration, templateData1, null, LOCALE, null);

        vm.setVariable("one", "a value");

        Assertions.assertEquals("{0:{one=a value}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=a value}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one"), vm.getVariableNames());

        vm.setVariable("one", "two values");

        Assertions.assertEquals("{0:{one=two values}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one"), vm.getVariableNames());

        vm.removeVariable("one");

        Assertions.assertEquals("{0:{}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{}(test01)", vm.toString());
        Assertions.assertEquals(Collections.emptySet(), vm.getVariableNames());

        vm.setVariable("one", "two values");

        Assertions.assertEquals("{0:{one=two values}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one"), vm.getVariableNames());

        vm.increaseLevel();

        vm.setVariable("one", "hello");

        Assertions.assertEquals("{1:{one=hello},0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=hello}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one"), vm.getVariableNames());

        vm.removeVariable("one");

        Assertions.assertEquals("{1:{one=(*removed*)},0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
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
        vm.setInliner(new StandardTextInliner(configuration));

        Assertions.assertEquals("{1:{one=hello, two=twello}[StandardTextInliner],0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=hello, two=twello}[StandardTextInliner](test01)", vm.toString());
        Assertions.assertEquals(createSet("one","two"), vm.getVariableNames());

        vm.removeVariable("two");

        Assertions.assertEquals("{1:{one=hello}[StandardTextInliner],0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=hello}[StandardTextInliner](test01)", vm.toString());
        Assertions.assertEquals(createSet("one"), vm.getVariableNames());

        vm.removeVariable("one");

        Assertions.assertEquals("{1:{one=(*removed*)}[StandardTextInliner],0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{}[StandardTextInliner](test01)", vm.toString());
        Assertions.assertEquals(Collections.emptySet(), vm.getVariableNames());

        vm.decreaseLevel();

        Assertions.assertEquals("{0:{one=two values}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one"), vm.getVariableNames());

        vm.increaseLevel();
        vm.setVariable("two", "twellor");

        Assertions.assertEquals("{1:{two=twellor},0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values, two=twellor}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one","two"), vm.getVariableNames());

        vm.increaseLevel();
        vm.setVariable("three", "twelloree");

        Assertions.assertEquals("{2:{three=twelloree},1:{two=twellor},0:{one=two values}(test01)}[2]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values, two=twellor, three=twelloree}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one","three","two"), vm.getVariableNames());

        vm.setVariable("one", "atwe");

        Assertions.assertEquals("{2:{one=atwe, three=twelloree},1:{two=twellor},0:{one=two values}(test01)}[2]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, two=twellor, three=twelloree}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one","three","two"), vm.getVariableNames());

        vm.increaseLevel();

        vm.removeVariable("two");

        Assertions.assertEquals("{3:{two=(*removed*)},2:{one=atwe, three=twelloree},1:{two=twellor},0:{one=two values}(test01)}[3]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one","three"), vm.getVariableNames());

        vm.increaseLevel();

        vm.removeVariable("two");

        Assertions.assertEquals("{3:{two=(*removed*)},2:{one=atwe, three=twelloree},1:{two=twellor},0:{one=two values}(test01)}[4]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one","three"), vm.getVariableNames());

        vm.increaseLevel();

        Assertions.assertEquals("{3:{two=(*removed*)},2:{one=atwe, three=twelloree},1:{two=twellor},0:{one=two values}(test01)}[5]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one","three"), vm.getVariableNames());

        vm.setVariable("four", "lotwss");

        Assertions.assertEquals("{5:{four=lotwss},3:{two=(*removed*)},2:{one=atwe, three=twelloree},1:{two=twellor},0:{one=two values}(test01)}[5]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree, four=lotwss}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one","three","four"), vm.getVariableNames());

        vm.setVariable("two", "itwiii");

        Assertions.assertEquals("{5:{four=lotwss, two=itwiii},3:{two=(*removed*)},2:{one=atwe, three=twelloree},1:{two=twellor},0:{one=two values}(test01)}[5]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree, four=lotwss, two=itwiii}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one","three","four","two"), vm.getVariableNames());

        vm.decreaseLevel();

        Assertions.assertEquals("{3:{two=(*removed*)},2:{one=atwe, three=twelloree},1:{two=twellor},0:{one=two values}(test01)}[4]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one","three"), vm.getVariableNames());

        vm.decreaseLevel();

        Assertions.assertEquals("{3:{two=(*removed*)},2:{one=atwe, three=twelloree},1:{two=twellor},0:{one=two values}(test01)}[3]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, three=twelloree}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one","three"), vm.getVariableNames());

        vm.decreaseLevel();

        Assertions.assertEquals("{2:{one=atwe, three=twelloree},1:{two=twellor},0:{one=two values}(test01)}[2]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, two=twellor, three=twelloree}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one","three","two"), vm.getVariableNames());

        vm.decreaseLevel();

        Assertions.assertEquals("{1:{two=twellor},0:{one=two values}(test01)}[1]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values, two=twellor}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one","two"), vm.getVariableNames());

        vm.decreaseLevel();

        Assertions.assertEquals("{0:{one=two values}(test01)}[0]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=two values}(test01)", vm.toString());
        Assertions.assertEquals(createSet("one"), vm.getVariableNames());

    }


    @Test
    public void test06() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);

        final EngineContext vm = new EngineContext(configuration, templateData1, null, LOCALE, null);

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
    public void test07() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);

        final EngineContext vm = new EngineContext(configuration, templateData1, null, LOCALE, null);

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
        Assertions.assertEquals("{2:{one=atwe, three=twelloree},1:{one=hello, two=twellor},0:{one=a value}(test01)}[2]", vm.getStringRepresentationByLevel());
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
        Assertions.assertEquals("{2:{one=atwe, three=twelloree}<BIGFORM>,1:{one=hello, two=twellor},0:{one=a value}(test01)}[2]", vm.getStringRepresentationByLevel());
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
        Assertions.assertEquals("{3:{two=(*removed*)},2:{one=atwe, three=twelloree}<BIGFORM>,1:{one=hello, two=twellor},0:{one=a value}(test01)}[3]", vm.getStringRepresentationByLevel());
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
        Assertions.assertEquals("{4:<SMALLFORM>,3:{two=(*removed*)},2:{one=atwe, three=twelloree}<BIGFORM>,1:{one=hello, two=twellor},0:{one=a value}(test01)}[4]", vm.getStringRepresentationByLevel());
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
        Assertions.assertEquals("{4:<SMALLFORM>,3:{two=(*removed*)},2:{one=atwe, three=twelloree}<BIGFORM>,1:{one=hello, two=twellor},0:{one=a value}(test01)}[5]", vm.getStringRepresentationByLevel());
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
        Assertions.assertEquals("{5:{four=lotwss},4:<SMALLFORM>,3:{two=(*removed*)},2:{one=atwe, three=twelloree}<BIGFORM>,1:{one=hello, two=twellor},0:{one=a value}(test01)}[5]", vm.getStringRepresentationByLevel());
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
        Assertions.assertEquals("{4:<SMALLFORM>,3:{two=(*removed*)},2:{one=atwe, three=twelloree}<BIGFORM>,1:{one=hello, two=twellor},0:{one=a value}(test01)}[4]", vm.getStringRepresentationByLevel());
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
        Assertions.assertEquals("{3:{two=(*removed*)},2:{one=atwe, three=twelloree}<BIGFORM>,1:{one=hello, two=twellor},0:{one=a value}(test01)}[3]", vm.getStringRepresentationByLevel());
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
        Assertions.assertEquals("{2:{one=atwe, three=twelloree}<BIGFORM>,1:{one=hello, two=twellor},0:{one=a value}(test01)}[2]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, two=twellor, three=twelloree}<BIGFORM>(test01)", vm.toString());

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
        Assertions.assertEquals("{2:{one=atwe, three=twelloree}<MEDIUMFORM>,1:{one=hello, two=twellor},0:{one=a value}(test01)}[2]", vm.getStringRepresentationByLevel());
        Assertions.assertEquals("{one=atwe, two=twellor, three=twelloree}<MEDIUMFORM>(test01)", vm.toString());


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
    public void test08() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);

        final EngineContext vm = new EngineContext(configuration, templateData1, null, LOCALE, null);

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
    public void test09() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);
        final TemplateData templateData2 = TestTemplateDataConfigurationBuilder.build("test02", TemplateMode.HTML);
        final TemplateData templateData3 = TestTemplateDataConfigurationBuilder.build("test03", TemplateMode.XML);
        final TemplateData templateData4 = TestTemplateDataConfigurationBuilder.build("test04", TemplateMode.TEXT);

        final EngineContext vm = new EngineContext(configuration, templateData1, null, LOCALE, null);

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
    public void test10() {

        final IEngineConfiguration configuration = TestTemplateEngineConfigurationBuilder.build();
        final TemplateData templateData1 = TestTemplateDataConfigurationBuilder.build("test01", TemplateMode.HTML);

        final EngineContext vm = new EngineContext(configuration, templateData1, null, LOCALE, null);

        Assertions.assertFalse(vm.containsVariable("one"));
        Assertions.assertNull(vm.getVariable("one"));

        vm.setVariable("one", null);

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertNull(vm.getVariable("one"));

        vm.setVariable("one", "a value");

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("a value", vm.getVariable("one"));

        vm.increaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("a value", vm.getVariable("one"));

        vm.setVariable("one", null);

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertNull(vm.getVariable("one"));

        vm.decreaseLevel();

        Assertions.assertTrue(vm.containsVariable("one"));
        Assertions.assertEquals("a value", vm.getVariable("one"));

    }
    
    private static Set<String> createSet(final String... elements) {
        final Set<String> result = new LinkedHashSet<String>();
        for (final String element : elements) {
            result.add(element);
        }
        return result;
    }
    
    
}
