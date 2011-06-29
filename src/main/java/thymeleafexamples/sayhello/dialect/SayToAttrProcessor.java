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
        // Only execute this processor for "sayto" attributes.
        return AttrApplicability.createSetForAttrName("sayto");
    }

    public Integer getPrecedence() {
        // A value of 10000 is higher than any attribute in the
        // Spring Thymeleaf dialect. So this attribute will execute
        // after all other attributes from that dialect, if in the 
        // same tag.
        return Integer.valueOf(10000);
    }

    
    /*
     * Our processor is a subclass of the convenience abstract implementation
     * "AbstractTextChildModifierAttrProcessor", which takes care of the
     * DOM modifying stuff and allows us just to implement this "getText(...)"
     * method to compute the text to be set as tag body.
     */
    @Override
    protected String getText(final Arguments arguments,
            final TemplateResolution templateResolution, final Document document,
            final Element element, final Attr attribute, final String attributeName,
            final String attributeValue) {

        return "Hello, "  + attributeValue + "!";
        
    }


}
