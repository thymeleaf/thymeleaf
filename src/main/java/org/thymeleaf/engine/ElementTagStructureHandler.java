/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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

import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Structure handler implementation, internally used by the engine.
 * </p>
 * <p>
 *   This class should not be directly used from outside the engine.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class ElementTagStructureHandler implements IElementTagStructureHandler {


    boolean setBodyText;
    CharSequence setBodyTextValue;
    boolean setBodyTextProcessable;

    boolean setBodyModel;
    IModel setBodyModelValue;
    boolean setBodyModelProcessable;

    boolean insertBeforeModel;
    IModel insertBeforeModelValue;
    // model inserted before the current element CANNOT be processable

    boolean insertImmediatelyAfterModel;
    IModel insertImmediatelyAfterModelValue;
    boolean insertImmediatelyAfterModelProcessable;

    boolean replaceWithText;
    CharSequence replaceWithTextValue;
    boolean replaceWithTextProcessable;

    boolean replaceWithModel;
    IModel replaceWithModelValue;
    boolean replaceWithModelProcessable;

    boolean removeElement;

    boolean removeTags;

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

    boolean setTemplateData;
    TemplateData setTemplateDataValue;

    boolean iterateElement;
    String iterVariableName;
    String iterStatusVariableName;
    Object iteratedObject;




    ElementTagStructureHandler() {
        super();
        reset();
    }



    public void setBody(final CharSequence text, final boolean processable) {
        resetAllButLocalVariables();
        Validate.notNull(text, "Text cannot be null");
        this.setBodyText = true;
        this.setBodyTextValue = text;
        this.setBodyTextProcessable = processable;
    }


    public void setBody(final IModel model, final boolean processable) {
        resetAllButLocalVariables();
        Validate.notNull(model, "Model cannot be null");
        this.setBodyModel = true;
        this.setBodyModelValue = model;
        this.setBodyModelProcessable = processable;
    }


    public void insertBefore(final IModel model) {
        resetAllButLocalVariables();
        Validate.notNull(model, "Model cannot be null");
        this.insertBeforeModel = true;
        this.insertBeforeModelValue = model;
        // Model inserted BEFORE can never be processable
    }


    public void insertImmediatelyAfter(final IModel model, final boolean processable) {
        resetAllButLocalVariables();
        Validate.notNull(model, "Model cannot be null");
        this.insertImmediatelyAfterModel = true;
        this.insertImmediatelyAfterModelValue = model;
        this.insertImmediatelyAfterModelProcessable = processable;
    }


    public void replaceWith(final CharSequence text, final boolean processable) {
        resetAllButLocalVariables();
        Validate.notNull(text, "Text cannot be null");
        this.replaceWithText = true;
        this.replaceWithTextValue = text;
        this.replaceWithTextProcessable = processable;
    }


    public void replaceWith(final IModel model, final boolean processable) {
        resetAllButLocalVariables();
        Validate.notNull(model, "Model cannot be null");
        this.replaceWithModel = true;
        this.replaceWithModelValue = model;
        this.replaceWithModelProcessable = processable;
    }


    public void removeElement() {
        resetAllButLocalVariables();
        this.removeElement = true;
    }


    public void removeTags() {
        resetAllButLocalVariables();
        this.removeTags = true;
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


    public void setTemplateData(final TemplateData templateData) {
        this.setTemplateData = true;
        this.setTemplateDataValue = templateData;
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
        if (this.addedLocalVariables != null && this.addedLocalVariables.size() > 0) {
            this.addedLocalVariables.clear();
        }

        this.removeLocalVariable = false;
        if (this.removedLocalVariableNames != null && this.removedLocalVariableNames.size() > 0) {
            this.removedLocalVariableNames.clear();
        }

        this.setSelectionTarget = false;
        this.selectionTargetObject = null;

        this.setInliner = false;
        this.setInlinerValue = null;

        this.setTemplateData = false;
        this.setTemplateDataValue = null;

    }


    private void resetAllButLocalVariables() {

        this.setBodyText = false;
        this.setBodyTextValue = null;
        this.setBodyTextProcessable = false;

        this.setBodyModel = false;
        this.setBodyModelValue = null;
        this.setBodyModelProcessable = false;

        this.insertBeforeModel = false;
        this.insertBeforeModelValue = null;
        // There is no 'insertBeforeModelProcessable'

        this.insertImmediatelyAfterModel = false;
        this.insertImmediatelyAfterModelValue = null;
        this.insertImmediatelyAfterModelProcessable = false;

        this.replaceWithText = false;
        this.replaceWithTextValue = null;
        this.replaceWithTextProcessable = false;

        this.replaceWithModel = false;
        this.replaceWithModelValue = null;
        this.replaceWithModelProcessable = false;

        this.removeElement = false;

        this.removeTags = false;

        this.removeBody = false;

        this.removeAllButFirstChild = false;

        this.iterateElement = false;
        this.iterVariableName = null;
        this.iterStatusVariableName = null;
        this.iteratedObject = null;

    }




    void applyContextModifications(final IEngineContext engineContext) {

        if (this.setLocalVariable) {
            engineContext.setVariables(this.addedLocalVariables);
        }

        if (this.removeLocalVariable) {
            for (final String variableName : this.removedLocalVariableNames) {
                engineContext.removeVariable(variableName);
            }
        }

        if (this.setSelectionTarget) {
            engineContext.setSelectionTarget(this.selectionTargetObject);
        }

        if (this.setInliner) {
            engineContext.setInliner(this.setInlinerValue);
        }

        if (this.setTemplateData) {
            engineContext.setTemplateData(this.setTemplateDataValue);
        }

    }

}
