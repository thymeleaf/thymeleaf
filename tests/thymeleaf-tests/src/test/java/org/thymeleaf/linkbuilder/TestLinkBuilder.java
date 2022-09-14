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
package org.thymeleaf.linkbuilder;

import java.util.Map;

import org.thymeleaf.context.IExpressionContext;


public class TestLinkBuilder extends StandardLinkBuilder {

    public TestLinkBuilder() {
        super();
    }

    @Override
    protected String processLink(final IExpressionContext context, final String link) {
        if (context instanceof TestEngineContext) {
            return "[" + ((TestEngineContext) context).getLinkPrefix() + link + "]";
        }
        return "[" + link + "]";
    }

    @Override
    protected String computeContextPath(final IExpressionContext context, final String base, final Map<String, Object> parameters) {
        return "/fromthebuilder";
    }
}
