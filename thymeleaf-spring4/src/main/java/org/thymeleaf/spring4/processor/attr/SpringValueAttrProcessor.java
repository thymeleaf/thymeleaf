/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring4.processor.attr;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.spring4.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.standard.processor.attr.AbstractStandardSingleAttributeModifierAttrProcessor;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.1.0
 *
 */
public final class SpringValueAttrProcessor
        extends AbstractStandardSingleAttributeModifierAttrProcessor {


    // This is 1010 in order to make sure it is executed after "name" and "type"
    public static final int ATTR_PRECEDENCE = 1010;
    public static final String ATTR_NAME = "value";



    public SpringValueAttrProcessor() {
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
        if (element.hasNormalizedAttribute(Attribute.getPrefixFromAttributeName(attributeName), AbstractSpringFieldAttrProcessor.ATTR_NAME)) {
            // There still is a th:field to be executed, so better not process the value ourselves (let's let th:field do it)
            return attributeValue;
        }

        final String name = element.getAttributeValueFromNormalizedName("name");
        final String type = element.getAttributeValueFromNormalizedName("type");
        return RequestDataValueProcessorUtils.processFormFieldValue(
                arguments.getConfiguration(), arguments, name, attributeValue, type);

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

    
}
