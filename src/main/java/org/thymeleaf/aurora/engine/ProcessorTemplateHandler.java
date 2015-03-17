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

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ProcessorTemplateHandler extends AbstractTemplateHandler {


    private final TemplateHandlerEventQueue eventQueue;

    private int markupLevel = 0;

    private int skipMarkupFromLevel = Integer.MAX_VALUE;


    final OpenElementTagActionHandler openElementTagActionHandler;


    /**
     * <p>
     *   Creates a new instance of this handler.
     * </p>
     *
     */
    public ProcessorTemplateHandler() {
        super();
        this.eventQueue = new TemplateHandlerEventQueue(this);
        this.openElementTagActionHandler = new OpenElementTagActionHandler();
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

        boolean replaceBody = false;

        this.openElementTagActionHandler.reset();

        if (openElementTag.getAttributes().hasAttribute("th:text")) {
            final IText text =
                    getTemplateProcessingContext().getModelFactory().createText("woohooo!");
            this.eventQueue.add(text);
            replaceBody = true;
        }

        /*
         * PROCESS THE REST OF THE HANDLER CHAIN
         */
        super.handleOpenElement(openElementTag);

        /*
         * INCREASE THE MARKUP LEVEL, after processing the rest of the handler chain for this element
         */
        this.markupLevel++;

        /*
         * PROCESS THE QUEUE, launching all the queued events
         */
        this.eventQueue.processQueue();

        /*
         * SET BODY TO BE SKIPPED, if required
         */
        if (replaceBody) {
            // We make sure no other nested events will be processed at all
            this.skipMarkupFromLevel = this.markupLevel - 1;
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


    
}