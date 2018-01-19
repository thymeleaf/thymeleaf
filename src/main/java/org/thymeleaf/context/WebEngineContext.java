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

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.inline.NoOpInliner;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Basic <b>web</b> implementation of the {@link IEngineContext} interface, based on the Servlet API.
 * </p>
 * <p>
 *   This is the context implementation that will be used by default for web processing. Note that <b>this is an
 *   internal implementation, and there is no reason for users' code to directly reference or use it instead
 *   of its implemented interfaces</b>.
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
public class WebEngineContext extends AbstractEngineContext implements IEngineContext, IWebContext {

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
     * request object, and for better integration with JSP or any other template
     * engines or view-layer technologies that expect the HttpServletRequest to
     * be the 'only source of truth' for context variables.
     * ---------------------------------------------------------------------------
     */

    private static final String PARAM_VARIABLE_NAME = "param";
    private static final String SESSION_VARIABLE_NAME = "session";
    private static final String APPLICATION_VARIABLE_NAME = "application";

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final HttpSession session;
    private final ServletContext servletContext;

    private final RequestAttributesVariablesMap requestAttributesVariablesMap;
    private final Map<String,Object> requestParametersVariablesMap;
    private final Map<String,Object> sessionAttributesVariablesMap;
    private final Map<String,Object> applicationAttributesVariablesMap;




