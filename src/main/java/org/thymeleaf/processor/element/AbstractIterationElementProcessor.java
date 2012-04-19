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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.IElementNameProcessorMatcher;
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
public abstract class AbstractIterationElementProcessor 
        extends AbstractElementProcessor {

    
    public static String DEFAULT_STATUS_VAR_SUFFIX = "Stat";
    

    

    public AbstractIterationElementProcessor(final String elementName) {
        super(elementName);
    }
    
    public AbstractIterationElementProcessor(final IElementNameProcessorMatcher matcher) {
        super(matcher);
    }



    
    
    
    @Override
    public final ProcessorResult processElement(final Arguments arguments, final Element element) {

        
        final NestableNode parentNode = element.getParent();
        
        final IterationSpec iterationSpec = 
            getIterationSpec(arguments, element);

        final boolean removeHostIterationElement = 
                removeHostIterationElement(arguments, element);
        
        final String iteratedElementName = 
                getIteratedElementName(arguments, element);
        
        final String iterVar = iterationSpec.getIterVarName();
        final String statusVar = iterationSpec.getStatusVarName();
        final Object iteratedObject = iterationSpec.getIteratedObject();
        
        
        final List<?> list = ObjectUtils.convertToIterable(iteratedObject);

        int size = list.size(); 
        int index = 0;
        for (final Object obj : list) {
            
            Element iterElement = null;
            
            if (removeHostIterationElement) {
                
                // We can safely clone the host element because we will remove it below
                iterElement = (Element) element.cloneNode(parentNode, false);
                
            } else {
                
                // We do not clone the iteration element with the same name because that 
                // would probably result in an infinite loop as the iteration processor 
                // would be applied once and again. Instead, we create iterated elements
                // with a new name (iteratedElementName).
                
                if (iteratedElementName == null) {
                    throw new TemplateProcessingException(
                            "Cannot specify null iterated element name if the host iteration element is not being removed");
                }
                
                iterElement = element.cloneElementNodeWithNewName(parentNode, iteratedElementName, false);
                
            }
            parentNode.insertBefore(element, iterElement);
            
            /*
             * Prepare local variables that will be available for each iteration item
             */
            final Map<String,Object> nodeLocalVariables = new HashMap<String,Object>();
            nodeLocalVariables.put(iterVar, obj);
            final StatusVar status = 
                new StatusVar(index, index + 1, size, obj);
            if (statusVar != null) {
                nodeLocalVariables.put(statusVar, status);
            } else {
                nodeLocalVariables.put(iterVar + DEFAULT_STATUS_VAR_SUFFIX, status);
            }
            
            
            if (removeHostIterationElement) {
                final List<Node> children = iterElement.getChildren();
                for (final Node child : children) {
                    child.setAllNodeLocalVariables(nodeLocalVariables);
                }
                parentNode.extractChild(iterElement);
            } else {
                iterElement.setAllNodeLocalVariables(nodeLocalVariables);
                processClonedHostIterationElement(arguments, iterElement);
            }
            
            index++;
            
        }
        
        parentNode.removeChild(element);
        
        return ProcessorResult.OK;
        
    }
    


    
    protected abstract IterationSpec getIterationSpec(final Arguments arguments, final Element element);

    
    protected abstract boolean removeHostIterationElement(final Arguments arguments, final Element element);

    
    protected abstract String getIteratedElementName(final Arguments arguments, final Element element);
    
    
    protected abstract void processClonedHostIterationElement(final Arguments arguments, final Element iteratedChild);

    
    
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
            return (this.index == this.count - 1);
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
