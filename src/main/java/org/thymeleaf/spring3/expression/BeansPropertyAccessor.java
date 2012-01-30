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
import org.thymeleaf.spring3.context.Beans;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
class BeansPropertyAccessor extends ReflectivePropertyAccessor {
    
    private static final Class<?>[] TARGET_CLASSES = new Class<?>[] { Beans.class };
    
    
    public static final BeansPropertyAccessor INSTANCE = new BeansPropertyAccessor();
                                  
    
    public BeansPropertyAccessor() {
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
        if (!(target instanceof Beans)) {
            // This can happen simply because we're applying the same
            // AST tree on a different class (Spring internally caches property accessors).
            // So this exception might be considered "normal" by Spring AST evaluator and
            // just use it to refresh the property accessor cache.
            throw new AccessException("Cannot read target of class " + target.getClass().getName());
        }
        return ((Beans)target).containsKey(name);
    }

    

    @Override
    public TypedValue read(final EvaluationContext context, final Object target, final String name)
            throws AccessException {
        if (target == null) {
            throw new AccessException("Cannot read property of null target");
        }
        if (!(target instanceof Beans)) {
            // This can happen simply because we're applying the same
            // AST tree on a different class (Spring internally caches property accessors).
            // So this exception might be considered "normal" by Spring AST evaluator and
            // just use it to refresh the property accessor cache.
            throw new AccessException("Cannot read target of class " + target.getClass().getName());
        }
        return new TypedValue(((Beans)target).get(name));
    }

    
    
    
}
