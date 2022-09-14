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
package org.thymeleaf.templateresolver;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;


public final class TemplateResolverAttributesTest {

    private static final String EXPECTED_OUTPUT = "<div><p>hello</p></div>";


    public final static Map<String,Object> EXPECTED_ATTRIBUTES1;
    public final static Map<String,Object> EXPECTED_ATTRIBUTES2;
    public final static Map<String,Object> EXPECTED_ATTRIBUTES3;


    static {

        EXPECTED_ATTRIBUTES1 = new HashMap<String, Object>();
        EXPECTED_ATTRIBUTES1.put("two", "second attribute");
        EXPECTED_ATTRIBUTES1.put("one", Integer.valueOf(145123));

        EXPECTED_ATTRIBUTES2 = new HashMap<String, Object>();
        EXPECTED_ATTRIBUTES2.put("one", "just one vale");

        EXPECTED_ATTRIBUTES3 = null;

    }



    @Test
    public void testTemplateResolutionAttributes01() throws Exception {


        final AttributeTesterTemplateResolver templateResolver = new AttributeTesterTemplateResolver(EXPECTED_ATTRIBUTES1);

        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        final TemplateSpec templateSpec = new TemplateSpec("temp", EXPECTED_ATTRIBUTES1);

        final String output = templateEngine.process(templateSpec, new Context());

        Assertions.assertTrue(templateResolver.tempCalled);
        Assertions.assertTrue(templateResolver.fragCalled);
        Assertions.assertEquals(EXPECTED_OUTPUT, output);

    }



    @Test
    public void testTemplateResolutionAttributes02() throws Exception {


        final AttributeTesterTemplateResolver templateResolver = new AttributeTesterTemplateResolver(EXPECTED_ATTRIBUTES2);

        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        final TemplateSpec templateSpec = new TemplateSpec("temp", EXPECTED_ATTRIBUTES2);

        final String output = templateEngine.process(templateSpec, new Context());

        Assertions.assertTrue(templateResolver.tempCalled);
        Assertions.assertTrue(templateResolver.fragCalled);
        Assertions.assertEquals(EXPECTED_OUTPUT, output);

    }



    @Test
    public void testTemplateResolutionAttributes03() throws Exception {


        final AttributeTesterTemplateResolver templateResolver = new AttributeTesterTemplateResolver(EXPECTED_ATTRIBUTES3);

        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        final TemplateSpec templateSpec = new TemplateSpec("temp", EXPECTED_ATTRIBUTES3);

        final String output = templateEngine.process(templateSpec, new Context());

        Assertions.assertTrue(templateResolver.tempCalled);
        Assertions.assertTrue(templateResolver.fragCalled);
        Assertions.assertEquals(EXPECTED_OUTPUT, output);

    }




}
