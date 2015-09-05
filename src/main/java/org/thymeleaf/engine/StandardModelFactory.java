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
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.IText;
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


    private final IEngineConfiguration configuration;
    private final ITextRepository textRepository;
    private final AttributeDefinitions attributeDefinitions;
    private final ElementDefinitions elementDefinitions;
    private final TemplateMode templateMode;
    private final String template;
    private final TemplateManager templateManager;




    public StandardModelFactory(
            final IEngineConfiguration configuration, final TemplateMode templateMode,
            final String template, final TemplateManager templateManager) {

        super();

        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(templateMode, "Template Mode cannot be null");
        Validate.notNull(configuration.getTextRepository(), "Text Repository returned by Engine Configuration cannot be null");
        Validate.notNull(configuration.getAttributeDefinitions(), "Attribute Definitions returned by Engine Configuration cannot be null");
        Validate.notNull(configuration.getElementDefinitions(), "Element Definitions returned by Engine Configuration cannot be null");
        Validate.notNull(template, "Template cannot be null");
        Validate.notNull(templateManager, "Template Processor cannot be null");

        this.configuration = configuration;
        this.textRepository = this.configuration.getTextRepository();
        this.attributeDefinitions = this.configuration.getAttributeDefinitions();
        this.elementDefinitions = this.configuration.getElementDefinitions();
        this.templateMode = templateMode;
        this.template = template;
        this.templateManager = templateManager;

    }



    private void checkRestrictedEventForTextTemplateMode(final String eventClass) {
        if (this.templateMode.isText()) {
            throw new TemplateProcessingException(
                    "Events of class " + eventClass + " cannot be created in a text-type template " +
                    "mode (" + this.templateMode + ")");
        }
    }



    public Model createModel() {
        return new Model(this.configuration, this.templateMode);
    }



    public IModel parse(final String fragment) {
        // We will be setting useCache to false because we don't want to pollute the cache with mere String
        // parsing done from here. Also, we are 'artificially' specifying it as nested even if we don't really
        // know if this fragment is exactly a nested text inside the template, but that's not really important...
        return this.templateManager.parseNestedFragment(
                this.configuration, this.template, fragment, 0, 0, this.templateMode, false);
    }



    public ICDATASection createCDATASection(final String content) {
        checkRestrictedEventForTextTemplateMode("CDATASection");
        return new CDATASection(this.textRepository, content);
    }




    public IComment createComment(final String content) {
        checkRestrictedEventForTextTemplateMode("Comment");
        return new Comment(this.textRepository, content);
    }




    public IDocType createHTML5DocType() {
        checkRestrictedEventForTextTemplateMode("DocType");
        return new DocType(this.textRepository, null, null);
    }

    public IDocType createDocType(final String publicId, final String systemId) {
        checkRestrictedEventForTextTemplateMode("DocType");
        return new DocType(this.textRepository, publicId, systemId);
    }

    public IDocType createDocType(
            final String keyword,
            final String elementName,
            final String type,
            final String publicId,
            final String systemId,
            final String internalSubset) {

        checkRestrictedEventForTextTemplateMode("DocType");
        return new DocType(this.textRepository, keyword, elementName, type, publicId, systemId, internalSubset);
    }




    public IProcessingInstruction createProcessingInstruction(final String target, final String content) {
        checkRestrictedEventForTextTemplateMode("ProcessingInstruction");
        return new ProcessingInstruction(this.textRepository, target, content);
    }




    public IText createText(final String text) {
        return new Text(this.textRepository, text);
    }




    public IXMLDeclaration createXMLDeclaration(final String version, final String encoding, final String standalone) {
        checkRestrictedEventForTextTemplateMode("XMLDeclaration");
        return new XMLDeclaration(this.textRepository, version, encoding, standalone);
    }




    public IStandaloneElementTag createStandaloneElementTag(final String elementName, final boolean minimized) {
        return new StandaloneElementTag(this.templateMode, this.elementDefinitions, this.attributeDefinitions, elementName, false, minimized);
    }

    public IStandaloneElementTag createSyntheticStandaloneElementTag(final String elementName, final boolean minimized) {
        return new StandaloneElementTag(this.templateMode, this.elementDefinitions, this.attributeDefinitions, elementName, true, minimized);
    }


    public IOpenElementTag createOpenElementTag(final String elementName) {
        return new OpenElementTag(this.templateMode, this.elementDefinitions, this.attributeDefinitions, elementName, false);
    }

    public IOpenElementTag createSyntheticOpenElementTag(final String elementName) {
        checkRestrictedEventForTextTemplateMode("SyntheticOpenElementTag");
        return new OpenElementTag(this.templateMode, this.elementDefinitions, this.attributeDefinitions, elementName, true);
    }


    public ICloseElementTag createCloseElementTag(final String elementName) {
        return new CloseElementTag(this.templateMode, this.elementDefinitions, elementName, false, false);
    }

    public ICloseElementTag createSyntheticCloseElementTag(final String elementName) {
        checkRestrictedEventForTextTemplateMode("SyntheticCloseElementTag");
        return new CloseElementTag(this.templateMode, this.elementDefinitions, elementName, true, false);
    }

    public ICloseElementTag createUnmatchedCloseElementTag(final String elementName) {
        checkRestrictedEventForTextTemplateMode("UnmatchedCloseElementTag");
        return new CloseElementTag(this.templateMode, this.elementDefinitions, elementName, false, true);
    }


}
