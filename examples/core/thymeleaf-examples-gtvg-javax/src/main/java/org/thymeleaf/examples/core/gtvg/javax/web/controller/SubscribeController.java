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
package org.thymeleaf.examples.core.gtvg.javax.web.controller;

import java.io.Writer;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;

public class SubscribeController implements IGTVGController {

    
    public SubscribeController() {
        super();
    }
    
    
    public void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer)
            throws Exception {
        
        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
        templateEngine.process("subscribe", ctx, writer);
        
    }

}
