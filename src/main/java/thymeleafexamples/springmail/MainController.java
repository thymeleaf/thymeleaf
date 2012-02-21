package thymeleafexamples.springmail;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

/**
 * Application home page.
 */
@Controller
public class MainController {

    @Autowired 
    private JavaMailSender mailSender;
    
    @Autowired 
    private TemplateEngine templateEngine;
    
    @Autowired
    private ServletContext servletContext;



    
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
            final HttpServletRequest request, 
            final Locale locale) 
            throws MessagingException {
        
        // Prepare the evaluation context
        final WebContext ctx = new WebContext(request, this.servletContext, locale);
        ctx.setVariable("name", recipientName);
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
        
        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject("Example HTML email (simple)");
        message.setFrom("thymeleaf@example.com");
        message.setTo(recipientEmail);

        // Create the HTML body using Thymeleaf
        final String htmlContent =
                this.templateEngine.process("mail/email-simple.html", ctx);
        message.setText(htmlContent, true /* isHtml */);
        
        // Send email
        this.mailSender.send(mimeMessage);
        
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
            final HttpServletRequest request, 
            final Locale locale) 
            throws MessagingException, IOException {
        
        // Prepare the evaluation context
        final WebContext ctx = new WebContext(request, this.servletContext, locale);
        ctx.setVariable("name", recipientName);
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
        
        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = 
                new MimeMessageHelper(mimeMessage, true /* multipart */, "UTF-8");
        message.setSubject("Example HTML email with attachment");
        message.setFrom("thymeleaf@example.com");
        message.setTo(recipientEmail);

        // Create the HTML body using Thymeleaf
        final String htmlContent =
                this.templateEngine.process("mail/email-withattachment.html", ctx);
        message.setText(htmlContent, true /* isHtml */);
        
        // Add the attachment
        final InputStreamSource attachmentSource = new ByteArrayResource(attachment.getBytes());
        message.addAttachment(
                attachment.getOriginalFilename(), attachmentSource, attachment.getContentType());
        
        // Send mail
        this.mailSender.send(mimeMessage);
        
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
            final HttpServletRequest request, 
            final Locale locale)
            throws MessagingException, IOException {
        
        // Prepare the evaluation context
        final WebContext ctx = new WebContext(request, this.servletContext, locale);
        ctx.setVariable("name", recipientName);
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
        ctx.setVariable("image", image); // so that we can reference it from HTML
        
        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = 
                new MimeMessageHelper(mimeMessage, true /* multipart */, "UTF-8");
        message.setSubject("Example HTML email with inline image");
        message.setFrom("thymeleaf@example.com");
        message.setTo(recipientEmail);

        // Create the HTML body using Thymeleaf
        final String htmlContent =
                this.templateEngine.process("mail/email-inlineimage.html", ctx);
        message.setText(htmlContent, true /* isHtml */);
        
        // Add the inline image, referenced from the HTML code as "cid:{image.name}"
        final InputStreamSource imageSource = new ByteArrayResource(image.getBytes());
        message.addInline(image.getName(), imageSource, image.getContentType());
        
        // Send mail
        this.mailSender.send(mimeMessage);
        
        return "redirect:sent.html";
        
    }

    
}
