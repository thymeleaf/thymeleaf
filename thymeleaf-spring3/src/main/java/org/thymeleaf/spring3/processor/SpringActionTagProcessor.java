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
package org.thymeleaf.spring3.processor;

import java.util.Map;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.model.IElementAttributes;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring3.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;
import org.unbescape.html.HtmlEscape;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class  SpringActionTagProcessor
        extends AbstractStandardExpressionAttributeTagProcessor
        implements IAttributeDefinitionsAware {


    public static final int ATTR_PRECEDENCE = 1000;
    public static final String TARGET_ATTR_NAME = "action";

    private static final TemplateMode TEMPLATE_MODE = TemplateMode.HTML;

    private static final String METHOD_ATTR_NAME = "method";
    private static final String TYPE_ATTR_NAME = "type";
    private static final String NAME_ATTR_NAME = "name";
    private static final String VALUE_ATTR_NAME = "value";

    private AttributeDefinition targetAttributeDefinition;
    private AttributeDefinition methodAttributeDefinition;
    private AttributeDefinition typeAttributeDefinition;
    private AttributeDefinition nameAttributeDefinition;
    private AttributeDefinition valueAttributeDefinition;





    public SpringActionTagProcessor(final String dialectPrefix) {
        super(TEMPLATE_MODE, dialectPrefix, TARGET_ATTR_NAME, ATTR_PRECEDENCE, false);
    }




    public void setAttributeDefinitions(final AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        // We precompute the AttributeDefinitions in order to being able to use much
        // faster methods for setting/replacing attributes on the ElementAttributes implementation
        this.targetAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, TARGET_ATTR_NAME);
        this.methodAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, METHOD_ATTR_NAME);
        this.typeAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, TYPE_ATTR_NAME);
        this.nameAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, NAME_ATTR_NAME);
        this.valueAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, VALUE_ATTR_NAME);
    }




    @Override
    protected final void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final Object expressionResult,
            final IElementTagStructureHandler structureHandler) {

        String newAttributeValue = HtmlEscape.escapeHtml4Xml(expressionResult == null ? "" : expressionResult.toString());

        final IElementAttributes attributes = tag.getAttributes();

        // But before setting the 'action' attribute, we need to verify the 'method' attribute and let the
        // RequestDataValueProcessor act on it.
        final String httpMethod = attributes.getValue(this.methodAttributeDefinition.getAttributeName());

        // Let RequestDataValueProcessor modify the attribute value if needed
        newAttributeValue = RequestDataValueProcessorUtils.processAction(context, newAttributeValue, httpMethod);

        // Set the 'action' attribute
        StandardProcessorUtils.replaceAttribute(
                attributes, attributeName, this.targetAttributeDefinition, TARGET_ATTR_NAME, (newAttributeValue == null? "" : newAttributeValue));

        // If this th:action is in a <form> tag, we might need to add a hidden field (depending on Spring configuration)
        if ("form".equalsIgnoreCase(tag.getElementName())) {

            final Map<String,String> extraHiddenFields =
                    RequestDataValueProcessorUtils.getExtraHiddenFields(context);

            if (extraHiddenFields != null && extraHiddenFields.size() > 0) {

                final IModelFactory modelFactory = context.getConfiguration().getModelFactory(context.getTemplateMode());

                final IModel extraHiddenElementTags = modelFactory.createModel();

                for (final Map.Entry<String,String> extraHiddenField : extraHiddenFields.entrySet()) {

                    final IStandaloneElementTag extraHiddenElementTag =
                            modelFactory.createStandaloneElementTag("input", true);

                    final IElementAttributes extraHiddenElementTagAttributes = extraHiddenElementTag.getAttributes();

                    StandardProcessorUtils.setAttribute(extraHiddenElementTagAttributes, this.typeAttributeDefinition, TYPE_ATTR_NAME, "hidden");
                    StandardProcessorUtils.setAttribute(extraHiddenElementTagAttributes, this.nameAttributeDefinition, NAME_ATTR_NAME, extraHiddenField.getKey());
                    StandardProcessorUtils.setAttribute(extraHiddenElementTagAttributes, this.valueAttributeDefinition, VALUE_ATTR_NAME, extraHiddenField.getValue()); // no need to re-apply the processor here

                    extraHiddenElementTags.add(extraHiddenElementTag);

                }

                structureHandler.insertImmediatelyAfter(extraHiddenElementTags, false);

            }

        }

    }



}
