/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.util;

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.css.CssEscape;
import org.unbescape.html.HtmlEscape;
import org.unbescape.javascript.JavaScriptEscape;
import org.unbescape.xml.XmlEscape;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class EscapedAttributeUtils {



    public static String escapeAttribute(final TemplateMode templateMode, final String input) {

        if (input == null) {
            return null;
        }

        Validate.notNull(templateMode, "Template mode cannot be null");

        /*
         * Depending on the template mode that we are using, we might be receiving element attributes escaped in
         * different ways.
         *
         * HTML and XML have their own escaping/unescaping rules, which we can easily apply by means
         * of the corresponding Unbescape utility methods. TEXT, JAVASCRIPT and CSS are left out because there are no
         * attributes to be output in those modes as such.
         *
         * There is no standard way to escape/unescape in TEXT modes, but given TEXT mode is many times used for
         * markup (HTML or XML templates or inlined fragments), we will use HTML escaping/unescaping for TEXT mode.
         * Besides, this is consistent with the fact that TEXT-mode escaped output will also be HTML-escaped by
         * processors and inlining utilities in the Standard Dialects.
         */
        switch (templateMode) {

            case HTML:
                return HtmlEscape.escapeHtml4Xml(input);
            case XML:
                return XmlEscape.escapeXml10Attribute(input);
            default:
                throw new TemplateProcessingException(
                        "Unrecognized template mode " + templateMode + ". Cannot produce escaped attributes for " +
                        "this template mode.");
        }

    }



    public static String unescapeAttribute(final TemplateMode templateMode, final String input) {

        if (input == null) {
            return null;
        }

        Validate.notNull(templateMode, "Template mode cannot be null");

        /*
         * Depending on the template mode that we are using, we might be receiving element attributes escaped in
         * different ways.
         *
         * HTML, XML, JAVASCRIPT and CSS have their own escaping/unescaping rules, which we can easily apply by means
         * of the corresponding Unbescape utility methods.
         *
         * There is no standard way to escape/unescape in TEXT modes, but given TEXT mode is many times used for
         * markup (HTML or XML templates or inlined fragments), we will use HTML escaping/unescaping for TEXT mode.
         * Besides, this is consistent with the fact that TEXT-mode escaped output will also be HTML-escaped by
         * processors and inlining utilities in the Standard Dialects.
         */
        switch (templateMode) {

            case TEXT:
                // fall-through
            case HTML:
                return HtmlEscape.unescapeHtml(input);
            case XML:
                return XmlEscape.unescapeXml(input);
            case JAVASCRIPT:
                return JavaScriptEscape.unescapeJavaScript(input);
            case CSS:
                return CssEscape.unescapeCss(input);
            case RAW:
                return input;
            default:
                throw new TemplateProcessingException(
                        "Unrecognized template mode " + templateMode + ". Cannot unescape attribute value for " +
                        "this template mode.");
        }

    }






    private EscapedAttributeUtils() {
        super();
    }



}