    /**
     * <p>
     *   Creates a new instance of this {@link IEngineContext} implementation binding engine execution to
     *   the Servlet API.
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
     * @param request the servlet request object.
     * @param response the servlet response object.
     * @param servletContext the servlet context object.
     * @param locale the locale.
     * @param variables the context variables, probably coming from another {@link IContext} implementation.
     */
    public WebEngineContext(
            final IEngineConfiguration configuration,
            final TemplateData templateData,
            final Map<String,Object> templateResolutionAttributes,
            final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext,
            final Locale locale,
            final Map<String, Object> variables) {

        super(configuration, templateResolutionAttributes, locale);

        Validate.notNull(request, "Request cannot be null in web variables map");
        Validate.notNull(response, "Response cannot be null in web variables map");
        Validate.notNull(servletContext, "Servlet Context cannot be null in web variables map");

        this.request = request;
        this.response = response;
        this.session = request.getSession(false);
        this.servletContext = servletContext;

        this.requestAttributesVariablesMap =
                new RequestAttributesVariablesMap(configuration, templateData, templateResolutionAttributes, this.request, locale, variables);
        this.requestParametersVariablesMap = new RequestParametersMap(this.request);
        this.applicationAttributesVariablesMap = new ServletContextAttributesMap(this.servletContext);
        this.sessionAttributesVariablesMap = new SessionAttributesMap(this.session);

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
        return APPLICATION_VARIABLE_NAME.equals(name) || this.requestAttributesVariablesMap.containsVariable(name);
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


    public void setVariable(final String name, final Object value) {
        if (SESSION_VARIABLE_NAME.equals(name) ||
                PARAM_VARIABLE_NAME.equals(name) ||
                APPLICATION_VARIABLE_NAME.equals(name)) {
            throw new IllegalArgumentException(
                    "Cannot set variable called '" + name + "' into web variables map: such name is a reserved word");
        }
        this.requestAttributesVariablesMap.setVariable(name, value);
    }


    public void setVariables(final Map<String, Object> variables) {
        if (variables == null || variables.isEmpty()) {
            return;
        }
        // First perform reserved word check on every variable name to be inserted
        for (final String name : variables.keySet()) {
            if (SESSION_VARIABLE_NAME.equals(name) ||
                    PARAM_VARIABLE_NAME.equals(name) ||
                    APPLICATION_VARIABLE_NAME.equals(name)) {
                throw new IllegalArgumentException(
                        "Cannot set variable called '" + name + "' into web variables map: such name is a reserved word");
            }
        }
        this.requestAttributesVariablesMap.setVariables(variables);
    }


    public void removeVariable(final String name) {
        if (SESSION_VARIABLE_NAME.equals(name) ||
                PARAM_VARIABLE_NAME.equals(name) ||
                APPLICATION_VARIABLE_NAME.equals(name)) {
            throw new IllegalArgumentException(
                    "Cannot remove variable called '" + name + "' in web variables map: such name is a reserved word");
        }
        this.requestAttributesVariablesMap.removeVariable(name);
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




    public TemplateData getTemplateData() {
        return this.requestAttributesVariablesMap.getTemplateData();
    }

    public void setTemplateData(final TemplateData templateData) {
        this.requestAttributesVariablesMap.setTemplateData(templateData);
    }


    public List<TemplateData> getTemplateStack() {
        return this.requestAttributesVariablesMap.getTemplateStack();
    }




    public void setElementTag(final IProcessableElementTag elementTag) {
        this.requestAttributesVariablesMap.setElementTag(elementTag);
    }




    public List<IProcessableElementTag> getElementStack() {
        return this.requestAttributesVariablesMap.getElementStack();
    }


    public List<IProcessableElementTag> getElementStackAbove(final int contextLevel) {
        return this.requestAttributesVariablesMap.getElementStackAbove(contextLevel);
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



    static Object resolveLazy(final Object variable) {
        /*
         * Check the possibility that this variable is a lazy one, in which case we should not return it directly
         * but instead make sure it is initialized and return its value.
         */
        if (variable != null && variable instanceof ILazyContextVariable) {
            return ((ILazyContextVariable)variable).getValue();
        }
        return variable;
    }




    private static final class SessionAttributesMap extends NoOpMapImpl {

        private final HttpSession session;

        SessionAttributesMap(final HttpSession session) {
            super();
            this.session = session;
        }


        @Override
        public int size() {
            if (this.session == null) {
                return 0;
            }
            int size = 0;
            final Enumeration<String> attributeNames = this.session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                attributeNames.nextElement();
                size++;
            }
            return size;
        }

        @Override
        public boolean isEmpty() {
            if (this.session == null) {
                return true;
            }
            final Enumeration<String> attributeNames = this.session.getAttributeNames();
            return !attributeNames.hasMoreElements();
        }

        @Override
        public boolean containsKey(final Object key) {
            // Even if not completely correct to return 'true' for entries that might not exist, this is needed
            // in order to avoid Spring's MapAccessor throwing an exception when trying to access an element
            // that doesn't exist -- in the case of request parameters, session and servletContext attributes most
            // developers would expect null to be returned in such case, and that's what this 'true' will cause.
            return true;
        }

        @Override
        public boolean containsValue(final Object value) {
            // It wouldn't be consistent to have an 'ad hoc' implementation of #containsKey() but a 100% correct
            // implementation of #containsValue(), so we are leaving this as unsupported.
            throw new UnsupportedOperationException("Map does not support #containsValue()");
        }

        @Override
        public Object get(final Object key) {
            if (this.session == null) {
                return null;
            }
            return resolveLazy(this.session.getAttribute(key != null? key.toString() : null));
        }

        @Override
        public Set<String> keySet() {
            if (this.session == null) {
                return Collections.emptySet();
            }
            final Set<String> keySet = new LinkedHashSet<String>(5);
            final Enumeration<String> attributeNames = this.session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                keySet.add(attributeNames.nextElement());
            }
            return keySet;
        }

        @Override
        public Collection<Object> values() {
            if (this.session == null) {
                return Collections.emptySet();
            }
            final List<Object> values = new ArrayList<Object>(5);
            final Enumeration<String> attributeNames = this.session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                values.add(this.session.getAttribute(attributeNames.nextElement()));
            }
            return values;
        }

        @Override
        public Set<Entry<String,Object>> entrySet() {
            if (this.session == null) {
                return Collections.emptySet();
            }
            final Set<Entry<String,Object>> entrySet = new LinkedHashSet<Entry<String, Object>>(5);
            final Enumeration<String> attributeNames = this.session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                final String key = attributeNames.nextElement();
                final Object value = this.session.getAttribute(key);
                entrySet.add(new MapEntry(key, value));
            }
            return entrySet;
        }

    }




    private static final class ServletContextAttributesMap extends NoOpMapImpl {

        private final ServletContext servletContext;

        ServletContextAttributesMap(final ServletContext servletContext) {
            super();
            this.servletContext = servletContext;
        }


        @Override
        public int size() {
            int size = 0;
            final Enumeration<String> attributeNames = this.servletContext.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                attributeNames.nextElement();
                size++;
            }
            return size;
        }

        @Override
        public boolean isEmpty() {
            final Enumeration<String> attributeNames = this.servletContext.getAttributeNames();
            return !attributeNames.hasMoreElements();
        }

        @Override
        public boolean containsKey(final Object key) {
            // Even if not completely correct to return 'true' for entries that might not exist, this is needed
            // in order to avoid Spring's MapAccessor throwing an exception when trying to access an element
            // that doesn't exist -- in the case of request parameters, session and servletContext attributes most
            // developers would expect null to be returned in such case, and that's what this 'true' will cause.
            return true;
        }

        @Override
        public boolean containsValue(final Object value) {
            // It wouldn't be consistent to have an 'ad hoc' implementation of #containsKey() but a 100% correct
            // implementation of #containsValue(), so we are leaving this as unsupported.
            throw new UnsupportedOperationException("Map does not support #containsValue()");
        }

        @Override
        public Object get(final Object key) {
            return resolveLazy(this.servletContext.getAttribute(key != null? key.toString() : null));
        }

        @Override
        public Set<String> keySet() {
            final Set<String> keySet = new LinkedHashSet<String>(5);
            final Enumeration<String> attributeNames = this.servletContext.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                keySet.add(attributeNames.nextElement());
            }
            return keySet;
        }

        @Override
        public Collection<Object> values() {
            final List<Object> values = new ArrayList<Object>(5);
            final Enumeration<String> attributeNames = this.servletContext.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                values.add(this.servletContext.getAttribute(attributeNames.nextElement()));
            }
            return values;
        }

        @Override
        public Set<Map.Entry<String,Object>> entrySet() {
            final Set<Map.Entry<String,Object>> entrySet = new LinkedHashSet<Map.Entry<String, Object>>(5);
            final Enumeration<String> attributeNames = this.servletContext.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                final String key = attributeNames.nextElement();
                final Object value = this.servletContext.getAttribute(key);
                entrySet.add(new MapEntry(key, value));
            }
            return entrySet;
        }

    }




    private static final class RequestParametersMap extends NoOpMapImpl {

        private final HttpServletRequest request;

        RequestParametersMap(final HttpServletRequest request) {
            super();
            this.request = request;
        }


        @Override
        public int size() {
            return this.request.getParameterMap().size();
        }

        @Override
        public boolean isEmpty() {
            return this.request.getParameterMap().isEmpty();
        }

        @Override
        public boolean containsKey(final Object key) {
            // Even if not completely correct to return 'true' for entries that might not exist, this is needed
            // in order to avoid Spring's MapAccessor throwing an exception when trying to access an element
            // that doesn't exist -- in the case of request parameters, session and servletContext attributes most
            // developers would expect null to be returned in such case, and that's what this 'true' will cause.
            return true;
        }

        @Override
        public boolean containsValue(final Object value) {
            // It wouldn't be consistent to have an 'ad hoc' implementation of #containsKey() but a 100% correct
            // implementation of #containsValue(), so we are leaving this as unsupported.
            throw new UnsupportedOperationException("Map does not support #containsValue()");
        }

        @Override
        public Object get(final Object key) {
            final String[] parameterValues = this.request.getParameterValues(key != null? key.toString() : null);
            if (parameterValues == null) {
                return null;
            }
            return new RequestParameterValues(parameterValues);
        }

        @Override
        public Set<String> keySet() {
            return this.request.getParameterMap().keySet();
        }

        @Override
        public Collection<Object> values() {
            return this.request.getParameterMap().values();
        }

        @Override
        public Set<Map.Entry<String,Object>> entrySet() {
            return this.request.getParameterMap().entrySet();
        }

    }




    private static final class RequestAttributesVariablesMap extends AbstractEngineContext implements IEngineContext {

        private static final int DEFAULT_ELEMENT_HIERARCHY_SIZE = 20;
        private static final int DEFAULT_LEVELS_SIZE = 10;
        private static final int DEFAULT_LEVELARRAYS_SIZE = 5;

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
        private TemplateData[] templateDatas;
        private IProcessableElementTag[] elementTags;

        private SelectionTarget lastSelectionTarget = null;
        private IInliner lastInliner = null;
        private TemplateData lastTemplateData = null;

        private final List<TemplateData> templateStack;



        RequestAttributesVariablesMap(
                final IEngineConfiguration configuration,
                final TemplateData templateData,
                final Map<String,Object> templateResolutionAttributes,
                final HttpServletRequest request,
                final Locale locale,
                final Map<String, Object> variables) {

            super(configuration, templateResolutionAttributes, locale);

            this.request = request;

            this.levels = new int[DEFAULT_LEVELS_SIZE];
            this.names = new String[DEFAULT_LEVELS_SIZE][];
            this.oldValues = new Object[DEFAULT_LEVELS_SIZE][];
            this.newValues = new Object[DEFAULT_LEVELS_SIZE][];
            this.levelSizes = new int[DEFAULT_LEVELS_SIZE];
            this.selectionTargets = new SelectionTarget[DEFAULT_LEVELS_SIZE];
            this.inliners = new IInliner[DEFAULT_LEVELS_SIZE];
            this.templateDatas = new TemplateData[DEFAULT_LEVELS_SIZE];

            this.elementTags = new IProcessableElementTag[DEFAULT_ELEMENT_HIERARCHY_SIZE];

            Arrays.fill(this.levels, Integer.MAX_VALUE);
            Arrays.fill(this.names, null);
            Arrays.fill(this.oldValues, null);
            Arrays.fill(this.newValues, null);
            Arrays.fill(this.levelSizes, 0);
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
            return this.request.getAttribute(name) != null;
        }


        public Object getVariable(final String key) {
            return resolveLazy(this.request.getAttribute(key));
        }


        public Set<String> getVariableNames() {
            // --------------------------
            // Note this method relies on HttpServletRequest#getAttributeNames(), which is an extremely slow and
            // inefficient method in implementations like Apache Tomcat's. So the uses of this method should be
            // very controlled and reduced to the minimum. Specifically, any call that executes e.g. for every
            // expression evaluation should be disallowed. Only sporadic uses should be done.
            // Note also it would not be a good idea to cache the attribute names coming from the request if we
            // want to keep complete independence of the HttpServletRequest object, so that it can be modified
            // from the outside (e.g. from other libraries like Tiles) with Thymeleaf perfectly integrating with
            // those modifications.
            // --------------------------
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




        public void setVariable(final String name, final Object value) {

            ensureLevelInitialized(true);

            if (this.level > 0) {
                // We will only take care of new/old values if we are not on level 0

                int levelIndex = searchNameInIndex(name,this.index);
                if (levelIndex >= 0) {

                    // There already is a registered movement for this key - we should modify it instead of creating a new one
                    this.newValues[this.index][levelIndex] = value;

                } else {

                    if (this.names[this.index].length == this.levelSizes[this.index]) {
                        // We need to grow the arrays for this level
                        this.names[this.index] = Arrays.copyOf(this.names[this.index], this.names[this.index].length + DEFAULT_LEVELARRAYS_SIZE);
                        this.newValues[this.index] = Arrays.copyOf(this.newValues[this.index], this.newValues[this.index].length + DEFAULT_LEVELARRAYS_SIZE);
                        this.oldValues[this.index] = Arrays.copyOf(this.oldValues[this.index], this.oldValues[this.index].length + DEFAULT_LEVELARRAYS_SIZE);
                    }

                    levelIndex = this.levelSizes[this.index]; // We will add at the end

                    this.names[this.index][levelIndex] = name;

                    /*
                     * Per construction, according to the Servlet API, an attribute set to null and a non-existing
                     * attribute are exactly the same. So we don't really have a reason to worry about the attribute
                     * already existing or not when it was set to null.
                     */
                    this.oldValues[this.index][levelIndex] = this.request.getAttribute(name);

                    this.newValues[this.index][levelIndex] = value;

                    this.levelSizes[this.index]++;

                }

            }

            // No matter if value is null or not. Value null will be equivalent to .removeAttribute()
            this.request.setAttribute(name, value);

        }


        public void setVariables(final Map<String, Object> variables) {
            if (variables == null || variables.isEmpty()) {
                return;
            }
            for (final Map.Entry<String,Object> entry : variables.entrySet()) {
                setVariable(entry.getKey(), entry.getValue());
            }
        }


        public void removeVariable(final String name) {
            setVariable(name, null);
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
                    return this.newValues[n][idx] != null;
                }
            }

            return false;

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
            ensureLevelInitialized(false);
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
            ensureLevelInitialized(false);
            // We use NoOpInliner.INSTACE in order to signal when inlining has actually been disabled
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
            ensureLevelInitialized(false);
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




        private void ensureLevelInitialized(final boolean initVariables) {

            // First, check if the current index already signals the current level (in which case, everything is OK)
            if (this.levels[this.index] != this.level) {

                // The current level still had no index assigned -- we must do it, and maybe even grow structures

                this.index++; // This new index will be the one for our level

                if (this.levels.length == this.index) {
                    this.levels = Arrays.copyOf(this.levels, this.levels.length + DEFAULT_LEVELS_SIZE);
                    Arrays.fill(this.levels, this.index, this.levels.length, Integer.MAX_VALUE); // We fill the new places with MAX_VALUE
                    this.names = Arrays.copyOf(this.names, this.names.length + DEFAULT_LEVELS_SIZE);
                    this.newValues = Arrays.copyOf(this.newValues, this.newValues.length + DEFAULT_LEVELS_SIZE);
                    this.oldValues = Arrays.copyOf(this.oldValues, this.oldValues.length + DEFAULT_LEVELS_SIZE);
                    this.levelSizes = Arrays.copyOf(this.levelSizes, this.levelSizes.length + DEFAULT_LEVELS_SIZE);
                    // No need to initialize new places in this.levelSizes as copyOf already fills with zeroes
                    this.selectionTargets = Arrays.copyOf(this.selectionTargets, this.selectionTargets.length + DEFAULT_LEVELS_SIZE);
                    this.inliners = Arrays.copyOf(this.inliners, this.inliners.length + DEFAULT_LEVELS_SIZE);
                    this.templateDatas = Arrays.copyOf(this.templateDatas, this.templateDatas.length + DEFAULT_LEVELS_SIZE);
                }

                this.levels[this.index] = this.level;

            }

            if (this.level > 0) {
                // We will only take care of new/old values if we are not on level 0

                if (initVariables && this.names[this.index] == null) {
                    // the arrays for this level have still not been created

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
                        final Object currentValue = this.request.getAttribute(name);
                        if (newValue == currentValue) {
                            // Only if the value matches, in order to avoid modifying values that have been set directly
                            // into the request.
                            this.request.setAttribute(name,oldValue);
                        }
                    }
                    this.levelSizes[this.index] = 0;

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
                            if (newValue != this.request.getAttribute(name)) {
                                continue;
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
            final Map<String,Object> requestAttributes = new LinkedHashMap<String, Object>();
            final Enumeration<String> attrNames = this.request.getAttributeNames();
            while (attrNames.hasMoreElements()) {
                final String name = attrNames.nextElement();
                if (oldValuesSum.containsKey(name)) {
                    final Object oldValue = oldValuesSum.get(name);
                    if (oldValue != null) {
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
                    if (oldValue != null) {
                        requestAttributes.put(name, oldValue);
                    }
                }
            }
            if (strBuilder.length() > 1) {
                strBuilder.append(',');
            }
            strBuilder.append(this.levels[n]).append(":");
            strBuilder.append(requestAttributes.toString());
            if (this.selectionTargets[0] != null) {
                strBuilder.append("<").append(this.selectionTargets[0].selectionTarget).append(">");
            }
            if (this.inliners[0] != null) {
                strBuilder.append("[").append(this.inliners[0].getName()).append("]");
            }
            if (this.templateDatas[0] != null) {
                strBuilder.append("(").append(this.templateDatas[0].getTemplate()).append(")");
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
            final String templateDataStr = "(" + getTemplateData().getTemplate() + ")";
            return equivalentMap.toString() + (hasSelectionTarget()? "<" + getSelectionTarget() + ">" : "") + textInliningStr + templateDataStr;

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





    private abstract static class NoOpMapImpl implements Map<String,Object> {

        protected NoOpMapImpl() {
            super();
        }

        public int size() {
            return 0;
        }

        public boolean isEmpty() {
            return true;
        }

        public boolean containsKey(final Object key) {
            return false;
        }

        public boolean containsValue(final Object value) {
            return false;
        }

        public Object get(final Object key) {
            return null;
        }

        public Object put(final String key, final Object value) {
            throw new UnsupportedOperationException("Cannot add new entry: map is immutable");
        }

        public Object remove(final Object key) {
            throw new UnsupportedOperationException("Cannot remove entry: map is immutable");
        }

        public void putAll(final Map<? extends String, ? extends Object> m) {
            throw new UnsupportedOperationException("Cannot add new entry: map is immutable");
        }

        public void clear() {
            throw new UnsupportedOperationException("Cannot clear: map is immutable");
        }

        public Set<String> keySet() {
            return Collections.emptySet();
        }

        public Collection<Object> values() {
            return Collections.emptyList();
        }

        public Set<Entry<String,Object>> entrySet() {
            return Collections.emptySet();
        }


        static final class MapEntry implements Map.Entry<String,Object> {

            private final String key;
            private final Object value;

            MapEntry(final String key, final Object value) {
                super();
                this.key = key;
                this.value = value;
            }

            public String getKey() {
                return this.key;
            }

            public Object getValue() {
                return this.value;
            }

            public Object setValue(final Object value) {
                throw new UnsupportedOperationException("Cannot set value: map is immutable");
            }

        }


    }



    private static final class RequestParameterValues extends AbstractList<String> {

        private final String[] parameterValues;
        public final int length;

        RequestParameterValues(final String[] parameterValues) {
            this.parameterValues = parameterValues;
            this.length = this.parameterValues.length;
        }

        @Override
        public int size() {
            return this.length;
        }

        @Override
        public Object[] toArray() {
            return this.parameterValues.clone();
        }

        @Override
        public <T> T[] toArray(final T[] arr) {
            if (arr.length < this.length) {
                final T[] copy = (T[]) Array.newInstance(arr.getClass().getComponentType(), this.length);
                System.arraycopy(this.parameterValues, 0, copy, 0, this.length);
                return copy;
            }
            System.arraycopy(this.parameterValues, 0, arr, 0, this.length);
            if (arr.length > this.length) {
                arr[this.length] = null;
            }
            return arr;
        }

        @Override
        public String get(final int index) {
            return this.parameterValues[index];
        }

        @Override
        public int indexOf(final Object obj) {
            final String[] a = this.parameterValues;
            if (obj == null) {
                for (int i = 0; i < a.length; i++) {
                    if (a[i] == null) {
                        return i;
                    }
                }
            } else {
                for (int i = 0; i < a.length; i++) {
                    if (obj.equals(a[i])) {
                        return i;
                    }
                }
            }
            return -1;
        }

        @Override
        public boolean contains(final Object obj) {
            return indexOf(obj) != -1;
        }


        @Override
        public String toString() {
            // This toString() method will be responsible of outputting non-indexed request parameters in the
            // way most people expect, i.e. return parameterValues[0] when accessed without index and parameter is
            // single-valued (${param.a}), returning ArrayList#toString() when accessed without index and parameter
            // is multi-valued, and finally return the specific value when accessed with index (${param.a[0]})
            if (this.length == 0) {
                return "";
            }
            if (this.length == 1) {
                return this.parameterValues[0];
            }
            return super.toString();
        }
    }

}
