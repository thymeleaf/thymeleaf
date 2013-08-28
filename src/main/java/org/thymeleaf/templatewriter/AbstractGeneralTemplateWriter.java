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
package org.thymeleaf.templatewriter;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.CDATASection;
import org.thymeleaf.dom.Comment;
import org.thymeleaf.dom.DocType;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Element.RepresentationInTemplate;
import org.thymeleaf.dom.GroupNode;
import org.thymeleaf.dom.Macro;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Text;
import org.thymeleaf.util.DOMUtils;
import org.thymeleaf.util.Validate;


/**
 * 
 * @since 2.0.0
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 */
public abstract class AbstractGeneralTemplateWriter implements ITemplateWriter {


    private static final char[] COMMENT_PREFIX = "<!--".toCharArray();
    private static final char[] COMMENT_SUFFIX = "-->".toCharArray();

    private static final char[] CDATA_PREFIX = "<![CDATA[".toCharArray();
    private static final char[] CDATA_SUFFIX = "]]>".toCharArray();
    
    private static final char[] XML_DECLARATION_PREFIX = "<?xml version=\"1.0\"".toCharArray();
    private static final char[] XML_DECLARATION_SUFFIX = "?>\n".toCharArray();



    protected AbstractGeneralTemplateWriter() {
        super();
    }

    
    public void write(final Arguments arguments, final Writer writer, final Document document) 
                throws IOException {
        if (document == null) {
            return;
        }
        writeDocument(arguments, writer, document);
    }

    
    protected abstract boolean shouldWriteXmlDeclaration();
    
    protected abstract boolean useXhtmlTagMinimizationRules();
    
    
    
    
    protected void writeDocument(final Arguments arguments, final Writer writer, final Document document) 
                throws IOException {
        
        if (shouldWriteXmlDeclaration()) {
            writeXmlDeclaration(writer, document);
        }
        if (document.hasDocType()) {
            writeDocType(arguments, writer, document.getDocType());
            writer.write('\n');
        }
        if (document.hasChildren()) {
            final Node[] children = document.unsafeGetChildrenNodeArray();
            final int childrenLen = document.numChildren();
            for (int i = 0; i < childrenLen; i++) {
                writeNode(arguments, writer, children[i]);
            }
        }
        
    }
    
    
    
    protected void writeXmlDeclaration(final Writer writer, final Document document) 
            throws IOException {
        writer.write(XML_DECLARATION_PREFIX);
        if (document.hasNodeProperty(Node.NODE_PROPERTY_XML_ENCODING)) {
            final String xmlEncoding = (String) document.getNodeProperty(Node.NODE_PROPERTY_XML_ENCODING);
            if (xmlEncoding != null) {
                writer.write(" encoding=\"");
                writer.write(xmlEncoding);
                writer.write("\"");
            }
        }
        if (document.hasNodeProperty(Node.NODE_PROPERTY_XML_STANDALONE)) {
            final Boolean xmlStandalone = (Boolean) document.getNodeProperty(Node.NODE_PROPERTY_XML_STANDALONE);
            if (xmlStandalone != null && xmlStandalone.booleanValue()) {
                writer.write(" standalone=\"true\"");
            }
        }
        writer.write(XML_DECLARATION_SUFFIX);
    }
    
    
    
    
    @SuppressWarnings("unused")
    protected void writeDocType(final Arguments arguments, final Writer writer, final DocType docType) 
            throws IOException {
        docType.write(writer);
    }

    
    
