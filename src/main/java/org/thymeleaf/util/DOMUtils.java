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
package org.thymeleaf.util;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.TemplateRepository;
import org.thymeleaf.context.Context;
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.NestableAttributeHolderNode;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateOutputException;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.resourceresolver.ClassLoaderResourceResolver;
import org.thymeleaf.templatemode.StandardTemplateModeHandlers;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateparser.StandardTemplateParser;
import org.thymeleaf.templateparser.html.LegacyHtml5TemplateParser;
import org.thymeleaf.templateresolver.AlwaysValidTemplateResolutionValidity;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.templatewriter.AbstractGeneralTemplateWriter;
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
    // &apos; will not be used as it is considered an XML entity, but not
    // an HTML one. This causes problems in Internet Explorer <= 8.
    private static final char[] APOS = "&#39;".toCharArray();
    
    
    
    
    
    public static List<Node> extractFragmentByElementAndAttributeValue(
            final List<Node> rootNodes, final String elementName, final String attributeName, final String attributeValue) {

        Validate.notNull(rootNodes, "Root node list cannot be null");
        // Element name, attribute name and attribute value CAN be null 
        // (in that case, all elements will be searched)

        final String normalizedElementName = Element.normalizeElementName(elementName);
        final String normalizedAttributeName = Attribute.normalizeAttributeName(attributeName);
        
        final List<Node> fragmentNodes = new ArrayList<Node>(5);
        for (final Node rootNode : rootNodes) {
            final List<Node> extraction = 
                    extractFragmentFromNode(rootNode, normalizedElementName, normalizedAttributeName, attributeValue);
            if (extraction != null) {
                fragmentNodes.addAll(extraction);
            }
        }
        return fragmentNodes;
        
    }

    
    
    private static List<Node> extractFragmentFromNode(
            final Node node, final String normalizedElementName, final String normalizedAttributeName, final String attributeValue) {
        
        if (node instanceof NestableNode) {

            final NestableNode nestableNode = (NestableNode) node;
            
            /*
             * First check the element itself
             */
            if (nestableNode instanceof Element) {
                final Element element = (Element) nestableNode;
                if (normalizedElementName == null || normalizedElementName.equals(element.getNormalizedName())) {
                    if (normalizedAttributeName != null) {
                        if (element.hasNormalizedAttribute(normalizedAttributeName)) {
                            final String elementAttrValue = element.getAttributeValue(normalizedAttributeName);
                            if (elementAttrValue != null && elementAttrValue.trim().equals(attributeValue)) {
                                return Collections.singletonList((Node)nestableNode);
                            }
                        }
                    } else {
                        return Collections.singletonList((Node)nestableNode);
                    }
               }
            } else if (nestableNode instanceof NestableAttributeHolderNode) {
                final NestableAttributeHolderNode attributeHolderNode = (NestableAttributeHolderNode) nestableNode;
                // If not null, an element without name can never be selectable
                if (normalizedElementName == null) {
                    if (normalizedAttributeName != null) {
                        if (attributeHolderNode.hasNormalizedAttribute(normalizedAttributeName)) {
                            final String elementAttrValue = attributeHolderNode.getAttributeValue(normalizedAttributeName);
                            if (elementAttrValue != null && elementAttrValue.trim().equals(attributeValue)) {
                                return Collections.singletonList((Node)nestableNode);
                            }
                        }
                    } else {
                        return Collections.singletonList((Node)nestableNode);
                    }
                }
            }
            
            /*
             * If element does not match itself, try children
             */
            final List<Node> extraction = new ArrayList<Node>(5);
            final List<Node> children = nestableNode.getChildren();
            for (final Node child : children) {
                final List<Node> childResult =
                        extractFragmentFromNode(
                                child, normalizedElementName, normalizedAttributeName, attributeValue);
                if (childResult != null) {
                    extraction.addAll(childResult);
                }
            }
            return extraction;
            
        }
        
        return null;
        
    }


    /**
     *
     * @param ch ch
     * @param escapeQuotes escapeQuotes
     * @return the result
     * @throws IOException IOException
     * @deprecated This method has been deprecated in 2.1.3 and is no longer an adequate way to obtain escaped
     *             HTML code. The whole of the escaping mechanism defined on top of these DOMUtils escape/unescape
     *             methods had too many drawbacks and issues, so it was completely replaced by the Unbescape library.
     *             Will be removed in 3.0.
     */
    @Deprecated
    public static char[] escapeXml(final char[] ch, final boolean escapeQuotes) throws IOException {
        
        if (ch == null) {
            return null;
        }

        for (final char c : ch) {
            if (c == '&' || c == '<' || c == '>' || (escapeQuotes && (c == '\'' || c == '\"'))) {
                final CharArrayWriter writer = new CharArrayWriter();
                writeXmlEscaped(ch, writer, escapeQuotes);
                return writer.toCharArray();
            }
        }
        
        return ch;
        
    }


    /**
     *
     * @param str str
     * @param escapeQuotes escapeQuotes
     * @return the result
     * @throws IOException IOException
     * @deprecated This method has been deprecated in 2.1.3 and is no longer an adequate way to obtain escaped
     *             HTML code. The whole of the escaping mechanism defined on top of these DOMUtils escape/unescape
     *             methods had too many drawbacks and issues, so it was completely replaced by the Unbescape library.
     *             Will be removed in 3.0.
     */
    @Deprecated
    public static String escapeXml(final String str, final boolean escapeQuotes) throws IOException {
        
        if (str == null) {
            return null;
        }
        
        final int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            final char c = str.charAt(i);
            if (c == '&' || c == '<' || c == '>' || (escapeQuotes && (c == '\'' || c == '\"'))) {
                final StringWriter writer = new StringWriter();
                writeXmlEscaped(str, writer, escapeQuotes);
                return writer.toString();
            }
        }
        
        return str;
        
    }



    /**
     *
     * @param buffer buffer
     * @param writer writer
     * @param escapeQuotes escapeQuotes
     * @throws IOException IOException
     * @deprecated This method has been deprecated in 2.1.3 and is no longer an adequate way to obtain escaped
     *             HTML code. The whole of the escaping mechanism defined on top of these DOMUtils escape/unescape
     *             methods had too many drawbacks and issues, so it was completely replaced by the Unbescape library.
     *             Will be removed in 3.0.
     */
    @Deprecated
    public static void writeXmlEscaped(final String buffer, final Writer writer, final boolean escapeQuotes) throws IOException {
        if (buffer == null) {
            return;
        }
        writeXmlEscaped(buffer.toCharArray(), writer, escapeQuotes);
    }


    /**
     *
     * @param buffer buffer
     * @param writer writer
     * @param escapeQuotes escapeQuotes
     * @throws IOException IOException
     * @deprecated This method has been deprecated in 2.1.3 and is no longer an adequate way to obtain escaped
     *             HTML code. The whole of the escaping mechanism defined on top of these DOMUtils escape/unescape
     *             methods had too many drawbacks and issues, so it was completely replaced by the Unbescape library.
     *             Will be removed in 3.0.
     */
    @Deprecated
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
                if (isEntityStart(buffer, i)) {
                    // This avoids escaping &'s that are in fact starting 
                    // already escaped entities.
                    writer.write('&');
                } else {
                    writer.write(AMP);
                }
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
    

    
    private static boolean isEntityStart(final char[] buffer, final int position) {
        int i = position + 1;
        while (i < buffer.length) {
            final char c = buffer[i];
            if (!( (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || (c == ';') )) {
                return false;
            }
            if (c == ';') {
                return true;
            }
            i++;
        }
        return false;
    }



    /**
     *
     * @param str str
     * @param unescapeQuotes unescapeQuotes
     * @return the result
     * @deprecated This method has been deprecated in 2.1.3 and is no longer an adequate way to obtain unescaped
     *             HTML code. The whole of the escaping mechanism defined on top of these DOMUtils escape/unescape
     *             methods had too many drawbacks and issues, so it was completely replaced by the Unbescape library.
     *             Will be removed in 3.0.
     */
    @Deprecated
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
        }
        if (len == 4 && c1 == 'g' && c2 == 't') {
            return ">";
        }
        if (len == 5 && c1 == 'a' && c2 == 'm' && str.charAt(off + 3) == 'p') {
            return "&";
        }
        if (len == 6 && unescapeQuotes && c1 == 'q' && c2 == 'u' && str.charAt(off + 3) == 'o' && str.charAt(off + 4) == 't') {
            return "\"";
        }
        if (len == 6 && unescapeQuotes && c1 == 'a' && c2 == 'p' && str.charAt(off + 3) == 'o' && str.charAt(off + 4) == 's') {
            return "\'";
        }
        if (len == 5 && c1 == '#') {
            final char c3 = str.charAt(off + 3);
            if (c2 == '6' && c3 == '0') {
                return "<";
            } else if (c2 == '6' && c3 == '2') {
                return ">";
            } else if (c2 == '3' && c3 == '8') {
                return "&";
            } else if (unescapeQuotes && c2 == '3' && c3 == '4') {
                return "\"";
            } else if (unescapeQuotes && c2 == '3' && c3 == '9') {
                return "\'";
            }
        }
        return str.substring(off, (off + len));
        
    }
    
    
    
    
    public static String getXmlFor(final Node node) {
        return getOutputFor(node, new XmlTemplateWriter(), "XML");
    }
    
    
    public static String getXhtmlFor(final Node node) {
        return getOutputFor(node, new XhtmlHtml5TemplateWriter(), "XHTML");
    }
    
    
    public static String getHtml5For(final Node node) {
        return getOutputFor(node, new XhtmlHtml5TemplateWriter(), "HTML5");
    }
    
    

    
    /**
     * <p>
     *   This method is for testing purposes only! It creates mock configuration, template and resource
     *   resolution artifacts, etc. Its usage during normal operation could heavily affect performance.
     * </p>
     * 
     * @param node the node which output is to be computed
     * @param templateWriter the template writter to be used for creating the output
     * @param templateMode the template mode to be used for creating the output
     * @return the computed output
     * @since 2.0.8
     */
    public static String getOutputFor(final Node node, final AbstractGeneralTemplateWriter templateWriter, final String templateMode) {
        
        Validate.notNull(node, "Node cannot be null");
        Validate.notNull(templateWriter, "Template writer cannot be null");

        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addTemplateResolver(new ClassLoaderTemplateResolver());
        templateEngine.addMessageResolver(new StandardMessageResolver());
        templateEngine.setTemplateModeHandlers(StandardTemplateModeHandlers.ALL_TEMPLATE_MODE_HANDLERS);
        
        final String templateName = "output";

        final TemplateProcessingParameters templateProcessingParameters = 
                new TemplateProcessingParameters(templateEngine.getConfiguration(), templateName, new Context());
        
        final TemplateResolution templateResolution = 
                new TemplateResolution(templateName, "resource:"+templateName, 
                        new ClassLoaderResourceResolver(), "UTF-8", templateMode, new AlwaysValidTemplateResolutionValidity());

        templateEngine.initialize();
        
        final TemplateRepository templateRepository = templateEngine.getTemplateRepository();
        
        final Document document = new Document(templateName);
        document.addChild(node);
        
        
        final Arguments arguments = 
                new Arguments(new TemplateEngine(), 
                        templateProcessingParameters, templateResolution, 
                        templateRepository, document);

        return getOutputFor(arguments, node, templateWriter);
            
    }


    
    
    /**
     * 
     * @param arguments arguments
     * @param node node
     * @param templateWriter templateWriter
     * @return the result
     * @since 2.0.8
     */
    public static String getOutputFor(final Arguments arguments, final Node node, 
            final AbstractGeneralTemplateWriter templateWriter) {

        Validate.notNull(arguments, "Arguments cannot be null");
        Validate.notNull(node, "Node cannot be null");
        Validate.notNull(templateWriter, "Template writer cannot be null");
        
        try {
            
            final StringWriter writer = new StringWriter();
            templateWriter.writeNode(arguments, writer, node);
            return writer.toString();
            
        } catch (final IOException e) {
            throw new TemplateOutputException(
                    "Exception during creation of output for node", e);
        }
        
    }


    
    
    /**
     *
     * @param source source
     * @return the result
     * @since 2.0.8
     * @deprecated should be removed (or replaced by a getDOMFor or something similar, if applies) before 3.0 is out
     */
    @Deprecated
    public static Document getHtml5DOMFor(final Reader source) {
        Validate.notNull(source, "Source cannot be null");
        return getDOMFor(source, StandardTemplateParser.INSTANCE);
    }
    
    
    /**
     *
     * @param source source
     * @return the result
     * @since 2.0.8
     * @deprecated should be removed (or replaced by a getDOMFor or something similar, if applies) before 3.0 is out
     */
    @Deprecated
    public static Document getLegacyHTML5DOMFor(final Reader source) {
        Validate.notNull(source, "Source cannot be null");
        return getDOMFor(source, new LegacyHtml5TemplateParser("LEGACYHTML5", 1));
    }
    
    
    /**
     *
     * @param source source
     * @return the result
     * @since 2.0.8
     * @deprecated should be removed (or replaced by a getDOMFor or something similar, if applies) before 3.0 is out
     */
    @Deprecated
    public static Document getXmlDOMFor(final Reader source) {
        Validate.notNull(source, "Source cannot be null");
        return getDOMFor(source, StandardTemplateParser.INSTANCE);
    }
    
    
    /**
     *
     * @param source source
     * @return the result
     * @since 2.0.8
     * @deprecated should be removed (or replaced by a getDOMFor or something similar, if applies) before 3.0 is out
     */
    @Deprecated
    public static Document getXhtmlDOMFor(final Reader source) {
        Validate.notNull(source, "Source cannot be null");
        return getDOMFor(source, StandardTemplateParser.INSTANCE);
    }
    

    
    /**
     * 
     * @param source source
     * @param parser parser
     * @return the result
     * @since 2.0.8
     */
    public static Document getDOMFor(final Reader source, final ITemplateParser parser) {

        Validate.notNull(source, "Source cannot be null");
        Validate.notNull(parser, "Template parser cannot be null");

        final Configuration configuration = new Configuration();
        configuration.addTemplateResolver(new ClassLoaderTemplateResolver());
        configuration.addMessageResolver(new StandardMessageResolver());
        configuration.setTemplateModeHandlers(StandardTemplateModeHandlers.ALL_TEMPLATE_MODE_HANDLERS);
        configuration.initialize();

        return getDOMFor(configuration, source, parser);
        
    }
    
    
    
    
    /**
     * 
     * @param configuration configuration
     * @param source source
     * @param parser parser
     * @return the result
     * @since 2.0.8
     */
    public static Document getDOMFor(final Configuration configuration, 
            final Reader source, final ITemplateParser parser) {

        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(source, "Source cannot be null");
        Validate.notNull(parser, "Template parser cannot be null");
        
        try {
            
            return parser.parseTemplate(configuration, "input", source);
            
        } catch (final Exception e) {
            throw new TemplateInputException(
                    "Exception during parsing of source", e);
        }
        
    }
    

    
    
    private DOMUtils() {
        super();
    }

    
}

