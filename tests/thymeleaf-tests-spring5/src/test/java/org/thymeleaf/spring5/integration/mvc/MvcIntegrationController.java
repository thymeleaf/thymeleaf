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
package org.thymeleaf.spring5.integration.mvc;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.spring5.integration.Code;
import org.thymeleaf.spring5.integration.FormBean;
import org.thymeleaf.spring5.integration.IntegrationModels;
import org.thymeleaf.spring5.integration.OrderForm;

@Controller
public class MvcIntegrationController {


    private static final List<String> COLORS = Arrays.asList("red", "green", "blue");


    @GetMapping(path = "/hello", name = "helloMapping")
    public String hello(final Model model) {
        model.addAttribute("name", "World");
        model.addAttribute("code", new Code("abc"));
        model.addAttribute("items", Arrays.asList("one", "two"));
        return "hello";
    }


    @GetMapping("/messages")
    public String messages(final Model model) {
        model.addAttribute("dynamicKey", "greeting");
        return "messages";
    }


    @GetMapping("/form")
    public String form(final Model model) {
        model.addAttribute("formBean", FormBean.initial());
        model.addAttribute("colors", COLORS);
        return "form";
    }


    @PostMapping("/form")
    public String saveForm(
            @ModelAttribute("formBean") final FormBean formBean, final BindingResult bindingResult, final Model model) {
        if (!StringUtils.hasText(formBean.getName())) {
            bindingResult.rejectValue("name", "required");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("colors", COLORS);
            return "form";
        }
        return "redirect:/form?saved=" + formBean.getName();
    }


    @GetMapping("/fragment")
    public String fragment(final Model model) {
        model.addAttribute("name", "F");
        return "fragments :: main";
    }


    @GetMapping("/fragment-expression")
    public String fragmentExpression(final Model model) {
        model.addAttribute("name", "F");
        model.addAttribute("fragName", "main");
        return "fragments :: ${fragName}";
    }


    @GetMapping("/fragment-selector")
    public String fragmentSelector(@RequestParam("f") final String selector, final Model model) {
        model.addAttribute("name", "F");
        model.addAttribute("fragName", "main");
        return "fragments :: " + selector;
    }


    @GetMapping("/extra")
    public String extra() {
        return "extra-page";
    }


    @GetMapping("/redirect")
    public String redirect() {
        return "redirect:/hello";
    }


    @GetMapping("/forward")
    public String forward() {
        return "forward:/hello";
    }


    @GetMapping("/attributes")
    public String attributes(final HttpServletRequest request, final HttpSession session) {
        session.setAttribute("s", "session-value");
        request.setAttribute("requestAttr", "request-value");
        return "attributes";
    }


    @GetMapping("/links")
    public String links(final Model model) {
        IntegrationModels.links(model);
        return "links";
    }


    @GetMapping("/links-href-param")
    public String linksHrefParam() {
        return "links-href-param";
    }


    @GetMapping("/links-utext-param")
    public String linksUtextParam() {
        return "links-utext-param";
    }


    @GetMapping("/layout-page")
    public String layoutPage(final Model model) {
        model.addAttribute("name", "Layout content");
        return "layout-page";
    }


    @GetMapping("/layout-page-empty")
    public String layoutPageEmpty(final Model model) {
        model.addAttribute("name", "Layout content");
        return "layout-page-empty";
    }


    @GetMapping("/fragments-misc")
    public String fragmentsMisc(final Model model) {
        model.addAttribute("name", "N");
        model.addAttribute("cond", Boolean.TRUE);
        return "fragments-misc";
    }


    @GetMapping("/fragment-params")
    public String fragmentParams(final Model model) {
        model.addAttribute("t", "Param title");
        return "common/header :: header(title=${t})";
    }


    @GetMapping("/fragment-section")
    public String fragmentSection(final Model model) {
        model.addAttribute("name", "Section");
        return "layout-page :: main";
    }


    @GetMapping("/fragment-select")
    public String fragmentSelect(@RequestParam("which") final String which) {
        return IntegrationModels.fragmentSelection(which);
    }


    @GetMapping("/uses-extra")
    public String usesExtra() {
        return "uses-extra";
    }


    @GetMapping("/decoupled")
    public String decoupled(final Model model) {
        model.addAttribute("name", "From decoupled logic");
        return "decoupled";
    }


    @GetMapping("/layout-dialect")
    public String layoutDialect(final Model model) {
        model.addAttribute("name", "Layout Dialect content");
        return "ld/page";
    }


    @GetMapping("/order")
    public String order(final Model model) {
        IntegrationModels.order(model, OrderForm.initial());
        return "order";
    }


    @PostMapping("/order")
    public String saveOrder(
            @ModelAttribute("orderForm") final OrderForm orderForm, final BindingResult bindingResult, final Model model) {
        IntegrationModels.validateOrder(orderForm, bindingResult);
        if (bindingResult.hasErrors()) {
            IntegrationModels.order(model, orderForm);
            return "order";
        }
        return "redirect:/order?saved";
    }


    @GetMapping("/spel")
    public String spel(final Model model) {
        IntegrationModels.spel(model);
        return "spel";
    }


    @GetMapping("/report.txt")
    public String report(final Model model) {
        IntegrationModels.textTemplates(model);
        return "report.txt";
    }


    @GetMapping("/app.js")
    public String appJs(final Model model) {
        IntegrationModels.textTemplates(model);
        return "app.js";
    }


    @GetMapping("/inline-js")
    public String inlineJs(final Model model) {
        IntegrationModels.inlineJavaScript(model);
        return "inline-js";
    }


    @GetMapping(path = "/links/{id}", name = "linkMapping")
    public String linkTarget(@PathVariable("id") final String id) {
        return "hello";
    }


    @GetMapping("/static-links")
    public String staticLinks() {
        return "static-links";
    }

}
