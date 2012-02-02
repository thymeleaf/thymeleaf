/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.TemplateOutputException;
import org.thymeleaf.templatewriter.XhtmlHtml5TemplateWriter;
import org.thymeleaf.templatewriter.XmlTemplateWriter;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1.2
 *
 */
public final class DOMUtils {

    
    private static final char[] AMP = "&amp;".toCharArray();
    private static final char[] LT = "&lt;".toCharArray();
    private static final char[] GT = "&gt;".toCharArray();
    private static final char[] QUOT = "&quot;".toCharArray();
    private static final char[] APOS = "&apos;".toCharArray();
    
    
    
    public static NestableNode extractFragmentByAttributeValue(
            final Node root, final String elementName, final String attributeName, final String attributeValue) {

        Validate.notNull(root, "Root cannot be null");
        // Element name CAN be null (in that case, all elements will be searched)
        Validate.notNull(attributeName, "Attribute name cannot be null");
        Validate.notNull(attributeValue, "Attribute value cannot be null");
        
        return exploreNodeForExtractingFragment(root, Node.normalizeName(elementName), Node.normalizeName(attributeName), attributeValue);
        
    }
    
    
    private static NestableNode exploreNodeForExtractingFragment(
            final Node node, final String normalizedElementName, final String normalizedAttributeName, final String attributeValue) {
        
        if (node instanceof NestableNode) {

            final NestableNode nestableNode = (NestableNode) node;
            
            if (nestableNode instanceof Element) {
                final Element element = (Element) nestableNode;
                if (element.hasNormalizedAttribute(normalizedAttributeName) && 
                        (normalizedElementName == null || normalizedElementName.equals(element.getNormalizedName()))) {
                    final String elementAttrValue = element.getAttributeValue(normalizedAttributeName);
                    if (elementAttrValue != null && elementAttrValue.trim().equals(attributeValue)) {
                        return nestableNode;
                    }
                }
            }
            
            final List<Node> children = nestableNode.getChildren();
            for (final Node child : children) {
                final NestableNode childResult =
                        exploreNodeForExtractingFragment(
                                child, normalizedElementName, normalizedAttributeName, attributeValue);
                if (childResult != null) {
                    return childResult;
                }
            }
            
        }
        
        return null;
        
    }

    
    
    
    public static char[] escapeXml(final char[] ch, final boolean escapeQuotes) throws IOException {
        
        if (ch == null) {
            return null;
        }
        
        for (int i = 0; i < ch.length; i++) {
            final char c = ch[i];
            if (c == '&' || c == '<' || c == '>' || (escapeQuotes && (c == '\'' || c == '\"'))) {
                final CharArrayWriter writer = new CharArrayWriter();
                writeXmlEscaped(ch, writer, escapeQuotes);
                return writer.toCharArray();
            }
        }
        
        return ch;
        
    }
    
    

    public static void writeXmlEscaped(final String buffer, final Writer writer, final boolean escapeQuotes) throws IOException {
        if (buffer == null) {
            return;
        }
        writeXmlEscaped(buffer.toCharArray(), writer, escapeQuotes);
    }
        
    
    public static void writeXmlEscaped(final char[] buffer, final Writer writer, final boolean escapeQuotes) throws IOException {
        
        if (buffer == null || buffer.length == 0) {
            return;
        }
        
        int off = 0;
        int len = 0;
        int i = 0;
        
        while (i < buffer.length) {
            final char c = buffer[i];
            if (c == '&') {
                if (len > 0) {
                    writer.write(buffer, off, len);
                }
                writer.write(AMP);
                off = i + 1;
                len = 0;
            } else if (c == '<') {
                if (len > 0) {
                    writer.write(buffer, off, len);
                }
                writer.write(LT);
                off = i + 1;
                len = 0;
            } else if (c == '>') {
                if (len > 0) {
                    writer.write(buffer, off, len);
                }
                writer.write(GT);
                off = i + 1;
                len = 0;
            } else if (escapeQuotes && c == '\"') {
                if (len > 0) {
                    writer.write(buffer, off, len);
                }
                writer.write(QUOT);
                off = i + 1;
                len = 0;
            } else if (escapeQuotes && c == '\'') {
                if (len > 0) {
                    writer.write(buffer, off, len);
                }
                writer.write(APOS);
                off = i + 1;
                len = 0;
            } else {
                len++;
            }
            i++;
        }
        if (len > 0) {
            writer.write(buffer, off, len);
        }
    }
    

    
    
