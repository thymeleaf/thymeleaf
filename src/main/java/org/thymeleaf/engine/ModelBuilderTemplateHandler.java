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

    private final boolean fragment;
    private final Model model;
    private final IEngineConfiguration configuration;
    private final TemplateMode templateMode;



    public ModelBuilderTemplateHandler(final boolean fragment, final Model model) {
        super();
        Validate.notNull(model, "Model cannot be null");
        Validate.notNull(model.getConfiguration(), "Engine Configuration returned by Model cannot be null");
        Validate.notNull(model.getTemplateMode(), "Template Mode returned by Model cannot be null");
        this.fragment = fragment;
        this.model = model;
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
        if (!this.fragment) {
            this.model.add(templateStart);
            // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        }
        super.handleTemplateStart(templateStart);
    }


    @Override
    public void handleTemplateEnd(final ITemplateEnd templateEnd) {
        if (!this.fragment) {
            this.model.add(templateEnd);
            // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        }
        super.handleTemplateEnd(templateEnd);
    }





    @Override
    public void handleText(final IText text) {
        this.model.add(text);
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleText(text);
    }



    @Override
    public void handleComment(final IComment comment) {
        this.model.add(comment);
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleComment(comment);
    }


    @Override
    public void handleCDATASection(final ICDATASection cdataSection) {
        this.model.add(cdataSection);
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleCDATASection(cdataSection);
    }




    @Override
    public void handleStandaloneElement(final IStandaloneElementTag standaloneElementTag) {
        this.model.add(standaloneElementTag);
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleStandaloneElement(standaloneElementTag);
    }


    @Override
    public void handleOpenElement(final IOpenElementTag openElementTag) {
        this.model.add(openElementTag);
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleOpenElement(openElementTag);
    }


    @Override
    public void handleCloseElement(final ICloseElementTag closeElementTag) {
        this.model.add(closeElementTag);
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleCloseElement(closeElementTag);
    }




    @Override
    public void handleDocType(final IDocType docType) {
        this.model.add(docType);
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleDocType(docType);
    }




    @Override
    public void handleXMLDeclaration(final IXMLDeclaration xmlDeclaration) {
        this.model.add(xmlDeclaration);
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleXMLDeclaration(xmlDeclaration);
    }




    @Override
    public void handleProcessingInstruction(final IProcessingInstruction processingInstruction) {
        this.model.add(processingInstruction);
        // The clone we just created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleProcessingInstruction(processingInstruction);
    }


    
}