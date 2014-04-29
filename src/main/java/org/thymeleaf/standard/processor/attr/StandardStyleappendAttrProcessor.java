package org.thymeleaf.standard.processor.attr;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;

/**
 * Appends the result of an expression to the <b>style</b> attribute, eg:
 * <p>
 * &lt;div style="margin: 0;" th:styleappend="${myObject.type == 'special'} ? 'color: green;' : 
 * 																			  'color: black;'" /&gt;
 * <p>
 * Depending on the result of the expression, the style attribute will become
 * either "margin: 0; color: green;" or "margin: 0; color: black;".
 * 
 * @author Michal Kreuzman
 * @since 2.1.3
 */
public final class StandardStyleappendAttrProcessor 
		extends AbstractStandardSingleAttributeModifierAttrProcessor {
	
    public static final int ATTR_PRECEDENCE = 1100;
    public static final String ATTR_NAME = "styleappend";
    public static final String TARGET_ATTR_NAME = "style";
    
    
    public StandardStyleappendAttrProcessor() {
        super(ATTR_NAME);
    }

    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }

    @Override
    protected String getTargetAttributeName(
            final Arguments arguments, final Element element, final String attributeName) {
        return TARGET_ATTR_NAME;
    }

    @Override
    protected ModificationType getModificationType(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return ModificationType.APPEND_WITH_SPACE;
    }

    @Override
    protected boolean removeAttributeIfEmpty(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return true;
    }


}
