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
public interface ITemplateEngine {


    public void process(
            final TemplateMode templateMode, final String templateName, final IResource templateResource,
            final Writer writer);

    
}
