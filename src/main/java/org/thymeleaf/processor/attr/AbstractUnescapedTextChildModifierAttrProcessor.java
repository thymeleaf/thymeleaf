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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.DOMExecution;
import org.thymeleaf.exceptions.AttrProcessorException;
import org.thymeleaf.exceptions.TemplateEngineException;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractUnescapedTextChildModifierAttrProcessor 
        extends AbstractChildrenModifierAttrProcessor {
    
    
    
    public AbstractUnescapedTextChildModifierAttrProcessor() {
        super();
    }
    
    
    
    @Override
    protected final List<Node> getNewChildren(
            final Arguments arguments, final TemplateResolution templateResolution, 
            final Document document, final Element element, 
            final Attr attribute, final String attributeName, final String attributeValue) {
        
        final String text = getText(arguments, templateResolution, document, element, attribute, attributeName, attributeValue);
        
        try {
            
            // Use the parser to obtain a DOM from the String
            final Node fragNode = 
                arguments.getTemplateParser().parseXMLString(arguments, text);
            
            // Imports the nodes from the fragment document to the template one
            final Node node = document.importNode(fragNode, true);
            
            final List<Node> children = new ArrayList<Node>();
            final NodeList nodeList = node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                
                final Node child = nodeList.item(i);
                
                // Setting this allows avoiding text inliners processing already generated nodes or text,
                // which in turn avoids code injection.
                DOMExecution.setExecutableTree(child, false);
                
                children.add(child);
                
            }
            
            return Collections.unmodifiableList(children);
            
        } catch (final TemplateEngineException e) {
            throw e;
        } catch (final Exception e) {
            throw new AttrProcessorException(
                    "An error happened during parsing of unescaped text: \"" + attributeValue + "\"");
        }
        
    }

    
    protected abstract String getText(
            final Arguments arguments, final TemplateResolution templateResolution, 
            final Document document, final Element element, 
            final Attr attribute, final String attributeName, final String attributeValue);
    
    
    
}
