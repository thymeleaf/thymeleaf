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
package org.thymeleaf.aurora.context;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class VariablesMap implements ILocalVariableAwareVariablesMap {

    /*
     * This class is in charge of managing the map of variables in place at each moment in the template processing,
     * by taking care of the different markup levels the process is traversing and storing local variables only
     * for the levels they correspond to.
     */

    private static final int DEFAULT_LEVELS_SIZE = 3;
    private static final int DEFAULT_MAP_SIZE = 5;

    private int level = 0;
    private int index = 0;
    private int[] levels;
    private LinkedHashMap<String,Object>[] maps;
    private SelectionTarget[] selectionTargets;
    private Boolean[] textInliningActivations;

    private static final Object NON_EXISTING = new Object() {
        @Override
        public String toString() {
            return "(*removed*)";
        }
    };



    public VariablesMap(final Map<String, Object> variables) {

        super();

        this.levels = new int[DEFAULT_LEVELS_SIZE];
        this.maps = (LinkedHashMap<String, Object>[]) new LinkedHashMap<?,?>[DEFAULT_LEVELS_SIZE];
        this.selectionTargets = new SelectionTarget[DEFAULT_LEVELS_SIZE];
        this.textInliningActivations = new Boolean[DEFAULT_LEVELS_SIZE];
        Arrays.fill(this.levels, Integer.MAX_VALUE);
        Arrays.fill(this.maps, null);
        Arrays.fill(this.selectionTargets, null);
        Arrays.fill(this.textInliningActivations, null);
        this.levels[0] = 0;
        this.textInliningActivations[0] = Boolean.TRUE; // Active by default

        if (variables != null) {
            putAll(variables);
        }

    }



    public boolean containsVariable(final String key) {
        int n = this.index + 1;
        while (n-- != 0) {
            if (this.maps[n] != null && this.maps[n].containsKey(key)) {
                // The most modern entry we find for this key could be a removal --> false
                return (this.maps[n].get(key) != NON_EXISTING);
            }
        }
        return false;
    }


    public Object getVariable(final String key) {
        int n = this.index + 1;
        while (n-- != 0) {
            if (this.maps[n] != null && this.maps[n].containsKey(key)) {
                final Object result = this.maps[n].get(key);
                if (result == NON_EXISTING) {
                    return null;
                }
                return result;
            }
        }
        return null;
    }


    public Set<String> getVariableNames() {

        final Set<String> variableNames = new LinkedHashSet<String>();
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


    public void put(final String key, final Object value) {

        ensureLevelInitialized(DEFAULT_MAP_SIZE);

        if (value == NON_EXISTING && this.level == 0) {
            this.maps[this.index].remove(key);
        } else {
            this.maps[this.index].put(key, value);
        }

    }


    public void putAll(final Map<String, Object> map) {

        if (map == null) {
            return;
        }

        ensureLevelInitialized(Math.max(DEFAULT_MAP_SIZE, map.size() + 2));

        this.maps[this.index].putAll(map);

    }




    public void remove(final String key) {
        if (containsVariable(key)) {
            put(key, NON_EXISTING);
        }
    }




    public boolean hasSelectionTarget() {
        int n = this.index + 1;
        while (n-- != 0) {
            if (this.selectionTargets[n] != null) {
                return true;
            }
        }
        return false;
    }


    public Object getSelectionTarget() {
        int n = this.index + 1;
        while (n-- != 0) {
            if (this.selectionTargets[n] != null) {
                return this.selectionTargets[n].selectionTarget;
            }
        }
        return null;
    }


    public void setSelectionTarget(final Object selectionTarget) {
        ensureLevelInitialized(DEFAULT_MAP_SIZE);
        this.selectionTargets[this.index] = new SelectionTarget(selectionTarget);
    }




    public boolean isTextInliningActive() {
        int n = this.index + 1;
        while (n-- != 0) {
            if (this.textInliningActivations[n] != null) {
                return this.textInliningActivations[n].booleanValue();
            }
        }
        return false;
    }


    public void setTextInliningActive(final boolean active) {
        ensureLevelInitialized(DEFAULT_MAP_SIZE);
        this.textInliningActivations[this.index] = Boolean.valueOf(active);
    }




    private void ensureLevelInitialized(final int requiredSize) {

        // First, check if the current index already signals the current level (in which case, everything is OK)
        if (this.levels[this.index] != this.level) {

            // The current level still had no index assigned -- we must do it, and maybe even grow structures

            this.index++; // This new index will be the one for our level

            if (this.levels.length == this.index) {
                final int[] newLevels = new int[this.levels.length + DEFAULT_LEVELS_SIZE];
                final LinkedHashMap<String,Object>[] newMaps = (LinkedHashMap<String, Object>[]) new LinkedHashMap<?,?>[this.maps.length + DEFAULT_LEVELS_SIZE];
                final SelectionTarget[] newSelectionTargets = new SelectionTarget[this.selectionTargets.length + DEFAULT_LEVELS_SIZE];
                final Boolean[] newTextInliningActivations = new Boolean[this.textInliningActivations.length + DEFAULT_LEVELS_SIZE];
                Arrays.fill(newLevels, Integer.MAX_VALUE);
                Arrays.fill(newMaps, null);
                Arrays.fill(newSelectionTargets, null);
                Arrays.fill(newTextInliningActivations, null);
                System.arraycopy(this.levels, 0, newLevels, 0, this.levels.length);
                System.arraycopy(this.maps, 0, newMaps, 0, this.maps.length);
                System.arraycopy(this.selectionTargets, 0, newSelectionTargets, 0, this.selectionTargets.length);
                System.arraycopy(this.textInliningActivations, 0, newTextInliningActivations, 0, this.textInliningActivations.length);
                this.levels = newLevels;
                this.maps = newMaps;
                this.selectionTargets = newSelectionTargets;
                this.textInliningActivations = newTextInliningActivations;
            }

            this.levels[this.index] = this.level;

        }

        if (this.maps[this.index] == null) {
            // The map for this level has not yet been created
            this.maps[this.index] = new LinkedHashMap<String,Object>(requiredSize, 1.0f);
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
            this.textInliningActivations[this.index] = null;
            this.index--;
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
                for (final Map.Entry<String,Object> mapEntry : this.maps[n].entrySet()) {
                    final String name = mapEntry.getKey();
                    final Object value = mapEntry.getValue();
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
            if (n == 0 || !levelVars.isEmpty() || this.selectionTargets[n] != null || this.textInliningActivations[n] != null) {
                if (strBuilder.length() > 1) {
                    strBuilder.append(',');
                }
                strBuilder.append(this.levels[n] + ":");
                if (!levelVars.isEmpty() || n == 0) {
                    strBuilder.append(levelVars);
                }
                if (this.selectionTargets[n] != null) {
                    strBuilder.append("<" + this.selectionTargets[n].selectionTarget + ">");
                }
                if (this.textInliningActivations[n] != null) {
                    strBuilder.append("[" + this.textInliningActivations[n] + "]");
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
                for (final Map.Entry<String,Object> mapEntry : this.maps[i].entrySet()) {
                    final String name = mapEntry.getKey();
                    final Object value = mapEntry.getValue();
                    if (value == NON_EXISTING) {
                        equivalentMap.remove(name);
                        continue;
                    }
                    equivalentMap.put(name, value);
                }
            }
            i++;
        }
        return equivalentMap.toString() + (hasSelectionTarget()? "<" + getSelectionTarget() + ">" : "") + "[" + isTextInliningActive() + "]";

    }




    /*
     * This class works as a wrapper for the selection target, in order to differentiate whether we
     * have set a selection target, we have not, or we have set it but it's null
     */
    private static class SelectionTarget {

        final Object selectionTarget;

        SelectionTarget(final Object selectionTarget) {
            super();
            this.selectionTarget = selectionTarget;
        }

    }


}
