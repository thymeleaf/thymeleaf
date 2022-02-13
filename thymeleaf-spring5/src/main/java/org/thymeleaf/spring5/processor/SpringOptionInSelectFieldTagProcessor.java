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
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public final class SpringOptionInSelectFieldTagProcessor extends AbstractElementTagProcessor {


    // This is 1005 in order to make sure it is executed just before "value" (Spring's version) and especially "th:field"
    public static final int ATTR_PRECEDENCE = 1005;
    public static final String OPTION_TAG_NAME = "option";



    public SpringOptionInSelectFieldTagProcessor(final String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, OPTION_TAG_NAME, false, null, false, ATTR_PRECEDENCE);
    }




    @Override
    protected void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final IElementTagStructureHandler structureHandler) {

        final AttributeName selectAttrNameToAdd =
                (AttributeName) context.getVariable(SpringSelectFieldTagProcessor.OPTION_IN_SELECT_ATTR_NAME);
        if (selectAttrNameToAdd == null) {
            // Nothing to do
            return;
        }

        // It seems this <option> is inside a <select th:field="...">, and the processor for that "th:field" has left
        // as a local variable the name and value of the attribute to be added

        final String selectAttrValueToAdd =
                (String) context.getVariable(SpringSelectFieldTagProcessor.OPTION_IN_SELECT_ATTR_VALUE);

        if (tag.hasAttribute(selectAttrNameToAdd)) {
            if (!selectAttrValueToAdd.equals(tag.getAttributeValue(selectAttrNameToAdd))) {
                throw new TemplateProcessingException(
                        "If specified (which is not required), attribute \"" + selectAttrNameToAdd + "\" in " +
                        "\"option\" tag must have exactly the same value as in its containing \"select\" tag");
            }
        }

        // This attribute value does not need to be escaped, because we are just "transporting" the th:field in the
        // container <select> to its <option>'s, without any modifications. It will be executed (and its results
        // escaped) later...
        structureHandler.setAttribute(selectAttrNameToAdd.getCompleteAttributeNames()[0], selectAttrValueToAdd);

    }



}
