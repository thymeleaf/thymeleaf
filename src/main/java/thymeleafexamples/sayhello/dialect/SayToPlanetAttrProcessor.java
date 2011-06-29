package thymeleafexamples.sayhello.dialect;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.Arguments;
import org.thymeleaf.processor.applicability.AttrApplicability;
import org.thymeleaf.processor.attr.AbstractTextChildModifierAttrProcessor;
import org.thymeleaf.processor.value.IValueProcessor;
import org.thymeleaf.standard.processor.value.StandardValueProcessor;
import org.thymeleaf.standard.syntax.StandardSyntax;
import org.thymeleaf.standard.syntax.StandardSyntax.Value;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SayToPlanetAttrProcessor 
        extends AbstractTextChildModifierAttrProcessor {

    private static final String SAYTO_PLANET_MESSAGE = "msg.helloplanet"; 

    
    public SayToPlanetAttrProcessor() {
        super();
    }
    
    
    public Set<AttrApplicability> getAttributeApplicabilities() {
        // Only execute this processor for "saytoplanet" attributes.
        return AttrApplicability.createSetForAttrName("saytoplanet");
    }

    
    public Integer getPrecedence() {
        // Higher (less-precedent) than any attribute in the
        // Spring Thymeleaf dialect and also than "sayto".
        return Integer.valueOf(11000);
    }


    @Override
    public Set<Class<? extends IValueProcessor>> getValueProcessorDependencies() {
        // This attribute processor needs the StandardValueProcessor (declared
        // in the dialect class).
        final Set<Class<? extends IValueProcessor>> dependencies = new HashSet<Class<? extends IValueProcessor>>();
        dependencies.add(StandardValueProcessor.class);
        return dependencies;
    }


    
    @Override
    protected String getText(final Arguments arguments,
            final TemplateResolution templateResolution, final Document document,
            final Element element, final Attr attribute, final String attributeName,
            final String attributeValue) {

        final StandardValueProcessor valueProcessor =
            arguments.getConfiguration().getValueProcessorByClass(this,StandardValueProcessor.class);
        
        final Value planetValue = 
            StandardSyntax.parseValue(attributeValue, valueProcessor, arguments, templateResolution);
        
        final String planet = (String) valueProcessor.getValue(arguments, templateResolution, planetValue); 

        /*
         * This "getMessage(...)" method will first try to resolve the
         * message as a "template message" (one that is defined for a specific 
         * template or templates, and that would be resolved, in a Spring MVC app, 
         * by Spring's MessageSource objects).
         * 
         * If not found, it will try to resolve it as a "processor message", a type
         * of messages meant to appear in .properties files by the side of the 
         * attribute processor itself (or any of its superclasses) and, if needed, 
         * be packaged along with it in a .jar file for better encapsulation of UI 
         * components.
         */
        final String message = 
            getMessage(arguments, templateResolution, SAYTO_PLANET_MESSAGE, new Object[] {planet});
        
        return message;
        
    }


}
