/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.inline.IInliner;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
final class ElementStructureHandler implements IElementStructureHandler {


    boolean setBodyText;
    String setBodyTextValue;
    boolean setBodyTextProcessable;

    boolean setBodyMarkup;
    IMarkup setBodyMarkupValue;
    boolean setBodyMarkupProcessable;

    boolean insertBeforeMarkup;
    IMarkup insertBeforeMarkupValue;
    // markup inserted before the current element CANNOT be processable

    boolean insertAfterMarkup;
    IMarkup insertAfterMarkupValue;
    boolean insertAfterMarkupProcessable;

    boolean replaceWithText;
    String replaceWithTextValue;
    boolean replaceWithTextProcessable;

    boolean replaceWithMarkup;
    IMarkup replaceWithMarkupValue;
    boolean replaceWithMarkupProcessable;

    boolean removeElement;

    boolean removeTag;

    boolean removeBody;

    boolean removeAllButFirstChild;

    boolean setLocalVariable;
    Map<String,Object> addedLocalVariables;

    boolean removeLocalVariable;
    Set<String> removedLocalVariableNames;

    boolean setSelectionTarget;
    Object selectionTargetObject;

    boolean setInliner;
    IInliner setInlinerValue;

    boolean iterateElement;
    String iterVariableName;
    String iterStatusVariableName;
    Object iteratedObject;




    ElementStructureHandler() {
        super();
        reset();
    }



    public void setBody(final String text, final boolean processable) {
        resetAllButLocalVariables();
        Validate.notNull(text, "Text cannot be null");
        this.setBodyText = true;
        this.setBodyTextValue = text;
        this.setBodyTextProcessable = processable;
    }


    public void setBody(final IMarkup markup, final boolean processable) {
        resetAllButLocalVariables();
        Validate.notNull(markup, "Markup cannot be null");
        this.setBodyMarkup = true;
        this.setBodyMarkupValue = markup;
        this.setBodyMarkupProcessable = processable;
    }


    public void insertBefore(final IMarkup markup) {
        resetAllButLocalVariables();
        Validate.notNull(markup, "Markup cannot be null");
        this.insertBeforeMarkup = true;
        this.insertBeforeMarkupValue = markup;
        // Markup inserted BEFORE can never be processable
    }


    public void insertAfter(final IMarkup markup, final boolean processable) {
        resetAllButLocalVariables();
        Validate.notNull(markup, "Markup cannot be null");
        this.insertAfterMarkup = true;
        this.insertAfterMarkupValue = markup;
        this.insertAfterMarkupProcessable = processable;
    }


    public void replaceWith(final String text, final boolean processable) {
        resetAllButLocalVariables();
        Validate.notNull(text, "Text cannot be null");
        this.replaceWithText = true;
        this.replaceWithTextValue = text;
        this.replaceWithTextProcessable = processable;
    }


    public void replaceWith(final IMarkup markup, final boolean processable) {
        resetAllButLocalVariables();
        Validate.notNull(markup, "Markup cannot be null");
        this.replaceWithMarkup = true;
        this.replaceWithMarkupValue = markup;
        this.replaceWithMarkupProcessable = processable;
    }


    public void removeElement() {
        resetAllButLocalVariables();
        this.removeElement = true;
    }


    public void removeTag() {
        resetAllButLocalVariables();
        this.removeTag = true;
    }


    public void removeBody() {
        resetAllButLocalVariables();
        this.removeBody = true;
    }


    public void removeAllButFirstChild() {
        resetAllButLocalVariables();
        this.removeAllButFirstChild = true;
    }


    public void removeLocalVariable(final String name) {
        // Can be combined with others, no need to reset
        this.removeLocalVariable = true;
        if (this.removedLocalVariableNames == null) {
            this.removedLocalVariableNames = new HashSet<String>(3);
        }
        this.removedLocalVariableNames.add(name);
    }


    public void setLocalVariable(final String name, final Object value) {
        // Can be combined with others, no need to reset
        this.setLocalVariable = true;
        if (this.addedLocalVariables == null) {
            this.addedLocalVariables = new HashMap<String, Object>(3);
        }
        this.addedLocalVariables.put(name, value);
    }


    public void setSelectionTarget(final Object selectionTarget) {
        // Can be combined with others, no need to reset
        this.setSelectionTarget = true;
        this.selectionTargetObject = selectionTarget;
    }


    public void setInliner(final IInliner inliner) {
        this.setInliner = true;
        this.setInlinerValue = inliner;
    }


    public void iterateElement(final String iterVariableName, final String iterStatusVariableName, final Object iteratedObject) {
        Validate.notEmpty(iterVariableName, "Iteration variable name cannot be null");
        // Iteration status variable name CAN be null
        // IteratedObject CAN be null
        resetAllButLocalVariables();
        this.iterateElement = true;
        this.iterVariableName = iterVariableName;
        this.iterStatusVariableName = iterStatusVariableName;
        this.iteratedObject = iteratedObject;
    }




    public void reset() {

        resetAllButLocalVariables();

        this.setLocalVariable = false;
        if (this.addedLocalVariables != null) {
            this.addedLocalVariables.clear();
        }

        this.removeLocalVariable = false;
        if (this.removedLocalVariableNames != null) {
            this.removedLocalVariableNames.clear();
        }

        this.setSelectionTarget = false;
        this.selectionTargetObject = null;

        this.setInliner = false;
        this.setInlinerValue = null;

    }


    private void resetAllButLocalVariables() {

        this.setBodyText = false;
        this.setBodyTextValue = null;
        this.setBodyTextProcessable = false;

        this.setBodyMarkup = false;
        this.setBodyMarkupValue = null;
        this.setBodyMarkupProcessable = false;

        this.insertBeforeMarkup = false;
        this.insertBeforeMarkupValue = null;
        // There is no 'insertBeforeMarkupProcessable'

        this.insertAfterMarkup = false;
        this.insertAfterMarkupValue = null;
        this.insertAfterMarkupProcessable = false;

        this.replaceWithText = false;
        this.replaceWithTextValue = null;
        this.replaceWithTextProcessable = false;

        this.replaceWithMarkup = false;
        this.replaceWithMarkupValue = null;
        this.replaceWithMarkupProcessable = false;

        this.removeElement = false;

        this.removeTag = false;

        this.removeBody = false;

        this.removeAllButFirstChild = false;

        this.iterateElement = false;
        this.iterVariableName = null;
        this.iterStatusVariableName = null;
        this.iteratedObject = null;

    }


}
