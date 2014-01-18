/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring4.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ConstructorResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.OperatorOverloader;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypeComparator;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypeLocator;
import org.springframework.expression.TypedValue;

/**
 * <p>
 *   The purpose of this class is to serve as a wrapper for a
 *   standard-defined {@link org.springframework.expression.spel.support.StandardEvaluationContext} object
 *   which will contain every expression-evaluation structure needed except for the
 *   expression variables. This avoids initializing the structures in 
 *   {@link org.springframework.expression.spel.support.StandardEvaluationContext} (some of which
 *   involve synchronized blocks) with every SpEL expression evaluation. 
 * </p>
 * 
 * @author Guven Demir
 * @author Daniel Fern&aacute;ndez
 *
 * @since 1.1.3
 *
 */
public final class SpelEvaluationContext implements EvaluationContext {
    
    private static final List<PropertyAccessor> THYMELEAF_PROPERTY_ACCESSORS;

    private static final List<PropertyAccessor> DEFAULT_PLUS_THYMELEAF_PROPERTY_ACCESSORS;

    public static final MapAccessor MAP_ACCESSOR_INSTANCE = new MapAccessor();

    private final EvaluationContext delegate;
    private final Map<String,Object> variables;
    private final List<PropertyAccessor> propertyAccessors;

    
    static {

        final List<PropertyAccessor> accessors = new ArrayList<PropertyAccessor>(4);
        accessors.add(VariablesMapPropertyAccessor.INSTANCE);
        accessors.add(BeansPropertyAccessor.INSTANCE);
        accessors.add(MAP_ACCESSOR_INSTANCE);
        THYMELEAF_PROPERTY_ACCESSORS = Collections.unmodifiableList(accessors);

        final List<PropertyAccessor> defaultPlusThymeleafPropertyAccessors = new ArrayList<PropertyAccessor>(6);
        defaultPlusThymeleafPropertyAccessors.addAll(
                SpelVariableExpressionEvaluator.DEFAULT_EVALUATION_CONTEXT.getPropertyAccessors());
        defaultPlusThymeleafPropertyAccessors.addAll(THYMELEAF_PROPERTY_ACCESSORS);
        DEFAULT_PLUS_THYMELEAF_PROPERTY_ACCESSORS = defaultPlusThymeleafPropertyAccessors;

    }
    
    
    
    public SpelEvaluationContext(final EvaluationContext delegate, final Map<String,Object> variables) {
        
        super();
        
        this.delegate = delegate;
        this.variables = variables;

        if (delegate == SpelVariableExpressionEvaluator.DEFAULT_EVALUATION_CONTEXT) {

            // If we are using the default as delegate (which will happen 99,99% times, just
            // use the precomputed proeprty accessor list
            this.propertyAccessors = DEFAULT_PLUS_THYMELEAF_PROPERTY_ACCESSORS;

        } else {
        
            final List<PropertyAccessor> delegatePropertyAccessors = delegate.getPropertyAccessors();
            if (delegatePropertyAccessors == null || delegatePropertyAccessors.size() == 0) {
                this.propertyAccessors = THYMELEAF_PROPERTY_ACCESSORS;
            } else {
                this.propertyAccessors = new ArrayList<PropertyAccessor>(delegatePropertyAccessors);
                this.propertyAccessors.addAll(THYMELEAF_PROPERTY_ACCESSORS);
            }

        }
        
    }

    
    public TypedValue getRootObject() {
        return this.delegate.getRootObject();
    }

    public List<ConstructorResolver> getConstructorResolvers() {
        return this.delegate.getConstructorResolvers();
    }

    public List<MethodResolver> getMethodResolvers() {
        return this.delegate.getMethodResolvers();
    }

    public List<PropertyAccessor> getPropertyAccessors() {
        return this.propertyAccessors;
    }

    public TypeLocator getTypeLocator() {
        return this.delegate.getTypeLocator();
    }

    public TypeConverter getTypeConverter() {
        return this.delegate.getTypeConverter();
    }

    public TypeComparator getTypeComparator() {
        return this.delegate.getTypeComparator();
    }

    public OperatorOverloader getOperatorOverloader() {
        return this.delegate.getOperatorOverloader();
    }

    public BeanResolver getBeanResolver() {
        return this.delegate.getBeanResolver();
    }

    public void setVariable(final String name, final Object value) {
        this.variables.put(name, value);
    }

    public Object lookupVariable(final String name) {
        final Object result = this.variables.get(name);
        if (result != null) {
            return result;
        }
        // fail back to delegate
        return this.delegate.lookupVariable(name);
    }
    
}
