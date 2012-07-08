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
package org.thymeleaf.spring3.expression;

import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.spring3.util.FieldUtils;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class Fields {

    private final Configuration configuration;
    private final IProcessingContext processingContext;
    
    
    
    public boolean hasErrors(final String field) {
        return FieldUtils.hasErrors(this.configuration, this.processingContext, field);
    }
    
    
    public List<String> errors(final String field) {
        return FieldUtils.errors(this.configuration, this.processingContext, field);
    }

    
    public String idFromName(final String fieldName) {
        return FieldUtils.idFromName(fieldName);
    }
    

    
    /**
     * @deprecated Use {@link #Fields(Configuration, IProcessingContext)} instead.
     *             Will be removed in 2.1.x
     */
    @Deprecated
	public Fields(final Arguments arguments) {
	    this(arguments.getConfiguration(), arguments);
	}
    
    
    public Fields(final Configuration configuration, final IProcessingContext processingContext) {
        super();
        this.configuration = configuration;
        this.processingContext = processingContext;
    }

    
}
