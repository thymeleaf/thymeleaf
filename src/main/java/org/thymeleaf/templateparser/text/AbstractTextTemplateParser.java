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
package org.thymeleaf.templateparser.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

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
            final int bufferPoolSize, final int bufferSize, final boolean processComments,
            final boolean standardDialectPresent, final String standardDialectPrefix) {
        super();
        this.parser = new TextParser(bufferPoolSize, bufferSize, processComments, standardDialectPresent, standardDialectPrefix);
    }




    /*
     * -------------------
     * PARSE METHODS
     * -------------------
     */



    public void parseStandalone(
            final IEngineConfiguration configuration,
            final String template,
            final ITemplateResource resource,
            final String[] selectors,
            final TemplateMode templateMode,
            final ITemplateHandler handler) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(template, "Template cannot be null");
        Validate.notNull(resource, "Template Resource cannot be null");
        Validate.isTrue(selectors == null || selectors.length == 0,
                        "Selectors cannot be specified for a template using a TEXT template mode: template inclusion " +
                        "operations must be always performed on whole template files, not fragments");
        Validate.notNull(templateMode, "Template Mode cannot be null");
        Validate.isTrue(templateMode.isText(), "Template Mode has to be a text template mode");
        Validate.notNull(handler, "Template Handler cannot be null");

        parse(configuration, null, template, resource, selectors, 0, 0, templateMode, handler);

    }


    public void parseNested(
            final IEngineConfiguration configuration,
            final String ownerTemplate,
            final String template,
            final ITemplateResource resource,
            final int lineOffset, final int colOffset,
            final TemplateMode templateMode,
            final ITemplateHandler handler) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(ownerTemplate, "Owner template cannot be null");
        Validate.notNull(template, "Template cannot be null");
        Validate.notNull(resource, "Template Resource cannot be null");
        // NOTE selectors cannot be specified when parsing a nested template
        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.isTrue(templateMode.isText(), "Template Mode has to be a text template mode");
        Validate.notNull(handler, "Template Handler cannot be null");

        parse(configuration, ownerTemplate, template, resource, null, lineOffset, colOffset, templateMode, handler);

    }



    private void parse(
            final IEngineConfiguration configuration,
            final String ownerTemplate, final String template, final ITemplateResource resource, final String[] selectors,
            final int lineOffset, final int colOffset,
            final TemplateMode templateMode,
            final ITemplateHandler templateHandler) {


        final String templateName = (ownerTemplate != null? ownerTemplate : template);

        try {

            // The final step of the handler chain will be the adapter that will convert the text parser's handler chain to thymeleaf's.
            ITextHandler handler =
                        new TemplateHandlerAdapterTextHandler(
                                templateName,
                                templateHandler,
                                configuration.getTextRepository(),
                                configuration.getElementDefinitions(),
                                configuration.getAttributeDefinitions(),
                                templateMode,
                                lineOffset, colOffset);


            // Just before the adapter text handler, we will insert the processing of inlined output expressions
            handler = new InlinedOutputExpressionTextHandler(
                                configuration,
                                templateMode,
                                configuration.isStandardDialectPresent(),
                                configuration.getStandardDialectPrefix(),
                                handler);

            // Obtain the resource reader
            Reader templateReader = new BufferedReader(resource.reader());


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
            throw new TemplateInputException(message, resource.getDescription(), e);
        } catch (final TextParseException e) {
            final String message = "An error happened during template parsing";
            if (e.getLine() != null && e.getCol() != null) {
                throw new TemplateInputException(message, resource.getDescription(), e.getLine().intValue(), e.getCol().intValue(), e);
            }
            throw new TemplateInputException(message, resource.getDescription(), e);
        }

    }

    
    
}
