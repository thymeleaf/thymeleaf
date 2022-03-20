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
package org.thymeleaf.spring5.context.webmvc;

import java.beans.PropertyEditor;

import org.springframework.validation.Errors;
import org.springframework.web.servlet.support.BindStatus;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Implementation of the {@link IThymeleafBindStatus} interface, meant to wrap a Spring
 *   {@link BindStatus} object.
 * </p>
 *
 * @see BindStatus
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
class SpringWebMvcThymeleafBindStatus implements IThymeleafBindStatus {

    private final BindStatus bindStatus;


    SpringWebMvcThymeleafBindStatus(final BindStatus bindStatus) {
        super();
        Validate.notNull(bindStatus, "BindStatus cannot be null");
        this.bindStatus = bindStatus;
    }


    @Override
    public String getPath() {
        return this.bindStatus.getPath();
    }

    @Override
    public String getExpression() {
        return this.bindStatus.getExpression();
    }

    @Override
    public Object getValue() {
        return this.bindStatus.getValue();
    }

    @Override
    public Class<?> getValueType() {
        return this.bindStatus.getValueType();
    }

    @Override
    public Object getActualValue() {
        return this.bindStatus.getActualValue();
    }

    @Override
    public String getDisplayValue() {
        return this.bindStatus.getDisplayValue();
    }

    @Override
    public boolean isError() {
        return this.bindStatus.isError();
    }

    @Override
    public String[] getErrorCodes() {
        return this.bindStatus.getErrorCodes();
    }

    @Override
    public String getErrorCode() {
        return this.bindStatus.getErrorCode();
    }

    @Override
    public String[] getErrorMessages() {
        return this.bindStatus.getErrorMessages();
    }

    @Override
    public String getErrorMessage() {
        return this.bindStatus.getErrorMessage();
    }

    @Override
    public String getErrorMessagesAsString(final String delimiter) {
        return this.bindStatus.getErrorMessagesAsString(delimiter);
    }

    @Override
    public Errors getErrors() {
        return this.bindStatus.getErrors();
    }

    @Override
    public PropertyEditor getEditor() {
        return this.bindStatus.getEditor();
    }

    @Override
    public PropertyEditor findEditor(final Class<?> valueClass) {
        return this.bindStatus.findEditor(valueClass);
    }




    @Override
    public String toString() {
        return this.bindStatus.toString();
    }

}
