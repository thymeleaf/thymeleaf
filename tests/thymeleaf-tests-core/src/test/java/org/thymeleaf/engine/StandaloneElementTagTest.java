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
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.markup.HTMLTemplateParser;
import org.thymeleaf.templateparser.markup.XMLTemplateParser;
import org.thymeleaf.templateresource.StringTemplateResource;


public final class StandaloneElementTagTest {

    private static final HTMLTemplateParser HTML_PARSER = new HTMLTemplateParser(2, 4096);
    private static final XMLTemplateParser XML_PARSER = new XMLTemplateParser(2, 4096);
    private static final IEngineConfiguration TEMPLATE_ENGINE_CONFIGURATION = TestTemplateEngineConfigurationBuilder.build();




    @Test
    public void testHtmlStandaloneElementAttrManagement() {

        final AttributeDefinitions attributeDefinitions = new AttributeDefinitions(Collections.EMPTY_MAP);

        StandaloneElementTag tag;

        tag = computeHtmlTag("<input>");
        Assertions.assertEquals("<input>", tag.toString());

        tag = computeHtmlTag("<input type=\"text\">");
        Assertions.assertEquals("<input type=\"text\">", tag.toString());

        tag = computeHtmlTag("<input type=\"text\"   value='hello!!!'>");
        Assertions.assertEquals("<input type=\"text\"   value='hello!!!'>", tag.toString());
        tag = tag.removeAttribute("type");
        Assertions.assertEquals("<input value='hello!!!'>", tag.toString());
        tag = tag.removeAttribute("value");
        Assertions.assertEquals("<input>", tag.toString());

        tag = computeHtmlTag("<input type=\"text\"   value='hello!!!'    >");
        Assertions.assertEquals("<input type=\"text\"   value='hello!!!'    >", tag.toString());
        tag = tag.removeAttribute(null, "type");
        Assertions.assertEquals("<input value='hello!!!'    >", tag.toString());
        tag = tag.removeAttribute(null, "value");
        Assertions.assertEquals("<input    >", tag.toString());

        tag = computeHtmlTag("<input type=\"text\"   value='hello!!!'    ba >");
        Assertions.assertEquals("<input type=\"text\"   value='hello!!!'    ba >", tag.toString());
        tag = tag.setAttribute(attributeDefinitions, null, "value", "bye! :(", null);
        Assertions.assertEquals("<input type=\"text\"   value='bye! :('    ba >", tag.toString());
        tag = tag.setAttribute(attributeDefinitions, null, "type", "one", null);
        Assertions.assertEquals("<input type=\"one\"   value='bye! :('    ba >", tag.toString());
        tag = tag.setAttribute(attributeDefinitions, null, "ba", "two", null);
        Assertions.assertEquals("<input type=\"one\"   value='bye! :('    ba=\"two\" >", tag.toString());
        tag = tag.setAttribute(attributeDefinitions, null, "ba", "three", AttributeValueQuotes.SINGLE);
        Assertions.assertEquals("<input type=\"one\"   value='bye! :('    ba='three' >", tag.toString());
        tag = tag.setAttribute(attributeDefinitions, null, "ba", "four", AttributeValueQuotes.NONE);
        Assertions.assertEquals("<input type=\"one\"   value='bye! :('    ba=four >", tag.toString());
        tag = tag.setAttribute(attributeDefinitions, null, "ba", "five", null);
        Assertions.assertEquals("<input type=\"one\"   value='bye! :('    ba=five >", tag.toString());
        tag = tag.setAttribute(attributeDefinitions, null, "ba", null, null);
        Assertions.assertEquals("<input type=\"one\"   value='bye! :('    ba >", tag.toString());
        tag = tag.setAttribute(attributeDefinitions, null, "ba", "six", null);
        Assertions.assertEquals("<input type=\"one\"   value='bye! :('    ba=\"six\" >", tag.toString());

        tag = computeHtmlTag("<input type=\"text\"   value='hello!!!'    ba=twenty >");
        tag = tag.setAttribute(attributeDefinitions, null, "ba", "thirty", null);
        Assertions.assertEquals("<input type=\"text\"   value='hello!!!'    ba=thirty >", tag.toString());

        tag = computeHtmlTag("<input type=\"text\"   value='hello!!!'    ba=twenty ><p id='one'/>");
        Assertions.assertEquals("<p id='one'/>", tag.toString());

    }




