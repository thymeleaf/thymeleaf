package thymeleafexamples.sayhello.dialect;

import java.util.Set;

import org.thymeleaf.Arguments;
import org.thymeleaf.processor.applicability.AttrApplicability;
import org.thymeleaf.processor.attr.AbstractTextChildModifierAttrProcessor;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SayToAttrProcessor 
        extends AbstractTextChildModifierAttrProcessor {

    
    public SayToAttrProcessor() {
        super();
    }
    
    
    public Set<AttrApplicability> getAttributeApplicabilities() {
        return AttrApplicability.createSetForAttrName("sayto");
    }

    public Integer getPrecedence() {
        return Integer.valueOf(2000);
    }

    @Override
    protected String getText(final Arguments arguments,
            final TemplateResolution templateResolution, final Document document,
            final Element element, final Attr attribute, final String attributeName,
            final String attributeValue) {
        
        return "Hello, "  + attributeValue + "!";
        
    }


}
