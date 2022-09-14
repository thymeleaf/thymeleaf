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
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.TestTemplateEngineConfigurationBuilder;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.markup.HTMLTemplateParser;
import org.thymeleaf.templateparser.markup.XMLTemplateParser;
import org.thymeleaf.templateresource.StringTemplateResource;


public final class ElementProcessorIteratorTest {

    private static final HTMLTemplateParser HTML_PARSER = new HTMLTemplateParser(2, 4096);
    private static final XMLTemplateParser XML_PARSER = new XMLTemplateParser(2, 4096);





    @Test
    public void testProcessorIteration01() {

        final IProcessorDialect dialect =
                ProcessorAggregationTestDialect.buildHTMLDialect("standard", "th",
                        "N-ELEMENT-10-null-src,N-ELEMENT-5-null-src");

        final TagObtentionTemplateHandler handler = computeHtmlTag("<a th:src='hello'>", dialect);
        final ElementProcessorIterator iterator = handler.iter;
        final OpenElementTag tag = handler.tag;

        Assertions.assertEquals("N-ELEMENT-5-null-{th:src,data-th-src}", iterator.next(handler.tag).toString());
        Assertions.assertEquals("N-ELEMENT-10-null-{th:src,data-th-src}", iterator.next(handler.tag).toString());
        Assertions.assertNull(iterator.next(tag));

    }



    @Test
    public void testProcessorIteration02() {

        final IProcessorDialect dialect =
                ProcessorAggregationTestDialect.buildHTMLDialect("standard", "th",
                        "N-ELEMENT-10-null-src,N-ELEMENT-5-null-src,N-ELEMENT-15-null-one");

        final TagObtentionTemplateHandler handler = computeHtmlTag("<a th:src='hello'>", dialect);
        final AttributeDefinitions attributeDefinitions = handler.attributeDefinitions;
        final ElementProcessorIterator iterator = handler.iter;
        OpenElementTag tag = handler.tag;

        Assertions.assertEquals("N-ELEMENT-5-null-{th:src,data-th-src}", iterator.next(tag).toString());
        tag = tag.setAttribute(attributeDefinitions, null, "th:one", "somevalue", null);
        Assertions.assertEquals("N-ELEMENT-10-null-{th:src,data-th-src}", iterator.next(tag).toString());
        Assertions.assertEquals("N-ELEMENT-15-null-{th:one,data-th-one}", iterator.next(tag).toString());
        Assertions.assertNull(iterator.next(tag));

    }



    @Test
    public void testProcessorIteration03() {

        final IProcessorDialect dialect =
                ProcessorAggregationTestDialect.buildHTMLDialect("standard", "th",
                        "N-ELEMENT-10-null-src,N-ELEMENT-5-null-src,N-ELEMENT-7-null-one");

        final TagObtentionTemplateHandler handler = computeHtmlTag("<a th:src='hello'>", dialect);
        final AttributeDefinitions attributeDefinitions = handler.attributeDefinitions;
        final ElementProcessorIterator iterator = handler.iter;
        OpenElementTag tag = handler.tag;

        Assertions.assertEquals("N-ELEMENT-5-null-{th:src,data-th-src}", iterator.next(tag).toString());
        tag = tag.setAttribute(attributeDefinitions, null, "th:one", "somevalue", null);
        Assertions.assertEquals("N-ELEMENT-7-null-{th:one,data-th-one}", iterator.next(tag).toString());
        Assertions.assertEquals("N-ELEMENT-10-null-{th:src,data-th-src}", iterator.next(tag).toString());
        Assertions.assertNull(iterator.next(tag));

    }



    @Test
    public void testProcessorIteration04() {

        final IProcessorDialect dialect =
                ProcessorAggregationTestDialect.buildHTMLDialect("standard", "th",
                        "N-ELEMENT-10-null-src,N-ELEMENT-5-null-src,N-ELEMENT-2-null-one");

        final TagObtentionTemplateHandler handler = computeHtmlTag("<a th:src='hello'>", dialect);
        final AttributeDefinitions attributeDefinitions = handler.attributeDefinitions;
        final ElementProcessorIterator iterator = handler.iter;
        OpenElementTag tag = handler.tag;

        Assertions.assertEquals("N-ELEMENT-5-null-{th:src,data-th-src}", iterator.next(tag).toString());
        tag = tag.setAttribute(attributeDefinitions, null, "th:one", "somevalue", null);
        Assertions.assertEquals("N-ELEMENT-2-null-{th:one,data-th-one}", iterator.next(tag).toString());
        Assertions.assertEquals("N-ELEMENT-10-null-{th:src,data-th-src}", iterator.next(tag).toString());
        Assertions.assertNull(iterator.next(tag));

    }



