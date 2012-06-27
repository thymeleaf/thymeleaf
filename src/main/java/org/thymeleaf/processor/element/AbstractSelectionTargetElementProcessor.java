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
package org.thymeleaf.processor.element;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.expression.ExpressionEvaluationContext;
import org.thymeleaf.processor.IElementNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractSelectionTargetElementProcessor 
        extends AbstractElementProcessor {
    
    
    

    public AbstractSelectionTargetElementProcessor(final String elementName) {
        super(elementName);
    }
    
    public AbstractSelectionTargetElementProcessor(final IElementNameProcessorMatcher matcher) {
        super(matcher);
    }


    
    
    
    @Override
    public final ProcessorResult processElement(final Arguments arguments, final Element element) {

        
        final Object newSelectionTarget = 
            getNewSelectionTarget(arguments, element);

        final boolean removeHostElement = 
                removeHostElement(arguments, element);
        
        Map<String,Object> additionalLocalVariables = 
            getAdditionalLocalVariables(arguments, element);
        
        if (additionalLocalVariables == null) {
            additionalLocalVariables = new HashMap<String, Object>(2, 1.0f);
        } else {
            additionalLocalVariables = new HashMap<String, Object>(additionalLocalVariables);
        }
        additionalLocalVariables.put(
                ExpressionEvaluationContext.SELECTION_TARGET_LOCAL_VARIABLE_NAME, newSelectionTarget);

        if (removeHostElement) {
            
            final List<Node> children = element.getChildren();
            for (final Node child : children) {
                child.setAllNodeLocalVariables(additionalLocalVariables);
            }
    
            element.getParent().extractChild(element);
            
            doAdditionalProcess(arguments, element);
            
            return ProcessorResult.OK;
            
        }
        
        doAdditionalProcess(arguments, element);
            
        return ProcessorResult.setLocalVariables(additionalLocalVariables);
        
    }
    
    
    @SuppressWarnings("unused")
    protected Map<String,Object> getAdditionalLocalVariables(final Arguments arguments, final Element element) {
        // This method is meant to be overriden. By default, no local variables
        // will be set.
        return Collections.emptyMap();
    }

    
    
    protected abstract Object getNewSelectionTarget(final Arguments arguments, final Element element);

 
    
    protected abstract boolean removeHostElement(final Arguments arguments, final Element element);

    
    
    @SuppressWarnings("unused")
    protected void doAdditionalProcess(final Arguments arguments, final Element element) {
        // Nothing to be done, meant to be overriden
    }

    
}
