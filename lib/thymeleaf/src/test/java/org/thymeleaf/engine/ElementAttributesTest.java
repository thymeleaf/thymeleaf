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

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.TestTemplateEngineConfigurationBuilder;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.markup.HTMLTemplateParser;
import org.thymeleaf.templateparser.markup.XMLTemplateParser;
import org.thymeleaf.templateresource.StringTemplateResource;


public final class ElementAttributesTest {

    private static final HTMLTemplateParser HTML_PARSER = new HTMLTemplateParser(2, 4096);
    private static final XMLTemplateParser XML_PARSER = new XMLTemplateParser(2, 4096);
    private static final IEngineConfiguration TEMPLATE_ENGINE_CONFIGURATION = TestTemplateEngineConfigurationBuilder.build();




    @Test
    public void testHtmlElementAttributesAttrManagement() {

        Attributes attrs;
        
        AttributeDefinitions attributeDefinitions = new AttributeDefinitions(Collections.EMPTY_MAP);
        
        attrs = computeHtmlAttributes("<input>");
        Assertions.assertEquals("", attrs.toString());

        attrs = computeHtmlAttributes("<input type=\"text\">");
        Assertions.assertEquals("[type]", attrs.getAttributeMap().keySet().toString());
        Assertions.assertEquals(" type=\"text\"", attrs.toString());

        attrs = computeHtmlAttributes("<input type=\"text\"   value='hello!!!'>");
        Assertions.assertEquals("[type, value]", attrs.getAttributeMap().keySet().toString());
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "type");
        Assertions.assertEquals(" value='hello!!!'", attrs.toString());
        Assertions.assertEquals("[value]", attrs.getAttributeMap().keySet().toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "value");
        Assertions.assertEquals("", attrs.toString());
        Assertions.assertEquals("[]", attrs.getAttributeMap().keySet().toString());

