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
package org.thymeleaf.templateparser.text;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Set;

import org.thymeleaf.EngineConfiguration;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.engine.TemplateHandlerAdapterTextHandler;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateparser.reader.ParserLevelCommentTextReader;
import org.thymeleaf.templateparser.reader.PrototypeOnlyCommentTextReader;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public abstract class AbstractTextTemplateParser implements ITemplateParser {


    private final TextParser parser;



    protected AbstractTextTemplateParser(
            final int bufferPoolSize, final int bufferSize, final boolean processCommentsAndLiterals,
            final boolean standardDialectPresent) {
        super();
        this.parser = new TextParser(bufferPoolSize, bufferSize, processCommentsAndLiterals, standardDialectPresent);
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
        Validate.isTrue(templateSelectors == null || templateSelectors.isEmpty(),
                        "Template selectors cannot be specified for a template using a TEXT template mode: template " +
                        "insertion operations must be always performed on whole template files, not fragments");
        Validate.notNull(templateMode, "Template Mode cannot be null");
        Validate.isTrue(templateMode.isText(), "Template Mode has to be a text template mode");
        Validate.isTrue(!useDecoupledLogic, "Cannot use decoupled logic in template mode " + templateMode);
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
        Validate.isTrue(templateMode.isText(), "Template Mode has to be a text template mode");
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


        // For a String template, we will use the ownerTemplate as templateName for its parsed events
        final String templateName = (resource != null? template : ownerTemplate);

        try {

            // The final step of the handler chain will be the adapter that will convert the text parser's handler chain to thymeleaf's.
            ITextHandler handler =
                        new TemplateHandlerAdapterTextHandler(
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
                handler = new InlinedOutputExpressionTextHandler(
                                configuration,
                                templateMode,
                                configuration.getStandardDialectPrefix(),
                                handler);
            }

            // Obtain the resource reader
            Reader templateReader = (resource != null? resource.reader() : new StringReader(template));


            // Add the required reader wrappers in order to process parser-level and prototype-only comment blocks
            if (templateMode == TemplateMode.TEXT) {
                // There are no /*[+...+]*/ blocks in TEXT mode (it makes no sense)
                templateReader = new ParserLevelCommentTextReader(templateReader);
            } else {
                // TemplateMode.JAVASCRIPT || TemplateMode.CSS
                templateReader = new ParserLevelCommentTextReader(new PrototypeOnlyCommentTextReader(templateReader));
            }


            this.parser.parse(templateReader, handler);


        } catch (final IOException e) {
            final String message = "An error happened during template parsing";
            throw new TemplateInputException(message, (resource != null? resource.getDescription() : template), e);
        } catch (final TextParseException e) {
            final String message = "An error happened during template parsing";
            if (e.getLine() != null && e.getCol() != null) {
                throw new TemplateInputException(message, (resource != null? resource.getDescription() : template), e.getLine().intValue(), e.getCol().intValue(), e);
            }
            throw new TemplateInputException(message, (resource != null? resource.getDescription() : template), e);
        }

    }

    
    
}
