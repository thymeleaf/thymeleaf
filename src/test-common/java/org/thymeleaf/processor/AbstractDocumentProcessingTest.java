package org.thymeleaf.processor;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templateparser.EntityResolver;
import org.thymeleaf.templateparser.TemplatePreprocessingReader;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public abstract class AbstractDocumentProcessingTest extends TestCase {

    private TemplateEngine templateEngine;
    private IContext context;
    private XPath xPath;
    private DocumentBuilder docBuilder;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.templateEngine = new TemplateEngine();
        TemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        this.templateEngine.setTemplateResolver(templateResolver);
        this.templateEngine.initialize();

        this.context = new Context();

        XPathFactory xPathFactory = XPathFactory.newInstance();
        this.xPath = xPathFactory.newXPath();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        this.docBuilder = factory.newDocumentBuilder();
        this.docBuilder.setEntityResolver(new EntityResolver(this.templateEngine.getConfiguration()));
    }

    protected Document processTemplate(String template) throws Exception {
        String out = this.templateEngine.process(template, this.context);
        System.out.println(out);

        final InputSource inputSource = new InputSource(
                new TemplatePreprocessingReader(new StringReader(out), 8192));
        return this.docBuilder.parse(inputSource);
    }

    protected void assertNodeExists(Document doc, String expr) throws Exception {
        NodeList n = evaluate(doc, expr);
        assertEquals(n.getLength(), 1);
    }

    protected void assertNodeDoesNotExist(Document doc, String expr) throws Exception {
        NodeList n = evaluate(doc, expr);
        assertEquals(n.getLength(), 0);
    }

    protected NodeList evaluate(Document doc, String expr) throws Exception {
        return (NodeList) this.xPath.evaluate(expr, doc, XPathConstants.NODESET);
    }

    protected Node getNode(Document doc, String expr) throws Exception {
        return (Node) this.xPath.evaluate(expr, doc, XPathConstants.NODE);
    }

}
