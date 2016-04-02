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
final class EventModelController {

    static final int DEFAULT_MODEL_LEVELS = 25;

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

    private AbstractGatheredModel gatheredModel;

    private SkipBody skipBody;
    private SkipBody[] skipBodyByLevel;
    private boolean[] skipCloseTagByLevel;
    private IProcessableElementTag[] unskippedFirstElementByLevel;

    private int modelLevel;


    EventModelController(
            final IEngineConfiguration configuration, final TemplateMode templateMode,
            final IEngineContext context) {

        super();

        this.configuration = configuration;
        this.templateMode = templateMode;
        this.context = context;

        this.gatheredModel = null;

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


    void startGatheringDelayedModel(final IProcessableElementTag firstTag) {

        this.gatheredModel = new DelayedGatheredModel(this.configuration, this.context);

        if (firstTag instanceof IOpenElementTag) {
            this.gatheredModel.gatherOpenElement((IOpenElementTag)firstTag);
            this.modelLevel--;
        } else if (firstTag instanceof IStandaloneElementTag) {
            this.gatheredModel.gatherStandaloneElement((IStandaloneElementTag)firstTag);
        } else {
            throw new TemplateProcessingException("Unknown type of first gathering tag: " + firstTag.getClass().getName());
        }

    }


    void startGatheringIteratedModel(
            final IProcessableElementTag firstTag, final String iterVariableName, final String iterStatusVariableName, final Object iteratedObject,
            final Text precedingWhitespace) {

        this.gatheredModel =
                new IteratedGatheredModel(this.configuration, this.context, iterVariableName, iterStatusVariableName, iteratedObject, precedingWhitespace);

        if (firstTag instanceof IOpenElementTag) {
            this.gatheredModel.gatherOpenElement((IOpenElementTag)firstTag);
            this.modelLevel--;
        } else if (firstTag instanceof IStandaloneElementTag) {
            this.gatheredModel.gatherStandaloneElement((IStandaloneElementTag)firstTag);
        } else {
            throw new TemplateProcessingException("Unknown type of first gathering tag: " + firstTag.getClass().getName());
        }

    }


    boolean isGatheringFinished() {
        return this.gatheredModel != null && this.gatheredModel.isGathered();
    }


    IGatheredModel getGatheredModel() {
        return this.gatheredModel;
    }


    void resetGathering() {
        this.gatheredModel = null;
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



    private void increaseModelLevel() {
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


    private void decreaseModelLevel() {
        this.modelLevel--;
        this.skipBody = this.skipBodyByLevel[this.modelLevel];
        if (this.context != null) {
            this.context.decreaseLevel();
        }
    }







    boolean shouldProcessText(final IText text) {
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherText(text);
            return false;
        }
        return this.skipBody.processNonElements;
    }


    boolean shouldProcessComment(final IComment comment) {
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherComment(comment);
            return false;
        }
        return this.skipBody.processNonElements;
    }


    boolean shouldProcessCDATASection(final ICDATASection cdataSection) {
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherCDATASection(cdataSection);
            return false;
        }
        return this.skipBody.processNonElements;
    }


    boolean shouldProcessStandaloneElement(final IStandaloneElementTag standaloneElementTag) {
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherStandaloneElement(standaloneElementTag);
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
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherOpenElement(openElementTag);
            return false;
        }
        boolean process = this.skipBody.processElements;
        if (this.skipBody == SkipBody.PROCESS_ONE_ELEMENT) {
            // This is the first (still unclosed) element, let's save it in case it is iterated
            this.unskippedFirstElementByLevel[this.modelLevel] = openElementTag;
        } else if (this.skipBody == SkipBody.SKIP_ELEMENTS && this.unskippedFirstElementByLevel[this.modelLevel] == openElementTag) {
            // The unskipped first element is being iterated! we should allow its processing
            skipBody(SkipBody.PROCESS_ONE_ELEMENT);
            process = true;
        }
        increaseModelLevel();
        return process;
    }


    boolean shouldProcessCloseElement(final ICloseElementTag closeElementTag) {
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherCloseElement(closeElementTag);
            return false;
        }
        decreaseModelLevel();
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
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherUnmatchedCloseElement(closeElementTag);
            return false;
        }
        return this.skipBody.processNonElements; // We will treat this as a non-element
    }


    boolean shouldProcessDocType(final IDocType docType) {
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherDocType(docType);
            return false;
        }
        return this.skipBody.processNonElements;
    }


    boolean shouldProcessXMLDeclaration(final IXMLDeclaration xmlDeclaration) {
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherXMLDeclaration(xmlDeclaration);
            return false;
        }
        return this.skipBody.processNonElements;
    }


    boolean shouldProcessProcessingInstruction(final IProcessingInstruction processingInstruction) {
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherProcessingInstruction(processingInstruction);
            return false;
        }
        return this.skipBody.processNonElements;
    }

    
}