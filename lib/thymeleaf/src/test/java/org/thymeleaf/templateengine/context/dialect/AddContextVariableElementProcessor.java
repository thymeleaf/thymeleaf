/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.templateengine.context.dialect;

import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.web.IWebExchange;

public class AddContextVariableElementProcessor extends AbstractElementTagProcessor {

    
    public AddContextVariableElementProcessor(final String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, "add-context-variable", true, null, false, 100);
    }



    @Override
    protected void doProcess(
            final ITemplateContext processingContext,
            final IProcessableElementTag tag,
            final IElementTagStructureHandler structureHandler) {

        final IWebContext webContext = (IWebContext) processingContext;
        final IEngineContext engineContext = (IEngineContext) webContext;

        final IWebExchange webExchange = webContext.getExchange();

        engineContext.setVariable("newvar0", "LocalVariablesNewVar0");
        engineContext.setVariable("newvar1", "LocalVariablesNewVar1");

        webExchange.setAttributeValue("newvar2", "RequestAttributesNewVar2");
        webExchange.setAttributeValue("newvar3", "RequestAttributesNewVar3");

        webExchange.getApplication().setAttributeValue("newvar4", "ApplicationAttributesNewVar4");
        webExchange.getApplication().setAttributeValue("newvar5", "ApplicationAttributesNewVar5");

        webExchange.getSession().setAttributeValue("newvar6", "SessionAttributesNewVar6");
        webExchange.getSession().setAttributeValue("newvar7", "SessionAttributesNewVar7");

        structureHandler.setLocalVariable("one", "one");
        structureHandler.removeElement();

    }


}
