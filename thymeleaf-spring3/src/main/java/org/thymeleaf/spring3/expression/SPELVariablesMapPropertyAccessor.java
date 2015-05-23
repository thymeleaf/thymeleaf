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

import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.thymeleaf.context.IVariablesMap;

/**
 * <p>
 *   Property accessor used for allowing Spring EL expression evaluators
 *   treat {@link IVariablesMap} objects correctly (map keys will be accessible
 *   as object properties).
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1 (reimplemented in 3.0.0)
 *
 */
public final class SPELVariablesMapPropertyAccessor implements PropertyAccessor {

    static final SPELVariablesMapPropertyAccessor INSTANCE = new SPELVariablesMapPropertyAccessor();

    private static final Class<?>[] TARGET_CLASSES = new Class<?>[] { IVariablesMap.class };


    // TODO Actually control request parameter restrictions (the evaluationcontext wrapper will know)!


    SPELVariablesMapPropertyAccessor() {
        super();
    }


    public Class<?>[] getSpecificTargetClasses() {
        return TARGET_CLASSES;
    }



    public boolean canRead(final EvaluationContext context, final Object target, final String name)
            throws AccessException {
        return target != null;
    }



    public TypedValue read(final EvaluationContext context, final Object target, final String name)
            throws AccessException {
        if (target == null) {
            throw new AccessException("Cannot read property of null target");
        }
        try {
            final IVariablesMap variablesMap = (IVariablesMap) target;
            return new TypedValue(variablesMap.getVariable(name));
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
        // There should never be a need to write on a VariablesMap during a template execution
        return false;
    }



    public void write(
            final EvaluationContext context, final Object target, final String name, final Object newValue)
            throws AccessException {
        // There should never be a need to write on a VariablesMap during a template execution
        throw new AccessException("Cannot write to " + IVariablesMap.class.getName());
    }


}