    public static String unescapeXml(final String str, final boolean unescapeQuotes) {
        
        if (str == null) {
            return null;
        }
        
        final int strLen = str.length();
        
        // Try to fail fast if no unescaping is needed
        boolean existsAmpersand = false;
        for (int i = 0; i < strLen; i++) {
            if (str.charAt(i) == '&') {
                existsAmpersand = true;
                break;
            }
        }
        if (!existsAmpersand) {
            return str;
        }
        
        int off = 0;
        int len = 0;
        int entityStart = -1;
        int entityLen = 0;
        
        int i = 0;
        
        final StringBuilder builder = new StringBuilder();
        while (i < strLen) {
            
            final char c = str.charAt(i);
            
            if (c == '&') {
                if (entityLen > 0) {
                    builder.append(str.substring(entityStart, (entityStart + entityLen)));
                } else if (len > 0) {
                    builder.append(str.substring(off, (off + len)));
                }
                entityStart = i;
                entityLen = 1;
                off = -1;
                len = 0;
            } else if (c == ';' && entityStart != -1) {
                builder.append(unescapeXmlEntity(str, entityStart, entityLen + 1, unescapeQuotes));
                entityStart = -1;
                entityLen = 0;
                off = i + 1;
                len = 0;
            } else {
                if (entityStart != -1) {
                    entityLen++;
                } else {
                    len++;
                }
            }
            i++;
        }
        if (entityLen > 0) {
            builder.append(str, entityStart, (entityStart + entityLen));
        } else if (len > 0) {
            builder.append(str.substring(off, (off + len)));
        }
        
        return builder.toString();
        
    }

    
    
    private static String unescapeXmlEntity(
            final String str, final int off, final int len, final boolean unescapeQuotes) {
        
        if (len < 4) {
            return str.substring(off, (off + len));
        }
        
        final char c1 = str.charAt(off + 1);
        final char c2 = str.charAt(off + 2);
        if (len == 4 && c1 == 'l' && c2 == 't') {
            return "<";
        } else if (len == 4 && c1 == 'g' && c2 == 't') {
            return ">";
        } else if (len == 5 && c1 == 'a' && c2 == 'm' && str.charAt(off + 3) == 'p') {
            return "&";
        } else if (len == 6 && unescapeQuotes && c1 == 'q' && c2 == 'u' && str.charAt(off + 3) == 'o' && str.charAt(off + 4) == 't') {
            return "\"";
        } else if (len == 6 && unescapeQuotes && c1 == 'a' && c2 == 'p' && str.charAt(off + 3) == 'o' && str.charAt(off + 4) == 's') {
            return "\'";
        } else {
            return str.substring(off, (off + len));
        }
        
    }
    
    
    
    
    public static String getXmlFor(final Node node) {
        try {
            final XmlTemplateWriter templateWriter = new XmlTemplateWriter();
            final StringWriter writer = new StringWriter();
            templateWriter.writeNode(null, writer, node);
            return writer.toString();
        } catch (final IOException e) {
            throw new TemplateOutputException(
                    "Exception during creation of XML output for node", e);
        }
    }
    
    
    public static String getXhtmlHtml5For(final Node node) {
        try {
            final XhtmlHtml5TemplateWriter templateWriter = new XhtmlHtml5TemplateWriter();
            final StringWriter writer = new StringWriter();
            templateWriter.writeNode(null, writer, node);
            return writer.toString();
        } catch (final IOException e) {
            throw new TemplateOutputException(
                    "Exception during creation of XHTML/HTML5 output for node", e);
        }
    }
    
    
    private DOMUtils() {
        super();
    }

    
}
