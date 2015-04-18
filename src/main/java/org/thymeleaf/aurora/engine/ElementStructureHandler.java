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
package org.thymeleaf.aurora.engine;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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

    boolean setBodyQueue;
    ITemplateHandlerEventQueue setBodyQueueValue;
    boolean setBodyQueueProcessable;

    boolean replaceWithText;
    String replaceWithTextValue;
    boolean replaceWithTextProcessable;

    boolean replaceWithQueue;
    ITemplateHandlerEventQueue replaceWithQueueValue;
    boolean replaceWithQueueProcessable;

    boolean removeElement;

    boolean removeTag;

    boolean setLocalVariable;
    Map<String,Object> addedLocalVariables = new LinkedHashMap<String, Object>(3);

    boolean removeLocalVariable;
    Set<String> removedLocalVariableNames = new LinkedHashSet<String>(3);

    boolean setSelectionTarget;
    Object selectionTargetObject;

    boolean setTextInliningActive;
    boolean setTextInliningActiveValue;

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


    public void setBody(final ITemplateHandlerEventQueue eventQueue, final boolean processable) {
        resetAllButLocalVariables();
        Validate.notNull(eventQueue, "Event Queue cannot be null");
        this.setBodyQueue = true;
        this.setBodyQueueValue = eventQueue;
        this.setBodyQueueProcessable = processable;
    }


    public void replaceWith(final String text, final boolean processable) {
        resetAllButLocalVariables();
        Validate.notNull(text, "Text cannot be null");
        this.replaceWithText = true;
        this.replaceWithTextValue = text;
        this.replaceWithTextProcessable = processable;
    }


    public void replaceWith(final ITemplateHandlerEventQueue eventQueue, final boolean processable) {
        resetAllButLocalVariables();
        Validate.notNull(eventQueue, "Event Queue cannot be null");
        this.replaceWithQueue = true;
        this.replaceWithQueueValue = eventQueue;
        this.replaceWithQueueProcessable = processable;
    }


    public void removeElement() {
        resetAllButLocalVariables();
        this.removeElement = true;
    }


    public void removeTag() {
        resetAllButLocalVariables();
        this.removeTag = true;
    }


    public void removeLocalVariable(final String name) {
        // Can be combined with others, no need to reset
        this.removeLocalVariable = true;
        this.removedLocalVariableNames.add(name);
    }


    public void setLocalVariable(final String name, final Object value) {
        // Can be combined with others, no need to reset
        this.setLocalVariable = true;
        this.addedLocalVariables.put(name, value);
    }


    public void setSelectionTarget(final Object selectionTarget) {
        // Can be combined with others, no need to reset
        this.setSelectionTarget = true;
        this.selectionTargetObject = selectionTarget;
    }


    public void setTextInliningActive(final boolean active) {
        this.setTextInliningActive = true;
        this.setTextInliningActiveValue = active;
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
        this.addedLocalVariables.clear();

        this.removeLocalVariable = false;
        this.removedLocalVariableNames.clear();

        this.setSelectionTarget = false;
        this.selectionTargetObject = null;

        this.setTextInliningActive = false;
        this.setTextInliningActiveValue = false;

    }


    private void resetAllButLocalVariables() {

        this.setBodyText = false;
        this.setBodyTextValue = null;
        this.setBodyTextProcessable = false;

        this.setBodyQueue = false;
        this.setBodyQueueValue = null;
        this.setBodyQueueProcessable = false;

        this.replaceWithText = false;
        this.replaceWithTextValue = null;
        this.replaceWithTextProcessable = false;

        this.replaceWithQueue = false;
        this.replaceWithQueueValue = null;
        this.replaceWithQueueProcessable = false;

        this.removeElement = false;

        this.removeTag = false;

        this.iterateElement = false;
        this.iterVariableName = null;
        this.iterStatusVariableName = null;
        this.iteratedObject = null;

    }


}
