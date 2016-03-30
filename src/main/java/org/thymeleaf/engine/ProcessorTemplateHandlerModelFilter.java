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

import java.util.Arrays;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;
import org.thymeleaf.templatemode.TemplateMode;


/*
 * This is an internal class for gathering a sequence of template events into a Model object. This will
 * be used from ProcessorTemplateHandler for gathering iterated sequences, as well as models that are to be
 * passed to ElementModelProcessors.
 * 
 * NOTE there is no need to implement ITemplateHandler or extend AbstractTemplateHandler.
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 *
 */
final class ProcessorTemplateHandlerModelFilter {

    static final int DEFAULT_MODEL_LEVELS = 25;

    enum GatheringType { ITERATION, MODEL, NONE }

    enum SkipBody {

        PROCESS(true, true, true),
        SKIP_ALL(false, false, false),
        SKIP_ELEMENTS(false, true, false),
        PROCESS_ONE_ELEMENT(true, true, true);

        final boolean processElements;
        final boolean processNonElements;
        final boolean processChildren;

        SkipBody(final boolean processElements, final boolean processNonElements, final boolean processChildren) {
            this.processElements = processElements;
            this.processNonElements = processNonElements;
            this.processChildren = processChildren;
        }

    }


    private final IEngineConfiguration configuration;
    private final TemplateMode templateMode;
    private final IEngineContext context;

    private boolean gather;
    private boolean gatheringFinished;
    private GatheringType gatheringType;
    private Model gatheredModel;
    private int gatherLevel;

    private SkipBody skipBody;
    private SkipBody[] skipBodyByLevel;
    private boolean[] skipCloseTagByLevel;
    private IProcessableElementTag[] unskippedFirstElementByLevel;

    private int modelLevel;


    ProcessorTemplateHandlerModelFilter(
            final IEngineConfiguration configuration, final TemplateMode templateMode,
            final IEngineContext context) {

        super();

        this.configuration = configuration;
        this.templateMode = templateMode;
        this.context = context;

        this.gather = false;
        this.gatheringFinished = false;
        this.gatheringType = null;
        this.gatheredModel = null;
        this.gatherLevel = -1;

        this.skipBodyByLevel = new SkipBody[DEFAULT_MODEL_LEVELS];
        this.skipBodyByLevel[this.modelLevel] = SkipBody.PROCESS;
        this.skipBody = this.skipBodyByLevel[this.modelLevel];

        this.skipCloseTagByLevel = new boolean[DEFAULT_MODEL_LEVELS];
        this.skipCloseTagByLevel[this.modelLevel] = false;

        this.unskippedFirstElementByLevel = new IProcessableElementTag[DEFAULT_MODEL_LEVELS];
        this.unskippedFirstElementByLevel[this.modelLevel] = null;

        this.modelLevel = 0;

    }


    int getModelLevel() {
        return this.modelLevel;
    }


    void startGathering(final GatheringType gatheringType, final OpenElementTag firstTag) {

        this.gatheringType = gatheringType;

        // We create a new model for each gathering operation, so that this filter can be used for nested executions
        // If not, we would risk resetting a model object which sequence of events we are actually processing
        this.gatheredModel = new Model(this.configuration, this.templateMode);

        this.gather = true;
        this.gatheringFinished = false;

        // We will add this tag directly, without increasing the level, because before initializing gathering
        // we have already checked if we had to skip the open tag (and we obviously said "no"), and at that moment
        // the model level was already increased
        this.gatheredModel.add(firstTag);

        // The gathering level will have to be checked after the close tag decreases the level open by the open tag
        this.gatherLevel = this.modelLevel - 1;

    }


    void resetGathering() {
        this.gather = false;
        this.gatheringFinished = false;
        this.gatheringType = null;
        // TODO Once gathering of model for openelement->modelprocessor is refactored, we should probably be able to set model to null here
        this.gatherLevel = -1;
    }


    boolean isGatheringFinished() {
        return this.gatheringFinished;
    }


    GatheringType getGatheringType() {
        return this.gatheringType;
    }


    Model getGatheredModel() {
        return this.gatheredModel;
    }



    void skip(final SkipBody skipBody, final boolean skipCloseTag) {
        skipBody(skipBody);
        skipCloseTag(skipCloseTag);
    }

    private void skipBody(final SkipBody skipBody) {
        this.skipBodyByLevel[this.modelLevel] = skipBody;
        this.skipBody = skipBody;
    }


    private void skipCloseTag(final boolean skipCloseTag) {
        if (!skipCloseTag) {
            return;
        }
        if (this.modelLevel == 0) {
            throw new TemplateProcessingException("Cannot set containing close tag to skip when model level is zero");
        }
        this.skipCloseTagByLevel[this.modelLevel - 1] = true;
    }



