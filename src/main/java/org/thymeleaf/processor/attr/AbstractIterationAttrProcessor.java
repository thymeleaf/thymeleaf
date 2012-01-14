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
package org.thymeleaf.processor.attr;

import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Tag;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.util.ObjectUtils;
import org.thymeleaf.util.Validate;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractIterationAttrProcessor 
        extends AbstractAttrProcessor {

    
    public static String DEFAULT_STATUS_VAR_SUFFIX = "Stat";
    

    
    
    
    
    public AbstractIterationAttrProcessor(final String attributeName) {
        super(attributeName);
    }
    
    
    public AbstractIterationAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    

    
    
    
    @Override
    public final ProcessorResult processAttribute(final Arguments arguments, final Tag tag, final String attributeName) {

        
        final NestableNode parentNode = tag.getParent();
        
        final IterationSpec iterationSpec = 
            getIterationSpec(arguments, tag, attributeName);
        
        
        final String iterVar = iterationSpec.getIterVarName();
        final String statusVar = iterationSpec.getStatusVarName();
        final Object iteratedObject = iterationSpec.getIteratedObject();
        
        
        final List<?> list = ObjectUtils.convertToList(iteratedObject);

        int size = list.size(); 
        int index = 0;
        for (final Object obj : list) {
            
            // We choose not to clone processors because the host tag
            // has at least one processor that is not valid for the 
            // new cloned tags: the iteration processor.
            final Tag clonedTag = (Tag) tag.cloneNode(parentNode, false);
            clonedTag.removeAttribute(attributeName);
            
            /*
             * Prepare local variables that will be available for each iteration item
             */
            clonedTag.addNodeLocalVariable(iterVar, obj);
            final StatusVar status = 
                new StatusVar(index, index + 1, size, obj);
            if (statusVar != null) {
                clonedTag.addNodeLocalVariable(statusVar, status);
            } else {
                clonedTag.addNodeLocalVariable(iterVar + DEFAULT_STATUS_VAR_SUFFIX, status);
            }
            
            parentNode.insertBefore(tag, clonedTag);

            processClonedHostIterationTag(arguments, clonedTag, attributeName);
            
            index++;
            
        }
        
        parentNode.removeChild(tag);
        
        return ProcessorResult.OK;
        
    }
    


    
    protected abstract IterationSpec getIterationSpec(
            final Arguments arguments, final Tag tag, final String attributeName);
    

    
    protected abstract void processClonedHostIterationTag(final Arguments arguments, final Tag iteratedChild, final String attributeName);
    
    
    /**
     * 
     * @author Daniel Fern&aacute;ndez
     * 
     * @since 1.0
     *
     */
    public static class StatusVar {
        
        private final int index;
        private final int count;
        private final int size;
        private final Object current;

        public StatusVar(final int index, final int count, final int size, final Object current) {
            super();
            this.index = index;
            this.count = count;
            this.size = size;
            this.current = current;
        }

        public int getIndex() {
            return this.index;
        }

        public int getCount() {
            return this.count;
        }
        
        public int getSize() {
            return this.size;
        }
        
        public Object getCurrent() {
            return this.current;
        }
        
        public boolean isEven() {
            return (this.index % 2 == 0);
        }
        
        public boolean isOdd() {
            return !isEven();
        }
        
        public boolean isFirst() {
            return (this.index == 0);
        }
        
        public boolean isLast() {
            return (this.index == this.size - 1);
        }
        
        @Override
        public String toString() {
            return "{index = " + this.index + ", count = " + this.count + 
                    ", size = " + this.size + ", current = " + (this.current == null? "null" : this.current.toString()) + "}";
        }
        
    }
    
    
    
    /**
     * 
     * @author Daniel Fern&aacute;ndez
     * 
     * @since 1.0
     *
     */
    protected static class IterationSpec {
        
        private final String iterVarName;
        private final String statusVarName;
        private final Object iteratedObject;
        
        public IterationSpec(final String iterVarName, 
                final String statusVarName, final Object iteratedObject) {
            super();
            Validate.notEmpty(iterVarName, "Iteration var name cannot be null or empty");
            this.iterVarName = iterVarName;
            this.statusVarName = statusVarName;
            this.iteratedObject = iteratedObject;
        }

        public String getIterVarName() {
            return this.iterVarName;
        }

        public String getStatusVarName() {
            return this.statusVarName;
        }

        public Object getIteratedObject() {
            return this.iteratedObject;
        }

        
    }
    
}
