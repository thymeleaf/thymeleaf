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

import java.util.Collections;
import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.ParsedTemplate;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Tag;
import org.thymeleaf.exceptions.AttrProcessorException;
import org.thymeleaf.processor.ITagNameProcessorMatcher;
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
public abstract class AbstractFragmentTagProcessor 
        extends AbstractTagProcessor {

    
    
    

    public AbstractFragmentTagProcessor(final String tagName) {
        super(tagName);
    }
    
    public AbstractFragmentTagProcessor(final ITagNameProcessorMatcher matcher) {
        super(matcher);
    }



    
    
    @Override
    public final ProcessorResult processTag(final Arguments arguments, final Tag tag) {
        
        final boolean substituteInclusionNode =
            getSubstituteInclusionNode(arguments, tag);
        
        final List<Node> newNodes = 
            getNewNodes(arguments, tag, substituteInclusionNode);

        tag.clearChildren();
        
        for (final Node newNode : newNodes) {
            tag.addChild(newNode);
        }
        
        
        if (!substituteInclusionNode) {
            return ProcessorResult.OK;
        }

        // Inclusion tag will be substituted by the new nodes
        tag.getParent().extractChild(tag);
        
        return ProcessorResult.OK;
            
    }

    
    
    
    private final List<Node> getNewNodes(
            final Arguments arguments, final Tag tag, final boolean substituteInclusionNode) {
        

        final AbstractFragmentSpec fragmentSpec =
            getFragmentSpec(arguments, tag);
        if (fragmentSpec == null) {
            throw new AttrProcessorException("Null value for \"" + tag.getName() + "\" fragment specification not allowed");
        }
        
        final NestableNode fragmentNode = getFragment(arguments, tag, fragmentSpec); 
        
        if (fragmentNode == null) {
            throw new AttrProcessorException(
                    "An error happened during parsing of include: \"" + tag.getName() + "\": fragment node is null");
        }

        try {
            
            if (substituteInclusionNode) {
                return Collections.singletonList((Node)fragmentNode);
            }
            
            return fragmentNode.getChildren();
            
        } catch (final Exception e) {
            throw new AttrProcessorException(
                    "An error happened during parsing of include: \"" + tag.getName() + "\"", e);
        }
        
    }

    
    
    private static NestableNode getFragment(
            final Arguments arguments, final Tag tag, final AbstractFragmentSpec fragmentSpec) {

        
        final String fragmentTemplateName = fragmentSpec.getFragmentTemplateName();
        final String templateName = arguments.getTemplateResolution().getTemplateName();
        
        if (templateName.equals(fragmentTemplateName)) {
            throw new AttrProcessorException(
                    "Template \"" + templateName + 
                    "\" references itself from a " +
                    "\"" + tag.getName() + "\" tag, which is forbidden.");
        }
        
        try {
            
            final TemplateProcessingParameters fragmentTemplateProcessingParameters = 
                new TemplateProcessingParameters(
                        arguments.getConfiguration(), fragmentTemplateName, arguments.getContext());
            
            final ParsedTemplate parsedTemplate = arguments.getTemplateParser().parseDocument(fragmentTemplateProcessingParameters);
            
            NestableNode fragmentNode = null; 
            if (fragmentSpec instanceof NamedFragmentSpec) {
                
                final NamedFragmentSpec namedFragmentSpec = (NamedFragmentSpec) fragmentSpec;
                final String fragmentTagName = namedFragmentSpec.getFragmentTagName();
                final String fragmentAttributeName = namedFragmentSpec.getFragmentAttributeName();
                final String fragmentAttributeValue = namedFragmentSpec.getFragmentAttributeValue();
                
                fragmentNode = 
                    DOMUtils.extractFragmentByAttributeValue(parsedTemplate.getDocument().getRoot(), fragmentTagName, fragmentAttributeName, fragmentAttributeValue);
                                        
                if (fragmentNode == null) {
                    throw new AttrProcessorException(
                            "Fragment \"" + fragmentAttributeValue + "\" in template \"" + fragmentTemplateName + "\" could not be found");
                }
                
            } else if (fragmentSpec instanceof CompleteTemplateFragmentSpec) {
                
                fragmentNode = parsedTemplate.getDocument().getRoot();
                
                if (fragmentNode == null) {
                    throw new AttrProcessorException(
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
//                    throw new AttrProcessorException(
//                            "No result for XPath expression \"" + xpathFragmentSpec.getXPathExpression() +"\" in template \"" + fragmentTemplateName + "\" could be found");
//                }
                
            }
            
            return fragmentNode;
        
        } catch (final AttrProcessorException e) {
            throw e;
        } catch (final Exception e) {
            throw new AttrProcessorException(
                    "An error happened during parsing of template: \"" + fragmentTemplateName + "\"", e);
        }
        
    }



    protected abstract boolean getSubstituteInclusionNode(final Arguments arguments, final Tag tag);

    protected abstract AbstractFragmentSpec getFragmentSpec(final Arguments arguments, final Tag tag);
    


    
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
