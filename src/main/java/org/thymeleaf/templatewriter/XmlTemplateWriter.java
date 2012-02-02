package org.thymeleaf.templatewriter;



/**
 * 
 * @since 2.0.0
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 */
public final class XmlTemplateWriter extends AbstractGeneralTemplateWriter {


    public XmlTemplateWriter() {
        super();
    }

    @Override
    protected boolean shouldWriteXmlDeclaration() {
        return true;
    }

    @Override
    protected boolean useXhtmlTagMinimizationRules() {
        return false;
    }
    
    
}
