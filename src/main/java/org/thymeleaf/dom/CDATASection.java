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
import org.thymeleaf.Configuration;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.2
 *
 */
public final class CDATASection extends AbstractTextNode {

    private static final char[] CDATA_PREFIX = "<![CDATA[".toCharArray();
    private static final char[] CDATA_SUFFIX = "]]>".toCharArray();
    
    
    public CDATASection(final String content) {
        super(content);
    }
    
    public CDATASection(final char[] content) {
        super(content);
    }
    


    
    @Override
    protected void precomputeNode(final Configuration configuration) {
        // Nothing to be done
    }

    
    
    
    @Override
    protected Node doCloneNode(final NestableNode newParent, final boolean cloneProcessors) {
        return new CDATASection(this.content);
    }


    
    
    @Override
    public void write(final Arguments arguments, final Writer writer) throws IOException {
        writer.write(CDATA_PREFIX);
        writer.write(this.content);
        writer.write(CDATA_SUFFIX);
    }


    
    
    public static final CDATASection translateDOMCDATASection(final org.w3c.dom.CDATASection domNode, final NestableNode parentNode) {
        final CDATASection cdata = new CDATASection(domNode.getData());
        cdata.parent = parentNode;
        return cdata;
    }
    
    
}
