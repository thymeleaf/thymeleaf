package org.thymeleaf.templatewriter;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Document;


/**
 * <p>
 *   Common interface for objects in charge of writing the results of
 *   processing templates.
 * </p>
 * 
 * <p>
 *   All <tt>ITemplateWriter</tt> implementations must be <b>thread-safe</b>.  
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
