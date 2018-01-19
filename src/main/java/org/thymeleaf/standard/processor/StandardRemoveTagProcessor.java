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
package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardRemoveTagProcessor extends AbstractStandardExpressionAttributeTagProcessor {

    public static final int PRECEDENCE = 1600;
    public static final String ATTR_NAME = "remove";

    public static final String VALUE_ALL = "all";
    public static final String VALUE_ALL_BUT_FIRST = "all-but-first";
    public static final String VALUE_TAG = "tag";
    public static final String VALUE_TAGS = "tags"; // 'tags' is also allowed underneath because that's what it does: remove both open and close tags.
    public static final String VALUE_BODY = "body";
    public static final String VALUE_NONE = "none";


    public StandardRemoveTagProcessor(final TemplateMode templateMode, final String dialectPrefix) {
        super(templateMode, dialectPrefix, ATTR_NAME, PRECEDENCE, true, false);
    }



    @Override
    protected void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final Object expressionResult,
            final IElementTagStructureHandler structureHandler) {

        if (expressionResult != null) {

            final String resultStr = expressionResult.toString();

            if (VALUE_ALL.equalsIgnoreCase(resultStr)) {
                structureHandler.removeElement();
            } else if (VALUE_TAG.equalsIgnoreCase(resultStr) || VALUE_TAGS.equalsIgnoreCase(resultStr)) {
                structureHandler.removeTags();
            } else if (VALUE_ALL_BUT_FIRST.equalsIgnoreCase(resultStr)) {
                structureHandler.removeAllButFirstChild();
            } else  if (VALUE_BODY.equalsIgnoreCase(resultStr)) {
                structureHandler.removeBody();
            } else if (!VALUE_NONE.equalsIgnoreCase(resultStr)) {
                throw new TemplateProcessingException(
                        "Invalid value specified for \"" + attributeName + "\": only 'all', 'tag', 'body', 'none' " +
                        "and 'all-but-first' are allowed, but \"" + attributeValue + "\" was specified.");
            }

        }

    }


}
