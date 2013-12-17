/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring3.resourceresolver;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.context.Context;
import org.thymeleaf.testing.templateengine.util.ResourceUtils;
import org.thymeleaf.util.ClassLoaderUtils;


public final class SpringResourceResourceResolverSpring3Test {



    @Test
    public void testGetResourceAsStream() throws Exception {

        final String templateLocation = "spring321/view/test.html";

        final ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:spring321/view/applicationContext.xml");

        final SpringResourceResourceResolver resolver =
                (SpringResourceResourceResolver) context.getBean("springResourceResourceResolver");

        final TemplateProcessingParameters parameters =
                new TemplateProcessingParameters(new Configuration(), "test", new Context());

        final InputStream is =
                resolver.getResourceAsStream(parameters, "classpath:" + templateLocation);

        final String testResource =
                ResourceUtils.normalize(ResourceUtils.read(is, "US-ASCII"));

        final String expected =
                ResourceUtils.read(
                        ClassLoaderUtils.getClassLoader(SpringResourceResourceResolverSpring3Test.class).getResourceAsStream(templateLocation),
                        "US-ASCII", true);

        Assert.assertEquals(expected, testResource);

    }

    
}
