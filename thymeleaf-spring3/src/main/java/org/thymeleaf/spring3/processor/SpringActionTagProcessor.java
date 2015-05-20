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

import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.spring3.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.standard.processor.attr.AbstractStandardSingleAttributeModifierAttrProcessor;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class SpringActionTagProcessor
        extends AbstractStandardSingleAttributeModifierAttrProcessor {


    public static final int ATTR_PRECEDENCE = 1000;
    public static final String ATTR_NAME = "action";



    public SpringActionTagProcessor() {
        super(ATTR_NAME);
    }

    
    
    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }



    @Override
    protected String getTargetAttributeName(
            final Arguments arguments, final Element element, final String attributeName) {
        return ATTR_NAME;
    }



    @Override
    protected String getTargetAttributeValue(
            final Arguments arguments, final Element element, final String attributeName) {
        final String attributeValue = super.getTargetAttributeValue(arguments, element, attributeName);
        final String httpMethod = element.getAttributeValueFromNormalizedName("method");
        return RequestDataValueProcessorUtils.processAction(
                arguments.getConfiguration(), arguments, attributeValue, httpMethod);
    }


    
    @Override
    protected ModificationType getModificationType(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return ModificationType.SUBSTITUTION;
    }



    @Override
    protected boolean removeAttributeIfEmpty(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return false;
    }





    @Override
    protected void doAdditionalProcess(
            final Arguments arguments, final Element element, final String attributeName) {

        if ("form".equals(element.getNormalizedName())) {

            final Map<String,String> extraHiddenFields =
                    RequestDataValueProcessorUtils.getExtraHiddenFields(arguments.getConfiguration(), arguments);

            if (extraHiddenFields != null && extraHiddenFields.size() > 0) {

                for (final Map.Entry<String,String> extraHiddenField : extraHiddenFields.entrySet()) {

                    final Element extraHiddenElement = new Element("input");
                    extraHiddenElement.setAttribute("type", "hidden");
                    extraHiddenElement.setAttribute("name", extraHiddenField.getKey());
                    extraHiddenElement.setAttribute("value", extraHiddenField.getValue()); // no need to re-apply the processor here

                    element.insertChild(element.numChildren(), extraHiddenElement);

                }

            }

        }

    }



}
