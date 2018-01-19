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

import org.thymeleaf.model.ITemplateEnd;

/*
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 *
 */
final class TemplateEndModelProcessable implements IEngineProcessable {

    private final ITemplateEnd templateEnd;
    private final Model model;
    private final ITemplateHandler modelHandler;
    private final ProcessorTemplateHandler processorTemplateHandler;
    private final ITemplateHandler nextHandler;
    private final TemplateFlowController flowController;

    private int offset;


    TemplateEndModelProcessable(
            final ITemplateEnd templateEnd,
            final Model model, final ITemplateHandler modelHandler,
            final ProcessorTemplateHandler processorTemplateHandler, final ITemplateHandler nextHandler,
            final TemplateFlowController flowController) {
        super();
        this.templateEnd = templateEnd;
        this.model = model;
        this.modelHandler = modelHandler;
        this.processorTemplateHandler = processorTemplateHandler;
        this.nextHandler = nextHandler;
        this.flowController = flowController;
        this.offset = 0;
    }


    public boolean process() {

        /*
         * First, check the stopProcess flag
         */
        if (this.flowController.stopProcessing) {
            return false;
        }

        /*
         * Process the queue
         */
        this.offset += this.model.process(this.modelHandler, this.offset, this.flowController);
        if (this.offset < this.model.queueSize || this.flowController.stopProcessing) {
            return false;
        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN.
         */
        this.nextHandler.handleTemplateEnd(this.templateEnd);


        /*
         * LAST ROUND OF CHECKS. If we have not returned our indexes to -1, something has gone wrong during processing
         */
        this.processorTemplateHandler.performTearDownChecks(this.templateEnd);


        /*
         * RETURN TRUE. Even if a stop was signaled after handling the TemplateEnd, we should not worry about it
         * because it would only affect events being executed AFTER delegating. And there are no events executing
         * after the TemplateEnd, ever.
         */
        return true;

    }


}