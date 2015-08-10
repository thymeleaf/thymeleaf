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

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IElementAttributes;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.text.AbstractTextHandler;
import org.thymeleaf.templateparser.text.TextParseException;
import org.thymeleaf.text.ITextRepository;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class TemplateHandlerAdapterTextHandler extends AbstractTextHandler {

    private static final String ATTRIBUTE_EQUALS_OPERATOR = "=";

    private final String templateName;
    private final boolean topLevelTemplate;
    private final ITemplateHandler templateHandler;
    private final ITextRepository textRepository;
    private final ElementDefinitions elementDefinitions;
    private final AttributeDefinitions attributeDefinitions;
    private final TemplateMode templateMode;

    private final DocumentStart documentStart;
    private final DocumentEnd documentEnd;

    private final Text text;

    private final OpenElementTag openElementTag;
    private final StandaloneElementTag standaloneElementTag;
    private final CloseElementTag closeElementTag;

    private ElementAttributes currentElementAttributes;


    public TemplateHandlerAdapterTextHandler(final String templateName,
                                             final boolean topLevelTemplate,
                                             final ITemplateHandler templateHandler,
                                             final ITextRepository textRepository,
                                             final ElementDefinitions elementDefinitions,
                                             final AttributeDefinitions attributeDefinitions,
                                             final TemplateMode templateMode) {
        super();

        Validate.notNull(templateHandler, "Template handler cannot be null");
        Validate.notNull(textRepository, "Text Repository cannot be null");
        Validate.notNull(elementDefinitions, "Element Definitions repository cannot be null");
        Validate.notNull(attributeDefinitions, "Attribute Definitions repository cannot be null");
        Validate.notNull(templateMode, "Template mode cannot be null");

        this.templateName = templateName;
        this.topLevelTemplate = topLevelTemplate;

        this.templateHandler = templateHandler;

        // We will default the text repository to a no-cache implementation
        this.textRepository = textRepository;

        // These cannot be null
        this.elementDefinitions = elementDefinitions;
        this.attributeDefinitions = attributeDefinitions;
        this.templateMode = templateMode;

        // We will be using these as objectual buffers in order to avoid creating too many objects
        this.documentStart = new DocumentStart();
        this.documentEnd = new DocumentEnd();

        this.text = new Text(this.textRepository);

        this.openElementTag = new OpenElementTag(this.templateMode, this.elementDefinitions, this.attributeDefinitions);
        this.standaloneElementTag = new StandaloneElementTag(this.templateMode, this.elementDefinitions, this.attributeDefinitions);
        this.closeElementTag = new CloseElementTag(this.templateMode, this.elementDefinitions);

        this.currentElementAttributes = null; // Will change as soon as we start processing an open or standalone tag
        
        
    }



    @Override
    public void handleDocumentStart(
            final long startTimeNanos, final int line, final int col)
            throws TextParseException {

        // We will only be issuing document start/end events on the top level templates, and never on the fragments
        // being parsed as a part of a th:insert/th:replace etc. in order to add their markup to the top level template.
        // The reason for this is that it would make no sense to have these events suspended during DOM-tree caching,
        // or iterations, or any similar processing mechanism, given the fact that these document start/end events do
        // not model nodes, nor any part of any type of node.
        // IMPORTANT: note that partial renderings of templates (like e.g. a Spring controller returning "home :: main"
        // as a template name) are indeed top level templates. These are simply templates that have been applied a
        // markup selector, but they are not fragments meant to be included in other higher-level templates being
        // processed.
        if (this.topLevelTemplate) {
            this.documentStart.reset(startTimeNanos, this.templateName, line, col);
            this.templateHandler.handleDocumentStart(this.documentStart);
        }

    }


    @Override
    public void handleDocumentEnd(
            final long endTimeNanos, final long totalTimeNanos, final int line, final int col)
            throws TextParseException {

        // We will only be issuing document start/end events on the top level templates, and never on the fragments
        // being parsed as a part of a th:insert/th:replace etc. in order to add their markup to the top level template.
        // The reason for this is that it would make no sense to have these events suspended during DOM-tree caching,
        // or iterations, or any similar processing mechanism, given the fact that these document start/end events do
        // not model nodes, nor any part of any type of node.
        // IMPORTANT: note that partial renderings of templates (like e.g. a Spring controller returning "home :: main"
        // as a template name) are indeed top level templates. These are simply templates that have been applied a
        // markup selector, but they are not fragments meant to be included in other higher-level templates being
        // processed.
        if (this.topLevelTemplate) {
            this.documentEnd.reset(endTimeNanos, totalTimeNanos, this.templateName, line, col);
            this.templateHandler.handleDocumentEnd(this.documentEnd);
        }

    }



    @Override
    public void handleText(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col)
            throws TextParseException {
        this.text.reset(buffer, offset, len, this.templateName, line, col);
        // Precompute some flag in texts - this should help performance, especially when using a template cache.
        // Example flags are: 'whitespace' (marking when a text only contains whitespace) or the internal 'inlineable'
        // (marking when a text might contain inlined expressions according to the Standard Dialects).
        // NOTE we are doing this only for those text nodes that are parsed, i.e. come from the template and therefore
        // will probably be processed (and their 'whitespace' flag queried), whereas we still save the need to call this
        // 'isWhitespace' computation on Text nodes added during processing itself (which might be many more, and larger)
        this.text.precomputeFlags();
        this.templateHandler.handleText(this.text);
    }




    @Override
    public void handleStandaloneElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized, final int line, final int col)
            throws TextParseException {

        this.standaloneElementTag.reset(
                this.textRepository.getText(buffer, nameOffset, nameLen), minimized, this.templateName, line, col);
        this.currentElementAttributes = (ElementAttributes) this.standaloneElementTag.getAttributes();

    }

    @Override
    public void handleStandaloneElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized, final int line, final int col)
            throws TextParseException {

        // Precompute the associated processors - this might help performance, especially when using an event cache
        this.standaloneElementTag.precomputeAssociatedProcessors();
        // Call the template handler method with the gathered info
        this.templateHandler.handleStandaloneElement(this.standaloneElementTag);
        this.currentElementAttributes = null;

    }



    @Override
    public void handleOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {

        this.openElementTag.reset(
                this.textRepository.getText(buffer, nameOffset, nameLen), this.templateName, line, col);
        this.currentElementAttributes = (ElementAttributes) this.openElementTag.getAttributes();

    }

    @Override
    public void handleOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {

        // Precompute the associated processors - this might help performance, especially when using an event cache
        this.openElementTag.precomputeAssociatedProcessors();
        // Call the template handler method with the gathered info
        this.templateHandler.handleOpenElement(this.openElementTag);
        this.currentElementAttributes = null;

    }



    @Override
    public void handleCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {

        this.closeElementTag.reset(this.textRepository.getText(buffer, nameOffset, nameLen), this.templateName, line, col);
        this.currentElementAttributes = null;

    }

    @Override
    public void handleCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {

        // Call the template handler method with the gathe red info
        this.templateHandler.handleCloseElement(this.closeElementTag);
        this.currentElementAttributes = null;

    }



    @Override
    public void handleAttribute(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int nameLine, final int nameCol,
            final int operatorOffset, final int operatorLen,
            final int operatorLine, final int operatorCol,
            final int valueContentOffset, final int valueContentLen,
            final int valueOuterOffset, final int valueOuterLen,
            final int valueLine, final int valueCol)
            throws TextParseException {

        if (this.currentElementAttributes == null) {
            throw new TemplateProcessingException(
                    "Cannot process: attribute is not related to an open/standalone tag", this.templateName, nameLine, nameCol);
        }

        final String attributeName = this.textRepository.getText(buffer, nameOffset, nameLen);

        final String attributeOperator =
                (operatorLen > 0 ?
                        (operatorLen == 1 && buffer[operatorOffset] == '=' ?
                                ATTRIBUTE_EQUALS_OPERATOR : // Shortcut for the most common case
                                this.textRepository.getText(buffer, operatorOffset, operatorLen)) :
                        null);

        final String value =
                (attributeOperator != null ?
                        this.textRepository.getText(buffer, valueContentOffset, valueContentLen) :
                        null);

        final IElementAttributes.ValueQuotes valueQuotes;
        if (value == null) {
            valueQuotes = null;
        } else if (valueOuterOffset == valueContentOffset) {
            valueQuotes = IElementAttributes.ValueQuotes.NONE;
        } else if (buffer[valueOuterOffset] == '"') {
            valueQuotes = IElementAttributes.ValueQuotes.DOUBLE;
        } else if (buffer[valueOuterOffset] == '\'') {
            valueQuotes = IElementAttributes.ValueQuotes.SINGLE;
        } else {
            valueQuotes = IElementAttributes.ValueQuotes.NONE;
        }

        // Note we are using 'autowhitespace', given text-mode parsing does not include whitespace parsing
        this.currentElementAttributes.setAttribute(
                attributeName, attributeOperator, value, valueQuotes, nameLine, nameCol, true);

    }



}
