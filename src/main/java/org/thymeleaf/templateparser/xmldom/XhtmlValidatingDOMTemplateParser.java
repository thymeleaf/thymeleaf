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
public final class XhtmlValidatingDOMTemplateParser extends AbstractValidatingDOMTemplateParser {


    // We will use this fragment start for fragments in validating XHTML modes because fragments cannot
    // be validated anyway (there's no context for them) and the non-validating pool will be always used.
    // Therefore having a document element name ('html') will be enough, as this is the only aspect controlled
    // by non-validating parsers. 
    private static final String FRAGMENT_WRAP_START = "<!DOCTYPE html>\n<html><body><div>";
    private static final String FRAGMENT_WRAP_END = "</div></body></html>";

    
    
    public XhtmlValidatingDOMTemplateParser(final int poolSize) {
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
