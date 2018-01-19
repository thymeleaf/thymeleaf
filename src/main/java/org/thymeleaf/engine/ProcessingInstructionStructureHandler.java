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

import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.processinginstruction.IProcessingInstructionStructureHandler;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Structure handler implementation, internally used by the engine.
 * </p>
 * <p>
 *   This class should not be directly used from outside the engine.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class ProcessingInstructionStructureHandler implements IProcessingInstructionStructureHandler {


    boolean setProcessingInstruction;
    String setProcessingInstructionTarget;
    String setProcessingInstructionContent;

    boolean replaceWithModel;
    IModel replaceWithModelValue;
    boolean replaceWithModelProcessable;

    boolean removeProcessingInstruction;




    ProcessingInstructionStructureHandler() {
        super();
        reset();
    }




    public void setProcessingInstruction(final String target, final String content) {
        reset();
        Validate.notNull(target, "Target cannot be null");
        Validate.notNull(content, "Content cannot be null");
        this.setProcessingInstruction = true;
        this.setProcessingInstructionTarget = target;
        this.setProcessingInstructionContent = content;
    }


    public void replaceWith(final IModel model, final boolean processable) {
        reset();
        Validate.notNull(model, "Model cannot be null");
        this.replaceWithModel = true;
        this.replaceWithModelValue = model;
        this.replaceWithModelProcessable = processable;
    }


    public void removeProcessingInstruction() {
        reset();
        this.removeProcessingInstruction = true;
    }




    public void reset() {

        this.setProcessingInstruction = false;
        this.setProcessingInstructionTarget = null;
        this.setProcessingInstructionContent = null;

        this.replaceWithModel = false;
        this.replaceWithModelValue = null;
        this.replaceWithModelProcessable = false;

        this.removeProcessingInstruction = false;

    }


}
