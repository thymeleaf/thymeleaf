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
import java.util.Locale;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import thymeleafexamples.springmail.business.service.EmailService;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class SendingController {

    @Autowired
    private EmailService emailService;

    /* Send plain TEXT mail */
    @RequestMapping(value = "/sendMailText", method = POST)
    public String sendTextMail(
        @RequestParam("recipientName") final String recipientName,
        @RequestParam("recipientEmail") final String recipientEmail,
        final Locale locale)
        throws MessagingException {

        this.emailService.sendTextMail(recipientName, recipientEmail, locale);
        return "redirect:sent.html";

    }

    /* Send HTML mail (simple) */
    @RequestMapping(value = "/sendMailSimple", method = POST)
    public String sendSimpleMail(
        @RequestParam("recipientName") final String recipientName,
        @RequestParam("recipientEmail") final String recipientEmail,
        final Locale locale)
        throws MessagingException {

        this.emailService.sendSimpleMail(recipientName, recipientEmail, locale);
        return "redirect:sent.html";

    }

    /* Send HTML mail with attachment. */
    @RequestMapping(value = "/sendMailWithAttachment", method = POST)
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

    /* Send HTML mail with inline image */
    @RequestMapping(value = "/sendMailWithInlineImage", method = POST)
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

    /* Send editable HTML mail */
    @RequestMapping(value = "/sendEditableMail", method = POST)
    public String sendMailWithInline(
        @RequestParam("recipientName") final String recipientName,
        @RequestParam("recipientEmail") final String recipientEmail,
        @RequestParam("body") final String body,
        final Locale locale)
        throws MessagingException, IOException {

        this.emailService.sendEditableMail(
            recipientName, recipientEmail, body, locale);
        return "redirect:sent.html";

    }

}
