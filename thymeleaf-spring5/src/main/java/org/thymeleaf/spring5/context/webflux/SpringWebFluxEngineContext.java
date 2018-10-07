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
package org.thymeleaf.spring5.context.webflux;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.AbstractEngineContext;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.EngineContext;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.ILazyContextVariable;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.spring5.context.SpringContextUtils;
import org.thymeleaf.util.Validate;
import reactor.core.publisher.Mono;

/**
 * <p>
 *   Basic <b>web</b> implementation of the {@link IEngineContext} interface, based on the Spring WebFlux
 *   infrastructure.
 * </p>
 * <p>
 *   This is the context implementation that will be used by default for template processing in Spring WebFlux
 *   environments. Note that <b>this is an internal implementation, and there is no reason for users' code to
 *   directly reference or use it instead of its implemented interfaces</b>.
 * </p>
 * <p>
 *   This class is NOT thread-safe. Thread-safety is not a requirement for context implementations.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public class SpringWebFluxEngineContext
        extends AbstractEngineContext implements IEngineContext, ISpringWebFluxContext {


    private static final String PARAM_VARIABLE_NAME = "param";
    private static final String SESSION_VARIABLE_NAME = "session";

    private final ServerHttpRequest request;
    private final ServerHttpResponse response;
    private final ServerWebExchange exchange;

    private final WebExchangeAttributesVariablesMap webExchangeAttributesVariablesMap;
    private final Map<String,Object> requestParametersVariablesMap;
    private final Map<String,Object> sessionAttributesVariablesMap;




    /**
     * <p>
     *   Creates a new instance of this {@link IEngineContext} implementation binding engine execution to
     *   the Spring WebFlux request handling mechanisms, mainly modelled by {@link ServerWebExchange}.
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
     * @param exchange the web exchange object being used for request handling.
     * @param locale the locale.
     * @param variables the context variables, probably coming from another {@link IContext} implementation.
     */
    public SpringWebFluxEngineContext(
            final IEngineConfiguration configuration,
            final TemplateData templateData,
            final Map<String,Object> templateResolutionAttributes,
            final ServerWebExchange exchange,
            final Locale locale,
            final Map<String, Object> variables) {

        super(configuration, templateResolutionAttributes, locale);

        Validate.notNull(exchange, "Server Web Exchange cannot be null in web variables map");

        this.exchange = exchange;
        this.request = this.exchange.getRequest();
        this.response = this.exchange.getResponse();
        // No need to call "Mono<WebSession> this.exchange.getSession()" because the reactive variable it returns
        // should have been already configured by SpringStandardDialect for async resolution before View execution
        // (by means of a specially-named execution attribute), so we should be instead retrieving the WebSession
        // (not he reactive Mono<WebSession>) from the context.

        this.webExchangeAttributesVariablesMap =
                new WebExchangeAttributesVariablesMap(configuration, templateData, templateResolutionAttributes, this.exchange, locale, variables);
        this.requestParametersVariablesMap = new RequestParametersMap(this.request);
        this.sessionAttributesVariablesMap =
                new SessionAttributesMap(this.webExchangeAttributesVariablesMap);

    }


    @Override
    public ServerHttpRequest getRequest() {
        return this.request;
    }


    @Override
    public ServerHttpResponse getResponse() {
        return this.response;
    }


    @Override
    public Mono<WebSession> getSession() {
        return this.exchange.getSession();
    }


    @Override
    public ServerWebExchange getExchange() {
        return this.exchange;
    }


    @Override
    public boolean containsVariable(final String name) {
        return SESSION_VARIABLE_NAME.equals(name) ||
                PARAM_VARIABLE_NAME.equals(name) ||
                this.webExchangeAttributesVariablesMap.containsVariable(name);
    }


    @Override
    public Object getVariable(final String key) {
        if (SESSION_VARIABLE_NAME.equals(key)) {
            return this.sessionAttributesVariablesMap;
        }
        if (PARAM_VARIABLE_NAME.equals(key)) {
            return this.requestParametersVariablesMap;
        }
        return this.webExchangeAttributesVariablesMap.getVariable(key);
    }


    @Override
    public Set<String> getVariableNames() {
        // Note this set will NOT include 'param', 'session' or 'application', as they are considered special
        // ways to access attributes/parameters in these Servlet API structures
        return this.webExchangeAttributesVariablesMap.getVariableNames();
    }


    @Override
    public void setVariable(final String name, final Object value) {
        if (SESSION_VARIABLE_NAME.equals(name) || PARAM_VARIABLE_NAME.equals(name)) {
            throw new IllegalArgumentException(
                    "Cannot set variable called '" + name + "' into web variables map: such name is a reserved word");
        }
        this.webExchangeAttributesVariablesMap.setVariable(name, value);
    }


    @Override
    public void setVariables(final Map<String, Object> variables) {
        if (variables == null || variables.isEmpty()) {
            return;
        }
        // First perform reserved word check on every variable name to be inserted
        for (final String name : variables.keySet()) {
            if (SESSION_VARIABLE_NAME.equals(name) || PARAM_VARIABLE_NAME.equals(name)) {
                throw new IllegalArgumentException(
                        "Cannot set variable called '" + name + "' into web variables map: such name is a reserved word");
            }
        }
        this.webExchangeAttributesVariablesMap.setVariables(variables);
    }


    @Override
    public void removeVariable(final String name) {
        if (SESSION_VARIABLE_NAME.equals(name) || PARAM_VARIABLE_NAME.equals(name)) {
            throw new IllegalArgumentException(
                    "Cannot remove variable called '" + name + "' in web variables map: such name is a reserved word");
        }
        this.webExchangeAttributesVariablesMap.removeVariable(name);
    }


    @Override
    public boolean isVariableLocal(final String name) {
        return this.webExchangeAttributesVariablesMap.isVariableLocal(name);
    }


    @Override
    public boolean hasSelectionTarget() {
        return this.webExchangeAttributesVariablesMap.hasSelectionTarget();
    }


    @Override
    public Object getSelectionTarget() {
        return this.webExchangeAttributesVariablesMap.getSelectionTarget();
    }


    @Override
    public void setSelectionTarget(final Object selectionTarget) {
        this.webExchangeAttributesVariablesMap.setSelectionTarget(selectionTarget);
    }




    @Override
    public IInliner getInliner() {
        return this.webExchangeAttributesVariablesMap.getInliner();
    }

    @Override
    public void setInliner(final IInliner inliner) {
        this.webExchangeAttributesVariablesMap.setInliner(inliner);
    }




    @Override
    public TemplateData getTemplateData() {
        return this.webExchangeAttributesVariablesMap.getTemplateData();
    }

    @Override
    public void setTemplateData(final TemplateData templateData) {
        this.webExchangeAttributesVariablesMap.setTemplateData(templateData);
    }


    @Override
    public List<TemplateData> getTemplateStack() {
        return this.webExchangeAttributesVariablesMap.getTemplateStack();
    }




    @Override
    public void setElementTag(final IProcessableElementTag elementTag) {
        this.webExchangeAttributesVariablesMap.setElementTag(elementTag);
    }




    @Override
    public List<IProcessableElementTag> getElementStack() {
        return this.webExchangeAttributesVariablesMap.getElementStack();
    }


    @Override
    public List<IProcessableElementTag> getElementStackAbove(final int contextLevel) {
        return this.webExchangeAttributesVariablesMap.getElementStackAbove(contextLevel);
    }




    @Override
    public int level() {
        return this.webExchangeAttributesVariablesMap.level();
    }


    @Override
    public void increaseLevel() {
        this.webExchangeAttributesVariablesMap.increaseLevel();
    }


    @Override
    public void decreaseLevel() {
        this.webExchangeAttributesVariablesMap.decreaseLevel();
    }




    public String getStringRepresentationByLevel() {
        // Request parameters, session and servlet context can be safely ignored here
        return this.webExchangeAttributesVariablesMap.getStringRepresentationByLevel();
    }




    @Override
    public String toString() {
        // Request parameters, session and servlet context can be safely ignored here
        return this.webExchangeAttributesVariablesMap.toString();
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

        /*
         * NOTE the WebSession is not actually obtained from the ServerWebExchange at this point, but instead
         * from the context. The reason is ServerWebExchange#getSession() returns Mono<WebSession>, and in order
         * to resolve this reactive variable in a non-blocking manner we have used the execution attributes
         * mechanism in the dialect in order to specify that Mono<WebSession> should be resolved before
         * template execution.
         */

        private final WebExchangeAttributesVariablesMap attrVars;
        private WebSession session = null;


        SessionAttributesMap(final WebExchangeAttributesVariablesMap attrVars) {
            super();
            this.attrVars = attrVars;
        }

        private WebSession getSession() {
            if (this.session == null) {
                this.session = (WebSession) this.attrVars.getVariable(SpringContextUtils.WEB_SESSION_ATTRIBUTE_NAME);
            }
            return this.session;
        }

        @Override
        public int size() {
            final WebSession webSession = getSession();
            if (webSession == null) {
                return 0;
            }
            return webSession.getAttributes().size();
        }

        @Override
        public boolean isEmpty() {
            final WebSession webSession = getSession();
            if (webSession == null) {
                return true;
            }
            return webSession.getAttributes().isEmpty();
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
            final WebSession webSession = getSession();
            if (webSession == null) {
                return null;
            }
            return resolveLazy(webSession.getAttributes().get(key != null? key.toString() : null));
        }

        @Override
        public Set<String> keySet() {
            final WebSession webSession = getSession();
            if (webSession == null) {
                return Collections.emptySet();
            }
            return webSession.getAttributes().keySet();
        }

        @Override
        public Collection<Object> values() {
            final WebSession webSession = getSession();
            if (webSession == null) {
                return Collections.emptyList();
            }
            return webSession.getAttributes().values();
        }

        @Override
        public Set<Entry<String,Object>> entrySet() {
            final WebSession webSession = getSession();
            if (webSession == null) {
                return Collections.emptySet();
            }
            return webSession.getAttributes().entrySet();
        }

    }




    private static final class RequestParametersMap extends NoOpMapImpl {

        private final ServerHttpRequest request;

        RequestParametersMap(final ServerHttpRequest request) {
            super();
            this.request = request;
        }


        @Override
        public int size() {
            return this.request.getQueryParams().size();
        }

        @Override
        public boolean isEmpty() {
            return this.request.getQueryParams().isEmpty();
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
            final List<String> parameterValues = this.request.getQueryParams().get(key != null? key.toString() : null);
            if (parameterValues == null) {
                return null;
            }
            return new RequestParameterValues(parameterValues);
        }

        @Override
        public Set<String> keySet() {
            return this.request.getQueryParams().keySet();
        }

        @Override
        public Collection<Object> values() {
            return (Collection<Object>) (Collection<?>) this.request.getQueryParams().values();
        }

        @Override
        public Set<Entry<String,Object>> entrySet() {
            return (Set<Entry<String,Object>>) (Set<?>) this.request.getQueryParams().entrySet();
        }

    }



    /*
     * The variables map used for integration with Spring WebFlux's ServerWebExchange uses
     * the attributes stored in this ServerWebExchange as a default, so that if a variable is not
     * found in the maps managed by the extended EngineContext (which is where it would be if
     * we had set it from the model or the template itself), the ServerWebExchange attributes will
     * be queried.
     */
    private static final class WebExchangeAttributesVariablesMap extends EngineContext {

        private final ServerWebExchange exchange;


        WebExchangeAttributesVariablesMap(
                final IEngineConfiguration configuration,
                final TemplateData templateData,
                final Map<String,Object> templateResolutionAttributes,
                final ServerWebExchange exchange,
                final Locale locale,
                final Map<String, Object> variables) {

            super(configuration, templateData, templateResolutionAttributes, locale, variables);
            this.exchange = exchange;

        }


        @Override
        public boolean containsVariable(final String name) {
            if (super.containsVariable(name)) {
                return true;
            }
            return this.exchange.getAttributes().containsKey(name);
        }


        @Override
        public Object getVariable(final String key) {
            final Object value = super.getVariable(key);
            if (value != null) {
                return value;
            }
            return this.exchange.getAttributes().get(key);
        }


        @Override
        public Set<String> getVariableNames() {
            final Set<String> variableNames = super.getVariableNames();
            variableNames.addAll(this.exchange.getAttributes().keySet());
            return variableNames;
        }


        @Override
        public String getStringRepresentationByLevel() {
            final StringBuilder strBuilder = new StringBuilder(super.getStringRepresentationByLevel());
            strBuilder.append("[[EXCHANGE: " + this.exchange.getAttributes() + "]]");
            return strBuilder.toString();
        }




        @Override
        public String toString() {
            final StringBuilder strBuilder = new StringBuilder(super.toString());
            strBuilder.append("[[EXCHANGE: " + this.exchange.getAttributes() + "]]");
            return strBuilder.toString();
        }

    }





    private abstract static class NoOpMapImpl implements Map<String,Object> {

        protected NoOpMapImpl() {
            super();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean containsKey(final Object key) {
            return false;
        }

        @Override
        public boolean containsValue(final Object value) {
            return false;
        }

        @Override
        public Object get(final Object key) {
            return null;
        }

        @Override
        public Object put(final String key, final Object value) {
            throw new UnsupportedOperationException("Cannot add new entry: map is immutable");
        }

        @Override
        public Object remove(final Object key) {
            throw new UnsupportedOperationException("Cannot remove entry: map is immutable");
        }

        @Override
        public void putAll(final Map<? extends String, ? extends Object> m) {
            throw new UnsupportedOperationException("Cannot add new entry: map is immutable");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("Cannot clear: map is immutable");
        }

        @Override
        public Set<String> keySet() {
            return Collections.emptySet();
        }

        @Override
        public Collection<Object> values() {
            return Collections.emptyList();
        }

        @Override
        public Set<Entry<String,Object>> entrySet() {
            return Collections.emptySet();
        }


        static final class MapEntry implements Entry<String,Object> {

            private final String key;
            private final Object value;

            MapEntry(final String key, final Object value) {
                super();
                this.key = key;
                this.value = value;
            }

            @Override
            public String getKey() {
                return this.key;
            }

            @Override
            public Object getValue() {
                return this.value;
            }

            @Override
            public Object setValue(final Object value) {
                throw new UnsupportedOperationException("Cannot set value: map is immutable");
            }

        }


    }



    private static final class RequestParameterValues extends AbstractList<String> {

        private final List<String> parameterValues;

        RequestParameterValues(final List<String> parameterValues) {
            this.parameterValues = parameterValues;
        }

        @Override
        public int size() {
            return this.parameterValues.size();
        }

        @Override
        public Object[] toArray() {
            return this.parameterValues.toArray();
        }

        @Override
        public <T> T[] toArray(final T[] arr) {
            return this.parameterValues.toArray(arr);
        }

        @Override
        public String get(final int index) {
            return this.parameterValues.get(index);
        }

        @Override
        public int indexOf(final Object obj) {
            return this.parameterValues.indexOf(obj);
        }

        @Override
        public boolean contains(final Object obj) {
            return this.parameterValues.contains(obj);
        }


        @Override
        public String toString() {
            // This toString() method will be responsible of outputting non-indexed request parameters in the
            // way most people expect, i.e. return parameterValues[0] when accessed without index and parameter is
            // single-valued (${param.a}), returning ArrayList#toString() when accessed without index and parameter
            // is multi-valued, and finally return the specific value when accessed with index (${param.a[0]})
            final int size = this.parameterValues.size();
            if (size == 0) {
                return "";
            }
            if (size == 1) {
                return this.parameterValues.get(0);
            }
            return this.parameterValues.toString();
        }
    }

}
