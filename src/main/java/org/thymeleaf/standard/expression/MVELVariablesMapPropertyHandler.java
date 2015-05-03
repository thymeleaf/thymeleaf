
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
package org.thymeleaf.standard.expression;

import org.mvel2.integration.PropertyHandler;
import org.mvel2.integration.VariableResolverFactory;
import org.thymeleaf.context.IVariablesMap;

/**
 * <p>
 *   Implementation of {@code PropertyHandler} that allows MVEL to access the contents of {@link IVariablesMap}
 *   implementations as if it were a Map.
 * </p>
 * <p>
 *   Note this accessor also has to take care of variable access restrictions (like e.g. forbidding access to
 *   the request parameters in unescaped text or in preprocessing expressions).
 * </p>
 *
 * @see PropertyHandler
 * @since 3.0.0
 */
public final class MVELVariablesMapPropertyHandler implements PropertyHandler {

    public static final String RESTRICT_REQUEST_PARAMETERS = "%RESTRICT_REQUEST_PARAMETERS%";


    // TODO Actually control request parameter restrictions!

    MVELVariablesMapPropertyHandler() {
        super();
    }




    public Object getProperty(final String name, final Object contextObj, final VariableResolverFactory variableFactory) {

        if (!(contextObj instanceof IVariablesMap)) {
            throw new IllegalStateException(
                    "Wrong target type. This property accessor is only usable for IVariableMap implementations.");
        }

        final IVariablesMap map = (IVariablesMap) contextObj;
        return map.getVariable(name == null? null : name.toString());

    }




    public Object setProperty(
            final String name, final Object contextObj, final VariableResolverFactory variableFactory, final Object value) {
        // IVariablesMap implementations should never be set values from MVEL expressions
        throw new UnsupportedOperationException("Cannot set values into VariablesMap instances from MVEL Expressions");
    }


}
