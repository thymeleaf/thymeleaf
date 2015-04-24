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

import org.thymeleaf.model.IAutoCloseElementTag;
import org.thymeleaf.model.IAutoOpenElementTag;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IUnmatchedCloseElementTag;
import org.thymeleaf.model.IXMLDeclaration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.text.ITextRepository;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public class StandardModelFactory implements IModelFactory {


    private final TemplateMode templateMode;
    private final ITextRepository textRepository;
    private final AttributeDefinitions attributeDefinitions;
    private final ElementDefinitions elementDefinitions;




    public StandardModelFactory(
            final TemplateMode templateMode, final ITextRepository textRepository,
            final AttributeDefinitions attributeDefinitions, final ElementDefinitions elementDefinitions) {

        super();

        Validate.notNull(templateMode, "Template Mode cannot be null");
        Validate.notNull(textRepository, "Text Repository cannot be null");
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        Validate.notNull(elementDefinitions, "Element Definitions cannot be null");

        this.templateMode = templateMode;
        this.textRepository = textRepository;
        this.attributeDefinitions = attributeDefinitions;
        this.elementDefinitions = elementDefinitions;

    }




    public ICDATASection createCDATASection(final String content) {
        return new CDATASection(this.textRepository, content);
    }




    public IComment createComment(final String content) {
        return new Comment(this.textRepository, content);
    }




    public IDocType createHTML5DocType() {
        return new DocType(this.textRepository, null, null);
    }

    public IDocType createDocType(final String publicId, final String systemId) {
        return new DocType(this.textRepository, publicId, systemId);
    }

    public IDocType createDocType(
            final String keyword,
            final String elementName,
            final String type,
            final String publicId,
            final String systemId,
            final String internalSubset) {

        return new DocType(this.textRepository, keyword, elementName, type, publicId, systemId, internalSubset);
    }




    public IProcessingInstruction createProcessingInstruction(final String target, final String content) {
        return new ProcessingInstruction(this.textRepository, target, content);
    }




    public IText createText(final String text) {
        return new Text(this.textRepository, text);
    }




    public IXMLDeclaration createXMLDeclaration(final String version, final String encoding, final String standalone) {
        return new XMLDeclaration(this.textRepository, version, encoding, standalone);
    }




    public IStandaloneElementTag createStandaloneElementTag(final String elementName, final boolean minimized) {
        return new StandaloneElementTag(this.templateMode, this.elementDefinitions, this.attributeDefinitions, elementName, minimized);
    }


    public IOpenElementTag createOpenElementTag(final String elementName) {
        return new OpenElementTag(this.templateMode, this.elementDefinitions, this.attributeDefinitions, elementName);
    }


    public IAutoOpenElementTag createAutoOpenElementTag(final String elementName) {
        return new AutoOpenElementTag(this.templateMode, this.elementDefinitions, this.attributeDefinitions, elementName);
    }


    public ICloseElementTag createCloseElementTag(final String elementName) {
        return new CloseElementTag(this.templateMode, this.elementDefinitions, elementName);
    }


    public IAutoCloseElementTag createAutoCloseElementTag(final String elementName) {
        return new AutoCloseElementTag(this.templateMode, this.elementDefinitions, elementName);
    }


    public IUnmatchedCloseElementTag createUnmatchedCloseElementTag(final String elementName) {
        return new UnmatchedCloseElementTag(this.templateMode, this.elementDefinitions, elementName);
    }


}
