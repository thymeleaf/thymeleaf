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
package org.thymeleaf.processor.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.thymeleaf.Arguments;
import org.thymeleaf.ParsedTemplate;
import org.thymeleaf.exceptions.AttrProcessorException;
import org.thymeleaf.processor.SubstitutionTag;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.DOMUtils;
import org.thymeleaf.util.Validate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public abstract class AbstractFragmentTagProcessor 
        extends AbstractTagProcessor {

    
    
    
    public AbstractFragmentTagProcessor() {
        super();
    }


    
    
    
    public final TagProcessResult process(final Arguments arguments,
            final TemplateResolution templateResolution, final Document document,
            final Element element) {
        
        final boolean substituteInclusionNode =
            getSubstituteInclusionNode(arguments, templateResolution, document, element);
        
        final List<Node> newNodes = 
            getNewNodes(arguments, templateResolution, document, element, substituteInclusionNode);
        
        if (!substituteInclusionNode) {
            
            final NodeList currentElementChildren = element.getChildNodes();
            final int childrenLen = currentElementChildren.getLength();
            for (int i = childrenLen - 1; i >= 0; i--) {
                element.removeChild(currentElementChildren.item(i));
            }
            
            for (final Node newChild : newNodes) {
                element.appendChild(newChild);
            }
            
            return TagProcessResult.NO_ACTION;
            
        }

        // Inclusion tag will be substituted by the new nodes
        
        final List<SubstitutionTag> substitutionTags = new ArrayList<SubstitutionTag>();
        for (final Node substitute : newNodes) {
            substitutionTags.add(SubstitutionTag.forNode(substitute));
        }
        
        return TagProcessResult.forSubstituteTag(substitutionTags);
            
    }

    
    
    
    private final List<Node> getNewNodes(
            final Arguments arguments, final TemplateResolution templateResolution, 
            final Document document, final Element element, 
            final boolean substituteInclusionNode) {
        

        final AbstractFragmentSpec fragmentSpec =
            getFragmentSpec(arguments, templateResolution, document, element);
        if (fragmentSpec == null) {
            throw new AttrProcessorException("Null value for \"" +
                        element.getTagName() + "\" fragment specification not allowed");
        }
        
        final Node fragmentNode = getFragment(arguments, element, fragmentSpec); 
        
        if (fragmentNode == null) {
            throw new AttrProcessorException(
                    "An error happened during parsing of include: \"" + element.getTagName() + "\": fragment node is null");
        }

        try {
            
            // Imports the nodes from the fragment document to the template one
            final Node node = document.importNode(fragmentNode, true);
            
            if (substituteInclusionNode) {
                return Collections.singletonList(node);
            }
            
            final List<Node> children = new ArrayList<Node>();
            final NodeList nodeList = node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                children.add(nodeList.item(i));
            }
            
            return children;
            
        } catch (final Exception e) {
            throw new AttrProcessorException(
                    "An error happened during parsing of include: \"" + element.getTagName() + "\"", e);
        }
        
    }

    
    
    private static Node getFragment(
            final Arguments arguments, final Element element, final AbstractFragmentSpec fragmentSpec) {

        
        final String fragmentTemplateName = fragmentSpec.getFragmentTemplateName();
        
        if (arguments.getTemplateName().equals(fragmentTemplateName)) {
            throw new AttrProcessorException(
                    "Template \"" + arguments.getTemplateName() + 
                    " references itself from a " +
                    "\"" + element.getTagName() + "\" attribute, which is forbidden.");
        }
        
        try {
            
            final Arguments fragmentArguments = 
                new Arguments(
                        arguments.getConfiguration(), 
                        arguments.getTemplateParser(), 
                        fragmentTemplateName, 
                        arguments.getContext(),
                        arguments.getLocalVariables(),
                        arguments.getSelectionTarget(),
                        arguments.getIdCounts(),
                        arguments.getTextInliner());
            
            final ParsedTemplate parsedTemplate = arguments.getTemplateParser().parseDocument(fragmentArguments);
            
            Node fragmentNode = null; 
            if (fragmentSpec instanceof NamedFragmentSpec) {
                
                final NamedFragmentSpec namedFragmentSpec = (NamedFragmentSpec) fragmentSpec;
                final String fragmentTagName = namedFragmentSpec.getFragmentTagName();
                final String fragmentAttributeName = namedFragmentSpec.getFragmentAttributeName();
                final String fragmentAttributeValue = namedFragmentSpec.getFragmentAttributeValue();
                
                fragmentNode = 
                    DOMUtils.extractFragmentByAttributevalue(parsedTemplate.getDocument(), fragmentTagName, fragmentAttributeName, fragmentAttributeValue);
                                        
                if (fragmentNode == null) {
                    throw new AttrProcessorException(
                            "Fragment \"" + fragmentAttributeValue + "\" in template \"" + fragmentTemplateName + "\" could not be found");
                }
                
            } else if (fragmentSpec instanceof CompleteTemplateFragmentSpec) {
                
                fragmentNode = parsedTemplate.getDocument().getDocumentElement();
                
                if (fragmentNode == null) {
                    throw new AttrProcessorException(
                            "Root node in template \"" + fragmentTemplateName + "\" could not be found");
                }
                
            } else if (fragmentSpec instanceof XPathFragmentSpec) {

                final XPathFragmentSpec xpathFragmentSpec = (XPathFragmentSpec) fragmentSpec;
                
                final XPath xpath = XPathFactory.newInstance().newXPath();
                final XPathExpression xpathExpr = xpath.compile(xpathFragmentSpec.getXPathExpression());
                
                final NodeList exprResult = 
                    (NodeList) xpathExpr.evaluate(parsedTemplate.getDocument(), XPathConstants.NODESET);
                
                fragmentNode = exprResult.item(0);
                
                if (fragmentNode == null) {
                    throw new AttrProcessorException(
                            "No result for XPath expression \"" + xpathFragmentSpec.getXPathExpression() +"\" in template \"" + fragmentTemplateName + "\" could be found");
                }
                
            }
            
            return fragmentNode;
        
        } catch (final AttrProcessorException e) {
            throw e;
        } catch (final Exception e) {
            throw new AttrProcessorException(
                    "An error happened during parsing of template: \"" + fragmentTemplateName + "\"", e);
        }
        
    }



    protected abstract boolean getSubstituteInclusionNode(
            final Arguments arguments, final TemplateResolution templateResolution, 
            final Document document, final Element element);

    protected abstract AbstractFragmentSpec getFragmentSpec(
            final Arguments arguments, final TemplateResolution templateResolution, 
            final Document document, final Element element);
    


    
    /**
     * 
     * @author Daniel Fern&aacute;ndez
     * 
     * @since 1.0
     *
     */
    protected static abstract class AbstractFragmentSpec {
        
        private final String fragmentTemplateName;
        
        
        public AbstractFragmentSpec(final String fragmentTemplateName) {
            super();
            Validate.notEmpty(fragmentTemplateName, "Fragment template name cannot be null or empty");
            this.fragmentTemplateName = fragmentTemplateName;
        }


        public String getFragmentTemplateName() {
            return this.fragmentTemplateName;
        }

    }


    
    /**
     * 
     * @author Daniel Fern&aacute;ndez
     * 
     * @since 1.0
     *
     */
    protected static final class NamedFragmentSpec extends AbstractFragmentSpec {
        
        private final String fragmentTagName;
        private final String fragmentAttributeName;
        private final String fragmentAttributeValue;
        
        public NamedFragmentSpec(final String fragmentTemplateName,
                final String fragmentAttributeName, final String fragmentAttributeValue) {
            this(fragmentTemplateName, null, fragmentAttributeName, fragmentAttributeValue);
        }
        
        public NamedFragmentSpec(final String fragmentTemplateName,
                final String fragmentTagName,  final String fragmentAttributeName, final String fragmentAttributeValue) {
            super(fragmentTemplateName);
            // Fragment Tag name CAN be null. In that case any tag name will be applicable
            Validate.notEmpty(fragmentAttributeName, "Fragment attribute name cannot be null or empty");
            Validate.notEmpty(fragmentAttributeValue, "Fragment attribute value cannot be null or empty");
            this.fragmentTagName = fragmentTagName;
            this.fragmentAttributeName = fragmentAttributeName;
            this.fragmentAttributeValue = fragmentAttributeValue;
        }


        public String getFragmentTagName() {
            return this.fragmentTagName;
        }

        
        public String getFragmentAttributeName() {
            return this.fragmentAttributeName;
        }


        public String getFragmentAttributeValue() {
            return this.fragmentAttributeValue;
        }
        
    }


    
    /**
     * 
     * @author Daniel Fern&aacute;ndez
     * 
     * @since 1.0
     *
     */
    protected static final class CompleteTemplateFragmentSpec extends AbstractFragmentSpec {
        
        public CompleteTemplateFragmentSpec(final String fragmentTemplateName) {
            super(fragmentTemplateName);
        }

    }


    
    /**
     * 
     * @author Daniel Fern&aacute;ndez
     * 
     * @since 1.0
     *
     */
    protected static final class XPathFragmentSpec extends AbstractFragmentSpec {
        
        private final String xpathExpression;

        public XPathFragmentSpec(final String fragmentTemplateName, 
                final String xpathExpression) {
            super(fragmentTemplateName);
            Validate.notEmpty(xpathExpression, "XPath expression cannot be null or empty");
            this.xpathExpression = xpathExpression;
        }
        
        public String getXPathExpression() {
            return this.xpathExpression;
        }
        

    }
    
    
}