    @Test
    public void testProcessorIteration05() {

        final IProcessorDialect dialect =
                ProcessorAggregationTestDialect.buildHTMLDialect("standard", "th",
                        "N-ELEMENT-10-null-src,N-ELEMENT-5-null-src,N-ELEMENT-2-null-one");

        final TagObtentionTemplateHandler handler = computeHtmlTag("<a th:src='hello'>", dialect);
        final AttributeDefinitions attributeDefinitions = handler.attributeDefinitions;
        final ElementProcessorIterator iterator = handler.iter;
        OpenElementTag tag = handler.tag;

        tag = tag.setAttribute(attributeDefinitions, null, "th:one", "somevalue", null);
        Assertions.assertEquals("N-ELEMENT-2-null-{th:one,data-th-one}", iterator.next(tag).toString());
        Assertions.assertEquals("N-ELEMENT-5-null-{th:src,data-th-src}", iterator.next(tag).toString());
        Assertions.assertEquals("N-ELEMENT-10-null-{th:src,data-th-src}", iterator.next(tag).toString());
        Assertions.assertNull(iterator.next(tag));

    }



    @Test
    public void testProcessorIteration06() {

        final IProcessorDialect dialect =
                ProcessorAggregationTestDialect.buildHTMLDialect("standard", "th",
                        "N-ELEMENT-10-null-src,N-ELEMENT-5-null-src,N-ELEMENT-2-null-one");

        final TagObtentionTemplateHandler handler = computeHtmlTag("<a th:src='hello'>", dialect);
        final ElementProcessorIterator iterator = handler.iter;
        OpenElementTag tag = handler.tag;

        tag = tag.removeAttribute("th:src");
        Assertions.assertNull(iterator.next(tag));

    }



    @Test
    public void testProcessorIteration07() {

        final IProcessorDialect dialect =
                ProcessorAggregationTestDialect.buildHTMLDialect("standard", "th",
                        "N-ELEMENT-10-null-src,N-ELEMENT-5-null-src,N-ELEMENT-2-null-one");

        final TagObtentionTemplateHandler handler = computeHtmlTag("<a th:src='hello'>", dialect);
        final AttributeDefinitions attributeDefinitions = handler.attributeDefinitions;
        final ElementProcessorIterator iterator = handler.iter;
        OpenElementTag tag = handler.tag;

        tag = tag.removeAttribute("th:src");
        tag = tag.setAttribute(attributeDefinitions, null, "th:one", "somevalue", null);
        Assertions.assertEquals("N-ELEMENT-2-null-{th:one,data-th-one}", iterator.next(tag).toString());
        Assertions.assertNull(iterator.next(tag));

    }



    @Test
    public void testProcessorIteration08() {

        final IProcessorDialect dialect =
                ProcessorAggregationTestDialect.buildHTMLDialect("standard", "th",
                        "N-ELEMENT-10-null-src,N-ELEMENT-5-null-src,N-ELEMENT-2-null-one");

        final TagObtentionTemplateHandler handler = computeHtmlTag("<a th:src='hello'>", dialect);
        final AttributeDefinitions attributeDefinitions = handler.attributeDefinitions;
        final ElementProcessorIterator iterator = handler.iter;
        OpenElementTag tag = handler.tag;

        Assertions.assertEquals("N-ELEMENT-5-null-{th:src,data-th-src}", iterator.next(tag).toString());
        tag = tag.setAttribute(attributeDefinitions, null, "th:one", "somevalue", null);
        tag = tag.removeAttribute("data-th-src");
        Assertions.assertEquals("N-ELEMENT-2-null-{th:one,data-th-one}", iterator.next(tag).toString());
        Assertions.assertNull(iterator.next(tag));

    }



