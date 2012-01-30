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


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public enum AttrProcessDOMAction {
    
    REMOVE_TAG(true, false, true, false),
    REMOVE_TAG_AND_CHILDREN(true, true, true, false),
    REMOVE_CHILDREN(false, true, true, false),
    REMOVE_ATTRIBUTE(false, false, true, false),
    SUBSTITUTE_TAG(true, true, false, true),
    NO_ACTION(false, false, false, false);
    
    
    private final boolean removeTag;
    private final boolean removeChildren;
    private final boolean removeAttribute;
    private final boolean substituteTag;


    
    
    private AttrProcessDOMAction(
            final boolean removeTag, final boolean removeChildren, 
            final boolean removeAttribute, final boolean substituteTag) {
        this.removeTag = removeTag;
        this.removeChildren = removeChildren;
        this.removeAttribute = removeAttribute;
        this.substituteTag = substituteTag;
    }


    public boolean isTagRemoved() {
        return this.removeTag;
    }

    public boolean isChildrenRemoved() {
        return this.removeChildren;
    }
    
    public boolean isAttrRemoved() {
        return this.removeAttribute;
    }

    public boolean isTagSubstituted() {
        return this.substituteTag;
    }
    
}
