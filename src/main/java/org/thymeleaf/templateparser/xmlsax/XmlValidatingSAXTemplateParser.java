package org.thymeleaf.templateparser.xmlsax;

import java.util.List;

import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;

/**
 * <p>
 *   Parses XML documents, using a standard SAX parser.
 * </p>
 * 
 * <p>
 *   This implementation populates tree nodes with detailed location 
 *   information (document name and line number).
 * </p>
 * 
 * @since 2.0.0
 * 
 * @author Guven Demir
 * @author Daniel Fern&aacute;ndez
 * 
 */
public final class XmlValidatingSAXTemplateParser extends AbstractValidatingSAXTemplateParser {


    // We simply add enough information for a non-validating parser to consider it alright
    // (a document root element declaration).
    private static final String FRAGMENT_WRAP_START = "<!DOCTYPE thymeleaf-xml-fragment>\n<thymeleaf-xml-fragment>";
    private static final String FRAGMENT_WRAP_END = "</thymeleaf-xml-fragment>";

    
    
    public XmlValidatingSAXTemplateParser(final int poolSize) {
        super(poolSize);
    }


    @Override
    protected final String wrapFragment(final String fragment) {
        return FRAGMENT_WRAP_START + fragment + FRAGMENT_WRAP_END;
    }

    
    @Override
    protected final List<Node> unwrapFragment(final Document document) {
        return ((NestableNode)document.getFirstChild()).getChildren();
    }
    
}
