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


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public enum TagProcessDOMAction {
    
    REMOVE_TAG(true, false, false),
    REMOVE_TAG_AND_CHILDREN(true, true, false),
    REMOVE_CHILDREN(false, true, false),
    SUBSTITUTE_TAG(true, true, true),
    NO_ACTION(false, false, false);
    
    
    private final boolean removeTag;
    private final boolean removeChildren;
    private final boolean substituteTag;


    
    
    private TagProcessDOMAction(
            final boolean removeTag, final boolean removeChildren, final boolean substituteTag) {
        this.removeTag = removeTag;
        this.removeChildren = removeChildren;
        this.substituteTag = substituteTag;
    }


    public boolean isTagRemoved() {
        return this.removeTag;
    }

    public boolean isChildrenRemoved() {
        return this.removeChildren;
    }

    public boolean isTagSubstituted() {
        return this.substituteTag;
    }
    
}
