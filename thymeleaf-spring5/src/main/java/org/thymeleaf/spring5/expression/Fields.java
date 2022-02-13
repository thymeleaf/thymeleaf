/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring5.expression;

import java.util.List;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.spring5.util.DetailedError;
import org.thymeleaf.spring5.util.FieldUtils;


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
 * @since 3.0.3
 *
 */
public final class Fields {

    private final IExpressionContext context;

    public boolean hasAnyErrors() {
        return FieldUtils.hasAnyErrors(this.context);
    }

    public boolean hasErrors() {
        return FieldUtils.hasAnyErrors(this.context);
    }

    public boolean hasErrors(final String field) {
        return FieldUtils.hasErrors(this.context, field);
    }

    public boolean hasGlobalErrors() {
        return FieldUtils.hasGlobalErrors(this.context);
    }

    public List<String> allErrors() {
        return FieldUtils.errors(this.context);
    }

    public List<String> errors() {
        return FieldUtils.errors(this.context);
    }

    public List<String> errors(final String field) {
        return FieldUtils.errors(this.context, field);
    }

    public List<String> globalErrors() {
        return FieldUtils.globalErrors(this.context);
    }


    public String idFromName(final String fieldName) {
        return FieldUtils.idFromName(fieldName);
    }



    public List<DetailedError> allDetailedErrors() {
        return FieldUtils.detailedErrors(this.context);
    }

    public List<DetailedError> detailedErrors() {
        return FieldUtils.detailedErrors(this.context);
    }

    public List<DetailedError> detailedErrors(final String field) {
        return FieldUtils.detailedErrors(this.context, field);
    }

    public List<DetailedError> globalDetailedErrors() {
        return FieldUtils.globalDetailedErrors(this.context);
    }



    public Fields(final IExpressionContext context) {
        super();
        this.context = context;
    }


}
