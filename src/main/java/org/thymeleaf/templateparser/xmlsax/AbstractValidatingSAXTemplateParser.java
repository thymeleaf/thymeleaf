package org.thymeleaf.templateparser.xmlsax;

import javax.xml.parsers.SAXParser;

import org.thymeleaf.util.ResourcePool;

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
public abstract  class AbstractValidatingSAXTemplateParser extends AbstractNonValidatingSAXTemplateParser {
    
    
    
    private ResourcePool<SAXParser> pool;

    
    
    protected AbstractValidatingSAXTemplateParser(final int poolSize) {
        super(poolSize);
        this.pool = createSaxParsers(poolSize, true);
    }
    
    


    @Override
    protected ResourcePool<SAXParser> getPool() {
        return this.pool;
    }



}
