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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
final class TemplateProcessingVariableContext implements ITemplateProcessingVariableContext {

    private static final int DEFAULT_LEVELS_SIZE = 3;
    private static final int DEFAULT_MAP_SIZE = 5;

    private int level = 0;
    private int index = 0;
    private int[] levels;
    private LinkedHashMap<String,Object>[] maps;

    private static final Object NON_EXISTING = new Object() {
        @Override
        public String toString() {
            return "(*removed*)";
        }
    };



    TemplateProcessingVariableContext(final Map<String, Object> variables) {

        super();

        this.levels = new int[DEFAULT_LEVELS_SIZE];
        this.maps = (LinkedHashMap<String, Object>[]) new LinkedHashMap<?,?>[DEFAULT_LEVELS_SIZE];
        Arrays.fill(this.levels, Integer.MAX_VALUE);
        Arrays.fill(this.maps, null);
        this.levels[0] = 0;

        if (variables != null) {
            putAll(variables);
        }

    }



    public boolean contains(final String key) {
        int n = this.index + 1;
        while (n-- != 0) {
            if (this.maps[n] != null && this.maps[n].containsKey(key)) {
                // The most modern entry we find for this key could be a removal --> false
                return (this.maps[n].get(key) != NON_EXISTING);
            }
        }
        return false;
    }


    public Object get(final String key) {
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


    public void put(final String key, final Object value) {

        if (this.levels[this.index] != this.level) {
            // We need to create structures for this new level

            this.index++;

            if (this.levels.length == this.index) {
                final int[] newLevels = new int[this.levels.length + DEFAULT_LEVELS_SIZE];
                final LinkedHashMap<String,Object>[] newMaps = (LinkedHashMap<String, Object>[]) new LinkedHashMap<?,?>[this.maps.length + DEFAULT_LEVELS_SIZE];
                Arrays.fill(newLevels, Integer.MAX_VALUE);
                Arrays.fill(newMaps, null);
                System.arraycopy(this.levels, 0, newLevels, 0, this.levels.length);
                System.arraycopy(this.maps, 0, newMaps, 0, this.maps.length);
                this.levels = newLevels;
                this.maps = newMaps;
            }

            this.levels[this.index] = this.level;

        }

        if (this.maps[this.index] == null) {
            // The map for this level has not yet been created
            this.maps[this.index] = new LinkedHashMap<String,Object>(DEFAULT_MAP_SIZE, 1.0f);
        }

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

        if (this.levels[this.index] != this.level) {
            // We need to create structures for this new level

            this.index++;

            if (this.levels.length == this.index) {
                final int[] newLevels = new int[this.levels.length + DEFAULT_LEVELS_SIZE];
                final LinkedHashMap<String,Object>[] newMaps = (LinkedHashMap<String, Object>[]) new LinkedHashMap<?,?>[this.maps.length + DEFAULT_LEVELS_SIZE];
                Arrays.fill(newLevels, Integer.MAX_VALUE);
                Arrays.fill(newMaps, null);
                System.arraycopy(this.levels, 0, newLevels, 0, this.levels.length);
                System.arraycopy(this.maps, 0, newMaps, 0, this.maps.length);
                this.levels = newLevels;
                this.maps = newMaps;
            }

            this.levels[this.index] = this.level;

        }

        if (this.maps[this.index] == null) {
            // The map for this level has not yet been created
            this.maps[this.index] = new LinkedHashMap<String,Object>(Math.max(DEFAULT_MAP_SIZE, map.size() + 2), 1.0f);
        }

        this.maps[this.index].putAll(map);

    }


    public void remove(final String key) {
        if (contains(key)) {
            put(key, NON_EXISTING);
        }
    }


    public int level() {
        return this.level;
    }


    public void increaseLevel() {
        this.level++;
    }


    public void decreaseLevel() {
        Validate.isTrue(this.level > 0, "Cannot decrease variable context level below 0");
        if (this.levels[this.index] == this.level) {
            this.levels[this.index] = Integer.MAX_VALUE;
            if (this.maps[this.index] != null) {
                this.maps[this.index].clear();
            }
            this.index--;
        }
        this.level--;
    }




    public String getStringRepresentationByLevel() {

        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append('{');
        int n = this.index + 1;
        while (n-- != 0) {
            if (this.maps[n] != null) {
                final Map<String,Object> levelVars = new LinkedHashMap<String, Object>();
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
                if (strBuilder.length() > 1) {
                    strBuilder.append(',');
                }
                strBuilder.append(this.levels[n] + ":" + levelVars);
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
        return equivalentMap.toString();

    }



}
