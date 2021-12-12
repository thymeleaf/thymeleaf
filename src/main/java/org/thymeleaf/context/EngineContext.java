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
package org.thymeleaf.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.inline.NoOpInliner;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Basic non-web implementation of the {@link IEngineContext} interface.
 * </p>
 * <p>
 *   This is the context implementation that will be used by default for non-web processing. Note that <b>this is an
 *   internal implementation, and that there is no reason for users' code to directly reference or use it instead
 *   of its implemented interfaces</b>. Normally, the only reason to directly use this class would be as a part of
 *   some kind of integration/extension efforts (e.g. making Thymeleaf work in a specific type of non-Servlet-based
 *   application server).
 * </p>
 * <p>
 *   This class is NOT thread-safe. Thread-safety is not a requirement for context implementations.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class EngineContext extends AbstractEngineContext implements IEngineContext {

    /*
     * This class is in charge of managing the map of variables in place at each moment in the template processing,
     * by taking care of the different model levels the process is traversing and storing local variables only
     * for the levels they correspond to.
     */

    private static final int DEFAULT_ELEMENT_HIERARCHY_SIZE = 20;
    private static final int DEFAULT_LEVELS_SIZE = 10;
    private static final int DEFAULT_MAP_SIZE = 5;


    private int level = 0;
    private int index = 0;
    private int[] levels;
    private HashMap<String,Object>[] maps;
    private SelectionTarget[] selectionTargets;
    private IInliner[] inliners;
    private TemplateData[] templateDatas;
    private IProcessableElementTag[] elementTags;

    private SelectionTarget lastSelectionTarget = null;
    private IInliner lastInliner = null;
    private TemplateData lastTemplateData = null;

    private final List<TemplateData> templateStack;


    private static final Object NON_EXISTING = new Object() {
        @Override
        public String toString() {
            return "(*removed*)";
        }
    };

    // Using a constant for NULL will avoid the need to perform contains() calls on the maps to determine if an entry exists
    private static final Object NULL = new Object() {
        @Override
        public String toString() {
            return "null";
        }
    };



    /**
     * <p>
     *   Creates a new instance of this {@link IEngineContext} implementation.
     * </p>
     * <p>
     *   Note that implementations of {@link IEngineContext} are not meant to be used in order to call
     *   the template engine (use implementations of {@link IContext} such as {@link Context} or {@link WebContext}
     *   instead). This is therefore mostly an <b>internal</b> implementation, and users should have no reason
     *   to ever call this constructor except in very specific integration/extension scenarios.
     * </p>
     *
     * @param configuration the configuration instance being used.
     * @param templateData the template data for the template to be processed.
     * @param templateResolutionAttributes the template resolution attributes.
     * @param locale the locale.
     * @param variables the context variables, probably coming from another {@link IContext} implementation.
     */
    public EngineContext(
            final IEngineConfiguration configuration,
            final TemplateData templateData,
            final Map<String,Object> templateResolutionAttributes,
            final Locale locale,
            final Map<String, Object> variables) {

        super(configuration, templateResolutionAttributes, locale);

        this.levels = new int[DEFAULT_LEVELS_SIZE];
        this.maps = (HashMap<String, Object>[]) new HashMap<?,?>[DEFAULT_LEVELS_SIZE];
        this.selectionTargets = new SelectionTarget[DEFAULT_LEVELS_SIZE];
        this.inliners = new IInliner[DEFAULT_LEVELS_SIZE];
        this.templateDatas = new TemplateData[DEFAULT_LEVELS_SIZE];

        this.elementTags = new IProcessableElementTag[DEFAULT_ELEMENT_HIERARCHY_SIZE];

        Arrays.fill(this.levels, Integer.MAX_VALUE);
        Arrays.fill(this.maps, null);
        Arrays.fill(this.selectionTargets, null);
        Arrays.fill(this.inliners, null);
        Arrays.fill(this.templateDatas, null);

        Arrays.fill(this.elementTags, null);

        this.levels[0] = 0;
        this.templateDatas[0] = templateData;
        this.lastTemplateData = templateData;

        this.templateStack = new ArrayList<TemplateData>(DEFAULT_LEVELS_SIZE);
        this.templateStack.add(templateData);

        if (variables != null) {
            setVariables(variables);
        }

    }


    public boolean containsVariable(final String name) {
        int n = this.index + 1;
        Object value;
        HashMap map;
        while (n-- != 0) {
            map = this.maps[n];
            if (map != null && map.size() > 0) {
                // Even if the value is null, we will have a value (null is inserted as the NULL constant)
                value = map.get(name);
                if (value != null) {
                    // The most modern entry we find for this key could be a removal --> false
                    return value != NON_EXISTING;
                }
            }
        }
        return false;
    }


    public Object getVariable(final String key) {
        int n = this.index + 1;
        Object value;
        HashMap map;
        while (n-- != 0) {
            map = this.maps[n];
            if (map != null && map.size() > 0) {
                // Even if the value is null, we will have a value (null is inserted as the NULL constant)
                value = map.get(key);
                if (value != null) {
                    if (value == NON_EXISTING || value == NULL) {
                        return null;
                    }
                    return resolveLazy(value);
                }
            }
        }
        return null;
    }


    public Set<String> getVariableNames() {

        final Set<String> variableNames = new HashSet<String>();
        int n = this.index + 1;
        int i = 0;
        while (n-- != 0) {
            if (this.maps[i] != null) {
                for (final Map.Entry<String,Object> mapEntry : this.maps[i].entrySet()) {
                    if (mapEntry.getValue() == NON_EXISTING) {
                        variableNames.remove(mapEntry.getKey());
                        continue;
                    }
                    variableNames.add(mapEntry.getKey());
                }
            }
            i++;
        }
        return variableNames;

    }


    public void setVariable(final String name, final Object value) {

        ensureLevelInitialized(DEFAULT_MAP_SIZE);

        if (value == NON_EXISTING && this.level == 0) {
            this.maps[this.index].remove(name);
        } else {
            if (value == null) {
                this.maps[this.index].put(name, NULL);
            } else {
                this.maps[this.index].put(name, value);
            }
        }

    }


    public void setVariables(final Map<String, Object> variables) {

        if (variables == null || variables.isEmpty()) {
            return;
        }

        ensureLevelInitialized(Math.max(DEFAULT_MAP_SIZE, variables.size() + 2));

        for (final Map.Entry<String, Object> entry : variables.entrySet()) {
            final Object value = entry.getValue();
            if (value == null) {
                this.maps[this.index].put(entry.getKey(), NULL);
            } else {
                this.maps[this.index].put(entry.getKey(), value);
            }
        }

    }




    public void removeVariable(final String name) {
        if (containsVariable(name)) {
            setVariable(name, NON_EXISTING);
        }
    }




    public boolean isVariableLocal(final String name) {
        int n = this.index + 1;
        while (n-- > 1) { // variables at n == 0 are not local!
            if (this.maps[n] != null && this.maps[n].containsKey(name)) {
                final Object result = this.maps[n].get(name);
                if (result == NON_EXISTING) {
                    return false; // We return false for "non existing"
                }
                return true;
            }
        }
        return false; // We return false for "non existing"
    }




    public boolean hasSelectionTarget() {
        if (this.lastSelectionTarget != null) {
            return true;
        }
        int n = this.index + 1;
        while (n-- != 0) {
            if (this.selectionTargets[n] != null) {
                return true;
            }
        }
        return false;
    }


    public Object getSelectionTarget() {
        if (this.lastSelectionTarget != null) {
            return this.lastSelectionTarget.selectionTarget;
        }
        int n = this.index + 1;
        while (n-- != 0) {
            if (this.selectionTargets[n] != null) {
                this.lastSelectionTarget = this.selectionTargets[n];
                return this.lastSelectionTarget.selectionTarget;
            }
        }
        return null;
    }


    public void setSelectionTarget(final Object selectionTarget) {
        ensureLevelInitialized(-1);
        this.lastSelectionTarget = new SelectionTarget(selectionTarget);
        this.selectionTargets[this.index] = this.lastSelectionTarget;
    }




    public IInliner getInliner() {
        if (this.lastInliner != null) {
            if (this.lastInliner == NoOpInliner.INSTANCE) {
                return null;
            }
            return this.lastInliner;
        }
        int n = this.index + 1;
        while (n-- != 0) {
            if (this.inliners[n] != null) {
                this.lastInliner = this.inliners[n];
                if (this.lastInliner == NoOpInliner.INSTANCE) {
                    return null;
                }
                return this.lastInliner;
            }
        }
        return null;
    }


    public void setInliner(final IInliner inliner) {
        ensureLevelInitialized(-1);
        // We use NoOpInliner.INSTANCE in order to signal when inlining has actually been disabled
        this.lastInliner = (inliner == null? NoOpInliner.INSTANCE : inliner);
        this.inliners[this.index] = this.lastInliner;
    }




    public TemplateData getTemplateData() {
        if (this.lastTemplateData != null) {
            return this.lastTemplateData;
        }
        int n = this.index + 1;
        while (n-- != 0) {
            if (this.templateDatas[n] != null) {
                this.lastTemplateData = this.templateDatas[n];
                return this.lastTemplateData;
            }
        }
        return null;
    }


    public void setTemplateData(final TemplateData templateData) {
        Validate.notNull(templateData, "Template Data cannot be null");
        ensureLevelInitialized(-1);
        this.lastTemplateData = templateData;
        this.templateDatas[this.index] = this.lastTemplateData;
        this.templateStack.clear();
    }




    public List<TemplateData> getTemplateStack() {
        if (!this.templateStack.isEmpty()) {
            // If would have been empty if we had just decreased a level or added a new template
            return Collections.unmodifiableList(new ArrayList<TemplateData>(this.templateStack));
        }
        for (int i = 0; i <= this.index; i++) {
            if (this.templateDatas[i] != null) {
                this.templateStack.add(this.templateDatas[i]);
            }
        }

        return Collections.unmodifiableList(new ArrayList<TemplateData>(this.templateStack));
    }




    public void setElementTag(final IProcessableElementTag elementTag) {
        if (this.elementTags.length <= this.level) {
            this.elementTags = Arrays.copyOf(this.elementTags, Math.max(this.level, this.elementTags.length + DEFAULT_ELEMENT_HIERARCHY_SIZE));
        }
        this.elementTags[this.level] = elementTag;
    }




    public List<IProcessableElementTag> getElementStack() {
        final List<IProcessableElementTag> elementStack = new ArrayList<IProcessableElementTag>(this.level);
        for (int i = 0; i <= this.level && i < this.elementTags.length; i++) {
            if (this.elementTags[i] != null) {
                elementStack.add(this.elementTags[i]);
            }
        }

        return Collections.unmodifiableList(elementStack);
    }


    public List<IProcessableElementTag> getElementStackAbove(final int contextLevel) {
        final List<IProcessableElementTag> elementStack = new ArrayList<IProcessableElementTag>(this.level);
        for (int i = contextLevel + 1; i <= this.level && i < this.elementTags.length; i++) {
            if (this.elementTags[i] != null) {
                elementStack.add(this.elementTags[i]);
            }
        }

        return Collections.unmodifiableList(elementStack);
    }




    private void ensureLevelInitialized(final int requiredSize) {

        // First, check if the current index already signals the current level (in which case, everything is OK)
        if (this.levels[this.index] != this.level) {

            // The current level still had no index assigned -- we must do it, and maybe even grow structures

            this.index++; // This new index will be the one for our level

            if (this.levels.length == this.index) {
                this.levels = Arrays.copyOf(this.levels, this.levels.length + DEFAULT_LEVELS_SIZE);
                Arrays.fill(this.levels, this.index, this.levels.length, Integer.MAX_VALUE); // We fill the new places with MAX_VALUE
                this.maps = Arrays.copyOf(this.maps, this.maps.length + DEFAULT_LEVELS_SIZE);
                this.selectionTargets = Arrays.copyOf(this.selectionTargets, this.selectionTargets.length + DEFAULT_LEVELS_SIZE);
                this.inliners = Arrays.copyOf(this.inliners, this.inliners.length + DEFAULT_LEVELS_SIZE);
                this.templateDatas = Arrays.copyOf(this.templateDatas, this.templateDatas.length + DEFAULT_LEVELS_SIZE);
            }

            this.levels[this.index] = this.level;

        }

        if (requiredSize >= 0 && this.maps[this.index] == null) {
            // The map for this level has not yet been created
            this.maps[this.index] = new HashMap<String,Object>(requiredSize, 1.0f);
        }

    }


    public int level() {
        return this.level;
    }


    public void increaseLevel() {
        this.level++;
    }


    public void decreaseLevel() {

        Validate.isTrue(this.level > 0, "Cannot decrease variable map level below 0");

        if (this.levels[this.index] == this.level) {

            this.levels[this.index] = Integer.MAX_VALUE;
            if (this.maps[this.index] != null) {
                this.maps[this.index].clear();
            }
            this.selectionTargets[this.index] = null;
            this.inliners[this.index] = null;
            this.templateDatas[this.index] = null;
            this.index--;

            // These might not belong to this level, but just in case...
            this.lastSelectionTarget = null;
            this.lastInliner = null;
            this.lastTemplateData = null;
            this.templateStack.clear();

        }

        if (this.level < this.elementTags.length) {
            this.elementTags[this.level] = null;
        }

        this.level--;

    }


    public String getStringRepresentationByLevel() {

        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append('{');
        int n = this.index + 1;
        while (n-- != 0) {
            final Map<String,Object> levelVars = new LinkedHashMap<String, Object>();
            if (this.maps[n] != null) {
                final List<String> entryNames = new ArrayList<String>(this.maps[n].keySet());
                Collections.sort(entryNames);
                for (final String name : entryNames) {
                    final Object value = this.maps[n].get(name);
                    if (value == NON_EXISTING) {
                        // We only have to add this if it is really removing anything
                        int n2 = n;
                        while (n2-- != 0) {
                            if (this.maps[n2] != null && this.maps[n2].containsKey(name)) {
                                if (this.maps[n2].get(name) != NON_EXISTING) {
                                    levelVars.put(name, value);
                                }
                                break;
                            }
                        }
                        continue;
                    }
                    levelVars.put(name, value);
                }
            }
            if (n == 0 || !levelVars.isEmpty() || this.selectionTargets[n] != null || this.inliners[n] != null || this.templateDatas[n] != null) {
                if (strBuilder.length() > 1) {
                    strBuilder.append(',');
                }
                strBuilder.append(this.levels[n]).append(":");
                if (!levelVars.isEmpty() || n == 0) {
                    strBuilder.append(levelVars);
                }
                if (this.selectionTargets[n] != null) {
                    strBuilder.append("<").append(this.selectionTargets[n].selectionTarget).append(">");
                }
                if (this.inliners[n] != null) {
                    strBuilder.append("[").append(this.inliners[n].getName()).append("]");
                }
                if (this.templateDatas[n] != null) {
                    strBuilder.append("(").append(this.templateDatas[n].getTemplate()).append(")");
                }
            }
        }
        strBuilder.append("}[");
        strBuilder.append(this.level);
        strBuilder.append(']');
        return strBuilder.toString();

    }




    @Override
    public String toString() {

        final Map<String,Object> equivalentMap = new LinkedHashMap<String, Object>();
        int n = this.index + 1;
        int i = 0;
        while (n-- != 0) {
            if (this.maps[i] != null) {
                final List<String> entryNames = new ArrayList<String>(this.maps[i].keySet());
                Collections.sort(entryNames);
                for (final String name : entryNames) {
                    final Object value = this.maps[i].get(name);
                    if (value == NON_EXISTING) {
                        equivalentMap.remove(name);
                        continue;
                    }
                    equivalentMap.put(name, value);
                }
            }
            i++;
        }
        final String textInliningStr = (getInliner() != null? "[" + getInliner().getName() + "]" : "" );
        final String templateDataStr = "(" + getTemplateData().getTemplate() + ")";
        return equivalentMap.toString() + (hasSelectionTarget()? "<" + getSelectionTarget() + ">" : "") + textInliningStr + templateDataStr;

    }




    private static Object resolveLazy(final Object variable) {
        /*
         * Check the possibility that this variable is a lazy one, in which case we should not return it directly
         * but instead make sure it is initialized and return its value.
         */
        if (variable != null && variable instanceof ILazyContextVariable) {
            return ((ILazyContextVariable)variable).getValue();
        }
        return variable;
    }



    /*
     * This class works as a wrapper for the selection target, in order to differentiate whether we
     * have set a selection target, we have not, or we have set it but it's null
     */
    private static final class SelectionTarget {

        final Object selectionTarget;

        SelectionTarget(final Object selectionTarget) {
            super();
            this.selectionTarget = selectionTarget;
        }

    }


}
