/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2022, The THYMELEAF team (http://www.thymeleaf.org)
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

package org.thymeleaf.spring5.view;

import java.util.Locale;

import org.springframework.web.servlet.View;

public final class ThymeleafViewResolverWrapper {

    /*
     * THIS CLASS IS NEEDED IN ORDER TO CALL ThymeleafViewResolver#loadView(...) WHICH IS PROTECTED
     */

    private ThymeleafViewResolverWrapper() {
        super();
    }

    public static View loadView(
            final ThymeleafViewResolver viewResolver, final String viewName, final Locale locale)
            throws Exception {
        return viewResolver.loadView(viewName, locale);
    }

}
