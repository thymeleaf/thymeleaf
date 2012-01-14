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
package org.thymeleaf.processor;

import org.thymeleaf.dom.Node;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   <b>Internal use only</p>: stores a processor along with its matching context. 
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class ProcessorAndContext implements Comparable<ProcessorAndContext> {
    
    private final IProcessor processor;
    private final ProcessorMatchingContext context;
    
    public ProcessorAndContext(final IProcessor processor, final ProcessorMatchingContext context) {
        super();
        Validate.notNull(processor, "Processor cannot be null");
        Validate.notNull(context, "Context cannot be null");
        this.processor = processor;
        this.context = context;
    }
    
    public IProcessor getProcessor() {
        return this.processor;
    }
    
    public ProcessorMatchingContext getContext() {
        return this.context;
    }
    
    public boolean matches(final Node node) {
        return this.processor.getMatcher().matches(node,this.context);
    }
    
    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.context == null) ? 0 : this.context.hashCode());
        result = prime * result
                + ((this.processor == null) ? 0 : this.processor.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProcessorAndContext other = (ProcessorAndContext) obj;
        if (this.context == null) {
            if (other.context != null) {
                return false;
            }
        } else if (!this.context.equals(other.context)) {
            return false;
        }
        if (this.processor == null) {
            if (other.processor != null) {
                return false;
            }
        } else if (!this.processor.equals(other.processor)) {
            return false;
        }
        return true;
    }

    
    public int compareTo(final ProcessorAndContext o) {
        return this.processor.compareTo(o.getProcessor());
    }


}
