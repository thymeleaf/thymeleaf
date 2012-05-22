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

import java.util.Set;

import org.thymeleaf.processor.applicability.AttrApplicability;
import org.thymeleaf.processor.attr.AbstractRemovalAttrProcessor;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class StandardRemoveAttrProcessor
        extends AbstractRemovalAttrProcessor {

    public static final Integer ATTR_PRECEDENCE = Integer.valueOf(1600);
    public static final String ATTR_NAME = "remove";

    public static final String VALUE_ALL = "all";
    public static final String VALUE_TAG = "tag";
    public static final String VALUE_BODY = "body";
    
    

    
    public StandardRemoveAttrProcessor() {
        super();
    }


    
    public Set<AttrApplicability> getAttributeApplicabilities() {
        return AttrApplicability.createSetForAttrName(ATTR_NAME);
    }

    public Integer getPrecedence() {
        return ATTR_PRECEDENCE;
    }
    



    @Override
    protected String getRemoveAllAttrValue() {
        return VALUE_ALL;
    }



    @Override
    protected String getRemoveBodyAttrValue() {
        return VALUE_BODY;
    }



    @Override
    protected String getRemoveTagAttrValue() {
        return VALUE_TAG;
    }
    


    
}
