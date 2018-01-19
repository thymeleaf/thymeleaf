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
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.TemplateModelController.SkipBody;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;


/*
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 *
 */
abstract class AbstractGatheringModelProcessable implements IGatheringModelProcessable {


    private final ProcessorTemplateHandler processorTemplateHandler;
    private final IEngineContext context;
    private final Model syntheticModel;
    private final TemplateModelController modelController;
    private final TemplateFlowController flowController;

    private final SkipBody buildTimeSkipBody;
    private final boolean buildTimeSkipCloseTag;

    private final ProcessorExecutionVars processorExecutionVars;

    private boolean gatheringFinished = false;

    private int modelLevel;


    AbstractGatheringModelProcessable(
            final IEngineConfiguration configuration, final ProcessorTemplateHandler processorTemplateHandler, final IEngineContext context,
            final TemplateModelController modelController, final TemplateFlowController flowController,
            final SkipBody buildTimeSkipBody, final boolean buildTimeSkipCloseTag,
            final ProcessorExecutionVars processorExecutionVars) {

        super();

        this.processorTemplateHandler = processorTemplateHandler;
        this.context = context;
        this.modelController = modelController;
        this.flowController = flowController;
        this.buildTimeSkipBody = buildTimeSkipBody;
        this.buildTimeSkipCloseTag = buildTimeSkipCloseTag;

        if (this.context == null) {
            throw new TemplateProcessingException(
                    "Neither iteration nor model gathering are supported because local variable support is DISABLED. " +
                    "This is due to the use of an implementation of the " + ITemplateContext.class.getName() + " interface " +
                    "that does not provide local-variable support. In order to have local-variable support, the context " +
                    "implementation should also implement the " + IEngineContext.class.getName() +
                    " interface");
        }

        this.syntheticModel = new Model(configuration, context.getTemplateMode());
        this.processorExecutionVars = processorExecutionVars.cloneVars();
        this.gatheringFinished = false;
        this.modelLevel = 0;
    }


    public final void resetGatheredSkipFlagsAfterNoIterations() {
        if (this.buildTimeSkipBody == SkipBody.PROCESS_ONE_ELEMENT) {
            this.modelController.skip(SkipBody.SKIP_ELEMENTS, this.buildTimeSkipCloseTag);
        } else {
            this.modelController.skip(this.buildTimeSkipBody, this.buildTimeSkipCloseTag);
        }
    }

    public final void resetGatheredSkipFlags() {
        this.modelController.skip(this.buildTimeSkipBody, this.buildTimeSkipCloseTag);
    }


    protected final void prepareProcessing() {
        this.processorTemplateHandler.setCurrentGatheringModel(this);
        resetGatheredSkipFlags();
    }


    protected final ProcessorTemplateHandler getProcessorTemplateHandler() {
        return this.processorTemplateHandler;
    }

    protected final TemplateFlowController getFlowController() {
        return this.flowController;
    }


    public final boolean isGatheringFinished() {
        return this.gatheringFinished;
    }



    protected final IEngineContext getContext() {
        return this.context;
    }



    public ProcessorExecutionVars initializeProcessorExecutionVars() {
        // This was cloned during construction
        return this.processorExecutionVars;
    }




    public final Model getInnerModel() {
        return this.syntheticModel;
    }





    public final void gatherText(final IText text) {
        if (this.gatheringFinished) {
            throw new TemplateProcessingException("Gathering is finished already! We cannot gather more events");
        }
        this.syntheticModel.add(text);
    }


    public final void gatherComment(final IComment comment) {
        if (this.gatheringFinished) {
            throw new TemplateProcessingException("Gathering is finished already! We cannot gather more events");
        }
        this.syntheticModel.add(comment);
    }


    public final void gatherCDATASection(final ICDATASection cdataSection) {
        if (this.gatheringFinished) {
            throw new TemplateProcessingException("Gathering is finished already! We cannot gather more events");
        }
        this.syntheticModel.add(cdataSection);
    }


    public final void gatherStandaloneElement(final IStandaloneElementTag standaloneElementTag) {
        if (this.gatheringFinished) {
            throw new TemplateProcessingException("Gathering is finished already! We cannot gather more events");
        }
        this.syntheticModel.add(standaloneElementTag);
        if (this.modelLevel == 0) {
            this.gatheringFinished = true;
        }
    }


    public final void gatherOpenElement(final IOpenElementTag openElementTag) {
        if (this.gatheringFinished) {
            throw new TemplateProcessingException("Gathering is finished already! We cannot gather more events");
        }
        this.syntheticModel.add(openElementTag);
        this.modelLevel++;
    }


    public final void gatherCloseElement(final ICloseElementTag closeElementTag) {
        if (closeElementTag.isUnmatched()) {
            gatherUnmatchedCloseElement(closeElementTag);
            return;
        }
        if (this.gatheringFinished) {
            throw new TemplateProcessingException("Gathering is finished already! We cannot gather more events");
        }
        this.modelLevel--;
        this.syntheticModel.add(closeElementTag);
        if (this.modelLevel == 0) {
            // OK, we are finished gathering, this close tag ends the process
            this.gatheringFinished = true;
        }
    }


    public final void gatherUnmatchedCloseElement(final ICloseElementTag closeElementTag) {
        if (this.gatheringFinished) {
            throw new TemplateProcessingException("Gathering is finished already! We cannot gather more events");
        }
        this.syntheticModel.add(closeElementTag);
    }


    public final void gatherDocType(final IDocType docType) {
        if (this.gatheringFinished) {
            throw new TemplateProcessingException("Gathering is finished already! We cannot gather more events");
        }
        this.syntheticModel.add(docType);
    }


    public final void gatherXMLDeclaration(final IXMLDeclaration xmlDeclaration) {
        if (this.gatheringFinished) {
            throw new TemplateProcessingException("Gathering is finished already! We cannot gather more events");
        }
        this.syntheticModel.add(xmlDeclaration);
    }


    public final void gatherProcessingInstruction(final IProcessingInstruction processingInstruction) {
        if (this.gatheringFinished) {
            throw new TemplateProcessingException("Gathering is finished already! We cannot gather more events");
        }
        this.syntheticModel.add(processingInstruction);
    }

    
}