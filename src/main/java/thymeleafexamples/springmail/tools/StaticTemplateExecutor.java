/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package thymeleafexamples.springmail.tools;

import org.thymeleaf.context.IContext;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.util.Validate;

/**
 * Process a Thymeleaf template content provided as a String.
 */
public class StaticTemplateExecutor {
    
    private static final String TEMPLATE_NAME = "custom";

    private final String templateMode;
    
    private final IContext context;
    
    private final IMessageResolver messageResolver;
    
    public StaticTemplateExecutor(final IContext context, final IMessageResolver messageResolver, final String templateMode) {
        Validate.notNull(context, "Context must be non-null");
        Validate.notNull(templateMode, "Template mode must be non-null");
        Validate.notNull(messageResolver, "MessageResolver must be non-null");
        this.context = context;
        this.templateMode = templateMode;
        this.messageResolver = messageResolver;
    }
    
    public String processTemplateCode(final String code) {
        Validate.notNull(code, "Code must be non-null");
        ITemplateResolver templateResolver = new MemoryTemplateResolver(code, templateMode);
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setMessageResolver(messageResolver);
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.initialize();
        return templateEngine.process(TEMPLATE_NAME, context);
    }    
}
