package org.thymeleaf;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.parser.XmlSaxDocumentParser;
import org.thymeleaf.messageresolver.StandardMessageResolver;

/**
 * <p>
 * Tests the new ITemplateParser implementation, backed by the new
 * IDocumentParser implementation, agains the old template parser.
 * </p>
 * 
 * @author guvend
 */
public class TemplateParserTest
{
    private static final String DT_T1 = "<!DOCTYPE html SYSTEM " +
        "\"http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-1.dtd\">";

    // -----
    
    private Configuration configuration;
    private TemplateProcessingParameters templateProcessingParameters;
    
    private StringTemplateResolver templateResolver;
    private ITemplateWriter originalParser;
    private ITemplateWriter newParser;
    
    // -----
    
    @Before
    public void initialize()
    {
        templateResolver = new StringTemplateResolver("!!!");

        configuration = new Configuration();
        configuration.setTemplateResolver(templateResolver);
        configuration.setMessageResolver(new StandardMessageResolver());
        configuration.initialize();
        
        templateProcessingParameters = new TemplateProcessingParameters(
            configuration, "test", new TestContext());

        // -----
        
        originalParser = new OriginalTemplateParser(configuration);
        
        newParser = new TemplateRepository(configuration,
            new XmlSaxDocumentParser(configuration, 2),
            new NopParsedTemplateCache());
    }
    
    @Test
    public void testBasics()
    {
        test(DT_T1 +
            "<html><body><p class='myClass'>basics</p></body></html>");
        
        test(DT_T1 +
            "<html><!--c1--><head><script><![CDATA[cd1]]></script></head>" +
            "<body><!--c2--><p><![CDATA[cd2]]></p></body></html>");
        
        test(DT_T1 +
            "<html><body><div th:remove='tag'>remove me</div></body></html>");
    }

    // -----
    
    private void test(String xml)
    {
        Document originalDocument = parse(xml, originalParser);
        Document newDocument = parse(xml, newParser);
        
        boolean equals = newDocument.equals(originalDocument);
        
        assertTrue("Document parsed with DOM parser must be the same " +
            "as parsed with the original parser", equals);
    }
    
    /**
     * Warning: This method is NOT re-entrant, it mutates the templateResolver
     */
    private Document parse(String xml, ITemplateWriter parser)
    {
        templateResolver.setContent(xml);
        
        Template template = parser.parseDocument(
            templateProcessingParameters);
        
        return template.getDocument();
    }
}
