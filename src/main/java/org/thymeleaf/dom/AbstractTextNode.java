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
package org.thymeleaf.dom;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public abstract class AbstractTextNode extends Node {

    protected char[] content;

    
    public AbstractTextNode(final String content) {
        super();
        Validate.notNull(content, "Content cannot be null");
        this.content = content.toCharArray();
    }

    public AbstractTextNode(final char[] content) {
        super();
        Validate.notNull(content, "Content cannot be null");
        this.content = content;
    }
    
    
    public String getContent() {
        return new String(this.content);
    }
    
    char[] unsafeGetContentCharArray() {
        return this.content;
    }

    
    public void setContent(final String content) {
        this.content = content.toCharArray();
    }
    
    public void setContent(final char[] content) {
        this.content = content;
    }
    

    
    @Override
    final void doAdditionalSkippableComputing(final boolean skippable) {
        // Nothing to be done here!
    }

    
    
    @Override
    final void doAdditionalPrecompute(final Configuration configuration) {
        // Nothing to be done here!
    }

    
    
    @Override
    final void doAdditionalProcess(final Arguments arguments) {
        if (arguments.hasTextInliner()) {
            arguments.getTextInliner().inline(arguments, this);
        }
    }
    
    
}