        attrs = computeHtmlAttributes("<input type=\"text\"   value='hello!!!'    >");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, null, "type");
        Assertions.assertEquals(" value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, null, "value");
        Assertions.assertEquals("    ", attrs.toString());

        attrs = computeHtmlAttributes("<input th:type=\"text\"   th:value='hello!!!'    >");
        Assertions.assertEquals(" th:type=\"text\"   th:value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "th", "type");
        Assertions.assertEquals(" th:value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "th", "value");
        Assertions.assertEquals("    ", attrs.toString());

        attrs = computeHtmlAttributes("<input th:type=\"text\"   th:value='hello!!!'    >");
        Assertions.assertEquals(" th:type=\"text\"   th:value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "TH", "TYPE");
        Assertions.assertEquals(" th:value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "tH", "Value");
        Assertions.assertEquals("    ", attrs.toString());

        attrs = computeHtmlAttributes("<input th:type=\"text\"   th:value='hello!!!'    >");
        Assertions.assertEquals(" th:type=\"text\"   th:value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "data-th-type");
        Assertions.assertEquals(" th:value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "data-th-value");
        Assertions.assertEquals("    ", attrs.toString());

        attrs = computeHtmlAttributes("<input th:type=\"text\"   th:value='hello!!!'    >");
        Assertions.assertEquals(" th:type=\"text\"   th:value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(AttributeNames.forHTMLName("th:type"));
        Assertions.assertEquals(" th:value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(AttributeNames.forHTMLName("th:value"));
        Assertions.assertEquals("    ", attrs.toString());

        attrs = computeHtmlAttributes("<input th:type=\"text\"   th:value='hello!!!'    >");
        Assertions.assertEquals(" th:type=\"text\"   th:value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(AttributeNames.forHTMLName("th", "type"));
        Assertions.assertEquals(" th:value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(AttributeNames.forHTMLName("TH", "VALUE"));
        Assertions.assertEquals("    ", attrs.toString());

        attrs = computeHtmlAttributes("<input type=\"text\"   value='hello!!!'    ba>");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "value");
        Assertions.assertEquals(" type=\"text\"   ba", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "ba");
        Assertions.assertEquals(" type=\"text\"", attrs.toString());

        attrs = computeHtmlAttributes("<input type=\"text\"   value='hello!!!'    ba>");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "value");
        Assertions.assertEquals(" type=\"text\"   ba", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "type");
        Assertions.assertEquals(" ba", attrs.toString());

        attrs = computeHtmlAttributes("<input type=\"text\"   value='hello!!!'    ba >");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "value");
        Assertions.assertEquals(" type=\"text\"   ba ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "ba");
        Assertions.assertEquals(" type=\"text\" ", attrs.toString());

        attrs = computeHtmlAttributes("<input type=\"text\"   value='hello!!!'    ba >");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba ", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "value", "bye! :(", null);
        Assertions.assertEquals(" type=\"text\"   value='bye! :('    ba ", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "type", "one", null);
        Assertions.assertEquals(" type=\"one\"   value='bye! :('    ba ", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "ba", "two", null);
        Assertions.assertEquals(" type=\"one\"   value='bye! :('    ba=\"two\" ", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "ba", "three", AttributeValueQuotes.SINGLE);
        Assertions.assertEquals(" type=\"one\"   value='bye! :('    ba='three' ", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "ba", "four", AttributeValueQuotes.NONE);
        Assertions.assertEquals(" type=\"one\"   value='bye! :('    ba=four ", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "ba", "five", null);
        Assertions.assertEquals(" type=\"one\"   value='bye! :('    ba=five ", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "ba", null, null);
        Assertions.assertEquals(" type=\"one\"   value='bye! :('    ba ", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "ba", "six", null);
        Assertions.assertEquals(" type=\"one\"   value='bye! :('    ba=\"six\" ", attrs.toString());

        attrs = computeHtmlAttributes("<input type=\"text\"   value='hello!!!'    ba=twenty >");
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "ba", "thirty", null);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba=thirty ", attrs.toString());

        attrs = computeHtmlAttributes("<input type=\"text\"value='hello!!!' >");
        Assertions.assertEquals(" type=\"text\"value='hello!!!' ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "type");
        Assertions.assertEquals(" value='hello!!!' ", attrs.toString());

        attrs = computeHtmlAttributes("<input type=\"text\"value='hello!!!' name='one' >");
        Assertions.assertEquals(" type=\"text\"value='hello!!!' name='one' ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "type");
        Assertions.assertEquals(" value='hello!!!' name='one' ", attrs.toString());

        attrs = computeHtmlAttributes("<input type=\"text\"value='hello!!!' name='one'>");
        Assertions.assertEquals(" type=\"text\"value='hello!!!' name='one'", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "name");
        Assertions.assertEquals(" type=\"text\"value='hello!!!'", attrs.toString());

        attrs = computeHtmlAttributes("<input type=\"text\"   value='hello!!!'    ba= s>");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba= s", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "ba", null, null);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba", attrs.toString());

        attrs = computeHtmlAttributes("<input type=\"text\"   value='hello!!!'    ba= s>");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba= s", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "value", null, null);
        Assertions.assertEquals(" type=\"text\"   value    ba= s", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "value");
        Assertions.assertEquals(" type=\"text\"   ba= s", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "type", null, AttributeValueQuotes.SINGLE);
        Assertions.assertEquals(" type   ba= s", attrs.toString());

        attrs = computeHtmlAttributes("<input type=\"text\"   value='hello!!!'    ba= s>");
        Assertions.assertEquals("[type, value, ba]", attrs.getAttributeMap().keySet().toString());
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba= s", attrs.toString());
        attrs = new Attributes(null, null);
        Assertions.assertEquals("", attrs.toString());
        attrs = new Attributes(null, null);
        Assertions.assertEquals("", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "name", "onename", null);
        Assertions.assertEquals(" name=\"onename\"", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "value", "val", null);
        Assertions.assertEquals(" name=\"onename\" value=\"val\"", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "placeholder", null, null);
        Assertions.assertEquals(" name=\"onename\" value=\"val\" placeholder", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "placeholder", "a", null);
        Assertions.assertEquals(" name=\"onename\" value=\"val\" placeholder=\"a\"", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "value", null, null);
        Assertions.assertEquals(" name=\"onename\" value placeholder=\"a\"", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "name", "", null);
        Assertions.assertEquals(" name=\"\" value placeholder=\"a\"", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "name", "", AttributeValueQuotes.SINGLE);
        Assertions.assertEquals(" name='' value placeholder=\"a\"", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "name", null, null);
        Assertions.assertEquals(" name value placeholder=\"a\"", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "name", "", null);
        Assertions.assertEquals(" name=\"\" value placeholder=\"a\"", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "name");
        Assertions.assertEquals(" value placeholder=\"a\"", attrs.toString());
        Assertions.assertEquals(2, attrs.attributes.length);
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "name", "", null);
        Assertions.assertEquals("[value, placeholder, name]", attrs.getAttributeMap().keySet().toString());
        Assertions.assertEquals(" value placeholder=\"a\" name=\"\"", attrs.toString());

        attrs = computeHtmlAttributes("<input type=\"text\"   value='hello!!!'    ba= s>");
        attrs = attrs.removeAttribute(TemplateMode.HTML, "type");
        Assertions.assertEquals(" value='hello!!!'    ba= s", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "type", null, null);
        Assertions.assertEquals(" value='hello!!!'    ba= s type", attrs.toString());

        attrs = computeHtmlAttributes("<input type=\"text\"   value='hello!!!'    ba= s>");
        attrs = attrs.removeAttribute(TemplateMode.HTML, "type");
        Assertions.assertEquals(" value='hello!!!'    ba= s", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "title", null, null);
        Assertions.assertEquals(" value='hello!!!'    ba= s title", attrs.toString());

        attrs = computeHtmlAttributes("<input type=\"text\"   value='hello!!!'    ba= s>");
        attrs = attrs.removeAttribute(TemplateMode.HTML, "type");
        Assertions.assertEquals(" value='hello!!!'    ba= s", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "title", "", null);
        Assertions.assertEquals(" value='hello!!!'    ba= s title=\"\"", attrs.toString());

        attrs = computeHtmlAttributes("<input type=\"text\"   value='hello!!!'    ba= s>");
        attrs = attrs.removeAttribute(TemplateMode.HTML, "type");
        Assertions.assertEquals(" value='hello!!!'    ba= s", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "title", "", AttributeValueQuotes.SINGLE);
        Assertions.assertEquals(" value='hello!!!'    ba= s title=''", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "title", "", AttributeValueQuotes.NONE);
        Assertions.assertEquals(" value='hello!!!'    ba= s title=\"\"", attrs.toString());

        attrs = computeHtmlAttributes("<input type=\"text\"   value='hello!!!'    ba= s>");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba= s", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "value", "one", AttributeValueQuotes.NONE);
        Assertions.assertEquals(" type=\"text\"   value=one    ba= s", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "value", "", null);
        Assertions.assertEquals(" type=\"text\"   value=\"\"    ba= s", attrs.toString());

        attrs = computeHtmlAttributes("<input type=\"text\"   value='hello!!!'    ba= s>");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba= s", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "ba", "", null);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba= \"\"", attrs.toString());

        attrs = computeHtmlAttributes("<input type=\"text\"   value='hello!!!'    ba= s>");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba= s", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "ba", "one", null);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba= one", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "ba");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "ba", "one", null);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!' ba=\"one\"", attrs.toString());

        attrs = computeHtmlAttributes("<input type=\"text\"   value='hello!!!'    ba>");
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "ba", "one", AttributeValueQuotes.SINGLE);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba='one'", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "ba");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "ba", "two", null);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!' ba=\"two\"", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "be", "three", null);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!' ba=\"two\" be=\"three\"", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "bi", "four", null);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!' ba=\"two\" be=\"three\" bi=\"four\"", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "bo", "five", null);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!' ba=\"two\" be=\"three\" bi=\"four\" bo=\"five\"", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "bu", "six", null);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!' ba=\"two\" be=\"three\" bi=\"four\" bo=\"five\" bu=\"six\"", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "be");
        attrs = attrs.removeAttribute(TemplateMode.HTML, "bu");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!' ba=\"two\" bi=\"four\" bo=\"five\"", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "bi", null, null);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!' ba=\"two\" bi bo=\"five\"", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "bi");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!' ba=\"two\" bo=\"five\"", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "type");
        Assertions.assertEquals(" value='hello!!!' ba=\"two\" bo=\"five\"", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "type");
        Assertions.assertEquals(" value='hello!!!' ba=\"two\" bo=\"five\"", attrs.toString());

        attrs = computeHtmlAttributes("<input>");
        Assertions.assertEquals("", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "a", "one", null);
        Assertions.assertEquals(" a=\"one\"", attrs.toString());

        attrs = computeHtmlAttributes("<input>");
        Assertions.assertEquals("", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "a", "one", AttributeValueQuotes.NONE);
        Assertions.assertEquals(" a=one", attrs.toString());

        attrs = computeHtmlAttributes("<input   >");
        Assertions.assertEquals("   ", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "a", "one", null);
        Assertions.assertEquals(" a=\"one\"   ", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "b", "two", null);
        Assertions.assertEquals(" a=\"one\" b=\"two\"   ", attrs.toString());

        attrs = computeHtmlAttributes("<input\none  />");
        Assertions.assertEquals("\none  ", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "a", "two", null);
        Assertions.assertEquals("\none a=\"two\"  ", attrs.toString());

        attrs = computeHtmlAttributes("<input\none two/>");
        Assertions.assertEquals("\none two", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.HTML, "one");
        Assertions.assertEquals("\ntwo", attrs.toString());

    }




    @Test
    public void testXmlElementAttributesAttrManagement() {

        Attributes attrs;

        AttributeDefinitions attributeDefinitions = new AttributeDefinitions(Collections.EMPTY_MAP);

        attrs = computeXmlAttributes("<input/>");
        Assertions.assertEquals("", attrs.toString());

        attrs = computeXmlAttributes("<input type=\"text\"/>");
        Assertions.assertEquals(" type=\"text\"", attrs.toString());

        attrs = computeXmlAttributes("<input type=\"text\"   value='hello!!!'/>");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "type");
        Assertions.assertEquals(" value='hello!!!'", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "value");
        Assertions.assertEquals("", attrs.toString());

        attrs = computeXmlAttributes("<input type=\"text\"   value='hello!!!'    />");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, null, "type");
        Assertions.assertEquals(" value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, null, "value");
        Assertions.assertEquals("    ", attrs.toString());

        attrs = computeXmlAttributes("<input th:type=\"text\"   th:value='hello!!!'    />");
        Assertions.assertEquals(" th:type=\"text\"   th:value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "th", "type");
        Assertions.assertEquals(" th:value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "th", "value");
        Assertions.assertEquals("    ", attrs.toString());

        attrs = computeXmlAttributes("<input th:type=\"text\"   th:value='hello!!!'    />");
        Assertions.assertEquals(" th:type=\"text\"   th:value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "TH", "TYPE");
        Assertions.assertEquals(" th:type=\"text\"   th:value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "tH", "Value");
        Assertions.assertEquals(" th:type=\"text\"   th:value='hello!!!'    ", attrs.toString());

        attrs = computeXmlAttributes("<input th:type=\"text\"   th:value='hello!!!'    />");
        Assertions.assertEquals(" th:type=\"text\"   th:value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "data-th-type");
        Assertions.assertEquals(" th:type=\"text\"   th:value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "data-th-value");
        Assertions.assertEquals(" th:type=\"text\"   th:value='hello!!!'    ", attrs.toString());

        attrs = computeXmlAttributes("<input th:type=\"text\"   th:value='hello!!!'    />");
        Assertions.assertEquals(" th:type=\"text\"   th:value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(AttributeNames.forXMLName("th:type"));
        Assertions.assertEquals(" th:value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(AttributeNames.forXMLName("th:value"));
        Assertions.assertEquals("    ", attrs.toString());

        attrs = computeXmlAttributes("<input th:type=\"text\"   th:value='hello!!!'    />");
        Assertions.assertEquals("[th:type, th:value]", attrs.getAttributeMap().keySet().toString());
        Assertions.assertEquals(" th:type=\"text\"   th:value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(AttributeNames.forXMLName("th", "type"));
        Assertions.assertEquals(" th:value='hello!!!'    ", attrs.toString());
        attrs = attrs.removeAttribute(AttributeNames.forXMLName("TH", "VALUE"));
        Assertions.assertEquals(" th:value='hello!!!'    ", attrs.toString());

        attrs = computeXmlAttributes("<input type=\"text\"   value='hello!!!'    ba=''/>");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba=''", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "value");
        Assertions.assertEquals(" type=\"text\"   ba=''", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "ba");
        Assertions.assertEquals(" type=\"text\"", attrs.toString());

        attrs = computeXmlAttributes("<input type=\"text\"   value='hello!!!'    ba=''/>");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba=''", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "value");
        Assertions.assertEquals(" type=\"text\"   ba=''", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "type");
        Assertions.assertEquals(" ba=''", attrs.toString());

        attrs = computeXmlAttributes("<input type=\"text\"   value='hello!!!'    ba='' />");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba='' ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "value");
        Assertions.assertEquals(" type=\"text\"   ba='' ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "ba");
        Assertions.assertEquals(" type=\"text\" ", attrs.toString());

        attrs = computeXmlAttributes("<input type=\"text\"   value='hello!!!'    ba='' />");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba='' ", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "value", "bye! :(", null);
        Assertions.assertEquals(" type=\"text\"   value='bye! :('    ba='' ", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "type", "one", null);
        Assertions.assertEquals(" type=\"one\"   value='bye! :('    ba='' ", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "ba", "two", null);
        Assertions.assertEquals(" type=\"one\"   value='bye! :('    ba='two' ", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "ba", "three", AttributeValueQuotes.SINGLE);
        Assertions.assertEquals(" type=\"one\"   value='bye! :('    ba='three' ", attrs.toString());

        try {
            attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "ba", "four", AttributeValueQuotes.NONE);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "ba", null, AttributeValueQuotes.NONE);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "ba", null, null);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "ba", "five", null);
        Assertions.assertEquals(" type=\"one\"   value='bye! :('    ba='five' ", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "ba", "six", null);
        Assertions.assertEquals(" type=\"one\"   value='bye! :('    ba='six' ", attrs.toString());

        attrs = computeXmlAttributes("<input type=\"text\"   value='hello!!!'    ba='twenty' />");
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "ba", "thirty", null);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba='thirty' ", attrs.toString());

        attrs = computeXmlAttributes("<input type=\"text\"value='hello!!!' />");
        Assertions.assertEquals(" type=\"text\"value='hello!!!' ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "type");
        Assertions.assertEquals(" value='hello!!!' ", attrs.toString());

        attrs = computeXmlAttributes("<input type=\"text\"value='hello!!!' name='one' />");
        Assertions.assertEquals(" type=\"text\"value='hello!!!' name='one' ", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "type");
        Assertions.assertEquals(" value='hello!!!' name='one' ", attrs.toString());

        attrs = computeXmlAttributes("<input type=\"text\"value='hello!!!' name='one'/>");
        Assertions.assertEquals(" type=\"text\"value='hello!!!' name='one'", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "name");
        Assertions.assertEquals(" type=\"text\"value='hello!!!'", attrs.toString());

        attrs = computeXmlAttributes("<input type=\"text\"   value='hello!!!'    ba= 's'/>");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba= 's'", attrs.toString());

        attrs = computeXmlAttributes("<input type=\"text\"   value='hello!!!'    ba= 's'/>");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba= 's'", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "value");
        Assertions.assertEquals(" type=\"text\"   ba= 's'", attrs.toString());

        attrs = computeXmlAttributes("<input type=\"text\"   value='hello!!!'    ba= 's'/>");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba= 's'", attrs.toString());
        attrs = new Attributes(null, null);
        Assertions.assertEquals("", attrs.toString());
        attrs = new Attributes(null, null);
        Assertions.assertEquals("", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "name", "onename", null);
        Assertions.assertEquals(" name=\"onename\"", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "value", "val", null);
        Assertions.assertEquals(" name=\"onename\" value=\"val\"", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "placeholder", "a", null);
        Assertions.assertEquals(" name=\"onename\" value=\"val\" placeholder=\"a\"", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "name", "", null);
        Assertions.assertEquals(" name=\"\" value=\"val\" placeholder=\"a\"", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "name", "", AttributeValueQuotes.SINGLE);
        Assertions.assertEquals(" name='' value=\"val\" placeholder=\"a\"", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "name", "", null);
        Assertions.assertEquals(" name='' value=\"val\" placeholder=\"a\"", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "name");
        Assertions.assertEquals(" value=\"val\" placeholder=\"a\"", attrs.toString());
        Assertions.assertEquals(2, attrs.attributes.length);
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "name", "", null);
        Assertions.assertEquals(" value=\"val\" placeholder=\"a\" name=\"\"", attrs.toString());

        attrs = computeXmlAttributes("<input type=\"text\"   value='hello!!!'    ba= 's'/>");
        attrs = attrs.removeAttribute(TemplateMode.XML, "type");
        Assertions.assertEquals(" value='hello!!!'    ba= 's'", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "type", "", null);
        Assertions.assertEquals(" value='hello!!!'    ba= 's' type=\"\"", attrs.toString());

        attrs = computeXmlAttributes("<input type=\"text\"   value='hello!!!'    ba= 's'/>");
        attrs = attrs.removeAttribute(TemplateMode.XML, "type");
        Assertions.assertEquals(" value='hello!!!'    ba= 's'", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "title", " ", null);
        Assertions.assertEquals(" value='hello!!!'    ba= 's' title=\" \"", attrs.toString());

        attrs = computeXmlAttributes("<input type=\"text\"   value='hello!!!'    ba= 's'/>");
        attrs = attrs.removeAttribute(TemplateMode.XML, "type");
        Assertions.assertEquals(" value='hello!!!'    ba= 's'", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "title", "", null);
        Assertions.assertEquals(" value='hello!!!'    ba= 's' title=\"\"", attrs.toString());

        attrs = computeXmlAttributes("<input type=\"text\"   value='hello!!!'    ba= 's'/>");
        attrs = attrs.removeAttribute(TemplateMode.XML, "type");
        Assertions.assertEquals(" value='hello!!!'    ba= 's'", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "title", "", AttributeValueQuotes.SINGLE);
        Assertions.assertEquals(" value='hello!!!'    ba= 's' title=''", attrs.toString());
        try {
            // Shouldn't be able to set an empty-string value with no quotes
            attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "title", "", AttributeValueQuotes.NONE);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        attrs = computeXmlAttributes("<input type=\"text\"   value='hello!!!'    ba= 's'/>");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba= 's'", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "value", "one", AttributeValueQuotes.DOUBLE);
        Assertions.assertEquals(" type=\"text\"   value=\"one\"    ba= 's'", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "value", "", null);
        Assertions.assertEquals(" type=\"text\"   value=\"\"    ba= 's'", attrs.toString());

        attrs = computeXmlAttributes("<input type=\"text\"   value='hello!!!'    ba= 's'/>");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba= 's'", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "ba", "", null);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba= ''", attrs.toString());

        attrs = computeXmlAttributes("<input type=\"text\"   value='hello!!!'    ba= 's'/>");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba= 's'", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "ba", "one", null);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba= 'one'", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, null, "ba");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "ba", "one", null);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!' ba=\"one\"", attrs.toString());

        attrs = computeXmlAttributes("<input type=\"text\"   value='hello!!!'    ba=\"\"/>");
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "ba", "one", AttributeValueQuotes.SINGLE);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'    ba='one'", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "ba");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!'", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "ba", "two", null);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!' ba=\"two\"", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "be", "three", null);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!' ba=\"two\" be=\"three\"", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "bi", "four", null);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!' ba=\"two\" be=\"three\" bi=\"four\"", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "bo", "five", null);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!' ba=\"two\" be=\"three\" bi=\"four\" bo=\"five\"", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "bu", "six", null);
        Assertions.assertEquals(" type=\"text\"   value='hello!!!' ba=\"two\" be=\"three\" bi=\"four\" bo=\"five\" bu=\"six\"", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "be");
        attrs = attrs.removeAttribute(TemplateMode.XML, "bu");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!' ba=\"two\" bi=\"four\" bo=\"five\"", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "bi");
        Assertions.assertEquals(" type=\"text\"   value='hello!!!' ba=\"two\" bo=\"five\"", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "type");
        Assertions.assertEquals(" value='hello!!!' ba=\"two\" bo=\"five\"", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "type");
        Assertions.assertEquals(" value='hello!!!' ba=\"two\" bo=\"five\"", attrs.toString());

        attrs = computeXmlAttributes("<input/>");
        Assertions.assertEquals("", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "a", "one", null);
        Assertions.assertEquals(" a=\"one\"", attrs.toString());

        attrs = computeXmlAttributes("<input/>");
        Assertions.assertEquals("", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "a", "one", AttributeValueQuotes.SINGLE);
        Assertions.assertEquals(" a='one'", attrs.toString());

        attrs = computeXmlAttributes("<input   />");
        Assertions.assertEquals("   ", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "a", "one", null);
        Assertions.assertEquals(" a=\"one\"   ", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "b", "two", null);
        Assertions.assertEquals(" a=\"one\" b=\"two\"   ", attrs.toString());

        attrs = computeXmlAttributes("<input\none=\"\"  />");
        Assertions.assertEquals("\none=\"\"  ", attrs.toString());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "a", "two", null);
        Assertions.assertEquals("\none=\"\" a=\"two\"  ", attrs.toString());

        attrs = computeXmlAttributes("<input\none=\"\" two=\"\"/>");
        Assertions.assertEquals("\none=\"\" two=\"\"", attrs.toString());
        attrs = attrs.removeAttribute(TemplateMode.XML, "one");
        Assertions.assertEquals("\ntwo=\"\"", attrs.toString());

    }







    @Test
    public void testHtmlElementAttributesAttrObtention() {

        Attributes attrs;
        final AttributeDefinitions attributeDefinitions = TEMPLATE_ENGINE_CONFIGURATION.getAttributeDefinitions();

        attrs = computeHtmlAttributes("<input>");
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, "type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, null, "type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, "", "type"));
        Assertions.assertFalse(attrs.hasAttribute(AttributeNames.forHTMLName("type")));
        Assertions.assertNull(attrs.getAttribute(TemplateMode.HTML, "type"));
        Assertions.assertNull(attrs.getAttribute(TemplateMode.HTML, null, "type"));
        Assertions.assertNull(attrs.getAttribute(TemplateMode.HTML, "", "type"));
        Assertions.assertNull(attrs.getAttribute(AttributeNames.forHTMLName("type")));
        Assertions.assertNull(attrs.attributes);

        attrs = computeHtmlAttributes("<input type=\"text\">");
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, null, "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "", "type"));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forHTMLName("type")));
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.HTML, "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.HTML, null, "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.HTML, "", "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(AttributeNames.forHTMLName("type")).getValue());
        Assertions.assertSame(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(TemplateMode.HTML, "type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(TemplateMode.HTML, null, "type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(TemplateMode.HTML, "", "type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(AttributeNames.forHTMLName("type")).definition);
        Assertions.assertEquals(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(TemplateMode.HTML, "type").definition);
        Assertions.assertEquals(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(TemplateMode.HTML, null, "type").definition);
        Assertions.assertEquals(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(TemplateMode.HTML, "", "type").definition);
        Assertions.assertEquals(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(AttributeNames.forHTMLName("type")).definition);
        Assertions.assertEquals(1, attrs.attributes.length);

        attrs = computeHtmlAttributes("<input type='text'>");
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, null, "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "", "type"));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forHTMLName("type")));
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.HTML, "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.HTML, null, "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.HTML, "", "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(AttributeNames.forHTMLName("type")).getValue());
        Assertions.assertSame(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(TemplateMode.HTML, "type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(TemplateMode.HTML, null, "type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(TemplateMode.HTML, "", "type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(AttributeNames.forHTMLName("type")).definition);
        Assertions.assertEquals(1, attrs.attributes.length);

        attrs = computeHtmlAttributes("<input type=text>");
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, null, "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "", "type"));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forHTMLName("type")));
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.HTML, "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.HTML, null, "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.HTML, "", "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(AttributeNames.forHTMLName("type")).getValue());
        Assertions.assertSame(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(TemplateMode.HTML, "type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(TemplateMode.HTML, null, "type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(TemplateMode.HTML, "", "type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(AttributeNames.forHTMLName("type")).definition);
        Assertions.assertEquals(1, attrs.attributes.length);

        attrs = computeHtmlAttributes("<input type=\"text\" th:type=\"${thetype}\">");
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, null, "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "", "type"));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forHTMLName("type")));
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.HTML, "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.HTML, null, "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.HTML, "", "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(AttributeNames.forHTMLName("type")).getValue());
        Assertions.assertSame(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(TemplateMode.HTML, "type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(TemplateMode.HTML, null, "type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(TemplateMode.HTML, "", "type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(AttributeNames.forHTMLName("type")).definition);
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "th:type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "th", "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "TH", "Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, null, "th:type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, null, "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "", "th:type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "", "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forHTMLName("th:type")));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forHTMLName("TH:TYPE")));
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, "th:type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, "TH:Type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, "th", "type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, "TH", "Type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, null, "th:type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, null, "TH:Type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, "", "th:type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, "", "TH:Type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(AttributeNames.forHTMLName("th:type")).getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(AttributeNames.forHTMLName("TH:Type")).getValue());
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, "th:type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, "TH:Type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, "th", "type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, "TH", "Type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, null, "th:type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, null, "TH:Type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, "", "th:type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, "", "TH:Type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(AttributeNames.forHTMLName("th:type")).definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(AttributeNames.forHTMLName("TH:Type")).definition);
        Assertions.assertEquals(2, attrs.attributes.length);

        attrs = computeHtmlAttributes("<input type=\"text\" th:type=\"${thetype}\" sec:one=auth>");
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, null, "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "", "type"));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forHTMLName("type")));
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.HTML, "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.HTML, null, "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.HTML, "", "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(AttributeNames.forHTMLName("type")).getValue());
        Assertions.assertSame(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(TemplateMode.HTML, "type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(TemplateMode.HTML, null, "type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(TemplateMode.HTML, "", "type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("type"), attrs.getAttribute(AttributeNames.forHTMLName("type")).definition);
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "th:type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "th", "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "TH", "Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, null, "th:type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, null, "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "", "th:type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "", "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forHTMLName("th:type")));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forHTMLName("TH:TYPE")));
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, "th:type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, "TH:Type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, "th", "type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, "TH", "Type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, null, "th:type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, null, "TH:Type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, "", "th:type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, "", "TH:Type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(AttributeNames.forHTMLName("th:type")).getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(AttributeNames.forHTMLName("TH:Type")).getValue());
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, "th:type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, "TH:Type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, "th", "type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, "TH", "Type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, null, "th:type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, null, "TH:Type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, "", "th:type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, "", "TH:Type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(AttributeNames.forHTMLName("th:type")).definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(AttributeNames.forHTMLName("TH:Type")).definition);
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "th:type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "th", "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "TH", "Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, null, "th:type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, null, "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "", "th:type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "", "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forHTMLName("th:type")));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forHTMLName("TH:TYPE")));
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.HTML, "sec:one").getValue());
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.HTML, "SEC:One").getValue());
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.HTML, "sec", "one").getValue());
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.HTML, "SEC", "One").getValue());
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.HTML, null, "sec:one").getValue());
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.HTML, null, "SEC:One").getValue());
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.HTML, "", "sec:one").getValue());
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.HTML, "", "SEC:One").getValue());
        Assertions.assertEquals("auth", attrs.getAttribute(AttributeNames.forHTMLName("sec:one")).getValue());
        Assertions.assertEquals("auth", attrs.getAttribute(AttributeNames.forHTMLName("SEC:One")).getValue());
        Assertions.assertSame(attributeDefinitions.forHTMLName("sec:one"), attrs.getAttribute(TemplateMode.HTML, "sec:one").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("sec:one"), attrs.getAttribute(TemplateMode.HTML, "SEC:One").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("sec:one"), attrs.getAttribute(TemplateMode.HTML, "sec", "one").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("sec:one"), attrs.getAttribute(TemplateMode.HTML, "SEC", "One").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("sec:one"), attrs.getAttribute(TemplateMode.HTML, null, "sec:one").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("sec:one"), attrs.getAttribute(TemplateMode.HTML, null, "SEC:One").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("sec:one"), attrs.getAttribute(TemplateMode.HTML, "", "sec:one").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("sec:one"), attrs.getAttribute(TemplateMode.HTML, "", "SEC:One").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("sec:one"), attrs.getAttribute(AttributeNames.forHTMLName("sec:one")).definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("sec:one"), attrs.getAttribute(AttributeNames.forHTMLName("SEC:One")).definition);
        Assertions.assertEquals(3, attrs.attributes.length);

        attrs = computeHtmlAttributes("<input type=\"text\" th:type=\"${thetype}\" sec:one=auth>");
        attrs = attrs.removeAttribute(TemplateMode.HTML, "type");
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, "type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, null, "type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, "", "type"));
        Assertions.assertFalse(attrs.hasAttribute(AttributeNames.forHTMLName("type")));
        Assertions.assertNull(attrs.getAttribute(TemplateMode.HTML, "type"));
        Assertions.assertNull(attrs.getAttribute(TemplateMode.HTML, null, "type"));
        Assertions.assertNull(attrs.getAttribute(TemplateMode.HTML, "", "type"));
        Assertions.assertNull(attrs.getAttribute(AttributeNames.forHTMLName("type")));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "th:type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "th", "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "TH", "Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, null, "th:type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, null, "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "", "th:type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "", "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forHTMLName("th:type")));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forHTMLName("TH:TYPE")));
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, "th:type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, "TH:Type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, "th", "type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, "TH", "Type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, null, "th:type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, null, "TH:Type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, "", "th:type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.HTML, "", "TH:Type").getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(AttributeNames.forHTMLName("th:type")).getValue());
        Assertions.assertEquals("${thetype}", attrs.getAttribute(AttributeNames.forHTMLName("TH:Type")).getValue());
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, "th:type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, "TH:Type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, "th", "type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, "TH", "Type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, null, "th:type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, null, "TH:Type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, "", "th:type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(TemplateMode.HTML, "", "TH:Type").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(AttributeNames.forHTMLName("th:type")).definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("th:type"), attrs.getAttribute(AttributeNames.forHTMLName("TH:Type")).definition);
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "th:type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "th", "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "TH", "Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, null, "th:type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, null, "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "", "th:type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.HTML, "", "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forHTMLName("th:type")));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forHTMLName("TH:TYPE")));
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.HTML, "sec:one").getValue());
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.HTML, "SEC:One").getValue());
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.HTML, "sec", "one").getValue());
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.HTML, "SEC", "One").getValue());
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.HTML, null, "sec:one").getValue());
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.HTML, null, "SEC:One").getValue());
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.HTML, "", "sec:one").getValue());
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.HTML, "", "SEC:One").getValue());
        Assertions.assertEquals("auth", attrs.getAttribute(AttributeNames.forHTMLName("sec:one")).getValue());
        Assertions.assertEquals("auth", attrs.getAttribute(AttributeNames.forHTMLName("SEC:One")).getValue());
        Assertions.assertSame(attributeDefinitions.forHTMLName("sec:one"), attrs.getAttribute(TemplateMode.HTML, "sec:one").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("sec:one"), attrs.getAttribute(TemplateMode.HTML, "SEC:One").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("sec:one"), attrs.getAttribute(TemplateMode.HTML, "sec", "one").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("sec:one"), attrs.getAttribute(TemplateMode.HTML, "SEC", "One").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("sec:one"), attrs.getAttribute(TemplateMode.HTML, null, "sec:one").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("sec:one"), attrs.getAttribute(TemplateMode.HTML, null, "SEC:One").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("sec:one"), attrs.getAttribute(TemplateMode.HTML, "", "sec:one").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("sec:one"), attrs.getAttribute(TemplateMode.HTML, "", "SEC:One").definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("sec:one"), attrs.getAttribute(AttributeNames.forHTMLName("sec:one")).definition);
        Assertions.assertSame(attributeDefinitions.forHTMLName("sec:one"), attrs.getAttribute(AttributeNames.forHTMLName("SEC:One")).definition);
        Assertions.assertEquals(2, attrs.attributes.length);


        attrs = new Attributes(null, null);
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, "type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, null, "type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, "", "type"));
        Assertions.assertFalse(attrs.hasAttribute(AttributeNames.forHTMLName("type")));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, "TH:Type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, "th", "type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, "TH", "Type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, null, "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, null, "TH:Type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, "", "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, "", "TH:Type"));
        Assertions.assertFalse(attrs.hasAttribute(AttributeNames.forHTMLName("th:type")));
        Assertions.assertFalse(attrs.hasAttribute(AttributeNames.forHTMLName("TH:TYPE")));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, "TH:Type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, "th", "type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, "TH", "Type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, null, "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, null, "TH:Type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, "", "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.HTML, "", "TH:Type"));
        Assertions.assertFalse(attrs.hasAttribute(AttributeNames.forHTMLName("th:type")));
        Assertions.assertFalse(attrs.hasAttribute(AttributeNames.forHTMLName("TH:TYPE")));
        Assertions.assertNull(attrs.attributes);

        attrs = computeHtmlAttributes("<input type=text th:type=\"${thetype}\">");
        Assertions.assertEquals(AttributeValueQuotes.NONE, attrs.getAttribute(TemplateMode.HTML, "type").valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.NONE, attrs.getAttribute(TemplateMode.HTML, "", "type").valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.NONE, attrs.getAttribute(AttributeNames.forHTMLName("", "type")).valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.NONE, attrs.getAttribute(AttributeNames.forHTMLName("type")).valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.DOUBLE, attrs.getAttribute(TemplateMode.HTML, "th:type").valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.DOUBLE, attrs.getAttribute(TemplateMode.HTML, "", "th:type").valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.DOUBLE, attrs.getAttribute(TemplateMode.HTML, "th", "type").valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.DOUBLE, attrs.getAttribute(TemplateMode.HTML, "", "TH:Type").valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.DOUBLE, attrs.getAttribute(TemplateMode.HTML, "TH", "Type").valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.DOUBLE, attrs.getAttribute(AttributeNames.forHTMLName("", "th:type")).valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.DOUBLE, attrs.getAttribute(AttributeNames.forHTMLName("th:type")).valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.DOUBLE, attrs.getAttribute(AttributeNames.forHTMLName("th", "type")).valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.DOUBLE, attrs.getAttribute(AttributeNames.forHTMLName("TH:Type")).valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.DOUBLE, attrs.getAttribute(AttributeNames.forHTMLName("TH", "Type")).valueQuotes);

        attrs = computeHtmlAttributes("<input type='text' \nth:type=\"${thetype}\">");
        Assertions.assertEquals(1, attrs.getAttribute(TemplateMode.HTML, "type").line);
        Assertions.assertEquals(1, attrs.getAttribute(TemplateMode.HTML, "", "type").line);
        Assertions.assertEquals(1, attrs.getAttribute(AttributeNames.forHTMLName("", "type")).line);
        Assertions.assertEquals(1, attrs.getAttribute(AttributeNames.forHTMLName("type")).line);
        Assertions.assertEquals(8, attrs.getAttribute(TemplateMode.HTML, "type").col);
        Assertions.assertEquals(8, attrs.getAttribute(TemplateMode.HTML, "", "type").col);
        Assertions.assertEquals(8, attrs.getAttribute(AttributeNames.forHTMLName("", "type")).col);
        Assertions.assertEquals(8, attrs.getAttribute(AttributeNames.forHTMLName("type")).col);
        Assertions.assertEquals(2, attrs.getAttribute(TemplateMode.HTML, "th:type").line);
        Assertions.assertEquals(2, attrs.getAttribute(TemplateMode.HTML, "th", "type").line);
        Assertions.assertEquals(2, attrs.getAttribute(TemplateMode.HTML, "", "th:type").line);
        Assertions.assertEquals(2, attrs.getAttribute(TemplateMode.HTML, "TH", "Type").line);
        Assertions.assertEquals(2, attrs.getAttribute(TemplateMode.HTML, "", "TH:Type").line);
        Assertions.assertEquals(2, attrs.getAttribute(AttributeNames.forHTMLName("", "th:type")).line);
        Assertions.assertEquals(2, attrs.getAttribute(AttributeNames.forHTMLName("th", "type")).line);
        Assertions.assertEquals(2, attrs.getAttribute(AttributeNames.forHTMLName("", "TH:Type")).line);
        Assertions.assertEquals(2, attrs.getAttribute(AttributeNames.forHTMLName("TH", "Type")).line);
        Assertions.assertEquals(1, attrs.getAttribute(TemplateMode.HTML, "th:type").col);
        Assertions.assertEquals(1, attrs.getAttribute(TemplateMode.HTML, "", "th:type").col);
        Assertions.assertEquals(1, attrs.getAttribute(TemplateMode.HTML, "th", "type").col);
        Assertions.assertEquals(1, attrs.getAttribute(TemplateMode.HTML, "TH", "Type").col);
        Assertions.assertEquals(1, attrs.getAttribute(AttributeNames.forHTMLName("", "th:type")).col);
        Assertions.assertEquals(1, attrs.getAttribute(AttributeNames.forHTMLName("th:type")).col);
        Assertions.assertEquals(1, attrs.getAttribute(AttributeNames.forHTMLName("th", "type")).col);
        Assertions.assertEquals(1, attrs.getAttribute(AttributeNames.forHTMLName("TH:Type")).col);
        Assertions.assertEquals(1, attrs.getAttribute(AttributeNames.forHTMLName("TH", "Type")).col);

        attrs = computeHtmlAttributes("<input type='text' \na=\"b\" th:type=\"${thetype}\">");
        attrs = attrs.removeAttribute(TemplateMode.HTML, "a");
        Assertions.assertEquals(1, attrs.getAttribute(TemplateMode.HTML, "type").line);
        Assertions.assertEquals(1, attrs.getAttribute(TemplateMode.HTML, "", "type").line);
        Assertions.assertEquals(1, attrs.getAttribute(AttributeNames.forHTMLName("", "type")).line);
        Assertions.assertEquals(1, attrs.getAttribute(AttributeNames.forHTMLName("type")).line);
        Assertions.assertEquals(8, attrs.getAttribute(TemplateMode.HTML, "type").col);
        Assertions.assertEquals(8, attrs.getAttribute(TemplateMode.HTML, "", "type").col);
        Assertions.assertEquals(8, attrs.getAttribute(AttributeNames.forHTMLName("", "type")).col);
        Assertions.assertEquals(8, attrs.getAttribute(AttributeNames.forHTMLName("type")).col);
        Assertions.assertEquals(2, attrs.getAttribute(TemplateMode.HTML, "th:type").line);
        Assertions.assertEquals(2, attrs.getAttribute(TemplateMode.HTML, "th", "type").line);
        Assertions.assertEquals(2, attrs.getAttribute(TemplateMode.HTML, "", "th:type").line);
        Assertions.assertEquals(2, attrs.getAttribute(TemplateMode.HTML, "TH", "Type").line);
        Assertions.assertEquals(2, attrs.getAttribute(TemplateMode.HTML, "", "TH:Type").line);
        Assertions.assertEquals(2, attrs.getAttribute(AttributeNames.forHTMLName("", "th:type")).line);
        Assertions.assertEquals(2, attrs.getAttribute(AttributeNames.forHTMLName("th", "type")).line);
        Assertions.assertEquals(2, attrs.getAttribute(AttributeNames.forHTMLName("", "TH:Type")).line);
        Assertions.assertEquals(2, attrs.getAttribute(AttributeNames.forHTMLName("TH", "Type")).line);
        Assertions.assertEquals(7, attrs.getAttribute(TemplateMode.HTML, "th:type").col);
        Assertions.assertEquals(7, attrs.getAttribute(TemplateMode.HTML, "", "th:type").col);
        Assertions.assertEquals(7, attrs.getAttribute(TemplateMode.HTML, "th", "type").col);
        Assertions.assertEquals(7, attrs.getAttribute(TemplateMode.HTML, "TH", "Type").col);
        Assertions.assertEquals(7, attrs.getAttribute(AttributeNames.forHTMLName("", "th:type")).col);
        Assertions.assertEquals(7, attrs.getAttribute(AttributeNames.forHTMLName("th:type")).col);
        Assertions.assertEquals(7, attrs.getAttribute(AttributeNames.forHTMLName("th", "type")).col);
        Assertions.assertEquals(7, attrs.getAttribute(AttributeNames.forHTMLName("TH:Type")).col);
        Assertions.assertEquals(7, attrs.getAttribute(AttributeNames.forHTMLName("TH", "Type")).col);

        Assertions.assertTrue(attrs.getAttribute(TemplateMode.HTML, "th:type").hasLocation());
        Assertions.assertTrue(attrs.getAttribute(TemplateMode.HTML, "", "th:type").hasLocation());
        Assertions.assertTrue(attrs.getAttribute(TemplateMode.HTML, "th", "type").hasLocation());
        Assertions.assertTrue(attrs.getAttribute(TemplateMode.HTML, "TH", "Type").hasLocation());
        Assertions.assertTrue(attrs.getAttribute(AttributeNames.forHTMLName("", "th:type")).hasLocation());
        Assertions.assertTrue(attrs.getAttribute(AttributeNames.forHTMLName("th:type")).hasLocation());
        Assertions.assertTrue(attrs.getAttribute(AttributeNames.forHTMLName("th", "type")).hasLocation());
        Assertions.assertTrue(attrs.getAttribute(AttributeNames.forHTMLName("TH:Type")).hasLocation());
        Assertions.assertTrue(attrs.getAttribute(AttributeNames.forHTMLName("TH", "Type")).hasLocation());
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.HTML, null, "one", "two", null);
        Assertions.assertFalse(attrs.getAttribute(TemplateMode.HTML, "one").hasLocation());
        Assertions.assertFalse(attrs.getAttribute(TemplateMode.HTML, "", "one").hasLocation());
        Assertions.assertFalse(attrs.getAttribute(AttributeNames.forHTMLName("", "one")).hasLocation());
        Assertions.assertFalse(attrs.getAttribute(AttributeNames.forHTMLName("one")).hasLocation());

    }








    @Test
    public void testXmlElementAttributesAttrObtention() {

        Attributes attrs;
        final AttributeDefinitions attributeDefinitions = TEMPLATE_ENGINE_CONFIGURATION.getAttributeDefinitions();

        attrs = computeXmlAttributes("<input/>");
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, null, "type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "", "type"));
        Assertions.assertFalse(attrs.hasAttribute(AttributeNames.forXMLName("type")));
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "type"));
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, null, "type"));
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "", "type"));
        Assertions.assertNull(attrs.getAttribute(AttributeNames.forXMLName("type")));
        Assertions.assertNull(attrs.attributes);

        attrs = computeXmlAttributes("<input type=\"text\"/>");
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, null, "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "", "type"));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forXMLName("type")));
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.XML, "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.XML, null, "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.XML, "", "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(AttributeNames.forXMLName("type")).getValue());
        Assertions.assertSame(attributeDefinitions.forXMLName("type"), attrs.getAttribute(TemplateMode.XML, "type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("type"), attrs.getAttribute(TemplateMode.XML, null, "type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("type"), attrs.getAttribute(TemplateMode.XML, "", "type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("type"), attrs.getAttribute(AttributeNames.forXMLName("type")).definition);
        Assertions.assertEquals(attributeDefinitions.forXMLName("type"), attrs.getAttribute(TemplateMode.XML, "type").definition);
        Assertions.assertEquals(attributeDefinitions.forXMLName("type"), attrs.getAttribute(TemplateMode.XML, null, "type").definition);
        Assertions.assertEquals(attributeDefinitions.forXMLName("type"), attrs.getAttribute(TemplateMode.XML, "", "type").definition);
        Assertions.assertEquals(attributeDefinitions.forXMLName("type"), attrs.getAttribute(AttributeNames.forXMLName("type")).definition);
        Assertions.assertEquals(1, attrs.attributes.length);

        attrs = computeXmlAttributes("<input type='text'/>");
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, null, "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "", "type"));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forXMLName("type")));
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.XML, "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.XML, null, "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.XML, "", "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(AttributeNames.forXMLName("type")).getValue());
        Assertions.assertSame(attributeDefinitions.forXMLName("type"), attrs.getAttribute(TemplateMode.XML, "type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("type"), attrs.getAttribute(TemplateMode.XML, null, "type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("type"), attrs.getAttribute(TemplateMode.XML, "", "type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("type"), attrs.getAttribute(AttributeNames.forXMLName("type")).definition);
        Assertions.assertEquals(1, attrs.attributes.length);

        attrs = computeXmlAttributes("<input type=\"text\" th:type=\"${thetype}\"/>");
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, null, "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "", "type"));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forXMLName("type")));
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.XML, "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.XML, null, "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.XML, "", "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(AttributeNames.forXMLName("type")).getValue());
        Assertions.assertSame(attributeDefinitions.forXMLName("type"), attrs.getAttribute(TemplateMode.XML, "type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("type"), attrs.getAttribute(TemplateMode.XML, null, "type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("type"), attrs.getAttribute(TemplateMode.XML, "", "type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("type"), attrs.getAttribute(AttributeNames.forXMLName("type")).definition);
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "th", "type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "TH", "Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, null, "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, null, "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "", "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "", "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forXMLName("th:type")));
        Assertions.assertFalse(attrs.hasAttribute(AttributeNames.forXMLName("TH:TYPE")));
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.XML, "th:type").getValue());
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "TH:Type"));
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.XML, "th", "type").getValue());
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "TH", "Type"));
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.XML, null, "th:type").getValue());
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, null, "TH:Type"));
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.XML, "", "th:type").getValue());
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "", "TH:Type"));
        Assertions.assertEquals("${thetype}", attrs.getAttribute(AttributeNames.forXMLName("th:type")).getValue());
        Assertions.assertNull(attrs.getAttribute(AttributeNames.forXMLName("TH:Type")));
        Assertions.assertSame(attributeDefinitions.forXMLName("th:type"), attrs.getAttribute(TemplateMode.XML, "th:type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("th:type"), attrs.getAttribute(TemplateMode.XML, "th", "type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("th:type"), attrs.getAttribute(TemplateMode.XML, null, "th:type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("th:type"), attrs.getAttribute(TemplateMode.XML, "", "th:type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("th:type"), attrs.getAttribute(AttributeNames.forXMLName("th:type")).definition);
        Assertions.assertEquals(2, attrs.attributes.length);

        attrs = computeXmlAttributes("<input type=\"text\" th:type=\"${thetype}\" sec:one='auth'/>");
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, null, "type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "", "type"));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forXMLName("type")));
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.XML, "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.XML, null, "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(TemplateMode.XML, "", "type").getValue());
        Assertions.assertEquals("text", attrs.getAttribute(AttributeNames.forXMLName("type")).getValue());
        Assertions.assertSame(attributeDefinitions.forXMLName("type"), attrs.getAttribute(TemplateMode.XML, "type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("type"), attrs.getAttribute(TemplateMode.XML, null, "type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("type"), attrs.getAttribute(TemplateMode.XML, "", "type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("type"), attrs.getAttribute(AttributeNames.forXMLName("type")).definition);
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "th", "type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "TH", "Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, null, "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, null, "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "", "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "", "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forXMLName("th:type")));
        Assertions.assertFalse(attrs.hasAttribute(AttributeNames.forXMLName("TH:TYPE")));
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.XML, "th:type").getValue());
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "TH:Type"));
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.XML, "th", "type").getValue());
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "TH", "Type"));
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.XML, null, "th:type").getValue());
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, null, "TH:Type"));
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.XML, "", "th:type").getValue());
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "", "TH:Type"));
        Assertions.assertEquals("${thetype}", attrs.getAttribute(AttributeNames.forXMLName("th:type")).getValue());
        Assertions.assertNull(attrs.getAttribute(AttributeNames.forXMLName("TH:Type")));
        Assertions.assertSame(attributeDefinitions.forXMLName("th:type"), attrs.getAttribute(TemplateMode.XML, "th:type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("th:type"), attrs.getAttribute(TemplateMode.XML, "th", "type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("th:type"), attrs.getAttribute(TemplateMode.XML, null, "th:type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("th:type"), attrs.getAttribute(TemplateMode.XML, "", "th:type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("th:type"), attrs.getAttribute(AttributeNames.forXMLName("th:type")).definition);
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "th", "type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "TH", "Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, null, "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, null, "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "", "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "", "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forXMLName("th:type")));
        Assertions.assertFalse(attrs.hasAttribute(AttributeNames.forXMLName("TH:TYPE")));
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.XML, "sec:one").getValue());
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "SEC:One"));
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.XML, "sec", "one").getValue());
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "SEC", "One"));
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.XML, null, "sec:one").getValue());
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, null, "SEC:One"));
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.XML, "", "sec:one").getValue());
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "", "SEC:One"));
        Assertions.assertEquals("auth", attrs.getAttribute(AttributeNames.forXMLName("sec:one")).getValue());
        Assertions.assertNull(attrs.getAttribute(AttributeNames.forXMLName("SEC:One")));
        Assertions.assertSame(attributeDefinitions.forXMLName("sec:one"), attrs.getAttribute(TemplateMode.XML, "sec:one").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("sec:one"), attrs.getAttribute(TemplateMode.XML, "sec", "one").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("sec:one"), attrs.getAttribute(TemplateMode.XML, null, "sec:one").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("sec:one"), attrs.getAttribute(TemplateMode.XML, "", "sec:one").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("sec:one"), attrs.getAttribute(AttributeNames.forXMLName("sec:one")).definition);
        Assertions.assertEquals(3, attrs.attributes.length);

        attrs = computeXmlAttributes("<input type=\"text\" th:type=\"${thetype}\" sec:one='auth'/>");
        attrs = attrs.removeAttribute(TemplateMode.XML, "type");
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, null, "type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "", "type"));
        Assertions.assertFalse(attrs.hasAttribute(AttributeNames.forXMLName("type")));
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "type"));
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, null, "type"));
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "", "type"));
        Assertions.assertNull(attrs.getAttribute(AttributeNames.forXMLName("type")));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "th", "type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "TH", "Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, null, "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, null, "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "", "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "", "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forXMLName("th:type")));
        Assertions.assertFalse(attrs.hasAttribute(AttributeNames.forXMLName("TH:TYPE")));
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.XML, "th:type").getValue());
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "TH:Type"));
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.XML, "th", "type").getValue());
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "TH", "Type"));
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.XML, null, "th:type").getValue());
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, null, "TH:Type"));
        Assertions.assertEquals("${thetype}", attrs.getAttribute(TemplateMode.XML, "", "th:type").getValue());
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "", "TH:Type"));
        Assertions.assertEquals("${thetype}", attrs.getAttribute(AttributeNames.forXMLName("th:type")).getValue());
        Assertions.assertNull(attrs.getAttribute(AttributeNames.forXMLName("TH:Type")));
        Assertions.assertSame(attributeDefinitions.forXMLName("th:type"), attrs.getAttribute(TemplateMode.XML, "th:type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("th:type"), attrs.getAttribute(TemplateMode.XML, "th", "type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("th:type"), attrs.getAttribute(TemplateMode.XML, null, "th:type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("th:type"), attrs.getAttribute(TemplateMode.XML, "", "th:type").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("th:type"), attrs.getAttribute(AttributeNames.forXMLName("th:type")).definition);
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "th", "type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "TH", "Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, null, "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, null, "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(TemplateMode.XML, "", "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "", "TH:Type"));
        Assertions.assertTrue(attrs.hasAttribute(AttributeNames.forXMLName("th:type")));
        Assertions.assertFalse(attrs.hasAttribute(AttributeNames.forXMLName("TH:TYPE")));
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.XML, "sec:one").getValue());
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "SEC:One"));
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.XML, "sec", "one").getValue());
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "SEC", "One"));
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.XML, null, "sec:one").getValue());
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, null, "SEC:One"));
        Assertions.assertEquals("auth", attrs.getAttribute(TemplateMode.XML, "", "sec:one").getValue());
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "", "SEC:One"));
        Assertions.assertEquals("auth", attrs.getAttribute(AttributeNames.forXMLName("sec:one")).getValue());
        Assertions.assertNull(attrs.getAttribute(AttributeNames.forXMLName("SEC:One")));
        Assertions.assertSame(attributeDefinitions.forXMLName("sec:one"), attrs.getAttribute(TemplateMode.XML, "sec:one").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("sec:one"), attrs.getAttribute(TemplateMode.XML, "sec", "one").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("sec:one"), attrs.getAttribute(TemplateMode.XML, null, "sec:one").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("sec:one"), attrs.getAttribute(TemplateMode.XML, "", "sec:one").definition);
        Assertions.assertSame(attributeDefinitions.forXMLName("sec:one"), attrs.getAttribute(AttributeNames.forXMLName("sec:one")).definition);
        Assertions.assertEquals(2, attrs.attributes.length);


        attrs = new Attributes(null, null);
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, null, "type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "", "type"));
        Assertions.assertFalse(attrs.hasAttribute(AttributeNames.forXMLName("type")));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "TH:Type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "th", "type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "TH", "Type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, null, "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, null, "TH:Type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "", "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "", "TH:Type"));
        Assertions.assertFalse(attrs.hasAttribute(AttributeNames.forXMLName("th:type")));
        Assertions.assertFalse(attrs.hasAttribute(AttributeNames.forXMLName("TH:TYPE")));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "TH:Type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "th", "type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "TH", "Type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, null, "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, null, "TH:Type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "", "th:type"));
        Assertions.assertFalse(attrs.hasAttribute(TemplateMode.XML, "", "TH:Type"));
        Assertions.assertFalse(attrs.hasAttribute(AttributeNames.forXMLName("th:type")));
        Assertions.assertFalse(attrs.hasAttribute(AttributeNames.forXMLName("TH:TYPE")));
        Assertions.assertNull(attrs.attributes);

        attrs = computeXmlAttributes("<input type='text' th:type=\"${thetype}\"/>");
        Assertions.assertEquals(AttributeValueQuotes.SINGLE, attrs.getAttribute(TemplateMode.XML, "type").valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.SINGLE, attrs.getAttribute(TemplateMode.XML, "", "type").valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.SINGLE, attrs.getAttribute(AttributeNames.forXMLName("", "type")).valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.SINGLE, attrs.getAttribute(AttributeNames.forXMLName("type")).valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.DOUBLE, attrs.getAttribute(TemplateMode.XML, "th:type").valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.DOUBLE, attrs.getAttribute(TemplateMode.XML, "", "th:type").valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.DOUBLE, attrs.getAttribute(TemplateMode.XML, "th", "type").valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.DOUBLE, attrs.getAttribute(AttributeNames.forXMLName("", "th:type")).valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.DOUBLE, attrs.getAttribute(AttributeNames.forXMLName("th:type")).valueQuotes);
        Assertions.assertEquals(AttributeValueQuotes.DOUBLE, attrs.getAttribute(AttributeNames.forXMLName("th", "type")).valueQuotes);

        attrs = computeXmlAttributes("<input type='text' \nth:type=\"${thetype}\"/>");
        Assertions.assertEquals(1, attrs.getAttribute(TemplateMode.HTML, "type").line);
        Assertions.assertEquals(1, attrs.getAttribute(TemplateMode.XML, "", "type").line);
        Assertions.assertEquals(1, attrs.getAttribute(AttributeNames.forXMLName("", "type")).line);
        Assertions.assertEquals(1, attrs.getAttribute(AttributeNames.forXMLName("type")).line);
        Assertions.assertEquals(8, attrs.getAttribute(TemplateMode.XML, "type").col);
        Assertions.assertEquals(8, attrs.getAttribute(TemplateMode.XML, "", "type").col);
        Assertions.assertEquals(8, attrs.getAttribute(AttributeNames.forXMLName("", "type")).col);
        Assertions.assertEquals(8, attrs.getAttribute(AttributeNames.forXMLName("type")).col);
        Assertions.assertEquals(2, attrs.getAttribute(TemplateMode.XML, "th:type").line);
        Assertions.assertEquals(2, attrs.getAttribute(TemplateMode.XML, "th", "type").line);
        Assertions.assertEquals(2, attrs.getAttribute(TemplateMode.XML, "", "th:type").line);
        Assertions.assertEquals(2, attrs.getAttribute(AttributeNames.forXMLName("", "th:type")).line);
        Assertions.assertEquals(2, attrs.getAttribute(AttributeNames.forXMLName("th", "type")).line);
        Assertions.assertEquals(1, attrs.getAttribute(TemplateMode.XML, "th:type").col);
        Assertions.assertEquals(1, attrs.getAttribute(TemplateMode.XML, "", "th:type").col);
        Assertions.assertEquals(1, attrs.getAttribute(TemplateMode.XML, "th", "type").col);
        Assertions.assertEquals(1, attrs.getAttribute(AttributeNames.forXMLName("", "th:type")).col);
        Assertions.assertEquals(1, attrs.getAttribute(AttributeNames.forXMLName("th:type")).col);
        Assertions.assertEquals(1, attrs.getAttribute(AttributeNames.forXMLName("th", "type")).col);

        attrs = computeXmlAttributes("<input type='text' \na=\"b\" th:type=\"${thetype}\"/>");
        attrs = attrs.removeAttribute(TemplateMode.XML, "a");
        Assertions.assertEquals(1, attrs.getAttribute(TemplateMode.XML, "type").line);
        Assertions.assertEquals(1, attrs.getAttribute(TemplateMode.XML, "", "type").line);
        Assertions.assertEquals(1, attrs.getAttribute(AttributeNames.forXMLName("", "type")).line);
        Assertions.assertEquals(1, attrs.getAttribute(AttributeNames.forXMLName("type")).line);
        Assertions.assertEquals(8, attrs.getAttribute(TemplateMode.XML, "type").col);
        Assertions.assertEquals(8, attrs.getAttribute(TemplateMode.XML, "", "type").col);
        Assertions.assertEquals(8, attrs.getAttribute(AttributeNames.forXMLName("", "type")).col);
        Assertions.assertEquals(8, attrs.getAttribute(AttributeNames.forXMLName("type")).col);
        Assertions.assertEquals(2, attrs.getAttribute(TemplateMode.XML, "th:type").line);
        Assertions.assertEquals(2, attrs.getAttribute(TemplateMode.XML, "th", "type").line);
        Assertions.assertEquals(2, attrs.getAttribute(TemplateMode.XML, "", "th:type").line);
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "TH", "Type"));
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "", "TH:Type"));
        Assertions.assertEquals(2, attrs.getAttribute(AttributeNames.forXMLName("", "th:type")).line);
        Assertions.assertEquals(2, attrs.getAttribute(AttributeNames.forXMLName("th", "type")).line);
        Assertions.assertEquals(7, attrs.getAttribute(TemplateMode.XML, "th:type").col);
        Assertions.assertEquals(7, attrs.getAttribute(TemplateMode.XML, "", "th:type").col);
        Assertions.assertEquals(7, attrs.getAttribute(TemplateMode.XML, "th", "type").col);
        Assertions.assertEquals(7, attrs.getAttribute(AttributeNames.forXMLName("", "th:type")).col);
        Assertions.assertEquals(7, attrs.getAttribute(AttributeNames.forXMLName("th:type")).col);
        Assertions.assertEquals(7, attrs.getAttribute(AttributeNames.forXMLName("th", "type")).col);

        Assertions.assertTrue(attrs.getAttribute(TemplateMode.XML, "th:type").hasLocation());
        Assertions.assertTrue(attrs.getAttribute(TemplateMode.XML, "", "th:type").hasLocation());
        Assertions.assertTrue(attrs.getAttribute(TemplateMode.XML, "th", "type").hasLocation());
        Assertions.assertNull(attrs.getAttribute(TemplateMode.XML, "TH", "Type"));
        Assertions.assertTrue(attrs.getAttribute(AttributeNames.forXMLName("", "th:type")).hasLocation());
        Assertions.assertTrue(attrs.getAttribute(AttributeNames.forXMLName("th:type")).hasLocation());
        Assertions.assertTrue(attrs.getAttribute(AttributeNames.forXMLName("th", "type")).hasLocation());
        Assertions.assertNull(attrs.getAttribute(AttributeNames.forXMLName("TH:Type")));
        Assertions.assertNull(attrs.getAttribute(AttributeNames.forXMLName("TH", "Type")));
        attrs = attrs.setAttribute(attributeDefinitions, TemplateMode.XML, null, "one", "two", null);
        Assertions.assertFalse(attrs.getAttribute(TemplateMode.XML, "one").hasLocation());
        Assertions.assertFalse(attrs.getAttribute(TemplateMode.XML, "", "one").hasLocation());
        Assertions.assertFalse(attrs.getAttribute(AttributeNames.forXMLName("", "one")).hasLocation());
        Assertions.assertFalse(attrs.getAttribute(AttributeNames.forXMLName("one")).hasLocation());

    }




    private static Attributes computeHtmlAttributes(final String input) {

        final ElementAttributeObtentionTemplateHandler handler = new ElementAttributeObtentionTemplateHandler();

        HTML_PARSER.parseStandalone(TEMPLATE_ENGINE_CONFIGURATION, "test", "test", null, new StringTemplateResource(input), TemplateMode.HTML, false, handler);

        return (handler.attributes != null ? handler.attributes : Attributes.EMPTY_ATTRIBUTES);

    }




    private static Attributes computeXmlAttributes(final String input) {

        final ElementAttributeObtentionTemplateHandler handler = new ElementAttributeObtentionTemplateHandler();

        XML_PARSER.parseStandalone(TEMPLATE_ENGINE_CONFIGURATION, "test", "test", null, new StringTemplateResource(input), TemplateMode.XML, false, handler);

        return (handler.attributes != null ? handler.attributes : Attributes.EMPTY_ATTRIBUTES);

    }




    private static class ElementAttributeObtentionTemplateHandler extends AbstractTemplateHandler {


        Attributes attributes;


        @Override
        public void handleStandaloneElement(final IStandaloneElementTag standaloneElementTag) {
            this.attributes = ((AbstractProcessableElementTag)standaloneElementTag).attributes;
        }

        @Override
        public void handleOpenElement(final IOpenElementTag openElementTag) {
            this.attributes = ((AbstractProcessableElementTag)openElementTag).attributes;
        }

    }


}
