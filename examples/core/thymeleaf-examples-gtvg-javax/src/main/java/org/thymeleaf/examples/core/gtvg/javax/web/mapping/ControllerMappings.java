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
package org.thymeleaf.examples.core.gtvg.javax.web.mapping;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.examples.core.gtvg.javax.web.controller.HomeController;
import org.thymeleaf.examples.core.gtvg.javax.web.controller.IGTVGController;
import org.thymeleaf.examples.core.gtvg.javax.web.controller.OrderDetailsController;
import org.thymeleaf.examples.core.gtvg.javax.web.controller.ProductCommentsController;
import org.thymeleaf.examples.core.gtvg.javax.web.controller.ProductListController;
import org.thymeleaf.examples.core.gtvg.javax.web.controller.SubscribeController;
import org.thymeleaf.examples.core.gtvg.javax.web.controller.UserProfileController;
import org.thymeleaf.web.IWebRequest;
import org.thymeleaf.examples.core.gtvg.javax.web.controller.OrderListController;


public class ControllerMappings {


    private static Map<String, IGTVGController> controllersByURL;


    static {
        controllersByURL = new HashMap<String, IGTVGController>();
        controllersByURL.put("/", new HomeController());
        controllersByURL.put("/product/list", new ProductListController());
        controllersByURL.put("/product/comments", new ProductCommentsController());
        controllersByURL.put("/order/list", new OrderListController());
        controllersByURL.put("/order/details", new OrderDetailsController());
        controllersByURL.put("/subscribe", new SubscribeController());
        controllersByURL.put("/userprofile", new UserProfileController());
    }
    

    
    public static IGTVGController resolveControllerForRequest(final IWebRequest request) {
        final String path = getRequestPath(request);
        return controllersByURL.get(path);
    }


    // Path within application might contain the ";jsessionid" fragment due to URL rewriting
    private static String getRequestPath(final IWebRequest request) {

        String requestPath = request.getPathWithinApplication();

        final int fragmentIndex = requestPath.indexOf(';');
        if (fragmentIndex != -1) {
            requestPath = requestPath.substring(0, fragmentIndex);
        }

        return requestPath;

    }

    private ControllerMappings() {
        super();
    }


}
