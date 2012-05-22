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
package org.thymeleaf;

import java.util.Set;

import org.thymeleaf.exceptions.ParsingException;
import org.thymeleaf.processor.attr.IAttrProcessor;
import org.thymeleaf.processor.tag.ITagProcessor;
import org.thymeleaf.util.DOMUtils;
import org.thymeleaf.util.Validate;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;




/**
 * <p>
 *   Preprocess template DOM, setting executability flags where required.
 * </p>
 * <p>
 *   During cache storage of a template, all nodes in the template's DOM are scanned
 *   in order to detect which ones the engine can safely skip during normal operation, and
 *   flags are set consequently.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
final class DOMDocumentPreprocessor {
    
    
    /**
     * <p>
     *   Preprocesses the (recently parsed) document and:
     * </p>
     * <ul>
     *   <li>Set executability flags as appropiate for each node.</li>
     *   <li>Remove unneeded xmlns attributes from nodes.</li>
     *   <li>Add required artifacts to non-minimizable nodes so that they are not minimized.</li>
     * </ul>
     * 
     * @param configuration the configuration currently being used for the Template Engine.
     * @param document the document to be preprocessed
     */
    static void preprocess(
            final Configuration configuration, final TemplateMode templateMode, final Document document) {
        
        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.notNull(document, "Document cannot be null");
        
        Element rootElement = null;
        
        final NodeList docChildren = document.getChildNodes();

        if (docChildren.getLength() < 1) {
            throw new ParsingException(
                    "Invalid document structure: no root element found.");
        }
        
        if (docChildren.getLength() == 1) {
            
            rootElement = (Element) docChildren.item(0);
            
        } else if (docChildren.getLength() >= 2) {
            
            for (int i = 1; rootElement == null && i < docChildren.getLength(); i++) {
                if (docChildren.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    rootElement = (Element) docChildren.item(i);
                }
            }
            
            if (rootElement == null) {
                throw new ParsingException(
                        "Invalid document structure: No valid root element found.");
            }
            
        } else {
            throw new ParsingException(
                "Invalid document structure: No more than two top-level elements are allowed " +
                "(either root element, or doctype + root element).");
        }
        
        unsafePreprocessNode(configuration, templateMode, document, rootElement);
        
    }
    
    
    
    private static boolean unsafePreprocessNode(
            final Configuration configuration, final TemplateMode templateMode, final Document document, final Node node) {

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            
            boolean isNodeExecutable = false;
            
            final Element element = (Element) node;
            final String elementName = element.getNodeName();
            final String normalizedElementName =
                (elementName == null? null : elementName.toLowerCase());
            
            final Set<DialectConfiguration> dialectsForTagName = 
                configuration.getDialectsForTagName(normalizedElementName);
            
            if (dialectsForTagName != null) {
                // This tag name is (potentially) processed by some of our dialects 
                final ITagProcessor elementProcessor = configuration.getTagProcessor(dialectsForTagName, element);
                if (elementProcessor != null) {
                    isNodeExecutable = true;
                }
            }

            if (!isNodeExecutable) {

                final NamedNodeMap attributes = element.getAttributes();
                for (int i = 0; !isNodeExecutable && i < attributes.getLength(); i++) {
    
                    final Attr attribute = (Attr) attributes.item(i);
                    final String attributeName = attribute.getName();
                    final String normalizedAttributeName =
                        (attributeName == null? null : attributeName.toLowerCase());
                        
                    final Set<DialectConfiguration> dialectsForAttrName =
                        configuration.getDialectsForAttrName(normalizedAttributeName);
                    
                    if (dialectsForAttrName != null) {
                        // This attr name is (potentially) processed by some of our dialects 
                        final IAttrProcessor attrProcessor = configuration.getAttrProcessor(dialectsForAttrName, element, attribute);
                        if (attrProcessor != null) {
                            isNodeExecutable = true;
                        }
                    }
                    
                }
                
            }


            if (!isNodeExecutable) {
                // We already know this node is not executable.
                DOMExecution.setExecutableNode(node, false);
            }

            
            /*
             * Scan the node's children
             */
            
            boolean isTreeExecutable = isNodeExecutable;
            
            final NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                if (unsafePreprocessNode(configuration, templateMode, document, children.item(i))) {
                    // Tree is executable when at least one child is executable
                    isTreeExecutable = true;
                }
            }

            if (!isTreeExecutable) {
                // There is nothing to execute in this whole tree
                DOMExecution.setExecutableTree(node, false);
            }
            
            
            /*
             * Remove all "xmlns:*" attributes from tags
             */
            for (final String prefix : configuration.getAllPrefixes()) {
                final String xmlNsAttrName = configuration.getXmlNsAttrName(prefix);
                if (!configuration.isLenient(prefix) && xmlNsAttrName != null) {
                    final Attr xmlNsAttr = DOMUtils.findAttribute(xmlNsAttrName, element);
                    if (xmlNsAttr != null) {
                        element.removeAttributeNode(xmlNsAttr);
                    }
                }
            }
            
            
            if (nodeIsEmpty(element) && 
                (templateMode.isXHTML() || templateMode.isHTML5())) {
                
                /*
                 * If we are processing an XHTML template, we have to make sure that
                 * only certain tags get minimized (because the XHTML specification forbids
                 * many tags like <textarea>, <script>, <div> to be minimized as 
                 * <textarea />, <script /> or <div />). For this, we will add spurious
                 * Text nodes to those that cannot be minimized but have no body contents
                 * so that the XML method in the XML Transformer used by OutputHandler
                 * (which has to be used because no XHTML method exists, and the XML
                 * method simply does not work correctly for XHTML) does not minimize them.
                 * 
                 * These spurious text nodes will later be removed by a FilterWriter
                 * attached to the Transformer output.
                 * 
                 * See: http://www.w3.org/TR/xslt-xquery-serialization/#xhtml-output
                 */
                
                if (!Standards.MINIMIZABLE_XHTML_TAGS.contains(normalizedElementName)) {
                    final Text text = document.createTextNode("\uFFFC");
                    element.appendChild(text);
                    DOMExecution.setExecutableNode(text, false);
                }
                
            }
            
            
            
            return isTreeExecutable;
            
        }


        
        /*
         * By default, any non-element node is non-executable.
         * 
         * The only special case in which it could be executable is when an inliner has been
         * set for any of its parents. But in that case, the processor setting the inliner
         * should have cleared the "executability" flag for all the children of the 
         * tag that the inliner is being set at.
         */
        
        DOMExecution.setExecutableNode(node, false);
        return false;
        
        
    }
    

    

    
    
    private static boolean nodeIsEmpty(final Node node) {
        if (!node.hasChildNodes()) {
            return true;
        }
        if (node.getChildNodes().getLength() == 1) {
            if (node.getChildNodes().item(0) instanceof Text) {
                final Text textChild = (Text) node.getChildNodes().item(0);
                final String textContent = textChild.getTextContent();
                return textContent == null || textContent.equals("");
            }
        }
        return false;
    }
    
    
}
