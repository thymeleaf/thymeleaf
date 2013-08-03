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
package org.thymeleaf.engine.context.dialect;

import java.util.Collections;

import org.thymeleaf.Arguments;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.element.AbstractElementProcessor;

public class AddContextVariableElementProcessor extends AbstractElementProcessor {

    
    public AddContextVariableElementProcessor() {
        super("add-context-variable");
    }

    @Override
    public int getPrecedence() {
        return 100;
    }


    @Override
    protected ProcessorResult processElement(Arguments arguments, Element element) {

        ((AbstractContext)arguments.getContext()).setVariable("newvar0", "VariablesNewVar0");
        arguments.getContext().getVariables().put("newvar1", "VariablesNewVar1");

        ((WebContext)arguments.getContext()).getRequestAttributes().put("newvar2", "RequestAttributesNewVar2");
        ((WebContext)arguments.getContext()).getHttpServletRequest().setAttribute("newvar3", "ServletRequestNewVar3");

        ((WebContext)arguments.getContext()).getApplicationAttributes().put("newvar4", "ApplicationAttributesNewVar4");
        ((WebContext)arguments.getContext()).getServletContext().setAttribute("newvar5", "ApplicationAttributesNewVar5");

        element.setAllNodeLocalVariables(Collections.singletonMap("one", (Object)"one"));
        // Remove host element
        element.getParent().extractChild(element);

        return ProcessorResult.OK;
    }

}
