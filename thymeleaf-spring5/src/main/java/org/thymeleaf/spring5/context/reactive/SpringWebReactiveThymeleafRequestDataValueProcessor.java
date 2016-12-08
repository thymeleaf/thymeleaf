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
package org.thymeleaf.spring5.context.reactive;

import java.util.Map;

import org.thymeleaf.spring5.context.IThymeleafRequestDataValueProcessor;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
class SpringWebReactiveThymeleafRequestDataValueProcessor implements IThymeleafRequestDataValueProcessor {

    /*
     * TODO * The RequestDataValueProcessor mechanism is not yet implemented in Spring Web Reactive.
     * TODO   For the moment, all methods here are basically no-ops.
     */

    SpringWebReactiveThymeleafRequestDataValueProcessor() {
        super();
    }

    @Override
    public String processAction(final String action, final String httpMethod) {
        return action;
    }

    @Override
    public String processFormFieldValue(final String name, final String value, final String type) {
        return value;
    }

    @Override
    public Map<String, String> getExtraHiddenFields() {
        return null;
    }

    @Override
    public String processUrl(final String url) {
        return url;
    }

}
