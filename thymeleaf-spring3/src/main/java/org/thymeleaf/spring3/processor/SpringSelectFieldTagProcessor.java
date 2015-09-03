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
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IElementStructureHandler;
import org.thymeleaf.engine.Markup;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
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



    public SpringSelectFieldTagProcessor(final String dialectPrefix) {
        super(dialectPrefix, SELECT_TAG_NAME, null, null);
    }



    @Override
    protected void doProcess(final ITemplateProcessingContext processingContext, final IProcessableElementTag tag,
                             final AttributeName attributeName, final String attributeValue,
                             final BindStatus bindStatus, final IElementStructureHandler structureHandler) {

        String name = bindStatus.getExpression();
        name = (name == null? "" : name);

        final String id = computeId(processingContext, tag, name, false);

        final boolean multiple = tag.getAttributes().hasAttribute("multiple");

        tag.getAttributes().setAttribute("id", id); // No need to escape: this comes from an existing 'id' or from a token
        tag.getAttributes().setAttribute("name", name); // No need to escape: this is a java-valid token

        structureHandler.setLocalVariable(OPTION_IN_SELECT_ATTR_NAME, attributeName);
        structureHandler.setLocalVariable(OPTION_IN_SELECT_ATTR_VALUE, attributeValue);

        tag.getAttributes().removeAttribute(attributeName); // We need to remove it here before being cloned

        if (multiple && !isDisabled(tag)) {

            final Markup hiddenMethodElementMarkup = processingContext.getMarkupFactory().createMarkup();

            final String hiddenName = WebDataBinder.DEFAULT_FIELD_MARKER_PREFIX + name;
            final String type = "hidden";
            final String value =
                    RequestDataValueProcessorUtils.processFormFieldValue(processingContext, hiddenName, "1", type);

            final IStandaloneElementTag hiddenMethodElementTag =
                    processingContext.getMarkupFactory().createStandaloneElementTag("input", true);
            hiddenMethodElementTag.getAttributes().setAttribute("type", type);
            hiddenMethodElementTag.getAttributes().setAttribute("name", hiddenName);
            hiddenMethodElementTag.getAttributes().setAttribute("value", value);

            hiddenMethodElementMarkup.add(hiddenMethodElementTag);

            // We insert this hidden before because <select>'s are open element (with body), and if we insert it
            // after the element, we would be inserting the <input type="hidden"> inside the <select>, which would
            // incorrect...
            structureHandler.insertBefore(hiddenMethodElementMarkup);

        }

    }



    private static final boolean isDisabled(final IProcessableElementTag tag) {
        // Disabled = attribute "disabled" exists
        return tag.getAttributes().hasAttribute("disabled");
    }


}
