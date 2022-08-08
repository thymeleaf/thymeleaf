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
package org.thymeleaf.linkbuilder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.StringTemplateResolver;


public class SpringLinkBuilderTest {


    public SpringLinkBuilderTest() {
        super();
    }
    
    



    @Test
    public void testLinkBuilderSpring01() throws Exception {

        final Context ctx = new Context();
        ctx.setVariable("one", "This is one");

        final TemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(new StringTemplateResolver());
        templateEngine.setLinkBuilder(new TestLinkBuilder());
        final String result = templateEngine.process("<a th:href='@{/something}'>", ctx);

        final String expected = "<a href='[/fromthebuilder/something]'>";

        Assertions.assertEquals(expected,result);

    }




    @Test
    public void testLinkBuilderSpringWithECFactory01() throws Exception {

        final Context ctx = new Context();
        ctx.setVariable("one", "This is one");

        final TemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(new StringTemplateResolver());
        templateEngine.setLinkBuilder(new TestLinkBuilder());
        templateEngine.setEngineContextFactory(new TestEngineContextFactory());
        final String result = templateEngine.process("<a th:href='@{/something}'>", ctx);

        final String expected = "<a href='[ENGINE: /fromthebuilder/something]'>";

        Assertions.assertEquals(expected,result);

    }

    
    
}
