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
package org.thymeleaf.spring5.expression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.springframework.expression.spel.support.ReflectiveMethodResolver;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext.ThymeleafEvaluationContextACLMethodResolver;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext.ThymeleafEvaluationContextACLPropertyAccessor;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext.ThymeleafEvaluationContextACLTypeLocator;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Implementation of Thymeleaf's {@link IThymeleafEvaluationContext} interface designed to wrap around a
 *   delegated implementation of {@link EvaluationContext}, adding the
 *   Thymeleaf-required {@link PropertyAccessor} implementations and (optionally)
 *   a series of variables to be accessed like {@code #variableName} during expression evaluation.
 * </p>
 * <p>
 *   Note a class with this name existed since 2.1.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public final class ThymeleafEvaluationContextWrapper implements IThymeleafEvaluationContext {


    private static final MapAccessor MAP_ACCESSOR_INSTANCE = new MapAccessor();


    private final EvaluationContext delegate;
    private final List<PropertyAccessor> propertyAccessors; // can be initialized to null if we can delegate
    private final TypeLocator typeLocator;                  // can be initialized to null if we can delegate
    private final List<MethodResolver> methodResolvers;     // can be initialized to null if we can delegate

    private IExpressionObjects expressionObjects = null;
    private boolean requestParametersRestricted = false;
    private Map<String,Object> additionalVariables = null;




    public ThymeleafEvaluationContextWrapper(final EvaluationContext delegate) {
        
        super();

        Validate.notNull(delegate, "Evaluation context delegate cannot be null");

        this.delegate = delegate;

        if (this.delegate instanceof ThymeleafEvaluationContext) {

            this.propertyAccessors = null; // No need to initialize our own property accessors
            this.typeLocator = null;       // No need to initialize our own type locator
            this.methodResolvers = null;   // No need to initialize our own method resolvers

        } else {

            // We need to wrap any reflective method resolvers in order to forbid calling methods on any of the blocked classes
            this.propertyAccessors =
                    Stream.concat(
                            Stream.of(SPELContextPropertyAccessor.INSTANCE, MAP_ACCESSOR_INSTANCE),
                            this.delegate.getPropertyAccessors().stream()
                                    .map(pa -> (pa instanceof ReflectivePropertyAccessor) ?
                                            new ThymeleafEvaluationContextACLPropertyAccessor((ReflectivePropertyAccessor) pa) : pa))
                        .collect(Collectors.toList());

            // We need to establish a custom type locator in order to forbid access to certain dangerous classes in expressions
            this.typeLocator =
                    new ThymeleafEvaluationContextACLTypeLocator(this.delegate.getTypeLocator());

            // We need to wrap any reflective method resolvers in order to forbid calling methods on any of the blocked classes
            this.methodResolvers =
                    this.delegate.getMethodResolvers().stream()
                        .map(mr -> (mr instanceof ReflectiveMethodResolver) ?
                                new ThymeleafEvaluationContextACLMethodResolver((ReflectiveMethodResolver) mr) : mr)
                        .collect(Collectors.toList());

        }

    }

    
    public TypedValue getRootObject() {
        return this.delegate.getRootObject();
    }

    public List<ConstructorResolver> getConstructorResolvers() {
        return this.delegate.getConstructorResolvers();
    }

    public List<MethodResolver> getMethodResolvers() {
        return this.methodResolvers == null ? this.delegate.getMethodResolvers() : this.methodResolvers;
    }

    public List<PropertyAccessor> getPropertyAccessors() {
        return this.propertyAccessors == null ? this.delegate.getPropertyAccessors() : this.propertyAccessors;
    }

    public TypeLocator getTypeLocator() {
        return this.typeLocator == null ? this.delegate.getTypeLocator() : this.typeLocator;
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

        if (this.expressionObjects != null && this.expressionObjects.containsObject(name)) {
            final Object result = this.expressionObjects.getObject(name);
            if (result != null) {
                return result;
            }
        }

        if (this.additionalVariables != null && this.additionalVariables.containsKey(name)) {
            final Object result = this.additionalVariables.get(name);
            if (result != null) {
                return result;
            }
        }

        // fall back to delegate
        return this.delegate.lookupVariable(name);

    }


    public boolean isVariableAccessRestricted() {
        return this.requestParametersRestricted;
    }

    public void setVariableAccessRestricted(final boolean restricted) {
        this.requestParametersRestricted = restricted;
    }

    public IExpressionObjects getExpressionObjects() {
        return this.expressionObjects;
    }

    public void setExpressionObjects(final IExpressionObjects expressionObjects) {
        this.expressionObjects = expressionObjects;
    }


}
