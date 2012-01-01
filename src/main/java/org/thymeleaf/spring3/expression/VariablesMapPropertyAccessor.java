/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.thymeleaf.context.VariablesMap;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
class VariablesMapPropertyAccessor extends ReflectivePropertyAccessor {
    
    private static final Class<?>[] TARGET_CLASSES = new Class<?>[] { VariablesMap.class };
    
    
    public static final VariablesMapPropertyAccessor INSTANCE = new VariablesMapPropertyAccessor();
    
    
    
    public VariablesMapPropertyAccessor() {
        super();
    }

    
    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return TARGET_CLASSES;
    }


    
    @Override
    public boolean canRead(final EvaluationContext context, final Object target, final String name)
            throws AccessException {
        if (target == null) {
            return false;
        }
        return true;
    }

    

    @Override
    @SuppressWarnings("unchecked")
    public TypedValue read(final EvaluationContext context, final Object target, final String name)
            throws AccessException {
        if (target == null) {
            throw new AccessException("Cannot read property of null target");
        }
        return new TypedValue(((VariablesMap<String,?>)target).get(name));
    }


    
    @Override
    public boolean canWrite(
            final EvaluationContext context, final Object target, final String name) 
            throws AccessException {
        if (target == null) {
            return false;
        }
        return true;
    }

    

    @Override
    @SuppressWarnings("unchecked")
    public void write(
            final EvaluationContext context, final Object target, final String name, final Object newValue) 
            throws AccessException {
        if (target == null) {
            throw new AccessException("Cannot write property of null target");
        }
        ((VariablesMap<String,Object>)target).put(name, newValue);
    }

    
    
    
}
