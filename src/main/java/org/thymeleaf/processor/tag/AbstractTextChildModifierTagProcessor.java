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
package org.thymeleaf.processor.tag;

import java.util.Collections;
import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Tag;
import org.thymeleaf.dom.Text;
import org.thymeleaf.processor.ITagNameProcessorMatcher;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractTextChildModifierTagProcessor 
        extends AbstractMarkupSubstitutionTagProcessor {
    
    

    public AbstractTextChildModifierTagProcessor(final String tagName) {
        super(tagName);
    }
    
    public AbstractTextChildModifierTagProcessor(final ITagNameProcessorMatcher matcher) {
        super(matcher);
    }




    @Override
    protected List<Node> getMarkupSubstitutes(final Arguments arguments, final Tag tag) {

        final String text = getText(arguments, tag);
        
        final Text newNode = new Text(text == null? "" : text);
        // Setting this allows avoiding text inliners processing already generated text,
        // which in turn avoids code injection.
        newNode.setSkippable(true);
        
        return Collections.singletonList((Node)newNode);
        
    }

    
    protected abstract String getText(final Arguments arguments, final Tag tag);
    
}
