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
<<<<<<< HEAD
=======
import org.thymeleaf.IEngineConfiguration;
>>>>>>> 1b08adb4a3731da6645541808b99ed79cda36c40
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring3.requestdata.RequestDataValueProcessorUtils;


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



    public SpringSelectFieldTagProcessor(final IProcessorDialect dialect, final String dialectPrefix) {
        super(dialect, dialectPrefix, SELECT_TAG_NAME, null, null, true);
    }



    @Override
    protected void doProcess(final ITemplateContext context,
                             final IProcessableElementTag tag,
                             final AttributeName attributeName, final String attributeValue,
                             final String attributeTemplateName, final int attributeLine, final int attributeCol,
                             final BindStatus bindStatus, final IElementTagStructureHandler structureHandler) {

        String name = bindStatus.getExpression();
        name = (name == null? "" : name);

        final String id = computeId(context, tag, name, false);

        final boolean multiple = tag.getAttributes().hasAttribute("multiple");

        tag.getAttributes().setAttribute("id", id); // No need to escape: this comes from an existing 'id' or from a token
        tag.getAttributes().setAttribute("name", name); // No need to escape: this is a java-valid token

        structureHandler.setLocalVariable(OPTION_IN_SELECT_ATTR_NAME, attributeName);
        structureHandler.setLocalVariable(OPTION_IN_SELECT_ATTR_VALUE, attributeValue);

        if (multiple && !isDisabled(tag)) {

<<<<<<< HEAD
            final IModelFactory modelFactory = context.getConfiguration().getModelFactory(context.getTemplateMode());

            final IModel hiddenMethodElementModel = modelFactory.createModel();
=======
            final IModel hiddenMethodElementModel = context.getModelFactory().createModel();
>>>>>>> 1b08adb4a3731da6645541808b99ed79cda36c40

            final String hiddenName = WebDataBinder.DEFAULT_FIELD_MARKER_PREFIX + name;
            final String type = "hidden";
            final String value =
                    RequestDataValueProcessorUtils.processFormFieldValue(context, hiddenName, "1", type);

            final IStandaloneElementTag hiddenMethodElementTag =
<<<<<<< HEAD
                    modelFactory.createStandaloneElementTag("input", true);
=======
                    context.getModelFactory().createStandaloneElementTag("input", true);
>>>>>>> 1b08adb4a3731da6645541808b99ed79cda36c40
            hiddenMethodElementTag.getAttributes().setAttribute("type", type);
            hiddenMethodElementTag.getAttributes().setAttribute("name", hiddenName);
            hiddenMethodElementTag.getAttributes().setAttribute("value", value);

            hiddenMethodElementModel.add(hiddenMethodElementTag);

            // We insert this hidden before because <select>'s are open element (with body), and if we insert it
            // after the element, we would be inserting the <input type="hidden"> inside the <select>, which would
            // incorrect...
            structureHandler.insertBefore(hiddenMethodElementModel);

        }

    }



    private static final boolean isDisabled(final IProcessableElementTag tag) {
        // Disabled = attribute "disabled" exists
        return tag.getAttributes().hasAttribute("disabled");
    }


}
