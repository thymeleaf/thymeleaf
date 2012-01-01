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
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Tag;
import org.thymeleaf.processor.ITagNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractSelectionTargetTagProcessor 
        extends AbstractTagProcessor {
    
    
    

    public AbstractSelectionTargetTagProcessor(final String tagName) {
        super(tagName);
    }
    
    public AbstractSelectionTargetTagProcessor(final ITagNameProcessorMatcher matcher) {
        super(matcher);
    }


    
    
    
    @Override
    public final ProcessorResult processTag(final Arguments arguments, final Tag tag) {

        
        final Object newSelectionTarget = 
            getNewSelectionTarget(arguments, tag);

        final boolean removeHostTag = 
                removeHostTag(arguments, tag);
        
        final Map<String,Object> additionalLocalVariables = 
            getAdditionalLocalVariables(arguments, tag);

        if (removeHostTag) {
            
            if (additionalLocalVariables != null) {
                final List<Node> children = tag.getChildren();
                for (final Node child : children) {
                    child.addNodeLocalVariables(additionalLocalVariables);
                }
            }
    
            tag.getParent().extractChild(tag);
            
            return ProcessorResult.setSelectionTarget(newSelectionTarget);
            
        }
            
        if (additionalLocalVariables != null) {
            return ProcessorResult.setLocalVariablesAndSelectionTarget(additionalLocalVariables, newSelectionTarget);
        }
        
        return ProcessorResult.setSelectionTarget(newSelectionTarget);
        
    }
    
    
    @SuppressWarnings("unused")
    protected Map<String,Object> getAdditionalLocalVariables(final Arguments arguments, final Tag tag) {
        // This method is meant to be overriden. By default, no local variables
        // will be set.
        return Collections.emptyMap();
    }

    
    
    protected abstract Object getNewSelectionTarget(final Arguments arguments, final Tag tag);

 
    
    protected abstract boolean removeHostTag(final Arguments arguments, final Tag tag);

    
}
