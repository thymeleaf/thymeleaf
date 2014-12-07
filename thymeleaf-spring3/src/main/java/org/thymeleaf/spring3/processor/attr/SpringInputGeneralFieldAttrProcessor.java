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
package org.thymeleaf.spring3.processor.attr;

import java.util.Map;

import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.form.ValueFormatterWrapper;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.spring3.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.util.StringUtils;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class SpringInputGeneralFieldAttrProcessor 
        extends AbstractSpringFieldAttrProcessor {

    
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

    
    public static final SpringInputGeneralFieldAttrProcessor[] PROCESSORS;

    private boolean computedType;




    static {
        PROCESSORS = new SpringInputGeneralFieldAttrProcessor[ALL_TYPE_ATTR_VALUES.length];
        for (int i = 0; i < ALL_TYPE_ATTR_VALUES.length; i++) {
            PROCESSORS[i] = 
                    new SpringInputGeneralFieldAttrProcessor(ALL_TYPE_ATTR_VALUES[i]);
        }
    }
    
    


    private SpringInputGeneralFieldAttrProcessor(final String hostFilterAttributeValue) {
        super(ATTR_NAME, INPUT_TAG_NAME, INPUT_TYPE_ATTR_NAME, hostFilterAttributeValue);
        /*
         * This tries to verify whether we are executing the instance of this attribute processor that matches on
         * type == null, which means that we are executing it because we are lacking a more specialized type, and also
         * that we might also have generated a "type" attribute by other means (e.g. th:type), a circumstance that
         * we should check before executing the processor.
         */
        this.computedType = (hostFilterAttributeValue == null);
    }



    @Override
    protected ProcessorResult doProcess(final Arguments arguments, final Element element,
            final String attributeName, final String attributeValue, final BindStatus bindStatus,
            final Map<String, Object> localVariables) {

        // First of all, take care of the fact that we might have generated the value of the "type" attribute
        // with a different attribute (e.g. th:type), and that might have provoked this processor to be
        // executed instead of the right, more specialized one (e.g. "checkbox").
        if (this.computedType) {
            if (!StringUtils.isEmptyOrWhitespace(element.getAttributeValueFromNormalizedName("type"))) {
                // This means we now have a type which we didn't have before, and we must recompute
                element.setRecomputeProcessorsImmediately(true);
                return ProcessorResult.OK;
            }
        }

        // Now we know we are executing the right processor

        String name = bindStatus.getExpression();
        name = (name == null? "" : name);
        
        final String id = computeId(arguments, element, name, false);

        // Thanks to precedence, this should have already been computed
        final String type = element.getAttributeValueFromNormalizedName("type");

        // Apply the conversions (editor), depending on type (no conversion for "number" and "range"
        // Also, no escaping needed as attribute values are always escaped by default
        final String value =
                applyConversion(type)?
                        ValueFormatterWrapper.getDisplayString(bindStatus.getValue(), bindStatus.getEditor(), false) :
                        ValueFormatterWrapper.getDisplayString(bindStatus.getActualValue(), false);

        element.setAttribute("id", id);
        element.setAttribute("name", name);
        
        element.setAttribute(
                "value",
                RequestDataValueProcessorUtils.processFormFieldValue(
                        arguments.getConfiguration(), arguments, name, value, type));

        element.removeAttribute(attributeName);
        
        return ProcessorResult.setLocalVariables(localVariables);         
        
    }


    private static boolean applyConversion(final String type) {
        return !(type != null && ("number".equalsIgnoreCase(type) || "range".equalsIgnoreCase(type)));
    }
    

}
