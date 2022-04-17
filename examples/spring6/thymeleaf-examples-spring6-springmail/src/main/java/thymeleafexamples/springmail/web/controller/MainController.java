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
package thymeleafexamples.springmail.web.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import thymeleafexamples.springmail.business.service.EmailService;

@Controller
public class MainController {

    private static final String EDITABLE_TEMPLATE = "mail/editablehtml/email-editable.html";

    @Autowired
    private EmailService emailService;

    /* Home page. */
    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    /* Plain text email. */
    @RequestMapping(value = "/text.html", method = RequestMethod.GET)
    public String text() {
        return "text";
    }

    /* Simple HTML email. */
    @RequestMapping(value = "/simple.html", method = RequestMethod.GET)
    public String simple() {
        return "simple";
    }

    /* HTML email with attachment. */
    @RequestMapping(value = "/attachment.html", method = RequestMethod.GET)
    public String attachment() {
        return "attachment";
    }

    /* HTML email with inline image. */
    @RequestMapping(value = "/inline.html", method = RequestMethod.GET)
    public String inline() {
        return "inline";
    }

    /* Editable HTML email. */
    @RequestMapping(value = "/editable.html", method = RequestMethod.GET)
    public String editable(final Model model) throws IOException {
        model.addAttribute("baseTemplate", this.emailService.getEditableMailTemplate());
        return "editable";
    }

    /* Sending confirmation page. */
    @RequestMapping(value = "/sent.html", method = RequestMethod.GET)
    public String sent() {
        return "sent";
    }

}
