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
package org.thymeleaf.standard.serializer;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.unbescape.css.CssEscape;


/**
 * <p>
 *   Default implementation of the {@link IStandardCSSSerializer}.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class StandardCSSSerializer implements IStandardCSSSerializer {




    public StandardCSSSerializer() {
        super();
    }




    public void serializeValue(final Object object, final Writer writer) {
        try {
            writeValue(writer, object);
        } catch (final IOException e) {
            throw new TemplateProcessingException(
                    "An exception was raised while trying to serialize object to CSS", e);
        }
    }






    private static void writeValue(final Writer writer, final Object object) throws IOException {
        if (object == null) {
            writeNull(writer);
            return;
        }
        if (object instanceof CharSequence) {
            writeString(writer, object.toString());
            return;
        }
        if (object instanceof Character) {
            writeString(writer, object.toString());
            return;
        }
        if (object instanceof Number) {
            writeNumber(writer, (Number) object);
            return;
        }
        if (object instanceof Boolean) {
            writeBoolean(writer, (Boolean) object);
            return;
        }
        writeString(writer, object.toString());
    }


    private static void writeNull(final Writer writer) throws IOException {
        writer.write(""); // There isn't really a 'null' token in CSS
    }


    private static void writeString(final Writer writer, final String str) throws IOException {
        writer.write(CssEscape.escapeCssIdentifier(str));
    }


    private static void writeNumber(final Writer writer, final Number number) throws IOException {
        writer.write(number.toString());
    }


    private static void writeBoolean(final Writer writer, final Boolean bool) throws IOException {
        writer.write(bool.toString());
    }



}
