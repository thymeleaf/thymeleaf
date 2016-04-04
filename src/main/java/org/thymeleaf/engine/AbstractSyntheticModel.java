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
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.EventModelController.SkipBody;
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
abstract class AbstractSyntheticModel implements ISyntheticModel {


    private final IEngineContext context;
    private final Model syntheticModel;
    private final EventModelController eventModelController;

    private final SkipBody buildTimeSkipBody;
    private final boolean buildTimeSkipCloseTag;

    private final ElementProcessorIterator suspendedProcessorIterator;
    private final Model suspendedModelBefore;
    private final Model suspendedModelAfter;
    private final boolean suspendedModelAfterProcessable;
    private final boolean suspendedDiscardEvent;
    private final SkipBody suspendedSkipBody;
    private final boolean suspendedSkipCloseTag;

    private boolean gatheringFinished = false;

    private int modelLevel;


    AbstractSyntheticModel(
            final IEngineConfiguration configuration, final IEngineContext context,
            final EventModelController eventModelController, final SkipBody buildTimeSkipBody, final boolean buildTimeSkipCloseTag,
            final ElementProcessorIterator suspendedProcessorIterator,
            final Model suspendedModelBefore, final Model suspendedModelAfter, final boolean suspendedModelAfterProcessable,
            final boolean suspendedDiscardEvent, final SkipBody suspendedSkipBody,
            final boolean suspendedSkipCloseTag) {

        super();

        this.context = context;
        this.eventModelController = eventModelController;
        this.buildTimeSkipBody = buildTimeSkipBody;
        this.buildTimeSkipCloseTag = buildTimeSkipCloseTag;
        this.syntheticModel = new Model(configuration, context.getTemplateMode());
        this.suspendedProcessorIterator = suspendedProcessorIterator;
        this.suspendedModelBefore = suspendedModelBefore;
        this.suspendedModelAfter = suspendedModelAfter;
        this.suspendedModelAfterProcessable = suspendedModelAfterProcessable;
        this.suspendedDiscardEvent = suspendedDiscardEvent;
        this.suspendedSkipBody = suspendedSkipBody;
        this.suspendedSkipCloseTag = suspendedSkipCloseTag;

        this.gatheringFinished = false;

        this.modelLevel = 0;

        if (this.context == null) {
            throw new TemplateProcessingException(
                    "Neither iteration nor model gathering are supported because local variable support is DISABLED. " +
                    "This is due to the use of an implementation of the " + ITemplateContext.class.getName() + " interface " +
                    "that does not provide local-variable support. In order to have local-variable support, the context " +
                    "implementation should also implement the " + IEngineContext.class.getName() +
                    " interface");
        }


    }


    public final void resetGatheredSkipFlags() {
        this.eventModelController.skip(this.buildTimeSkipBody, this.buildTimeSkipCloseTag);
    }



    public final boolean isGatheringFinished() {
        return this.gatheringFinished;
    }


    protected final IEngineContext getContext() {
        return this.context;
    }



    public final ElementProcessorIterator getSuspendedProcessorIterator() {
        return this.suspendedProcessorIterator;
    }


    public final Model getSuspendedModelBefore() {
        return this.suspendedModelBefore;
    }


    public final Model getSuspendedModelAfter() {
        return this.suspendedModelAfter;
    }


    public final boolean isSuspendedModelAfterProcessable() {
        return this.suspendedModelAfterProcessable;
    }


    public final boolean isSuspendedDiscardEvent() {
        return this.suspendedDiscardEvent;
    }


    public final SkipBody getSuspendedSkipBody() {
        return this.suspendedSkipBody;
    }


    public final boolean isSuspendedSkipCloseTag() {
        return this.suspendedSkipCloseTag;
    }




    public final Model getInnerModel() {
        return this.syntheticModel;
    }



    public abstract void process(final ITemplateHandler handler);




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