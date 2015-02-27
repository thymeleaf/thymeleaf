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
package org.thymeleaf.aurora.engine;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.util.Validate;


/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class MarkupOutput {







    public static void writeStandaloneElement(
            final Writer writer,
            final String name,
            final ElementAttributes elementAttributes,
            final boolean minimized)
            throws IOException {

        Validate.notNull(writer, "Writer cannot be null");

        writer.write('<');
        writer.write(name);
        elementAttributes.write(writer);
        if (minimized) {
            writer.write('/');
        }
        writer.write('>');

    }


    public static void writeOpenElement(
            final Writer writer,
            final String name,
            final ElementAttributes elementAttributes)
            throws IOException {

        Validate.notNull(writer, "Writer cannot be null");

        writer.write('<');
        writer.write(name);
        elementAttributes.write(writer);
        writer.write('>');

    }


    public static void writeAutoOpenElement(
            final Writer writer,
            final String name,
            final ElementAttributes elementAttributes)
            throws IOException {

        // Nothing to be written... balanced elements were not present at the original template!

    }


    public static void writeCloseElement(
            final Writer writer,
            final String name)
            throws IOException {

        Validate.notNull(writer, "Writer cannot be null");

        writer.write("</");
        writer.write(name);
        writer.write('>');

    }


    public static void writeAutoCloseElement(
            final Writer writer,
            final String name)
            throws IOException {

        // Nothing to be written... balanced elements were not present at the original template!

    }


    public static void writeUnmatchedCloseElement(
            final Writer writer,
            final String name)
            throws IOException {

        Validate.notNull(writer, "Writer cannot be null");

        // We will write exactly the same as for non-unmatched close elements, because that does not matter from the markup point
        writer.write("</");
        writer.write(name);
        writer.write('>');

    }
















    private MarkupOutput() {
        super();
    }
    
    
}