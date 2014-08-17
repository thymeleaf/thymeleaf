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
package org.thymeleaf.standard.processor.attr;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.attr.AbstractMarkupRemovalAttrProcessor;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

/**
 * Removes a piece of this template from the final result.
 * <p>
 * If this attribute's value is <b>all</b>, both the containing tag and it's
 * children will be removed.  If the value is <b>body</b>, only the tag's
 * children will be removed.  If the value is <b>tag</b>, the containing tag
 * will be removed, but not it's children.  If the value is <b>all-but-first</b>,
 * then all but the first child of the containing tag will be removed.
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 1.0
 */
public final class StandardRemoveAttrProcessor
        extends AbstractMarkupRemovalAttrProcessor {

    public static final int ATTR_PRECEDENCE = 1600;
    public static final String ATTR_NAME = "remove";


    public static final String VALUE_ALL = "all";
    public static final String VALUE_ALL_BUT_FIRST = "all-but-first";
    public static final String VALUE_TAG = "tag";
    public static final String VALUE_BODY = "body";

    /**
     * @since 2.1.0
     */
    public static final String VALUE_NONE = "none";

    

    
    public StandardRemoveAttrProcessor() {
        super(ATTR_NAME);
    }



    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }


    @Override
    protected RemovalType getRemovalType(final Arguments arguments, final Element element, final String attributeName) {


        final String attributeValue = element.getAttributeValue(attributeName);

        final Configuration configuration = arguments.getConfiguration();
        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);

        final IStandardExpression expression = expressionParser.parseExpression(configuration, arguments, attributeValue);

        final Object result = expression.execute(configuration, arguments);

        if (result == null) {
            return RemovalType.NONE;
        }

        final String resultStr = result.toString();

        if (VALUE_ALL.equalsIgnoreCase(resultStr)) {
            return RemovalType.ALL;
        }
        if (VALUE_NONE.equalsIgnoreCase(resultStr)) {
            return RemovalType.NONE;
        }
        if (VALUE_TAG.equalsIgnoreCase(resultStr)) {
            return RemovalType.ELEMENT;
        }
        if (VALUE_ALL_BUT_FIRST.equalsIgnoreCase(resultStr)) {
            return RemovalType.ALLBUTFIRST;
        }
        if (VALUE_BODY.equalsIgnoreCase(resultStr)) {
            return RemovalType.BODY;
        }

        throw new TemplateProcessingException(
                "Invalid value specified for \"" + attributeName + "\": only 'all', 'tag', 'body', 'none' " +
                "and 'all-but-first' are allowed, but \"" + attributeValue + "\" was specified.");

    }

}
