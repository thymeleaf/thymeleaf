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

/*
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 *
 */
final class SimpleModelProcessable implements IEngineProcessable {

    private final Model model;
    private final ITemplateHandler modelHandler;
    private final TemplateFlowController flowController;

    private int offset;


    SimpleModelProcessable(
            final Model model, final ITemplateHandler modelHandler, final TemplateFlowController flowController) {
        super();
        this.model = model;
        this.modelHandler = modelHandler;
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

        /*
         * Compute whether the whole model has been processed or not
         */
        return (this.offset == this.model.queueSize && !this.flowController.stopProcessing);

    }


    ITemplateHandler getModelHandler() {
        return this.modelHandler;
    }


    Model getModel() {
        return this.model;
    }


}