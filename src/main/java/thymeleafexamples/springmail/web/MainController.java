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
package thymeleafexamples.springmail.web;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.thymeleaf.util.ClassLoaderUtils;
import static thymeleafexamples.springmail.config.SpringWebInitializer.ENCODING;

@Controller
public class MainController {

    private static final String EDITABLE_TEMPLATE = "mail/email-editable.html";
    
    /* Home page. */
    @RequestMapping(value = {"/", "/index.html"}, method = GET)
    public String index() {
        return "index";
    }
    
    /* Plain text email. */
    @RequestMapping(value = "/text.html", method = GET)
    public String text() {
        return "text";
    }
    
    /* Simple HTML email. */
    @RequestMapping(value = "/simple.html", method = GET)
    public String simple() {
        return "simple";
    }
    
    /* HTML email with attachment. */
    @RequestMapping(value = "/attachment.html", method = GET)
    public String attachment() {
        return "attachment";
    }
    
    /* HTML email with inline image. */
    @RequestMapping(value = "/inline.html", method = GET)
    public String inline() {
        return "inline";
    }
    
    /* Editable HTML email. */
    @RequestMapping(value = "/editable.html", method = GET)
    public String editable(Model model) throws IOException {
        final ClassLoader classLoader = ClassLoaderUtils.getClassLoader(MainController.class);
        InputStream inputStream = classLoader.getResourceAsStream(EDITABLE_TEMPLATE);
        String baseTemplate = IOUtils.toString(inputStream, ENCODING);
        model.addAttribute("baseTemplate", baseTemplate);
        return "editable";
    }
    
    /* Sending confirmation page. */
    @RequestMapping(value = "/sent.html", method = GET)
    public String sent() {
        return "sent";
    }
    
}
