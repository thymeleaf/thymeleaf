/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
import org.thymeleaf.engine.TemplateModelController.SkipBody;


/*
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 *
 */
final class GatheringModelProcessable extends AbstractGatheringModelProcessable {


    private final IEngineContext context;

    private int offset;




    GatheringModelProcessable(
            final IEngineConfiguration configuration, ProcessorTemplateHandler processorTemplateHandler, final IEngineContext context,
            final TemplateModelController modelController, final TemplateFlowController flowController,
            final SkipBody gatheredSkipBody, final boolean gatheredSkipCloseTag,
            final ProcessorExecutionVars processorExecutionVars) {
        super(configuration, processorTemplateHandler, context, modelController, flowController, gatheredSkipBody, gatheredSkipCloseTag, processorExecutionVars);
        this.context = context;
        this.offset = 0;
    }



    public boolean process() {


        /*
         * First, check the stopProcess flag
         */
        final TemplateFlowController flowController = getFlowController();
        if (flowController != null && flowController.stopProcessing) {
            return false;
        }

        if (this.offset == 0) {
            /*
             * Reset the "skipBody" and "skipCloseTag" values at the event model controller, and also set this
             * synthetic model into the processor handler so that it can be used by the executed events
             */
            prepareProcessing();
        }

        /*
         * PROCESS THE MODEL
         */
        final Model model = getInnerModel();
        this.offset += model.process(getProcessorTemplateHandler(), this.offset, flowController);

        /*
         * Compute whether the whole model has been processed or not
         */
        final boolean processed =
                (flowController == null || (this.offset == model.queueSize && !flowController.stopProcessing));

        if (processed) {
            /*
             * DECREASE THE CONTEXT LEVEL
             * This was increased before starting gathering, when the handling of the first gathered event started.
             */
            this.context.decreaseLevel();
        }

        return processed;

    }



}