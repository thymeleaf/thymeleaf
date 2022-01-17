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
package org.thymeleaf.spring3.expression;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypeLocator;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.expression.spel.support.StandardTypeLocator;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.standard.expression.RestrictedRequestAccessUtils;
import org.thymeleaf.standard.expression.StandardExpressionObjectFactory;
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
 *   {@link org.thymeleaf.spring3.view.ThymeleafView} initialization) links the execution of expressions
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
 * @since 2.1.0
 *
 */
public final class ThymeleafEvaluationContext
            extends StandardEvaluationContext
            implements IThymeleafEvaluationContext {

    public static final String THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME = "thymeleaf::EvaluationContext";


    private static final MapAccessor MAP_ACCESSOR_INSTANCE = new MapAccessor();
    private static final TypeLocator TYPE_LOCATOR = new ThymeleafEvaluationContextACLTypeLocator();


    private final ApplicationContext applicationContext;

    private IExpressionObjects expressionObjects = null;
    private boolean variableAccessRestricted = false;




    public ThymeleafEvaluationContext(final ApplicationContext applicationContext, final ConversionService conversionService) {
        
        super();

        Validate.notNull(applicationContext, "Application Context cannot be null");
        // ConversionService CAN be null

        this.applicationContext = applicationContext;
        this.setBeanResolver(new BeanFactoryResolver(applicationContext));
        if (conversionService != null) {
            this.setTypeConverter(new StandardTypeConverter(conversionService));
        }

        this.addPropertyAccessor(SPELContextPropertyAccessor.INSTANCE);
        this.addPropertyAccessor(MAP_ACCESSOR_INSTANCE);

        // We need to establish a custom type locator in order to forbid access to certain dangerous classes in expressions
        this.setTypeLocator(TYPE_LOCATOR);

    }


    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }


    @Override
    public Object lookupVariable(final String name) {

        if (this.expressionObjects != null && this.expressionObjects.containsObject(name)) {

            final Object result = this.expressionObjects.getObject(name);
            if (result != null) {

                // We need to first check if we are in a restricted environment. If so, restrict access to the request.
                if (this.variableAccessRestricted &&
                        (StandardExpressionObjectFactory.REQUEST_EXPRESSION_OBJECT_NAME.equals(name) ||
                                StandardExpressionObjectFactory.HTTP_SERVLET_REQUEST_EXPRESSION_OBJECT_NAME.equals(name))) {
                    return RestrictedRequestAccessUtils.wrapRequestObject(result);
                }

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

        private final StandardTypeLocator typeLocator;

        ThymeleafEvaluationContextACLTypeLocator() {
            super();
            this.typeLocator = new StandardTypeLocator();
            // A default prefix on "java.lang" is added by default, but we will remove it in order to avoid
            // the filter forbidding all "java.lang.*" classes to be bypassed.
            this.typeLocator.removeImport("java.lang");
        }

        @Override
        public Class<?> findType(final String typeName) throws EvaluationException {
            if (typeName != null && !ExpressionUtils.isTypeAllowed(typeName)) {
                throw new EvaluationException(
                        String.format(
                                "Access is forbidden for type '%s' in Thymeleaf expressions. " +
                                "Blacklisted packages are: %s. Whitelisted classes are: %s.",
                                typeName, ExpressionUtils.getBlacklist(), ExpressionUtils.getWhitelist()));
            }
            return this.typeLocator.findType(typeName);
        }

    }

}