    @Test
    public void testProcessorIteration09() {

        final IProcessorDialect dialect =
                ProcessorAggregationTestDialect.buildHTMLDialect("standard", "th",
                        "N-ELEMENT-10-null-src,N-ELEMENT-5-null-src,N-ELEMENT-2-null-one");

        final TagObtentionTemplateHandler handler = computeHtmlTag("<a th:src='hello'>", dialect);
        final AttributeDefinitions attributeDefinitions = handler.attributeDefinitions;
        final ElementProcessorIterator iterator = handler.iter;
        OpenElementTag tag = handler.tag;

        Assertions.assertEquals("N-ELEMENT-5-null-{th:src,data-th-src}", iterator.next(tag).toString());
        tag = tag.setAttribute(attributeDefinitions, null, "th:one", "somevalue", null);
        Assertions.assertEquals("N-ELEMENT-2-null-{th:one,data-th-one}", iterator.next(tag).toString());
        tag = tag.removeAttribute("th:src");
        Assertions.assertNull(iterator.next(tag));

    }



    @Test
    public void testProcessorIteration10() {

        // This one checks that iteration also works OK for tags using a non-standard implementation

        final IProcessorDialect dialect =
                ProcessorAggregationTestDialect.buildHTMLDialect("standard", "th",
                        "N-ELEMENT-10-null-src,N-ELEMENT-5-null-src,N-ELEMENT-2-null-one");

        final TagObtentionTemplateHandler handler = computeHtmlTag("<a th:src='hello'>", dialect);
        final AttributeDefinitions attributeDefinitions = handler.attributeDefinitions;
        final ElementProcessorIterator iterator = handler.iter;
        OpenElementTag tag = handler.tag;

        Assertions.assertEquals("N-ELEMENT-5-null-{th:src,data-th-src}", iterator.next(tag).toString());
        tag = tag.setAttribute(attributeDefinitions, null, "th:one", "somevalue", null);
        Assertions.assertEquals("N-ELEMENT-2-null-{th:one,data-th-one}", iterator.next(tag).toString());
        tag = tag.removeAttribute("th:src");
        Assertions.assertNull(iterator.next(tag));

    }



    @Test
    public void testProcessorIteration11() {

        final IProcessorDialect dialect =
                ProcessorAggregationTestDialect.buildHTMLDialect("standard", "th",
                        "N-ELEMENT-10-null-src,N-ELEMENT-5-null-src,N-ELEMENT-2-null-one");

        final TagObtentionTemplateHandler handler = computeHtmlTag("<div class='one'><a th:src='hello'>", dialect);
        final AttributeDefinitions attributeDefinitions = handler.attributeDefinitions;
        final ElementProcessorIterator iterator = handler.iter;
        OpenElementTag tag = handler.tag;

        Assertions.assertEquals("N-ELEMENT-5-null-{th:src,data-th-src}", iterator.next(tag).toString());
        tag = tag.setAttribute(attributeDefinitions, null, "th:one", "somevalue", null);
        Assertions.assertEquals("N-ELEMENT-2-null-{th:one,data-th-one}", iterator.next(tag).toString());
        tag = tag.removeAttribute("th:src");
        Assertions.assertNull(iterator.next(tag));

    }



    @Test
    public void testProcessorIteration12() {

        // This one checks that iteration also works OK for tags using a non-standard implementation

        final IProcessorDialect dialect =
                ProcessorAggregationTestDialect.buildHTMLDialect("standard", "th",
                        "N-ELEMENT-10-null-src,N-ELEMENT-5-null-src,N-ELEMENT-2-null-one");

        final TagObtentionTemplateHandler handler = computeHtmlTag("<div class='one'><a th:src='hello'>", dialect);
        final AttributeDefinitions attributeDefinitions = handler.attributeDefinitions;
        final ElementProcessorIterator iterator = handler.iter;
        OpenElementTag tag = handler.tag;

        Assertions.assertEquals("N-ELEMENT-5-null-{th:src,data-th-src}", iterator.next(tag).toString());
        tag = tag.setAttribute(attributeDefinitions, null, "th:one", "somevalue", null);
        Assertions.assertEquals("N-ELEMENT-2-null-{th:one,data-th-one}", iterator.next(tag).toString());
        tag = tag.removeAttribute("th:src");
        Assertions.assertNull(iterator.next(tag));

    }



