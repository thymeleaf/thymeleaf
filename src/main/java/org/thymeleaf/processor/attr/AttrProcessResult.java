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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thymeleaf.inliner.ITextInliner;
import org.thymeleaf.processor.SubstitutionTag;
import org.thymeleaf.util.Validate;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class AttrProcessResult {

    
    private static final List<SubstitutionTag> EMPTY_TAGS = Collections.unmodifiableList(new ArrayList<SubstitutionTag>());
    private static final Map<String,Object> EMPTY_VARIABLES = Collections.unmodifiableMap(new HashMap<String, Object>());

    
    
    public static final AttrProcessResult REMOVE_TAG = new AttrProcessResult(AttrProcessDOMAction.REMOVE_TAG, null, null, null, false, null, false);
    public static final AttrProcessResult REMOVE_TAG_AND_CHILDREN = new AttrProcessResult(AttrProcessDOMAction.REMOVE_TAG_AND_CHILDREN, null, null, null, false, null, false);
    public static final AttrProcessResult REMOVE_CHILDREN = new AttrProcessResult(AttrProcessDOMAction.REMOVE_CHILDREN, null, null, null, false, null, false);
    public static final AttrProcessResult REMOVE_ATTRIBUTE = new AttrProcessResult(AttrProcessDOMAction.REMOVE_ATTRIBUTE, null, null, null, false, null, false);
    public static final AttrProcessResult NO_ACTION = new AttrProcessResult(AttrProcessDOMAction.NO_ACTION, null, null, null, false, null, false);
    
    private final AttrProcessDOMAction action;
    private final List<SubstitutionTag> substitutionTags;
    private final Map<String,Object> localVariables;
    private final Object selectionTarget;
    private final boolean selectionTargetSet;
    private final ITextInliner textInliner;
    private final boolean textInlinerSet;


    
    public static AttrProcessResult forRemoveTag() {
        return REMOVE_TAG;
    }
    
    public static AttrProcessResult forRemoveTag(final ITextInliner textInliner) {
        return new AttrProcessResult(AttrProcessDOMAction.REMOVE_TAG, null, null, null, false, textInliner, true);
    }
    
    public static AttrProcessResult forRemoveTag(final Map<String,Object> localVariables) {
        return new AttrProcessResult(AttrProcessDOMAction.REMOVE_TAG, null, localVariables, null, false, null, false);
    }
    
    public static AttrProcessResult forRemoveTag(final Map<String,Object> localVariables, final ITextInliner textInliner) {
        return new AttrProcessResult(AttrProcessDOMAction.REMOVE_TAG, null, localVariables, null, false, textInliner, true);
    }
    
    public static AttrProcessResult forRemoveTagWithSelectionTarget(final Object target) {
        return new AttrProcessResult(AttrProcessDOMAction.REMOVE_TAG, null, null, target, true, null, false);
    }
    
    public static AttrProcessResult forRemoveTagWithSelectionTarget(final ITextInliner textInliner, final Object target) {
        return new AttrProcessResult(AttrProcessDOMAction.REMOVE_TAG, null, null, target, true, textInliner, true);
    }
    
    public static AttrProcessResult forRemoveTagWithSelectionTarget(final Map<String,Object> localVariables, final Object target) {
        return new AttrProcessResult(AttrProcessDOMAction.REMOVE_TAG, null, localVariables, target, true, null, false);
    }
    
    public static AttrProcessResult forRemoveTagWithSelectionTarget(final Map<String,Object> localVariables, final ITextInliner textInliner, final Object target) {
        return new AttrProcessResult(AttrProcessDOMAction.REMOVE_TAG, null, localVariables, target, true, textInliner, true);
    }
    

    
    
    public static AttrProcessResult forRemoveTagAndChildren() {
        return REMOVE_TAG_AND_CHILDREN;
    }
    
    public static AttrProcessResult forRemoveTagAndChildrenWithSelectionTarget(final Object target) {
        return new AttrProcessResult(AttrProcessDOMAction.REMOVE_TAG_AND_CHILDREN, null, null, target, true, null, false);
    }


    
    
    public static AttrProcessResult forRemoveChildren() {
        return REMOVE_CHILDREN;
    }
    
    public static AttrProcessResult forRemoveChildrenWithSelectionTarget(final Object target) {
        return new AttrProcessResult(AttrProcessDOMAction.REMOVE_CHILDREN, null, null, target, true, null, false);
    }
    

    
    
    public static AttrProcessResult forRemoveAttribute() {
        return REMOVE_ATTRIBUTE;
    }
    
    public static AttrProcessResult forRemoveAttribute(final ITextInliner textInliner) {
        return new AttrProcessResult(AttrProcessDOMAction.REMOVE_ATTRIBUTE, null, null, null, false, textInliner, true);
    }
    
    public static AttrProcessResult forRemoveAttribute(final Map<String,Object> localVariables) {
        return new AttrProcessResult(AttrProcessDOMAction.REMOVE_ATTRIBUTE, null, localVariables, null, false, null, false);
    }
    
    public static AttrProcessResult forRemoveAttribute(final Map<String,Object> localVariables, final ITextInliner textInliner) {
        return new AttrProcessResult(AttrProcessDOMAction.REMOVE_ATTRIBUTE, null, localVariables, null, false, textInliner, true);
    }
    
    public static AttrProcessResult forRemoveAttributeWithSelectionTarget(final Object target) {
        return new AttrProcessResult(AttrProcessDOMAction.REMOVE_ATTRIBUTE, null, null, target, true, null, false);
    }
    
    public static AttrProcessResult forRemoveAttributeWithSelectionTarget(final ITextInliner textInliner, final Object target) {
        return new AttrProcessResult(AttrProcessDOMAction.REMOVE_ATTRIBUTE, null, null, target, true, textInliner, true);
    }
    
    public static AttrProcessResult forRemoveAttributeWithSelectionTarget(final Map<String,Object> localVariables, final Object target) {
        return new AttrProcessResult(AttrProcessDOMAction.REMOVE_ATTRIBUTE, null, localVariables, target, true, null, false);
    }
    
    public static AttrProcessResult forRemoveAttributeWithSelectionTarget(final Map<String,Object> localVariables, final ITextInliner textInliner, final Object target) {
        return new AttrProcessResult(AttrProcessDOMAction.REMOVE_ATTRIBUTE, null, localVariables, target, true, textInliner, true);
    }
    
    
    

    
    public static AttrProcessResult forNoAction() {
        return NO_ACTION;
    }
    
    public static AttrProcessResult forNoAction(final ITextInliner textInliner) {
        return new AttrProcessResult(AttrProcessDOMAction.NO_ACTION, null, null, null, false, textInliner, true);
    }
    
    public static AttrProcessResult forNoAction(final Map<String,Object> localVariables) {
        return new AttrProcessResult(AttrProcessDOMAction.NO_ACTION, null, localVariables, null, false, null, false);
    }
    
    public static AttrProcessResult forNoAction(final Map<String,Object> localVariables, final ITextInliner textInliner) {
        return new AttrProcessResult(AttrProcessDOMAction.NO_ACTION, null, localVariables, null, false, textInliner, true);
    }
    
    public static AttrProcessResult forNoActionWithSelectionTarget(final Object target) {
        return new AttrProcessResult(AttrProcessDOMAction.NO_ACTION, null, null, target, true, null, false);
    }
    
    public static AttrProcessResult forNoActionWithSelectionTarget(final ITextInliner textInliner, final Object target) {
        return new AttrProcessResult(AttrProcessDOMAction.NO_ACTION, null, null, target, true, textInliner, true);
    }
    
    public static AttrProcessResult forNoActionWithSelectionTarget(final Map<String,Object> localVariables, final Object target) {
        return new AttrProcessResult(AttrProcessDOMAction.NO_ACTION, null, localVariables, target, true, null, false);
    }
    
    public static AttrProcessResult forNoActionWithSelectionTarget(final Map<String,Object> localVariables, final ITextInliner textInliner, final Object target) {
        return new AttrProcessResult(AttrProcessDOMAction.NO_ACTION, null, localVariables, target, true, textInliner, true);
    }


    
    
    
    public static AttrProcessResult forSubstituteTag(final List<SubstitutionTag> substitutionTags) {
        return new AttrProcessResult(AttrProcessDOMAction.SUBSTITUTE_TAG, substitutionTags, null, null, false, null, false);
    }
    
    public static AttrProcessResult forSubstituteTag(final List<SubstitutionTag> substitutionTags, final ITextInliner textInliner) {
        return new AttrProcessResult(AttrProcessDOMAction.SUBSTITUTE_TAG, substitutionTags, null, null, false, textInliner, true);
    }
    
    public static AttrProcessResult forSubstituteTagWithSelectionTarget(final List<SubstitutionTag> substitutionTags, final Object target) {
        return new AttrProcessResult(AttrProcessDOMAction.SUBSTITUTE_TAG, substitutionTags, null, target, true, null, false);
    }
    
    public static AttrProcessResult forSubstituteTagWithSelectionTarget(final List<SubstitutionTag> substitutionTags, final ITextInliner textInliner, final Object target) {
        return new AttrProcessResult(AttrProcessDOMAction.SUBSTITUTE_TAG, substitutionTags, null, target, true, textInliner, true);
    }

    
    

    
    private AttrProcessResult(
            final AttrProcessDOMAction action,
            final List<SubstitutionTag> substitutionTags,
            final Map<String,Object> localVariables,
            final Object selectionTarget,
            final boolean selectionTargetSet, 
            final ITextInliner textInliner,
            final boolean textInlinerSet) {
        super();
        Validate.notNull(action, "Action cannot be null");
        this.action = action;
        this.substitutionTags =
            (substitutionTags == null?
                    EMPTY_TAGS :
                    Collections.unmodifiableList(new ArrayList<SubstitutionTag>(substitutionTags)));
        this.localVariables =
            (localVariables == null?
                    EMPTY_VARIABLES :
                    Collections.unmodifiableMap(new HashMap<String, Object>(localVariables)));
        this.selectionTarget = selectionTarget;
        this.selectionTargetSet = selectionTargetSet;
        this.textInliner = textInliner;
        this.textInlinerSet = textInlinerSet;
    }


    public AttrProcessDOMAction getAction() {
        return this.action;
    }

    
    public boolean hasSubstitutionTags() {
        return (this.substitutionTags != null && this.substitutionTags.size() > 0);
    }

    public List<SubstitutionTag> getSubstitutionTags() {
        return this.substitutionTags;
    }
    
    
    public boolean hasLocalVariables() {
        return (this.localVariables != null && this.localVariables.size() > 0);
    }

    public Map<String, Object> getLocalVariables() {
        return this.localVariables;
    }
    
    public Object getSelectionTarget() {
        return this.selectionTarget;
    }
    
    public boolean isSelectionTargetSet() {
        return this.selectionTargetSet;
    }
    
    public ITextInliner getTextInliner() {
        return this.textInliner;
    }
    
    public boolean isTextInlinerSet() {
        return this.textInlinerSet;
    }
    
    
}
