/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.templateengine.prepostprocessors.dialect;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AbstractTemplateHandler;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.ITemplateEnd;
import org.thymeleaf.model.ITemplateStart;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;

public class Dialect01PostProcessor extends AbstractTemplateHandler {

    private int processingInstructions = 0;
    private int openElementTags = 0;
    private int standaloneElementTags = 0;
    private int texts = 0;
    private int comments = 0;
    private int cdataSections = 0;
    private int docTypes = 0;
    private int xmlDeclarations = 0;

    private IModelFactory modelFactory;


    public Dialect01PostProcessor() {
        super();
    }




    @Override
    public void setContext(final ITemplateContext context) {
        super.setContext(context);
        this.modelFactory = context.getModelFactory();
    }



    @Override
    public void handleProcessingInstruction(final IProcessingInstruction processingInstruction) {
        final String content = nullToEmpty(processingInstruction.getContent());
        super.handleProcessingInstruction(this.modelFactory.createProcessingInstruction(processingInstruction.getTarget(), content + "(post:" + this.processingInstructions++ + ")"));
    }

    @Override
    public void handleCloseElement(final ICloseElementTag closeElementTag) {
        // Nothing to be done here
        super.handleCloseElement(closeElementTag);
    }

    @Override
    public void handleOpenElement(final IOpenElementTag openElementTag) {
        super.handleOpenElement(this.modelFactory.setAttribute(openElementTag, "post", "" + this.openElementTags++));
    }

    @Override
    public void handleStandaloneElement(final IStandaloneElementTag standaloneElementTag) {
        super.handleStandaloneElement(this.modelFactory.setAttribute(standaloneElementTag, "post", "" + this.standaloneElementTags++));
    }

    @Override
    public void handleText(final IText text) {
        final String t = nullToEmpty(text.getText());
        super.handleText(this.modelFactory.createText(t + "(post:" + this.texts++ + ")"));
    }

    @Override
    public void handleComment(final IComment comment) {
        final String c = nullToEmpty(comment.getContent());
        super.handleComment(this.modelFactory.createComment(c + "(post:" + this.comments++ + ")"));
    }

    @Override
    public void handleCDATASection(final ICDATASection cdataSection) {
        final String c = nullToEmpty(cdataSection.getContent());
        super.handleCDATASection(this.modelFactory.createCDATASection(c + "(post:" + this.cdataSections++ + ")"));
    }

    @Override
    public void handleDocType(final IDocType docType) {
        final String is = nullToEmpty(docType.getInternalSubset());
        super.handleDocType(this.modelFactory.createDocType(docType.getKeyword(), docType.getElementName(), docType.getPublicId(), docType.getSystemId(), is + "(post:" + this.docTypes++ + ")"));
    }

    @Override
    public void handleXMLDeclaration(final IXMLDeclaration xmlDeclaration) {
        final String is = nullToEmpty(xmlDeclaration.getEncoding());
        super.handleXMLDeclaration(this.modelFactory.createXMLDeclaration(xmlDeclaration.getVersion(), is + "(post:" + this.xmlDeclarations++ + ")", xmlDeclaration.getStandalone()));
    }

    @Override
    public void handleTemplateEnd(final ITemplateEnd templateEnd) {
        // Nothing to be done here
        super.handleTemplateEnd(templateEnd);
    }

    @Override
    public void handleTemplateStart(final ITemplateStart templateStart) {
        // Nothing to be done here
        super.handleTemplateStart(templateStart);
    }


    private static String nullToEmpty(final String value) {
        if (value == null) {
            return "";
        }
        return value + " ";
    }

}
