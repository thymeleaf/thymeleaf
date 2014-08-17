/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package thymeleafexamples.gtvg.web.application;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import thymeleafexamples.gtvg.web.controller.HomeController;
import thymeleafexamples.gtvg.web.controller.IGTVGController;
import thymeleafexamples.gtvg.web.controller.OrderDetailsController;
import thymeleafexamples.gtvg.web.controller.OrderListController;
import thymeleafexamples.gtvg.web.controller.ProductCommentsController;
import thymeleafexamples.gtvg.web.controller.ProductListController;
import thymeleafexamples.gtvg.web.controller.SubscribeController;
import thymeleafexamples.gtvg.web.controller.UserProfileController;


public class GTVGApplication {

    
    private static Map<String, IGTVGController> controllersByURL;
    private static TemplateEngine templateEngine;
    
    
    
    static {
        initializeControllersByURL();
        initializeTemplateEngine();
    }
    
    
    private static void initializeTemplateEngine() {
        
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver();
        
        // XHTML is the default mode, but we will set it anyway for better understanding of code
        templateResolver.setTemplateMode("XHTML");
        // This will convert "home" to "/WEB-INF/templates/home.html"
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        // Set template cache TTL to 1 hour. If not set, entries would live in cache until expelled by LRU
        templateResolver.setCacheTTLMs(Long.valueOf(3600000L));
        
        // Cache is set to true by default. Set to false if you want templates to
        // be automatically updated when modified.
        templateResolver.setCacheable(true);
        
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        
    }
    
    
    private static Map<String, IGTVGController> initializeControllersByURL() {
        
        controllersByURL = new HashMap<String, IGTVGController>();
        controllersByURL.put("/", new HomeController());
        controllersByURL.put("/product/list", new ProductListController());
        controllersByURL.put("/product/comments", new ProductCommentsController());
        controllersByURL.put("/order/list", new OrderListController());
        controllersByURL.put("/order/details", new OrderDetailsController());
        controllersByURL.put("/subscribe", new SubscribeController());
        controllersByURL.put("/userprofile", new UserProfileController());
        
        return controllersByURL;
        
    }

    
    public static IGTVGController resolveControllerForRequest(final HttpServletRequest request) {
        final String path = getRequestPath(request);
        return controllersByURL.get(path);
    }
    
    
    public static TemplateEngine getTemplateEngine() {
        return templateEngine;
    }

    
    
    private static String getRequestPath(final HttpServletRequest request) {
        
        String requestURI = request.getRequestURI();
        final String contextPath = request.getContextPath();
        
        final int fragmentIndex = requestURI.indexOf(';'); 
        if (fragmentIndex != -1) {
            requestURI = requestURI.substring(0, fragmentIndex);
        }
        
        if (requestURI.startsWith(contextPath)) {
            return requestURI.substring(contextPath.length());
        }
        return requestURI;
    }
    
    
    
    private GTVGApplication() {
        super();
    }
    
    
}
