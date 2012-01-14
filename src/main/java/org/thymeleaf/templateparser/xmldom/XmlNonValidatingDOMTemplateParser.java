package org.thymeleaf.templateparser.xmldom;

import java.util.List;

import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;

/**
 * <p>
 *   Parses XML documents, using a standard non-validating DOM parser.
 * </p>
 * 
 * <p>
 *   This implementation first builds a DOM tree using the
 *   standard DOM API, and then translates this tree into a
 *   Thymeleaf-specific one. It also populates tree nodes with 
 *   basic location information (document name only).
 * </p>
 * 
 * @since 2.0.0
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 */
public final class XmlNonValidatingDOMTemplateParser extends AbstractNonValidatingDOMTemplateParser {


    // Even if we are using validating XML templates, fragments cannot be validated due to the lack of
    // context, so we simply add enough information for a non-validating parser to consider it alright
    // (a document root element declaration).
    private static final String FRAGMENT_WRAP_START = "<!DOCTYPE thymeleaf-xml-fragment>\n<thymeleaf-xml-fragment>";
    private static final String FRAGMENT_WRAP_END = "</thymeleaf-xml-fragment>";

    
    
    public XmlNonValidatingDOMTemplateParser(final int poolSize) {
        super(poolSize);
    }


    @Override
    protected final String wrapFragment(final String fragment) {
        return FRAGMENT_WRAP_START + fragment + FRAGMENT_WRAP_END;
    }

    
    @Override
    protected final List<Node> unwrapFragment(final Document document) {
        return ((NestableNode)((NestableNode)((NestableNode)document.getFirstChild()).getFirstChild()).getFirstChild()).getChildren();
    }
    
}
