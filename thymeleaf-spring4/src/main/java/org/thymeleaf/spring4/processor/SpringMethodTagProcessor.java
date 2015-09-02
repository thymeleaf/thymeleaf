/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring4.processor;

import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IElementStructureHandler;
import org.thymeleaf.engine.MutableMarkup;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.spring4.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.html.HtmlEscape;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class SpringMethodTagProcessor extends AbstractStandardExpressionAttributeTagProcessor {

    
    public static final int ATTR_PRECEDENCE = 990;
    public static final String ATTR_NAME = "method";



    public SpringMethodTagProcessor(final String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, ATTR_NAME, ATTR_PRECEDENCE);
    }



    @Override
    protected final void doProcess(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue, final Object expressionResult,
            final IElementStructureHandler structureHandler) {

        final String newAttributeValue = HtmlEscape.escapeHtml4Xml(expressionResult == null ? null : expressionResult.toString());

        // Set the 'method' attribute, or remove it if evaluated to null
        if (newAttributeValue == null || newAttributeValue.length() == 0) {
            tag.getAttributes().removeAttribute(ATTR_NAME);
        } else {
            tag.getAttributes().setAttribute(ATTR_NAME, newAttributeValue);
        }

        tag.getAttributes().removeAttribute(attributeName); // We need to remove it here before being cloned

        // If this th:action is in a <form> tag, we might need to add a hidden field for non-supported HTTP methods
        if (newAttributeValue != null && "form".equalsIgnoreCase(tag.getElementName())) {

            if (!isMethodBrowserSupported(newAttributeValue)) {

                // Browsers only support HTTP GET and POST. If a different method
                // has been specified, then Spring MVC allows us to specify it
                // using a hidden input with name '_method' and set 'post' for the
                // <form> tag.

                tag.getAttributes().setAttribute(ATTR_NAME, "post");

                final MutableMarkup hiddenMethodMarkup = processingContext.getMarkupFactory().createMarkup();

                final String type = "hidden";
                final String name = "_method";
                final String value =
                        RequestDataValueProcessorUtils.processFormFieldValue(processingContext, name, newAttributeValue, type);

                final IStandaloneElementTag hiddenMethodElementTag =
                        processingContext.getMarkupFactory().createStandaloneElementTag("input", true);
                hiddenMethodElementTag.getAttributes().setAttribute("type", type);
                hiddenMethodElementTag.getAttributes().setAttribute("name", name);
                hiddenMethodElementTag.getAttributes().setAttribute("value", value); // no need to escape

                hiddenMethodMarkup.add(hiddenMethodElementTag);

                structureHandler.insertAfter(hiddenMethodMarkup, false);

            }

        }

    }




    /*
     * Determine if the HTTP method is supported by browsers (i.e. GET or POST).
     */
    protected boolean isMethodBrowserSupported(final String method) {
        return ("get".equalsIgnoreCase(method) || "post".equalsIgnoreCase(method));
    }

    
}
