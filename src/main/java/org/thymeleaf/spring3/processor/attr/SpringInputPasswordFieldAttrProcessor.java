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
package org.thymeleaf.spring3.processor.attr;

import java.util.Map;

import org.springframework.web.servlet.support.BindStatus;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Tag;
import org.thymeleaf.processor.ProcessorResult;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class SpringInputPasswordFieldAttrProcessor 
        extends AbstractSpringFieldAttrProcessor {

    
    
    public static final String PASSWORD_INPUT_TYPE_ATTR_VALUE = "password";
    

    

    
    public SpringInputPasswordFieldAttrProcessor() {
        super(ATTR_NAME,
              INPUT_TAG_NAME,
              INPUT_TYPE_ATTR_NAME,
              PASSWORD_INPUT_TYPE_ATTR_VALUE);
    }






    @Override
    protected ProcessorResult doProcess(final Arguments arguments, final Tag tag,
            final String attributeName, final String attributeValue, final BindStatus bindStatus,
            final Map<String, Object> localVariables) {
        
        String name = bindStatus.getExpression();
        name = (name == null? "" : name);
        
        final String id = computeId(arguments, tag, name, false);
        
        tag.setAttribute("id", id);
        tag.setAttribute("name", name);
        
        tag.setAttribute("value", "");
        tag.removeAttribute(attributeName);
        
        return ProcessorResult.setLocalVariables(localVariables);         
        
    }

    

}
