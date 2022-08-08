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
package org.thymeleaf.templateparser.decoupled.gtvg;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.linkbuilder.StandardLinkBuilder;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.testing.templateengine.util.ResourceUtils;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.DateUtils;


public class DecoupledGTVGTest {



    public DecoupledGTVGTest() {
        super();
    }




    @Test
    public void testGTVGHome() throws Exception {

        final Context ctx = new Context();
        ctx.setVariable("user", new User("John", "Apricot", "Antarctica", null));
        ctx.setVariable("today", DateUtils.create(2016, 02, 18));

        test("home", "templateparser/decoupled/gtvg/result/home.html", ctx);

    }



    @Test
    public void testGTVGSubscribe() throws Exception {

        test("subscribe", "templateparser/decoupled/gtvg/result/subscribe.html", new Context());

    }






    private static void test(final String templateName, final String expectedResultLocation, final IContext context) throws Exception {


        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        templateResolver.setTemplateMode("HTML");
        templateResolver.setPrefix("templateparser/decoupled/gtvg/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setUseDecoupledLogic(true);


        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.setLinkBuilder(new StandardLinkBuilder() {
            @Override
            protected String computeContextPath(final IExpressionContext context, final String base, final Map<String, Object> parameters) {
                return "";
            }
        });


        final String result = templateEngine.process(templateName, context);

        final String expected =
                ResourceUtils.read(
                        ClassLoaderUtils.getClassLoader(DecoupledGTVGTest.class).getResourceAsStream(expectedResultLocation),
                        "ISO-8859-1",
                        true);

        Assertions.assertEquals(ResourceUtils.normalize(expected), ResourceUtils.normalize(result));


    }



}
