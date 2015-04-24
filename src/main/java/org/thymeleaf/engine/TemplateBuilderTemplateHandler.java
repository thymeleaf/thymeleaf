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
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.model.IAutoCloseElementTag;
import org.thymeleaf.model.IAutoOpenElementTag;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IUnmatchedCloseElementTag;
import org.thymeleaf.model.IXMLDeclaration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;


/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class TemplateBuilderTemplateHandler extends AbstractTemplateHandler {


    private IEngineConfiguration configuration;
    private TemplateMode templateMode;
    private Template template;



    /**
     * <p>
     *   Creates a new instance of this handler.
     * </p>
     */
    public TemplateBuilderTemplateHandler() {
        super();
    }



    public Template getTemplate() {
        return this.template;
    }



    @Override
    public void setProcessingContext(final ITemplateProcessingContext processingContext) {
        Validate.notNull(processingContext, "Processing Context cannot be null");
        Validate.notNull(processingContext.getConfiguration(), "Engine Configuration returned by Processing Context cannot be null");
        Validate.notNull(processingContext.getTemplateMode(), "Template Mode returned by Processing Context cannot be null");
        super.setProcessingContext(processingContext);
        this.configuration = processingContext.getConfiguration();
        this.templateMode = processingContext.getTemplateMode();
        this.template = new Template(processingContext);
    }





    @Override
    public void handleText(final IText text) {
        this.template.add(Text.asEngineText(this.configuration, text, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleText(text);
    }



    @Override
    public void handleComment(final IComment comment) {
        this.template.add(Comment.asEngineComment(this.configuration, comment, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleComment(comment);
    }

    
    @Override
    public void handleCDATASection(final ICDATASection cdataSection) {
        this.template.add(CDATASection.asEngineCDATASection(this.configuration, cdataSection, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleCDATASection(cdataSection);
    }




    @Override
    public void handleStandaloneElement(final IStandaloneElementTag standaloneElementTag) {
        this.template.add(StandaloneElementTag.asEngineStandaloneElementTag(this.templateMode, this.configuration, standaloneElementTag, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleStandaloneElement(standaloneElementTag);
    }


    @Override
    public void handleOpenElement(final IOpenElementTag openElementTag) {
        this.template.add(OpenElementTag.asEngineOpenElementTag(this.templateMode, this.configuration, openElementTag, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleOpenElement(openElementTag);
    }


    @Override
    public void handleAutoOpenElement(final IAutoOpenElementTag autoOpenElementTag) {
        this.template.add(AutoOpenElementTag.asEngineAutoOpenElementTag(this.templateMode, this.configuration, autoOpenElementTag, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleAutoOpenElement(autoOpenElementTag);
    }


    @Override
    public void handleCloseElement(final ICloseElementTag closeElementTag) {
        this.template.add(CloseElementTag.asEngineCloseElementTag(this.templateMode, this.configuration, closeElementTag, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleCloseElement(closeElementTag);
    }


    @Override
    public void handleAutoCloseElement(final IAutoCloseElementTag autoCloseElementTag) {
        this.template.add(AutoCloseElementTag.asEngineAutoCloseElementTag(this.templateMode, this.configuration, autoCloseElementTag, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleAutoCloseElement(autoCloseElementTag);
    }


    @Override
    public void handleUnmatchedCloseElement(final IUnmatchedCloseElementTag unmatchedCloseElementTag) {
        this.template.add(UnmatchedCloseElementTag.asEngineUnmatchedCloseElementTag(this.templateMode, this.configuration, unmatchedCloseElementTag, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleUnmatchedCloseElement(unmatchedCloseElementTag);
    }




    @Override
    public void handleDocType(final IDocType docType) {
        this.template.add(DocType.asEngineDocType(this.configuration, docType, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleDocType(docType);
    }

    
    
    
    @Override
    public void handleXMLDeclaration(final IXMLDeclaration xmlDeclaration) {
        this.template.add(XMLDeclaration.asEngineXMLDeclaration(this.configuration, xmlDeclaration, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleXMLDeclaration(xmlDeclaration);
    }




    @Override
    public void handleProcessingInstruction(final IProcessingInstruction processingInstruction) {
        this.template.add(ProcessingInstruction.asEngineProcessingInstruction(this.configuration, processingInstruction, true));
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleProcessingInstruction(processingInstruction);
    }


    
}