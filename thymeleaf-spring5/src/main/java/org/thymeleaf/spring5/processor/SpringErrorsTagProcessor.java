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
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;
import org.thymeleaf.spring5.util.FieldUtils;
import org.thymeleaf.spring5.util.SpringValueFormatter;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.html.HtmlEscape;

/**
 * Works in a similar way to <b>#fields.errors()</b>, but lists all errors for
 * the given field name, separated by a &lt;br/&gt;
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.3
 */
public final class SpringErrorsTagProcessor extends AbstractAttributeTagProcessor {

    private static final String ERROR_DELIMITER = "<br />";
    
    public static final int ATTR_PRECEDENCE = 1700;
    public static final String ATTR_NAME = "errors";

    

    
    
    public SpringErrorsTagProcessor(final String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, null, false, ATTR_NAME, true, ATTR_PRECEDENCE, true);
    }




    @Override
    protected void doProcess(
            final ITemplateContext context, final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IElementTagStructureHandler structureHandler) {

        final IThymeleafBindStatus bindStatus = FieldUtils.getBindStatus(context, attributeValue);

        if (bindStatus.isError()) {

            final StringBuilder strBuilder = new StringBuilder();
            final String[] errorMsgs = bindStatus.getErrorMessages();

            for (int i = 0; i < errorMsgs.length; i++) {
                if (i > 0) {
                    strBuilder.append(ERROR_DELIMITER);
                }
                final String displayString = SpringValueFormatter.getDisplayString(errorMsgs[i], false);
                strBuilder.append(HtmlEscape.escapeHtml4Xml(displayString));
            }

            structureHandler.setBody(strBuilder.toString(), false);

            // Just in case we also have a th:errorclass in this tag
            structureHandler.setLocalVariable(SpringContextVariableNames.THYMELEAF_FIELD_BIND_STATUS, bindStatus);

        } else {

            structureHandler.removeElement();

        }

    }


}
