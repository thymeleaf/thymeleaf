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
package org.thymeleaf.context;

import java.util.Collections;
import java.util.Set;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dialect.IDialect;


public final class TestTemplateEngineConfigurationBuilder {



    public static IEngineConfiguration build() {
        final TemplateEngine templateEngine = new TemplateEngine();
        return templateEngine.getConfiguration();
    }


    public static IEngineConfiguration build(final IDialect dialect) {
        return build(Collections.singleton(dialect));
    }


    public static IEngineConfiguration build(final Set<IDialect> dialects) {
        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setDialects(dialects);
        return templateEngine.getConfiguration();
    }


    private TestTemplateEngineConfigurationBuilder() {
        super();
    }

}
