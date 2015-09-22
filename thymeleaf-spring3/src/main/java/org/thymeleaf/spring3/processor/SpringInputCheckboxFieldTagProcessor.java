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

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.form.SelectedValueComparatorWrapper;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring3.requestdata.RequestDataValueProcessorUtils;
import org.unbescape.html.HtmlEscape;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class SpringInputCheckboxFieldTagProcessor
        extends AbstractSpringFieldTagProcessor {

    
    public static final String CHECKBOX_INPUT_TYPE_ATTR_VALUE = "checkbox";
    

    
    public SpringInputCheckboxFieldTagProcessor(final IProcessorDialect dialect, final String dialectPrefix) {
        super(dialect, dialectPrefix, INPUT_TAG_NAME, INPUT_TYPE_ATTR_NAME, new String[] { CHECKBOX_INPUT_TYPE_ATTR_VALUE }, true);
    }




    @Override
    protected void doProcess(final ITemplateProcessingContext processingContext, final IProcessableElementTag tag,
                             final AttributeName attributeName, final String attributeValue,
                             final String attributeTemplateName, final int attributeLine, final int attributeCol,
                             final BindStatus bindStatus, final IElementTagStructureHandler structureHandler) {

        String name = bindStatus.getExpression();
        name = (name == null? "" : name);

        final String id = computeId(processingContext, tag, name, true);

        String value = null;
        boolean checked = false;

        Object boundValue = bindStatus.getValue();
        final Class<?> valueType = bindStatus.getValueType();


        if (Boolean.class.equals(valueType) || boolean.class.equals(valueType)) {

            if (boundValue instanceof String) {
                boundValue = Boolean.valueOf((String) boundValue);
            }
            final Boolean booleanValue = (boundValue != null ? (Boolean) boundValue : Boolean.FALSE);
            value = "true";
            checked = booleanValue.booleanValue();

        } else {

            value = tag.getAttributes().getValue("value");
            if (value == null) {
                throw new TemplateProcessingException(
                        "Attribute \"value\" is required in \"input(checkbox)\" tags " +
                                "when binding to non-boolean values");
            }

            checked = SelectedValueComparatorWrapper.isSelected(bindStatus, HtmlEscape.unescapeHtml(value));

        }

        tag.getAttributes().setAttribute("id", id); // No need to escape: this comes from an existing 'id' or from a token
        tag.getAttributes().setAttribute("name", name); // No need to escape: this is a java-valid token
        tag.getAttributes().setAttribute(
                "value", RequestDataValueProcessorUtils.processFormFieldValue(processingContext, name, value, "checkbox"));
        if (checked) {
            tag.getAttributes().setAttribute("checked", "checked");
        } else {
            tag.getAttributes().removeAttribute("checked");
        }


        if (!isDisabled(tag)) {

            /*
             * Non-disabled checkboxes need an additional <input type="hidden"> in order to note their presence in
             * the HTML document. Given unchecked checkboxes are not sent by browsers as a result of form submission,
             * this is the only way to differentiate between a checkbox that is unchecked and a checkbox that was
             * never displayed or is disabled.
             */

            final IModel hiddenTagModel = processingContext.getModelFactory().createModel();

            final String hiddenName = WebDataBinder.DEFAULT_FIELD_MARKER_PREFIX + name;
            final String hiddenValue = "on";

            final IStandaloneElementTag hiddenTag =
                    processingContext.getModelFactory().createStandaloneElementTag(INPUT_TAG_NAME, true);
            hiddenTag.getAttributes().setAttribute("type", "hidden");
            hiddenTag.getAttributes().setAttribute("name", hiddenName);
            hiddenTag.getAttributes().setAttribute(
                    "value", RequestDataValueProcessorUtils.processFormFieldValue(processingContext, hiddenName, hiddenValue, "hidden"));

            hiddenTagModel.add(hiddenTag);

            structureHandler.insertImmediatelyAfter(hiddenTagModel, false);

        }


    }


    private static final boolean isDisabled(final IProcessableElementTag tag) {
        // Disabled = attribute "disabled" exists
        return tag.getAttributes().hasAttribute("disabled");
    }

    

}
