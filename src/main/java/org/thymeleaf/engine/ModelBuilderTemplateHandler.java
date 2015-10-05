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
package org.thymeleaf.engine;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.ITemplateEnd;
import org.thymeleaf.model.ITemplateStart;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;


/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ModelBuilderTemplateHandler extends AbstractTemplateHandler {

    private final Model model;
    private final EngineEventQueue modelQueue;
    private final IEngineConfiguration configuration;
    private final TemplateMode templateMode;



    public ModelBuilderTemplateHandler(final Model model) {
        super();
        Validate.notNull(model, "Model cannot be null");
        Validate.notNull(model.getConfiguration(), "Engine Configuration returned by Model cannot be null");
        Validate.notNull(model.getTemplateMode(), "Template Mode returned by Model cannot be null");
        this.model = model;
        this.modelQueue = this.model.getEventQueue();
        this.configuration = model.getConfiguration();
        this.templateMode = model.getTemplateMode();
    }



    public Model getModel() {
        return this.model;
    }


    // Note we are NOT implementing the setProcessingContext method, because we don't need it at all when just using
    // this handler for parsing (we are not processing anything!)



    @Override
    public void handleTemplateStart(final ITemplateStart templateStart) {
        this.modelQueue.build(TemplateStart.asEngineTemplateStart(templateStart, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleTemplateStart(templateStart);
    }


    @Override
    public void handleTemplateEnd(final ITemplateEnd templateEnd) {
        this.modelQueue.build(TemplateEnd.asEngineTemplateEnd(templateEnd, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleTemplateEnd(templateEnd);
    }





    @Override
    public void handleText(final IText text) {
        this.modelQueue.build(Text.asEngineText(this.configuration, text, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleText(text);
    }



    @Override
    public void handleComment(final IComment comment) {
        this.modelQueue.build(Comment.asEngineComment(this.configuration, comment, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleComment(comment);
    }


    @Override
    public void handleCDATASection(final ICDATASection cdataSection) {
        this.modelQueue.build(CDATASection.asEngineCDATASection(this.configuration, cdataSection, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleCDATASection(cdataSection);
    }




    @Override
    public void handleStandaloneElement(final IStandaloneElementTag standaloneElementTag) {
        this.modelQueue.build(StandaloneElementTag.asEngineStandaloneElementTag(this.templateMode, this.configuration, standaloneElementTag, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleStandaloneElement(standaloneElementTag);
    }


    @Override
    public void handleOpenElement(final IOpenElementTag openElementTag) {
        this.modelQueue.build(OpenElementTag.asEngineOpenElementTag(this.templateMode, this.configuration, openElementTag, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleOpenElement(openElementTag);
    }


    @Override
    public void handleCloseElement(final ICloseElementTag closeElementTag) {
        this.modelQueue.build(CloseElementTag.asEngineCloseElementTag(this.templateMode, this.configuration, closeElementTag, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleCloseElement(closeElementTag);
    }




    @Override
    public void handleDocType(final IDocType docType) {
        this.modelQueue.build(DocType.asEngineDocType(this.configuration, docType, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleDocType(docType);
    }




    @Override
    public void handleXMLDeclaration(final IXMLDeclaration xmlDeclaration) {
        this.modelQueue.build(XMLDeclaration.asEngineXMLDeclaration(this.configuration, xmlDeclaration, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleXMLDeclaration(xmlDeclaration);
    }




    @Override
    public void handleProcessingInstruction(final IProcessingInstruction processingInstruction) {
        this.modelQueue.build(ProcessingInstruction.asEngineProcessingInstruction(this.configuration, processingInstruction, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleProcessingInstruction(processingInstruction);
    }


    
}