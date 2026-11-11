/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.standard.processor.StandardRemovableAttributeTagProcessor;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Processor for the {@code th:formaction} attribute, which lets Spring's {@code RequestDataValueProcessor}
 *   process the resulting URL as a form action, using the value of the {@code formmethod} attribute
 *   (or {@code GET} if absent) as HTTP method.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.1.7
 *
 */
public final class SpringFormActionTagProcessor
        extends AbstractStandardExpressionAttributeTagProcessor
        implements IAttributeDefinitionsAware {


    // Executed after th:formmethod (a removable attribute) so that its result is available
    public static final int ATTR_PRECEDENCE = StandardRemovableAttributeTagProcessor.PRECEDENCE + 10;
    public static final String TARGET_ATTR_NAME = "formaction";

    private static final TemplateMode TEMPLATE_MODE = TemplateMode.HTML;

    private static final String METHOD_ATTR_NAME = "formmethod";
    private static final String METHOD_ATTR_DEFAULT_VALUE = "GET";

    private AttributeDefinition targetAttributeDefinition;
    private AttributeDefinition methodAttributeDefinition;





    public SpringFormActionTagProcessor(final String dialectPrefix) {
        super(TEMPLATE_MODE, dialectPrefix, TARGET_ATTR_NAME, ATTR_PRECEDENCE, false, false);
    }




    public void setAttributeDefinitions(final AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        // We precompute the AttributeDefinitions in order to being able to use much
        // faster methods for setting/replacing attributes on the ElementAttributes implementation
        this.targetAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, TARGET_ATTR_NAME);
        this.methodAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, METHOD_ATTR_NAME);
    }




    @Override
    protected final void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final Object expressionResult,
            final IElementTagStructureHandler structureHandler) {

        String newAttributeValue =
                EscapedAttributeUtils.escapeAttribute(TEMPLATE_MODE, expressionResult == null ? null : expressionResult.toString());

        // As with the rest of removable attributes, an empty result removes the target attribute
        if (newAttributeValue == null || newAttributeValue.length() == 0) {
            structureHandler.removeAttribute(this.targetAttributeDefinition.getAttributeName());
            structureHandler.removeAttribute(attributeName);
            return;
        }

        final String methodAttributeValue = tag.getAttributeValue(this.methodAttributeDefinition.getAttributeName());
        final String httpMethod = methodAttributeValue == null ? METHOD_ATTR_DEFAULT_VALUE : methodAttributeValue;

        // Let RequestDataValueProcessor modify the attribute value if needed
        newAttributeValue = RequestDataValueProcessorUtils.processAction(context, newAttributeValue, httpMethod);

        StandardProcessorUtils.replaceAttribute(
                structureHandler, attributeName, this.targetAttributeDefinition, TARGET_ATTR_NAME, (newAttributeValue == null? "" : newAttributeValue));

    }



}
