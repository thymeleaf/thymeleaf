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
package org.thymeleaf.engine;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.engine.EventModelController.SkipBody;
import org.thymeleaf.exceptions.TemplateProcessingException;

import static org.thymeleaf.engine.ProcessorTemplateHandler.GATHERED_MODEL_CONTEXT_VARIABLE_NAME;


/*
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 *
 */
final class DelayedGatheredModel extends AbstractGatheredModel {


    private final IEngineContext context;

    private boolean processed;




    DelayedGatheredModel(
            final IEngineConfiguration configuration, final IEngineContext context,
            final ElementProcessorIterator suspendedProcessorIterator,
            final Model suspendedModel, final boolean suspendedModelProcessable,
            final boolean suspendedModelProcessBeforeDelegate,
            final boolean suspendedDiscardEvent, final SkipBody suspendedSkipBody, final boolean suspendedSkipCloseTag) {
        super(configuration, context, suspendedProcessorIterator, suspendedModel, suspendedModelProcessable, suspendedModelProcessBeforeDelegate, suspendedDiscardEvent, suspendedSkipBody, suspendedSkipCloseTag);
        this.context = context;
        this.processed = false;
    }


    public boolean isProcessed() {
        return this.processed;
    }





    public void process(final ITemplateHandler handler) {

        /*
         * Check this hasn't already been processed. Only one execution is allowed
         */
        if (this.processed) {
            throw new TemplateProcessingException(
                    "This delayed model has already been processed. Execution can only take place once");
        }

        /*
         * Set the gathered model into the context
         */
        this.context.setVariable(GATHERED_MODEL_CONTEXT_VARIABLE_NAME, this);

        /*
         * PROCESS THE MODEL
         */
        getInnerModel().process(handler);

        /*
         * DECREASE THE CONTEXT LEVEL
         * This was increased before starting gathering, when the handling of the first gathered event started.
         */
        this.context.decreaseLevel();

        /*
         * SET THE EXECUTION FLAG TO TRUE
         */
        this.processed = true;

    }



}