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
package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.LazyEscapingCharSequence;
import org.unbescape.html.HtmlEscape;
import org.unbescape.xml.XmlEscape;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardTextTagProcessor extends AbstractStandardExpressionAttributeTagProcessor {

    public static final int PRECEDENCE = 1300;
    public static final String ATTR_NAME = "text";


    public StandardTextTagProcessor(final IProcessorDialect dialect, final TemplateMode templateMode, final String dialectPrefix) {
        super(dialect, templateMode, dialectPrefix, ATTR_NAME, PRECEDENCE, true);
    }



    @Override
    protected void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final String attributeTemplateName, final int attributeLine, final int attributeCol,
            final Object expressionResult,
            final IElementTagStructureHandler structureHandler) {

        final TemplateMode templateMode = getTemplateMode();

        /*
         * Depending on the template mode and the length of the text to be output escaped, we will try to opt for
         * the most resource-efficient alternative.
         *
         *    * If we are outputting RAW, there is no escape to do, just pass through.
         *    * If we are outputting HTML, XML or TEXT we know output will be textual (result of calling .toString() on
         *      the expression result), and therefore we can decide between an immediate vs lazy escaping alternative
         *      depending on size. We will perform lazy escaping, writing directly to output Writer, if length > 100.
         *    * If we are outputting JAVASCRIPT or CSS, we will always pass the expression result unchanged to a lazy
         *      escape processor, so that whatever the JS/CSS serializer wants to do, it does it directly on the
         *      output Writer and the entire results are never really needed in memory.
         */

        final CharSequence text;

        if (templateMode != TemplateMode.JAVASCRIPT && templateMode != TemplateMode.CSS) {

            final String input = (expressionResult == null? "" : expressionResult.toString());

            if (templateMode == TemplateMode.RAW) {
                // RAW -> just output

                text = input;

            } else {

                if (input.length() > 100) {
                    // Might be a large text -> Lazy escaping on the output Writer
                    text = new LazyEscapingCharSequence((StandardDialect) getDialect(), templateMode, input);
                } else {
                    // Not large -> better use a bit more of memory, but be faster
                    text = produceEscapedOutput(templateMode, input);
                }

            }

        } else {
            // JavaScript and CSS serializers always work directly on the output Writer, no need to store the entire
            // serialized contents in memory (unless the Writer itself wants to do so).

            text = new LazyEscapingCharSequence((StandardDialect) getDialect(), templateMode, expressionResult);

        }

        // Report the result to the engine, whichever the type of process we have applied
        structureHandler.setBody(text, false);

    }


    private static String produceEscapedOutput(final TemplateMode templateMode, final String input) {

        switch (templateMode) {

            case TEXT:
                // fall-through
            case HTML:
                return HtmlEscape.escapeHtml4Xml(input);
            case XML:
                return XmlEscape.escapeXml10(input);
            default:
                throw new TemplateProcessingException(
                        "Unrecognized template mode " + templateMode + ". Cannot produce escaped output for " +
                        "this template mode.");
        }

    }


}
