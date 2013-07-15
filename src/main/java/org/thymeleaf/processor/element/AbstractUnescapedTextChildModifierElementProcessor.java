/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.processor.element;

import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateRepository;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.TemplateEngineException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.IElementNameProcessorMatcher;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractUnescapedTextChildModifierElementProcessor 
        extends AbstractMarkupSubstitutionElementProcessor {
    
    

    public AbstractUnescapedTextChildModifierElementProcessor(final String elementName) {
        super(elementName);
    }
    
    public AbstractUnescapedTextChildModifierElementProcessor(final IElementNameProcessorMatcher matcher) {
        super(matcher);
    }

    
    
    @Override
    protected List<Node> getMarkupSubstitutes(final Arguments arguments, final Element element) {
        
        final String text = getText(arguments, element);
        
        try {
            
            final TemplateRepository templateRepository = arguments.getTemplateRepository();
            final List<Node> fragNodes = templateRepository.getFragment(arguments, text);
            
            for (final Node node : fragNodes) {
                node.setProcessable(false);
            }

            return fragNodes;
            
        } catch (final TemplateEngineException e) {
            throw e;
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "An error happened during parsing of unescaped text: \"" + element.getOriginalName() + "\"", e);
        }
        
    }


    
    
    protected abstract String getText(final Arguments arguments, final Element element);
    
    
    
    
    
}
