package thymeleafexamples.sayhello.dialect;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.attr.IAttrProcessor;
import org.thymeleaf.processor.value.IValueProcessor;
import org.thymeleaf.spring3.dialect.SpringThymeleafDialect;

public class HelloDialect extends AbstractDialect {

    public HelloDialect() {
        super();
    }
    
    /*
     * All of this dialect's attributes and/or tags
     * will start with 'hello:*'
     */
    public String getPrefix() {
        return "hello";
    }

    
    /*
     * Non-lenient: if a tag starting with 'hello:' is
     * found but no processor exists in this dialect for it,
     * throw an exception. 
     */
    public boolean isLenient() {
        return false;
    }

    
    /*
     * The attribute processors.
     */
    @Override
    public Set<IAttrProcessor> getAttrProcessors() {
        final Set<IAttrProcessor> attrProcessors = new HashSet<IAttrProcessor>();
        attrProcessors.add(new SayToAttrProcessor());
        attrProcessors.add(new SayToPlanetAttrProcessor());
        return attrProcessors;
    }

    
    /*
     * Value processors: we will be using the set of value
     * processors from the Spring Thymeleaf dialect, and so we will
     * benefit from all its syntax features (${...}, #{...}, 
     * SpringEL evaluation, etc.)
     */
    @Override
    public Set<IValueProcessor> getValueProcessors() {
        return SpringThymeleafDialect.createSpringThymeleafValueProcessorsSet();
    }

}
