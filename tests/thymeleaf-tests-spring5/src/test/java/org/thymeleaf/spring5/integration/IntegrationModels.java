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
package org.thymeleaf.spring5.integration;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

// Model contents shared by the MVC and WebFlux controllers
public final class IntegrationModels {

    public static final LocalDate DATE = LocalDate.of(2026, 11, 11);
    public static final List<String> ALL_FEATURES = Arrays.asList("fast", "cheap", "green");
    public static final List<String> ALL_SIZES = Arrays.asList("S", "M", "L");


    public static void links(final Model model) {
        model.addAttribute("v", "a b&c");
        model.addAttribute("id", Integer.valueOf(12));
        model.addAttribute("url", "/dyn/path");
        model.addAttribute("list", Arrays.asList(Integer.valueOf(1), Integer.valueOf(2)));
    }


    public static void spel(final Model model) {
        model.addAttribute("numbers", Arrays.asList(1, 2, 3, 4, 5, 6));
        model.addAttribute("people", Arrays.asList(new Person("Ann", 30), new Person("Bob", 40)));
        model.addAttribute("map", Collections.singletonMap("k", "v"));
        model.addAttribute("date", DATE);
    }


    public static void inlineJavaScript(final Model model) {
        model.addAttribute("person", new Person("Ann <x>", 30));
        model.addAttribute("date", DATE);
        model.addAttribute("text", "a</script>b`${c}");
    }


    public static void textTemplates(final Model model) {
        model.addAttribute("name", "World");
        model.addAttribute("items", Arrays.asList("one", "two"));
    }


    public static void order(final Model model, final OrderForm orderForm) {
        model.addAttribute("orderForm", orderForm);
        model.addAttribute("allFeatures", ALL_FEATURES);
        model.addAttribute("allSizes", ALL_SIZES);
    }


    public static void validateOrder(final OrderForm orderForm, final BindingResult bindingResult) {
        if (orderForm.getFeatures() == null || orderForm.getFeatures().isEmpty()) {
            bindingResult.reject("order.noFeatures");
        }
        for (int i = 0; i < orderForm.getItems().size(); i++) {
            if (!StringUtils.hasText(orderForm.getItems().get(i).getName())) {
                bindingResult.rejectValue("items[" + i + "].name", "required");
            }
        }
    }


    public static String fragmentSelection(final String which) {
        return "a".equals(which) ? "common/header :: plain" : "common/header :: #byid";
    }


    private IntegrationModels() {
        super();
    }

}
