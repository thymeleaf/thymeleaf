/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
import org.thymeleaf.Standards;
import org.thymeleaf.dom.Element;
import org.thymeleaf.util.PrefixUtils;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class StandardConditionalFixedValueAttrProcessor 
        extends AbstractStandardConditionalFixedValueAttrProcessor {

    public static final int ATTR_PRECEDENCE = 1000;
    
    public static final String[] ATTR_NAMES = Standards.HTML_CONDITIONAL_FIXED_VALUE_ATTR_NAMES;

    
    public static final StandardConditionalFixedValueAttrProcessor[] PROCESSORS;
    

    
    static {
        
        PROCESSORS = new StandardConditionalFixedValueAttrProcessor[ATTR_NAMES.length];
        for (int i = 0; i < PROCESSORS.length; i++) {
            PROCESSORS[i] = new StandardConditionalFixedValueAttrProcessor(ATTR_NAMES[i]);
        }
        
    }
    
    
    
    private StandardConditionalFixedValueAttrProcessor(final String attributeName) {
        super(attributeName);
    }



    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }



    @Override
    protected String getTargetAttributeName(final Arguments arguments, 
            final Element element, final String attributeName) {
        return PrefixUtils.getUnprefixed(attributeName);
    }



    @Override
    protected String getTargetAttributeFixedValue(final Arguments arguments, 
            final Element element, final String attributeName) {
        return PrefixUtils.getUnprefixed(attributeName);
    }

    
}
