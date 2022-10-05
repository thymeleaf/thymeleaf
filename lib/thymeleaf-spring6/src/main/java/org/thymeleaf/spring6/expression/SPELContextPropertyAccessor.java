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
package org.thymeleaf.spring6.expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.thymeleaf.context.IContext;

/**
 * <p>
 *   Property accessor used for allowing Spring EL expression evaluators
 *   treat {@link IContext} objects correctly (map keys will be accessible
 *   as object properties).
 * </p>
 * <p>
 *   Note that, even if {@link IContext} objects used as expression roots will be accessible as
 *   {@code java.util.Map}s thanks to {@link SPELContextMapWrapper}, this property accessor
 *   class is still needed in order to access nested context info like the {@code session} or
 *   {@code param} maps in web contexts.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public final class SPELContextPropertyAccessor implements PropertyAccessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SPELContextPropertyAccessor.class);

    static final SPELContextPropertyAccessor INSTANCE = new SPELContextPropertyAccessor();

    private static final String REQUEST_PARAMETERS_RESTRICTED_VARIABLE_NAME = "param";
    private static final Class<?>[] TARGET_CLASSES = new Class<?>[] { IContext.class };




    SPELContextPropertyAccessor() {
        super();
    }


    public Class<?>[] getSpecificTargetClasses() {
        return TARGET_CLASSES;
    }



    public boolean canRead(final EvaluationContext context, final Object target, final String name)
            throws AccessException {
        if (context instanceof IThymeleafEvaluationContext) {
            if (((IThymeleafEvaluationContext) context).isVariableAccessRestricted()) {
                if (REQUEST_PARAMETERS_RESTRICTED_VARIABLE_NAME.equals(name)) {
                    throw new AccessException(
                            "Access to variable \"" + name + "\" is forbidden in this context. Note some restrictions apply to " +
                            "variable access. For example, direct access to request parameters is forbidden in preprocessing and " +
                            "unescaped expressions, in TEXT template mode, in fragment insertion specifications and " +
                            "in some specific attribute processors.");
                }
            }
        }
        return target != null;
    }



    public TypedValue read(final EvaluationContext evaluationContext, final Object target, final String name)
            throws AccessException {

        if (target == null) {
            throw new AccessException("Cannot read property of null target");
        }

        try {

            final IContext context = (IContext) target;
            return new TypedValue(context.getVariable(name));

        } catch (final ClassCastException e) {
            // This can happen simply because we're applying the same
            // AST tree on a different class (Spring internally caches property accessors).
            // So this exception might be considered "normal" by Spring AST evaluator and
            // just use it to refresh the property accessor cache.
            throw new AccessException("Cannot read target of class " + target.getClass().getName());
        }

    }




    public boolean canWrite(
            final EvaluationContext context, final Object target, final String name)
            throws AccessException {
        // There should never be a need to write on an IContext during a template execution
        return false;
    }



    public void write(
            final EvaluationContext context, final Object target, final String name, final Object newValue)
            throws AccessException {
        // There should never be a need to write on an IContext during a template execution
        throw new AccessException("Cannot write to " + IContext.class.getName());
    }


}
