package org.thymeleaf.templateparser;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Node;

/**
 * <p>
 *   Represents a document parser, which parses the given resource into
 *   a Thymeleaf DOM tree.
 * </p>
 * 
 * <p>
 *   All <tt>IDocumentParser</tt> implementations must be <b>thread-safe</b>.  
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
     *   Parses the document contained in the given String object.
     * </p>
     * 
     * @param configuration the Configuration object
     * @param documentName the document name (optional).
     * @param source the String containing the document to be parsed (required).
     * @return the parsed Document.
     */
    public Document parseTemplate(final Configuration configuration, final String documentName, final String source);


    /**
     * <p>
     *   Parses the document contained in the given <tt>InputStream</tt> object.
     * </p>
     * 
     * @param configuration the Configuration object
     * @param documentName the document name (optional).
     * @param source the InputStream on the document to be parsed (required).
     * @param characterEncoding the character encoding that must be used for reading the input stream.
     * @return the parsed Document.
     */
    public Document parseTemplate(final Configuration configuration, final String documentName, final InputStream source, final String characterEncoding);


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
