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
package org.thymeleaf.offline;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.util.ClassLoaderUtils;





public class OfflineTest {
    
    
    public OfflineTest() {
        super();
    }
    
    
    
    @Test
    public void testOffline01() throws Exception {

        final Context ctx = new Context();
        ctx.setVariable("one", "This is one");
        
        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(new ClassLoaderTemplateResolver());
        final String result = templateEngine.process("offline/offline01.html", ctx);

        final String expected = 
                IOUtils.toString(ClassLoaderUtils.getClassLoader(OfflineTest.class).getResourceAsStream("offline/offline01-result.html"));
        
        Assert.assertEquals(expected, result);
        
    }
    
    
    
    @Test
    public void testOfflineSpring01() throws Exception {

        final Context ctx = new Context();
        ctx.setVariable("one", "This is one");
        
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(new ClassLoaderTemplateResolver());
        final String result = templateEngine.process("offline/offlinespring01.html", ctx);

        final String expected = 
                IOUtils.toString(ClassLoaderUtils.getClassLoader(OfflineTest.class).getResourceAsStream("offline/offlinespring01-result.html"));
        
        Assert.assertEquals(expected, result);
        
    }

    
    
    
}