    @Test
    public void testXmlStandaloneElementAttrManagement() {

        final AttributeDefinitions attributeDefinitions = new AttributeDefinitions(Collections.EMPTY_MAP);

        StandaloneElementTag tag;

        tag = computeXmlTag("<input/>");
        Assertions.assertEquals("<input/>", tag.toString());

        tag = computeXmlTag("<input type=\"text\"/>");
        Assertions.assertEquals("<input type=\"text\"/>", tag.toString());

        tag = computeXmlTag("<input type=\"text\"   value='hello!!!'/>");
        Assertions.assertEquals("<input type=\"text\"   value='hello!!!'/>", tag.toString());
        tag = tag.removeAttribute("type");
        Assertions.assertEquals("<input value='hello!!!'/>", tag.toString());
        tag = tag.removeAttribute("value");
        Assertions.assertEquals("<input/>", tag.toString());

        tag = computeXmlTag("<input type=\"text\"   value='hello!!!'    />");
        Assertions.assertEquals("<input type=\"text\"   value='hello!!!'    />", tag.toString());
        tag = tag.removeAttribute(null, "type");
        Assertions.assertEquals("<input value='hello!!!'    />", tag.toString());
        tag = tag.removeAttribute(null, "value");
        Assertions.assertEquals("<input    />", tag.toString());

        tag = computeXmlTag("<input th:type=\"text\"   th:value='hello!!!'    />");
        Assertions.assertEquals("<input th:type=\"text\"   th:value='hello!!!'    />", tag.toString());
        tag = tag.removeAttribute("th", "type");
        Assertions.assertEquals("<input th:value='hello!!!'    />", tag.toString());
        tag = tag.removeAttribute("th", "value");
        Assertions.assertEquals("<input    />", tag.toString());

        tag = computeXmlTag("<input type=\"text\"   value='hello!!!'    ba='' />");
        Assertions.assertEquals("<input type=\"text\"   value='hello!!!'    ba='' />", tag.toString());
        tag = tag.setAttribute(attributeDefinitions, null, "value", "bye! :(", null);
        Assertions.assertEquals("<input type=\"text\"   value='bye! :('    ba='' />", tag.toString());
        tag = tag.setAttribute(attributeDefinitions, null, "type", "one", null);
        Assertions.assertEquals("<input type=\"one\"   value='bye! :('    ba='' />", tag.toString());
        tag = tag.setAttribute(attributeDefinitions, null, "ba", "two", null);
        Assertions.assertEquals("<input type=\"one\"   value='bye! :('    ba='two' />", tag.toString());
        tag = tag.setAttribute(attributeDefinitions, null, "ba", "three", AttributeValueQuotes.SINGLE);
        Assertions.assertEquals("<input type=\"one\"   value='bye! :('    ba='three' />", tag.toString());

        try {
            tag = tag.setAttribute(attributeDefinitions, null, "ba", "four", AttributeValueQuotes.NONE);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            tag = tag.setAttribute(attributeDefinitions, null, "ba", null, AttributeValueQuotes.NONE);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            tag = tag.setAttribute(attributeDefinitions, null, "ba", null, null);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        tag = tag.setAttribute(attributeDefinitions, null, "ba", "five", null);
        Assertions.assertEquals("<input type=\"one\"   value='bye! :('    ba='five' />", tag.toString());
        tag = tag.setAttribute(attributeDefinitions, null, "ba", "six", null);
        Assertions.assertEquals("<input type=\"one\"   value='bye! :('    ba='six' />", tag.toString());

        tag = computeXmlTag("<input type=\"text\"   value='hello!!!'    ba='twenty' /><meta id='one' />");
        Assertions.assertEquals("<meta id='one' />", tag.toString());

    }







    @Test
    public void testHtmlStandaloneElementPropertyManagement() {

        StandaloneElementTag tag;
        final ElementDefinitions elementDefinitions = TEMPLATE_ENGINE_CONFIGURATION.getElementDefinitions();

        tag = computeHtmlTag("<input>");
        Assertions.assertSame(elementDefinitions.forHTMLName("input"), tag.getElementDefinition());
        tag = new StandaloneElementTag(tag.templateMode, tag.elementDefinition, tag.elementCompleteName, tag.attributes, tag.synthetic, true, tag.templateName, tag.line, tag.col);
        Assertions.assertEquals("<input/>", tag.toString());
        Assertions.assertSame(elementDefinitions.forHTMLName("input"), tag.getElementDefinition());

        tag = computeHtmlTag("<input />");
        Assertions.assertSame(elementDefinitions.forHTMLName("input"), tag.getElementDefinition());
        tag = new StandaloneElementTag(tag.templateMode, tag.elementDefinition, tag.elementCompleteName, tag.attributes, tag.synthetic, false, tag.templateName, tag.line, tag.col);
        Assertions.assertEquals("<input >", tag.toString());
        Assertions.assertSame(elementDefinitions.forHTMLName("input"), tag.getElementDefinition());

    }








    @Test
    public void testXmlStandaloneElementPropertyManagement() {

        StandaloneElementTag tag;
        final ElementDefinitions elementDefinitions = TEMPLATE_ENGINE_CONFIGURATION.getElementDefinitions();

        tag = computeXmlTag("<input/>");
        Assertions.assertSame(elementDefinitions.forXMLName("input"), tag.getElementDefinition());
        try {
            tag = new StandaloneElementTag(tag.templateMode, tag.elementDefinition, tag.elementCompleteName, tag.attributes, tag.synthetic, false, tag.templateName, tag.line, tag.col);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

    }




    private static StandaloneElementTag computeHtmlTag(final String input) {

        final String templateName = "test";
        final TagObtentionTemplateHandler handler = new TagObtentionTemplateHandler();

        HTML_PARSER.parseStandalone(TEMPLATE_ENGINE_CONFIGURATION, templateName, templateName, null, new StringTemplateResource(input), TemplateMode.HTML, false, handler);

        return handler.tag;

    }




    private static StandaloneElementTag computeXmlTag(final String input) {

        final String templateName = "test";
        final TagObtentionTemplateHandler handler = new TagObtentionTemplateHandler();

        XML_PARSER.parseStandalone(TEMPLATE_ENGINE_CONFIGURATION, templateName, templateName, null, new StringTemplateResource(input), TemplateMode.XML, false, handler);

        return handler.tag;

    }




    private static class TagObtentionTemplateHandler extends AbstractTemplateHandler {


        StandaloneElementTag tag;


        @Override
        public void handleStandaloneElement(final IStandaloneElementTag standaloneElementTag) {
            this.tag = StandaloneElementTag.asEngineStandaloneElementTag(standaloneElementTag);
        }

    }


}
