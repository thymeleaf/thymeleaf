/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring4.processor;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.spring4.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.standard.processor.attr.AbstractStandardSingleAttributeModifierAttrProcessor;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class SpringMethodTagProcessor
        extends AbstractStandardSingleAttributeModifierAttrProcessor {

    
    public static final int ATTR_PRECEDENCE = 990;
    public static final String ATTR_NAME = "method";
    
    
    
    public SpringMethodTagProcessor() {
        super(ATTR_NAME);
    }

    
    
    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }



    @Override
    protected String getTargetAttributeName(
            final Arguments arguments, final Element element, final String attributeName) {
        return ATTR_NAME;
    }

    
    @Override
    protected ModificationType getModificationType(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return ModificationType.SUBSTITUTION;
    }



    @Override
    protected boolean removeAttributeIfEmpty(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return true;
    }


    


    

    @Override
    protected void doAdditionalProcess(
            final Arguments arguments, final Element element, final String attributeName) {
        
        if ("form".equals(element.getNormalizedName())) {

            final String method = element.getAttributeValue("method");
            
            if (!isMethodBrowserSupported(method)) {
                
                // Browsers only support HTTP GET and POST. If a different method
                // has been specified, then Spring MVC allows us to specify it
                // using a hidden input with name '_method' and set 'post' for the
                // <form> tag.

                final String type = "hidden";
                final String name = "_method";
                final String value =
                        RequestDataValueProcessorUtils.processFormFieldValue(
                                arguments.getConfiguration(), arguments, name, method, type);

                final Element hiddenMethodElement = new Element("input");
                hiddenMethodElement.setAttribute("type", type);
                hiddenMethodElement.setAttribute("name", name);
                hiddenMethodElement.setAttribute("value", value);

                element.insertChild(0, hiddenMethodElement);
                
                element.setAttribute("method", "post");
                
            }
            
        }
        
    }




    /*
     * Determine if the HTTP method is supported by browsers (i.e. GET or POST).
     */
    protected boolean isMethodBrowserSupported(final String method) {
        return ("get".equalsIgnoreCase(method) || "post".equalsIgnoreCase(method));
    }

    
}
