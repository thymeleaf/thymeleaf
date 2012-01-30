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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.exceptions.NoAvailableProcessorException;
import org.thymeleaf.exceptions.ParsingException;
import org.thymeleaf.inliner.ITextInliner;
import org.thymeleaf.processor.SubstitutionTag;
import org.thymeleaf.processor.attr.AttrProcessResult;
import org.thymeleaf.processor.attr.IAttrProcessor;
import org.thymeleaf.processor.tag.ITagProcessor;
import org.thymeleaf.processor.tag.TagProcessResult;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.DOMUtils;
import org.thymeleaf.util.PrefixUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
final class DOMDocumentProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(DOMDocumentProcessor.class);
    

    static DocumentType transform(final Arguments arguments, final TemplateResolution templateResolution, final Document document) {

        final TemplateMode templateMode = templateResolution.getTemplateMode();
        
        final NodeList docChildren = document.getChildNodes();
        
        DocumentType docType = null;
        Element rootElement = null;
        
        if (docChildren.getLength() < 1) {
            throw new ParsingException(
                    "Invalid document structure: no root element found.");
        }
        
        if (docChildren.getLength() == 1) {
            
            if (docChildren.item(0).getNodeType() != Node.ELEMENT_NODE) {
                throw new ParsingException(
                        "Invalid document structure: If only one top-level node exists, this " +
                        "has to be the root element.");
            }
            
            if (templateMode.isXHTML() || templateMode.isHTML5()) {
                throw new ParsingException(
                        "Invalid document structure: Web templates (XHTML/HTML5) must include a DOCTYPE declaration.");
            }
            
            rootElement = (Element) docChildren.item(0);
            
        } else if (docChildren.getLength() >= 2) {
            
            if (docChildren.item(0).getNodeType() != Node.DOCUMENT_TYPE_NODE) {
                throw new ParsingException(
                        "Invalid document structure: If two top-level nodes exist, the first one " +
                        "has to be the DOCTYPE.");
            }
            
            for (int i = 1; rootElement == null && i < docChildren.getLength(); i++) {
                if (docChildren.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    rootElement = (Element) docChildren.item(i);
                }
            }
            
            if (rootElement == null) {
                throw new ParsingException(
                        "Invalid document structure: No valid root element found.");
            }
            
            docType = (DocumentType) docChildren.item(0);
            
            final String publicId = docType.getPublicId();
            final String systemId = docType.getSystemId();
            
            if (templateMode.isHTML5()) {
                if (!((publicId == null || publicId.trim().equals("")) &&
                      (systemId == null || systemId.trim().equals("") || systemId.trim().equalsIgnoreCase(Standards.HTML_5_LEGACY_WILDCARD_SYSTEMID.getValue())))) {
                    throw new ParsingException(
                            "Template is being processed in " + templateMode + " mode. Only " +
                            "\"<!DOCTYPE html>\" and \"<!DOCTYPE html SYSTEM \"" + Standards.HTML_5_LEGACY_WILDCARD_SYSTEMID + "\">\" " +
                            "are allowed."); 
                }
            }
            if (templateMode.isXHTML()) {
                if (systemId == null || systemId.trim().equals("")) {
                      throw new ParsingException(
                              "Template is being processed in " + templateMode + " mode. A " +
                              "correct 'PUBLIC' or 'SYSTEM' DOCTYPE declaration is required."); 
                  }
            }
            
        }
        

        /*
         * Start traversing the node tree and applying transformations
         */
        transformNode(arguments, templateResolution, document, rootElement);
        
        return docType;
        
    }

    
    
    
    private static void transformNode(final Arguments arguments,
            final TemplateResolution templateResolution, 
            final Document document, final Node node) {
        
        final Configuration configuration = arguments.getConfiguration();
        
        
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            
            
            /*
             * We should only go on if this tree is executable
             */
            if (DOMExecution.isExecutableTree(node)) {

                final Element element = (Element) node;
                final String elementName = element.getNodeName();
                final String normalizedElementName = 
                    (elementName == null? null : elementName.toLowerCase());
                final String elementPrefix = PrefixUtils.getPrefix(normalizedElementName);
                
                boolean tagIsRemoved = false;
                boolean tagChildrenAreRemoved = false;
                boolean tagIsSubstituted = false;
                
                boolean textInlinerSet = false;
                ITextInliner textInliner = null;
                
                final Map<String,Object> localVariables = new LinkedHashMap<String,Object>();
                final List<SubstitutionTag> substitutionTags = new ArrayList<SubstitutionTag>();
                Object selectionTarget = null;
                boolean selectionTargetSet = false;

                
                /*
                 * If this node (just the node) is not executable, skip trying to
                 * process the tag or any of its attributes.
                 */
                if (DOMExecution.isExecutableNode(element)) {

                    
                    final Set<DialectConfiguration> dialectsForTagName = 
                        configuration.getDialectsForTagName(normalizedElementName);
                    
                    if (dialectsForTagName != null) {
                        // This tag name is (potentially) processed by some of our dialects 
                        
                        final ITagProcessor elementProcessor = configuration.getTagProcessor(dialectsForTagName, element);
                        if (elementProcessor == null) {
                            
                            if (configuration.isPrefixManaged(elementPrefix) && !configuration.isLenient(elementPrefix)) {
                                throw new NoAvailableProcessorException(
                                        "No processor in dialect found for tag \"" + normalizedElementName + "\"");
                            }
                            
                        } else {
                            
                            if (logger.isTraceEnabled()) {
                                logger.trace("[THYMELEAF][{}] TAG: Processing tag: \"{}\"", TemplateEngine.threadIndex(), normalizedElementName);
                            }
                            
                            final TagProcessResult tagProcessResult =
                                elementProcessor.process(arguments, templateResolution, document, element);
                            tagIsRemoved = tagProcessResult.getAction().isTagRemoved();
                            tagChildrenAreRemoved = tagProcessResult.getAction().isChildrenRemoved();
                            tagIsSubstituted = tagProcessResult.getAction().isTagSubstituted();
                            localVariables.putAll(tagProcessResult.getLocalVariables());
                            substitutionTags.addAll(tagProcessResult.getSubstitutionTags());
                            if (tagProcessResult.isSelectionTargetSet()) {
                                selectionTargetSet = true;
                                selectionTarget = tagProcessResult.getSelectionTarget();
                            }
                            if (tagProcessResult.isTextInlinerSet()) {
                                textInlinerSet = true;
                                textInliner = tagProcessResult.getTextInliner();
                            }
                            
                        }
                        
                    } else if (configuration.isPrefixManaged(elementPrefix) && !configuration.isLenient(elementPrefix)) {
                        throw new NoAvailableProcessorException(
                                "No processor in dialect found for tag \"" + normalizedElementName + "\"");
                    }
    
                    
                    /*
                     * If tag is not removed after tag execution, we should process attributes
                     * Also, if tag has been marked as non executable by the tag processor, no further process should be done
                     */
                    if (!tagIsRemoved && DOMExecution.isExecutableNode(element)) {
                        
                        final Map<String,IAttrProcessor> attrProcessorsToExecute = new LinkedHashMap<String,IAttrProcessor>(); 
    
                        /*
                         * First, we create a list with the attr processors that should be executed.
                         */
                        final NamedNodeMap attributes = element.getAttributes();
                        for (int i = 0; i < attributes.getLength(); i++) {
    
                            final Attr attribute = (Attr) attributes.item(i);
                            final String attributeName = attribute.getName();
                            final String normalizedAttributeName = 
                                (attributeName == null? null : attributeName.toLowerCase());
                            final String attributePrefix = PrefixUtils.getPrefix(normalizedAttributeName);
                            
                            final Set<DialectConfiguration> dialectsForAttrName =
                                configuration.getDialectsForAttrName(normalizedAttributeName);
                            
                            if (dialectsForAttrName != null) {
                                // This attr name is (potentially) processed by some of our dialects 
                                
                                final IAttrProcessor attrProcessor = configuration.getAttrProcessor(dialectsForAttrName, element, attribute);
                                if (attrProcessor != null) {
                                    attrProcessorsToExecute.put(normalizedAttributeName, attrProcessor);
                                } else if (configuration.isPrefixManaged(attributePrefix) && !configuration.isLenient(attributePrefix)) {
                                    throw new NoAvailableProcessorException(
                                            "No processor in dialect found for attribute \"" + normalizedAttributeName + "\" with value = \"" + attribute.getValue() + "\"");
                                }
                                
                            } else if (configuration.isPrefixManaged(attributePrefix) && !configuration.isLenient(attributePrefix)) {
                                throw new NoAvailableProcessorException(
                                        "No processor in dialect found for attribute \"" + normalizedAttributeName + "\" with value = \"" + attribute.getValue() + "\"");
                            }
                            
                        }
                        
                        /*
                         * Attr processors are sorted according to their precedence
                         */
                        final Map<String,IAttrProcessor> orderedAttrProcessorsToExecute = 
                            new TreeMap<String,IAttrProcessor>(
                                    new AttrProcessorMapComparator(attrProcessorsToExecute));
                        orderedAttrProcessorsToExecute.putAll(attrProcessorsToExecute);
    
                        /*
                         * Once ordered, attr processors are executed on their corresponding attribute nodes
                         */
                        for (final Map.Entry<String,IAttrProcessor> attrProcessorEntry :  orderedAttrProcessorsToExecute.entrySet()) {
    
                            if (tagIsRemoved || !DOMExecution.isExecutableNode(element)) {
                                break;
                            }
                            
                            final String attributeName = attrProcessorEntry.getKey(); // it's already normalized
                            final IAttrProcessor attrProcessor = attrProcessorEntry.getValue();
    
                            final Attr attribute = DOMUtils.findAttribute(attributeName, element);
                            
                            if (logger.isTraceEnabled()) {
                                logger.trace("[THYMELEAF][{}] ATTRIBUTE: Processing attribute: \"{}\" with value \"{}\"", new Object[] {TemplateEngine.threadIndex(), attributeName, attribute.getValue()});
                            }
                            
                            /*
                             * Compute the Arguments object to be used for attribute evaluation
                             */
                            Arguments attrArguments = null;
                            if (localVariables.isEmpty()) {
                                if (!selectionTargetSet) {
                                    attrArguments = arguments;
                                } else {
                                    attrArguments = arguments.setSelectionTarget(selectionTarget);
                                }
                            } else {
                                if (!selectionTargetSet) {
                                    attrArguments = arguments.addLocalVariables(localVariables);
                                } else {
                                    attrArguments = arguments.addLocalVariablesAndSetSelectionTarget(localVariables, selectionTarget);
                                }
                            }
                            
    
                            final AttrProcessResult attrProcessResult =
                                attrProcessor.process(attrArguments, templateResolution, document, element, attribute);
                            tagIsRemoved = attrProcessResult.getAction().isTagRemoved();
                            tagChildrenAreRemoved = attrProcessResult.getAction().isChildrenRemoved();
                            tagIsSubstituted = attrProcessResult.getAction().isTagSubstituted();
                            localVariables.putAll(attrProcessResult.getLocalVariables());
                            substitutionTags.addAll(attrProcessResult.getSubstitutionTags());
                            if (attrProcessResult.isSelectionTargetSet()) {
                                selectionTargetSet = true;
                                selectionTarget = attrProcessResult.getSelectionTarget();
                            }
                            if (attrProcessResult.isTextInlinerSet()) {
                                textInlinerSet = true;
                                textInliner = attrProcessResult.getTextInliner();
                            }
                            
                            if (attrProcessResult.getAction().isAttrRemoved()) {
                                // We try to obtain the Attr object again because it could have
                                // been removed by the execution of the AttrProcessor
                                final Attr removedAttribute = DOMUtils.findAttribute(attributeName, element);
                                if (removedAttribute != null) {
                                    element.removeAttributeNode(removedAttribute);
                                }
                            }
                            
                        }
                        
                    }
                
                }

                
                /*
                 * Compute the Arguments object to be used for children evaluation
                 */
                Arguments childrenArguments = null;
                if (localVariables.isEmpty()) {
                    if (!selectionTargetSet) {
                        if (!textInlinerSet) {
                            childrenArguments = arguments;
                        } else {
                            childrenArguments = arguments.setTextInliner(textInliner);
                        }
                    } else {
                        if (!textInlinerSet) {
                            childrenArguments = arguments.setSelectionTarget(selectionTarget);
                        } else {
                            childrenArguments = arguments.setTextInlinerAndSetSelectionTarget(textInliner, selectionTarget);
                        }
                    }
                } else {
                    if (!selectionTargetSet) {
                        if (!textInlinerSet) {
                            childrenArguments = arguments.addLocalVariables(localVariables);
                        } else {
                            childrenArguments = arguments.addLocalVariablesAndTextInliner(localVariables, textInliner);
                        }
                    } else {
                        if (!textInlinerSet) {
                            childrenArguments = arguments.addLocalVariablesAndSetSelectionTarget(localVariables, selectionTarget);
                        } else {
                            childrenArguments = arguments.addLocalVariablesAndTextInlinerAndSetSelectionTarget(localVariables, textInliner, selectionTarget);
                        }
                    }
                }
                
                if (!tagIsRemoved) {

                    if (tagChildrenAreRemoved) {

                        final NodeList children = node.getChildNodes();
                        final List<Node> childNodes = new ArrayList<Node>();
                        for (int i = 0; i < children.getLength(); i++) {
                            // In case nodes are deleted along the way, we create a list in order not
                            // to have to rely on the NodeList object.
                            childNodes.add(children.item(i));
                        }
                        for (final Node child : childNodes) {
                            element.removeChild(child);
                        }
                        
                    } else {
        
                        final NodeList children = node.getChildNodes();
                        final List<Node> childNodes = new ArrayList<Node>();
                        for (int i = 0; i < children.getLength(); i++) {
                            // In case nodes are deleted along the way, we create a list in order not
                            // to have to rely on the NodeList object.
                            childNodes.add(children.item(i));
                        }
                        for (final Node child : childNodes) {
                            transformNode(childrenArguments, templateResolution, document, child);
                        }
                        
                    }
                    
                } else {

                    if (tagChildrenAreRemoved) {

                        final NodeList children = node.getChildNodes();
                        final List<Node> childNodes = new ArrayList<Node>();
                        for (int i = 0; i < children.getLength(); i++) {
                            // In case nodes are deleted along the way, we create a list in order not
                            // to have to rely on the NodeList object.
                            childNodes.add(children.item(i));
                        }
                        for (final Node child : childNodes) {
                            element.removeChild(child);
                        }
                        
                        if (tagIsSubstituted) {

                            for (final SubstitutionTag substitutionTag : substitutionTags) {

                                element.getParentNode().insertBefore(substitutionTag.getNode(), element);
                            }
                            
                            for (final SubstitutionTag substitutionTag : substitutionTags) {
                                
                                final Map<String,Object> substitutionLocalVariables = new LinkedHashMap<String, Object>();
                                substitutionLocalVariables.putAll(localVariables);
                                substitutionLocalVariables.putAll(substitutionTag.getLocalVariables());
                                
                                /*
                                 * Compute the Arguments object to be used for substitution evaluation
                                 */
                                Arguments substitutionArguments = null;
                                if (substitutionLocalVariables.isEmpty()) {
                                    if (!selectionTargetSet) {
                                        if (!textInlinerSet) {
                                            substitutionArguments = arguments;
                                        } else {
                                            substitutionArguments = arguments.setTextInliner(textInliner);
                                        }
                                    } else {
                                        if (!textInlinerSet) {
                                            substitutionArguments = arguments.setSelectionTarget(selectionTarget);
                                        } else {
                                            substitutionArguments = arguments.setTextInlinerAndSetSelectionTarget(textInliner, selectionTarget);
                                        }
                                    }
                                } else {
                                    if (!selectionTargetSet) {
                                        if (!textInlinerSet) {
                                            substitutionArguments = arguments.addLocalVariables(substitutionLocalVariables);
                                        } else {
                                            substitutionArguments = arguments.addLocalVariablesAndTextInliner(substitutionLocalVariables, textInliner);
                                        }
                                    } else {
                                        if (!textInlinerSet) {
                                            substitutionArguments = arguments.addLocalVariablesAndSetSelectionTarget(substitutionLocalVariables, selectionTarget);
                                        } else {
                                            substitutionArguments = arguments.addLocalVariablesAndTextInlinerAndSetSelectionTarget(substitutionLocalVariables, textInliner, selectionTarget);
                                        }
                                    }
                                }
                                
                                transformNode(substitutionArguments, templateResolution, document, substitutionTag.getNode());
                                
                            }
                                
                        }
                        
                        element.getParentNode().removeChild(element);
                        
                        
                    } else {
                        
                        final NodeList children = node.getChildNodes();
                        final List<Node> childNodes = new ArrayList<Node>();
                        for (int i = 0; i < children.getLength(); i++) {
                            // In case nodes are deleted along the way, we create a list in order not
                            // to have to rely on the NodeList object.
                            childNodes.add(children.item(i));
                        }
                        for (final Node child : childNodes) {
                            element.removeChild(child);
                            element.getParentNode().insertBefore(child, element);
                        }
                        element.getParentNode().removeChild(element);
                        for (final Node child : childNodes) {
                            transformNode(childrenArguments, templateResolution, document, child);
                        }
                        
                    }
                    
                }
                
            
                
                if (!tagIsRemoved &&
                    nodeIsEmpty(element) && 
                    (templateResolution.getTemplateMode().isXHTML() || templateResolution.getTemplateMode().isHTML5())) {
                    
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
                    }
                    
                }
                

            }

            
        } else if (arguments.hasTextInliner() && 
                   (node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.CDATA_SECTION_NODE)) {
            
            /*
             * We check the text node has not been generated during template processing
             * from evaluating a variable, so that we avoid code injection.
             */
            if (DOMExecution.isExecutableNode(node)) {
                // The cast to Text is allowed because the CDATASection interface extends from Text
                arguments.getTextInliner().inline(arguments, templateResolution, (Text)node);
            }
            
        }

        
        
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
    


    
    
    private DOMDocumentProcessor() {
        super();
    }

    
    
    
    
    
    private static class AttrProcessorMapComparator implements Comparator<String> {

        private final Map<String,IAttrProcessor> map;
        
        AttrProcessorMapComparator(final Map<String,IAttrProcessor> map) {
            super();
            this.map = map;
        }
        
        public int compare(final String key1, final String key2) {
            final int result = this.map.get(key1).compareTo(this.map.get(key2)); 
            if (result == 0) {
                // If IAttrProcessors have the same precedence, just order them
                // sequentially (returning 0 would result in a map entry being removed!)
                return 1;
            }
            return result;
        }
        
    }
    
    
}