    @Test
    public void testProcessorIteration13() {

        final IProcessorDialect dialect =
                ProcessorAggregationTestDialect.buildHTMLDialect("standard", "th",
                        "N-ELEMENT-10-null-src,N-ELEMENT-5-*a-src,N-ELEMENT-2-null-one");

        final TagObtentionTemplateHandler handler = computeHtmlTag("<div class='one'><p th:src='uuuh'><a th:src='hello'>", dialect);
        final AttributeDefinitions attributeDefinitions = handler.attributeDefinitions;
        final ElementProcessorIterator iterator = handler.iter;
        OpenElementTag tag = handler.tag;

        Assertions.assertEquals("N-ELEMENT-5-{a}-{th:src,data-th-src}", iterator.next(tag).toString());
        tag = tag.setAttribute(attributeDefinitions, null, "th:one", "somevalue", null);
        Assertions.assertEquals("N-ELEMENT-2-null-{th:one,data-th-one}", iterator.next(tag).toString());
        tag = tag.removeAttribute("th:src");
        Assertions.assertNull(iterator.next(tag));

    }



    @Test
    public void testProcessorIteration14() {

        // This one checks that iteration also works OK for tags using a non-standard implementation

        final IProcessorDialect dialect =
                ProcessorAggregationTestDialect.buildHTMLDialect("standard", "th",
                        "N-ELEMENT-10-null-src,N-ELEMENT-5-*a-src,N-ELEMENT-2-null-one");

        final TagObtentionTemplateHandler handler = computeHtmlTag("<div class='one'><p th:src='uuuh'><a th:src='hello'>", dialect);
        final AttributeDefinitions attributeDefinitions = handler.attributeDefinitions;
        final ElementProcessorIterator iterator = handler.iter;
        OpenElementTag tag = handler.tag;

        Assertions.assertEquals("N-ELEMENT-5-{a}-{th:src,data-th-src}", iterator.next(tag).toString());
        tag = tag.setAttribute(attributeDefinitions, null, "th:one", "somevalue", null);
        Assertions.assertEquals("N-ELEMENT-2-null-{th:one,data-th-one}", iterator.next(tag).toString());
        tag = tag.removeAttribute("th:src");
        Assertions.assertNull(iterator.next(tag));

    }










    private static TagObtentionTemplateHandler computeHtmlTag(final String input, final IDialect dialect) {
        return computeHtmlTag(input, Collections.singleton(dialect));
    }

    private static TagObtentionTemplateHandler computeHtmlTag(final String input, final Set<IDialect> dialects) {

        final String templateName = "test";
        final TagObtentionTemplateHandler handler = new TagObtentionTemplateHandler();
        final IEngineConfiguration templateEngineContext = TestTemplateEngineConfigurationBuilder.build(dialects);
        handler.attributeDefinitions = templateEngineContext.getAttributeDefinitions();

        HTML_PARSER.parseStandalone(templateEngineContext, templateName, templateName, null, new StringTemplateResource(input), TemplateMode.HTML, false, handler);

        return handler;

    }




    private static TagObtentionTemplateHandler computeXmlTag(final String input, final IDialect dialect) {
        return computeXmlTag(input, Collections.singleton(dialect));
    }

    private static TagObtentionTemplateHandler computeXmlTag(final String input, final Set<IDialect> dialects) {

        final String templateName = "test";
        final TagObtentionTemplateHandler handler = new TagObtentionTemplateHandler();
        final IEngineConfiguration templateEngineContext = TestTemplateEngineConfigurationBuilder.build(dialects);
        handler.attributeDefinitions = templateEngineContext.getAttributeDefinitions();

        XML_PARSER.parseStandalone(templateEngineContext, templateName, templateName, null, new StringTemplateResource(input), TemplateMode.XML, false, handler);

        return handler;

    }




    private static class TagObtentionTemplateHandler extends AbstractTemplateHandler {

        AttributeDefinitions attributeDefinitions;
        OpenElementTag tag;
        ElementProcessorIterator iter = new ElementProcessorIterator();

        TagObtentionTemplateHandler() {
            super();
        }

        @Override
        public void setContext(final ITemplateContext context) {
            super.setContext(context);
            this.attributeDefinitions = context.getConfiguration().getAttributeDefinitions();
        }

        @Override
        public void handleOpenElement(final IOpenElementTag openElementTag) {
            final OpenElementTag oetag = (OpenElementTag) openElementTag;
            if (this.tag != null) {
                this.iter.next(this.tag); // Force the creation and computation of the iterator, and leave it not-completed for more thorough testing
            }
            this.tag = oetag;
            this.iter.reset();
        }

    }






}
