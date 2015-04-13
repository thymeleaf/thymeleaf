/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.aurora.engine;

import java.util.Arrays;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.aurora.ITemplateEngineConfiguration;
import org.thymeleaf.aurora.context.ITemplateProcessingContext;
import org.thymeleaf.aurora.context.IVariablesMap;
import org.thymeleaf.aurora.model.IAutoCloseElementTag;
import org.thymeleaf.aurora.model.IAutoOpenElementTag;
import org.thymeleaf.aurora.model.ICDATASection;
import org.thymeleaf.aurora.model.ICloseElementTag;
import org.thymeleaf.aurora.model.IComment;
import org.thymeleaf.aurora.model.IDocType;
import org.thymeleaf.aurora.model.IOpenElementTag;
import org.thymeleaf.aurora.model.IProcessingInstruction;
import org.thymeleaf.aurora.model.IStandaloneElementTag;
import org.thymeleaf.aurora.model.IText;
import org.thymeleaf.aurora.model.IUnmatchedCloseElementTag;
import org.thymeleaf.aurora.model.IXMLDeclaration;
import org.thymeleaf.aurora.processor.IProcessor;
import org.thymeleaf.aurora.processor.element.IElementProcessor;
import org.thymeleaf.aurora.processor.node.INodeProcessor;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ProcessorTemplateHandler extends AbstractTemplateHandler {


    private static final Logger logger = LoggerFactory.getLogger(ProcessorTemplateHandler.class);


    private final ElementTagActionHandler actionHandler;

    private ITemplateProcessingContext templateProcessingContext;
    private ITemplateEngineConfiguration configuration;

    private ILocalVariableAwareVariablesMap variablesMap;

    private int markupLevel = 0;

    private int skipMarkupFromLevel = Integer.MAX_VALUE;
    private LevelArray skipCloseTagLevels = new LevelArray(5);

    private final ProcessorIterator processorIterator = new ProcessorIterator();

    // This should only be modified by means of the 'increaseHandlerExecLevel' and 'decreaseHandlerExecLevel' methods
    private int handlerExecLevel = -1;

    // These structures will be indexed by the handlerExecLevel, which allows structures to be used across different levels of nesting
    private TemplateHandlerEventQueue[] eventQueues = null;
    private Text[] textBuffers = null;
    private OpenElementTag[] openElementTagBuffers = null;
    private CloseElementTag[] closeElementTagBuffers = null;
    private IterationSpec[] iterationSpecs = null;



    /**
     * <p>
     *   Creates a new instance of this handler.
     * </p>
     *
     */
    public ProcessorTemplateHandler() {
        super();
        this.actionHandler = new ElementTagActionHandler();
    }




    @Override
    public void setTemplateProcessingContext(final ITemplateProcessingContext templateProcessingContext) {

        super.setTemplateProcessingContext(templateProcessingContext);

        this.templateProcessingContext = templateProcessingContext;
        Validate.notNull(this.templateProcessingContext, "Template Processing Context cannot be null");
        Validate.notNull(this.templateProcessingContext.getTemplateMode(), "Template Mode returned by Template Processing Context cannot be null");

        this.configuration = templateProcessingContext.getConfiguration();
        Validate.notNull(this.configuration, "Template Engine Configuration returned by Template Processing Context cannot be null");
        Validate.notNull(this.configuration.getTextRepository(), "Text Repository returned by Template Engine Configuration cannot be null");
        Validate.notNull(this.configuration.getElementDefinitions(), "Element Definitions returned by Template Engine Configuration cannot be null");
        Validate.notNull(this.configuration.getAttributeDefinitions(), "Attribute Definitions returned by Template Engine Configuration cannot be null");

        final IVariablesMap variablesMap = templateProcessingContext.getVariablesMap();
        Validate.notNull(variablesMap, "Variables Map returned by Template Processing Context cannot be null");
        if (variablesMap instanceof ILocalVariableAwareVariablesMap) {
            this.variablesMap = (ILocalVariableAwareVariablesMap) variablesMap;
        } else {
            logger.warn("Unknown implementation of the " + IVariablesMap.class.getName() + " interface: " +
                        variablesMap.getClass().getName() + ". Local variable support will be DISABLED.");
        }

    }




    private void increaseHandlerExecLevel() {

        this.handlerExecLevel++;

        if (this.eventQueues == null) {
            // No arrays created yet - must create

            this.eventQueues = new TemplateHandlerEventQueue[3];
            Arrays.fill(this.eventQueues, null);
            this.textBuffers = new Text[3];
            Arrays.fill(this.textBuffers, null);
            this.openElementTagBuffers = new OpenElementTag[3];
            Arrays.fill(this.openElementTagBuffers, null);
            this.closeElementTagBuffers = new CloseElementTag[3];
            Arrays.fill(this.closeElementTagBuffers, null);
            this.iterationSpecs = new IterationSpec[3];
            Arrays.fill(this.iterationSpecs, null);

        }

        if (this.eventQueues.length == this.handlerExecLevel) {
            // We need to grow the arrays

            final TemplateHandlerEventQueue[] newEventQueues = new TemplateHandlerEventQueue[this.handlerExecLevel + 3];
            Arrays.fill(newEventQueues, null);
            System.arraycopy(this.eventQueues, 0, newEventQueues, 0, this.handlerExecLevel);
            this.eventQueues = newEventQueues;

            final Text[] newTextBuffers = new Text[this.handlerExecLevel + 3];
            Arrays.fill(newTextBuffers, null);
            System.arraycopy(this.textBuffers, 0, newTextBuffers, 0, this.handlerExecLevel);
            this.textBuffers = newTextBuffers;

            final OpenElementTag[] newOpenElementTagBuffers = new OpenElementTag[this.handlerExecLevel + 3];
            Arrays.fill(newOpenElementTagBuffers, null);
            System.arraycopy(this.openElementTagBuffers, 0, newOpenElementTagBuffers, 0, this.handlerExecLevel);
            this.openElementTagBuffers = newOpenElementTagBuffers;

            final CloseElementTag[] newCloseElementTagBuffers = new CloseElementTag[this.handlerExecLevel + 3];
            Arrays.fill(newCloseElementTagBuffers, null);
            System.arraycopy(this.closeElementTagBuffers, 0, newCloseElementTagBuffers, 0, this.handlerExecLevel);
            this.closeElementTagBuffers = newCloseElementTagBuffers;

            final IterationSpec[] newIterationSpecs = new IterationSpec[this.handlerExecLevel + 3];
            Arrays.fill(newIterationSpecs, null);
            System.arraycopy(this.iterationSpecs, 0, newIterationSpecs, 0, this.handlerExecLevel);
            this.iterationSpecs = newIterationSpecs;

        }

        if (this.eventQueues[this.handlerExecLevel] == null) {
            this.eventQueues[this.handlerExecLevel] =
                    new TemplateHandlerEventQueue(
                            10, this.configuration.getTextRepository(),
                            this.configuration.getElementDefinitions(), this.configuration.getAttributeDefinitions(),
                            this.templateProcessingContext.getTemplateMode());
        } else {
            this.eventQueues[this.handlerExecLevel].reset();
        }

        if (this.textBuffers[this.handlerExecLevel] == null) {
            // Note we are not using the model factory because we need this exact implementation of the structure interface
            this.textBuffers[this.handlerExecLevel] = new Text(this.configuration.getTextRepository());
        }

        if (this.openElementTagBuffers[this.handlerExecLevel] == null) {
            // Note we are not using the model factory because we need this exact implementation of the structure interface
            this.openElementTagBuffers[this.handlerExecLevel] =
                    new OpenElementTag(
                            this.templateProcessingContext.getTemplateMode(),
                            this.configuration.getElementDefinitions(), this.configuration.getAttributeDefinitions());
        }

        if (this.closeElementTagBuffers[this.handlerExecLevel] == null) {
            // Note we are not using the model factory because we need this exact implementation of the structure interface
            this.closeElementTagBuffers[this.handlerExecLevel] =
                    new CloseElementTag(
                            this.templateProcessingContext.getTemplateMode(), this.configuration.getElementDefinitions());
        }

        if (this.iterationSpecs[this.handlerExecLevel] == null) {
            this.iterationSpecs[this.handlerExecLevel] = new IterationSpec();
        } else {
            this.iterationSpecs[this.handlerExecLevel].reset();
        }

    }


    private void decreaseHandlerExecLevel() {
        this.handlerExecLevel--;
    }




    @Override
    public void handleDocumentStart(final long startTimeNanos, final int line, final int col) {
        super.handleDocumentStart(startTimeNanos, line, col);
        increaseHandlerExecLevel();
    }




    @Override
    public void handleDocumentEnd(final long endTimeNanos, final long totalTimeNanos, final int line, final int col) {
        decreaseHandlerExecLevel();
        super.handleDocumentEnd(endTimeNanos, totalTimeNanos, line, col);
    }




    @Override
    public void handleText(final IText text) {

        // Check whether we just need to discard any markup in this level
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            return;
        }

        // Check whether we are in the middle of an iteration and we just need to cache this to the queue (for now)
        if (this.markupLevel >= this.iterationSpecs[this.handlerExecLevel].fromMarkupLevel) {
            this.eventQueues[this.handlerExecLevel].add(text.cloneNode());
            return;
        }

        // Includes calling the next handler in the chain
        super.handleText(text);

    }



    @Override
    public void handleComment(final IComment comment) {

        // Check whether we just need to discard any markup in this level
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            return;
        }

        // Check whether we are in the middle of an iteration and we just need to cache this to the queue (for now)
        if (this.markupLevel >= this.iterationSpecs[this.handlerExecLevel].fromMarkupLevel) {
            this.eventQueues[this.handlerExecLevel].add(comment.cloneNode());
            return;
        }

        // Includes calling the next handler in the chain
        super.handleComment(comment);

    }

    
    @Override
    public void handleCDATASection(final ICDATASection cdataSection) {

        // Check whether we just need to discard any markup in this level
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            return;
        }

        // Check whether we are in the middle of an iteration and we just need to cache this to the queue (for now)
        if (this.markupLevel >= this.iterationSpecs[this.handlerExecLevel].fromMarkupLevel) {
            this.eventQueues[this.handlerExecLevel].add(cdataSection.cloneNode());
            return;
        }

        // Includes calling the next handler in the chain
        super.handleCDATASection(cdataSection);

    }




    @Override
    public void handleStandaloneElement(final IStandaloneElementTag standaloneElementTag) {

        /*
         * CHECK WHETHER THIS MARKUP REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            return;
        }


        /*
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.markupLevel >= this.iterationSpecs[this.handlerExecLevel].fromMarkupLevel) {
            this.eventQueues[this.handlerExecLevel].add(standaloneElementTag.cloneElementTag());
            return;
        }

/**/
        standaloneElementTag.getAttributes().setAttribute("markupLevel", Integer.valueOf(this.markupLevel).toString());
        standaloneElementTag.getAttributes().setAttribute("execLevel", Integer.valueOf(this.handlerExecLevel).toString());
        standaloneElementTag.getAttributes().setAttribute("variablesMapLevel", Integer.valueOf(this.variablesMap.level()).toString());
/**/

        /*
         * CHECK WHETHER WE ACTUALLY HAVE ANYTHING TO PROCESS, quickly delegating to 'next' if not
         */
        if (!standaloneElementTag.hasAssociatedProcessors()) {
            super.handleStandaloneElement(standaloneElementTag);
            return;
        }


        /*
         * INITIALIZE THE PROCESSOR ITERATOR that will be used for executing all the processors
         */
        this.processorIterator.reset(standaloneElementTag);


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseHandlerExecLevel();
        final TemplateHandlerEventQueue queue = this.eventQueues[this.handlerExecLevel];
        boolean queueProcessable = false;


        /*
         * INCREASE THE VARIABLES MAP LEVEL so that all local variables created during the execution of processors
         * are available for the rest of the processors as well as the body of the tag
         */
        if (this.variablesMap != null) {
            this.variablesMap.increaseLevel();
        }


        /*
         * EXECUTE PROCESSORS
         */
        IProcessor processor;
        boolean tagRemoved = false; // Will allow us to determine when to stop iterating, and whether we need to delegate to 'next'
        boolean tagBodyAdded = false; // Will allow us to determine whether we have added a body to this tag, converting it into an open one
        while (!tagRemoved && (processor = this.processorIterator.next()) != null) {

            this.actionHandler.reset();

            if (processor instanceof IElementProcessor) {

                final IElementProcessor elementProcessor = ((IElementProcessor)processor);
                elementProcessor.process(getTemplateProcessingContext(), standaloneElementTag, this.actionHandler);

                if (this.actionHandler.setLocalVariable) {
                    if (this.variablesMap != null) {
                        this.variablesMap.putAll(this.actionHandler.addedLocalVariables);
                    }
                }

                if (this.actionHandler.removeLocalVariable) {
                    if (this.variablesMap != null) {
                        for (final String variableName : this.actionHandler.removedLocalVariableNames) {
                            this.variablesMap.remove(variableName);
                        }
                    }
                }

                if (this.actionHandler.setBodyText) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.actionHandler.setBodyTextProcessable;

                    this.openElementTagBuffers[this.handlerExecLevel].setFromStandaloneElementTag(standaloneElementTag);
                    this.closeElementTagBuffers[this.handlerExecLevel].setFromStandaloneElementTag(standaloneElementTag);

                    this.textBuffers[this.handlerExecLevel].setText(this.actionHandler.setBodyTextValue);

                    queue.add(this.textBuffers[this.handlerExecLevel]);

                    tagBodyAdded = true;

                } else if (this.actionHandler.setBodyQueue) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.actionHandler.setBodyQueueProcessable;

                    this.openElementTagBuffers[this.handlerExecLevel].setFromStandaloneElementTag(standaloneElementTag);
                    this.closeElementTagBuffers[this.handlerExecLevel].setFromStandaloneElementTag(standaloneElementTag);

                    queue.addAll(this.actionHandler.setBodyQueueValue.cloneQueue(true));

                    tagBodyAdded = true;

                } else if (this.actionHandler.replaceWithText) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.actionHandler.replaceWithTextProcessable;

                    this.textBuffers[this.handlerExecLevel].setText(this.actionHandler.setBodyTextValue);

                    queue.add(this.textBuffers[this.handlerExecLevel]);

                    tagRemoved = true;

                } else if (this.actionHandler.replaceWithQueue) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.actionHandler.replaceWithQueueProcessable;

                    queue.addAll(this.actionHandler.replaceWithQueueValue.cloneQueue(true));

                    tagRemoved = true;

                } else if (this.actionHandler.removeElement) {

                    queue.reset(); // Remove any previous results on the queue

                    tagRemoved = true;

                } else if (this.actionHandler.removeTag) {

                    // No modifications to the queue - it's just the tag that will be removed, not its possible contents

                    tagRemoved = true;

                }

            } else if (processor instanceof INodeProcessor) {
                throw new UnsupportedOperationException("Support for Node processors not implemented yet");
            } else {
                throw new IllegalStateException(
                        "An element has been found with an associated processor of type " + processor.getClass().getName() +
                                " which is neither an element nor a Node processor.");
            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN in the case we DID NOT reshape the tag to non-void
         */
        if (!tagRemoved && !tagBodyAdded) {
            super.handleStandaloneElement(standaloneElementTag);
        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN (for the open tag) in the case we DID reshape the tag to non-void
         */
        if (!tagRemoved && tagBodyAdded) {
            super.handleOpenElement(this.openElementTagBuffers[this.handlerExecLevel]);
            this.markupLevel++;
        }


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        queue.process(queueProcessable ? this : getNext(), true);


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN (for the close tag) in the case we DID reshape the tag to non-void
         */
        if (!tagRemoved && tagBodyAdded) {
            this.markupLevel--;
            super.handleCloseElement(this.closeElementTagBuffers[this.handlerExecLevel]);
        }


        /*
         * DECREASE THE VARIABLES MAP LEVEL once we have executed all the processors (and maybe a body if we added
         * one to the tag converting it into an open tag)
         */
        if (this.variablesMap != null) {
            this.variablesMap.decreaseLevel();
        }


        /*
         * DECREASE THE EXEC LEVEL, so that the structures can be reused
         */
        decreaseHandlerExecLevel();

    }




    @Override
    public void handleOpenElement(final IOpenElementTag openElementTag) {

        /*
         * CHECK WHETHER THIS MARKUP REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            this.markupLevel++;
            return;
        }


        /*
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.markupLevel >= this.iterationSpecs[this.handlerExecLevel].fromMarkupLevel) {
            this.eventQueues[this.handlerExecLevel].add(openElementTag.cloneElementTag());
            this.markupLevel++;
            return;
        }

/**/
        openElementTag.getAttributes().setAttribute("markupLevel", Integer.valueOf(this.markupLevel).toString());
        openElementTag.getAttributes().setAttribute("execLevel", Integer.valueOf(this.handlerExecLevel).toString());
        openElementTag.getAttributes().setAttribute("variablesMapLevel", Integer.valueOf(this.variablesMap.level()).toString());
/**/

        /*
         * CHECK WHETHER WE ACTUALLY HAVE ANYTHING TO PROCESS, quickly delegating to 'next' if not
         */
        if (!openElementTag.hasAssociatedProcessors()) {
            super.handleOpenElement(openElementTag);
            this.markupLevel++;
            if (this.variablesMap != null) {
                this.variablesMap.increaseLevel();
            }
            return;
        }


        /*
         * INITIALIZE THE PROCESSOR ITERATOR that will be used for executing all the processors
         */
        this.processorIterator.reset(openElementTag);


        /*
         * REGISTER A NEW EXEC LEVEL, and allow the corresponding structures to be created just in case they are needed
         */
        increaseHandlerExecLevel();
        final TemplateHandlerEventQueue queue = this.eventQueues[this.handlerExecLevel];
        boolean queueProcessable = false;


        /*
         * INCREASE THE VARIABLES MAP LEVEL so that all local variables created during the execution of processors
         * are available for the rest of the processors as well as the body of the tag
         */
        if (this.variablesMap != null) {
            this.variablesMap.increaseLevel();
        }

        /*
         * EXECUTE PROCESSORS
         */
        IProcessor processor;
        boolean tagRemoved = false; // Will allow us to determine when to stop iterating, and whether we need to delegate to 'next'
        boolean bodyRemoved = false; // Will allow us to determine whether we need to skip the original element's body or not
        while (!tagRemoved && (processor = this.processorIterator.next()) != null) {

            this.actionHandler.reset();

            if (processor instanceof IElementProcessor) {

                final IElementProcessor elementProcessor = ((IElementProcessor)processor);
                elementProcessor.process(getTemplateProcessingContext(), openElementTag, this.actionHandler);

                if (this.actionHandler.setLocalVariable) {
                    if (this.variablesMap != null) {
                        this.variablesMap.putAll(this.actionHandler.addedLocalVariables);
                    }
                }

                if (this.actionHandler.removeLocalVariable) {
                    if (this.variablesMap != null) {
                        for (final String variableName : this.actionHandler.removedLocalVariableNames) {
                            this.variablesMap.remove(variableName);
                        }
                    }
                }

                if (this.actionHandler.iterateElement) {

                    this.iterationSpecs[this.handlerExecLevel].fromMarkupLevel = this.markupLevel + 1;
                    this.iterationSpecs[this.handlerExecLevel].iterVariableName = this.actionHandler.iterVariableName;
                    this.iterationSpecs[this.handlerExecLevel].iterStatusVariableName = this.actionHandler.iterStatusVariableName;
                    this.iterationSpecs[this.handlerExecLevel].iterator = this.actionHandler.iterator;

                    queue.add(openElementTag.cloneElementTag());

                    this.markupLevel++;

                    // Nothing else to be done by this handler... let's just queue the rest of the events to be iterated
                    // Note we are not even decreasing the handlerExecLevel (we need the iterator information there!)
                    return;

                } else if (this.actionHandler.setBodyText) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.actionHandler.setBodyTextProcessable;

                    this.textBuffers[this.handlerExecLevel].setText(this.actionHandler.setBodyTextValue);

                    queue.add(this.textBuffers[this.handlerExecLevel]);

                    bodyRemoved = true;

                } else if (this.actionHandler.setBodyQueue) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.actionHandler.setBodyQueueProcessable;

                    queue.addAll(this.actionHandler.setBodyQueueValue.cloneQueue(true));

                    bodyRemoved = true;

                } else if (this.actionHandler.replaceWithText) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.actionHandler.replaceWithTextProcessable;

                    this.textBuffers[this.handlerExecLevel].setText(this.actionHandler.setBodyTextValue);

                    queue.add(this.textBuffers[this.handlerExecLevel]);

                    tagRemoved = true;
                    bodyRemoved = true;

                } else if (this.actionHandler.replaceWithQueue) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.actionHandler.replaceWithQueueProcessable;

                    queue.addAll(this.actionHandler.replaceWithQueueValue.cloneQueue(true));

                    tagRemoved = true;
                    bodyRemoved = true;

                } else if (this.actionHandler.removeElement) {

                    queue.reset(); // Remove any previous results on the queue

                    tagRemoved = true;
                    bodyRemoved = true;

                } else if (this.actionHandler.removeTag) {

                    // No modifications to the queue - it's just the tag that will be removed, not its possible contents

                    tagRemoved = true;

                }

            } else if (processor instanceof INodeProcessor) {
                throw new UnsupportedOperationException("Support for Node processors not implemented yet");
            } else {
                throw new IllegalStateException(
                        "An element has been found with an associated processor of type " + processor.getClass().getName() +
                        " which is neither an element nor a Node processor.");
            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN and INCREASE THE MARKUP LEVEL RIGHT AFTERWARDS
         */
        if (!tagRemoved) {
            super.handleOpenElement(openElementTag);
        }


        /*
         * INCREASE THE MARKUP LEVEL to the value that will be applied to the tag's bodies
         */
        this.markupLevel++;


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        queue.process(queueProcessable ? this : getNext(), true);


        /*
         * SET BODY TO BE SKIPPED, if required
         */
        if (bodyRemoved) {
            // We make sure no other nested events will be processed at all
            this.skipMarkupFromLevel = this.markupLevel;
        }


        /*
         * MAKE SURE WE SKIP THE CORRESPONDING CLOSE TAG, if required
         */
        if (tagRemoved) {
            this.skipCloseTagLevels.add(this.markupLevel - 1);
            // We cannot decrease here the variables map level because we aren't actually decreasing the markup
            // level until we find the corresponding close tag
        }


        /*
         * DECREASE THE EXEC LEVEL, so that the structures can be reused
         */
        decreaseHandlerExecLevel();

    }




    @Override
    public void handleAutoOpenElement(final IAutoOpenElementTag autoOpenElementTag) {

        // TODO Once engine code is completed for standalone + open, copy open here

        // Check whether we just need to discard any markup in this level
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            this.markupLevel++;
            if (this.variablesMap != null) {
                this.variablesMap.increaseLevel();
            }
            return;
        }

        // Includes calling the next handler in the chain
        super.handleAutoOpenElement(autoOpenElementTag);

        // Note we increase the markup level after processing the rest of the chain for this element
        this.markupLevel++;
        if (this.variablesMap != null) {
            this.variablesMap.increaseLevel();
        }

    }




    @Override
    public void handleCloseElement(final ICloseElementTag closeElementTag) {

        /*
         * DECREASE THE MARKUP LEVEL, as only the body of elements should be considered in a higher level
         */
        this.markupLevel--;

        /*
         * CHECK WHETHER THIS MARKUP REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            return;
        }

        /*
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.markupLevel >= this.iterationSpecs[this.handlerExecLevel].fromMarkupLevel) {
            this.eventQueues[this.handlerExecLevel].add(closeElementTag.cloneElementTag());
            return;
        }

        /*
         * CHECK WHETHER WE ARE JUST CLOSING AN ITERATION, and in such case, process it
         */
        if (this.markupLevel + 1 == this.iterationSpecs[this.handlerExecLevel].fromMarkupLevel) {

            // Add the last tag: the closing one
            this.eventQueues[this.handlerExecLevel].add(closeElementTag);

            // Process the queue by iterating it
            processIteration();

            // Decrease the variables map level
            if (this.variablesMap != null) {
                this.variablesMap.decreaseLevel();
            }

            // Decrease the handler exec level (which we hadn't done when we opened the iteration)
            decreaseHandlerExecLevel();

            return;

        }

        /*
         * DECREASE THE VARIABLES MAP LEVEL, once we know this tag was not part of a block of discarded markup
         */
        if (this.variablesMap != null) {
            this.variablesMap.decreaseLevel();
        }

        /*
         * CHECK WHETHER WE SHOULD KEEP SKIPPING MARKUP or we just got to the end of the discarded block
         */
        if (this.markupLevel + 1 == this.skipMarkupFromLevel) {
            // We've reached the last point where markup should be discarded, so we should reset the variable
            this.skipMarkupFromLevel = Integer.MAX_VALUE;
        }

        /*
         * CHECK WHETHER THIS CLOSE TAG ITSELF MUST BE DISCARDED because we also discarded the open one (even if not necessarily the body)
         */
        if (this.skipCloseTagLevels.matchAndPop(this.markupLevel)) {
            return;
        }

        /*
         * CALL THE NEXT HANDLER in the chain
         */
        super.handleCloseElement(closeElementTag);

    }




    @Override
    public void handleAutoCloseElement(final IAutoCloseElementTag autoCloseElementTag) {

        /*
         * DECREASE THE MARKUP LEVEL, as only the body of elements should be considered in a higher level
         */
        this.markupLevel--;

        /*
         * CHECK WHETHER THIS MARKUP REGION SHOULD BE DISCARDED, for example, as a part of a skipped body
         */
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            return;
        }

        /*
         * CHECK WHETHER WE ARE IN THE MIDDLE OF AN ITERATION and we just need to cache this to the queue (for now)
         */
        if (this.markupLevel >= this.iterationSpecs[this.handlerExecLevel].fromMarkupLevel) {
            this.eventQueues[this.handlerExecLevel].add(autoCloseElementTag.cloneElementTag());
            return;
        }

        /*
         * CHECK WHETHER WE ARE JUST CLOSING AN ITERATION, and in such case, process it
         */
        if (this.markupLevel + 1 == this.iterationSpecs[this.handlerExecLevel].fromMarkupLevel) {

            // Add the last tag: the closing one
            this.eventQueues[this.handlerExecLevel].add(autoCloseElementTag);

            // Process the queue by iterating it
            processIteration();

            // Decrease the variables map level
            if (this.variablesMap != null) {
                this.variablesMap.decreaseLevel();
            }

            // Decrease the handler exec level (which we hadn't done when we opened the iteration)
            decreaseHandlerExecLevel();

            return;

        }

        /*
         * DECREASE THE VARIABLES MAP LEVEL, once we know this tag was not part of a block of discarded markup
         */
        if (this.variablesMap != null) {
            this.variablesMap.decreaseLevel();
        }

        /*
         * CHECK WHETHER WE SHOULD KEEP SKIPPING MARKUP or we just got to the end of the discarded block
         */
        if (this.markupLevel + 1 == this.skipMarkupFromLevel) {
            // We've reached the last point where markup should be discarded, so we should reset the variable
            this.skipMarkupFromLevel = Integer.MAX_VALUE;
        }

        /*
         * CHECK WHETHER THIS CLOSE TAG ITSELF MUST BE DISCARDED because we also discarded the open one (even if not necessarily the body)
         */
        if (this.skipCloseTagLevels.matchAndPop(this.markupLevel)) {
            return;
        }

        /*
         * CALL THE NEXT HANDLER in the chain
         */
        super.handleAutoCloseElement(autoCloseElementTag);

    }




    @Override
    public void handleUnmatchedCloseElement(final IUnmatchedCloseElementTag unmatchedCloseElementTag) {

        // Check whether we just need to discard any markup in this level
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            return;
        }

        // Check whether we are in the middle of an iteration and we just need to cache this to the queue (for now)
        if (this.markupLevel >= this.iterationSpecs[this.handlerExecLevel].fromMarkupLevel) {
            this.eventQueues[this.handlerExecLevel].add(unmatchedCloseElementTag.cloneElementTag());
            return;
        }

        // Unmatched closes do not affect the markup level


        // Includes calling the next handler in the chain
        super.handleUnmatchedCloseElement(unmatchedCloseElementTag);

    }




    @Override
    public void handleDocType(final IDocType docType) {

        // Check whether we just need to discard any markup in this level
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            return;
        }

        // Check whether we are in the middle of an iteration and we just need to cache this to the queue (for now)
        if (this.markupLevel >= this.iterationSpecs[this.handlerExecLevel].fromMarkupLevel) {
            this.eventQueues[this.handlerExecLevel].add(docType.cloneNode());
            return;
        }

        // Includes calling the next handler in the chain
        super.handleDocType(docType);

    }

    
    
    
    @Override
    public void handleXmlDeclaration(final IXMLDeclaration xmlDeclaration) {

        // Check whether we just need to discard any markup in this level
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            return;
        }

        // Check whether we are in the middle of an iteration and we just need to cache this to the queue (for now)
        if (this.markupLevel >= this.iterationSpecs[this.handlerExecLevel].fromMarkupLevel) {
            this.eventQueues[this.handlerExecLevel].add(xmlDeclaration.cloneNode());
            return;
        }

        // Includes calling the next handler in the chain
        super.handleXmlDeclaration(xmlDeclaration);

    }






    @Override
    public void handleProcessingInstruction(final IProcessingInstruction processingInstruction) {

        // Check whether we just need to discard any markup in this level
        if (this.markupLevel >= this.skipMarkupFromLevel) {
            return;
        }

        // Check whether we are in the middle of an iteration and we just need to cache this to the queue (for now)
        if (this.markupLevel >= this.iterationSpecs[this.handlerExecLevel].fromMarkupLevel) {
            this.eventQueues[this.handlerExecLevel].add(processingInstruction.cloneNode());
            return;
        }

        // Includes calling the next handler in the chain
        super.handleProcessingInstruction(processingInstruction);

    }






    private void processIteration() {

        if (this.variablesMap == null) {
            throw new TemplateProcessingException(
                    "Iteration is not supported because local variable support is DISABLED. This is due to " +
                    "the use of an unknown implementation of the " + IVariablesMap.class.getName() + " interface. " +
                    "Use " + StandardTemplateProcessingContextFactory.class.getName() + " in order to avoid this.");
        }

        final String iterVariableName = this.iterationSpecs[this.handlerExecLevel].iterVariableName;
        final String iterStatusVariableName = this.iterationSpecs[this.handlerExecLevel].iterStatusVariableName;
        final Iterator<?> iterator = this.iterationSpecs[this.handlerExecLevel].iterator;

        // We need to reset it or we'll obtain an infinite loop!
        this.iterationSpecs[this.handlerExecLevel].reset();

        if (iterator == null) {
            return;
        }

        while (iterator.hasNext()) {

            this.variablesMap.increaseLevel();

            this.variablesMap.put(iterVariableName, iterator.next());
            this.variablesMap.put(iterStatusVariableName, null); // TODO Add iterationstatus object!

            this.eventQueues[this.handlerExecLevel].process(this, false);

            this.variablesMap.decreaseLevel();

        }

        this.eventQueues[this.handlerExecLevel].reset();

    }






    private static class LevelArray {

        private int[] array;
        private int size;


        LevelArray(final int initialLen) {
            super();
            this.array = new int[initialLen];
            this.size = 0;
        }


        void add(final int level) {

            if (this.array.length == this.size) {
                // We need to grow the array!
                final int[] newArray = new int[this.array.length + 5];
                System.arraycopy(this.array,0,newArray,0,this.size);
                this.array = newArray;
            }

            this.array[this.size++] = level;

        }

        boolean matchOrHigher(final int level) {
            if (this.size > 0 && this.array[this.size - 1] <= level) {
                return true;
            }
            return false;
        }

        boolean matchAndPop(final int level) {
            if (this.size > 0 && this.array[this.size - 1] == level) {
                this.size--;
                return true;
            }
            return false;
        }

    }



    private static class IterationSpec {

        private int fromMarkupLevel;
        private String iterVariableName;
        private String iterStatusVariableName;
        private Iterator<?> iterator;

        IterationSpec() {
            super();
            reset();
        }

        void reset() {
            this.fromMarkupLevel = Integer.MAX_VALUE;
            this.iterVariableName = null;
            this.iterStatusVariableName = null;
            this.iterator = null;
        }

    }
    
}