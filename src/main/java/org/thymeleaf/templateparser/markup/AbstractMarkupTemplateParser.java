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
package org.thymeleaf.templateparser.markup;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Set;

import org.attoparser.IMarkupHandler;
import org.attoparser.IMarkupParser;
import org.attoparser.MarkupParser;
import org.attoparser.ParseException;
import org.attoparser.config.ParseConfiguration;
import org.attoparser.select.BlockSelectorMarkupHandler;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.engine.TemplateHandlerAdapterMarkupHandler;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.processor.text.ITextProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateparser.reader.ParserLevelCommentMarkupReader;
import org.thymeleaf.templateparser.reader.PrototypeOnlyCommentMarkupReader;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public abstract class AbstractMarkupTemplateParser implements ITemplateParser {


    private final IMarkupParser parser;
    private final boolean html;



    protected AbstractMarkupTemplateParser(final ParseConfiguration parseConfiguration, final int bufferPoolSize, final int bufferSize) {
        super();
        Validate.notNull(parseConfiguration, "Parse configuration cannot be null");
        this.parser = new MarkupParser(parseConfiguration, bufferPoolSize, bufferSize);
        this.html = parseConfiguration.getMode().equals(ParseConfiguration.ParsingMode.HTML);
    }




    /*
     * -------------------
     * PARSE METHODS
     * -------------------
     */



    public void parseStandalone(
            final IEngineConfiguration configuration,
            final String ownerTemplate,
            final String template,
            final Set<String> templateSelectors,
            final ITemplateResource resource,
            final TemplateMode templateMode,
            final ITemplateHandler handler) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        // ownerTemplate CAN be null if this is a first-level template
        Validate.notNull(template, "Template cannot be null");
        Validate.notNull(resource, "Template Resource cannot be null");
        // templateSelectors CAN be null if we are going to render the entire template
        Validate.notNull(templateMode, "Template Mode cannot be null");
        Validate.isTrue(templateMode.isMarkup(), "Template Mode has to be a markup template mode");
        Validate.notNull(handler, "Template Handler cannot be null");

        parse(configuration, ownerTemplate, template, templateSelectors, resource, 0, 0, templateMode, handler);

    }


    public void parseString(
            final IEngineConfiguration configuration,
            final String ownerTemplate,
            final String template,
            final int lineOffset, final int colOffset,
            final TemplateMode templateMode,
            final ITemplateHandler handler) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(ownerTemplate, "Owner template cannot be null");
        Validate.notNull(template, "Template cannot be null");
        // NOTE selectors cannot be specified when parsing a nested template
        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.isTrue(templateMode.isMarkup(), "Template Mode has to be a markup template mode");
        Validate.notNull(handler, "Template Handler cannot be null");

        parse(configuration, ownerTemplate, template, null, null, lineOffset, colOffset, templateMode, handler);

    }



    private void parse(
            final IEngineConfiguration configuration,
            final String ownerTemplate, final String template, final Set<String> templateSelectors,
            final ITemplateResource resource,
            final int lineOffset, final int colOffset,
            final TemplateMode templateMode,
            final ITemplateHandler templateHandler) {

        if (templateMode == TemplateMode.HTML) {
            Validate.isTrue(this.html, "Parser is configured as XML, but HTML-mode template parsing is being requested");
        } else if (templateMode == TemplateMode.XML) {
            Validate.isTrue(!this.html, "Parser is configured as HTML, but XML-mode template parsing is being requested");
        } else {
            throw new IllegalArgumentException(
                    "Parser is configured as " + (this.html? "HTML" : "XML") + " but an unsupported template mode " +
                    "has been specified: " + templateMode);
        }

        // For a String template, we will use the ownerTemplate as templateName for its parsed events
        final String templateName = (resource != null? template : ownerTemplate);

        try {

            // The final step of the handler chain will be the adapter that will convert attoparser's handler chain to thymeleaf's.
            IMarkupHandler handler =
                        new TemplateHandlerAdapterMarkupHandler(
                                templateName,
                                templateHandler,
                                configuration.getTextRepository(),
                                configuration.getElementDefinitions(),
                                configuration.getAttributeDefinitions(),
                                templateMode,
                                lineOffset, colOffset);

            // Just before the adapter markup handler, we will insert the processing of inlined output expressions
            // but only if we are not going to disturb the execution of text processors coming from other dialects
            // (which might see text blocks coming as several blocks instead of just one). We will not really worry
            // about pre-processors and post-processors because they are single classes that can always merge
            // consecutive text events.
            if (canPreProcessInlinedOutputExpresssions(configuration, templateMode)) {
                handler = new InlinedOutputExpressionMarkupHandler(
                                configuration,
                                templateMode,
                                configuration.isStandardDialectPresent(),
                                configuration.getStandardDialectPrefix(),
                                handler);
            }


            // If we need to select blocks, we will need a block selector here. Note this will get executed in the
            // handler chain AFTER thymeleaf's own TemplateHandlerAdapterMarkupHandler, so that we will be able to
            // include in selectors code inside prototype-only comments.
            if (templateSelectors != null && !templateSelectors.isEmpty()) {

                final String standardDialectPrefix = configuration.getStandardDialectPrefix();

                final TemplateFragmentMarkupReferenceResolver referenceResolver =
                        (standardDialectPrefix != null ?
                            TemplateFragmentMarkupReferenceResolver.forPrefix(this.html, standardDialectPrefix) : null);
                handler = new BlockSelectorMarkupHandler(handler, templateSelectors.toArray(new String[templateSelectors.size()]), referenceResolver);
            }


            // Obtain the resource reader
            Reader templateReader = (resource != null? resource.reader() : new StringReader(template));


            // Add the required reader wrappers in order to process parser-level and prototype-only comment blocks
            templateReader = new ParserLevelCommentMarkupReader(new PrototypeOnlyCommentMarkupReader(templateReader));


            this.parser.parse(templateReader, handler);


        } catch (final IOException e) {
            final String message = "An error happened during template parsing";
            throw new TemplateInputException(message, (resource != null? resource.getDescription() : template), e);
        } catch (final ParseException e) {
            final String message = "An error happened during template parsing";
            if (e.getLine() != null && e.getCol() != null) {
                throw new TemplateInputException(message, (resource != null? resource.getDescription() : template), e.getLine().intValue(), e.getCol().intValue(), e);
            }
            throw new TemplateInputException(message, (resource != null? resource.getDescription() : template), e);
        }

    }




    private static boolean canPreProcessInlinedOutputExpresssions(
            final IEngineConfiguration configuration, final TemplateMode templateMode) {

        if (!configuration.isStandardDialectPresent()) {
            // If we haven't configured the standard dialect, preprocessing inlined expressions makes no sense
            return false;
        }

        final Set<ITextProcessor> textProcessors = configuration.getTextProcessors(templateMode);
        // If the Standard Dialect is present, the inlining text processor will be there. If there are
        // more than that, then it is not the standard dialect the only one wanting to process texts, which means
        // we should not disturb the text event structure by applying inlining preprocessing here.
        return textProcessors.size() <= 1;

    }


    
}
