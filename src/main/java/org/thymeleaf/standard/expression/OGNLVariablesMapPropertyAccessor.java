
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

import java.util.Map;

import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.PropertyAccessor;
import ognl.enhance.UnsupportedCompilationException;
import org.thymeleaf.context.IVariablesMap;

/**
 * <p>
 *   Implementation of {@code PropertyAccessor} that allows OGNL to access the contents of {@link IVariablesMap}
 *   implementations as if it were a Map.
 * </p>
 * <p>
 *   Note this accessor also has to take care of variable access restrictions (like e.g. forbidding access to
 *   the request parameters in unescaped text or in preprocessing expressions).
 * </p>
 *
 * @author Michal Kreuzman
 * @author Daniel Fern&aacute;ndez
 * @see PropertyAccessor
 * @since 2.0 (reimplemented in 3.0.0)
 */
public final class OGNLVariablesMapPropertyAccessor implements PropertyAccessor {

    /*
     * This class supports OGNL-specific needs for the IVariablesMap implementations, in order to avoid problems like
     * e.g. the existence of a context variable named 'size'.
     */

    public static final String RESTRICT_REQUEST_PARAMETERS = "%RESTRICT_REQUEST_PARAMETERS%";


    // TODO Actually control request parameter restrictions!

    OGNLVariablesMapPropertyAccessor() {
        super();
    }




    public Object getProperty(final Map context, final Object target, final Object name) throws OgnlException {

        if (!(target instanceof IVariablesMap)) {
            throw new IllegalStateException(
                    "Wrong target type. This property accessor is only usable for IVariableMap implementations.");
        }

        final IVariablesMap map = (IVariablesMap) target;
        return map.getVariable(name == null? null : name.toString());

    }




    public void setProperty(final Map context, final Object target, final Object name, final Object value) throws OgnlException {
        // IVariablesMap implementations should never be set values from OGNL expressions
        throw new UnsupportedOperationException("Cannot set values into VariablesMap instances from OGNL Expressions");
    }




    public String getSourceAccessor(final OgnlContext context, final Object target, final Object index) {
        // This method is called during OGNL's bytecode enhancement optimizations in order to determine better-
        // performing methods to access the properties of an object. It's been written trying to mimic
        // what is done at MapPropertyAccessor#getSourceAccessor() method, removing all the parts related to indexed
        // access, which do not apply to IVariablesMap implementations.

        context.setCurrentAccessor(Map.class);
        context.setCurrentType(Object.class);

        return ".getVariable(" + index + ")";

    }




    public String getSourceSetter(final OgnlContext context, final Object target, final Object index) {
        // This method is called during OGNL's bytecode enhancement optimizations in order to determine better-
        // performing methods to access the properties of an object. Given IVariablesMap implementations should never
        // be set any values from OGNL, this exception should never be thrown anyway.
        throw new UnsupportedCompilationException(
                "Setting expression for " + context.getCurrentObject() + " with index of " + index + " cannot " +
                "be computed. IVariablesMap implementations are considered read-only by OGNL.");
    }


}
