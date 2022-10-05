
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
package org.thymeleaf.standard.expression;

import java.util.Map;

import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.PropertyAccessor;
import ognl.enhance.UnsupportedCompilationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.IContext;

/**
 * <p>
 *   Implementation of {@code PropertyAccessor} that allows OGNL to access the contents of {@link IContext}
 *   implementations as if they were a Map.
 * </p>
 * <p>
 *   Note this accessor also has to take care of variable access restrictions (like e.g. forbidding access to
 *   the request parameters in unescaped text or in preprocessing expressions).
 * </p>
 * <p>
 *   Note a class with this name existed since 2.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Michal Kreuzman
 * @author Daniel Fern&aacute;ndez
 * @see PropertyAccessor
 * @since 3.0.0
 */
public final class OGNLContextPropertyAccessor implements PropertyAccessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(OGNLContextPropertyAccessor.class);

    public static final String RESTRICT_REQUEST_PARAMETERS = "%RESTRICT_REQUEST_PARAMETERS%";
    static final String REQUEST_PARAMETERS_RESTRICTED_VARIABLE_NAME = "param";


    OGNLContextPropertyAccessor() {
        super();
    }




    public Object getProperty(final Map ognlContext, final Object target, final Object name) throws OgnlException {

        if (!(target instanceof IContext)) {
            throw new IllegalStateException(
                    "Wrong target type. This property accessor is only usable for " + IContext.class.getName() + " implementations, and " +
                    "in this case the target object is " + (target == null? "null" : ("of class " + target.getClass().getName())));
        }

        if (REQUEST_PARAMETERS_RESTRICTED_VARIABLE_NAME.equals(name) && ognlContext != null && ognlContext.containsKey(RESTRICT_REQUEST_PARAMETERS)) {
            throw new OgnlException(
                    "Access to variable \"" + name + "\" is forbidden in this context. Note some restrictions apply to " +
                    "variable access. For example, direct access to request parameters is forbidden in preprocessing and " +
                    "unescaped expressions, in TEXT template mode, in fragment insertion specifications and " +
                    "in some specific attribute processors.");
        }

        final String propertyName = (name == null? null : name.toString());

        /*
         * NOTE we do not check here whether we are being asked for the 'locale', 'request', 'response', etc.
         * because there already are specific expression objects for the most important of them, which should
         * be used instead: #locale, #httpServletRequest, #httpSession, etc.
         * The variables maps should just be used as a map, without exposure of its more-internal methods...
         */
        final IContext context = (IContext) target;
        return context.getVariable(propertyName);

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

        context.setCurrentAccessor(IContext.class);
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
