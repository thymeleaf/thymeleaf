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
package org.thymeleaf.processor.attr;

import java.util.Collections;
import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.Template;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.dom.DOMSelector;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.util.DOMUtils;
import org.thymeleaf.util.Validate;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 * @deprecated replaced by {@link AbstractFragmentHandlingAttrProcessor}. Will be
 *             removed in 2.1.x.
 *
 */
@Deprecated
public abstract class AbstractFragmentAttrProcessor 
        extends AbstractAttrProcessor {

    
    private static final String DOM_SELECTOR_EXPRESSION_PREFIX = "{dom_selector}";
    
    
    
    public AbstractFragmentAttrProcessor(final String attributeName) {
        super(attributeName);
    }
    
    
    public AbstractFragmentAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    

    
    
    @Override
    public final ProcessorResult processAttribute(final Arguments arguments, final Element element, final String attributeName) {
        
        final String attributeValue = element.getAttributeValue(attributeName);
        
        final boolean substituteInclusionNode =
            getSubstituteInclusionNode(arguments, element, attributeName, attributeValue);
        
        final List<Node> newNodes = 
            getNewNodes(arguments, element, attributeName, attributeValue, substituteInclusionNode);

        element.clearChildren();
        
        for (final Node newNode : newNodes) {
            element.addChild(newNode);
        }
        
        element.removeAttribute(attributeName);
        
        if (!substituteInclusionNode) {
            return ProcessorResult.OK;
        }

        // Inclusion element will be substituted by the new nodes
        element.getParent().extractChild(element);
        
        return ProcessorResult.OK;
            
    }

    
    
    
    private final List<Node> getNewNodes(
            final Arguments arguments, final Element element, 
            final String attributeName, final String attributeValue,
            final boolean substituteInclusionNode) {
        
        final AbstractFragmentSpec fragmentSpec =
            getFragmentSpec(arguments, element, attributeName, attributeValue);
        if (fragmentSpec == null) {
            throw new TemplateProcessingException("Null value for \"" + attributeName + "\" fragment specification not allowed");
        }
        
        final Node fragmentNode = getFragment(arguments, attributeName, fragmentSpec); 
        
        if (fragmentNode == null) {
            throw new TemplateProcessingException(
                    "An error happened during inclusion/substitution of \"" + attributeValue + "\": fragment node is null");
        }

        try {
            
            if (substituteInclusionNode) {
                return Collections.singletonList(fragmentNode);
            }
            
            if (!(fragmentNode instanceof NestableNode)) {
                throw new TemplateProcessingException(
                        "An error happened during parsing of include: \"" + attributeValue + "\": selected fragment has no children " +
                        "and therefore is not suitable for use in an inclusion operation -- maybe a substitution operation should be used instead?");
            }
            return ((NestableNode)fragmentNode).getChildren();
            
        } catch (final TemplateProcessingException e) {
            throw e;
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "An error happened during inclusion/substitution of \"" + attributeValue + "\"", e);
        }
        
    }

    
    
    private static Node getFragment(
            final Arguments arguments, final String attributeName, final AbstractFragmentSpec fragmentSpec) {

        
        final String templateName = arguments.getTemplateResolution().getTemplateName();
        final String fragmentTemplateName = fragmentSpec.getFragmentTemplateName();
        
        if (templateName.equals(fragmentTemplateName)) {
            throw new TemplateProcessingException(
                    "Template \"" + templateName + 
                    "\" references itself from a " +
                    "\"" + attributeName + "\" attribute, which is forbidden.");
        }
        
        try {
            
            final TemplateProcessingParameters fragmentTemplateProcessingParameters = 
                new TemplateProcessingParameters(
                        arguments.getConfiguration(), fragmentTemplateName, arguments.getContext());
            
            final Template parsedTemplate = arguments.getTemplateRepository().getTemplate(fragmentTemplateProcessingParameters);
            
            Node fragmentNode = null; 
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
                
            } else if (fragmentSpec instanceof DOMSelectorFragmentSpec) {

                final DOMSelectorFragmentSpec domSelectorFragmentSpec = (DOMSelectorFragmentSpec) fragmentSpec;
                final String domSelectorExpression = domSelectorFragmentSpec.getSelectorExpression();

                DOMSelector selector = null;
                ICache<String,Object> expressionCache = null;
                
                final ICacheManager cacheManager = arguments.getConfiguration().getCacheManager();
                if (cacheManager != null) {
                    expressionCache = cacheManager.getExpressionCache();
                    if (expressionCache != null) {
                        selector = (DOMSelector) expressionCache.get(DOM_SELECTOR_EXPRESSION_PREFIX + domSelectorExpression);
                    }
                }
                
                if (selector == null) {
                    selector = new DOMSelector(domSelectorExpression);
                    if (expressionCache != null) {
                        expressionCache.put(DOM_SELECTOR_EXPRESSION_PREFIX + domSelectorExpression, selector);
                    }
                }
                
                final List<Node> selectedNodes = selector.select(parsedTemplate.getDocument().getChildren());
                if (selectedNodes == null || selectedNodes.size() == 0) {
                    throw new TemplateProcessingException(
                            "No result for DOM selector expression \"" + domSelectorExpression +"\" in template \"" + fragmentTemplateName + "\" could be found");
                }
                    
                fragmentNode = selectedNodes.get(0);
                
            }
            
            return fragmentNode;
        
        } catch (final TemplateProcessingException e) {
            throw e;
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "An error happened during parsing of template: \"" + fragmentTemplateName + "\"", e);
        }
        
    }



    protected abstract boolean getSubstituteInclusionNode(
            final Arguments arguments, final Element element, 
            final String attributeName, final String attributeValue);

    protected abstract AbstractFragmentSpec getFragmentSpec(
            final Arguments arguments, final Element element, 
            final String attributeName, final String attributeValue);
    


    
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
     * @since 2.0.0
     *
     */
    protected static final class DOMSelectorFragmentSpec extends AbstractFragmentSpec {
        
        private final String selectorExpression;

        public DOMSelectorFragmentSpec(final String fragmentTemplateName, 
                final String selectorExpression) {
            super(fragmentTemplateName);
            Validate.notEmpty(selectorExpression, "DOM selector expression cannot be null or empty");
            this.selectorExpression = selectorExpression;
        }
        
        public String getSelectorExpression() {
            return this.selectorExpression;
        }
        

    }
    
    
}
