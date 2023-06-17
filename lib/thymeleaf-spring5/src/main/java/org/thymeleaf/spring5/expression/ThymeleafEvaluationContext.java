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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.MethodExecutor;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypeLocator;
import org.springframework.expression.spel.support.ReflectiveMethodResolver;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.expression.spel.support.StandardTypeLocator;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.spring5.view.ThymeleafView;
import org.thymeleaf.standard.expression.IExpressionClassAccessEvaluator;
import org.thymeleaf.util.ExpressionUtils;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Thymeleaf's basic implementation of the {@link IThymeleafEvaluationContext} interface, which in turn extends
 *   from Spring's {@link org.springframework.expression.EvaluationContext} interface.
 * </p>
 * <p>
 *   This implementation adds Thymeleaf's own property accessors
 *   (see {@link org.springframework.expression.PropertyAccessor}) for accessing
 *   the {@link org.thymeleaf.context.IContext} object in which variables are stored.
 * </p>
 * <p>
 *   Also, this evaluation context (which is usually instanced at the
 *   {@link ThymeleafView} initialization) links the execution of expressions
 *   with the available {@link BeanFactory} and {@link ConversionService} instances, used during evaluation.
 * </p>
 * <p>
 *   Before executing a Spring EL expression using this evaluation context, it should be enriched with the
 *   variables to be made accessible (like {@code #variableName}), using a
 *   {@link ThymeleafEvaluationContextWrapper} object.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public final class ThymeleafEvaluationContext
            extends StandardEvaluationContext
            implements IThymeleafEvaluationContext {

    public static final String THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME = "thymeleaf::EvaluationContext";


    private static final Map<Integer, ReflectivePropertyAccessor> REFLECTIVE_PROPERTY_ACCESSOR_INSTANCE_MAP = new HashMap<>();
    private static final MapAccessor MAP_ACCESSOR_INSTANCE = new MapAccessor();
    private static final Map<Integer, TypeLocator> TYPE_LOCATOR_MAP = new HashMap<>();
    private static final Map<Integer, List<MethodResolver>> METHOD_RESOLVERS_MAP = new HashMap<>();


    private final ApplicationContext applicationContext;
    
    private final IExpressionClassAccessEvaluator expressionClassAccessEvaluator;

    private IExpressionObjects expressionObjects = null;
    private boolean variableAccessRestricted = false;




    public ThymeleafEvaluationContext(final ApplicationContext applicationContext, final ConversionService conversionService, final IExpressionClassAccessEvaluator expressionClassAccessEvaluator) {

        super();

        Validate.notNull(applicationContext, "Application Context cannot be null");
        Validate.notNull(expressionClassAccessEvaluator, "Expression Class Access Evaluator cannot be null");
        // ConversionService CAN be null

        this.applicationContext = applicationContext;
        this.expressionClassAccessEvaluator = expressionClassAccessEvaluator;
        this.setBeanResolver(new BeanFactoryResolver(applicationContext));
        if (conversionService != null) {
            this.setTypeConverter(new StandardTypeConverter(conversionService));
        }
        
        final ReflectivePropertyAccessor reflectivePropertyAccessorInstance = REFLECTIVE_PROPERTY_ACCESSOR_INSTANCE_MAP.computeIfAbsent(
                Objects.hashCode(expressionClassAccessEvaluator), 
                (key) -> new ThymeleafEvaluationContextACLPropertyAccessor(expressionClassAccessEvaluator)
        );
        
        final TypeLocator typeLocator = TYPE_LOCATOR_MAP.computeIfAbsent(
                Objects.hashCode(expressionClassAccessEvaluator), 
                (key) -> new ThymeleafEvaluationContextACLTypeLocator(expressionClassAccessEvaluator)
        );
        
        final List<MethodResolver> methodResolvers = METHOD_RESOLVERS_MAP.computeIfAbsent(
                Objects.hashCode(expressionClassAccessEvaluator), 
                (key) -> Collections.singletonList(new ThymeleafEvaluationContextACLMethodResolver(expressionClassAccessEvaluator))
        );

        final List<PropertyAccessor> propertyAccessors = new ArrayList<>(5);
        propertyAccessors.add(SPELContextPropertyAccessor.INSTANCE);
        propertyAccessors.add(MAP_ACCESSOR_INSTANCE);
        propertyAccessors.add(reflectivePropertyAccessorInstance);
        this.setPropertyAccessors(propertyAccessors);

        // We need to establish a custom type locator in order to forbid access to certain dangerous classes in expressions
        this.setTypeLocator(typeLocator);

        // We need to establish a custom method resolver in order to forbid calling methods on any of the blocked classes
        this.setMethodResolvers(methodResolvers);

    }


    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }


    @Override
    public Object lookupVariable(final String name) {
        if (this.expressionObjects != null && this.expressionObjects.containsObject(name)) {
            final Object result = this.expressionObjects.getObject(name);
            if (result != null) {
                return result;
            }
        }
        // fall back to superclass
        return super.lookupVariable(name);
    }




    public boolean isVariableAccessRestricted() {
        return this.variableAccessRestricted;
    }

    public void setVariableAccessRestricted(final boolean restricted) {
        this.variableAccessRestricted = restricted;
    }

    public IExpressionObjects getExpressionObjects() {
        return this.expressionObjects;
    }

    public void setExpressionObjects(final IExpressionObjects expressionObjects) {
        this.expressionObjects = expressionObjects;
    }



    static final class ThymeleafEvaluationContextACLTypeLocator implements TypeLocator {

        private final TypeLocator typeLocator;
        
        private final IExpressionClassAccessEvaluator expressionClassAccessEvaluator;

        ThymeleafEvaluationContextACLTypeLocator(final IExpressionClassAccessEvaluator expressionClassAccessEvaluator) {
            this(new StandardTypeLocator(), expressionClassAccessEvaluator);
        }

        ThymeleafEvaluationContextACLTypeLocator(final TypeLocator typeLocator, final IExpressionClassAccessEvaluator expressionClassAccessEvaluator) {
            super();
            // typeLocator CAN be null
            this.typeLocator = typeLocator;
            this.expressionClassAccessEvaluator = expressionClassAccessEvaluator;
            
            if (this.typeLocator instanceof StandardTypeLocator) {
                // A default prefix on "java.lang" is added by default, but we will remove it in order to avoid
                // the filter forbidding all "java.lang.*" classes to be bypassed.
                ((StandardTypeLocator)this.typeLocator).removeImport("java.lang");
            }
        }

        @Override
        public Class<?> findType(final String typeName) throws EvaluationException {
            if (this.typeLocator == null) {
                throw new EvaluationException("Type could not be located (no type locator configured): " + typeName);
            }
            if (!expressionClassAccessEvaluator.isTypeAllowed(typeName)) {
                throw new EvaluationException(
                        String.format("Access is forbidden for type '%s' in this expression context.", typeName));
            }
            return this.typeLocator.findType(typeName);
        }

    }



    static final class ThymeleafEvaluationContextACLPropertyAccessor extends ReflectivePropertyAccessor {

        private final ReflectivePropertyAccessor propertyAccessor;
        
        private final IExpressionClassAccessEvaluator expressionClassAccessEvaluator;

        ThymeleafEvaluationContextACLPropertyAccessor(final IExpressionClassAccessEvaluator expressionClassAccessEvaluator) {
            this(null, expressionClassAccessEvaluator);
        }

        ThymeleafEvaluationContextACLPropertyAccessor(final ReflectivePropertyAccessor propertyAccessor, IExpressionClassAccessEvaluator expressionClassAccessEvaluator) {
            super(false); // allowWrite = false
            // propertyAccessor CAN be null
            this.propertyAccessor = propertyAccessor;
            this.expressionClassAccessEvaluator = expressionClassAccessEvaluator;
        }


        @Override
        public boolean canRead(final EvaluationContext context, final Object targetObject, final String name) throws AccessException {

            final boolean canRead;
            if (this.propertyAccessor != null) {
                canRead =  this.propertyAccessor.canRead(context, targetObject, name);
            } else {
                canRead = super.canRead(context, targetObject, name);
            }

            if (canRead) {
                // We need to perform the check on the getter equivalent to the member being called
                final String methodEquiv =
                        ("empty".equals(name) || "blank".equals(name)) ?
                            "is" + Character.toUpperCase(name.charAt(0)) + name.substring(1) :
                            "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);

                if (!expressionClassAccessEvaluator.isMemberAllowed(targetObject, methodEquiv)) {
                    throw new EvaluationException(
                            String.format(
                                    "Accessing member '%s' is forbidden for type '%s' in this expression context.",
                                    name, targetObject.getClass()));
                }
            }

            return canRead;

        }

    }



    static final class ThymeleafEvaluationContextACLMethodResolver extends ReflectiveMethodResolver {

        private final ReflectiveMethodResolver methodResolver;
        
        private final IExpressionClassAccessEvaluator expressionClassAccessEvaluator;

        ThymeleafEvaluationContextACLMethodResolver(final IExpressionClassAccessEvaluator expressionClassAccessEvaluator) {
            this(null, expressionClassAccessEvaluator);
        }

        ThymeleafEvaluationContextACLMethodResolver(final ReflectiveMethodResolver methodResolver, IExpressionClassAccessEvaluator expressionClassAccessEvaluator) {
            super();
            // methodResolver CAN be null
            this.methodResolver = methodResolver;
            this.expressionClassAccessEvaluator = expressionClassAccessEvaluator;
        }

        @Override
        public MethodExecutor resolve(
                final EvaluationContext context, final Object targetObject,
                final String name, final List<TypeDescriptor> argumentTypes) throws AccessException {

            final MethodExecutor methodExecutor;
            if (this.methodResolver != null) {
                methodExecutor = this.methodResolver.resolve(context, targetObject, name, argumentTypes);
            } else {
                methodExecutor = super.resolve(context, targetObject, name, argumentTypes);
            }

            if (methodExecutor != null) {
                if (!expressionClassAccessEvaluator.isMemberAllowed(targetObject, name)) {
                    throw new EvaluationException(
                            String.format(
                                    "Calling method '%s' is forbidden for type '%s' in this expression context.",
                                    name, targetObject.getClass()));
                }
            }

            return methodExecutor;

        }

    }

}
