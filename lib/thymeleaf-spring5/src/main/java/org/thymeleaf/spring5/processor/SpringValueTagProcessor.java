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

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;
import org.unbescape.html.HtmlEscape;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public final class SpringValueTagProcessor
        extends AbstractStandardExpressionAttributeTagProcessor
        implements IAttributeDefinitionsAware {


    // This is 1010 in order to make sure it is executed after "name" and "type"
    public static final int ATTR_PRECEDENCE = 1010;
    public static final String TARGET_ATTR_NAME = "value";

    private static final TemplateMode TEMPLATE_MODE = TemplateMode.HTML;

    private static final String TYPE_ATTR_NAME = "type";
    private static final String NAME_ATTR_NAME = "name";

    private AttributeDefinition targetAttributeDefinition;
    private AttributeDefinition fieldAttributeDefinition;
    private AttributeDefinition typeAttributeDefinition;
    private AttributeDefinition nameAttributeDefinition;




    public SpringValueTagProcessor(final String dialectPrefix) {
        super(TEMPLATE_MODE, dialectPrefix, TARGET_ATTR_NAME, ATTR_PRECEDENCE, false, false);
    }




    public void setAttributeDefinitions(final AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        // We precompute the AttributeDefinitions in order to being able to use much
        // faster methods for setting/replacing attributes on the ElementAttributes implementation
        final String dialectPrefix = getMatchingAttributeName().getMatchingAttributeName().getPrefix();
        this.targetAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, TARGET_ATTR_NAME);
        this.fieldAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, dialectPrefix, AbstractSpringFieldTagProcessor.ATTR_NAME);
        this.typeAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, TYPE_ATTR_NAME);
        this.nameAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, NAME_ATTR_NAME);
    }




    @Override
    protected final void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final Object expressionResult,
            final IElementTagStructureHandler structureHandler) {

        String newAttributeValue = HtmlEscape.escapeHtml4Xml(expressionResult == null ? "" : expressionResult.toString());

        // Let RequestDataValueProcessor modify the attribute value if needed, but only in the case we don't also have
        // a 'th:field' - in such case, we will let th:field do its job
        if (!tag.hasAttribute(this.fieldAttributeDefinition.getAttributeName())) {

            // We will need to know the 'name' and 'type' attribute values in order to (potentially) modify the 'value'
            final String nameValue = tag.getAttributeValue(this.nameAttributeDefinition.getAttributeName());
            final String typeValue = tag.getAttributeValue(this.typeAttributeDefinition.getAttributeName());

            newAttributeValue =
                    RequestDataValueProcessorUtils.processFormFieldValue(context, nameValue, newAttributeValue, typeValue);

        }

        // Set the 'value' attribute
        StandardProcessorUtils.replaceAttribute(structureHandler, attributeName, this.targetAttributeDefinition, TARGET_ATTR_NAME, (newAttributeValue == null? "" : newAttributeValue));

    }


}
