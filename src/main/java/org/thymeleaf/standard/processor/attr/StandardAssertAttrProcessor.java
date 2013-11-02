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

/**
 * A comma-separated list of expressions which should be evaluated and produce
 * <tt>true</tt> for every evaluation, raising an exception if not. 
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.1.0
 *
 */
public final class StandardAssertAttrProcessor
        extends AbstractStandardAssertionAttrProcessor {

    public static final int ATTR_PRECEDENCE = 1550;
    public static final String ATTR_NAME = "assert";




    public StandardAssertAttrProcessor() {
        super(ATTR_NAME);
    }



    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }
    

    
}
