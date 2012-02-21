/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
import java.util.Locale;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import thymeleafexamples.springmail.service.EmailService;


@Controller
public class MainController {

    @Autowired 
    private EmailService emailService;



    
    @RequestMapping("/")
    public String root() {
        return "redirect:/index.html";
    }

    
    /* Home page. */
    @RequestMapping("/index.html")
    public String index() {
        return "index.html";
    }

    
    /* Sending confirmation page. */
    @RequestMapping("/sent.html")
    public String sent() {
        return "sent.html";
    }

    
    
    /* 
     * Send HTML mail (simple) 
     */
    @RequestMapping(value = "/sendMailSimple", method = RequestMethod.POST)
    public String sendTextMail(
            @RequestParam("recipientName") final String recipientName,
            @RequestParam("recipientEmail") final String recipientEmail,
            final Locale locale) 
            throws MessagingException {

        this.emailService.sendSimpleMail(recipientName, recipientEmail, locale);
        return "redirect:sent.html";
        
    }

    
    
    
    /* 
     * Send HTML mail with attachment. 
     */
    @RequestMapping(value = "/sendMailWithAttachment", method = RequestMethod.POST)
    public String sendMailWithAttachment(
            @RequestParam("recipientName") final String recipientName,
            @RequestParam("recipientEmail") final String recipientEmail,
            @RequestParam("attachment") final MultipartFile attachment,
            final Locale locale) 
            throws MessagingException, IOException {

        this.emailService.sendMailWithAttachment(
                recipientName, recipientEmail, attachment.getOriginalFilename(), 
                attachment.getBytes(), attachment.getContentType(), locale);
        return "redirect:sent.html";
        
    }

    
    
    /* 
     * Send HTML mail with inline image
     */
    @RequestMapping(value = "/sendMailWithInlineImage", method = RequestMethod.POST)
    public String sendMailWithInline(
            @RequestParam("recipientName") final String recipientName,
            @RequestParam("recipientEmail") final String recipientEmail,
            @RequestParam("image") final MultipartFile image,
            final Locale locale)
            throws MessagingException, IOException {

        this.emailService.sendMailWithInline(
                recipientName, recipientEmail, image.getName(), 
                image.getBytes(), image.getContentType(), locale);
        return "redirect:sent.html";
        
    }

    
    
    @ExceptionHandler(Exception.class)
    public String error() {
        return "error.html";
    }
    
}
