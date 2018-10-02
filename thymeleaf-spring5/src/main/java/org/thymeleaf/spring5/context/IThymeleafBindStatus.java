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
package org.thymeleaf.spring5.context;

import java.beans.PropertyEditor;

import org.springframework.validation.Errors;

/**
 * <p>
 *   This interface is meant to abstract a Spring {@code BindStatus}, without the client code
 *   needing to know if it is a Spring WebMVC or Spring WebFlux implementation of this
 *   {@code BindStatus}.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public interface IThymeleafBindStatus {

    public String getPath();
    public String getExpression();
    public Object getValue();
    public Class<?> getValueType();
    public Object getActualValue();
    public String getDisplayValue();

    public boolean isError();
    public String[] getErrorCodes();
    public String getErrorCode();
    public String[] getErrorMessages();
    public String getErrorMessage();
    public String getErrorMessagesAsString(String delimiter);
    public Errors getErrors();

    public PropertyEditor getEditor();
    public PropertyEditor findEditor(Class<?> valueClass);

}
