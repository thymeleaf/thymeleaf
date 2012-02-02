package org.thymeleaf.util;

import org.thymeleaf.dom.CDATASection;
import org.thymeleaf.dom.Comment;
import org.thymeleaf.dom.DocType;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Text;
import org.thymeleaf.templateparser.EntitySubstitutionTemplateReader;

/**
 * <p>
 *   Utility class for converting XML DOM entities to Thymeleaf DOM entities.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 */
public class StandardDOMTranslator {

    
    private StandardDOMTranslator() {
        super();
    }

    
    
    public static final Node translateNode(final org.w3c.dom.Node domNode, final NestableNode parentNode, final String documentName) {
        
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
    

    
    public static final Document translateDocument(final org.w3c.dom.Document domDocument, final String documentName) {
        
        final org.w3c.dom.DocumentType domDocumentType = domDocument.getDoctype();
        final org.w3c.dom.NodeList children = domDocument.getChildNodes();
        final Document document = new Document(documentName, translateDocumentType(domDocumentType));
        
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
                document.addChild(translateNode(child, document, documentName));
            }
        }
        return document;
        
    }


    
    public static final DocType translateDocumentType(final org.w3c.dom.DocumentType domDocumentType) {
        
        if (domDocumentType == null) {
            return null;
        }
        
        final String rootElementName = domDocumentType.getName();
        
        return new DocType(
                rootElementName, domDocumentType.getPublicId(),
                domDocumentType.getSystemId());
        
    }

    
    
    public static final Element translateElement(final org.w3c.dom.Element domNode, final NestableNode parentNode, final String documentName) {
        
        final String elementTagName = domNode.getTagName();
        final Element element = new Element(elementTagName, documentName);
        element.setParent(parentNode);
        
        final org.w3c.dom.NamedNodeMap attributes = domNode.getAttributes();
        final int attributesLen = attributes.getLength();
        for (int i = 0; i < attributesLen; i++) {
            final org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attributes.item(i);
            element.setAttribute(
                    attr.getName(), 
                    EntitySubstitutionTemplateReader.removeEntitySubstitutions(attr.getValue()));
        }
        
        final org.w3c.dom.NodeList children = domNode.getChildNodes();
        final int childrenLen = children.getLength();
        for (int i = 0; i < childrenLen; i++) {
            final org.w3c.dom.Node child = children.item(i);
            element.addChild(translateNode(child, element, documentName));
        }
        
        return element;
        
    }
    
    

    public static final Comment translateComment(final org.w3c.dom.Comment domNode, final NestableNode parentNode, final String documentName) {
        
        final Comment comment = new Comment(domNode.getData(), documentName);
        comment.setParent(parentNode);
        return comment;
        
    }

    
    
    public static final CDATASection translateCDATASection(final org.w3c.dom.CDATASection domNode, final NestableNode parentNode, final String documentName) {
        final CDATASection cdata = 
                new CDATASection(EntitySubstitutionTemplateReader.removeEntitySubstitutions(domNode.getData()), false, documentName);
        cdata.setParent(parentNode);
        return cdata;
    }

    
    
    public static final Text translateText(final org.w3c.dom.Text domNode, final NestableNode parentNode, final String documentName) {
        
        final Text text = 
                new Text(EntitySubstitutionTemplateReader.removeEntitySubstitutions(domNode.getData()), false, documentName);
        text.setParent(parentNode);
        return text;
        
    }
    
    
}
