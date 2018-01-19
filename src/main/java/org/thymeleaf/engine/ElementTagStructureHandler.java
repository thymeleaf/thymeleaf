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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.model.AttributeValueQuotes;
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

    boolean setAttribute;
    Object[][] setAttributeValues;
    int setAttributeValuesSize;

    boolean replaceAttribute;
    Object[][] replaceAttributeValues;
    int replaceAttributeValuesSize;

    boolean removeAttribute;
    Object[][] removeAttributeValues;
    int removeAttributeValuesSize;

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
        resetAllButVariablesOrAttributes();
        Validate.notNull(text, "Text cannot be null");
        this.setBodyText = true;
        this.setBodyTextValue = text;
        this.setBodyTextProcessable = processable;
    }


    public void setBody(final IModel model, final boolean processable) {
        resetAllButVariablesOrAttributes();
        Validate.notNull(model, "Model cannot be null");
        this.setBodyModel = true;
        this.setBodyModelValue = model;
        this.setBodyModelProcessable = processable;
    }


    public void insertBefore(final IModel model) {
        resetAllButVariablesOrAttributes();
        Validate.notNull(model, "Model cannot be null");
        this.insertBeforeModel = true;
        this.insertBeforeModelValue = model;
        // Model inserted BEFORE can never be processable
    }


    public void insertImmediatelyAfter(final IModel model, final boolean processable) {
        resetAllButVariablesOrAttributes();
        Validate.notNull(model, "Model cannot be null");
        this.insertImmediatelyAfterModel = true;
        this.insertImmediatelyAfterModelValue = model;
        this.insertImmediatelyAfterModelProcessable = processable;
    }


    public void replaceWith(final CharSequence text, final boolean processable) {
        resetAllButVariablesOrAttributes();
        Validate.notNull(text, "Text cannot be null");
        this.replaceWithText = true;
        this.replaceWithTextValue = text;
        this.replaceWithTextProcessable = processable;
    }


    public void replaceWith(final IModel model, final boolean processable) {
        resetAllButVariablesOrAttributes();
        Validate.notNull(model, "Model cannot be null");
        this.replaceWithModel = true;
        this.replaceWithModelValue = model;
        this.replaceWithModelProcessable = processable;
    }


    public void removeElement() {
        resetAllButVariablesOrAttributes();
        this.removeElement = true;
    }


    public void removeTags() {
        resetAllButVariablesOrAttributes();
        this.removeTags = true;
    }


    public void removeBody() {
        resetAllButVariablesOrAttributes();
        this.removeBody = true;
    }


    public void removeAllButFirstChild() {
        resetAllButVariablesOrAttributes();
        this.removeAllButFirstChild = true;
    }


    public void removeLocalVariable(final String name) {
        // Can be combined with others, no need to resetGathering
        Validate.notNull(name, "Variable name cannot be null");
        this.removeLocalVariable = true;
        if (this.removedLocalVariableNames == null) {
            this.removedLocalVariableNames = new HashSet<String>(3);
        }
        this.removedLocalVariableNames.add(name);
    }


    public void setLocalVariable(final String name, final Object value) {
        // Can be combined with others, no need to resetGathering
        Validate.notNull(name, "Variable name cannot be null");
        this.setLocalVariable = true;
        if (this.addedLocalVariables == null) {
            this.addedLocalVariables = new HashMap<String, Object>(3);
        }
        this.addedLocalVariables.put(name, value);
    }


    public void setAttribute(final String attributeName, final String attributeValue) {
        // Can be combined with others, no need to resetGathering
        Validate.notNull(attributeName, "Attribute name cannot be null");
        ensureSetAttributeSize();
        this.setAttribute = true;
        final Object[] values = this.setAttributeValues[this.setAttributeValuesSize];
        values[0] = null;
        values[1] = attributeName;
        values[2] = attributeValue;
        values[3] = null;
        this.setAttributeValuesSize++;
    }


    public void setAttribute(final String attributeName, final String attributeValue, final AttributeValueQuotes attributeValueQuotes) {
        // Can be combined with others, no need to resetGathering
        Validate.notNull(attributeName, "Attribute name cannot be null");
        ensureSetAttributeSize();
        this.setAttribute = true;
        final Object[] values = this.setAttributeValues[this.setAttributeValuesSize];
        values[0] = null;
        values[1] = attributeName;
        values[2] = attributeValue;
        values[3] = attributeValueQuotes;
        this.setAttributeValuesSize++;
    }


    // NOTE this method is not part of the structure handler interface, as it uses the attributeDefinition.
    // The idea is that it can be used as an additional optimization by processors in Standard Dialects, after casting.
    public void setAttribute(final AttributeDefinition attributeDefinition, final String attributeName, final String attributeValue, final AttributeValueQuotes attributeValueQuotes) {
        // Can be combined with others, no need to resetGathering
        Validate.notNull(attributeDefinition, "Attribute definition cannot be null");
        Validate.notNull(attributeName, "Attribute name cannot be null");
        ensureSetAttributeSize();
        this.setAttribute = true;
        final Object[] values = this.setAttributeValues[this.setAttributeValuesSize];
        values[0] = attributeDefinition;
        values[1] = attributeName;
        values[2] = attributeValue;
        values[3] = attributeValueQuotes;
        this.setAttributeValuesSize++;
    }


    private void ensureSetAttributeSize() {
        if (this.setAttributeValues == null) {
            this.setAttributeValues = new Object[3][];
        }
        if (this.setAttributeValues.length == this.setAttributeValuesSize) {
            this.setAttributeValues = Arrays.copyOf(this.setAttributeValues, this.setAttributeValues.length + 3);
        }
        if (this.setAttributeValues[this.setAttributeValuesSize] == null) {
            this.setAttributeValues[this.setAttributeValuesSize] = new Object[4];
        }
    }


    public void replaceAttribute(final AttributeName oldAttributeName, final String attributeName, final String attributeValue) {
        // Can be combined with others, no need to resetGathering
        Validate.notNull(oldAttributeName, "Old attribute name cannot be null");
        Validate.notNull(attributeName, "Attribute name cannot be null");
        ensureReplaceAttributeSize();
        this.replaceAttribute = true;
        final Object[] values = this.replaceAttributeValues[this.replaceAttributeValuesSize];
        values[0] = oldAttributeName;
        values[1] = null;
        values[2] = attributeName;
        values[3] = attributeValue;
        values[4] = null;
        this.replaceAttributeValuesSize++;
    }


    public void replaceAttribute(final AttributeName oldAttributeName, final String attributeName, final String attributeValue, final AttributeValueQuotes attributeValueQuotes) {
        // Can be combined with others, no need to resetGathering
        Validate.notNull(oldAttributeName, "Old attribute name cannot be null");
        Validate.notNull(attributeName, "Attribute name cannot be null");
        ensureReplaceAttributeSize();
        this.replaceAttribute = true;
        final Object[] values = this.replaceAttributeValues[this.replaceAttributeValuesSize];
        values[0] = oldAttributeName;
        values[1] = null;
        values[2] = attributeName;
        values[3] = attributeValue;
        values[4] = attributeValueQuotes;
        this.replaceAttributeValuesSize++;
    }


    // NOTE this method is not part of the structure handler interface, as it uses the attributeDefinition.
    // The idea is that it can be used as an additional optimization by processors in Standard Dialects, after casting.
    public void replaceAttribute(final AttributeName oldAttributeName, final AttributeDefinition attributeDefinition, final String attributeName, final String attributeValue, final AttributeValueQuotes attributeValueQuotes) {
        // Can be combined with others, no need to resetGathering
        Validate.notNull(oldAttributeName, "Old attribute name cannot be null");
        Validate.notNull(attributeDefinition, "Attribute definition cannot be null");
        Validate.notNull(attributeName, "Attribute name cannot be null");
        ensureReplaceAttributeSize();
        this.replaceAttribute = true;
        final Object[] values = this.replaceAttributeValues[this.replaceAttributeValuesSize];
        values[0] = oldAttributeName;
        values[1] = attributeDefinition;
        values[2] = attributeName;
        values[3] = attributeValue;
        values[4] = attributeValueQuotes;
        this.replaceAttributeValuesSize++;
    }


    private void ensureReplaceAttributeSize() {
        if (this.replaceAttributeValues == null) {
            this.replaceAttributeValues = new Object[3][];
        }
        if (this.replaceAttributeValues.length == this.replaceAttributeValuesSize) {
            this.replaceAttributeValues = Arrays.copyOf(this.replaceAttributeValues, this.replaceAttributeValues.length + 3);
        }
        if (this.replaceAttributeValues[this.replaceAttributeValuesSize] == null) {
            this.replaceAttributeValues[this.replaceAttributeValuesSize] = new Object[5];
        }
    }


    public void removeAttribute(final String attributeName) {
        // Can be combined with others, no need to resetGathering
        Validate.notNull(attributeName, "Attribute name cannot be null");
        ensureRemoveAttributeSize();
        this.removeAttribute = true;
        final Object[] values = this.removeAttributeValues[this.removeAttributeValuesSize];
        values[0] = attributeName;
        values[1] = null;
        this.removeAttributeValuesSize++;
    }


    public void removeAttribute(final String prefix, final String name) {
        // Can be combined with others, no need to resetGathering
        Validate.notNull(name, "Attribute name cannot be null");
        ensureRemoveAttributeSize();
        this.removeAttribute = true;
        final Object[] values = this.removeAttributeValues[this.removeAttributeValuesSize];
        values[0] = prefix;
        values[1] = name;
        this.removeAttributeValuesSize++;
    }


    public void removeAttribute(final AttributeName attributeName) {
        // Can be combined with others, no need to resetGathering
        Validate.notNull(attributeName, "Attribute name cannot be null");
        ensureRemoveAttributeSize();
        this.removeAttribute = true;
        final Object[] values = this.removeAttributeValues[this.removeAttributeValuesSize];
        values[0] = attributeName;
        values[1] = null;
        this.removeAttributeValuesSize++;
    }


    private void ensureRemoveAttributeSize() {
        if (this.removeAttributeValues == null) {
            this.removeAttributeValues = new Object[3][];
        }
        if (this.removeAttributeValues.length == this.removeAttributeValuesSize) {
            this.removeAttributeValues = Arrays.copyOf(this.removeAttributeValues, this.removeAttributeValues.length + 3);
        }
        if (this.removeAttributeValues[this.removeAttributeValuesSize] == null) {
            this.removeAttributeValues[this.removeAttributeValuesSize] = new Object[2];
        }
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


    public void iterateElement(final String iterVariableName, final String iterStatusVariableName, final Object iteratedObject) {
        Validate.notEmpty(iterVariableName, "Iteration variable name cannot be null");
        // Iteration status variable name CAN be null
        // IteratedObject CAN be null
        resetAllButVariablesOrAttributes();
        this.iterateElement = true;
        this.iterVariableName = iterVariableName;
        this.iterStatusVariableName = iterStatusVariableName;
        this.iteratedObject = iteratedObject;
    }




    public void reset() {

        resetAllButVariablesOrAttributes();

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

        this.setAttribute = false;
        this.setAttributeValuesSize = 0;

        this.replaceAttribute = false;
        this.replaceAttributeValuesSize = 0;

        this.removeAttribute = false;
        this.removeAttributeValuesSize = 0;

    }


    private void resetAllButVariablesOrAttributes() {

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


    <T extends AbstractProcessableElementTag> T applyAttributes(final AttributeDefinitions attributeDefinitions, final T tag) {

        T ttag = tag;

        if (this.removeAttribute) {
            for (int i = 0; i < this.removeAttributeValuesSize; i++) {
                final Object[] values = this.removeAttributeValues[i];
                if (values[1] != null) {
                    // (String prefix, String suffix)
                    ttag = (T) ttag.removeAttribute((String)values[0], (String)values[1]);
                } else if (values[0] instanceof AttributeName) {
                    // (AttributeName attributeName)
                    ttag = (T) ttag.removeAttribute((AttributeName)values[0]);
                } else {
                    // (String attributeName)
                    ttag = (T) ttag.removeAttribute((String)values[0]);
                }
            }
        }

        if (this.replaceAttribute) {
            for (int i = 0; i < this.replaceAttributeValuesSize; i++) {
                final Object[] values = this.replaceAttributeValues[i];
                ttag = (T) ttag.replaceAttribute(attributeDefinitions, (AttributeName)values[0], (AttributeDefinition)values[1], (String)values[2], (String)values[3], (AttributeValueQuotes)values[4]);
            }
        }

        if (this.setAttribute) {
            for (int i = 0; i < this.setAttributeValuesSize; i++) {
                final Object[] values = this.setAttributeValues[i];
                ttag = (T) ttag.setAttribute(attributeDefinitions, (AttributeDefinition)values[0], (String)values[1], (String)values[2], (AttributeValueQuotes)values[3]);
            }
        }

        return ttag;

    }


}
