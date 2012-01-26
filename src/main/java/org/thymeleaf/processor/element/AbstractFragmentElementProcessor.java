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
package org.thymeleaf.processor.element;

import java.util.Collections;
import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.Template;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.IElementNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.util.DOMUtils;
import org.thymeleaf.util.Validate;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public abstract class AbstractFragmentElementProcessor 
        extends AbstractElementProcessor {

    
    
    

    public AbstractFragmentElementProcessor(final String elementName) {
        super(elementName);
    }
    
    public AbstractFragmentElementProcessor(final IElementNameProcessorMatcher matcher) {
        super(matcher);
    }



    
    
    @Override
    public final ProcessorResult processElement(final Arguments arguments, final Element element) {
        
        final boolean substituteInclusionNode =
            getSubstituteInclusionNode(arguments, element);
        
        final List<Node> newNodes = 
            getNewNodes(arguments, element, substituteInclusionNode);

        element.clearChildren();
        
        for (final Node newNode : newNodes) {
            element.addChild(newNode);
        }
        
        
        if (!substituteInclusionNode) {
            return ProcessorResult.OK;
        }

        // Inclusion element will be substituted by the new nodes
        element.getParent().extractChild(element);
        
        return ProcessorResult.OK;
            
    }

    
    
    
    private final List<Node> getNewNodes(
            final Arguments arguments, final Element element, final boolean substituteInclusionNode) {
        

        final AbstractFragmentSpec fragmentSpec =
            getFragmentSpec(arguments, element);
        if (fragmentSpec == null) {
            throw new TemplateProcessingException("Null value for \"" + element.getName() + "\" fragment specification not allowed");
        }
        
        final NestableNode fragmentNode = getFragment(arguments, element, fragmentSpec); 
        
        if (fragmentNode == null) {
            throw new TemplateProcessingException(
                    "An error happened during parsing of include: \"" + element.getName() + "\": fragment node is null");
        }

        try {
            
            if (substituteInclusionNode) {
                return Collections.singletonList((Node)fragmentNode);
            }
            
            return fragmentNode.getChildren();
            
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "An error happened during parsing of include: \"" + element.getName() + "\"", e);
        }
        
    }

    
    
    private static NestableNode getFragment(
            final Arguments arguments, final Element element, final AbstractFragmentSpec fragmentSpec) {

        
        final String fragmentTemplateName = fragmentSpec.getFragmentTemplateName();
        final String templateName = arguments.getTemplateResolution().getTemplateName();
        
        if (templateName.equals(fragmentTemplateName)) {
            throw new TemplateProcessingException(
                    "Template \"" + templateName + 
                    "\" references itself from a " +
                    "\"" + element.getName() + "\" element, which is forbidden.");
        }
        
        try {
            
            final TemplateProcessingParameters fragmentTemplateProcessingParameters = 
                new TemplateProcessingParameters(
                        arguments.getConfiguration(), fragmentTemplateName, arguments.getContext());
            
            final Template parsedTemplate = arguments.getTemplateRepository().getTemplate(fragmentTemplateProcessingParameters);
            
            NestableNode fragmentNode = null; 
            if (fragmentSpec instanceof NamedFragmentSpec) {
                
                final NamedFragmentSpec namedFragmentSpec = (NamedFragmentSpec) fragmentSpec;
                final String fragmentElementName = namedFragmentSpec.getFragmentElementName();
                final String fragmentAttributeName = namedFragmentSpec.getFragmentAttributeName();
                final String fragmentAttributeValue = namedFragmentSpec.getFragmentAttributeValue();
                
                fragmentNode = 
                    DOMUtils.extractFragmentByAttributeValue(parsedTemplate.getDocument(), fragmentElementName, fragmentAttributeName, fragmentAttributeValue);
                                        
                if (fragmentNode == null) {
                    throw new TemplateProcessingException(
                            "Fragment \"" + fragmentAttributeValue + "\" in template \"" + fragmentTemplateName + "\" could not be found");
                }
                
            } else if (fragmentSpec instanceof CompleteTemplateFragmentSpec) {
                
                fragmentNode = parsedTemplate.getDocument();
                
                if (fragmentNode == null) {
                    throw new TemplateProcessingException(
                            "Root node in template \"" + fragmentTemplateName + "\" could not be found");
                }
                
            } else if (fragmentSpec instanceof XPathFragmentSpec) {
                
throw new IllegalStateException("XPath evaluation has been temporarily deactivated!"); 
 // TODO Uncomment this and apply XPath by taking the nodes, converting them to String,
 // feeding this String to xpathExpr.evaluate, obtaining a DOM NodeList as a result and
 // applying Node.translateDomNode on exprResult.item(0).
                             

//                final XPathFragmentSpec xpathFragmentSpec = (XPathFragmentSpec) fragmentSpec;
//                
//                final XPath xpath = XPathFactory.newInstance().newXPath();
//                final XPathExpression xpathExpr = xpath.compile(xpathFragmentSpec.getXPathExpression());
//                
//                final NodeList exprResult = 
//                    (NodeList) xpathExpr.evaluate(parsedTemplate.getDocument(), XPathConstants.NODESET);
//                
//                fragmentNode = exprResult.item(0);
//                
//                if (fragmentNode == null) {
//                    throw new TemplateProcessingException(
//                            "No result for XPath expression \"" + xpathFragmentSpec.getXPathExpression() +"\" in template \"" + fragmentTemplateName + "\" could be found");
//                }
                
            }
            
            return fragmentNode;
        
        } catch (final TemplateProcessingException e) {
            throw e;
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "An error happened during parsing of template: \"" + fragmentTemplateName + "\"", e);
        }
        
    }



    protected abstract boolean getSubstituteInclusionNode(final Arguments arguments, final Element element);

    protected abstract AbstractFragmentSpec getFragmentSpec(final Arguments arguments, final Element element);
    


    
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
        
        private final String fragmentElementName;
        private final String fragmentAttributeName;
        private final String fragmentAttributeValue;
        
        public NamedFragmentSpec(final String fragmentTemplateName,
                final String fragmentAttributeName, final String fragmentAttributeValue) {
            this(fragmentTemplateName, null, fragmentAttributeName, fragmentAttributeValue);
        }
        
        public NamedFragmentSpec(final String fragmentTemplateName,
                final String fragmentElementName,  final String fragmentAttributeName, final String fragmentAttributeValue) {
            super(fragmentTemplateName);
            // Fragment Element name CAN be null. In that case any element name will be applicable
            Validate.notEmpty(fragmentAttributeName, "Fragment attribute name cannot be null or empty");
            Validate.notEmpty(fragmentAttributeValue, "Fragment attribute value cannot be null or empty");
            this.fragmentElementName = fragmentElementName;
            this.fragmentAttributeName = fragmentAttributeName;
            this.fragmentAttributeValue = fragmentAttributeValue;
        }


        public String getFragmentElementName() {
            return this.fragmentElementName;
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
