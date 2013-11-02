/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Text;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.util.EvaluationUtil;
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

    
    public static final String DEFAULT_STATUS_VAR_SUFFIX = "Stat";
    

    
    
    
    
    protected AbstractIterationAttrProcessor(final String attributeName) {
        super(attributeName);
    }
    
    
    protected AbstractIterationAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    

    
    
    
    @Override
    public final ProcessorResult processAttribute(final Arguments arguments, final Element element, final String attributeName) {

        
        final NestableNode parentNode = element.getParent();

        // Find out if there's some whitespace to duplicate so as to preserve the
        // 'look' of the HTML code when completed
        Node previousNode = null;
        for (final Node child: parentNode.getChildren()) {
            if (child == element) {
                break;
            }
            previousNode = child;
        }
        boolean preserveWhitespace = false;
        String whitespace = null;
        if (previousNode != null && previousNode instanceof Text) {
            final String content = ((Text)previousNode).getContent();
            if (content.trim().length() == 0) {
                preserveWhitespace = true;
                whitespace = content;
            }
            else {
                int i = content.length();
                while (i > 0 && Character.isWhitespace(content.charAt(i - 1))) {
                    i--;
                }
                if (i < content.length()) {
                    preserveWhitespace = true;
                    whitespace = content.substring(i);
                }
            }
        }

        final IterationSpec iterationSpec = 
            getIterationSpec(arguments, element, attributeName);
        
        
        final String iterVar = iterationSpec.getIterVarName();
        final String statusVar = iterationSpec.getStatusVarName();
        final Object iteratedObject = iterationSpec.getIteratedObject();

        final List<?> list = EvaluationUtil.evaluateAsIterable(iteratedObject);

        final int size = list.size();
        int index = 0;
        for (final Object obj : list) {
            
            // We choose not to clone processors because the host element
            // has at least one processor that is not valid for the 
            // new cloned elements: the iteration processor.
            final Element clonedElement = (Element) element.cloneNode(parentNode, false);
            clonedElement.removeAttribute(attributeName);
            
            /*
             * Prepare local variables that will be available for each iteration item
             */
            clonedElement.setNodeLocalVariable(iterVar, obj);
            final StatusVar status = 
                new StatusVar(index, index + 1, size, obj);
            if (statusVar != null) {
                clonedElement.setNodeLocalVariable(statusVar, status);
            } else {
                clonedElement.setNodeLocalVariable(iterVar + DEFAULT_STATUS_VAR_SUFFIX, status);
            }
            
            // Add whitespace to preserve the look in the resulting HTML code
            if (preserveWhitespace && index > 0) {
                parentNode.insertBefore(element, new Text(whitespace));
            }
            parentNode.insertBefore(element, clonedElement);

            processClonedHostIterationElement(arguments, clonedElement, attributeName);
            
            index++;
            
        }
        
        parentNode.removeChild(element);
        
        return ProcessorResult.OK;
        
    }
    


    
    protected abstract IterationSpec getIterationSpec(
            final Arguments arguments, final Element element, final String attributeName);
    

    
    protected abstract void processClonedHostIterationElement(final Arguments arguments, final Element iteratedChild, final String attributeName);
    
    
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
