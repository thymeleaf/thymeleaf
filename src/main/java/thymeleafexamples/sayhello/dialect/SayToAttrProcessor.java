/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractTextChildModifierAttrProcessor;

public class SayToAttrProcessor 
        extends AbstractTextChildModifierAttrProcessor {

    
    public SayToAttrProcessor() {
        // Only execute this processor for 'sayto' attributes.
        super("sayto");
    }
    

    public int getPrecedence() {
        // A value of 10000 is higher than any attribute in the
        // SpringStandard dialect. So this attribute will execute
        // after all other attributes from that dialect, if in the 
        // same tag.
        return 10000;
    }

    
    /*
     * Our processor is a subclass of the convenience abstract implementation
     * 'AbstractTextChildModifierAttrProcessor', which takes care of the
     * DOM modifying stuff and allows us just to implement this 'getText(...)'
     * method to compute the text to be set as tag body.
     */
    @Override
    protected String getText(final Arguments arguments, final Element element, 
            final String attributeName) {

        return "Hello, "  + element.getAttributeValue(attributeName) + "!";
        
    }


}
