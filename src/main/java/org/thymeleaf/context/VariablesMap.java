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
package org.thymeleaf.context;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.inline.ITextInliner;
import org.thymeleaf.inline.NoOpTextInliner;
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

    private final Locale locale;

    private int level = 0;
    private int index = 0;
    private int[] levels;
    private HashMap<String,Object>[] maps;
    private SelectionTarget[] selectionTargets;
    private ITextInliner[] textInliners;

    private static final Object NON_EXISTING = new Object() {
        @Override
        public String toString() {
            return "(*removed*)";
        }
    };



    /*
     * There is no reason for a user to directly create an instance of this - they should create Context or
     * WebContext instances instead.
     */
    VariablesMap(final Locale locale, final Map<String, Object> variables) {

        super();

        Validate.notNull(locale, "Locale cannot be null in web variables map");

        this.locale = locale;

        this.levels = new int[DEFAULT_LEVELS_SIZE];
        this.maps = (HashMap<String, Object>[]) new HashMap<?,?>[DEFAULT_LEVELS_SIZE];
        this.selectionTargets = new SelectionTarget[DEFAULT_LEVELS_SIZE];
        this.textInliners = new ITextInliner[DEFAULT_LEVELS_SIZE];
        Arrays.fill(this.levels, Integer.MAX_VALUE);
        Arrays.fill(this.maps, null);
        Arrays.fill(this.selectionTargets, null);
        Arrays.fill(this.textInliners, null);
        this.levels[0] = 0;

        if (variables != null) {
            putAll(variables);
        }

    }


    public Locale getLocale() {
        return this.locale;
    }



    public boolean containsVariable(final String name) {
        int n = this.index + 1;
        while (n-- != 0) {
            if (this.maps[n] != null && this.maps[n].containsKey(name)) {
                // The most modern entry we find for this key could be a removal --> false
                return (this.maps[n].get(name) != NON_EXISTING);
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




    public ITextInliner getTextInliner() {
        int n = this.index + 1;
        while (n-- != 0) {
            if (this.textInliners[n] != null) {
                if (this.textInliners[n] == NoOpTextInliner.INSTANCE) {
                    return null;
                }
                return this.textInliners[n];
            }
        }
        return null;
    }


    public void setTextInliner(final ITextInliner textInliner) {
        ensureLevelInitialized(DEFAULT_MAP_SIZE);
        // We use NoOpTexInliner.INSTACE in order to signal when inlining has actually been disabled
        this.textInliners[this.index] = (textInliner == null? NoOpTextInliner.INSTANCE : textInliner);
    }




    private void ensureLevelInitialized(final int requiredSize) {

        // First, check if the current index already signals the current level (in which case, everything is OK)
        if (this.levels[this.index] != this.level) {

            // The current level still had no index assigned -- we must do it, and maybe even grow structures

            this.index++; // This new index will be the one for our level

            if (this.levels.length == this.index) {
                final int[] newLevels = new int[this.levels.length + DEFAULT_LEVELS_SIZE];
                final HashMap<String,Object>[] newMaps = (HashMap<String, Object>[]) new HashMap<?,?>[this.maps.length + DEFAULT_LEVELS_SIZE];
                final SelectionTarget[] newSelectionTargets = new SelectionTarget[this.selectionTargets.length + DEFAULT_LEVELS_SIZE];
                final ITextInliner[] newTextInliners = new ITextInliner[this.textInliners.length + DEFAULT_LEVELS_SIZE];
                Arrays.fill(newLevels, Integer.MAX_VALUE);
                Arrays.fill(newMaps, null);
                Arrays.fill(newSelectionTargets, null);
                Arrays.fill(newTextInliners, null);
                System.arraycopy(this.levels, 0, newLevels, 0, this.levels.length);
                System.arraycopy(this.maps, 0, newMaps, 0, this.maps.length);
                System.arraycopy(this.selectionTargets, 0, newSelectionTargets, 0, this.selectionTargets.length);
                System.arraycopy(this.textInliners, 0, newTextInliners, 0, this.textInliners.length);
                this.levels = newLevels;
                this.maps = newMaps;
                this.selectionTargets = newSelectionTargets;
                this.textInliners = newTextInliners;
            }

            this.levels[this.index] = this.level;

        }

        if (this.maps[this.index] == null) {
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
            this.textInliners[this.index] = null;
            this.index--;
        }
        this.level--;
    }


    public String getStringRepresentationByLevel() {

        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append('{');
        int n = this.index + 1;
        while (n-- != 0) {
            final Map<String,Object> levelVars = new HashMap<String, Object>();
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
            if (n == 0 || !levelVars.isEmpty() || this.selectionTargets[n] != null || this.textInliners[n] != null) {
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
                if (this.textInliners[n] != null) {
                    strBuilder.append("[" + this.textInliners[n].getName() + "]");
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

        final Map<String,Object> equivalentMap = new HashMap<String, Object>();
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
        final String textInliningStr = (getTextInliner() != null? "[" + getTextInliner().getName() + "]" : "" );
        return equivalentMap.toString() + (hasSelectionTarget()? "<" + getSelectionTarget() + ">" : "") + textInliningStr;

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
