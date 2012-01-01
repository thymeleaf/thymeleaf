/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
import org.thymeleaf.dom.Tag;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.applicability.AndApplicabilityFilter;
import org.thymeleaf.processor.applicability.AttrValueApplicabilityFilter;
import org.thymeleaf.processor.applicability.IApplicabilityFilter;
import org.thymeleaf.processor.applicability.OrApplicabilityFilter;
import org.thymeleaf.processor.applicability.TagNameApplicabilityFilter;



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

    
    
    public SpringInputGeneralFieldAttrProcessor() {
        super();
    }

    


    @Override
    protected IApplicabilityFilter getApplicabilityFilter() {
        
        final IApplicabilityFilter tagApplicabilityFilter = new TagNameApplicabilityFilter(INPUT_TAG_NAME);
        
        final IApplicabilityFilter typeTextApplicabilityFilter = new AttrValueApplicabilityFilter(INPUT_TYPE_ATTR_NAME, TEXT_INPUT_TYPE_ATTR_VALUE); 
        final IApplicabilityFilter typeHiddenApplicabilityFilter = new AttrValueApplicabilityFilter(INPUT_TYPE_ATTR_NAME, HIDDEN_INPUT_TYPE_ATTR_VALUE);
        final IApplicabilityFilter typeDatetimeApplicabilityFilter = new AttrValueApplicabilityFilter(INPUT_TYPE_ATTR_NAME, DATETIME_INPUT_TYPE_ATTR_VALUE);
        final IApplicabilityFilter typeDatetimeLocalApplicabilityFilter = new AttrValueApplicabilityFilter(INPUT_TYPE_ATTR_NAME, DATETIMELOCAL_INPUT_TYPE_ATTR_VALUE);
        final IApplicabilityFilter typeDateApplicabilityFilter = new AttrValueApplicabilityFilter(INPUT_TYPE_ATTR_NAME, DATE_INPUT_TYPE_ATTR_VALUE);
        final IApplicabilityFilter typeMonthApplicabilityFilter = new AttrValueApplicabilityFilter(INPUT_TYPE_ATTR_NAME, MONTH_INPUT_TYPE_ATTR_VALUE);
        final IApplicabilityFilter typeTimeApplicabilityFilter = new AttrValueApplicabilityFilter(INPUT_TYPE_ATTR_NAME, TIME_INPUT_TYPE_ATTR_VALUE);
        final IApplicabilityFilter typeWeekApplicabilityFilter = new AttrValueApplicabilityFilter(INPUT_TYPE_ATTR_NAME, WEEK_INPUT_TYPE_ATTR_VALUE);
        final IApplicabilityFilter typeNumberApplicabilityFilter = new AttrValueApplicabilityFilter(INPUT_TYPE_ATTR_NAME, NUMBER_INPUT_TYPE_ATTR_VALUE);
        final IApplicabilityFilter typeRangeApplicabilityFilter = new AttrValueApplicabilityFilter(INPUT_TYPE_ATTR_NAME, RANGE_INPUT_TYPE_ATTR_VALUE);
        final IApplicabilityFilter typeEmailApplicabilityFilter = new AttrValueApplicabilityFilter(INPUT_TYPE_ATTR_NAME, EMAIL_INPUT_TYPE_ATTR_VALUE);
        final IApplicabilityFilter typeUrlApplicabilityFilter = new AttrValueApplicabilityFilter(INPUT_TYPE_ATTR_NAME, URL_INPUT_TYPE_ATTR_VALUE);
        final IApplicabilityFilter typeSearchApplicabilityFilter = new AttrValueApplicabilityFilter(INPUT_TYPE_ATTR_NAME, SEARCH_INPUT_TYPE_ATTR_VALUE);
        final IApplicabilityFilter typeTelApplicabilityFilter = new AttrValueApplicabilityFilter(INPUT_TYPE_ATTR_NAME, TEL_INPUT_TYPE_ATTR_VALUE);
        final IApplicabilityFilter typeColorApplicabilityFilter = new AttrValueApplicabilityFilter(INPUT_TYPE_ATTR_NAME, COLOR_INPUT_TYPE_ATTR_VALUE);
        
        final IApplicabilityFilter typesApplicabilityFilter = 
            new OrApplicabilityFilter(
                    typeTextApplicabilityFilter, 
                    typeHiddenApplicabilityFilter,
                    typeDatetimeApplicabilityFilter,
                    typeDatetimeLocalApplicabilityFilter,
                    typeDateApplicabilityFilter,
                    typeMonthApplicabilityFilter,
                    typeTimeApplicabilityFilter,
                    typeWeekApplicabilityFilter,
                    typeNumberApplicabilityFilter,
                    typeRangeApplicabilityFilter,
                    typeEmailApplicabilityFilter,
                    typeUrlApplicabilityFilter,
                    typeSearchApplicabilityFilter,
                    typeTelApplicabilityFilter,
                    typeColorApplicabilityFilter);
        
        return new AndApplicabilityFilter(tagApplicabilityFilter, typesApplicabilityFilter);
    }




    @Override
    protected ProcessorResult doProcess(final Arguments arguments, final Tag tag,
            final String attributeName, final String attributeValue, final BindStatus bindStatus,
            final Map<String, Object> localVariables) {
        
        String name = bindStatus.getExpression();
        name = (name == null? "" : name);
        
        final String id = computeId(arguments, tag, name, false);
        
        // No escaping needed as attribute values are always escaped by default
        final String value = ValueFormatterWrapper.getDisplayString(bindStatus.getValue(), bindStatus.getEditor(), false);
        
        tag.setAttribute("id", id);
        tag.setAttribute("name", name);
        
        tag.setAttribute("value", value);
        tag.removeAttribute(attributeName);
        
        return ProcessorResult.setLocalVariables(localVariables);         
        
    }

    

}
