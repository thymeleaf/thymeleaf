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

import org.thymeleaf.aurora.context.ITemplateEngineContext;
import org.thymeleaf.aurora.context.ITemplateProcessingContext;
import org.thymeleaf.aurora.processor.IProcessor;
import org.thymeleaf.aurora.processor.element.IElementProcessor;
import org.thymeleaf.aurora.processor.node.INodeProcessor;
import org.thymeleaf.aurora.text.ITextRepository;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ProcessorTemplateHandler extends AbstractTemplateHandler {

    private final TemplateHandlerEventQueue eventQueue;
    private final ElementTagActionHandler actionHandler;

    private ITemplateEngineContext templateEngineContext;
    private ITextRepository textRepository;
    private IModelFactory modelFactory;

    private int markupLevel = 0;

    private int skipMarkupFromLevel = Integer.MAX_VALUE;
    private LevelArray skipCloseTagLevels = new LevelArray(5);

    private final ProcessorIterator processorIterator = new ProcessorIterator();
    private Text bufferText = null;



    /**
     * <p>
     *   Creates a new instance of this handler.
     * </p>
     *
     */
    public ProcessorTemplateHandler() {
        super();
        this.eventQueue = new TemplateHandlerEventQueue();
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

        this.bufferText = new Text(this.textRepository);

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

        // Check whether we just need to discard any markup in this level
        if (this.markupLevel > this.skipMarkupFromLevel) {
            return;
        }

        standaloneElementTag.getAttributes().setAttribute("level", String.valueOf(this.markupLevel));

        // Includes calling the next handler in the chain
        super.handleStandaloneElement(standaloneElementTag);

    }


    @Override
    public void handleOpenElement(final IOpenElementTag openElementTag) {

        // Check whether we just need to discard any markup in this level
        if (this.markupLevel > this.skipMarkupFromLevel) {
            this.markupLevel++;
            return;
        }

        boolean skipTag = false;
        boolean skipBody = false;

        if (openElementTag.hasAssociatedProcessors()) {

            this.processorIterator.reset(openElementTag);

            IProcessor processor;
            while ((processor = this.processorIterator.next()) != null) {

                if (processor instanceof IElementProcessor) {

                    this.actionHandler.reset();

                    final IProcessableElementTag resultTag =
                            ((IElementProcessor)processor).process(getTemplateProcessingContext(), openElementTag, this.actionHandler);

                    if (this.actionHandler.setBodyText) {
                        this.bufferText.setText(this.actionHandler.setBodyTextValue);
                        this.eventQueue.add(this.bufferText);
                        skipBody = true;
                    } else if(this.actionHandler.setBodyQueue) {
                        this.eventQueue.addAll(this.actionHandler.setBodyQueueValue);
                        skipBody = true;
                    } else if (this.actionHandler.replaceWithText) {
                        this.bufferText.setText(this.actionHandler.replaceWithTextValue);
                        this.eventQueue.add(this.bufferText);
                        skipTag = true;
                        skipBody = true;
                    } else if (this.actionHandler.replaceWithQueue) {
                        this.eventQueue.addAll(this.actionHandler.replaceWithQueueValue);
                        skipTag = true;
                        skipBody = true;
                    }

                } else if (processor instanceof INodeProcessor) {
                    throw new UnsupportedOperationException("Support for Node processors not implemented yet");
                } else {
                    throw new IllegalStateException(
                            "An element has been found with an associated processor of type " + processor.getClass().getName() +
                            " which is neither an element nor a Node processor.");
                }

            }

        }


        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        if (!skipTag) {

            super.handleOpenElement(openElementTag);

            /*
             * INCREASE THE MARKUP LEVEL, after processing the rest of the handler chain for this element
             */
            this.markupLevel++;

        }


        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        this.eventQueue.process(getNext());

        /*
         * SET BODY TO BE SKIPPED, if required
         */
        if (skipBody) {
            // We make sure no other nested events will be processed at all
            this.skipMarkupFromLevel = this.markupLevel - 1;
        }
        if (skipTag) {
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