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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
final class TemplateProcessingWebVariablesMap implements ITemplateProcessingVariablesMap {

    /*
     * ---------------------------------------------------------------------------
     * THIS MAP FORWARDS ALL OPERATIONS TO THE UNDERLYING REQUEST, EXCEPT
     * FOR THE request, session AND application VARIABLES.
     * ---------------------------------------------------------------------------
     */

    private static final String PARAM_VARIABLE_NAME = "param";
    private static final String SESSION_VARIABLE_NAME = "session";
    private static final String APPLICATION_VARIABLE_NAME = "application";

    private final HttpServletRequest request;
    private final ServletContext servletContext;


    private static final int DEFAULT_LEVELS_SIZE = 2;
    private static final int DEFAULT_NAME_SETS_SIZE = 5;
    private static final int DEFAULT_ATTIC_MAP_SIZE = 5;

    private int level = 0;
    private int index = 0;
    private int[] levels;
    // No names or attic will be used for level 0, that will be just the request object
    private Set<String>[] nameSets;
    private HashMap<String,Object>[] atticMaps;



    TemplateProcessingWebVariablesMap(final HttpServletRequest request, final ServletContext servletContext,
                                      final Map<String, Object> variables) {

        super();

        Validate.notNull(request, "Request cannot be null in web variables map");
        Validate.notNull(servletContext, "Servlet Context cannot be null in web variables map");

        this.request = request;
        this.servletContext = servletContext;

        this.levels = new int[DEFAULT_LEVELS_SIZE];
        this.nameSets = (Set<String>[]) new HashSet<?>[DEFAULT_LEVELS_SIZE];
        this.atticMaps = (HashMap<String, Object>[]) new HashMap<?,?>[DEFAULT_LEVELS_SIZE];
        Arrays.fill(this.levels, Integer.MAX_VALUE);
        Arrays.fill(this.nameSets, null);
        Arrays.fill(this.atticMaps, null);
        this.levels[0] = 0;

        if (variables != null) {
            putAll(variables);
        }

    }



    public boolean contains(final String key) {

        // For most implementations of HttpServletRequest, trying to get a value instead of iterating the
        // keys Enumeration seems faster as a way to know if something exists (in the cases when we are checking
        // for existing keys a good % of the total times).
        if (this.request.getAttribute(key) != null) {
            return true;
        }

        final Enumeration attrNamesEnum = this.request.getAttributeNames();

        if (key == null) {
            while (attrNamesEnum.hasMoreElements()) {
                if (attrNamesEnum.nextElement() == null) {
                    return true;
                }
            }
            return false;
        }

        while (attrNamesEnum.hasMoreElements()) {
            if (key.equals(attrNamesEnum.nextElement())) {
                return true;
            }
        }

        return false;

    }


    public Object get(final String key) {
        return this.request.getAttribute(key);
    }


    void put(final String key, final Object value) {

        if (this.levels[this.index] != this.level) {
            // We need to create structures for this new level

            this.index++;

            if (this.levels.length == this.index) {
                final int[] newLevels = new int[this.levels.length + DEFAULT_LEVELS_SIZE];
                final Set<String>[] newNameSets = (Set<String>[]) new HashSet<?>[this.nameSets.length + DEFAULT_LEVELS_SIZE];
                final HashMap<String,Object>[] newMaps = (HashMap<String, Object>[]) new HashMap<?,?>[this.atticMaps.length + DEFAULT_LEVELS_SIZE];
                Arrays.fill(newLevels, Integer.MAX_VALUE);
                Arrays.fill(newNameSets, null);
                Arrays.fill(newMaps, null);
                System.arraycopy(this.levels, 0, newLevels, 0, this.levels.length);
                System.arraycopy(this.nameSets, 0, newNameSets, 0, this.nameSets.length);
                System.arraycopy(this.atticMaps, 0, newMaps, 0, this.atticMaps.length);
                this.levels = newLevels;
                this.nameSets = newNameSets;
                this.atticMaps = newMaps;
            }

            this.levels[this.index] = this.level;

        }

        if (!contains(key)) {
            this.request.setAttribute(key, value);
            if (this.level > 0) {
                if (this.nameSets[this.index])
            }
        }

        if (this.levels[this.index] != this.level) {
            // We need to create structures for this new level

            this.index++;

            if (this.levels.length == this.index) {
                final int[] newLevels = new int[this.levels.length + DEFAULT_LEVELS_SIZE];
                final HashMap<String,Object>[] newMaps = (HashMap<String, Object>[]) new HashMap<?,?>[this.atticMaps.length + DEFAULT_LEVELS_SIZE];
                Arrays.fill(newLevels, Integer.MAX_VALUE);
                Arrays.fill(newMaps, null);
                System.arraycopy(this.levels, 0, newLevels, 0, this.levels.length);
                System.arraycopy(this.atticMaps, 0, newMaps, 0, this.atticMaps.length);
                this.levels = newLevels;
                this.atticMaps = newMaps;
            }

            this.levels[this.index] = this.level;

        }

        if (this.atticMaps[this.index] == null) {
            // The map for this level has not yet been created
            this.atticMaps[this.index] = new HashMap<String,Object>(DEFAULT_ATTIC_MAP_SIZE, 1.0f);
        }

        this.atticMaps[this.index].put(key, value);

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
                final HashMap<String,Object>[] newMaps = (HashMap<String, Object>[]) new HashMap<?,?>[this.atticMaps.length + DEFAULT_LEVELS_SIZE];
                Arrays.fill(newLevels, Integer.MAX_VALUE);
                Arrays.fill(newMaps, null);
                System.arraycopy(this.levels, 0, newLevels, 0, this.levels.length);
                System.arraycopy(this.atticMaps, 0, newMaps, 0, this.atticMaps.length);
                this.levels = newLevels;
                this.atticMaps = newMaps;
            }

            this.levels[this.index] = this.level;

        }

        if (this.atticMaps[this.index] == null) {
            // The map for this level has not yet been created
            this.atticMaps[this.index] = new HashMap<String,Object>(Math.max(DEFAULT_ATTIC_MAP_SIZE, map.size() + 2), 1.0f);
        }

        this.atticMaps[this.index].putAll(map);

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
            if (this.atticMaps[this.index] != null) {
                this.atticMaps[this.index].clear();
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
            if (this.atticMaps[n] != null) {
                if (strBuilder.length() > 1) {
                    strBuilder.append(',');
                }
                strBuilder.append(this.levels[n] + ":" + this.atticMaps[n].toString());
            }
        }
        strBuilder.append('}');
        return strBuilder.toString();
    }

}
