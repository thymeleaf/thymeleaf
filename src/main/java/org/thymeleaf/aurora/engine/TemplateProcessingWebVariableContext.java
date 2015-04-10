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
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.aurora.context.IVariableContext;
import org.thymeleaf.aurora.context.IVariablesMap;
import org.thymeleaf.aurora.context.IWebVariableContext;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
final class TemplateProcessingWebVariableContext
        implements ITemplateProcessingVariableContext, IWebVariableContext {

    /*
     * ---------------------------------------------------------------------------
     * THIS MAP FORWARDS ALL OPERATIONS TO THE UNDERLYING REQUEST, EXCEPT
     * FOR THE request, session AND application VARIABLES.
     *
     * NOTE that, even if attributes are leveled so that above level 0 they are
     * considered local and thus disappear after lowering the level, attributes
     * directly set on the request, session or servletcontext objects are
     * considered global and therefore valid even when the level decreased (though
     * they can be overridden).
     * ---------------------------------------------------------------------------
     */

    private static final String PARAM_VARIABLE_NAME = "param";
    private static final String SESSION_VARIABLE_NAME = "session";
    private static final String APPLICATION_VARIABLE_NAME = "application";

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final HttpSession session;
    private final ServletContext servletContext;

    private final ITemplateProcessingVariableContext requestAttributesVariableContext;
    private final IVariableContext sessionAttributesVariableContext;
    private final IVariableContext applicationAttributesVariableContext;




    TemplateProcessingWebVariableContext(
            final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext, final Map<String, Object> variables) {

        super();

        Validate.notNull(request, "Request cannot be null in web variables map");
        Validate.notNull(response, "Response cannot be null in web variables map");
        Validate.notNull(servletContext, "Servlet Context cannot be null in web variables map");

        this.request = request;
        this.response = response;
        this.session = request.getSession(false);
        this.servletContext = servletContext;

        this.requestAttributesVariableContext = new RequestAttributesVariableContext(this.request, variables);
        this.applicationAttributesVariableContext = new ServletContextAttributeContext(this.servletContext);
        this.sessionAttributesVariableContext = (this.session == null ? null : new SessionAttributeContext(this.session));

    }




    public HttpServletRequest getRequest() {
        return this.request;
    }


    public HttpServletResponse getResponse() {
        return this.response;
    }


    public HttpSession getSession() {
        return this.session;
    }


    public ServletContext getServletContext() {
        return this.servletContext;
    }


    public boolean contains(final String key) {
        if (SESSION_VARIABLE_NAME.equals(key)) {
            return this.sessionAttributesVariableContext != null;
        }
        if (APPLICATION_VARIABLE_NAME.equals(key)) {
            return true;
        }
        return this.requestAttributesVariableContext.contains(key);
    }


    public Object get(final String key) {
        if (SESSION_VARIABLE_NAME.equals(key)) {
            return this.sessionAttributesVariableContext;
        }
        if (APPLICATION_VARIABLE_NAME.equals(key)) {
            return this.applicationAttributesVariableContext;
        }
        return this.requestAttributesVariableContext.get(key);
    }


    public void put(final String key, final Object value) {
        this.requestAttributesVariableContext.put(key, value);
    }


    public void putAll(final Map<String, Object> map) {
        this.requestAttributesVariableContext.putAll(map);
    }


    public void remove(final String key) {
        this.requestAttributesVariableContext.remove(key);
    }



    public int level() {
        return this.requestAttributesVariableContext.level();
    }


    public void increaseLevel() {
        this.requestAttributesVariableContext.increaseLevel();
    }


    public void decreaseLevel() {
        this.requestAttributesVariableContext.decreaseLevel();
    }




    public String getStringRepresentationByLevel() {
        return this.requestAttributesVariableContext.getStringRepresentationByLevel();
    }




    @Override
    public String toString() {
        return this.requestAttributesVariableContext.toString();
    }





    private static final class SessionAttributeContext implements IVariableContext {

        private final HttpSession session;

        SessionAttributeContext(final HttpSession session) {
            super();
            this.session = session;
        }

        public Enumeration<String> getAttributeNames() {
            return this.session.getAttributeNames();
        }

        public Object getAttribute(final String name) {
            return this.session.getAttribute(name);
        }

        public void setAttribute(final String name, final Object value) {
            this.session.setAttribute(name, value);
        }

        public void removeAttribute(final String name) {
            this.session.removeAttribute(name);
        }

    }




    private static final class ServletContextAttributeContext implements IVariableContext {

        private final ServletContext servletContext;

        ServletContextAttributeContext(final ServletContext servletContext) {
            super();
            this.servletContext = servletContext;
        }

        public Enumeration<String> getAttributeNames() {
            return this.servletContext.getAttributeNames();
        }

        public Object getAttribute(final String name) {
            return this.servletContext.getAttribute(name);
        }

        public void setAttribute(final String name, final Object value) {
            this.servletContext.setAttribute(name, value);
        }

        public void removeAttribute(final String name) {
            this.servletContext.removeAttribute(name);
        }

    }




    private static final class RequestAttributesVariableContext implements ITemplateProcessingVariableContext {

        private static final int DEFAULT_LEVELS_SIZE = 3;
        private static final int DEFAULT_LEVELARRAYS_SIZE = 5;

        private final HttpServletRequest request;

        private int level = 0;
        private int index = 0;
        private int[] levels;

        private String[][] names;
        private Object[][] oldValues;
        private Object[][] newValues;
        private int[] levelSizes;

        private static final Object NON_EXISTING = new Object() {
            @Override
            public String toString() {
                return "(*removed*)";
            }
        };


        RequestAttributesVariableContext(final HttpServletRequest request, final Map<String, Object> variables) {

            super();

            this.request = request;

            this.levels = new int[DEFAULT_LEVELS_SIZE];
            this.names = new String[DEFAULT_LEVELS_SIZE][];
            this.oldValues = new Object[DEFAULT_LEVELS_SIZE][];
            this.newValues = new Object[DEFAULT_LEVELS_SIZE][];
            this.levelSizes = new int[DEFAULT_LEVELS_SIZE];
            Arrays.fill(this.levels, Integer.MAX_VALUE);
            Arrays.fill(this.names, null);
            Arrays.fill(this.oldValues, null);
            Arrays.fill(this.newValues, null);
            Arrays.fill(this.levelSizes, 0);
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

            final Enumeration<String> attrNamesEnum = this.request.getAttributeNames();

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



        private int searchName(final String name) {
            int n = this.levelSizes[this.index];
            if (name == null) {
                while (n-- != 0) {
                    if (this.names[this.index][n] == null) {
                        return n;
                    }
                }
                return -1;
            }
            while (n-- != 0) {
                if (name.equals(this.names[this.index][n])) {
                    return n;
                }
            }
            return -1;
        }




        public void put(final String key, final Object value) {

            if (this.levels[this.index] != this.level) {
                // We need to create structures for this new level

                this.index++;

                if (this.levels.length == this.index) {
                    final int[] newLevels = new int[this.levels.length + DEFAULT_LEVELS_SIZE];
                    final String[][] newNames = new String[this.names.length + DEFAULT_LEVELS_SIZE][];
                    final Object[][] newNewValues = new Object[this.newValues.length + DEFAULT_LEVELS_SIZE][];
                    final Object[][] newOldValues = new Object[this.oldValues.length + DEFAULT_LEVELS_SIZE][];
                    final int[] newLevelSizes = new int[this.levelSizes.length + DEFAULT_LEVELS_SIZE];
                    Arrays.fill(newLevels, Integer.MAX_VALUE);
                    Arrays.fill(newNames, null);
                    Arrays.fill(newNewValues, null);
                    Arrays.fill(newOldValues, null);
                    Arrays.fill(newLevelSizes, 0);
                    System.arraycopy(this.levels, 0, newLevels, 0, this.levels.length);
                    System.arraycopy(this.names, 0, newNames, 0, this.names.length);
                    System.arraycopy(this.newValues, 0, newNewValues, 0, this.newValues.length);
                    System.arraycopy(this.oldValues, 0, newOldValues, 0, this.oldValues.length);
                    System.arraycopy(this.levelSizes, 0, newLevelSizes, 0, this.levelSizes.length);
                    this.levels = newLevels;
                    this.names = newNames;
                    this.newValues = newNewValues;
                    this.oldValues = newOldValues;
                    this.levelSizes = newLevelSizes;
                }

                this.levels[this.index] = this.level;

            }

            if (this.level > 0) {
                // We will only take care of new/old values if we are not on level 0

                if (this.names[this.index] == null) {
                    // the arrays for this level have still not ben created

                    this.names[this.index] = new String[DEFAULT_LEVELARRAYS_SIZE];
                    Arrays.fill(this.names[this.index], null);

                    this.newValues[this.index] = new Object[DEFAULT_LEVELARRAYS_SIZE];
                    Arrays.fill(this.newValues[this.index], null);

                    this.oldValues[this.index] = new Object[DEFAULT_LEVELARRAYS_SIZE];
                    Arrays.fill(this.oldValues[this.index], null);

                    this.levelSizes[this.index] = 0;

                }

                int levelIndex = searchName(key);
                if (levelIndex != -1) {

                    // There already is a registered movement for this key - we should modify it instead of creating a new one
                    this.newValues[this.index][levelIndex] = value;

                } else {

                    if (this.names[this.index].length == this.levelSizes[this.index]) {
                        // We need to grow the arrays for this level

                        final String[] newNames = new String[this.names[this.index].length + DEFAULT_LEVELARRAYS_SIZE];
                        final Object[] newNewValues = new Object[this.newValues[this.index].length + DEFAULT_LEVELARRAYS_SIZE];
                        final Object[] newOldValues = new Object[this.oldValues[this.index].length + DEFAULT_LEVELARRAYS_SIZE];
                        Arrays.fill(newNames, null);
                        Arrays.fill(newNewValues, null);
                        Arrays.fill(newOldValues, null);
                        System.arraycopy(this.names[this.index], 0, newNames, 0, this.names[this.index].length);
                        System.arraycopy(this.newValues[this.index], 0, newNewValues, 0, this.newValues[this.index].length);
                        System.arraycopy(this.oldValues[this.index], 0, newOldValues, 0, this.oldValues[this.index].length);
                        this.names[this.index] = newNames;
                        this.newValues[this.index] = newNewValues;
                        this.oldValues[this.index] = newOldValues;

                    }

                    levelIndex = this.levelSizes[this.index]; // We will add at the end

                    this.names[this.index][levelIndex] = key;
                    if (contains(key)) {
                        this.oldValues[this.index][levelIndex] = this.request.getAttribute(key);
                    } else {
                        this.oldValues[this.index][levelIndex] = NON_EXISTING;
                    }
                    this.newValues[this.index][levelIndex] = value;

                    this.levelSizes[this.index]++;

                }

            }

            if (value == NON_EXISTING) {
                this.request.removeAttribute(key);
            } else {
                this.request.setAttribute(key, value);
            }

        }


        public void putAll(final Map<String, Object> map) {
            if (map == null) {
                return;
            }
            for (final Map.Entry<String,Object> entry : map.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
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

                if (this.names[this.index] != null && this.levelSizes[this.index] > 0) {
                    // There were movements at this level, so we have to revert them

                    int n = this.levelSizes[this.index];
                    while (n-- != 0) {
                        final String name = this.names[this.index][n];
                        final Object newValue = this.newValues[this.index][n];
                        final Object oldValue = this.oldValues[this.index][n];
                        if (newValue == NON_EXISTING) {
                            if (!contains(name)) {
                                // Only if not contained, in order to avoid modifying values that have been set directly
                                // into the request.
                                if (oldValue != NON_EXISTING) {
                                    this.request.setAttribute(name,oldValue);
                                }
                            }
                        } else if (newValue == this.request.getAttribute(name)) {
                            // Only if the value matches, in order to avoid modifying values that have been set directly
                            // into the request.
                            if (oldValue == NON_EXISTING) {
                                this.request.removeAttribute(name);
                            } else {
                                this.request.setAttribute(name,oldValue);
                            }
                        }
                    }
                    this.levelSizes[this.index] = 0;

                }

                this.index--;

            }
            this.level--;
        }




        public String getStringRepresentationByLevel() {

            final StringBuilder strBuilder = new StringBuilder();
            strBuilder.append('{');
            final Map<String,Object> oldValuesSum = new LinkedHashMap<String, Object>();
            int n = this.index + 1;
            while (n-- != 1) {
                if (this.names[n] != null && this.levelSizes[n] > 0) {
                    final Map<String,Object> levelVars = new LinkedHashMap<String, Object>();
                    for (int i = 0; i < this.levelSizes[n]; i++) {
                        final String name = this.names[n][i];
                        final Object newValue = this.newValues[n][i];
                        final Object oldValue = this.oldValues[n][i];
                        if (newValue == oldValue) {
                            // This is a no-op!
                            continue;
                        }
                        if (!oldValuesSum.containsKey(name)) {
                            // This means that, either the value in the request is the same as the newValue, or it was modified
                            // directly at the request and we need to discard this entry.
                            if (newValue == NON_EXISTING) {
                                if (contains(name)) {
                                    continue;
                                }
                            } else {
                                if (newValue != this.request.getAttribute(name)) {
                                    continue;
                                }
                            }
                        } else {
                            // This means that, either the old value in the map is the same as the newValue, or it was modified
                            // directly at the request and we need to discard this entry.
                            if (newValue != oldValuesSum.get(name)) {
                                continue;
                            }
                        }
                        levelVars.put(name, newValue);
                        oldValuesSum.put(name, oldValue);
                    }
                    if (strBuilder.length() > 1) {
                        strBuilder.append(',');
                    }
                    strBuilder.append(this.levels[n] + ":" + levelVars.toString());
                }
            }
            final Map<String,Object> requestAttributes = new LinkedHashMap<String, Object>();
            final Enumeration<String> attrNames = this.request.getAttributeNames();
            while (attrNames.hasMoreElements()) {
                final String name = attrNames.nextElement();
                if (oldValuesSum.containsKey(name)) {
                    final Object oldValue = oldValuesSum.get(name);
                    if (oldValue != NON_EXISTING) {
                        requestAttributes.put(name, oldValuesSum.get(name));
                    }
                    oldValuesSum.remove(name);
                } else {
                    requestAttributes.put(name, this.request.getAttribute(name));
                }
            }
            for (Map.Entry<String,Object> oldValuesSumEntry : oldValuesSum.entrySet()) {
                final String name = oldValuesSumEntry.getKey();
                if (!requestAttributes.containsKey(name)) {
                    final Object oldValue = oldValuesSumEntry.getValue();
                    if (oldValue != NON_EXISTING) {
                        requestAttributes.put(name, oldValue);
                    }
                }
            }
            if (strBuilder.length() > 1) {
                strBuilder.append(',');
            }
            strBuilder.append("0:" + requestAttributes.toString());
            strBuilder.append("}[");
            strBuilder.append(this.level);
            strBuilder.append(']');
            return strBuilder.toString();

        }




        @Override
        public String toString() {

            final Map<String,Object> equivalentMap = new LinkedHashMap<String, Object>();
            final Enumeration<String> attributeNamesEnum = this.request.getAttributeNames();
            while (attributeNamesEnum.hasMoreElements()) {
                final String name = attributeNamesEnum.nextElement();
                equivalentMap.put(name, this.request.getAttribute(name));
            }
            return equivalentMap.toString();

        }


    }



}
