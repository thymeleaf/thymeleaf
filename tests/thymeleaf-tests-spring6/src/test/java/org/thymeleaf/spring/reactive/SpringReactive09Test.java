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
package org.thymeleaf.spring.reactive;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.result.view.RequestDataValueProcessor;
import org.springframework.web.server.ServerWebExchange;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.spring.reactive.data.Album;

public final class SpringReactive09Test extends AbstractSpringReactiveTest {



    @Test
    public void testEmptyNameFormBean() throws Exception {

        final Album album = new Album(1, null);

        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("album", album);

        final IWebContext context =
                ReactiveTestUtils.buildReactiveContext(model, new TestingRequestDataValueProcessor());

        testTemplate("reactive09", null, context, "reactive09-01");

    }


    @Test
    public void testFullFormBean() throws Exception {

        final Album album = new Album(100, "Whatever");

        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("album", album);

        final IWebContext context =
                ReactiveTestUtils.buildReactiveContext(model, new TestingRequestDataValueProcessor());

        testTemplate("reactive09", null, context, "reactive09-02");

    }



    static class TestingRequestDataValueProcessor implements RequestDataValueProcessor {


        @Override
        public String processAction(final ServerWebExchange exchange, final String action, final String httpMethod) {
            return "{" + action + "}";
        }

        @Override
        public String processFormFieldValue(final ServerWebExchange exchange, final String name, final String value, final String type) {
            return "{" + value + "}";
        }

        @Override
        public Map<String, String> getExtraHiddenFields(final ServerWebExchange exchange) {
            return Collections.singletonMap("onehiddenfield", "onehiddenvalue");
        }

        @Override
        public String processUrl(final ServerWebExchange exchange, final String url) {
            return "{" + url + "}";
        }

    }



}
