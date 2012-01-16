package org.thymeleaf.templateparser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Document;
import org.thymeleaf.exceptions.TemplateInputException;

/**
 * <p>
 *   Optional base class for IDocumentParser implementations.
 * </p>
 * 
 * @since 2.0.0
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Guven Demir
 * 
 */
public abstract class AbstractTemplateParser implements ITemplateParser {


    
    protected AbstractTemplateParser() {
        super();
    }
    
    
    
    
    public final Document parseTemplate(final Configuration configuration, final String documentName, final String source) {
        return parseTemplate(configuration, documentName, new StringReader(source));
    }


    
    public final Document parseTemplate(final Configuration configuration, final String documentName, final InputStream source, final String characterEncoding) {
        
        Reader reader = null;
        if (characterEncoding != null && !characterEncoding.trim().equals("")) {
            try {
                reader = new InputStreamReader(source, characterEncoding);
            } catch (final UnsupportedEncodingException e) {
                throw new TemplateInputException("Exception parsing document", e);
            }
        } else {
            reader = new InputStreamReader(source);
        }
        
        return parseTemplate(configuration, documentName, reader);
        
    }

    
}
