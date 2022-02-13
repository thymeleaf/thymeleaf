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

import java.util.LinkedHashMap;
import java.util.Map;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
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
public final class SpringMethodTagProcessor
            extends AbstractStandardExpressionAttributeTagProcessor
            implements IAttributeDefinitionsAware {


    public static final int ATTR_PRECEDENCE = 990;
    public static final String TARGET_ATTR_NAME = "method";

    private static final TemplateMode TEMPLATE_MODE = TemplateMode.HTML;

    private static final String TYPE_ATTR_NAME = "type";
    private static final String NAME_ATTR_NAME = "name";
    private static final String VALUE_ATTR_NAME = "value";

    private AttributeDefinition targetAttributeDefinition;




    public SpringMethodTagProcessor(final String dialectPrefix) {
        super(TEMPLATE_MODE, dialectPrefix, TARGET_ATTR_NAME, ATTR_PRECEDENCE, false, false);
    }




    public void setAttributeDefinitions(final AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        // We precompute the AttributeDefinitions in order to being able to use much
        // faster methods for setting/replacing attributes on the ElementAttributes implementation
        this.targetAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, TARGET_ATTR_NAME);
    }




    @Override
    protected final void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final Object expressionResult,
            final IElementTagStructureHandler structureHandler) {

        final String newAttributeValue = HtmlEscape.escapeHtml4Xml(expressionResult == null ? null : expressionResult.toString());

        // Set the 'method' attribute, or remove it if evaluated to null
        if (newAttributeValue == null || newAttributeValue.length() == 0) {
            structureHandler.removeAttribute(this.targetAttributeDefinition.getAttributeName());
            structureHandler.removeAttribute(attributeName);
        } else {
            StandardProcessorUtils.replaceAttribute(structureHandler, attributeName, this.targetAttributeDefinition, TARGET_ATTR_NAME, newAttributeValue);
        }

        // If this th:action is in a <form> tag, we might need to add a hidden field for non-supported HTTP methods
        if (newAttributeValue != null && "form".equalsIgnoreCase(tag.getElementCompleteName())) {

            if (!isMethodBrowserSupported(newAttributeValue)) {

                // Browsers only support HTTP GET and POST. If a different method
                // has been specified, then Spring MVC allows us to specify it
                // using a hidden input with name '_method' and set 'post' for the
                // <form> tag.

                StandardProcessorUtils.setAttribute(structureHandler, this.targetAttributeDefinition, TARGET_ATTR_NAME, "post");

                final IModelFactory modelFactory = context.getModelFactory();

                final IModel hiddenMethodModel = modelFactory.createModel();

                final String type = "hidden";
                final String name = "_method";
                final String value = RequestDataValueProcessorUtils.processFormFieldValue(context, name, newAttributeValue, type);

                final Map<String,String> hiddenAttributes = new LinkedHashMap<String,String>(4,1.0f);
                hiddenAttributes.put(TYPE_ATTR_NAME, type);
                hiddenAttributes.put(NAME_ATTR_NAME, name);
                hiddenAttributes.put(VALUE_ATTR_NAME, value); // no need to escape

                final IStandaloneElementTag hiddenMethodElementTag =
                        modelFactory.createStandaloneElementTag("input", hiddenAttributes, AttributeValueQuotes.DOUBLE, false, true);

                hiddenMethodModel.add(hiddenMethodElementTag);

                structureHandler.insertImmediatelyAfter(hiddenMethodModel, false);

            }

        }

    }




    /*
     * Determine if the HTTP method is supported by browsers (i.e. GET or POST).
     */
    protected boolean isMethodBrowserSupported(final String method) {
        return ("get".equalsIgnoreCase(method) || "post".equalsIgnoreCase(method));
    }


}
