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
import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
final class TemplateProcessingVariablesMap implements ITemplateProcessingVariablesMap {

    private static final int DEFAULT_LEVELS_SIZE = 2;
    private static final int DEFAULT_MAP_SIZE = 5;

    private int level = 0;
    private int index = 0;
    private int[] levels;
    private HashMap<String,Object>[] maps;



    TemplateProcessingVariablesMap(final Map<String,Object> variables) {

        super();

        this.levels = new int[DEFAULT_LEVELS_SIZE];
        this.maps = (HashMap<String, Object>[]) new HashMap<?,?>[DEFAULT_LEVELS_SIZE];
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
                return true;
            }
        }
        return false;
    }


    public Object get(final String key) {
        int n = this.index + 1;
        while (n-- != 0) {
            if (this.maps[n] != null && this.maps[n].containsKey(key)) {
                return this.maps[n].get(key);
            }
        }
        return null;
    }


    void put(final String key, final Object value) {

        if (this.levels[this.index] != this.level) {
            // We need to create structures for this new level

            this.index++;

            if (this.levels.length == this.index) {
                final int[] newLevels = new int[this.levels.length + DEFAULT_LEVELS_SIZE];
                final HashMap<String,Object>[] newMaps = (HashMap<String, Object>[]) new HashMap<?,?>[this.maps.length + DEFAULT_LEVELS_SIZE];
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
            this.maps[this.index] = new HashMap<String,Object>(DEFAULT_MAP_SIZE, 1.0f);
        }

        this.maps[this.index].put(key, value);

    }


    void putAll(final Map<String, Object> map) {

        if (map == null) {
            return;
        }

        if (this.levels[this.index] != this.level) {
            // We need to create structures for this new level

            this.index++;

            if (this.levels.length == this.index) {
                final int[] newLevels = new int[this.levels.length + DEFAULT_LEVELS_SIZE];
                final HashMap<String,Object>[] newMaps = (HashMap<String, Object>[]) new HashMap<?,?>[this.maps.length + DEFAULT_LEVELS_SIZE];
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
            this.maps[this.index] = new HashMap<String,Object>(Math.max(DEFAULT_MAP_SIZE, map.size() + 2), 1.0f);
        }

        this.maps[this.index].putAll(map);

    }


    int level() {
        return this.level;
    }


    void increaseLevel() {
        this.level++;
    }


    void decreaseLevel() {
        Validate.isTrue(this.level > 0, "Cannot decrease variables map level below 0");
        if (this.levels[this.index] == this.level) {
            this.levels[this.index] = Integer.MAX_VALUE;
            if (this.maps[this.index] != null) {
                this.maps[this.index].clear();
            }
            this.index--;
        }
        this.level--;
    }




    @Override
    public String toString() {

        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append('{');
        int n = this.index + 1;
        while (n-- != 0) {
            if (this.maps[n] != null) {
                if (strBuilder.length() > 1) {
                    strBuilder.append(',');
                }
                strBuilder.append(this.levels[n] + ":" + this.maps[n].toString());
            }
        }
        strBuilder.append('}');
        return strBuilder.toString();
    }

}
