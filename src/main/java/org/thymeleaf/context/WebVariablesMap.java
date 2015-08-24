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
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.inline.IInliner;
import org.thymeleaf.inline.NoOpInliner;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class WebVariablesMap
        implements IWebVariablesMap, ILocalVariableAwareVariablesMap {

    /*
     * ---------------------------------------------------------------------------
     * THIS MAP FORWARDS ALL OPERATIONS TO THE UNDERLYING REQUEST, EXCEPT
     * FOR THE param (request parameters), session (session attributes) AND
     * application (servlet context attributes) VARIABLES.
     *
     * NOTE that, even if attributes are leveled so that above level 0 they are
     * considered local and thus disappear after lowering the level, attributes
     * directly set on the request object are considered global and therefore
     * valid even when the level decreased (though they can be overridden). This
     * is so for better simulating the effect of directly working against the
     * request object, and for better integration with JSP.
     * ---------------------------------------------------------------------------
     */

    private static final String PARAM_VARIABLE_NAME = "param";
    private static final String SESSION_VARIABLE_NAME = "session";
    private static final String APPLICATION_VARIABLE_NAME = "application";

    private final Locale locale;

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final HttpSession session;
    private final ServletContext servletContext;

    private final RequestAttributesVariablesMap requestAttributesVariablesMap;
    private final IVariablesMap requestParametersVariablesMap;
    private final IVariablesMap sessionAttributesVariablesMap;
    private final IVariablesMap applicationAttributesVariablesMap;




    /*
     * There is no reason for a user to directly create an instance of this - they should create Context or
     * WebContext instances instead.
     */
    WebVariablesMap(
            final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext,
            final Locale locale, final Map<String, Object> variables) {

        super();

        Validate.notNull(request, "Request cannot be null in web variables map");
        Validate.notNull(response, "Response cannot be null in web variables map");
        Validate.notNull(servletContext, "Servlet Context cannot be null in web variables map");
        Validate.notNull(locale, "Locale cannot be null in web variables map");

        this.locale = locale;

        this.request = request;
        this.response = response;
        this.session = request.getSession(false);
        this.servletContext = servletContext;

        this.requestAttributesVariablesMap = new RequestAttributesVariablesMap(this.request, this.locale, variables);
        this.requestParametersVariablesMap = new RequestParametersVariablesMap(this.request, this.locale);
        this.applicationAttributesVariablesMap = new ServletContextAttributesVariablesMap(this.servletContext, this.locale);
        this.sessionAttributesVariablesMap = (this.session == null ? null : new SessionAttributesVariablesMap(this.session, this.locale));

    }


    public Locale getLocale() {
        return this.locale;
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


    public boolean containsVariable(final String name) {
        if (SESSION_VARIABLE_NAME.equals(name)) {
            return this.sessionAttributesVariablesMap != null;
        }
        if (PARAM_VARIABLE_NAME.equals(name)) {
            return true;
        }
        if (APPLICATION_VARIABLE_NAME.equals(name)) {
            return true;
        }
        return this.requestAttributesVariablesMap.containsVariable(name);
    }


    public Object getVariable(final String key) {
        if (SESSION_VARIABLE_NAME.equals(key)) {
            return this.sessionAttributesVariablesMap;
        }
        if (PARAM_VARIABLE_NAME.equals(key)) {
            return this.requestParametersVariablesMap;
        }
        if (APPLICATION_VARIABLE_NAME.equals(key)) {
            return this.applicationAttributesVariablesMap;
        }
        return this.requestAttributesVariablesMap.getVariable(key);
    }


    public Set<String> getVariableNames() {
        // Note this set will NOT include 'param', 'session' or 'application', as they are considered special
        // ways to access attributes/parameters in these Servlet API structures
        return this.requestAttributesVariablesMap.getVariableNames();
    }


    public void put(final String key, final Object value) {
        if (SESSION_VARIABLE_NAME.equals(key) ||
                PARAM_VARIABLE_NAME.equals(key) ||
                APPLICATION_VARIABLE_NAME.equals(key)) {
            throw new IllegalArgumentException(
                    "Cannot set variable called '" + key + "' into web variables map: such name is a reserved word");
        }
        this.requestAttributesVariablesMap.put(key, value);
    }


    public void putAll(final Map<String, Object> map) {
        // We will not delegate to requestAttributesVariablesMap because we need to perform the reserved-name check
        // for each variable being set.
        if (map == null) {
            return;
        }
        for (final Map.Entry<String,Object> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }


    public void remove(final String key) {
        if (SESSION_VARIABLE_NAME.equals(key) ||
                PARAM_VARIABLE_NAME.equals(key) ||
                APPLICATION_VARIABLE_NAME.equals(key)) {
            throw new IllegalArgumentException(
                    "Cannot remove variable called '" + key + "' in web variables map: such name is a reserved word");
        }
        this.requestAttributesVariablesMap.remove(key);
    }


    public boolean isVariableLocal(final String name) {
        return this.requestAttributesVariablesMap.isVariableLocal(name);
    }


    public boolean hasSelectionTarget() {
        return this.requestAttributesVariablesMap.hasSelectionTarget();
    }


    public Object getSelectionTarget() {
        return this.requestAttributesVariablesMap.getSelectionTarget();
    }


    public void setSelectionTarget(final Object selectionTarget) {
        this.requestAttributesVariablesMap.setSelectionTarget(selectionTarget);
    }




    public IInliner getInliner() {
        return this.requestAttributesVariablesMap.getInliner();
    }

    public void setInliner(final IInliner inliner) {
        this.requestAttributesVariablesMap.setInliner(inliner);
    }




    public int level() {
        return this.requestAttributesVariablesMap.level();
    }


    public void increaseLevel() {
        this.requestAttributesVariablesMap.increaseLevel();
    }


    public void decreaseLevel() {
        this.requestAttributesVariablesMap.decreaseLevel();
    }




    public String getStringRepresentationByLevel() {
        // Request parameters, session and servlet context can be safely ignored here
        return this.requestAttributesVariablesMap.getStringRepresentationByLevel();
    }




    @Override
    public String toString() {
        // Request parameters, session and servlet context can be safely ignored here
        return this.requestAttributesVariablesMap.toString();
    }





    private static final class SessionAttributesVariablesMap implements IVariablesMap {

        private final HttpSession session;
        private final Locale locale;

        SessionAttributesVariablesMap(final HttpSession session, final Locale locale) {
            super();
            this.session = session;
            this.locale = locale;
        }

        public Locale getLocale() {
            return this.locale;
        }

        public Object getVariable(final String key) {
            return this.session.getAttribute(key);
        }

        public boolean containsVariable(final String name) {
            return existsInEnumeration(this.session.getAttributeNames(), name);
        }


        public Set<String> getVariableNames() {

            final Set<String> variableNames = new HashSet<String>();
            final Enumeration<String> attributeNamesEnum = this.session.getAttributeNames();
            while (attributeNamesEnum.hasMoreElements()) {
                variableNames.add(attributeNamesEnum.nextElement());
            }
            return variableNames;

        }

        public boolean hasSelectionTarget() {
            return false;
        }

        public Object getSelectionTarget() {
            return null;
        }

        public IInliner getInliner() {
            return null;
        }

    }




    private static final class ServletContextAttributesVariablesMap implements IVariablesMap {

        private final ServletContext servletContext;
        private final Locale locale;

        ServletContextAttributesVariablesMap(final ServletContext servletContext, final Locale locale) {
            super();
            this.servletContext = servletContext;
            this.locale = locale;
        }

        public Locale getLocale() {
            return this.locale;
        }

        public Object getVariable(final String key) {
            return this.servletContext.getAttribute(key);
        }

        public boolean containsVariable(final String name) {
            // For most implementations of ServletContext, trying to get a value instead of iterating the
            // names Enumeration seems faster as a way to know if something exists (in the cases when we are checking
            // for existing keys a good % of the total times).
            if (this.servletContext.getAttribute(name) != null) {
                return true;
            }
            return existsInEnumeration(this.servletContext.getAttributeNames(), name);
        }


        public Set<String> getVariableNames() {

            final Set<String> variableNames = new HashSet<String>();
            final Enumeration<String> attributeNamesEnum = this.servletContext.getAttributeNames();
            while (attributeNamesEnum.hasMoreElements()) {
                variableNames.add(attributeNamesEnum.nextElement());
            }
            return variableNames;

        }

        public boolean hasSelectionTarget() {
            return false;
        }

        public Object getSelectionTarget() {
            return null;
        }

        public IInliner getInliner() {
            return null;
        }

    }




    private static final class RequestParametersVariablesMap implements IVariablesMap {

        private final HttpServletRequest request;
        private final Locale locale;

        RequestParametersVariablesMap(final HttpServletRequest request, final Locale locale) {
            super();
            this.request = request;
            this.locale = locale;
        }

        public Locale getLocale() {
            return this.locale;
        }

        public Object getVariable(final String key) {
            // We will always be returning the String[] version of the parameters
            return this.request.getParameterValues(key);
        }

        public boolean containsVariable(final String name) {
            // For most implementations of HttpServletRequest, trying to get a value instead of iterating the
            // names Enumeration or the ParameterMap keys (which might be locked but will be created each time if not)
            // seems faster as a way to know if something exists (in the cases when we are checking
            // for existing keys a good % of the total times).
            if (this.request.getParameterValues(name) != null) {
                return true;
            }
            // We can still be fast if this parameter map has been locked so that we don't receive a new instance
            // each time we call it (see e.g. org.apache.catalina.connector.Request)
            return this.request.getParameterMap().containsKey(name);
        }

        public Set<String> getVariableNames() {
            // If the parameter map is locked (see e.g. org.apache.catalina.connector.Request) this can be quite fast
            return this.request.getParameterMap().keySet();
        }

        public boolean hasSelectionTarget() {
            return false;
        }

        public Object getSelectionTarget() {
            return null;
        }

        public IInliner getInliner() {
            return null;
        }

    }




    private static final class RequestAttributesVariablesMap implements IVariablesMap, ILocalVariableAwareVariablesMap {

        private static final int DEFAULT_LEVELS_SIZE = 3;
        private static final int DEFAULT_LEVELARRAYS_SIZE = 5;

        private final Locale locale;

        private final HttpServletRequest request;

        private int level = 0;
        private int index = 0;
        private int[] levels;

        private String[][] names;
        private Object[][] oldValues;
        private Object[][] newValues;
        private int[] levelSizes;
        private SelectionTarget[] selectionTargets;
        private IInliner[] inliners;

        private static final Object NON_EXISTING = new Object() {
            @Override
            public String toString() {
                return "(*removed*)";
            }
        };


        RequestAttributesVariablesMap(final HttpServletRequest request, final Locale locale, final Map<String, Object> variables) {

            super();

            this.request = request;
            this.locale = locale;

            this.levels = new int[DEFAULT_LEVELS_SIZE];
            this.names = new String[DEFAULT_LEVELS_SIZE][];
            this.oldValues = new Object[DEFAULT_LEVELS_SIZE][];
            this.newValues = new Object[DEFAULT_LEVELS_SIZE][];
            this.levelSizes = new int[DEFAULT_LEVELS_SIZE];
            this.selectionTargets = new SelectionTarget[DEFAULT_LEVELS_SIZE];
            this.inliners = new IInliner[DEFAULT_LEVELS_SIZE];
            Arrays.fill(this.levels, Integer.MAX_VALUE);
            Arrays.fill(this.names, null);
            Arrays.fill(this.oldValues, null);
            Arrays.fill(this.newValues, null);
            Arrays.fill(this.levelSizes, 0);
            Arrays.fill(this.selectionTargets, null);
            Arrays.fill(this.inliners, null);
            this.levels[0] = 0;

            if (variables != null) {
                putAll(variables);
            }

        }

        public Locale getLocale() {
            return this.locale;
        }

        public boolean containsVariable(final String name) {

            // --------------------------
            // Note this method relies on HttpServletRequest#getAttributeNames(), which is an extremely slow and
            // inefficient method in implementations like Apache Tomcat's. So the uses of this method should be
            // very controlled and reduced to the minimum. Specifically, any call that executes e.g. for every
            // expression evaluation should be disallowed. Only sporadic uses like e.g. from the put() method should
            // be done.
            // Note also it would not be a good idea to cache the attribute names coming from the request if we
            // want to keep complete independence of the HttpServletRequest object, so that it can be modified
            // from the outside (e.g. from other libraries like Tiles) with Thymeleaf perfectly integrating with
            // those modifications.
            // --------------------------


            // For most implementations of HttpServletRequest, trying to get a value instead of iterating the
            // names Enumeration seems faster as a way to know if something exists (in the cases when we are checking
            // for existing keys a good % of the total times).
            if (this.request.getAttribute(name) != null) {
                return true;
            }

            return existsInEnumeration(this.request.getAttributeNames(), name);

        }


        public Object getVariable(final String key) {
            return this.request.getAttribute(key);
        }


        public Set<String> getVariableNames() {

            final Set<String> variableNames = new HashSet<String>(10);
            final Enumeration<String> attributeNamesEnum = this.request.getAttributeNames();
            while (attributeNamesEnum.hasMoreElements()) {
                variableNames.add(attributeNamesEnum.nextElement());
            }
            return variableNames;

        }


        private int searchNameInIndex(final String name, final int idx) {
            int n = this.levelSizes[idx];
            if (name == null) {
                while (n-- != 0) {
                    if (this.names[idx][n] == null) {
                        return n;
                    }
                }
                return -1;
            }
            while (n-- != 0) {
                if (name.equals(this.names[idx][n])) {
                    return n;
                }
            }
            return -1;
        }




        public void put(final String key, final Object value) {
            put(key, value, null);
        }

        private void put(final String key, final Object value, final Set<String> alreadyContainedNames) {

            ensureLevelInitialized();

            if (this.level > 0) {
                // We will only take care of new/old values if we are not on level 0

                int levelIndex = searchNameInIndex(key,this.index);
                if (levelIndex >= 0) {

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
                    // By checking the 'alreadyContainedNames' argument, we try to save calls to the very slow
                    // HttpServletRequest#getAttributeNames() in the case we are calling this from putAll(...)
                    if ((alreadyContainedNames == null && containsVariable(key)) ||
                            (alreadyContainedNames != null && alreadyContainedNames.contains(key))) {
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
            if (map == null || map.isEmpty()) {
                return;
            }
            // By precomputing here a 'alreadyContainedNames' set, we try to save calls to the very slow
            // HttpServletRequest#getAttributeNames() in our calls to put(), which has to determine whether a
            // variable already exists or not (and would otherwise call "containsVariable()")
            // Also note this is only required when level > 0
            final Set<String> alreadyContainedNames = (this.level > 0? getVariableNames() : null);
            for (final Map.Entry<String,Object> entry : map.entrySet()) {
                put(entry.getKey(), entry.getValue(), alreadyContainedNames);
            }
        }


        public void remove(final String key) {
            if (containsVariable(key)) {
                put(key, NON_EXISTING);
            }
        }




        public boolean isVariableLocal(final String name) {

            if (this.level == 0) {
                // We are at level 0, so we cannot have local variables at all
                return false;
            }

            int n = this.index + 1;
            while (n-- > 1) { // variables at n == 0 are not local!
                final int idx = searchNameInIndex(name, n);
                if (idx >= 0) {
                    return this.newValues[n][idx] != NON_EXISTING;
                }
            }

            return false;

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
            ensureLevelInitialized();
            this.selectionTargets[this.index] = new SelectionTarget(selectionTarget);
        }




        public IInliner getInliner() {
            int n = this.index + 1;
            while (n-- != 0) {
                if (this.inliners[n] != null) {
                    if (this.inliners[n] == NoOpInliner.INSTANCE) {
                        return null;
                    }
                    return this.inliners[n];
                }
            }
            return null;
        }


        public void setInliner(final IInliner inliner) {
            ensureLevelInitialized();
            // We use NoOpInliner.INSTACE in order to signal when inlining has actually been disabled
            this.inliners[this.index] = (inliner == null? NoOpInliner.INSTANCE : inliner);
        }



        private void ensureLevelInitialized() {

            // First, check if the current index already signals the current level (in which case, everything is OK)
            if (this.levels[this.index] != this.level) {

                // The current level still had no index assigned -- we must do it, and maybe even grow structures

                this.index++; // This new index will be the one for our level

                if (this.levels.length == this.index) {
                    final int[] newLevels = new int[this.levels.length + DEFAULT_LEVELS_SIZE];
                    final String[][] newNames = new String[this.names.length + DEFAULT_LEVELS_SIZE][];
                    final Object[][] newNewValues = new Object[this.newValues.length + DEFAULT_LEVELS_SIZE][];
                    final Object[][] newOldValues = new Object[this.oldValues.length + DEFAULT_LEVELS_SIZE][];
                    final int[] newLevelSizes = new int[this.levelSizes.length + DEFAULT_LEVELS_SIZE];
                    final SelectionTarget[] newSelectionTargets = new SelectionTarget[this.selectionTargets.length + DEFAULT_LEVELS_SIZE];
                    final IInliner[] newInliners = new IInliner[this.inliners.length + DEFAULT_LEVELS_SIZE];
                    Arrays.fill(newLevels, Integer.MAX_VALUE);
                    Arrays.fill(newNames, null);
                    Arrays.fill(newNewValues, null);
                    Arrays.fill(newOldValues, null);
                    Arrays.fill(newLevelSizes, 0);
                    Arrays.fill(newSelectionTargets, null);
                    Arrays.fill(newInliners, null);
                    System.arraycopy(this.levels, 0, newLevels, 0, this.levels.length);
                    System.arraycopy(this.names, 0, newNames, 0, this.names.length);
                    System.arraycopy(this.newValues, 0, newNewValues, 0, this.newValues.length);
                    System.arraycopy(this.oldValues, 0, newOldValues, 0, this.oldValues.length);
                    System.arraycopy(this.levelSizes, 0, newLevelSizes, 0, this.levelSizes.length);
                    System.arraycopy(this.selectionTargets, 0, newSelectionTargets, 0, this.selectionTargets.length);
                    System.arraycopy(this.inliners, 0, newInliners, 0, this.inliners.length);
                    this.levels = newLevels;
                    this.names = newNames;
                    this.newValues = newNewValues;
                    this.oldValues = newOldValues;
                    this.levelSizes = newLevelSizes;
                    this.selectionTargets = newSelectionTargets;
                    this.inliners = newInliners;
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

                if (this.names[this.index] != null && this.levelSizes[this.index] > 0) {
                    // There were movements at this level, so we have to revert them

                    int n = this.levelSizes[this.index];
                    while (n-- != 0) {
                        final String name = this.names[this.index][n];
                        final Object newValue = this.newValues[this.index][n];
                        final Object oldValue = this.oldValues[this.index][n];
                        if (newValue == NON_EXISTING) {
                            if (!containsVariable(name)) {
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

                this.selectionTargets[this.index] = null;
                this.inliners[this.index] = null;

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
                final Map<String,Object> levelVars = new LinkedHashMap<String, Object>();
                if (this.names[n] != null && this.levelSizes[n] > 0) {
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
                                if (containsVariable(name)) {
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
                }
                if (!levelVars.isEmpty() || this.selectionTargets[n] != null || this.inliners[n] != null) {
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
                    if (this.inliners[n] != null) {
                        strBuilder.append("[" + this.inliners[n].getName() + "]");
                    }
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
            strBuilder.append(this.levels[n] + ":");
            strBuilder.append(requestAttributes.toString());
            if (this.selectionTargets[0] != null) {
                strBuilder.append("<" + this.selectionTargets[0].selectionTarget + ">");
            }
            if (this.inliners[0] != null) {
                strBuilder.append("[" + this.inliners[0].getName() + "]");
            }
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
            final String textInliningStr = (getInliner() != null? "[" + getInliner().getName() + "]" : "" );
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




    private static boolean existsInEnumeration(final Enumeration<String> enumeration, final String value) {
        if (value == null) {
            while (enumeration.hasMoreElements()) {
                if (enumeration.nextElement() == null) {
                    return true;
                }
            }
            return false;
        }
        while (enumeration.hasMoreElements()) {
            if (value.equals(enumeration.nextElement())) {
                return true;
            }
        }
        return false;
    }


}
