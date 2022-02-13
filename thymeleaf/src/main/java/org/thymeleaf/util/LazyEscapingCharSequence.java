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

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.serializer.IStandardCSSSerializer;
import org.thymeleaf.standard.serializer.IStandardJavaScriptSerializer;
import org.thymeleaf.standard.serializer.StandardSerializers;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.html.HtmlEscape;
import org.unbescape.xml.XmlEscape;


/**
 * <p>
 *   Character sequence that performs a lazy escaping of a text, so that it is directly written to a {@code Writer}
 *   output during the escape operation itself.
 * </p>
 * <p>
 *   It is used sometimes internally by the engine in order to avoid the creation of extra String objects in
 *   some scenarios (e.g. th:text).
 * </p>
 * <p>
 *   This is mostly an <strong>internal class</strong>, and its use is not recommended from user's code.
 * </p>
 * <p>
 *   This class is <strong>not</strong> thread-safe.
 * </p>
 *
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class LazyEscapingCharSequence extends AbstractLazyCharSequence {

    private final IEngineConfiguration configuration;
    private final TemplateMode templateMode;
    private final Object input;


    public LazyEscapingCharSequence(final IEngineConfiguration configuration, final TemplateMode templateMode, final Object input) {

        super();

        if (configuration == null) {
            throw new IllegalArgumentException("Engine Configuraion is null, which is forbidden");
        }
        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode is null, which is forbidden");
        }

        this.configuration = configuration;
        this.templateMode = templateMode;
        this.input = input;

    }




    @Override
    protected String resolveText() {
        final Writer stringWriter = new FastStringWriter();
        produceEscapedOutput(stringWriter);
        return stringWriter.toString();
    }


    @Override
    protected void writeUnresolved(final Writer writer) throws IOException {
        produceEscapedOutput(writer);
    }





    private void produceEscapedOutput(final Writer writer) {

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
        try {

            switch (templateMode) {

                case TEXT:
                    // fall-through
                case HTML:
                    if (this.input != null) {
                        HtmlEscape.escapeHtml4Xml(this.input.toString(), writer);
                    }
                    return;
                case XML:
                    if (this.input != null) {
                        // Note we are outputting a body content here, so it is important that we use the version
                        // of XML escaping meant for content, not attributes (slight differences)
                        XmlEscape.escapeXml10(this.input.toString(), writer);
                    }
                    return;
                case JAVASCRIPT:
                    final IStandardJavaScriptSerializer javaScriptSerializer = StandardSerializers.getJavaScriptSerializer(this.configuration);
                    javaScriptSerializer.serializeValue(this.input, writer);
                    return;
                case CSS:
                    final IStandardCSSSerializer cssSerializer = StandardSerializers.getCSSSerializer(this.configuration);
                    cssSerializer.serializeValue(this.input, writer);
                    return;
                case RAW:
                    if (this.input != null) {
                        writer.write(this.input.toString());
                    }
                    return;
                default:
                    throw new TemplateProcessingException(
                            "Unrecognized template mode " + templateMode + ". Cannot produce escaped output for " +
                            "this template mode.");
            }

        } catch (final IOException e) {
            throw new TemplateProcessingException("An error happened while trying to produce escaped output", e);
        }

    }


}
