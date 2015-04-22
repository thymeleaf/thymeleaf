/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.expression;

import org.thymeleaf.aurora.context.ITemplateProcessingContext;
import org.thymeleaf.util.Validate;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class Ids {

    
    private final ITemplateProcessingContext processingContext;
    
    
    public String seq(final Object id) {
        Validate.notNull(id, "ID cannot be null");
        final String str = id.toString();
        return str + this.processingContext.getIdentifierSequences().getAndIncrementIDSeq(str);
    }
    
    public String next(final Object id) {
        Validate.notNull(id, "ID cannot be null");
        final String str = id.toString();
        return str + this.processingContext.getIdentifierSequences().getNextIDSeq(str);
    }
    
    public String prev(final Object id) {
        Validate.notNull(id, "ID cannot be null");
        final String str = id.toString();
        return str + this.processingContext.getIdentifierSequences().getPreviousIDSeq(str);
    }
    

    
    public Ids(final ITemplateProcessingContext processingContext) {
        super();
        Validate.notNull(processingContext, "Processing Context cannot be null");
        this.processingContext = processingContext;
    }
    
}
