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

import org.thymeleaf.aurora.context.ITemplateEngineContext;
import org.thymeleaf.aurora.context.ITemplateProcessingContext;
import org.thymeleaf.aurora.processor.IProcessor;
import org.thymeleaf.aurora.processor.element.IElementProcessor;
import org.thymeleaf.aurora.processor.node.INodeProcessor;
import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.aurora.text.ITextRepository;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ProcessorTemplateHandler extends AbstractTemplateHandler {


    private final ElementTagActionHandler actionHandler;

    private ITemplateEngineContext templateEngineContext;
    private ITextRepository textRepository;
    private IModelFactory modelFactory;
    private TemplateMode templateMode;
    private ElementDefinitions elementDefinitions;
    private AttributeDefinitions attributeDefinitions;

    private int markupLevel = 0;

    private int skipMarkupFromLevel = Integer.MAX_VALUE;
    private LevelArray skipCloseTagLevels = new LevelArray(5);

    private final ProcessorIterator processorIterator = new ProcessorIterator();

    // This should only be modified by means of the 'increaseHandlerExecLevel' and 'decreaseHandlerExecLevel' methods
    private int handlerExecLevel = -1;

    // These structures will be indexed by the handlerExecLevel
    private TemplateHandlerEventQueue[] eventQueues = null;
    private Text[] textBuffers = null;
    private OpenElementTag[] openElementTagBuffers = null;
    private CloseElementTag[] closeElementTagBuffers = null;



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

        this.templateEngineContext = templateProcessingContext.getTemplateEngineContext();
        Validate.notNull(this.templateEngineContext, "Template Engine Context returned by Template Processing Context cannot be null");

        this.modelFactory = templateProcessingContext.getModelFactory();
        Validate.notNull(this.modelFactory, "Model Factory returned by Template Processing Context cannot be null");

        this.textRepository = this.templateEngineContext.getTextRepository();
        Validate.notNull(this.textRepository, "Text Repository returned by Template Engine Context cannot be null");

        this.templateMode = templateProcessingContext.getTemplateMode();
        Validate.notNull(this.templateMode, "Template Mode returned by Template Processing Context cannot be null");

        this.elementDefinitions = this.templateEngineContext.getElementDefinitions();
        Validate.notNull(this.elementDefinitions, "Element Definitions returned by Template Engine Context cannot be null");

        this.attributeDefinitions = this.templateEngineContext.getAttributeDefinitions();
        Validate.notNull(this.attributeDefinitions, "Attribute Definitions returned by Template Engine Context cannot be null");

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

        }

        if (this.eventQueues[this.handlerExecLevel] == null) {
            this.eventQueues[this.handlerExecLevel] = new TemplateHandlerEventQueue();
        } else {
            this.eventQueues[this.handlerExecLevel].reset();
        }

        if (this.textBuffers[this.handlerExecLevel] == null) {
            // Note we are not using the model factory because we need this exact implementation of the structure interface
            this.textBuffers[this.handlerExecLevel] = new Text(this.textRepository);
        }

        if (this.openElementTagBuffers[this.handlerExecLevel] == null) {
            // Note we are not using the model factory because we need this exact implementation of the structure interface
            this.openElementTagBuffers[this.handlerExecLevel] = new OpenElementTag(this.templateMode, this.elementDefinitions, this.attributeDefinitions);
        }

        if (this.closeElementTagBuffers[this.handlerExecLevel] == null) {
            // Note we are not using the model factory because we need this exact implementation of the structure interface
            this.closeElementTagBuffers[this.handlerExecLevel] = new CloseElementTag(this.templateMode, this.elementDefinitions);
        }

    }


    private void decreaseHandlerExecLevel() {
        this.handlerExecLevel--;
    }




    @Override
    public void handleText(final IText text) {

        // Check whether we just need to discard any markup in this level
        if (this.markupLevel > this.skipMarkupFromLevel) {
            return;
        }

        // Includes calling the next handler in the chain
        super.handleText(text);

    }



    @Override
    public void handleComment(final IComment comment) {

        // Check whether we just need to discard any markup in this level
        if (this.markupLevel > this.skipMarkupFromLevel) {
            return;
        }

        // Includes calling the next handler in the chain
        super.handleComment(comment);

    }

    
    @Override
    public void handleCDATASection(final ICDATASection cdataSection) {

        // Check whether we just need to discard any markup in this level
        if (this.markupLevel > this.skipMarkupFromLevel) {
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
        if (this.markupLevel > this.skipMarkupFromLevel) {
            return;
        }


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

                    queue.addAll(this.actionHandler.setBodyQueueValue); // Just after the open tag, before the close tag

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

                    queue.addAll(this.actionHandler.replaceWithQueueValue);

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
        queue.process(queueProcessable? this : getNext());


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN (for the close tag) in the case we DID reshape the tag to non-void
         */
        if (!tagRemoved && tagBodyAdded) {
            this.markupLevel--;
            super.handleCloseElement(this.closeElementTagBuffers[this.handlerExecLevel]);
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
        if (this.markupLevel > this.skipMarkupFromLevel) {
            this.markupLevel++;
            return;
        }


        /*
         * CHECK WHETHER WE ACTUALLY HAVE ANYTHING TO PROCESS, quickly delegating to 'next' if not
         */
        if (!openElementTag.hasAssociatedProcessors()) {
            super.handleOpenElement(openElementTag);
            this.markupLevel++;
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

                if (this.actionHandler.setBodyText) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.actionHandler.setBodyTextProcessable;

                    this.textBuffers[this.handlerExecLevel].setText(this.actionHandler.setBodyTextValue);

                    queue.add(this.textBuffers[this.handlerExecLevel]);

                    bodyRemoved = true;

                } else if (this.actionHandler.setBodyQueue) {

                    queue.reset(); // Remove any previous results on the queue
                    queueProcessable = this.actionHandler.setBodyQueueProcessable;

                    queue.addAll(this.actionHandler.setBodyQueueValue); // Just after the open tag, before the close tag

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

                    queue.addAll(this.actionHandler.replaceWithQueueValue);

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
            this.markupLevel++;
        }


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        queue.process(queueProcessable? this : getNext());


        /*
         * DECREASE THE EXEC LEVEL, so that the structures can be reused
         */
        decreaseHandlerExecLevel();


        /*
         * SET BODY TO BE SKIPPED, if required
         */
        if (bodyRemoved) {
            // We make sure no other nested events will be processed at all
            this.skipMarkupFromLevel = this.markupLevel - 1;
        }


        /*
         * MAKE SURE WE SKIP THE CORRESPONDING CLOSE TAG, if required
         */
        if (tagRemoved) {
            this.skipCloseTagLevels.add(this.markupLevel - 1);
        }

    }




    @Override
    public void handleAutoOpenElement(final IOpenElementTag openElementTag) {

        // Check whether we just need to discard any markup in this level
        if (this.markupLevel > this.skipMarkupFromLevel) {
            this.markupLevel++;
            return;
        }

        // Includes calling the next handler in the chain
        super.handleAutoOpenElement(openElementTag);

        // Note we increase the markup level after processing the rest of the chain for this element
        this.markupLevel++;

    }




    @Override
    public void handleCloseElement(final ICloseElementTag closeElementTag) {

        this.markupLevel--;

        // Check whether we just need to discard any markup in this level
        if (this.markupLevel > this.skipMarkupFromLevel) {
            return;
        } else if (this.markupLevel == this.skipMarkupFromLevel) {
            // We've reached the last point where markup should be discarded, so we should reset the variable
            this.skipMarkupFromLevel = Integer.MAX_VALUE;
        }
        if (this.skipCloseTagLevels.matchAndPop(this.markupLevel)) {
            return;
        }

        // Includes calling the next handler in the chain
        super.handleCloseElement(closeElementTag);

    }




    @Override
    public void handleAutoCloseElement(final ICloseElementTag closeElementTag) {

        this.markupLevel--;

        // Check whether we just need to discard any markup in this level
        if (this.markupLevel > this.skipMarkupFromLevel) {
            return;
        } else if (this.markupLevel == this.skipMarkupFromLevel) {
            // We've reached the last point where markup should be discarded, so we should reset the variable
            this.skipMarkupFromLevel = Integer.MAX_VALUE;
        }
        if (this.skipCloseTagLevels.matchAndPop(this.markupLevel)) {
            return;
        }

        // Includes calling the next handler in the chain
        super.handleAutoCloseElement(closeElementTag);

    }




    @Override
    public void handleUnmatchedCloseElement(final ICloseElementTag closeElementTag) {

        // Check whether we just need to discard any markup in this level
        if (this.markupLevel > this.skipMarkupFromLevel) {
            return;
        }

        // Unmatched closes do not affect the markup level


        // Includes calling the next handler in the chain
        super.handleUnmatchedCloseElement(closeElementTag);

    }




    @Override
    public void handleDocType(final IDocType docType) {

        // Check whether we just need to discard any markup in this level
        if (this.markupLevel > this.skipMarkupFromLevel) {
            return;
        }

        // Includes calling the next handler in the chain
        super.handleDocType(docType);

    }

    
    
    
    @Override
    public void handleXmlDeclaration(final IXMLDeclaration xmlDeclaration) {

        // Check whether we just need to discard any markup in this level
        if (this.markupLevel > this.skipMarkupFromLevel) {
            return;
        }

        // Includes calling the next handler in the chain
        super.handleXmlDeclaration(xmlDeclaration);

    }






    @Override
    public void handleProcessingInstruction(final IProcessingInstruction processingInstruction) {

        // Check whether we just need to discard any markup in this level
        if (this.markupLevel > this.skipMarkupFromLevel) {
            return;
        }

        // Includes calling the next handler in the chain
        super.handleProcessingInstruction(processingInstruction);

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

        boolean matchAndPop(final int level) {
            if (this.size > 0 && this.array[this.size - 1] == level) {
                this.size--;
                return true;
            }
            return false;
        }

    }

    
}