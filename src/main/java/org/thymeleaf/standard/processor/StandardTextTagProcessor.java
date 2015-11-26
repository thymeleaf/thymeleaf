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

import java.io.StringWriter;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;
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

        final String text = produceEscapedOutput(getTemplateMode(), expressionResult);

        structureHandler.setBody(text, false);

    }



    private String produceEscapedOutput(final TemplateMode templateMode, final Object input) {

        Validate.notNull(templateMode, "Template mode cannot be null");

        /*
         * Producing ESCAPED output is somewhat simple in HTML or XML modes, as it simply consists of converting
         * input into a String and HTML-or-XML-escaping it.
         *
         * But for JavaScript or CSS, it becomes a bit more complicated than that. JavaScript will output a complete
         * literal (incl. single quotes) if input is a String or a non-recognized value type, but will print input
         * as an object, number, boolean, etc. if it is recognized to be of one of these types. CSS will output
         * quoted literals.
         *
         * Note that, when in TEXT mode, HTML escaping will be used (as TEXT is many times used for
         * processing HTML templates)
         */
        switch (templateMode) {

            case TEXT:
                // fall-through
            case HTML:
                return (input == null? "" : HtmlEscape.escapeHtml4Xml(input.toString()));
            case XML:
                return (input == null? "" : XmlEscape.escapeXml10(input.toString()));
            case JAVASCRIPT:
                final StringWriter jsStringWriter = new StringWriter();
                ((StandardDialect)getDialect()).getJavaScriptSerializer().serializeValue(input, jsStringWriter);
                return jsStringWriter.toString();
            case CSS:
                final StringWriter cssStringWriter = new StringWriter();
                ((StandardDialect)getDialect()).getCSSSerializer().serializeValue(input, cssStringWriter);
                return cssStringWriter.toString();
            case RAW:
                return (input == null? "" : input.toString());
            default:
                throw new TemplateProcessingException(
                        "Unrecognized template mode " + templateMode + ". Cannot produce escaped output for " +
                                "this template mode.");
        }

    }


}
