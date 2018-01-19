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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
import org.thymeleaf.model.ITemplateEvent;
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
final class TemplateModelController {

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

    // This is a set containing all the names of the elements for which, when iterated, we should preserve
    // the preceding whitespace if it exists so that resulting markup is more readable. Note they are all block
    // elements or, at least, elements for which preceding whitespace should not matter
    private static final Set<HTMLElementName> ITERATION_WHITESPACE_APPLICABLE_ELEMENT_NAMES =
            new HashSet<HTMLElementName>(Arrays.asList(new HTMLElementName[] {
                    ElementNames.forHTMLName("address"), ElementNames.forHTMLName("article"), ElementNames.forHTMLName("aside"),
                    ElementNames.forHTMLName("audio"), ElementNames.forHTMLName("blockquote"), ElementNames.forHTMLName("canvas"),
                    ElementNames.forHTMLName("dd"), ElementNames.forHTMLName("div"), ElementNames.forHTMLName("dl"),
                    ElementNames.forHTMLName("dt"), ElementNames.forHTMLName("fieldset"), ElementNames.forHTMLName("figcaption"),
                    ElementNames.forHTMLName("figure"), ElementNames.forHTMLName("footer"),ElementNames.forHTMLName("form"),
                    ElementNames.forHTMLName("h1"), ElementNames.forHTMLName("h2"), ElementNames.forHTMLName("h3"),
                    ElementNames.forHTMLName("h4"), ElementNames.forHTMLName("h5"), ElementNames.forHTMLName("h6"),
                    ElementNames.forHTMLName("header"), ElementNames.forHTMLName("hgroup"), ElementNames.forHTMLName("hr"),
                    ElementNames.forHTMLName("li"), ElementNames.forHTMLName("main"), ElementNames.forHTMLName("nav"),
                    ElementNames.forHTMLName("noscript"), ElementNames.forHTMLName("ol"), ElementNames.forHTMLName("option"),
                    ElementNames.forHTMLName("output"), ElementNames.forHTMLName("p"), ElementNames.forHTMLName("pre"),
                    ElementNames.forHTMLName("section"), ElementNames.forHTMLName("table"), ElementNames.forHTMLName("tbody"),
                    ElementNames.forHTMLName("td"), ElementNames.forHTMLName("tfoot"), ElementNames.forHTMLName("th"),
                    ElementNames.forHTMLName("tr"), ElementNames.forHTMLName("ul"), ElementNames.forHTMLName("video")
            }));




    private final IEngineConfiguration configuration;
    private final TemplateMode templateMode;
    private final ProcessorTemplateHandler processorTemplateHandler;
    private final IEngineContext context;

    private TemplateFlowController templateFlowController;

    private AbstractGatheringModelProcessable gatheredModel;

    private SkipBody skipBody;
    private SkipBody[] skipBodyByLevel;
    private boolean[] skipCloseTagByLevel;
    private IProcessableElementTag[] unskippedFirstElementByLevel;

    // These two variables will help us keep account of what events have been triggered before an iteration, in
    // case we want to apply prettifying to the surrounding white spaces of an iterated piece of markup
    private ITemplateEvent lastEvent = null;
    private ITemplateEvent secondToLastEvent = null;

    private int modelLevel;


