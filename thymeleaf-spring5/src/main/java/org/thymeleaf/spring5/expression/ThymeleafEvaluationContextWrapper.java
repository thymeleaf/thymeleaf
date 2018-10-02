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
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.standard.expression.RestrictedRequestAccessUtils;
import org.thymeleaf.standard.expression.StandardExpressionObjectFactory;
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

    private IExpressionObjects expressionObjects = null;
    private boolean requestParametersRestricted = false;
    private Map<String,Object> additionalVariables = null;




    public ThymeleafEvaluationContextWrapper(final EvaluationContext delegate) {
        
        super();

        Validate.notNull(delegate, "Evaluation context delegate cannot be null");

        this.delegate = delegate;

        if ((this.delegate instanceof ThymeleafEvaluationContext)) {

            this.propertyAccessors = null; // No need to initialize our own list

        } else if (this.delegate instanceof StandardEvaluationContext) {

            ((StandardEvaluationContext) this.delegate).addPropertyAccessor(SPELContextPropertyAccessor.INSTANCE);
            ((StandardEvaluationContext) this.delegate).addPropertyAccessor(MAP_ACCESSOR_INSTANCE);
            this.propertyAccessors = null; // No need to initialize our own list

        } else {

            // We don't know the specific EvaluationContext implementation, so we need to initialize our own PA list

            this.propertyAccessors = new ArrayList<PropertyAccessor>(5);
            this.propertyAccessors.addAll(this.delegate.getPropertyAccessors());
            this.propertyAccessors.add(SPELContextPropertyAccessor.INSTANCE);
            this.propertyAccessors.add(MAP_ACCESSOR_INSTANCE);

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
        return this.propertyAccessors == null ? this.delegate.getPropertyAccessors() : this.propertyAccessors;
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

        if (this.expressionObjects != null && this.expressionObjects.containsObject(name)) {

            final Object result = this.expressionObjects.getObject(name);
            if (result != null) {

                // We need to first check if we are in a restricted environment. If so, restrict access to the request.
                if (this.requestParametersRestricted &&
                        (StandardExpressionObjectFactory.REQUEST_EXPRESSION_OBJECT_NAME.equals(name) ||
                                StandardExpressionObjectFactory.HTTP_SERVLET_REQUEST_EXPRESSION_OBJECT_NAME.equals(name))) {
                    return RestrictedRequestAccessUtils.wrapRequestObject(result);
                }

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
