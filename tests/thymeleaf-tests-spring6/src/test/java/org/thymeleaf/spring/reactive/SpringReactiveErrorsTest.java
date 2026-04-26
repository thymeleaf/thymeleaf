/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.templateengine.springintegration.model.FormBean01;

public final class SpringReactiveErrorsTest extends AbstractSpringReactiveTest {


    @Test
    public void testDetailedErrors() throws Exception {

        final FormBean01 formBean = new FormBean01();
        formBean.setName("Mark Lettuce");

        final BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(formBean, "formBean");
        bindingResult.reject("no_code", "Global form error");
        bindingResult.rejectValue("name", "no_code", "Name is not valid");

        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("formBean", formBean);
        model.put(BindingResult.MODEL_KEY_PREFIX + "formBean", bindingResult);

        final IWebContext context = ReactiveTestUtils.buildReactiveContext(model);

        testTemplate("reactive-errors01", null, context, "reactive-errors01-01");

    }


}
