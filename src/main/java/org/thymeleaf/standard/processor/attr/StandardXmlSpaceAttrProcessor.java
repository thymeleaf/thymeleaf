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
package org.thymeleaf.standard.processor.attr;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Tag;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class StandardXmlSpaceAttrProcessor 
        extends AbstractStandardSingleAttributeModifierAttrProcessor {

    public static final int ATTR_PRECEDENCE = 1000;
    public static final String ATTR_NAME = "xmlspace";
    public static final String TARGET_ATTR_NAME = "xml:space";
    
    
    
    public StandardXmlSpaceAttrProcessor() {
        super(ATTR_NAME);
    }

    

    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }



    @Override
    protected String getTargetAttributeName(
            final Arguments arguments, final Tag tag, final String attributeName) {
        return TARGET_ATTR_NAME;
    }

    
    @Override
    protected ModificationType getModificationType(
            final Arguments arguments, final Tag tag, final String attributeName, final String newAttributeName) {
        return ModificationType.SUBSTITUTION;
    }



    @Override
    protected boolean removeAttributeIfEmpty(
            final Arguments arguments, final Tag tag, final String attributeName, final String newAttributeName) {
        return true;
    }



    
}