    public void writeNode(final Arguments arguments, final Writer writer, final Node node) 
            throws IOException {
    
        Validate.notNull(arguments, "Arguments cannot be null");
        if (node == null) {
            return;
        }
        
        if (node instanceof Element) {
            writeElement(arguments, writer, (Element)node);
        } else if (node instanceof GroupNode) {
            writeGroupNode(arguments, writer, (GroupNode)node);
        } else if (node instanceof Text) {
            writeText(arguments, writer, (Text)node);
        } else if (node instanceof Comment) {
            writeComment(arguments, writer, (Comment)node);
        } else if (node instanceof CDATASection) {
            writeCDATASection(arguments, writer, (CDATASection)node);
        } else if (node instanceof Macro) {
            writeMacro(arguments, writer, (Macro)node);
        } else if (node instanceof Document) {
            writeDocument(arguments, writer, (Document)node);
        } else {
            throw new IllegalStateException("Cannot write node of class \"" + node.getClass().getName());
        }
    
    }

    

    
    protected void writeGroupNode(final Arguments arguments, final Writer writer, final GroupNode groupNode) 
            throws IOException {
        
        if (groupNode.hasChildren()) {
            final Node[] children = groupNode.unsafeGetChildrenNodeArray();
            final int childrenLen = groupNode.numChildren();
            for (int i = 0; i < childrenLen; i++) {
                writeNode(arguments, writer, children[i]);
            }
        }
        
    }

    
    
    
    protected void writeElement(final Arguments arguments, final Writer writer, final Element element) 
            throws IOException {
        
        writer.write('<');
        writer.write(element.getOriginalName());
        if (element.hasAttributes()) {
            
            final Configuration configuration = arguments.getConfiguration();
            
            final Attribute[] attributes = element.unsafeGetAttributes();
            final int attributesLen = element.numAttributes();
            
            for (int i = 0; i < attributesLen; i++) {
                
                final Attribute attribute = attributes[i];
                boolean writeAttribute = true;
                
                if (attribute.getNormalizedName().startsWith("xmlns:")) {
                    // xmlns attributes related to thymeleaf-managed prefixes (prefixes assigned to any of the
                    // dialects configured at the template engine) are always removed.
                    final String xmlnsPrefix = attribute.getNormalizedName().substring("xmlns:".length());
                    if (configuration.isPrefixManaged(xmlnsPrefix)) {
                        writeAttribute = false;
                    }
                }

                if (writeAttribute) {
                    
                    writer.write(' ');
                    writer.write(attribute.getOriginalName());
                    
                    final String attrValue = attribute.getValue();
                    if (!(attrValue == null && attribute.isOnlyName())) {
                        writer.write('=');
                        writer.write('\"');
                        if (attrValue != null) {
                            writer.write(DOMUtils.escapeXml(attrValue, true));                        
                        }
                        writer.write('\"');
                    }
                        
                }
            }
        }
        
        /*
         * How to determine if a tag will be written minimized, have a closing tag, etc.
         * 
         * 1. WITH CHILDREN
         *    1.a. Will have a closing tag if:
         *         (representationInTemplate != ONLY_OPEN) // This includes "null"
         *    1.b. Will not have a closing tag if:
         *         (representationInTemplate == ONLY_OPEN)
         *         
         * 2. WITHOUT CHILDREN
         *    2.a. Will be minimized (standalone) if:
         *         (representationInTemplate == STANDALONE) OR
         *         ((representationInTemplate == null OR representationInTemplate == OPEN_AND_CLOSE_NONEMPTY) AND 
         *             (isWeb == false OR isMinimizable == true))
         *    2.b. Will have open and close tag if:
         *         (representationInTemplate == OPEN_AND_CLOSE_EMPTY) OR
         *         ((representationInTemplate == null OR representationInTemplate == OPEN_AND_CLOSE_NONEMPTY) AND 
         *             (isWeb == true AND isMinimizable == false))
         *    2.c. Will have only open tag (no close tag, not XML-well-formed) if:
         *         (representationInTemplate == ONLY_OPEN)       
         *  
         */
        final RepresentationInTemplate representationInTemplate = element.getRepresentationInTemplate();

        if (element.hasChildren()) {
            writer.write('>');
            final Node[] children = element.unsafeGetChildrenNodeArray();
            final int childrenLen = element.numChildren();
            for (int i = 0; i < childrenLen; i++) {
                writeNode(arguments, writer, children[i]);
            }
            if (representationInTemplate != RepresentationInTemplate.ONLY_OPEN) {
                writer.write('<');
                writer.write('/');
                writer.write(element.getOriginalName());
                writer.write('>');
            }
        } else {
            if (representationInTemplate == null || representationInTemplate == RepresentationInTemplate.OPEN_AND_CLOSE_NONEMPTY) {
                if (useXhtmlTagMinimizationRules()) {
                    if (element.isMinimizableIfWeb()) {
                        writer.write(' ');
                        writer.write('/');
                        writer.write('>');
                    } else {
                        writer.write('>');
                        writer.write('<');
                        writer.write('/');
                        writer.write(element.getOriginalName());
                        writer.write('>');
                    }
                } else {
                    writer.write('/');
                    writer.write('>');
                }
            } else if (representationInTemplate == RepresentationInTemplate.OPEN_AND_CLOSE_EMPTY) {
                writer.write('>');
                writer.write('<');
                writer.write('/');
                writer.write(element.getOriginalName());
                writer.write('>');
            } else if (representationInTemplate == RepresentationInTemplate.STANDALONE) {
                if (useXhtmlTagMinimizationRules()) {
                    writer.write(' ');
                }
                writer.write('/');
                writer.write('>');
            } else if (representationInTemplate == RepresentationInTemplate.ONLY_OPEN) {
                writer.write('>');
            }
        }
    }

    
    
    @SuppressWarnings("unused")
    protected void writeCDATASection(final Arguments arguments, final Writer writer, final CDATASection cdataSection) 
            throws IOException {
        writer.write(CDATA_PREFIX);
        writer.write(cdataSection.unsafeGetContentCharArray());
        writer.write(CDATA_SUFFIX);
    }

    
    
    @SuppressWarnings("unused")
    protected void writeComment(final Arguments arguments, final Writer writer, final Comment comment) 
            throws IOException {
        writer.write(COMMENT_PREFIX);
        writer.write(comment.unsafeGetContentCharArray());
        writer.write(COMMENT_SUFFIX);
    }

    
    
    @SuppressWarnings("unused")
    protected void writeText(final Arguments arguments, final Writer writer, final Text text) 
            throws IOException {
        writer.write(text.unsafeGetContentCharArray());
    }

    
    
    @SuppressWarnings("unused")
    protected void writeMacro(final Arguments arguments, final Writer writer, final Macro macro) 
            throws IOException {
        writer.write(macro.unsafeGetContentCharArray());
    }
    

    
    
}
