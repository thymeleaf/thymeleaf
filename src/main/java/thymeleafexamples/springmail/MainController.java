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
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.xml.sax.SAXException;

/**
 * Application home page.
 */
@Controller
public class MainController implements ServletContextAware {

    private static final boolean MULTIPART = true;
    private static final boolean HTML = true;

    @Autowired private JavaMailSender mailSender;
    @Autowired private TemplateEngine templateEngine;

    private ServletContext context;

    public void setServletContext(ServletContext context) {
        this.context = context;
    }

    @RequestMapping("/")
    public String root() {
        return "redirect:/index.html";
    }

    /** Home page. */
    @RequestMapping("/index.html")
    public String index() {
        return "index.html";
    }

    /** Sending confirmation page. */
    @RequestMapping("/sent.html")
    public String sent() {
        return "sent.html";
    }

    /** Send a plain text email. */
    @RequestMapping(value = "/sendTextMail", method = RequestMethod.POST)
    public String sendTextMail(@RequestParam("recipient") String recipient,
            HttpServletRequest request, Locale locale) throws MessagingException, SAXException, IOException {
        
        // Prepare message
        MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject("Example plain text mail");
        message.setFrom("thymeleaf@example.com");
        message.setTo(recipient);
        
        // Message body from Thymeleaf template
        WebContext ctx = new WebContext(request, this.context, locale);
        ctx.setVariable("name", "John Smith");
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
        String html = this.templateEngine.process("mail/plain.html", ctx);
        message.setText(HtmlUtil.extractBody(html));
        
        // Send mail
        this.mailSender.send(mimeMessage);
        return "redirect:sent.html";
    }

    /** Send a HTML email with an attachment. */
    @RequestMapping(value = "/sendMailWithAttachment", method = RequestMethod.POST)
    public String sendMailWithAttachment(@RequestParam("recipient") String recipient,
            @RequestParam("document") MultipartFile document,
            HttpServletRequest request, Locale locale) throws MessagingException, IOException {
        
        // Prepare message
        MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, MULTIPART, "UTF-8");
        message.setSubject("Example mail with attachments");
        message.setFrom("thymeleaf@example.com");
        message.setTo(recipient);
        
        // Message body from Thymeleaf template
        WebContext ctx = new WebContext(request, this.context, locale);
        ctx.setVariable("name", "John Smith");
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
        message.setText(this.templateEngine.process("mail/attachment.html", ctx), HTML);
        
        // Attachment
        InputStreamSource documentSource = new ByteArrayResource(document.getBytes());
        message.addAttachment(document.getName(), documentSource);
        
        // Send mail
        this.mailSender.send(mimeMessage);
        return "redirect:sent.html";
        
    }

    /** Send a HTML email with an inline image. */
    @RequestMapping(value = "/sendMailWithInline", method = RequestMethod.POST)
    public String sendMailWithInline(@RequestParam("recipient") String recipient,
            @RequestParam("image") MultipartFile image,
            HttpServletRequest request, Locale locale) throws MessagingException, IOException {
        
        // Prepare message
        MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, MULTIPART, "UTF-8");
        message.setSubject("Example mail with embedded image");
        message.setFrom("thymeleaf@example.com");
        message.setTo(recipient);
        
        // Message body from Thymeleaf template
        WebContext ctx = new WebContext(request, this.context, locale);
        ctx.setVariable("name", "John Smith");
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
        ctx.setVariable("image", image);
        message.setText(this.templateEngine.process("mail/inline.html", ctx), HTML);
        
        // Inline image
        InputStreamSource imageSource = new ByteArrayResource(image.getBytes());
        message.addInline(image.getName(), imageSource, image.getContentType());
        
        // Send mail
        this.mailSender.send(mimeMessage);
        return "redirect:sent.html";
        
    }
}
