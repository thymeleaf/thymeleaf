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
 * Selects a child element for rendering if the child's <b>th:case</b>
 * expression is an exact match of this processor's expression, eg:
 * <p>
 * &lt;div th:switch="${user.role}"&gt;<br/>
 * &nbsp; &lt;p th:case="'admin'"&gt;User is an administrator&lt;/p&gt;<br/>
 * &nbsp; &lt;p th:case="#{roles.manager}"&gt;User is a manager&lt;/p&gt;<br/>
 * &lt;/div&gt;
 * <p>
 * Note that as soon as one th:case attribute is evaluated as true, every other
 * <tt>th:case</tt> attribute in the same switch context is evaluated as false.
 * <p>
 * The default option is specified as th:case="*"
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 2.0.0
 */
public final class StandardSwitchAttrProcessor 
        extends AbstractStandardSwitchStructureAttrProcessor {

    public static final int ATTR_PRECEDENCE = 250;
    public static final String ATTR_NAME = "switch";
    
    
    
    public StandardSwitchAttrProcessor() {
        super(ATTR_NAME);
    }

    
    
    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }

    
    

    
}
