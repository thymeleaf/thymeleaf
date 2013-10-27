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

/**
 * Create one or more local variables scoped to this tag, that can then be
 * referred to by other expressions.  Takes a comma-separated list of
 * localname/expression pairs, eg:
 * <p>
 * &lt;div th:with="firstPer=${persons[0]},secondPer=${persons[1]}"&gt;<br/>
 * &nbsp; &lt;p th:text="${firstPer.name}"&gt;First person name&lt;p&gt;<br/>
 * &nbsp; &lt;p th:text="${secondPer.name}"&gt;Second person name&lt;p&gt;<br/>
 * &lt;/div&gt;
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 1.0
 */
public final class StandardWithAttrProcessor 
        extends AbstractStandardLocalVariableDefinitionAttrProcessor {

    public static final int ATTR_PRECEDENCE = 600;
    public static final String ATTR_NAME = "with";
    
    
    
    public StandardWithAttrProcessor() {
        super(ATTR_NAME);
    }
    
    

    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }



    
}