    private void increaseLevel() {
        this.modelLevel++;
        if (this.skipBodyByLevel.length == this.modelLevel) {
            this.skipBodyByLevel = Arrays.copyOf(this.skipBodyByLevel, this.skipBodyByLevel.length + DEFAULT_MODEL_LEVELS/2);
            this.skipCloseTagByLevel = Arrays.copyOf(this.skipCloseTagByLevel, this.skipCloseTagByLevel.length + DEFAULT_MODEL_LEVELS/2);
            this.unskippedFirstElementByLevel = Arrays.copyOf(this.unskippedFirstElementByLevel, this.unskippedFirstElementByLevel.length + DEFAULT_MODEL_LEVELS/2);
        }
        skipBody(this.skipBody.processChildren ? SkipBody.PROCESS : SkipBody.SKIP_ALL);
        this.skipCloseTagByLevel[this.modelLevel] = false;
        this.unskippedFirstElementByLevel[this.modelLevel] = null;
        if (this.context != null) {
            this.context.increaseLevel();
        }
    }


    private void decreaseLevel() {
        this.modelLevel--;
        this.skipBody = this.skipBodyByLevel[this.modelLevel];
        if (this.context != null) {
            this.context.decreaseLevel();
        }
    }




    boolean shouldProcessText(final IText text) {
        if (this.gather) {
            this.gatheredModel.add(text);
            return false;
        }
        return this.skipBody.processNonElements;
    }


    boolean shouldProcessComment(final IComment comment) {
        if (this.gather) {
            this.gatheredModel.add(comment);
            return false;
        }
        return this.skipBody.processNonElements;
    }


    boolean shouldProcessCDATASection(final ICDATASection cdataSection) {
        if (this.gather) {
            this.gatheredModel.add(cdataSection);
            return false;
        }
        return this.skipBody.processNonElements;
    }


    boolean shouldProcessStandaloneElement(final IStandaloneElementTag standaloneElementTag) {
        if (this.gather) {
            this.gatheredModel.add(standaloneElementTag);
            return false;
        }
        if (this.skipBody == SkipBody.PROCESS_ONE_ELEMENT) {
            // This was the first element, the others will be skipped. Let's save it in case it is iterated
            this.unskippedFirstElementByLevel[this.modelLevel] = standaloneElementTag;
            skipBody(SkipBody.SKIP_ELEMENTS);
            return true;
        }
        return this.skipBody.processElements;
    }


    boolean shouldProcessOpenElement(final IOpenElementTag openElementTag) {
        boolean process = this.skipBody.processElements;
        if (this.skipBody == SkipBody.PROCESS_ONE_ELEMENT) {
            // This is the first (still unclosed) element, let's save it in case it is iterated
            this.unskippedFirstElementByLevel[this.modelLevel] = openElementTag;
        } else if (this.skipBody == SkipBody.SKIP_ELEMENTS && this.unskippedFirstElementByLevel[this.modelLevel] == openElementTag) {
            // The unskipped first element is being iterated! we should allow its processing
            skipBody(SkipBody.PROCESS_ONE_ELEMENT);
            process = true;
        }
        increaseLevel();
        if (this.gather) {
            this.gatheredModel.add(openElementTag);
            return false;
        }
        return process;
    }


    boolean shouldProcessCloseElement(final ICloseElementTag closeElementTag) {
        decreaseLevel();
        if (this.gather) {
            this.gatheredModel.add(closeElementTag);
            if (this.modelLevel == this.gatherLevel) {
                // OK, we are finished gathering, this close tag ends the process
                this.gatheringFinished = true;
            }
            return false;
        }
        if (this.skipBody == SkipBody.PROCESS_ONE_ELEMENT) {
            // This was the first element, the others will be skipped
            skipBody(SkipBody.SKIP_ELEMENTS);
            return true;
        }
        if (this.skipCloseTagByLevel[this.modelLevel]) {
            this.skipCloseTagByLevel[this.modelLevel] = false;
            return false;
        }
        return this.skipBody.processElements;
    }


    boolean shouldProcessUnmatchedCloseElement(final ICloseElementTag closeElementTag) {
        if (this.gather) {
            this.gatheredModel.add(closeElementTag);
            return false;
        }
        return this.skipBody.processNonElements; // We will treat this as a non-element
    }


    boolean shouldProcessDocType(final IDocType docType) {
        if (this.gather) {
            this.gatheredModel.add(docType);
            return false;
        }
        return this.skipBody.processNonElements;
    }


    boolean shouldProcessXMLDeclaration(final IXMLDeclaration xmlDeclaration) {
        if (this.gather) {
            this.gatheredModel.add(xmlDeclaration);
            return false;
        }
        return this.skipBody.processNonElements;
    }


    boolean shouldProcessProcessingInstruction(final IProcessingInstruction processingInstruction) {
        if (this.gather) {
            this.gatheredModel.add(processingInstruction);
            return false;
        }
        return this.skipBody.processNonElements;
    }

    
}