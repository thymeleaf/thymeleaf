package org.thymeleaf.templateparser.xmldom;

import javax.xml.parsers.DocumentBuilder;

import org.thymeleaf.util.ResourcePool;

/**
 * <p>
 *   Parses XML documents, using a standard validating DOM parser.
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
public abstract class AbstractValidatingDOMTemplateParser extends AbstractNonValidatingDOMTemplateParser {
    
    
    
    private ResourcePool<DocumentBuilder> pool;

    
    
    protected AbstractValidatingDOMTemplateParser(final int poolSize) {
        super(poolSize);
        this.pool = createDocumentBuilders(poolSize, true);
    }
    
    


    @Override
    protected ResourcePool<DocumentBuilder> getPool() {
        return this.pool;
    }




    @Override
    protected boolean shouldAddThymeleafRootToParser() {
        return false;
    }



}
