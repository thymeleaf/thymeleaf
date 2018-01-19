/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
import org.thymeleaf.processor.element.IElementModelStructureHandler;

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
public final class ElementModelStructureHandler implements IElementModelStructureHandler {


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




    ElementModelStructureHandler() {
        super();
        reset();
    }



    public void removeLocalVariable(final String name) {
        // Can be combined with others, no need to resetGathering
        this.removeLocalVariable = true;
        if (this.removedLocalVariableNames == null) {
            this.removedLocalVariableNames = new HashSet<String>(3);
        }
        this.removedLocalVariableNames.add(name);
    }


    public void setLocalVariable(final String name, final Object value) {
        // Can be combined with others, no need to resetGathering
        this.setLocalVariable = true;
        if (this.addedLocalVariables == null) {
            this.addedLocalVariables = new HashMap<String, Object>(3);
        }
        this.addedLocalVariables.put(name, value);
    }


    public void setSelectionTarget(final Object selectionTarget) {
        // Can be combined with others, no need to resetGathering
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




    public void reset() {

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




    void applyContextModifications(final IEngineContext engineContext) {

        if (engineContext == null) {
            return;
        }

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
