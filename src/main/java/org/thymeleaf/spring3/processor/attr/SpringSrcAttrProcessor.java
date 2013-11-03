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
package org.thymeleaf.spring3.processor.attr;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.spring3.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.standard.processor.attr.AbstractStandardSingleAttributeModifierAttrProcessor;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.1.0
 *
 */
public final class SpringSrcAttrProcessor
        extends AbstractStandardSingleAttributeModifierAttrProcessor {


    public static final int ATTR_PRECEDENCE = 1000;
    public static final String ATTR_NAME = "src";



    public SpringSrcAttrProcessor() {
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
        return RequestDataValueProcessorUtils.processUrl(arguments.getConfiguration(), arguments, attributeValue);
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
