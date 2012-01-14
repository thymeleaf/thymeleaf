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

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.Arguments;
import org.thymeleaf.util.DOMUtils;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class Text extends AbstractTextNode {

    
    public Text(final String content) {
        super(content);
    }

    public Text(final char[] content) {
        super(content);
    }
    


    
    
    

    @Override
    Node createClonedInstance(final NestableNode newParent, final boolean cloneProcessors) {
        return new Text(this.content);
    }
    

    
    
    @Override
    void doCloneNodeInternals(final Node node, final NestableNode newParent, final boolean cloneProcessors) {
        // Nothing to be done here
    }




    
    @Override
    void write(final Arguments arguments, final Writer writer) throws IOException {
        DOMUtils.writeXmlEscaped(this.content, writer, false);
    }



    

    public static final Text translateDOMText(final org.w3c.dom.Text domNode, final NestableNode parentNode) {
        final Text text = new Text(domNode.getData());
        text.parent = parentNode;
        return text;
    }
    
    
}
