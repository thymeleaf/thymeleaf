package org.thymeleaf.templateparser.html;

import java.util.List;

import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;

/**
 * <p>
 *   Document parser implementation for non-XML HTML documents.
 * </p>
 * 
 * @since 2.0.0
 * 
 * @author Daniel Fern&aacute;ndez
 */
public final class LegacyHtml5TemplateParser extends AbstractHtmlTemplateParser {
    

    private static final String FRAGMENT_WRAP_START = "<!DOCTYPE html>\n<html><head></head><body><div>";
    private static final String FRAGMENT_WRAP_END = "</div></body></html>";
    
    
    public LegacyHtml5TemplateParser(final String templateModeName, int poolSize) {
        super(templateModeName, poolSize);
    }



    @Override
    protected final String wrapFragment(final String fragment) {
        return FRAGMENT_WRAP_START + fragment + FRAGMENT_WRAP_END;
    }

    
    @Override
    protected final List<Node> unwrapFragment(final Document document) {
        return ((NestableNode)((NestableNode)((NestableNode)document.getFirstChild()).getChildren().get(1)).getFirstChild()).getChildren();
    }

    
}
