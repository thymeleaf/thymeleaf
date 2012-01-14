package org.thymeleaf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Tag;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateparser.xmldom.XhtmlAndHtml5NonValidatingDOMTemplateParser;
import org.thymeleaf.templateparser.xmlsax.XhtmlAndHtml5NonValidatingSAXTemplateParser;
import org.thymeleaf.templatewriter.ITemplateWriter;

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
public class DocumentParserTest
{
//    private static final String DT_T1 = "<!DOCTYPE html SYSTEM " +
//		"\"http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-1.dtd\">";
//    
//    private Configuration configuration;
//    private TemplateProcessingParameters templateProcessingParameters;
//    
//    private StringTemplateResolver templateResolver;
//    private ITemplateWriter originalTemplateParser;
//    private ITemplateParser xmlDomDocumentParser;
//    private ITemplateParser xmlSaxDocumentParser;
//    
//    // -----
//    
//    @Before
//    public void initialize()
//    {
//        templateResolver = new StringTemplateResolver("!!!");
//
//        configuration = new Configuration();
//        configuration.setTemplateResolver(templateResolver);
//        configuration.setMessageResolver(new StandardMessageResolver());
//        configuration.initialize();
//        
//        templateProcessingParameters = new TemplateProcessingParameters(
//            configuration, "test", new TestContext());
//        
//        xmlDomDocumentParser = new XhtmlAndHtml5NonValidatingDOMTemplateParser(2);
//        xmlSaxDocumentParser = new XhtmlAndHtml5NonValidatingSAXTemplateParser(2);
//    }
//    
//    // -----
//    
//    @Test
//    public void testBasics()
//    {
//        test(DT_T1 +
//            "<html><body><p class='myClass'>basics</p></body></html>");
//        
//        test(DT_T1 +
//            "<html><!--c1--><head><script><![CDATA[cd1]]></script></head>" +
//            "<body><!--c2--><p><![CDATA[cd2]]></p></body></html>");
//        
//        test(DT_T1 +
//            "<html><body><div th:remove='tag'>remove me</div></body></html>");
//    }
//    
//    @Test
//    public void testLocation()
//    {
//        String template =
//            /* 1 */ DT_T1 + "\n" +
//            /* 2 */ "\n" +
//            /* 3 */ "<html>" + "\n" +
//            /* 4 */ "    <body>" + "\n" +
//            /* 5 */ "        <p class='myClass'>first paragraph</p>" + "\n" +
//            /* 6 */ "        <p class='anotherClass'>second paragraph</p>" + "\n" +
//            /* 7 */ "    </body>" + "\n" +
//            /* 8 */ "</html>" + "\n"
//            ;
//        
//        String documentName = "my-document";
//        
//        Document saxDoc = xmlSaxDocumentParser.parseTemplate(this.configuration, documentName, template);
//        Document domDoc = xmlDomDocumentParser.parseTemplate(this.configuration, documentName, template);
//
//        testLocation("SAX [ORIGINAL] ", saxDoc, documentName, false);
//        testLocation("SAX [CLONED] ", saxDoc.clone(true), documentName, false);
//
//        testLocation("DOM [ORIGINAL] ", domDoc, documentName, true);
//        testLocation("DOM [CLONED] ", domDoc.clone(true), documentName, true);
//    }
//    
//    private void testLocation(String msg, Document doc, String documentName,
//        boolean nullLineNumbers)
//    {
//        Root root = doc.getRoot();
//        
//        Tag html = root.getTags().get(0);
//        Tag body = html.getTags().get(0);
//        Tag p1 = body.getTags().get(0);
//        Tag p2 = body.getTags().get(1);
//        
//        assertEquals(msg + "document name of html", documentName, html.getDocumentName());
//        assertEquals(msg + "document name of body", documentName, body.getDocumentName());
//        assertEquals(msg + "document name of p1", documentName, p1.getDocumentName());
//        assertEquals(msg + "document name of p2", documentName, p2.getDocumentName());
//
//        if(!nullLineNumbers)
//        {
//            assertEquals(msg + "linenumber of html", (Integer)3, html.getLineNumber());
//            assertEquals(msg + "linenumber of body", (Integer)4, body.getLineNumber());
//            assertEquals(msg + "linenumber of p1", (Integer)5, p1.getLineNumber());
//            assertEquals(msg + "linenumber of p2", (Integer)6, p2.getLineNumber());
//        }
//        else
//        {
//            assertEquals(msg + "linenumber of html", null, html.getLineNumber());
//            assertEquals(msg + "linenumber of body", null, body.getLineNumber());
//            assertEquals(msg + "linenumber of p1", null, p1.getLineNumber());
//            assertEquals(msg + "linenumber of p2", null, p2.getLineNumber());
//        }
//    }
//    
//    // -----
//    
//    /**
//     * Warning: This method is NOT re-entrant, it mutates the templateResolver
//     */
//    private void test(String xml)
//    {
//        templateResolver.setContent(xml);
//        
//        Template template = originalTemplateParser.parseDocument(
//            templateProcessingParameters);
//        
//        Document originalDocument = template.getDocument();
//        
//        boolean validating = templateResolver.resolveTemplate(
//            templateProcessingParameters).getTemplateMode().isValidating();
//
//        testDomParser("document-name", xml, validating, originalDocument);
//        testSaxParser("document-name", xml, validating, originalDocument);
//    }
//    
//    private void testDomParser(String documentName, String xml,
//        boolean validating, Document originalDocument)
//    {
//        Document newDocument = xmlDomDocumentParser.parse(documentName, xml, validating);
//        
//        boolean equals = newDocument.equals(originalDocument);
//        
//        assertTrue("Document parsed with DOM parser must be the same " +
//            "as parsed with the original parser", equals);
//    }
//    
//    private void testSaxParser(String documentName, String xml, boolean validating,
//        Document originalDocument)
//    {
//        Document newDocument = xmlSaxDocumentParser.parse(documentName, xml, validating);
//        
//        boolean equals = newDocument.equals(originalDocument);
//        
//        assertTrue("Document parsed with SAX parser must be the same " +
//            "as parsed with the original parser", equals);
//    }
}
