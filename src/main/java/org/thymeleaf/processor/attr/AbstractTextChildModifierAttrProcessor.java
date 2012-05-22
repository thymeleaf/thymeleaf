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
package org.thymeleaf.processor.attr;

import java.util.Collections;
import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.DOMExecution;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractTextChildModifierAttrProcessor 
        extends AbstractChildrenModifierAttrProcessor {
    
    
    
    public AbstractTextChildModifierAttrProcessor() {
        super();
    }
    
    
    
    @Override
    protected final List<Node> getNewChildren(
            final Arguments arguments, final TemplateResolution templateResolution, 
            final Document document, final Element element, 
            final Attr attribute, final String attributeName, final String attributeValue) {
        
        final String text = getText(arguments, templateResolution, document, element, attribute, attributeName, attributeValue);
        
        final Text newNode = document.createTextNode(text == null? "" : text);
        
        // Setting this allows avoiding text inliners processing already generated text,
        // which in turn avoids code injection.
        DOMExecution.setExecutableNode(newNode, false);
        
        return Collections.singletonList((Node)newNode);
        
    }

    
    protected abstract String getText(
            final Arguments arguments, final TemplateResolution templateResolution, 
            final Document document, final Element element, 
            final Attr attribute, final String attributeName, final String attributeValue);
    
}
