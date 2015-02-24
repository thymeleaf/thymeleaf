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



    public static void writeText(
            final Writer writer,
            final char[] buffer,
            final int offset, final int len)
            throws IOException {

        Validate.notNull(writer, "Writer cannot be null");
        writer.write(buffer, offset, len);

    }


    public static void writeComment(
            final Writer writer,
            final char[] buffer,
            final int outerOffset, final int outerLen)
            throws IOException {

        Validate.notNull(writer, "Writer cannot be null");
        writer.write(buffer, outerOffset, outerLen);

    }

    
    public static void writeCDATASection(
            final Writer writer,
            final char[] buffer,
            final int outerOffset, final int outerLen)
            throws IOException {

        Validate.notNull(writer, "Writer cannot be null");
        writer.write(buffer, outerOffset, outerLen);

    }




    public static void writeStandaloneElement(
            final Writer writer,
            final String name,
            final ElementAttributes elementAttributes,
            final boolean minimized)
            throws IOException {

        Validate.notNull(writer, "Writer cannot be null");

        writer.write('<');
        writer.write(name);
        writeAttributes(writer, elementAttributes);
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
        writeAttributes(writer, elementAttributes);
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




    public static void writeAttributes(
            final Writer writer,
            final ElementAttributes elementAttributes)
            throws IOException {

        int n = elementAttributes.attributesSize;
        int i = 0;

        // Write the attributes, with their corresponding inner whitespaces if they exist
        while (n-- != 0) {
            if (i < elementAttributes.innerWhiteSpacesSize) {
                writer.write(elementAttributes.innerWhiteSpaces[i].whiteSpace);
            } else {
                // For some reason we don't have a whitespace, so we use the default white space
                writer.write(' ');
            }
            writeAttribute(writer, elementAttributes.attributes[i]);
            i++;
        }

        // There might be a final whitespace after the last attribute
        if (i < elementAttributes.innerWhiteSpacesSize) {
            writer.write(elementAttributes.innerWhiteSpaces[i].whiteSpace);
        }

    }


    /* Non-public, as the Attribute class isn't either */
    static void writeAttribute(
            final Writer writer,
            final ElementAttribute attribute)
            throws IOException {

            /*
             * How an attribute will be written:
             *    - If operator == null : only the attribute name will be written.
             *    - If operator != null AND value == null : the attribute will be written as if its value were the empty string.
             */

        writer.write(attribute.name);
        if (attribute.operator != null) {
            writer.write(attribute.operator);
            if (attribute.doubleQuoted) {
                writer.write('"');
                if (attribute.value != null) {
                    writer.write(attribute.value);
                }
                writer.write('"');
            } else if (attribute.singleQuoted) {
                writer.write('\'');
                if (attribute.value != null) {
                    writer.write(attribute.value);
                }
                writer.write('\'');
            } else {
                if (attribute.value != null) {
                    writer.write(attribute.value);
                }
            }
        }

    }




    public static void writeDocType(
            final Writer writer,
            final String docType)
            throws IOException {

        Validate.notNull(writer, "Writer cannot be null");

        writer.write(docType);

    }


    public static void writeDocType(
            final Writer writer,
            final String keyword,
            final String elementName,
            final String type,
            final String publicId,
            final String systemId,
            final String internalSubset)
            throws IOException {

        Validate.notNull(writer, "Writer cannot be null");

        writer.write("<!");
        writer.write(keyword);
        writer.write(' ');
        writer.write(elementName);
        if (type != null) {
            writer.write(' ');
            writer.write(type);
            if (publicId != null) {
                writer.write(" \"");
                writer.write(publicId);
                writer.write('"');
            }
            writer.write(" \"");
            writer.write(systemId);
            writer.write('"');
        }
        if (internalSubset != null) {
            writer.write(" [");
            writer.write(internalSubset);
            writer.write(']');
        }
        writer.write('>');

    }




    public static void writeXmlDeclaration(
            final Writer writer,
            final String xmlDeclaration)
            throws IOException {

        Validate.notNull(writer, "Writer cannot be null");

        writer.write(xmlDeclaration);

    }


    public static void writeXmlDeclaration(
            final Writer writer,
            final String keyword,
            final String version,
            final String encoding,
            final String standalone)
            throws IOException {

        Validate.notNull(writer, "Writer cannot be null");

        writer.write("<?");
        writer.write(keyword);
        if (version != null) {
            writer.write(' ');
            writer.write(XmlDeclaration.ATTRIBUTE_NAME_VERSION);
            writer.write("=\"");
            writer.write(version);
            writer.write('"');
        }
        if (encoding != null) {
            writer.write(' ');
            writer.write(XmlDeclaration.ATTRIBUTE_NAME_ENCODING);
            writer.write("=\"");
            writer.write(encoding);
            writer.write('"');
        }
        if (standalone != null) {
            writer.write(' ');
            writer.write(XmlDeclaration.ATTRIBUTE_NAME_STANDALONE);
            writer.write("=\"");
            writer.write(standalone);
            writer.write('"');
        }
        writer.write("?>");

    }






    public static void writeProcessingInstruction(
            final Writer writer,
            final String processingInstruction)
            throws IOException {

        Validate.notNull(writer, "Writer cannot be null");

        writer.write(processingInstruction);

    }


    public static void writeProcessingInstruction(
            final Writer writer,
            final String target,
            final String content)
            throws IOException {

        Validate.notNull(writer, "Writer cannot be null");

        writer.write("<?");
        writer.write(target);
        if (content != null) {
            writer.write(' ');
            writer.write(content);
        }
        writer.write("?>");

    }








    private MarkupOutput() {
        super();
    }
    
    
}