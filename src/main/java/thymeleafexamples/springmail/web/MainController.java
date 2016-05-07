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
package thymeleafexamples.springmail.web;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.util.ClassLoaderUtils;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static thymeleafexamples.springmail.config.SpringWebInitializer.ENCODING;

@Controller
public class MainController {

    private static final String EDITABLE_TEMPLATE = "mail/email-editable.html";
    
    /* Home page. */
    @RequestMapping(value = {"/", "/index.html"}, method = GET)
    public String index() {
        return "index.html";
    }
    
    /* Simple HTML email. */
    @RequestMapping(value = "/simple.html", method = GET)
    public String simple() {
        return "simple.html";
    }
    
    /* HTML email with attachment. */
    @RequestMapping(value = "/attachment.html", method = GET)
    public String attachment() {
        return "attachment.html";
    }
    
    /* HTML email with inline image. */
    @RequestMapping(value = "/inline.html", method = GET)
    public String inline() {
        return "inline.html";
    }
    
    /* Editable HTML email. */
    @RequestMapping(value = "/editable.html", method = GET)
    public String editable(Model model) throws IOException {
        final ClassLoader cl = ClassLoaderUtils.getClassLoader(MainController.class);
        InputStream inputStream = cl.getResourceAsStream(EDITABLE_TEMPLATE);
        String baseTemplate = IOUtils.toString(inputStream, ENCODING);
        model.addAttribute("baseTemplate", baseTemplate);
        return "editable.html";
    }
    
    /* Sending confirmation page. */
    @RequestMapping(value = "/sent.html", method = GET)
    public String sent() {
        return "sent.html";
    }
    
}
