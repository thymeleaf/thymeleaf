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
package org.thymeleaf.aurora;

import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.thymeleaf.aurora.context.IContext;
import org.thymeleaf.aurora.context.ITemplateProcessingContext;
import org.thymeleaf.aurora.engine.ITemplateHandler;
import org.thymeleaf.aurora.engine.OutputTemplateHandler;
import org.thymeleaf.aurora.engine.ProcessorTemplateHandler;
import org.thymeleaf.aurora.engine.StandardTemplateProcessingContextFactory;
import org.thymeleaf.aurora.parser.HTMLTemplateParser;
import org.thymeleaf.aurora.parser.ITemplateParser;
import org.thymeleaf.aurora.parser.XMLTemplateParser;
import org.thymeleaf.aurora.resource.IResource;
import org.thymeleaf.aurora.standard.StandardDialect;
import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.aurora.text.ITextRepository;
import org.thymeleaf.aurora.text.TextRepositories;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;


/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class TemplateEngine implements ITemplateEngine {

    private static final Set<DialectConfiguration> STANDARD_DIALECT_CONFIGURATIONS;

    private final ITemplateEngineConfiguration configuration;
    private final ITemplateParser htmlParser;
    private final ITemplateParser xmlParser;



    static {
        final StandardDialect standardDialect = new StandardDialect();
        final DialectConfiguration standardDialectConfiguration = new DialectConfiguration(standardDialect);
        STANDARD_DIALECT_CONFIGURATIONS = Collections.singleton(standardDialectConfiguration);
    }



    public TemplateEngine() {
        this(STANDARD_DIALECT_CONFIGURATIONS, TextRepositories.createLimitedSizeCacheRepository());
    }


    public TemplateEngine(final Set<DialectConfiguration> dialectConfigurations, final ITextRepository textRepository) {

        super();

        Validate.notNull(dialectConfigurations, "Dialect configuration set cannot be null");

        final ITextRepository engineTextRepository =
                (textRepository != null? textRepository : TextRepositories.createNoCacheRepository());

        this.configuration = new TemplateEngineConfiguration(dialectConfigurations, engineTextRepository);
        this.htmlParser = new HTMLTemplateParser(40,2048);
        this.xmlParser = new XMLTemplateParser(40, 2048);

    }






    public void process(
            final TemplateMode templateMode, final String templateName, final IResource templateResource,
            final IContext context, final Writer writer) {

        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.notNull(templateName, "Template name cannot be null");
        Validate.notNull(templateResource, "Template resource cannot be null");
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(writer, "Writer cannot be null");


        /*
         * Create of the Template Processing Context instance that corresponds to this execution of the template engine
         */
        final ITemplateProcessingContext templateProcessingContext =
                StandardTemplateProcessingContextFactory.build(this.configuration, templateName, templateMode, context);


        /*
         * Declare the pair of pointers that will allow us to build the chain of template handlers
         */
        ITemplateHandler firstHandler = null;
        ITemplateHandler lastHandler = null;


        /*
         * First type of handlers to be added: pre-processors (if any)
         */
        final List<Class<? extends ITemplateHandler>> preProcessors = this.configuration.getPreProcessors();
        if (preProcessors != null) {
            for (final Class<? extends ITemplateHandler> preProcessorClass : preProcessors) {
                final ITemplateHandler preProcessor;
                try {
                    preProcessor = preProcessorClass.newInstance();
                } catch (final Exception e) {
                    // This should never happen - class was already checked during configuration to contain a zero-arg constructor
                    throw new TemplateProcessingException(
                            "An exception happened during the creation of a new instance of pre-processor " + preProcessorClass.getClass().getName(), e);
                }
                // Initialize the pre-processor
                preProcessor.setTemplateProcessingContext(templateProcessingContext);
                if (firstHandler == null) {
                    firstHandler = preProcessor;
                    lastHandler = preProcessor;
                } else {
                    lastHandler.setNext(preProcessor);
                    lastHandler = preProcessor;
                }
            }
        }


        /*
         * Initialize and add to the chain te Processor Handler itself, the central piece of the chain
         */
        final ProcessorTemplateHandler processorHandler = new ProcessorTemplateHandler();
        processorHandler.setTemplateProcessingContext(templateProcessingContext);
        if (firstHandler == null) {
            firstHandler = processorHandler;
            lastHandler = processorHandler;
        } else {
            lastHandler.setNext(processorHandler);
            lastHandler = processorHandler;
        }


        /*
         * After the Processor Handler, we now must add the post-processors (if any)
         */
        final List<Class<? extends ITemplateHandler>> postProcessors = this.configuration.getPostProcessors();
        if (postProcessors != null) {
            for (final Class<? extends ITemplateHandler> postProcessorClass : postProcessors) {
                final ITemplateHandler postProcessor;
                try {
                    postProcessor = postProcessorClass.newInstance();
                } catch (final Exception e) {
                    // This should never happen - class was already checked during configuration to contain a zero-arg constructor
                    throw new TemplateProcessingException(
                            "An exception happened during the creation of a new instance of post-processor " + postProcessorClass.getClass().getName(), e);
                }
                // Initialize the pre-processor
                postProcessor.setTemplateProcessingContext(templateProcessingContext);
                if (firstHandler == null) {
                    firstHandler = postProcessor;
                    lastHandler = postProcessor;
                } else {
                    lastHandler.setNext(postProcessor);
                    lastHandler = postProcessor;
                }
            }
        }


        /*
         * Last step: the OUTPUT HANDLER
         */
        final OutputTemplateHandler outputHandler = new OutputTemplateHandler(writer);
        outputHandler.setTemplateProcessingContext(templateProcessingContext);
        if (firstHandler == null) {
            firstHandler = outputHandler;
            lastHandler = outputHandler;
        } else {
            lastHandler.setNext(outputHandler);
            lastHandler = outputHandler;
        }


        /*
         * Handler chain is in place - now we must use it for calling the parser and initiate the processing
         */
        if (templateMode.isHTML()) {
            this.htmlParser.parse(this.configuration, templateMode, templateResource, firstHandler);
        } else if (templateMode.isXML()) {
            this.xmlParser.parse(this.configuration, templateMode, templateResource, firstHandler);
        } else {
            throw new IllegalArgumentException(
                    "Cannot process template \"" + templateName + "\" with unsupported template mode: " + templateMode);
        }

    }



    
}
