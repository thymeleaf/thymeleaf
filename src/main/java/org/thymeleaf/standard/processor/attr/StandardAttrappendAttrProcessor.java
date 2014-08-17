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
import org.thymeleaf.dom.Element;

/**
 * Appends the result of an expression to any attribute/s, eg:
 * <p>
 * &lt;input type="button" value="Do it!" class="btn" th:attrappend="class=${' ' + cssStyle}" /&gt;
 * <p>
 * If you process this template with the <tt>cssStyle</tt> variable set to
 * "warning", you will get:
 * <p>
 * &lt;input type="button" value="Do it!" class="btn warning" /&gt;
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 1.0
 */
public final class StandardAttrappendAttrProcessor 
        extends AbstractStandardAttributeModifierAttrProcessor {

    public static final int ATTR_PRECEDENCE = 900;
    public static final String ATTR_NAME = "attrappend";

    



    public StandardAttrappendAttrProcessor() {
        super(ATTR_NAME);
    }

    
    

    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }
    
    
    
    @Override
    protected ModificationType getModificationType(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return ModificationType.APPEND;
    }



    @Override
    protected boolean removeAttributeIfEmpty(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return true;
    }




    
}
