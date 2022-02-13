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

import java.util.ArrayList;
import java.util.List;

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
import org.thymeleaf.util.Validate;


/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ModelBuilderTemplateHandler extends AbstractTemplateHandler {

    private final List<IEngineTemplateEvent> events;
    private final IEngineConfiguration configuration;
    private final TemplateData templateData;



    public ModelBuilderTemplateHandler(final IEngineConfiguration configuration, final TemplateData templateData) {
        super();
        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(templateData, "Template Data cannot be null");
        this.configuration = configuration;
        this.templateData = templateData;
        this.events = new ArrayList<IEngineTemplateEvent>(100);
    }



    public TemplateModel getModel() {
        return new TemplateModel(this.configuration, this.templateData, this.events.toArray(new IEngineTemplateEvent[this.events.size()]));
    }


    // Note we are NOT implementing the setContext method, because we don't need it at all when just using
    // this handler for parsing (we are not processing anything!)



    @Override
    public void handleTemplateStart(final ITemplateStart templateStart) {
        this.events.add(TemplateStart.asEngineTemplateStart(templateStart));
        // The engine event we might have created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleTemplateStart(templateStart);
    }


    @Override
    public void handleTemplateEnd(final ITemplateEnd templateEnd) {
        this.events.add(TemplateEnd.asEngineTemplateEnd(templateEnd));
        // The engine event we might have created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleTemplateEnd(templateEnd);
    }





    @Override
    public void handleText(final IText text) {
        this.events.add(Text.asEngineText(text));
        // The engine event we might have created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleText(text);
    }



    @Override
    public void handleComment(final IComment comment) {
        this.events.add(Comment.asEngineComment(comment));
        // The engine event we might have created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleComment(comment);
    }


    @Override
    public void handleCDATASection(final ICDATASection cdataSection) {
        this.events.add(CDATASection.asEngineCDATASection(cdataSection));
        // The engine event we might have created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleCDATASection(cdataSection);
    }




    @Override
    public void handleStandaloneElement(final IStandaloneElementTag standaloneElementTag) {
        this.events.add(StandaloneElementTag.asEngineStandaloneElementTag(standaloneElementTag));
        // The engine event we might have created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleStandaloneElement(standaloneElementTag);
    }


    @Override
    public void handleOpenElement(final IOpenElementTag openElementTag) {
        this.events.add(OpenElementTag.asEngineOpenElementTag(openElementTag));
        // The engine event we might have created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleOpenElement(openElementTag);
    }


    @Override
    public void handleCloseElement(final ICloseElementTag closeElementTag) {
        this.events.add(CloseElementTag.asEngineCloseElementTag(closeElementTag));
        // The engine event we might have created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleCloseElement(closeElementTag);
    }




    @Override
    public void handleDocType(final IDocType docType) {
        this.events.add(DocType.asEngineDocType(docType));
        // The engine event we might have created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleDocType(docType);
    }




    @Override
    public void handleXMLDeclaration(final IXMLDeclaration xmlDeclaration) {
        this.events.add(XMLDeclaration.asEngineXMLDeclaration(xmlDeclaration));
        // The engine event we might have created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleXMLDeclaration(xmlDeclaration);
    }




    @Override
    public void handleProcessingInstruction(final IProcessingInstruction processingInstruction) {
        this.events.add(ProcessingInstruction.asEngineProcessingInstruction(processingInstruction));
        // The engine event we might have created is not forwarded - this makes cache creating transparent to the handler chain
        super.handleProcessingInstruction(processingInstruction);
    }


    
}