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
package org.thymeleaf.spring3.expression;

import java.util.List;

import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.spring3.util.DetailedError;
import org.thymeleaf.spring3.util.FieldUtils;



/**
 * <p>
 *   Expression Object for performing form-field-related operations inside Thymeleaf Standard Expressions in Spring
 *   environments.
 * </p>
 * <p>
 *   Note a class with this name existed since 1.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @author Tobias Gafner
 * 
 * @since 3.0.0
 *
 */
public final class Fields {

    private final IProcessingContext processingContext;
    
    public boolean hasAnyErrors() {
        return FieldUtils.hasAnyErrors(this.processingContext);
    }
    
    public boolean hasErrors() {
        return FieldUtils.hasAnyErrors(this.processingContext);
    }
    
    public boolean hasErrors(final String field) {
        return FieldUtils.hasErrors(this.processingContext, field);
    }
    
    public boolean hasGlobalErrors() {
        return FieldUtils.hasGlobalErrors(this.processingContext);
    }

    public List<String> allErrors() {
        return FieldUtils.errors(this.processingContext);
    }

    public List<String> errors() {
        return FieldUtils.errors(this.processingContext);
    }

    public List<String> errors(final String field) {
        return FieldUtils.errors(this.processingContext, field);
    }
    
    public List<String> globalErrors() {
        return FieldUtils.globalErrors(this.processingContext);
    }

    
    public String idFromName(final String fieldName) {
        return FieldUtils.idFromName(fieldName);
    }



    public List<DetailedError> detailedErrors() {
        return FieldUtils.detailedErrors(this.processingContext);
    }


    
    public Fields(final IProcessingContext processingContext) {
        super();
        this.processingContext = processingContext;
    }

    
}
