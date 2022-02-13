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
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.spring5.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.spring5.util.SpringValueFormatter;
import org.thymeleaf.standard.util.StandardProcessorUtils;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public final class SpringInputGeneralFieldTagProcessor
        extends AbstractSpringFieldTagProcessor {


    // HTML4 input types
    public static final String TEXT_INPUT_TYPE_ATTR_VALUE = "text";
    public static final String HIDDEN_INPUT_TYPE_ATTR_VALUE = "hidden";
    // HTML5-specific input types
    public static final String DATETIME_INPUT_TYPE_ATTR_VALUE = "datetime";
    public static final String DATETIMELOCAL_INPUT_TYPE_ATTR_VALUE = "datetime-local";
    public static final String DATE_INPUT_TYPE_ATTR_VALUE = "date";
    public static final String MONTH_INPUT_TYPE_ATTR_VALUE = "month";
    public static final String TIME_INPUT_TYPE_ATTR_VALUE = "time";
    public static final String WEEK_INPUT_TYPE_ATTR_VALUE = "week";
    public static final String NUMBER_INPUT_TYPE_ATTR_VALUE = "number";
    public static final String RANGE_INPUT_TYPE_ATTR_VALUE = "range";
    public static final String EMAIL_INPUT_TYPE_ATTR_VALUE = "email";
    public static final String URL_INPUT_TYPE_ATTR_VALUE = "url";
    public static final String SEARCH_INPUT_TYPE_ATTR_VALUE = "search";
    public static final String TEL_INPUT_TYPE_ATTR_VALUE = "tel";
    public static final String COLOR_INPUT_TYPE_ATTR_VALUE = "color";


    private static final String[] ALL_TYPE_ATTR_VALUES =
            new String[] {
                    null,
                    TEXT_INPUT_TYPE_ATTR_VALUE,
                    HIDDEN_INPUT_TYPE_ATTR_VALUE,
                    DATETIME_INPUT_TYPE_ATTR_VALUE,
                    DATETIMELOCAL_INPUT_TYPE_ATTR_VALUE,
                    DATE_INPUT_TYPE_ATTR_VALUE,
                    MONTH_INPUT_TYPE_ATTR_VALUE,
                    TIME_INPUT_TYPE_ATTR_VALUE,
                    WEEK_INPUT_TYPE_ATTR_VALUE,
                    NUMBER_INPUT_TYPE_ATTR_VALUE,
                    RANGE_INPUT_TYPE_ATTR_VALUE,
                    EMAIL_INPUT_TYPE_ATTR_VALUE,
                    URL_INPUT_TYPE_ATTR_VALUE,
                    SEARCH_INPUT_TYPE_ATTR_VALUE,
                    TEL_INPUT_TYPE_ATTR_VALUE,
                    COLOR_INPUT_TYPE_ATTR_VALUE
            };




    public SpringInputGeneralFieldTagProcessor(final String dialectPrefix) {
        super(dialectPrefix, INPUT_TAG_NAME, TYPE_ATTR_NAME, ALL_TYPE_ATTR_VALUES, true);
    }




    @Override
    protected void doProcess(final ITemplateContext context,
                             final IProcessableElementTag tag,
                             final AttributeName attributeName, final String attributeValue,
                             final IThymeleafBindStatus bindStatus, final IElementTagStructureHandler structureHandler) {

        String name = bindStatus.getExpression();
        name = (name == null? "" : name);

        final String id = computeId(context, tag, name, false);

        // Thanks to precedence, this should have already been computed
        final String type = tag.getAttributeValue(this.typeAttributeDefinition.getAttributeName());

        // Apply the conversions (editor), depending on type (no conversion for "number" and "range"
        // Also, no escaping needed as attribute values are always escaped by default
        final String value =
                applyConversion(type)?
                        SpringValueFormatter.getDisplayString(bindStatus.getValue(), bindStatus.getEditor(), true) :
                        SpringValueFormatter.getDisplayString(bindStatus.getActualValue(), true);

        StandardProcessorUtils.setAttribute(structureHandler, this.idAttributeDefinition, ID_ATTR_NAME, id); // No need to escape: this comes from an existing 'id' or from a token
        StandardProcessorUtils.setAttribute(structureHandler, this.nameAttributeDefinition, NAME_ATTR_NAME, name); // No need to escape: this is a java-valid token

        StandardProcessorUtils.setAttribute(
                structureHandler, this.valueAttributeDefinition, VALUE_ATTR_NAME, RequestDataValueProcessorUtils.processFormFieldValue(context, name, value, type));

    }



    private static boolean applyConversion(final String type) {
        return !(type != null && ("number".equalsIgnoreCase(type) || "range".equalsIgnoreCase(type)));
    }


}
