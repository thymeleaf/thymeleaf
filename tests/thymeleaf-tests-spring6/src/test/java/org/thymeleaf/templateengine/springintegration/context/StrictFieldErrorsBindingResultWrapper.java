/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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
package org.thymeleaf.templateengine.springintegration.context;

import java.beans.PropertyEditor;
import java.util.List;
import java.util.Map;

import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;



/*
 * A BindingResult wrapper that simulates the strict behaviour of Spring Webflow's BindingModel,
 * which does not accept wildcard field expressions in getFieldErrors(String).
 */
class StrictFieldErrorsBindingResultWrapper implements BindingResult {

    private final BindingResult delegate;

    StrictFieldErrorsBindingResultWrapper(final BindingResult delegate) {
        this.delegate = delegate;
    }


    @Override
    public List<FieldError> getFieldErrors(final String field) {
        if ("*".equals(field)) {
            throw new IllegalArgumentException(
                    "Wildcard field expressions are not supported by this Errors implementation");
        }
        return this.delegate.getFieldErrors(field);
    }


    @Override
    public String getObjectName() {
        return this.delegate.getObjectName();
    }

    @Override
    public void reject(final String errorCode, final Object[] errorArgs, final String defaultMessage) {
        this.delegate.reject(errorCode, errorArgs, defaultMessage);
    }

    @Override
    public void rejectValue(final String field, final String errorCode, final Object[] errorArgs, final String defaultMessage) {
        this.delegate.rejectValue(field, errorCode, errorArgs, defaultMessage);
    }

    @Override
    public List<ObjectError> getGlobalErrors() {
        return this.delegate.getGlobalErrors();
    }

    @Override
    public List<FieldError> getFieldErrors() {
        return this.delegate.getFieldErrors();
    }

    @Override
    public Object getFieldValue(final String field) {
        return this.delegate.getFieldValue(field);
    }

    @Override
    public Object getTarget() {
        return this.delegate.getTarget();
    }

    @Override
    public Map<String, Object> getModel() {
        return this.delegate.getModel();
    }

    @Override
    public Object getRawFieldValue(final String field) {
        return this.delegate.getRawFieldValue(field);
    }

    @Override
    public PropertyEditor findEditor(final String field, final Class<?> valueType) {
        return this.delegate.findEditor(field, valueType);
    }

    @Override
    public PropertyEditorRegistry getPropertyEditorRegistry() {
        return this.delegate.getPropertyEditorRegistry();
    }

    @Override
    public String[] resolveMessageCodes(final String errorCode) {
        return this.delegate.resolveMessageCodes(errorCode);
    }

    @Override
    public String[] resolveMessageCodes(final String errorCode, final String field) {
        return this.delegate.resolveMessageCodes(errorCode, field);
    }

    @Override
    public void addError(final ObjectError error) {
        this.delegate.addError(error);
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }

}
