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
package org.thymeleaf.standard.processor.attr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.inliner.IStandardTextInliner;
import org.thymeleaf.standard.inliner.StandardDartTextInliner;
import org.thymeleaf.standard.inliner.StandardJavaScriptTextInliner;
import org.thymeleaf.standard.inliner.StandardTextTextInliner;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractStandardTextInlinerAttrProcessor 
        extends AbstractAttrProcessor {
    
    
    public static final String TEXT_INLINE = "text";
    public static final String JAVASCRIPT_INLINE = "javascript";
    public static final String DART_INLINE = "dart";
    public static final String NONE_INLINE = "none";

    
    
    

    
    public AbstractStandardTextInlinerAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    public AbstractStandardTextInlinerAttrProcessor(final String attributeName) {
        super(attributeName);
    }

    

    
    
    @Override
    public final ProcessorResult processAttribute(final Arguments arguments, final Element element, final String attributeName) {

        
        final IStandardTextInliner textInliner = getTextInliner(element, attributeName);

        final Map<String,Object> localVariables = new HashMap<String,Object>();
        localVariables.put(StandardDialect.INLINER_LOCAL_VARIABLE, textInliner);

        // This is probably unnecessary...
        ensureChildrenArePrecomputed(element);
        
        element.removeAttribute(attributeName);
        
        return ProcessorResult.setLocalVariablesAndProcessOnlyElementNodes(localVariables, false);
        
    }
    
    

    protected static IStandardTextInliner getTextInliner(final Element element, final String attributeName) {
        
        final String attributeValue = element.getAttributeValue(attributeName);
        
        if (attributeValue != null) {
            if (JAVASCRIPT_INLINE.equals(attributeValue.toLowerCase())) {
                return StandardJavaScriptTextInliner.INSTANCE;
            } else if (DART_INLINE.equals(attributeValue.toLowerCase())) {
                return StandardDartTextInliner.INSTANCE;
            } else if (TEXT_INLINE.equals(attributeValue.toLowerCase())) {
                return StandardTextTextInliner.INSTANCE;
            } else if (NONE_INLINE.equals(attributeValue.toLowerCase())) {
                return null;
            }
        }
        
        throw new TemplateProcessingException(
                "Cannot recognize value for \"" + attributeName + "\". Allowed values are " +
                "\"" + TEXT_INLINE + "\", \"" + JAVASCRIPT_INLINE + "\", " +
                "\"" + DART_INLINE + "\" and \"" + NONE_INLINE + "\"");
        
    }
    
    

    
    private static void ensureChildrenArePrecomputed(final Node node) {
        if (node != null) {
            node.setRecomputeProcessorsImmediately(true);
            if (node instanceof NestableNode) {
                final List<Node> children = ((NestableNode)node).getChildren();
                for (final Node child : children) {
                    ensureChildrenArePrecomputed(child);
                }
            }
        }
    }

    
    
}
