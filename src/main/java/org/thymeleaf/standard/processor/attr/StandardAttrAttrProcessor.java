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
 * Sets the value of any attribute/s.  Takes a list of comma-separated
 * attribute/expression pairs, eg:
 * <p>
 * &lt;img src="../../images/gtvglogo.png" th:attr="src=@{/images/gtvglogo.png},title=#{logo},alt=#{logo}" /&gt;
 * <p>
 * The above will set the <tt>src</tt>, <tt>title</tt>, and <tt>alt</tt>
 * attributes to the results of each of their respective expressions.
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 1.0
 */
public final class StandardAttrAttrProcessor 
        extends AbstractStandardAttributeModifierAttrProcessor {

    public static final int ATTR_PRECEDENCE = 700;
    public static final String ATTR_NAME = "attr";

    
    
    public StandardAttrAttrProcessor() {
        super(ATTR_NAME);
    }
    


    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }
    
    
    @Override
    protected ModificationType getModificationType(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return ModificationType.SUBSTITUTION;
    }



    @Override
    protected boolean removeAttributeIfEmpty(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return true;
    }


    
}
