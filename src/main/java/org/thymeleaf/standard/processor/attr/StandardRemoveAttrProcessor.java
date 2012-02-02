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

    public static final int ATTR_PRECEDENCE = 1600;
    public static final String ATTR_NAME = "remove";

    public static final String VALUE_ALL = "all";
    public static final String VALUE_ALL_BUT_FIRST = "all-but-first";
    public static final String VALUE_TAG = "tag";
    public static final String VALUE_BODY = "body";
    
    

    
    public StandardRemoveAttrProcessor() {
        super(ATTR_NAME);
    }



    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }
    



    @Override
    protected String getRemoveAllAttrValue() {
        return VALUE_ALL;
    }


    
    @Override
    protected String getRemoveAllButFirstAttrValue() {
        return VALUE_ALL_BUT_FIRST;
    }



    @Override
    protected String getRemoveBodyAttrValue() {
        return VALUE_BODY;
    }



    @Override
    protected String getRemoveElementAttrValue() {
        return VALUE_TAG;
    }
    


    
}
