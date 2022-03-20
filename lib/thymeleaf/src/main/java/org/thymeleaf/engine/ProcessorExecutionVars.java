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

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
final class ProcessorExecutionVars {

    final ElementProcessorIterator processorIterator;
    Model modelBefore = null;
    Model modelAfter = null;
    boolean modelAfterProcessable = false;
    boolean discardEvent = false;
    TemplateModelController.SkipBody skipBody = TemplateModelController.SkipBody.PROCESS;
    boolean skipCloseTag = false;


    ProcessorExecutionVars() {
        super();
        this.processorIterator = new ElementProcessorIterator();
    }


    ProcessorExecutionVars cloneVars() {
        final ProcessorExecutionVars clone = new ProcessorExecutionVars();
        clone.processorIterator.resetAsCloneOf(this.processorIterator);
        if (this.modelBefore != null) {
            clone.modelBefore = (Model) this.modelBefore.cloneModel();
        }
        if (this.modelAfter != null) {
            clone.modelAfter = (Model) this.modelAfter.cloneModel();
        }
        clone.modelAfterProcessable = this.modelAfterProcessable;
        clone.discardEvent = this.discardEvent;
        clone.skipBody = this.skipBody;
        clone.skipCloseTag = this.skipCloseTag;
        return clone;
    }

}
