package thymeleafexamples.sayhello.dialect;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.attr.IAttrProcessor;
import org.thymeleaf.processor.value.IValueProcessor;
import org.thymeleaf.spring3.dialect.SpringThymeleafDialect;

public class HelloDialect extends AbstractDialect {

    
    public String getPrefix() {
        return "hello";
    }

    public boolean isLenient() {
        return false;
    }

    
    @Override
    public Set<IAttrProcessor> getAttrProcessors() {
        final Set<IAttrProcessor> attrProcessors = new HashSet<IAttrProcessor>();
        attrProcessors.add(new SayToAttrProcessor());
        attrProcessors.add(new SayToPlanetAttrProcessor());
        return attrProcessors;
    }

    
    @Override
    public Set<IValueProcessor> getValueProcessors() {
        return SpringThymeleafDialect.createSpringThymeleafValueProcessorsSet();
    }

}
