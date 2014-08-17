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
package org.thymeleaf.spring4.expression;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Implementation of Spring's {@link org.springframework.expression.EvaluationContext}
 *   interface designed to wrap around another delegated implementation of this same interface,
 *   adding (if needed) the Thymeleaf-required
 *   {@link org.springframework.expression.PropertyAccessor} implementations and (optionally)
 *   a series of variables to be accessed like <tt>#variableName</tt> during expression evaluation.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.1.0
 *
 */
public final class ThymeleafEvaluationContextWrapper implements EvaluationContext {


    private final EvaluationContext delegate;
    private final List<PropertyAccessor> propertyAccessors;
    private Map<String,Object> additionalVariables;

    public static final MapAccessor MAP_ACCESSOR_INSTANCE = new MapAccessor();

    public ThymeleafEvaluationContextWrapper(final EvaluationContext delegate) {
        this(delegate, null);
    }


    public ThymeleafEvaluationContextWrapper(final EvaluationContext delegate, final Map<String,Object> additionalVariables) {
        
        super();

        Validate.notNull(delegate, "Evaluation context delegate cannot be null");

        this.delegate = delegate;

        if (this.delegate instanceof ThymeleafEvaluationContext) {
            this.propertyAccessors = this.delegate.getPropertyAccessors();
        } else {
            this.propertyAccessors = new ArrayList<PropertyAccessor>(5);
            this.propertyAccessors.addAll(this.delegate.getPropertyAccessors());
            this.propertyAccessors.add(VariablesMapPropertyAccessor.INSTANCE);
            this.propertyAccessors.add(BeansPropertyAccessor.INSTANCE);
            this.propertyAccessors.add(MAP_ACCESSOR_INSTANCE);
        }

        this.additionalVariables = additionalVariables;

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
        if (this.additionalVariables == null) {
            this.additionalVariables = new HashMap<String, Object>(5, 1.0f);
        }
        this.additionalVariables.put(name, value);
    }

    public Object lookupVariable(final String name) {
        if (this.additionalVariables != null && this.additionalVariables.containsKey(name)) {
            final Object result = this.additionalVariables.get(name);
            if (result != null) {
                return result;
            }
        }
        // fail back to delegate
        return this.delegate.lookupVariable(name);
    }
    
}
