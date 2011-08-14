/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package thymeleafexamples.sayhello.dialect;

import java.util.Set;

import org.thymeleaf.Arguments;
import org.thymeleaf.processor.applicability.AttrApplicability;
import org.thymeleaf.processor.attr.AbstractTextChildModifierAttrProcessor;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;
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
        // Only execute this processor for 'saytoplanet' attributes.
        return AttrApplicability.createSetForAttrName("saytoplanet");
    }

    
    public Integer getPrecedence() {
        // Higher (less-precedent) than any attribute in the
        // SpringStandard dialect and also than 'sayto'.
        return Integer.valueOf(11000);
    }


    
    @Override
    protected String getText(final Arguments arguments,
            final TemplateResolution templateResolution, final Document document,
            final Element element, final Attr attribute, final String attributeName,
            final String attributeValue) {

        final String planet =
            (String) StandardExpressionProcessor.processExpression(arguments, templateResolution, attributeValue);

        /*
         * This 'getMessage(...)' method will first try to resolve the
         * message as a 'template message' (one that is defined for a specific 
         * template or templates, and that would be resolved, in a Spring MVC app, 
         * by Spring's MessageSource objects).
         * 
         * If not found, it will try to resolve it as a 'processor message', a type
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
