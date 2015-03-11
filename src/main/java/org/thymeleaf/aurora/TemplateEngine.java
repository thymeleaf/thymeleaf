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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.thymeleaf.aurora.context.ITemplateEngineContext;
import org.thymeleaf.aurora.context.ITemplateProcessingContext;
import org.thymeleaf.aurora.context.TemplateEngineContext;
import org.thymeleaf.aurora.context.TemplateProcessingContext;
import org.thymeleaf.aurora.dialect.IDialect;
import org.thymeleaf.aurora.engine.ITemplateHandler;
import org.thymeleaf.aurora.engine.OutputTemplateHandler;
import org.thymeleaf.aurora.engine.ProcessorTemplateHandler;
import org.thymeleaf.aurora.parser.HTMLTemplateParser;
import org.thymeleaf.aurora.parser.ITemplateParser;
import org.thymeleaf.aurora.parser.XMLTemplateParser;
import org.thymeleaf.aurora.resource.IResource;
import org.thymeleaf.aurora.standard.StandardDialect;
import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.aurora.text.ITextRepository;
import org.thymeleaf.aurora.text.TextRepositories;
import org.thymeleaf.util.Validate;


/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class TemplateEngine implements ITemplateEngine {

    private static final Set<IDialect> STANDARD_DIALECT_SET;

    private final ITemplateEngineContext templateEngineContext;
    private final ITemplateParser htmlParser;
    private final ITemplateParser xmlParser;



    static {
        final Set<IDialect> newStandardDialectSet = new HashSet<IDialect>(2,1.0f);
        newStandardDialectSet.add(new StandardDialect());
        STANDARD_DIALECT_SET = Collections.unmodifiableSet(newStandardDialectSet);
    }



    public TemplateEngine() {
        this(STANDARD_DIALECT_SET, TextRepositories.createLimitedSizeCacheRepository());
    }


    public TemplateEngine(final Set<IDialect> dialects, final ITextRepository textRepository) {

        super();

        Validate.notNull(dialects, "Dialect list cannot be null");

        final ITextRepository engineTextRepository =
                (textRepository != null? textRepository : TextRepositories.createNoCacheRepository());

        this.templateEngineContext = new TemplateEngineContext(dialects, engineTextRepository);
        this.htmlParser = new HTMLTemplateParser(40,2048);
        this.xmlParser = new XMLTemplateParser(40, 2048);

    }






    public void process(
            final TemplateMode templateMode, final String templateName, final IResource templateResource,
            final Writer writer) {

        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.notNull(templateName, "Template name cannot be null");
        Validate.notNull(templateResource, "Template resource cannot be null");
        Validate.notNull(writer, "Writer cannot be null");

        final ITemplateProcessingContext templatePocessingContext =
                new TemplateProcessingContext(this.templateEngineContext, templateName, templateMode);

        final ProcessorTemplateHandler processorHandler = new ProcessorTemplateHandler(templatePocessingContext);
        final OutputTemplateHandler outputHandler = new OutputTemplateHandler(templateName, writer);

        processorHandler.setNext(outputHandler);

        final ITemplateHandler handlerChain = processorHandler;

        if (templateMode.isHTML()) {
            this.htmlParser.parse(this.templateEngineContext, templateMode, templateResource, handlerChain);
        } else if (templateMode.isXML()) {
            this.xmlParser.parse(this.templateEngineContext, templateMode, templateResource, handlerChain);
        } else {
            throw new IllegalArgumentException(
                    "Cannot process template \"" + templateName + "\" with unsupported template mode: " + templateMode);
        }

    }



    
}
