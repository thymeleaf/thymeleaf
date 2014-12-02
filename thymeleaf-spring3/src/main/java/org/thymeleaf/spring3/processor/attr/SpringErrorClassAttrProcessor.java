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
package org.thymeleaf.spring3.processor.attr;

import java.util.Arrays;

import org.springframework.web.servlet.support.BindStatus;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;
import org.thymeleaf.spring3.util.FieldUtils;
import org.thymeleaf.standard.expression.VariableExpression;
import org.thymeleaf.standard.processor.attr.AbstractStandardSingleAttributeModifierAttrProcessor;
import org.thymeleaf.util.StringUtils;

/**
 * Adds the given class to the field on which this attribute is applied, if that
 * field contains errors.  It's similar to a combination of <tt>th:classappend</tt>
 * with a <tt>${#fields.hasErrors()}</tt> expression.
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 2.1.0
 */
public final class SpringErrorClassAttrProcessor
        extends AbstractStandardSingleAttributeModifierAttrProcessor {

    public static final int ATTR_PRECEDENCE = 1500;
    public static final String ATTR_NAME = "errorclass";
    public static final String TARGET_ATTR_NAME = "class";



    public SpringErrorClassAttrProcessor() {
        super(ATTR_NAME);
    }



    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }





    @Override
    protected String getTargetAttributeName(
            final Arguments arguments, final Element element, final String attributeName) {
        return TARGET_ATTR_NAME;
    }


    @Override
    protected String getTargetAttributeValue(
            final Arguments arguments, final Element element, final String attributeName) {

        final BindStatus bindStatus = computeBindStatus(arguments, element, attributeName);
        if (bindStatus == null) {
            final String[] fieldProcessorNames =
                    Attribute.applyPrefixToAttributeName(
                            AbstractSpringFieldAttrProcessor.ATTR_NAME, Attribute.getPrefixFromAttributeName(attributeName));
            throw new TemplateProcessingException(
                    "Cannot apply \"" + attributeName + "\": this attribute requires the existence of " +
                            "a \"name\" (or " + Arrays.asList(fieldProcessorNames) + ") attribute with non-empty " +
                            "value in the same host tag.");
        }

        if (bindStatus.isError()) {

            // Compute the CSS class to be applied exactly as it would in a normal th:classappend processor
            return super.getTargetAttributeValue(arguments, element, attributeName);

        }

        return "";

    }



    /*
     * There are two scenarios for a th:errorclass to appear in: one is in an element for which a th:field has already
     * been executed, in which case we already have a BindStatus to check for errors; and the other one is an element
     * for which a th:field has not been executed, but which should have a "name" attribute (either directly or as
     * the result of executing a th:name) -- in this case, we'll have to build the BuildStatus ourselves.
     */
    private static BindStatus computeBindStatus(final Arguments arguments, final Element element, final String attributeName) {

        /*
         * First, try to obtain an already-existing BindStatus resulting from the execution of a th:field attribute
         * in the same element.
         */
        final BindStatus bindStatus =
                (BindStatus) arguments.getLocalVariable(SpringContextVariableNames.SPRING_FIELD_BIND_STATUS);
        if (bindStatus != null) {
            return bindStatus;
        }

        /*
         * It seems no th:field was executed on the same element, so we must rely on the "name" attribute (probably
         * specified by hand or by a th:name). No th:field was executed, so no BindStatus available -- we'll have to
         * build it ourselves.
         */
        final String fieldName = element.getAttributeValue("name");
        if (StringUtils.isEmptyOrWhitespace(fieldName)) {
            return null;
        }

        final VariableExpression boundExpression =
                (VariableExpression) arguments.getLocalVariable(SpringContextVariableNames.SPRING_BOUND_OBJECT_EXPRESSION);

        if (boundExpression == null) {
            // No bound expression, so just use the field name
            return FieldUtils.getBindStatusFromParsedExpression(arguments.getConfiguration(), arguments, false, fieldName);
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
        return FieldUtils.getBindStatusFromParsedExpression(arguments.getConfiguration(), arguments, false, computedFieldName);

    }





    @Override
    protected ModificationType getModificationType(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return ModificationType.APPEND_WITH_SPACE;
    }





    @Override
    protected boolean removeAttributeIfEmpty(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return true;
    }



}
