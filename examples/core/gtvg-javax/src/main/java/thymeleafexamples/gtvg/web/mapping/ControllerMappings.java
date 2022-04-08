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
package thymeleafexamples.gtvg.web.mapping;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.web.IWebRequest;
import thymeleafexamples.gtvg.web.controller.HomeController;
import thymeleafexamples.gtvg.web.controller.IGTVGController;
import thymeleafexamples.gtvg.web.controller.OrderDetailsController;
import thymeleafexamples.gtvg.web.controller.OrderListController;
import thymeleafexamples.gtvg.web.controller.ProductCommentsController;
import thymeleafexamples.gtvg.web.controller.ProductListController;
import thymeleafexamples.gtvg.web.controller.SubscribeController;
import thymeleafexamples.gtvg.web.controller.UserProfileController;


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
        final String path = request.getPathWithinApplication();
        return controllersByURL.get(path);
    }

    private ControllerMappings() {
        super();
    }


}
