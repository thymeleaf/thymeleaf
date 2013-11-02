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
package org.thymeleaf.standard.processor.attr;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;

/**
 * Appends the result of an expression to the <b>class</b> attribute, eg:
 * <p>
 * &lt;div class="item" th:classappend="${myObject.type == 'special'} ? 'special' : 'normal'" /&gt;
 * <p>
 * Depending on the result of the expression, the class attribute will become
 * either "item special" or "item normal".
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 1.0
 */
public final class StandardClassappendAttrProcessor 
        extends AbstractStandardSingleAttributeModifierAttrProcessor {
    
    public static final int ATTR_PRECEDENCE = 1100;
    public static final String ATTR_NAME = "classappend";
    public static final String TARGET_ATTR_NAME = "class";
    
    
    
    public StandardClassappendAttrProcessor() {
        super(ATTR_NAME);
    }



    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }



    @Override
    protected String getTargetAttributeName(
            final Arguments arguments, final Element element, final String attributeName) {
        return TARGET_ATTR_NAME;
    }


    @Override
    protected ModificationType getModificationType(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return ModificationType.APPEND_WITH_SPACE;
    }



    @Override
    protected boolean removeAttributeIfEmpty(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return true;
    }


}
