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
package org.thymeleaf.spring5.processor;

import java.util.Arrays;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.AttributeNames;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;
import org.thymeleaf.spring5.util.FieldUtils;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.expression.VariableExpression;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;
import org.unbescape.html.HtmlEscape;

/**
 * Adds the given class to the field on which this attribute is applied, if that
 * field contains errors.  It's similar to a combination of {@code th:classappend}
 * with a {@code ${#fields.hasErrors()}} expression.
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.3
 */
public final class SpringErrorClassTagProcessor
        extends AbstractAttributeTagProcessor
        implements IAttributeDefinitionsAware {

    public static final int ATTR_PRECEDENCE = 1800;
    public static final String ATTR_NAME = "errorclass";
    public static final String TARGET_ATTR_NAME = "class";

    private static final TemplateMode TEMPLATE_MODE = TemplateMode.HTML;

    private AttributeDefinition targetAttributeDefinition;




    public SpringErrorClassTagProcessor(final String dialectPrefix) {
        super(TEMPLATE_MODE, dialectPrefix, null, false, ATTR_NAME, true,ATTR_PRECEDENCE, true);
    }




    public void setAttributeDefinitions(final AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        // We precompute the AttributeDefinition of the target attribute in order to being able to use much
        // faster methods for setting/replacing attributes on the ElementAttributes implementation
        this.targetAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, TARGET_ATTR_NAME);
    }




    @Override
    protected final void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IElementTagStructureHandler structureHandler) {

        final IThymeleafBindStatus bindStatus = computeBindStatus(context, tag);
        if (bindStatus == null) {
            final AttributeName fieldAttributeName =
                    AttributeNames.forHTMLName(attributeName.getPrefix(), AbstractSpringFieldTagProcessor.ATTR_NAME);
            throw new TemplateProcessingException(
                    "Cannot apply \"" + attributeName + "\": this attribute requires the existence of " +
                    "a \"name\" (or " + Arrays.asList(fieldAttributeName.getCompleteAttributeNames()) + ") attribute " +
                    "with non-empty value in the same host tag.");
        }

        if (bindStatus.isError()) {

            final IEngineConfiguration configuration = context.getConfiguration();
            final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);

            final IStandardExpression expression = expressionParser.parseExpression(context, attributeValue);
            final Object expressionResult = expression.execute(context);

            String newAttributeValue = HtmlEscape.escapeHtml4Xml(expressionResult == null ? null : expressionResult.toString());

            // If we are not adding anything, we'll just leave it untouched
            if (newAttributeValue != null && newAttributeValue.length() > 0) {

                final AttributeName targetAttributeName = this.targetAttributeDefinition.getAttributeName();

                if (tag.hasAttribute(targetAttributeName)) {
                    final String currentValue = tag.getAttributeValue(targetAttributeName);
                    if (currentValue.length() > 0) {
                        newAttributeValue = currentValue + ' ' + newAttributeValue;
                    }
                }

                StandardProcessorUtils.setAttribute(structureHandler, this.targetAttributeDefinition, TARGET_ATTR_NAME, newAttributeValue);

            }

        }

    }




    /*
     * There are two scenarios for a th:errorclass to appear in: one is in an element for which a th:field has already
     * been executed, in which case we already have a BindStatus to check for errors; and the other one is an element
     * for which a th:field has not been executed, but which should have a "name" attribute (either directly or as
     * the result of executing a th:name) -- in this case, we'll have to build the BuildStatus ourselves.
     */
    private static IThymeleafBindStatus computeBindStatus(final IExpressionContext context, final IProcessableElementTag tag) {

        /*
         * First, try to obtain an already-existing BindStatus resulting from the execution of a th:field attribute
         * in the same element.
         */
        final IThymeleafBindStatus bindStatus =
                (IThymeleafBindStatus) context.getVariable(SpringContextVariableNames.THYMELEAF_FIELD_BIND_STATUS);
        if (bindStatus != null) {
            return bindStatus;
        }

        /*
         * It seems no th:field was executed on the same element, so we must rely on the "name" attribute (probably
         * specified by hand or by a th:name). No th:field was executed, so no BindStatus available -- we'll have to
         * build it ourselves.
         */
        final String fieldName = tag.getAttributeValue("name");
        if (StringUtils.isEmptyOrWhitespace(fieldName)) {
            return null;
        }

        final VariableExpression boundExpression =
                (VariableExpression) context.getVariable(SpringContextVariableNames.SPRING_BOUND_OBJECT_EXPRESSION);

        if (boundExpression == null) {
            // No bound expression, so just use the field name
            return FieldUtils.getBindStatusFromParsedExpression(context, false, fieldName);
        }

        // Bound object and field object names might intersect (e.g. th:object="a.b", name="b.c"), and we must compute
        // the real 'bindable' name ("a.b.c") by only using the first token in the bound object name, appending the
        // rest of the field name: "a" + "b.c" -> "a.b.c"
        final String boundExpressionStr = boundExpression.getExpression();
        final String computedFieldName;
        if (boundExpressionStr.indexOf('.') == -1) {
            computedFieldName = boundExpressionStr + '.' + fieldName; // we append because we will use no form root afterwards
        } else {
            computedFieldName = boundExpressionStr.substring(0, boundExpressionStr.indexOf('.')) + '.' + fieldName;
        }

        // We set "useRoot" to false because we have already computed that part
        return FieldUtils.getBindStatusFromParsedExpression(context, false, computedFieldName);

    }


}
