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
package org.thymeleaf.templateengine.processors.dialects.replacewithprocessable;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.processinginstruction.AbstractProcessingInstructionProcessor;
import org.thymeleaf.processor.processinginstruction.IProcessingInstructionStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

public class ReplaceWithProcessableProcessingInstructionProcessor extends AbstractProcessingInstructionProcessor {

    private static final int PRECEDENCE = 1000;


    public ReplaceWithProcessableProcessingInstructionProcessor() {
        super(TemplateMode.HTML, PRECEDENCE);
    }


    @Override
    protected void doProcess(
            final ITemplateContext context, final IProcessingInstruction processingInstruction, final IProcessingInstructionStructureHandler structureHandler) {

        final IModelFactory modelFactory = context.getModelFactory();

        final IStandaloneElementTag tag =
                modelFactory.createStandaloneElementTag("replaced", "th:text", "one", false, true);
        final IModel model = modelFactory.createModel(tag);

        structureHandler.replaceWith(model, true);

    }

}