    TemplateModelController(
            final IEngineConfiguration configuration, final TemplateMode templateMode,
            final ProcessorTemplateHandler processorTemplateHandler, final IEngineContext context) {

        super();

        this.configuration = configuration;
        this.templateMode = templateMode;
        this.processorTemplateHandler = processorTemplateHandler;
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


    void setTemplateFlowController(final TemplateFlowController templateFlowController) {
        this.templateFlowController = templateFlowController;
    }


    int getModelLevel() {
        return this.modelLevel;
    }


    void startGatheringDelayedModel(
            final IOpenElementTag firstTag, final ProcessorExecutionVars processorExecutionVars) {

        this.modelLevel--;

        final SkipBody gatheredSkipBody = this.skipBodyByLevel[this.modelLevel];
        final boolean gatheredSkipCloseTagByLevel = this.skipCloseTagByLevel[this.modelLevel];

        this.gatheredModel =
                new GatheringModelProcessable(
                        this.configuration, this.processorTemplateHandler, this.context,
                        this, this.templateFlowController,
                        gatheredSkipBody, gatheredSkipCloseTagByLevel, processorExecutionVars);

        this.gatheredModel.gatherOpenElement(firstTag);

    }


    void startGatheringDelayedModel(
            final IStandaloneElementTag firstTag, final ProcessorExecutionVars processorExecutionVars) {

        SkipBody gatheredSkipBody = this.skipBodyByLevel[this.modelLevel];
        gatheredSkipBody = (gatheredSkipBody == SkipBody.SKIP_ELEMENTS ? SkipBody.PROCESS_ONE_ELEMENT : gatheredSkipBody);
        final boolean gatheredSkipCloseTagByLevel = this.skipCloseTagByLevel[this.modelLevel];

        this.gatheredModel =
                new GatheringModelProcessable(
                        this.configuration, this.processorTemplateHandler, this.context,
                        this, this.templateFlowController,
                        gatheredSkipBody, gatheredSkipCloseTagByLevel, processorExecutionVars);

        this.gatheredModel.gatherStandaloneElement(firstTag);

    }


    void startGatheringIteratedModel(
            final IOpenElementTag firstTag, final ProcessorExecutionVars processorExecutionVars,
            final String iterVariableName, final String iterStatusVariableName, final Object iteratedObject) {

        this.modelLevel--;

        final SkipBody gatheredSkipBody = this.skipBodyByLevel[this.modelLevel];
        final boolean gatheredSkipCloseTagByLevel = this.skipCloseTagByLevel[this.modelLevel];

        final Text precedingWhitespace = computeWhiteSpacePrecedingIteration(firstTag.getElementDefinition().elementName);

        this.gatheredModel =
                new IteratedGatheringModelProcessable(
                        this.configuration, this.processorTemplateHandler, this.context,
                        this, this.templateFlowController,
                        gatheredSkipBody, gatheredSkipCloseTagByLevel, processorExecutionVars,
                        iterVariableName, iterStatusVariableName, iteratedObject, precedingWhitespace);

        this.gatheredModel.gatherOpenElement(firstTag);

    }


    void startGatheringIteratedModel(
            final IStandaloneElementTag firstTag, final ProcessorExecutionVars processorExecutionVars,
            final String iterVariableName, final String iterStatusVariableName, final Object iteratedObject) {

        SkipBody gatheredSkipBody = this.skipBodyByLevel[this.modelLevel];
        gatheredSkipBody = (gatheredSkipBody == SkipBody.SKIP_ELEMENTS ? SkipBody.PROCESS_ONE_ELEMENT : gatheredSkipBody);
        final boolean gatheredSkipCloseTagByLevel = this.skipCloseTagByLevel[this.modelLevel];

        final Text precedingWhitespace = computeWhiteSpacePrecedingIteration(firstTag.getElementDefinition().elementName);

        this.gatheredModel =
                new IteratedGatheringModelProcessable(
                        this.configuration, this.processorTemplateHandler, this.context,
                        this, this.templateFlowController,
                        gatheredSkipBody, gatheredSkipCloseTagByLevel, processorExecutionVars,
                        iterVariableName, iterStatusVariableName, iteratedObject, precedingWhitespace);

        this.gatheredModel.gatherStandaloneElement(firstTag);

    }


    GatheringModelProcessable createStandaloneEquivalentModel(
            final StandaloneElementTag standaloneElementTag, final ProcessorExecutionVars processorExecutionVars) {

        SkipBody gatheredSkipBody = this.skipBodyByLevel[this.modelLevel];
        gatheredSkipBody = (gatheredSkipBody == SkipBody.SKIP_ELEMENTS ? SkipBody.PROCESS_ONE_ELEMENT : gatheredSkipBody);
        final boolean gatheredSkipCloseTagByLevel = this.skipCloseTagByLevel[this.modelLevel];

        final OpenElementTag openTag =
                new OpenElementTag(
                        standaloneElementTag.templateMode, standaloneElementTag.elementDefinition,
                        standaloneElementTag.elementCompleteName, standaloneElementTag.attributes, standaloneElementTag.synthetic,
                        standaloneElementTag.templateName, standaloneElementTag.line, standaloneElementTag.col);
        final CloseElementTag closeTag =
                new CloseElementTag(
                        standaloneElementTag.templateMode, standaloneElementTag.elementDefinition,
                        standaloneElementTag.elementCompleteName, null, standaloneElementTag.synthetic, false,
                        standaloneElementTag.templateName, standaloneElementTag.line, standaloneElementTag.col);

        final GatheringModelProcessable equivalentModel =
                new GatheringModelProcessable(
                        this.configuration, this.processorTemplateHandler, this.context,
                        this, this.templateFlowController,
                        gatheredSkipBody, gatheredSkipCloseTagByLevel, processorExecutionVars);

        equivalentModel.gatherOpenElement(openTag);
        equivalentModel.gatherCloseElement(closeTag);

        return equivalentModel;

    }


    boolean isGatheringFinished() {
        return this.gatheredModel != null && this.gatheredModel.isGatheringFinished();
    }


    IGatheringModelProcessable getGatheredModel() {
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



    private void increaseModelLevel(final IOpenElementTag openElementTag) {
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
            this.context.setElementTag(openElementTag);
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
        this.lastEvent = text;
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherText(text);
            return false;
        }
        return this.skipBody.processNonElements;
    }


    boolean shouldProcessComment(final IComment comment) {
        this.lastEvent = comment;
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherComment(comment);
            return false;
        }
        return this.skipBody.processNonElements;
    }


