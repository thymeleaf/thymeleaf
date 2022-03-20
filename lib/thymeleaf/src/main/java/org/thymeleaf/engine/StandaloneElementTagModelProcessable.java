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

import org.thymeleaf.context.IEngineContext;

/*
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 *
 */
final class StandaloneElementTagModelProcessable implements IEngineProcessable {

    private final StandaloneElementTag standaloneElementTag;
    private final ProcessorExecutionVars vars;
    private final IEngineContext context;
    private final TemplateFlowController flowController;
    private final TemplateModelController modelController;
    private final ProcessorTemplateHandler processorTemplateHandler;
    private final ITemplateHandler nextTemplateHandler;

    private boolean beforeProcessed;
    private boolean delegationProcessed;
    private boolean afterProcessed;
    private int offset;


    StandaloneElementTagModelProcessable(
            final StandaloneElementTag standaloneElementTag, final ProcessorExecutionVars vars, final IEngineContext context,
            final TemplateModelController modelController, final TemplateFlowController flowController,
            final ProcessorTemplateHandler processorTemplateHandler, final ITemplateHandler nextTemplateHandler) {
        super();
        this.standaloneElementTag = standaloneElementTag;
        this.vars = vars;
        this.context = context;
        this.flowController = flowController;
        this.modelController = modelController;
        this.processorTemplateHandler = processorTemplateHandler;
        this.nextTemplateHandler = nextTemplateHandler;
        this.beforeProcessed = false;
        this.delegationProcessed = false;
        this.afterProcessed = false;
        this.offset = 0;
    }


    public boolean process() {

        /*
         * First, check the stopProcess flag
         */
        if (this.flowController.stopProcessing) {
            return false;
        }


        if (!this.beforeProcessed) {
            /*
             * PROCESS THE QUEUE BEFORE DELEGATING, if specified to do so
             */
            if (this.vars.modelBefore != null) {
                this.offset += this.vars.modelBefore.process(this.nextTemplateHandler, this.offset, this.flowController); // This is never processable
                if (this.offset < this.vars.modelBefore.queueSize || this.flowController.stopProcessing) {
                    return false;
                }
            }
            this.beforeProcessed = true;
            this.offset = 0;
        }


        if (!this.delegationProcessed) {
            /*
             * PROCESS THE REST OF THE HANDLER CHAIN and INCREASE THE MODEL LEVEL RIGHT AFTERWARDS
             */
            if (!this.vars.discardEvent) {
                this.nextTemplateHandler.handleStandaloneElement(this.standaloneElementTag);
            }
            this.delegationProcessed = true;
            this.offset = 0;
        }

        if (this.flowController.stopProcessing) {
            return false;
        }

        if (!this.afterProcessed) {
            /*
             * PROCESS THE QUEUE, launching all the queued events. Note executing the queue after increasing the model
             * level makes sense even if what the queue contains is a replacement for the complete element (including open
             * and close tags), because that way whatever comes in the queue will be encapsulated in a different model level
             * and its internal open/close tags should not affect the correct delimitation of this block.
             */
            if (this.vars.modelAfter != null) {
                final ITemplateHandler modelHandler = this.vars.modelAfterProcessable ? this.processorTemplateHandler : this.nextTemplateHandler;
                this.offset += this.vars.modelAfter.process(modelHandler, this.offset, this.flowController);
                if (this.offset < this.vars.modelAfter.queueSize || this.flowController.stopProcessing) {
                    return false;
                }
            }
            this.afterProcessed = true;
        }


        /*
         * DECREASE THE CONTEXT LEVEL once we have executed all the processors (and maybe a body if we added
         * one to the tag converting it into an open tag)
         */
        if (this.context != null) {
            this.context.decreaseLevel();
        }

        return true;

    }



}