/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.parsing;

import org.junit.Assert;
import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.testing.templateengine.util.ResourceUtils;
import org.thymeleaf.util.ClassLoaderUtils;


public class Parsing03Test {


    public Parsing03Test() {
        super();
    }
    
    
    
    @Test
    public void testParsing() throws Exception {

        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding("UTF-16");

        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        final String result = templateEngine.process("parsing/parsingtest03.bulk", new Context());

        final String expected = 
                ResourceUtils.read(
                        ClassLoaderUtils.getClassLoader(Parsing03Test.class).getResourceAsStream("parsing/parsingtest03-result.bulk"),
                        "ISO-8859-1",
                        true);
        
        Assert.assertEquals(expected, ResourceUtils.normalize(result));
        
    }
    

    
}
