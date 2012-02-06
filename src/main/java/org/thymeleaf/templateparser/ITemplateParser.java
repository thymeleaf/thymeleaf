package org.thymeleaf.templateparser;

import java.io.Reader;
import java.util.List;

import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Node;

/**
 * <p>
 *   Common interface for template parsers, in charge of reading and converting a 
 *   resolved template into a DOM tree.
 * </p>
 * <p>
 *   All implementations of this interface must be <b>thread-safe</b>.  
 * </p>
 * 
 * @since 2.0.0
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Guven Demir
 * 
 */
public interface ITemplateParser {
    

    /**
     * <p>
     *   Parses the document contained in the given <tt>Reader</tt> object.
     * </p>
     * 
     * @param configuration the Configuration object
     * @param documentName the document name (optional).
     * @param source the Reader on the document to be parsed (required).
     * @return the parsed Document.
     */
    public Document parseTemplate(final Configuration configuration, final String documentName, final Reader source);
    

    /**
     * <p>
     *   Parses the document fragment contained in the given <tt>String</tt> object.
     * </p>
     * 
     * @param configuration the Configuration object
     * @param fragment the String containing the fragment
     * @return the resulting list of nodes.
     */
    public List<Node> parseFragment(final Configuration configuration, final String fragment);
    
}