    boolean shouldProcessCDATASection(final ICDATASection cdataSection) {
        this.lastEvent = cdataSection;
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherCDATASection(cdataSection);
            return false;
        }
        return this.skipBody.processNonElements;
    }


    boolean shouldProcessStandaloneElement(final IStandaloneElementTag standaloneElementTag) {
        this.secondToLastEvent = this.lastEvent;
        this.lastEvent = standaloneElementTag;
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherStandaloneElement(standaloneElementTag);
            return false;
        }
        boolean process = this.skipBody.processElements;
        if (this.skipBody == SkipBody.PROCESS_ONE_ELEMENT) {
            // This was the first element, the others will be skipped. Let's save it in case it is iterated
            this.unskippedFirstElementByLevel[this.modelLevel] = standaloneElementTag;
            skipBody(SkipBody.SKIP_ELEMENTS);
            process = true;
        }
        if (process) {
            /*
             * INCREASE THE CONTEXT LEVEL so that all local variables created during the execution of processors
             * are available for the rest of the processors as well as the body of the tag
             */
            if (this.context != null) {
                this.context.increaseLevel();
                this.context.setElementTag(standaloneElementTag);
            }
        }
        return process;
    }


    boolean shouldProcessOpenElement(final IOpenElementTag openElementTag) {
        this.secondToLastEvent = this.lastEvent;
        this.lastEvent = openElementTag;
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
        increaseModelLevel(openElementTag);
        return process;
    }


    boolean shouldProcessCloseElement(final ICloseElementTag closeElementTag) {
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherCloseElement(closeElementTag);
            return false;
        }
        this.lastEvent = closeElementTag;
        decreaseModelLevel();
        if (this.skipBody == SkipBody.PROCESS_ONE_ELEMENT) {
            // This was the first element, the others will be skipped
            skipBody(SkipBody.SKIP_ELEMENTS);
            if (this.skipCloseTagByLevel[this.modelLevel]) {
                this.skipCloseTagByLevel[this.modelLevel] = false;
                return false;
            } else {
                return true;
            }
        }
        if (this.skipCloseTagByLevel[this.modelLevel]) {
            this.skipCloseTagByLevel[this.modelLevel] = false;
            return false;
        }
        return this.skipBody.processElements;
    }


    boolean shouldProcessUnmatchedCloseElement(final ICloseElementTag closeElementTag) {
        this.lastEvent = closeElementTag;
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherUnmatchedCloseElement(closeElementTag);
            return false;
        }
        return this.skipBody.processNonElements; // We will treat this as a non-element
    }


    boolean shouldProcessDocType(final IDocType docType) {
        this.lastEvent = docType;
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherDocType(docType);
            return false;
        }
        return this.skipBody.processNonElements;
    }


    boolean shouldProcessXMLDeclaration(final IXMLDeclaration xmlDeclaration) {
        this.lastEvent = xmlDeclaration;
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherXMLDeclaration(xmlDeclaration);
            return false;
        }
        return this.skipBody.processNonElements;
    }


    boolean shouldProcessProcessingInstruction(final IProcessingInstruction processingInstruction) {
        this.lastEvent = processingInstruction;
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherProcessingInstruction(processingInstruction);
            return false;
        }
        return this.skipBody.processNonElements;
    }




    private Text computeWhiteSpacePrecedingIteration(final ElementName iteratedElementName) {
        if (this.secondToLastEvent == null || !(this.secondToLastEvent instanceof IText)) {
            return null;
        }
        if (this.templateMode == TemplateMode.XML ||
                (this.templateMode == TemplateMode.HTML && ITERATION_WHITESPACE_APPLICABLE_ELEMENT_NAMES.contains(iteratedElementName))) {
            final Text lastEngineText = Text.asEngineText((IText) this.secondToLastEvent);
            if (lastEngineText.isWhitespace()) {
                return lastEngineText;
            }
        }
        return null;
    }

    
}