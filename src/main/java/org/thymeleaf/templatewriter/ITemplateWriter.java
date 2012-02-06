package org.thymeleaf.templatewriter;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Document;


/**
 * <p>
 *   Common interface for template parsers, in charge of writing processed DOM
 *   trees, this is, creating their corresponding output.
 * </p>
 * <p>
 *   All implementations of this interface must be <b>thread-safe</b>.  
 * </p>
 * 
 * @since 2.0.0
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 */
public interface ITemplateWriter {
 
    public void write(final Arguments arguments, final Writer writer, final Document document) throws IOException;
    
}
