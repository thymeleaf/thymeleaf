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
import java.io.CharArrayReader;
import java.io.Reader;
import java.io.StringReader;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.engine.TemplateHandlerAdapterTextHandler;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.resource.CharArrayResource;
import org.thymeleaf.resource.IResource;
import org.thymeleaf.resource.ReaderResource;
import org.thymeleaf.resource.StringResource;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateparser.reader.ParserLevelCommentTextReader;
import org.thymeleaf.templateparser.reader.PrototypeOnlyCommentTextReader;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public abstract class AbstractTextTemplateParser implements ITemplateParser {


    private final TextParser parser;



    protected AbstractTextTemplateParser(final int bufferPoolSize, final int bufferSize, final boolean processComments) {
        super();
        this.parser = new TextParser(bufferPoolSize, bufferSize, processComments);
    }




    /*
     * -------------------
     * PARSE METHODS
     * -------------------
     */



    public final void parseTemplate(
            final IEngineConfiguration configuration,
            final TemplateMode templateMode,
            final IResource templateResource,
            final String[] selectors,
            final ITemplateHandler templateHandler) {
        parse(configuration, templateMode, templateResource, true, selectors, templateHandler);
    }


    public final void parseFragment(
            final IEngineConfiguration configuration,
            final TemplateMode templateMode,
            final IResource templateResource,
            final String[] selectors,
            final ITemplateHandler templateHandler) {
        parse(configuration, templateMode, templateResource, false, selectors, templateHandler);
    }



    private void parse(
            final IEngineConfiguration configuration,
            final TemplateMode templateMode,
            final IResource templateResource,
            final boolean topLevel,
            final String[] selectors,
            final ITemplateHandler templateHandler) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(templateMode, "Template Mode cannot be null");
        Validate.isTrue(templateMode.isText(), "Template Mode has to be a text template mode");
        Validate.notNull(templateResource, "Template Resource cannot be null");
        // Text parsing does not allow selectors!
        Validate.isTrue(selectors == null || selectors.length == 0,
                "Selectors cannot be specified for a template using a TEXT template mode: template inclusion " +
                "operations must be always performed on whole template files, not fragments");
        Validate.notNull(templateHandler, "Template Handler cannot be null");

        final String templateResourceName = templateResource.getName();

        try {

            // The final step of the handler chain will be the adapter that will convert the text parser's handler chain to thymeleaf's.
            ITextHandler handler =
                        new TemplateHandlerAdapterTextHandler(
                                templateResourceName,
                                topLevel,
                                templateHandler,
                                configuration.getTextRepository(),
                                configuration.getElementDefinitions(),
                                configuration.getAttributeDefinitions(),
                                templateMode);


            // Compute the base reader, depending on the type of resource
            Reader templateReader;
            if (templateResource instanceof ReaderResource) {
                templateReader = new BufferedReader(((ReaderResource)templateResource).getContent());
            } else if (templateResource instanceof StringResource) {
                templateReader = new StringReader(((StringResource)templateResource).getContent());
            } else if (templateResource instanceof CharArrayResource) {
                final CharArrayResource charArrayResource = (CharArrayResource) templateResource;
                templateReader = new CharArrayReader(charArrayResource.getContent(), charArrayResource.getOffset(), charArrayResource.getLen());
            } else {
                throw new IllegalArgumentException(
                        "Cannot parse: unrecognized " + IResource.class.getSimpleName() + " implementation: " + templateResource.getClass().getName());
            }


            // Add the required reader wrappers in order to process parser-level and prototype-only comment blocks
            if (templateMode == TemplateMode.TEXT) {
                // There are no /*[+...+]*/ blocks in TEXT mode (it makes no sense)
                templateReader = new ParserLevelCommentTextReader(templateReader);
            } else {
                // TemplateMode.JAVASCRIPT || TemplateMode.CSS
                templateReader = new ParserLevelCommentTextReader(new PrototypeOnlyCommentTextReader(templateReader));
            }


            this.parser.parse(templateReader, handler);


        } catch (final TextParseException e) {
            final String message = "An error happened during template parsing";
            if (e.getLine() != null && e.getCol() != null) {
                throw new TemplateInputException(message, templateResource.getName(), e.getLine().intValue(), e.getCol().intValue(), e);
            }
            throw new TemplateInputException(message, templateResource.getName(), e);
        }

    }

    
    
}
