/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
import java.util.List;

import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Tag;

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
            final Node root, final String tagName, final String attributeName, final String attributeValue) {

        Validate.notNull(root, "Root cannot be null");
        // Tag name CAN be null (in that case, all tags will be searched)
        Validate.notNull(attributeName, "Attribute name cannot be null");
        Validate.notNull(attributeValue, "Attribute value cannot be null");
        
        return processNode(root, tagName, attributeName, attributeValue);
        
    }
    
    
    private static NestableNode processNode(
            final Node node, final String tagName, final String attributeName, final String attributeValue) {
        
        if (node instanceof NestableNode) {

            final NestableNode nestableNode = (NestableNode) node;
            
            if (nestableNode instanceof Tag) {
                final Tag tag = (Tag) nestableNode;
                if (tag.hasAttribute(attributeName) && (tagName == null || tag.isName(tagName))) {
                    final String tagAttrValue = tag.getAttributeValue(attributeName);
                    if (tagAttrValue != null && tagAttrValue.trim().equals(attributeValue)) {
                        return nestableNode;
                    }
                }
            }
            
            final List<Node> children = nestableNode.getChildren();
            for (final Node child : children) {
                final NestableNode childResult =
                        processNode(child, tagName, attributeName, attributeValue);
                if (childResult != null) {
                    return childResult;
                }
            }
            
        }
        
        return null;
        
    }


    
    public static void writeXmlEscaped(final String value, final Writer writer, final boolean escapeQuotes) throws IOException {
        writeXmlEscaped((value == null? null : value.toCharArray()), writer, escapeQuotes);
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
    
    
    
    
    
    private DOMUtils() {
        super();
    }
    
}
