package org.thymeleaf.templatewriter;



/**
 * 
 * @since 2.0.0
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 */
public final class XhtmlHtml5TemplateWriter extends AbstractGeneralTemplateWriter {


    public XhtmlHtml5TemplateWriter() {
        super();
    }

    @Override
    protected boolean writeXmlDeclaration() {
        return false;
    }

    @Override
    protected boolean useXhtmlTagMinimizationRules() {
        return true;
    }
    
    
}
