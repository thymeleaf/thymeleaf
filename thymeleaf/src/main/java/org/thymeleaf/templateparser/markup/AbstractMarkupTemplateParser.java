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
import org.attoparser.select.NodeSelectorMarkupHandler;
import org.thymeleaf.EngineConfiguration;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.engine.TemplateHandlerAdapterMarkupHandler;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateparser.markup.decoupled.DecoupledTemplateLogic;
import org.thymeleaf.templateparser.markup.decoupled.DecoupledTemplateLogicMarkupHandler;
import org.thymeleaf.templateparser.markup.decoupled.DecoupledTemplateLogicUtils;
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
            final boolean useDecoupledLogic,
            final ITemplateHandler handler) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        // ownerTemplate CAN be null if this is a first-level template
        Validate.notNull(template, "Template cannot be null");
        Validate.notNull(resource, "Template Resource cannot be null");
        // templateSelectors CAN be null if we are going to render the entire template
        Validate.notNull(templateMode, "Template Mode cannot be null");
        Validate.isTrue(templateMode.isMarkup(), "Template Mode has to be a markup template mode");
        Validate.notNull(handler, "Template Handler cannot be null");

        parse(configuration, ownerTemplate, template, templateSelectors, resource, 0, 0, templateMode, useDecoupledLogic, handler);

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

        parse(configuration, ownerTemplate, template, null, null, lineOffset, colOffset, templateMode, false, handler);

    }



    private void parse(
            final IEngineConfiguration configuration,
            final String ownerTemplate, final String template, final Set<String> templateSelectors,
            final ITemplateResource resource,
            final int lineOffset, final int colOffset,
            final TemplateMode templateMode,
            final boolean useDecoupledLogic,
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

            // We might need to first check for the existence of decoupled logic in a separate resource
            final DecoupledTemplateLogic decoupledTemplateLogic =
                    (useDecoupledLogic && resource != null ?
                            DecoupledTemplateLogicUtils.computeDecoupledTemplateLogic(
                                    configuration, ownerTemplate, template, templateSelectors, resource, templateMode, this.parser) :
                            null);


            // The final step of the handler chain will be the adapter that will convert attoparser's handler chain to thymeleaf's.
            IMarkupHandler handler =
                        new TemplateHandlerAdapterMarkupHandler(
                                templateName,
                                templateHandler,
                                configuration.getElementDefinitions(),
                                configuration.getAttributeDefinitions(),
                                templateMode,
                                lineOffset, colOffset);

            // Just before the adapter markup handler, we will insert the processing of inlined output expressions
            // but only if we are not going to disturb the execution of text processors coming from other dialects
            // (which might see text blocks coming as several blocks instead of just one).
            if (configuration instanceof EngineConfiguration && ((EngineConfiguration) configuration).isModelReshapeable(templateMode)) {
                handler = new InlinedOutputExpressionMarkupHandler(
                                configuration,
                                templateMode,
                                configuration.getStandardDialectPrefix(),
                                handler);
            }


            // Precompute flags
            final boolean injectAttributes = decoupledTemplateLogic != null && decoupledTemplateLogic.hasInjectedAttributes();
            final boolean selectBlock = templateSelectors != null && !templateSelectors.isEmpty();


            // Pre-create reference resolver if needed, so that it can be used in both block and node selection

            final TemplateFragmentMarkupReferenceResolver referenceResolver;
            if (injectAttributes || selectBlock) {
                final String standardDialectPrefix = configuration.getStandardDialectPrefix();
                referenceResolver =
                        (standardDialectPrefix != null ?
                                TemplateFragmentMarkupReferenceResolver.forPrefix(this.html, standardDialectPrefix) : null);
            } else {
                referenceResolver = null;
            }


            // If we need to select blocks, we will need a block selector here. Note this will get executed in the
            // handler chain AFTER thymeleaf's parser-level and prototype-only comment block readers, so that we
            // will be able to include in selectors code inside prototype-only comments.
            if (selectBlock) {
                handler = new BlockSelectorMarkupHandler(handler, templateSelectors.toArray(new String[templateSelectors.size()]), referenceResolver);
            }


            // If we need to select nodes in order to inject additional attributes, we will need a node selector here.
            // Note this will get executed in the handler chain AFTER thymeleaf's parser-level and prototype-only
            // comment block readers, so that we will be able to include in selectors code inside prototype-only comments.
            if (injectAttributes) {
                // This handler will be in charge of really injecting the attributes, reacting to the node-selection
                // signals sent by the NodeSelectorMarkupHandler configured below
                handler = new DecoupledTemplateLogicMarkupHandler(decoupledTemplateLogic, handler);
                // NOTE it is important that THIS IS THE FIRST NODE- OR BLOCK-SELECTION HANDLER TO BE APPLIED because
                // structures in the DecoupledTemplateLogicMarkupHandler will consider 0 (zero) as their injection
                // level of interest
                final Set<String> nodeSelectors = decoupledTemplateLogic.getAllInjectedAttributeSelectors();
                handler = new NodeSelectorMarkupHandler(handler, handler, nodeSelectors.toArray(new String[nodeSelectors.size()]), referenceResolver);
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


    
}
