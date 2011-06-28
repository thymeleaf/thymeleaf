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
import org.thymeleaf.util.MessagesUtils;
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
        return AttrApplicability.createSetForAttrName("saytoplanet");
    }

    
    public Integer getPrecedence() {
        return Integer.valueOf(2000);
    }

    
    @Override
    public Set<Class<? extends IValueProcessor>> getValueProcessorDependencies() {
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

        final String message = 
            MessagesUtils.resolveMsg(arguments, templateResolution, 
                    SAYTO_PLANET_MESSAGE, new Object[] {planet});
        
        return message;
        
    }


}
