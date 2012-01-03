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



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.2
 *
 */
public final class Root extends NestableNode {
    

    
    public Root() {
        super();
        setChildren(null);
    }


    
    
    
    @Override
    final void doAdditionalProcess(final Arguments arguments) {
        
        if (this.childrenLen > 0) {
            for (final Node child : this.children) {
                child.process(arguments);
            }
        }
        
    }
    
    
    

    
    /*
     * *********************************
     * *********************************
     *        OUTPUT HANDLING
     * *********************************
     * *********************************
     */
    
    
    @Override
    void write(final Arguments arguments, final Writer writer) throws IOException {
        if (hasChildren()) {
            for (final Node child : this.children) {
                child.write(arguments, writer);
            }
        }
    }



    
    

    
    /*
     * *********************************
     * *********************************
     *        NODE CLONING
     * *********************************
     * *********************************
     */
    


    @Override
    Node createClonedInstance(final NestableNode newParent, final boolean cloneProcessors) {
        return new Root();
    }

    

    @Override
    void doCloneNodeInternals(final Node node, final NestableNode newParent, final boolean cloneProcessors) {
        
        final Root root = (Root) node; 
        
        if (this.childrenLen > 0) {
            final Node[] tagChildren = new Node[this.childrenLen];
            for (int i = 0; i < this.childrenLen; i++) {
                tagChildren[i] = this.children[i].cloneNode(root, cloneProcessors);
            }
            root.setChildren(tagChildren);
        }
        
    }




    
}
