/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.thymeleaf.dom.CDATASection;
import org.thymeleaf.dom.Comment;
import org.thymeleaf.dom.DocType;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Text;
import org.thymeleaf.templateparser.TemplatePreprocessingReader;

/**
 * <p>
 *   Utility class for converting XML DOM entities to Thymeleaf DOM entities.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 */
public final class StandardDOMTranslator {

    
    private StandardDOMTranslator() {
        super();
    }

    
    
    public static Node translateNode(final org.w3c.dom.Node domNode, final NestableNode parentNode, final String documentName) {
        
        if(domNode instanceof org.w3c.dom.Element) {
            return translateElement((org.w3c.dom.Element)domNode, parentNode, documentName);
        } else if(domNode instanceof org.w3c.dom.Comment) {
            return translateComment((org.w3c.dom.Comment)domNode, parentNode, documentName);
        } else if(domNode instanceof org.w3c.dom.CDATASection) {
            return translateCDATASection((org.w3c.dom.CDATASection)domNode, parentNode, documentName);
        } else if(domNode instanceof org.w3c.dom.Text) {
            return translateText((org.w3c.dom.Text)domNode, parentNode, documentName);
        } else if(domNode instanceof org.w3c.dom.Document) {
            return translateDocument((org.w3c.dom.Document)domNode, documentName);
        } else {
            throw new IllegalArgumentException("Node " + domNode.getNodeName() +
                " of type " + domNode.getNodeType() + " and class " +
                domNode.getClass().getName() + " cannot be converted to " +
                "Thymeleaf's DOM representation.");
        }
        
    }
    

    
    public static Document translateDocument(final org.w3c.dom.Document domDocument, final String documentName) {
        return translateDocument(domDocument, documentName, null);
    }

        
    public static Document translateDocument(final org.w3c.dom.Document domDocument, final String documentName,
            final String originalDocTypeClause) {
        
        final org.w3c.dom.DocumentType domDocumentType = domDocument.getDoctype();
        final org.w3c.dom.NodeList children = domDocument.getChildNodes();
        final Document document = new Document(documentName, translateDocumentType(domDocumentType, originalDocTypeClause));
        
        final String xmlVersion = domDocument.getXmlVersion();
        if (xmlVersion != null) {
            document.setNodeProperty(Node.NODE_PROPERTY_XML_VERSION, xmlVersion);
        }
        
        final String xmlEncoding = domDocument.getXmlEncoding();
        if (xmlEncoding != null) {
            document.setNodeProperty(Node.NODE_PROPERTY_XML_ENCODING, xmlEncoding);
        }
        
        final boolean xmlStandalone = domDocument.getXmlStandalone();
        if (xmlStandalone) {
            document.setNodeProperty(Node.NODE_PROPERTY_XML_STANDALONE, Boolean.TRUE);
        }
        
        final int childrenLen = children.getLength();
        for (int i = 0; i < childrenLen; i++) {
            final org.w3c.dom.Node child = children.item(i);
            if (!(child instanceof org.w3c.dom.DocumentType)) {
                if (child instanceof org.w3c.dom.Element) {
                    final List<Node> childNodes =
                            translateRootElement((org.w3c.dom.Element)child, document, documentName);
                    for (final Node childNode : childNodes) {
                        document.addChild(childNode);
                    }
                } else {
                    document.addChild(translateNode(child, document, documentName));
                }
            }
        }
        return document;
        
    }


    
    public static DocType translateDocumentType(final org.w3c.dom.DocumentType domDocumentType) {
        return translateDocumentType(domDocumentType, null);
    }

    
    
    public static DocType translateDocumentType(
            final org.w3c.dom.DocumentType domDocumentType, final String originalDocTypeClause) {
        
        if (domDocumentType == null) {
            return null;
        }
        
        final String rootElementName = domDocumentType.getName();
        
        return new DocType(
                rootElementName, domDocumentType.getPublicId(),
                domDocumentType.getSystemId(), originalDocTypeClause);
        
    }

    
    
    public static Element translateElement(final org.w3c.dom.Element domNode, final NestableNode parentNode, final String documentName) {

        final String elementTagName = domNode.getTagName();
        
        final Element element = new Element(elementTagName, documentName);
        element.setParent(parentNode);
        
        final org.w3c.dom.NamedNodeMap attributes = domNode.getAttributes();
        final int attributesLen = attributes.getLength();
        for (int i = 0; i < attributesLen; i++) {
            final org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attributes.item(i);
            element.setAttribute(
                    attr.getName(),
                    DOMUtils.unescapeXml(
                            TemplatePreprocessingReader.removeEntitySubstitutions(attr.getValue()),
                            true));
        }
        
        final org.w3c.dom.NodeList children = domNode.getChildNodes();
        final int childrenLen = children.getLength();
        for (int i = 0; i < childrenLen; i++) {
            final org.w3c.dom.Node child = children.item(i);
            element.addChild(translateNode(child, element, documentName));
        }
        
        return element;
        
    }

    
    
    private static List<Node> translateRootElement(final org.w3c.dom.Element domNode,
            final NestableNode parentNode, final String documentName) {

        final String elementTagName = domNode.getTagName();
        
        if (!TemplatePreprocessingReader.SYNTHETIC_ROOT_ELEMENT_NAME.equals(elementTagName)) {
            return Collections.singletonList((Node)translateElement(domNode, parentNode, documentName));
        }

        final org.w3c.dom.NodeList children = domNode.getChildNodes();
        final int childrenLen = children.getLength();
        final List<Node> result = new ArrayList<Node>(childrenLen + 2);
        for (int i = 0; i < childrenLen; i++) {
            final org.w3c.dom.Node child = children.item(i);
            result.add(translateNode(child, parentNode, documentName));
        }
        
        return result;
        
    }
    
    

    public static Comment translateComment(final org.w3c.dom.Comment domNode, final NestableNode parentNode, final String documentName) {
        
        final Comment comment = new Comment(domNode.getData(), documentName);
        comment.setParent(parentNode);
        return comment;
        
    }

    
    
    public static CDATASection translateCDATASection(final org.w3c.dom.CDATASection domNode, final NestableNode parentNode, final String documentName) {
        final CDATASection cdata = 
                new CDATASection(TemplatePreprocessingReader.removeEntitySubstitutions(domNode.getData()), false, documentName);
        cdata.setParent(parentNode);
        return cdata;
    }

    
    
    public static Text translateText(final org.w3c.dom.Text domNode, final NestableNode parentNode, final String documentName) {
        
        final Text text = 
                new Text(TemplatePreprocessingReader.removeEntitySubstitutions(domNode.getData()), false, documentName);
        text.setParent(parentNode);
        return text;
        
    }
    
    
}
