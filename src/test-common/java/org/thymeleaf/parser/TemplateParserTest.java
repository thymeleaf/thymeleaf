package org.thymeleaf.parser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.TemplateRepository;
import org.thymeleaf.context.Context;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Element;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.resourceresolver.ClassLoaderResourceResolver;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templatemode.StandardTemplateModeHandlers;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateparser.xmldom.XhtmlAndHtml5NonValidatingDOMTemplateParser;
import org.thymeleaf.templateparser.xmlsax.XhtmlAndHtml5NonValidatingSAXTemplateParser;
import org.thymeleaf.templateresolver.NonCacheableTemplateResolutionValidity;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.templatewriter.ITemplateWriter;

import static org.junit.Assert.assertEquals;

/**
 * <p>
 * Tests IDocumentParser implementations agains the result obtained from
 * the original template parser implementation.
 * </p>
 * 
 * <p>
 * Tests the newly introduced location information (document name and line number)
 * supports of IDocumentParser implementations.
 * </p>
 * 
 * @author guvend
 */
public class TemplateParserTest {
    
    private static final String DT_T1 = "<!DOCTYPE html SYSTEM " +
		"\"http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-1.dtd\">";
    
    private Configuration configuration;
    
    private StringTemplateResolver templateResolver;
    private ITemplateParser xmlDomDocumentParser;
    private ITemplateParser xmlSaxDocumentParser;
    
    // -----
    
    @Before
    public void initialize() {
        
        this.templateResolver = new StringTemplateResolver("!!!");

        this.configuration = new Configuration();
        this.configuration.setDialect(new TestDialect());
        this.configuration.setTemplateResolver(this.templateResolver);
        this.configuration.setMessageResolver(new StandardMessageResolver());
        this.configuration.setTemplateModeHandlers(StandardTemplateModeHandlers.ALL_TEMPLATE_MODE_HANDLERS);
        this.configuration.initialize();
        
        this.xmlDomDocumentParser = new XhtmlAndHtml5NonValidatingDOMTemplateParser(2);
        this.xmlSaxDocumentParser = new XhtmlAndHtml5NonValidatingSAXTemplateParser(2);
        
    }
    
    // -----
    
    @Test
    public void testBasics() throws Exception {
        
        parseAndTest(DT_T1 +
            "<html><body><p class='myClass'>basics</p></body></html>");
        
        parseAndTest(DT_T1 +
            "<html><!--c1--><head><script><![CDATA[cd1]]></script></head>" +
            "<body><!--c2--><p><![CDATA[cd2]]></p></body></html>");
        
        parseAndTest(DT_T1 +
            "<html><body><div th:remove='all'>remove me</div></body></html>");
        
    }
    
    
    @Test
    public void testLocation() {
        
        String template =
            /* 1 */ DT_T1 + "\n" +
            /* 2 */ "\n" +
            /* 3 */ "<html>" + "\n" +
            /* 4 */ "    <body>" + "\n" +
            /* 5 */ "        <p class='myClass'>first paragraph</p>" + "\n" +
            /* 6 */ "        <p class='anotherClass'>second paragraph</p>" + "\n" +
            /* 7 */ "    </body>" + "\n" +
            /* 8 */ "</html>" + "\n"
            ;
        
        String documentName = "my-document";
        
        Document saxDoc = 
                this.xmlSaxDocumentParser.parseTemplate(this.configuration, documentName, new StringReader(template));
        Document domDoc = 
                this.xmlDomDocumentParser.parseTemplate(this.configuration, documentName, new StringReader(template));

        checkLocations("SAX [ORIGINAL] ", saxDoc, documentName, false);
        checkLocations("SAX [CLONED] ", saxDoc.clone(true), documentName, false);

        checkLocations("DOM [ORIGINAL] ", domDoc, documentName, true);
        checkLocations("DOM [CLONED] ", domDoc.clone(true), documentName, true);
        
    }

    
    private void checkLocations(final String msg, final Document doc, 
            final String documentName, final boolean nullLineNumbers) {
        
        Element html = doc.getFirstElementChild();
        Element body = html.getFirstElementChild();
        Element p1 = body.getElementChildren().get(0);
        Element p2 = body.getElementChildren().get(1);
        
        assertEquals(msg + "document name of html", documentName, html.getDocumentName());
        assertEquals(msg + "document name of body", documentName, body.getDocumentName());
        assertEquals(msg + "document name of p1", documentName, p1.getDocumentName());
        assertEquals(msg + "document name of p2", documentName, p2.getDocumentName());

        if(!nullLineNumbers) {
            assertEquals(msg + "linenumber of html", Integer.valueOf(3), html.getLineNumber());
            assertEquals(msg + "linenumber of body", Integer.valueOf(4), body.getLineNumber());
            assertEquals(msg + "linenumber of p1", Integer.valueOf(5), p1.getLineNumber());
            assertEquals(msg + "linenumber of p2", Integer.valueOf(6), p2.getLineNumber());
        } else {
            assertEquals(msg + "linenumber of html", null, html.getLineNumber());
            assertEquals(msg + "linenumber of body", null, body.getLineNumber());
            assertEquals(msg + "linenumber of p1", null, p1.getLineNumber());
            assertEquals(msg + "linenumber of p2", null, p2.getLineNumber());
        }
    }
    
    // -----
    
    /**
     * Warning: This method is NOT re-entrant, it mutates the templateResolver
     */
    private void parseAndTest(final String xml) throws IOException {
        
        this.templateResolver.setContent(xml);
        
        final Document domDocument = 
                this.xmlDomDocumentParser.parseTemplate(this.configuration, null, new StringReader(xml));
        final Document saxDocument = 
                this.xmlSaxDocumentParser.parseTemplate(this.configuration, null, new StringReader(xml));
        
        final String domDocumentOutput = createOutput(domDocument);
        final String saxDocumentOutput = createOutput(saxDocument);
        
        System.out.println("DOM: \n" + domDocumentOutput);
        System.out.println("SAX: \n" + domDocumentOutput);
        
        Assert.assertEquals(saxDocumentOutput, domDocumentOutput);
        
    }

    
    private String createOutput(final Document document) throws IOException {
        
        final TemplateProcessingParameters templateProcessingParameters = 
                new TemplateProcessingParameters(this.configuration, "test", new Context());
        
        final TemplateResolution templateResolution = 
                new TemplateResolution("test", "test", new ClassLoaderResourceResolver(), "UTF-8", "XHTML", 
                        new NonCacheableTemplateResolutionValidity());
        
        final Arguments arguments = 
                new Arguments(
                        new TemplateEngine(),
                        templateProcessingParameters, templateResolution, 
                        new TemplateRepository(this.configuration), document);
        
        final StringWriter stringWriter = new StringWriter();
        final ITemplateWriter templateWriter = 
                this.configuration.getTemplateModeHandler("XHTML").getTemplateWriter();
        templateWriter.write(arguments, stringWriter, document);
        return stringWriter.toString();
    }
    

    
    
    static class TestDialect extends StandardDialect {

        @Override
        public boolean isLenient() {
            return true;
        }
        
    }
    
}
