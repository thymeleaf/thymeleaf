/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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
package org.thymeleaf.templateresolver;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;

public class TemplateResolverDecoupledLogicTest {


    @Test
    public void testDecoupledLogicOnlyForMarkupTemplateModes() {

        final IEngineConfiguration configuration = new TemplateEngine().getConfiguration();

        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setXmlTemplateModePatterns(Collections.singleton("*.xml"));
        templateResolver.setTextTemplateModePatterns(Collections.singleton("*.txt"));
        templateResolver.setJavaScriptTemplateModePatterns(Collections.singleton("*.js"));
        templateResolver.setCSSTemplateModePatterns(Collections.singleton("*.css"));
        templateResolver.setRawTemplateModePatterns(Collections.singleton("*.raw"));

        templateResolver.setUseDecoupledLogic(true);
        Assertions.assertTrue(templateResolver.resolveTemplate(configuration, null, "page", null).getUseDecoupledLogic());
        Assertions.assertTrue(templateResolver.resolveTemplate(configuration, null, "doc.xml", null).getUseDecoupledLogic());
        Assertions.assertFalse(templateResolver.resolveTemplate(configuration, null, "report.txt", null).getUseDecoupledLogic());
        Assertions.assertFalse(templateResolver.resolveTemplate(configuration, null, "app.js", null).getUseDecoupledLogic());
        Assertions.assertFalse(templateResolver.resolveTemplate(configuration, null, "style.css", null).getUseDecoupledLogic());
        Assertions.assertFalse(templateResolver.resolveTemplate(configuration, null, "data.raw", null).getUseDecoupledLogic());

        templateResolver.setUseDecoupledLogic(false);
        Assertions.assertFalse(templateResolver.resolveTemplate(configuration, null, "page", null).getUseDecoupledLogic());

    }


}
