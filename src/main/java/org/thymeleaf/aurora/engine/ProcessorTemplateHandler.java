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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

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
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ProcessorTemplateHandler extends AbstractTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProcessorTemplateHandler.class);

    private static final String DEFAULT_STATUS_VAR_SUFFIX = "Stat";

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
    private IterationSpec[] iterationSpecs = null;

    // Flags used during the processing of tags
    private boolean tagRemoved = false;
    private boolean bodyRemoved = false;
    private boolean queueProcessable = false;

    // Flag used for suspending the execution of a tag and replacing it for a different event (perhaps after building a queue)
    private boolean suspended = false;



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
         * INITIALIZE THE EXECUTION LEVEL depending on whether we have a suspended a previous execution or not
         */
        final TemplateHandlerEventQueue queue;
        if (!this.suspended) {

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
            queue = this.eventQueues[this.handlerExecLevel];

            /*
             * INCREASE THE VARIABLES MAP LEVEL so that all local variables created during the execution of processors
             * are available for the rest of the processors as well as the body of the tag
             */
            if (this.variablesMap != null) {
                this.variablesMap.increaseLevel();
            }

        } else {
            // Execution of a tag was suspended, we need to recover the data

            /*
             * RETRIEVE THE QUEUE TO BE USED, potentially already containing some nodes
             * Note also that we should not reset the processorIterator in this case, as we would lose information
             * about the processors already executed
             */
            queue = this.eventQueues[this.handlerExecLevel];
            this.suspended = false;

        }


        /*
         * EXECUTE PROCESSORS
         */
        IProcessor processor;
        while (!this.tagRemoved && (processor = this.processorIterator.next()) != null) {

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

                if (this.actionHandler.iterateElement) {

                    this.iterationSpecs[this.handlerExecLevel].fromMarkupLevel = this.markupLevel + 1;
                    this.iterationSpecs[this.handlerExecLevel].iterVariableName = this.actionHandler.iterVariableName;
                    this.iterationSpecs[this.handlerExecLevel].iterStatusVariableName = this.actionHandler.iterStatusVariableName;
                    this.iterationSpecs[this.handlerExecLevel].iteratedObject = this.actionHandler.iteratedObject;

                    // This this standalone tag to the queue
                    queue.add(standaloneElementTag.cloneElementTag());

                    // Suspend the execution, so that each of the iterations starts exactly where this left
                    this.suspended = true;

                    // Process the queue by iterating it
                    processIteration();

                    // Decrease the variables map level
                    if (this.variablesMap != null) {
                        this.variablesMap.decreaseLevel();
                    }

                    // Decrease the handler exec level (which we hadn't done when we opened the iteration)
                    decreaseHandlerExecLevel();

                    // Nothing else to be done by this handler... let's just queue the rest of the events to be iterated
                    // Note we are not even decreasing the handlerExecLevel (we need the iterator information there!)
                    return;

                } else if (this.actionHandler.setBodyText) {

                    queue.reset(); // Remove any previous results on the queue
                    this.queueProcessable = this.actionHandler.setBodyTextProcessable;

                    // Prepare the now-equivalent open and close tags
                    final OpenElementTag openTag =
                            new OpenElementTag(
                                    this.templateProcessingContext.getTemplateMode(),
                                    this.configuration.getElementDefinitions(), this.configuration.getAttributeDefinitions());
                    final CloseElementTag closeTag =
                            new CloseElementTag(
                                    this.templateProcessingContext.getTemplateMode(), this.configuration.getElementDefinitions());
                    openTag.setFromStandaloneElementTag(standaloneElementTag);
                    closeTag.setFromStandaloneElementTag(standaloneElementTag);

                    // Prepare the text node that will be added to the queue (that we will suspend)
                    this.textBuffers[this.handlerExecLevel].setText(this.actionHandler.setBodyTextValue);
                    queue.add(this.textBuffers[this.handlerExecLevel]);

                    // Suspend the queue - execution will be restarted by the handleOpenElement event
                    this.suspended = true;

                    // Fire the now-equivalent events. Note the handleOpenElement event will take care of the suspended queue
                    handleOpenElement(openTag);
                    handleCloseElement(closeTag);

                    return;

                } else if (this.actionHandler.setBodyQueue) {

                    queue.reset(); // Remove any previous results on the queue
                    this.queueProcessable = this.actionHandler.setBodyTextProcessable;

                    // Prepare the now-equivalent open and close tags
                    final OpenElementTag openTag =
                            new OpenElementTag(
                                    this.templateProcessingContext.getTemplateMode(),
                                    this.configuration.getElementDefinitions(), this.configuration.getAttributeDefinitions());
                    final CloseElementTag closeTag =
                            new CloseElementTag(
                                    this.templateProcessingContext.getTemplateMode(), this.configuration.getElementDefinitions());
                    openTag.setFromStandaloneElementTag(standaloneElementTag);
                    closeTag.setFromStandaloneElementTag(standaloneElementTag);

                    // Prepare the queue (that we will suspend)
                    queue.addAll(this.actionHandler.setBodyQueueValue.cloneQueue(true));

                    // Suspend the queue - execution will be restarted by the handleOpenElement event
                    this.suspended = true;

                    // Fire the now-equivalent events. Note the handleOpenElement event will take care of the suspended queue
                    handleOpenElement(openTag);
                    handleCloseElement(closeTag);

                    return;

                } else if (this.actionHandler.replaceWithText) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.actionHandler.replaceWithTextProcessable;

                    this.textBuffers[this.handlerExecLevel].setText(this.actionHandler.setBodyTextValue);

                    queue.add(this.textBuffers[this.handlerExecLevel]);

                    this.tagRemoved = true;

                } else if (this.actionHandler.replaceWithQueue) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.actionHandler.replaceWithQueueProcessable;

                    queue.addAll(this.actionHandler.replaceWithQueueValue.cloneQueue(true));

                    this.tagRemoved = true;

                } else if (this.actionHandler.removeElement) {

                    queue.reset(); // Remove any previous results on the queue

                    this.tagRemoved = true;

                } else if (this.actionHandler.removeTag) {

                    // No modifications to the queue - it's just the tag that will be removed, not its possible contents

                    this.tagRemoved = true;

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
        if (!this.tagRemoved) {
            super.handleStandaloneElement(standaloneElementTag);
        }
        this.tagRemoved = false;


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        queue.process(this.queueProcessable ? this : getNext(), true);
        this.queueProcessable = false;


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
         * INITIALIZE THE EXECUTION LEVEL depending on whether we have a suspended a previous execution or not
         */
        final TemplateHandlerEventQueue queue;
        if (!this.suspended) {

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
            queue = this.eventQueues[this.handlerExecLevel];

            /*
             * INCREASE THE VARIABLES MAP LEVEL so that all local variables created during the execution of processors
             * are available for the rest of the processors as well as the body of the tag
             */
            if (this.variablesMap != null) {
                this.variablesMap.increaseLevel();
            }

        } else {
            // Execution of a tag was suspended, we need to recover the data

            /*
             * RETRIEVE THE QUEUE TO BE USED, potentially already containing some nodes
             * Note also that we should not reset the processorIterator in this case, as we would lose information
             * about the processors already executed
             */
            queue = this.eventQueues[this.handlerExecLevel];
            this.suspended = false;

        }


        /*
         * EXECUTE PROCESSORS
         */
        IProcessor processor;
        while (!this.tagRemoved && (processor = this.processorIterator.next()) != null) {

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
                    this.iterationSpecs[this.handlerExecLevel].iteratedObject = this.actionHandler.iteratedObject;

                    queue.insert(0, openElementTag.cloneElementTag()); // We put the open tag at the beginning of whatever is already on the queue

                    // Suspend the execution, so that each of the iterations starts exactly where this left
                    this.suspended = true;

                    // Increase markup level, as normal with open tags
                    this.markupLevel++;

                    // Nothing else to be done by this handler... let's just queue the rest of the events to be iterated
                    // Note we are not even decreasing the handlerExecLevel (we need the iterator information there!)
                    return;

                } else if (this.actionHandler.setBodyText) {

                    queue.reset(); // Remove any previous results on the queue
                    this.queueProcessable = this.actionHandler.setBodyTextProcessable;

                    this.textBuffers[this.handlerExecLevel].setText(this.actionHandler.setBodyTextValue);

                    queue.add(this.textBuffers[this.handlerExecLevel]);

                    this.bodyRemoved = true;

                } else if (this.actionHandler.setBodyQueue) {

                    queue.reset(); // Remove any previous results on the queue
                    this.queueProcessable = this.actionHandler.setBodyQueueProcessable;

                    queue.addAll(this.actionHandler.setBodyQueueValue.cloneQueue(true));

                    this.bodyRemoved = true;

                } else if (this.actionHandler.replaceWithText) {

                    queue.reset(); // Remove any previous results on the queue
                    this.queueProcessable = this.actionHandler.replaceWithTextProcessable;

                    this.textBuffers[this.handlerExecLevel].setText(this.actionHandler.setBodyTextValue);

                    queue.add(this.textBuffers[this.handlerExecLevel]);

                    this.tagRemoved = true;
                    this.bodyRemoved = true;

                } else if (this.actionHandler.replaceWithQueue) {

                    queue.reset(); // Remove any previous results on the queue
                    this.queueProcessable = this.actionHandler.replaceWithQueueProcessable;

                    queue.addAll(this.actionHandler.replaceWithQueueValue.cloneQueue(true));

                    this.tagRemoved = true;
                    this.bodyRemoved = true;

                } else if (this.actionHandler.removeElement) {

                    queue.reset(); // Remove any previous results on the queue

                    this.tagRemoved = true;
                    this.bodyRemoved = true;

                } else if (this.actionHandler.removeTag) {

                    // No modifications to the queue - it's just the tag that will be removed, not its possible contents

                    this.tagRemoved = true;

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
        if (!this.tagRemoved) {
            super.handleOpenElement(openElementTag);
        }


        /*
         * INCREASE THE MARKUP LEVEL to the value that will be applied to the tag's bodies
         */
        this.markupLevel++;


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        queue.process(this.queueProcessable ? this : getNext(), true);


        /*
         * SET BODY TO BE SKIPPED, if required
         */
        if (this.bodyRemoved) {
            // We make sure no other nested events will be processed at all
            this.skipMarkupFromLevel = this.markupLevel;
        }
        this.bodyRemoved = false;


        /*
         * MAKE SURE WE SKIP THE CORRESPONDING CLOSE TAG, if required
         */
        if (this.tagRemoved) {
            this.skipCloseTagLevels.add(this.markupLevel - 1);
            // We cannot decrease here the variables map level because we aren't actually decreasing the markup
            // level until we find the corresponding close tag
        }
        this.tagRemoved = false;


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
        String iterStatusVariableName = this.iterationSpecs[this.handlerExecLevel].iterStatusVariableName;
        if (StringUtils.isEmptyOrWhitespace(iterStatusVariableName)) {
            // If no name has been specified for the status variable, we will use the same as the iter var + "Stat"
            iterStatusVariableName = iterVariableName + DEFAULT_STATUS_VAR_SUFFIX;
        }
        final Object iteratedObject = this.iterationSpecs[this.handlerExecLevel].iteratedObject;

        // We need to reset it or we might obtain an infinite loop!
        this.iterationSpecs[this.handlerExecLevel].reset();

        /*
         * Depending on the class of the iterated object, we will iterate it in one way or another. And also we
         * might have a "size" value for the stat variable or not.
         */
        final Iterator<?> iterator = computeIterator(iteratedObject);

        final IterationStatusVar status = new IterationStatusVar();
        status.index = 0;
        status.size = computeIteratedSize(iteratedObject);

        // TODO Correctly deal with the queue now we might have suspended execution (and therefore we won't increase execLevel

        final boolean originalSuspended = this.suspended;
        final boolean originalQueueProcessable = this.queueProcessable;
        final boolean originalBodyRemoved = this.bodyRemoved;
        final boolean originalTagRemoved = this.tagRemoved;

        while (iterator.hasNext()) {

            status.current = iterator.next();

            this.variablesMap.increaseLevel();

            this.variablesMap.put(iterVariableName, status.current);
            this.variablesMap.put(iterStatusVariableName, status);

            this.eventQueues[this.handlerExecLevel].process(this, false);

            this.variablesMap.decreaseLevel();

            status.index++;

        }

        this.eventQueues[this.handlerExecLevel].reset();

    }







    private static Integer computeIteratedSize(final Object iteratedObject) {
        if (iteratedObject == null) {
            return 0;
        }
        if (iteratedObject instanceof Collection<?>) {
            return ((Collection<?>)iteratedObject).size();
        }
        if (iteratedObject instanceof Map<?,?>) {
            return ((Map<?,?>)iteratedObject).size();
        }
        if (iteratedObject.getClass().isArray()) {
            return Integer.valueOf(Array.getLength(iteratedObject));
        }
        if (iteratedObject instanceof Iterable<?>) {
            return null; // Cannot determine before actually iterating
        }
        if (iteratedObject instanceof Iterator<?>) {
            return null; // Cannot determine before actually iterating
        }
        return 1; // In this case, we will iterate the object as a collection of size 1
    }


    private static Iterator<?> computeIterator(final Object iteratedObject) {
        if (iteratedObject == null) {
            return Collections.EMPTY_LIST.iterator();
        }
        if (iteratedObject instanceof Collection<?>) {
            return ((Collection<?>)iteratedObject).iterator();
        }
        if (iteratedObject instanceof Map<?,?>) {
            return ((Map<?,?>)iteratedObject).entrySet().iterator();
        }
        if (iteratedObject.getClass().isArray()) {
            return new Iterator<Object>() {

                protected final Object array = iteratedObject;
                protected final int length = Array.getLength(this.array);
                private int i = 0;

                public boolean hasNext() {
                    return this.i < this.length;
                }

                public Object next() {
                    return Array.get(this.array, i++);
                }

                public void remove() {
                    throw new UnsupportedOperationException("Cannot remove from an array iterator");
                }

            };
        }
        if (iteratedObject instanceof Iterable<?>) {
            return ((Iterable<?>)iteratedObject).iterator();
        }
        if (iteratedObject instanceof Iterator<?>) {
            return (Iterator<?>)iteratedObject;
        }
        return Collections.singletonList(iteratedObject).iterator();
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
        private Object iteratedObject;

        IterationSpec() {
            super();
            reset();
        }

        void reset() {
            this.fromMarkupLevel = Integer.MAX_VALUE;
            this.iterVariableName = null;
            this.iterStatusVariableName = null;
            this.iteratedObject = null;
        }

    }


}