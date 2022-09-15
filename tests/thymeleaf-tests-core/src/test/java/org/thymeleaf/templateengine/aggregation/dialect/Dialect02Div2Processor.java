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
package org.thymeleaf.templateengine.aggregation.dialect;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

public class Dialect02Div2Processor extends AbstractElementTagProcessor {


    public Dialect02Div2Processor(final String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, "div", true, null, false, 110);
    }



    @Override
    protected void doProcess(
            final ITemplateContext processingContext,
            final IProcessableElementTag tag,
            final IElementTagStructureHandler structureHandler) {

        final IModelFactory modelFactory = processingContext.getModelFactory();

        final IModel markup = modelFactory.createModel();
        markup.add(modelFactory.createText("[From Dialect 02-2]"));
        structureHandler.insertBefore(markup);

    }

}
