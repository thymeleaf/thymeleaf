/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2022, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring.templateresolver;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.testing.templateengine.util.ResourceUtils;
import org.thymeleaf.util.ClassLoaderUtils;


public final class SpringResourceTemplateResolverTest {



    @Test
    public void testResolveTemplate() throws Exception {

        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        final IEngineConfiguration configuration = templateEngine.getConfiguration();

        final String templateLocation = "spring/templateresolver/test.html";

        final ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:spring/templateresolver/applicationContext.xml");

        final SpringResourceTemplateResolver resolver =
                (SpringResourceTemplateResolver) context.getBean("springResourceTemplateResolver");

        final TemplateMode templateMode = resolver.getTemplateMode();
        Assertions.assertEquals(TemplateMode.HTML, templateMode);

        final TemplateResolution resolution = resolver.resolveTemplate(configuration, null, "classpath:" + templateLocation, null);

        final ITemplateResource templateResource = resolution.getTemplateResource();

        final String templateResourceStr = IOUtils.toString(templateResource.reader());

        final String testResource = templateResourceStr.replace("\r","");

        final String expected =
                ResourceUtils.read(
                        ClassLoaderUtils.getClassLoader(SpringResourceTemplateResolverTest.class).getResourceAsStream(templateLocation),
                        "US-ASCII", true).replace("\r","");

        Assertions.assertEquals(expected, testResource);

    }

    
}
