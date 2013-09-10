/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring3.view;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.servlet.View;

public class ThymeleafViewResolverTest {



    @Test
    public void testConfigureViewBean() throws Exception {

        final ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:spring3/view/applicationContext.xml");

        final ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setApplicationContext(context);
        resolver.setViewClass(TestThymeleafView.class);

        final View view = resolver.loadView("testview", Locale.US);

        final String viewValue = ((TestThymeleafView)view).getSomething();
        Assert.assertEquals("view_value", viewValue);

        final TestThymeleafView.ViewBean viewBean = ((TestThymeleafView)view).getViewBean();
        Assert.assertNotNull(viewBean);

        final String beanValue = viewBean.getValue();
        Assert.assertEquals("bean_value", beanValue);

    }


    @Test
    public void testConfigureViewNoBean() throws Exception {

        final ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:spring3/view/applicationContext.xml");

        final ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setApplicationContext(context);
        resolver.setViewClass(TestThymeleafView.class);

        // testview2 does not exist as a declared bean at the application context
        final View view = resolver.loadView("testview2", Locale.US);

        // There's no matching view definition, so "something" should not be populated
        final String viewValue = ((TestThymeleafView)view).getSomething();
        Assert.assertNull(viewValue);

        final TestThymeleafView.ViewBean viewBean = ((TestThymeleafView)view).getViewBean();
        Assert.assertNotNull(viewBean);

        // This should be there, because it is applied through an @Autowired annotation
        final String beanValue = viewBean.getValue();
        Assert.assertEquals("bean_value", beanValue);

    }


}
