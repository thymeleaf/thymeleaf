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
package org.thymeleaf.templateengine.springintegration.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring5.util.SpringRequestUtils;
import org.thymeleaf.testing.templateengine.util.JavaxServletMockUtils;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.IWebRequest;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;


public final class SpringRequestUtilsTest {

    private static final ServletContext SERVLET_CONTEXT = JavaxServletMockUtils.buildServletContext().build();
    private static final HttpServletResponse HTTP_SERVLET_RESPONSE = JavaxServletMockUtils.buildResponse().build();


    @Test
    public void testCheckViewNameNotInRequest() {

        SpringRequestUtils.checkViewNameNotInRequest("lala", mockRequest("alala"));
        SpringRequestUtils.checkViewNameNotInRequest("lala :: le", mockRequest("a/elala::le//"));
        SpringRequestUtils.checkViewNameNotInRequest("lala :: le", mockRequest("a/elala :: le//"));
        SpringRequestUtils.checkViewNameNotInRequest("${lala} :: le", mockRequest("a/elala::le//"));
        SpringRequestUtils.checkViewNameNotInRequest("${lala} :: le", mockRequest("a/elala :: le//"));
        Assertions.assertThrows(TemplateProcessingException.class, () -> SpringRequestUtils.checkViewNameNotInRequest("${lala} :: le", mockRequest("a/e${lala}::le//")));
        Assertions.assertThrows(TemplateProcessingException.class, () -> SpringRequestUtils.checkViewNameNotInRequest("${lala} :: le", mockRequest("a/e${lala} :: le//")));
        Assertions.assertThrows(TemplateProcessingException.class, () -> SpringRequestUtils.checkViewNameNotInRequest("${lala} :: le", mockRequest("a/e${x} :: le//")));
        Assertions.assertThrows(TemplateProcessingException.class, () -> SpringRequestUtils.checkViewNameNotInRequest("${lala} :: le (a=23)", mockRequest("a/e${lala} :: le//")));
        SpringRequestUtils.checkViewNameNotInRequest("${lala} :: le", mockRequest("a/e","p0","${lala} :: le//"));
        Assertions.assertThrows(TemplateProcessingException.class, () -> SpringRequestUtils.checkViewNameNotInRequest("${lala} :: le", mockRequest("a/e","p0","${lala} :: le")));
        SpringRequestUtils.checkViewNameNotInRequest("${lala} :: le", mockRequest("a/e","p0","${lala}_le//"));
        SpringRequestUtils.checkViewNameNotInRequest("${lala}::le", mockRequest("a/e","p0","${lili}::le"));

    }


    private IWebRequest mockRequest(final String path, final String... params) {

        final Map<String,String[]> paramsMap = new HashMap<>();
        if (params != null && params.length > 0 && (params.length % 2 == 0)) {
            for (int i = 0; i < params.length; i+=2) {
                paramsMap.put(params[i], new String[] {params[i+1]});
            }
        }

        final HttpServletRequest request =
                JavaxServletMockUtils.buildRequest(SERVLET_CONTEXT, path).parameterMap(paramsMap).build();

        final IWebExchange webExchange =
                JavaxServletWebApplication.buildApplication(SERVLET_CONTEXT)
                        .buildExchange(request, HTTP_SERVLET_RESPONSE);

        return webExchange.getRequest();

    }


}
