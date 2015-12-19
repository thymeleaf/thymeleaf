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
package org.thymeleaf.spring4.processor;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.support.BindStatus;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IElementAttributes;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring4.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.standard.util.StandardProcessorUtils;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class SpringSelectFieldTagProcessor extends AbstractSpringFieldTagProcessor {


    static final String OPTION_IN_SELECT_ATTR_NAME = "%%OPTION_IN_SELECT_ATTR_NAME%%";
    static final String OPTION_IN_SELECT_ATTR_VALUE = "%%OPTION_IN_SELECT_ATTR_VALUE%%";



    public SpringSelectFieldTagProcessor(final String dialectPrefix) {
        super(dialectPrefix, SELECT_TAG_NAME, null, null, true);
    }



    @Override
    protected void doProcess(final ITemplateContext context,
                             final IProcessableElementTag tag,
                             final AttributeName attributeName, final String attributeValue,
                             final BindStatus bindStatus, final IElementTagStructureHandler structureHandler) {

        String name = bindStatus.getExpression();
        name = (name == null? "" : name);

        final String id = computeId(context, tag, name, false);

        final IElementAttributes attributes = tag.getAttributes();

        final boolean multiple = attributes.hasAttribute(this.multipleAttributeDefinition.getAttributeName());

        StandardProcessorUtils.setAttribute(attributes, this.idAttributeDefinition, ID_ATTR_NAME, id); // No need to escape: this comes from an existing 'id' or from a token
        StandardProcessorUtils.setAttribute(attributes, this.nameAttributeDefinition, NAME_ATTR_NAME, name); // No need to escape: this is a java-valid token

        structureHandler.setLocalVariable(OPTION_IN_SELECT_ATTR_NAME, attributeName);
        structureHandler.setLocalVariable(OPTION_IN_SELECT_ATTR_VALUE, attributeValue);

        if (multiple && !isDisabled(tag)) {

            final IModelFactory modelFactory = context.getConfiguration().getModelFactory(context.getTemplateMode());

            final IModel hiddenMethodElementModel = modelFactory.createModel();

            final String hiddenName = WebDataBinder.DEFAULT_FIELD_MARKER_PREFIX + name;
            final String type = "hidden";
            final String value =
                    RequestDataValueProcessorUtils.processFormFieldValue(context, hiddenName, "1", type);

            final IStandaloneElementTag hiddenMethodElementTag =
                    modelFactory.createStandaloneElementTag("input", true);
            final IElementAttributes hiddenMethodElementTagAttributes = hiddenMethodElementTag.getAttributes();

            StandardProcessorUtils.setAttribute(hiddenMethodElementTagAttributes, this.typeAttributeDefinition, TYPE_ATTR_NAME, type);
            StandardProcessorUtils.setAttribute(hiddenMethodElementTagAttributes, this.nameAttributeDefinition, NAME_ATTR_NAME, hiddenName);
            StandardProcessorUtils.setAttribute(hiddenMethodElementTagAttributes, this.valueAttributeDefinition, VALUE_ATTR_NAME, value);

            hiddenMethodElementModel.add(hiddenMethodElementTag);

            // We insert this hidden before because <select>'s are open element (with body), and if we insert it
            // after the element, we would be inserting the <input type="hidden"> inside the <select>, which would
            // incorrect...
            structureHandler.insertBefore(hiddenMethodElementModel);

        }

    }



    private final boolean isDisabled(final IProcessableElementTag tag) {
        // Disabled = attribute "disabled" exists
        return tag.getAttributes().hasAttribute(this.disabledAttributeDefinition.getAttributeName());
    }


}
