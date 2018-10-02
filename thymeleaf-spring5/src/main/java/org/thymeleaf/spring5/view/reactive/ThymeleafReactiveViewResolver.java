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
package org.thymeleaf.spring5.view.reactive;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.reactive.result.view.RedirectView;
import org.springframework.web.reactive.result.view.View;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.reactive.result.view.ViewResolverSupport;
import org.thymeleaf.spring5.ISpringWebFluxTemplateEngine;
import org.thymeleaf.util.Validate;
import reactor.core.publisher.Mono;


/**
 * <p>
 *   Implementation of the Spring WebFlux {@link ViewResolver}
 *   interface.
 * </p>
 * <p>
 *   View resolvers execute after the controller ends its execution. They receive the name
 *   of the view to be processed and are in charge of creating (and configuring) the
 *   corresponding {@link View} object for it.
 * </p>
 * <p>
 *   The {@link View} implementations managed by this class are of type {@link ThymeleafReactiveView}.
 * </p>
 * <p>
 *   In Spring WebFlux applications, Thymeleaf has three modes of operation depending on whether a limit
 *   has been set for the output chunk size and/or data-driver context variables have been specified:
 * </p>
 * <ul>
 *   <li><em>FULL</em>, when no limit for max chunk size is established ({@link #setResponseMaxChunkSizeBytes(int)})
 *       and no data-driver context variable has been specified. All template output will be generated in memory
 *       as a single chunk (a single {@link org.springframework.core.io.buffer.DataBuffer} object)
 *       and then sent to the server's output channels. In this mode, the Thymeleaf template engine
 *       works <em>unthrottled</em>, which may benefit performance in some scenarios with small templates,
 *       at the cost of a higher memory consumption.</li>
 *   <li><em>CHUNKED</em>, when a limit for max chunk size is established ({@link #setResponseMaxChunkSizeBytes(int)})
 *       but no data-driver context variable has been specified. Template output will be generated in chunks of a
 *       size equal or less than the specified limit (in bytes) and then sent to the server's output channels.
 *       After each chunk is emitted, the template engine will stop (thanks to its <em>throttling</em> mechanism), and
 *       wait for the server to request more chunks by means of reactive <em>backpressure</em>. Note
 *       this mechanism works single-threaded. When using this execution mode, the response will be configured
 *       by this {@link ViewResolver} so that each output chunk emitted provokes a <b>flush</b> operation at the
 *       server output channels (so that partial content is sent to the browser/client).</li>
 *   <li><em>DATA-DRIVEN</em>, when a <em>data-driver</em> variable has been specified at the context
 *       (by implementing {@link org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable}). This
 *       variable is expected to contain a <em>data stream</em> (usually in the shape of a
 *       {@link org.reactivestreams.Publisher} that Thymeleaf will consume, creating markup output as data
 *       is streamed from this <em>data-driver</em> and letting the output channels throttle
 *       template engine execution by means of back-pressure. When working in this mode, the response will be
 *       configured by this {@link ViewResolver} so that the server output channels are flushed after each
 *       engine execution (which will happen for each <em>buffer</em> (of configurable size) of elements
 *       collected from the <em>data-driver</em> stream. Additionally, if a value has been set for this
 *       {@code responseMaxChunkSizeBytes} property, the emitted
 *       {@link org.springframework.core.io.buffer.DataBuffer} output chunks will never exceed this size,
 *       and therefore more than one chunk could be emitted for each buffer of <em>data-driver</em> elements.</li>
 * </ul>
 * <p>
 *   Also note that the properties set by means of {@link #setFullModeViewNames(String[])} and
 *   {@link #setChunkedModeViewNames(String[])} also influence and fine-tune which templates are
 *   executed in {@code FULL} or {@code CHUNKED} mode (they have no effect on {@code DATA-DRIVEN}).
 * </p>
 * <p>
 *   Also note that {@link ThymeleafReactiveView} objects can be specifically configured to be executed in
 *   {@code CHUNKED} mode by instantiating prototypes of them for the desired view names and setting a
 *   per-view max chunk size by means of {@link ThymeleafReactiveView#setResponseMaxChunkSizeBytes(int)}. If this
 *   is set to {@link Integer#MAX_VALUE}, they will be effectively configured to execute in {@code FULL} mode. This
 *   per-view setting will always have higher precedence than the one performed at the {@link ViewResolver} level.
 * </p>
 *
 * @see ThymeleafReactiveView
 * @see ISpringWebFluxTemplateEngine
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public class ThymeleafReactiveViewResolver
        extends ViewResolverSupport
        implements ViewResolver, ApplicationContextAware {

    
    private static final Logger vrlogger = LoggerFactory.getLogger(ThymeleafReactiveViewResolver.class);


    /**
     * <p>
     *   Prefix to be used in view names (returned by controllers) for specifying an
     *   HTTP redirect.
     * </p>
     * <p>
     *   Value: {@code redirect:}
     * </p>
     */
    public static final String REDIRECT_URL_PREFIX = "redirect:";

    // TODO * Will this exist in future versions of Spring WebFlux? See https://jira.spring.io/browse/SPR-14537
    public static final String FORWARD_URL_PREFIX = "forward:";

    // Supported media types are all those defined at org.thymeleaf.util.ContentTypeUtils
    // Note that Spring will automatically perform content type negotiation based on the request query and a (possible)
    // HTTP Accept header, so there is no additional operation needed at the Thymeleaf side (template mode will
    // not be forced from the View/ViewResolvers side, but instead will be left to the template resolvers, which
    // might apply their own file extension suffix-based mechanism for a certain degree of auto-resolution).
    private static final List<MediaType> SUPPORTED_MEDIA_TYPES =
            Arrays.asList(new MediaType[] {
                    MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML_XML, // HTML
                    MediaType.APPLICATION_XML, MediaType.TEXT_XML,        // XML
                    MediaType.APPLICATION_RSS_XML,                        // RSS
                    MediaType.APPLICATION_ATOM_XML,                       // ATOM
                    new MediaType("application", "javascript"),           // JAVASCRIPT
                    new MediaType("application", "ecmascript"),           //
                    new MediaType("text", "javascript"),                  //
                    new MediaType("text", "ecmascript"),                  //
                    MediaType.APPLICATION_JSON,                           // JSON
                    new MediaType("text", "css"),                         // CSS
                    MediaType.TEXT_PLAIN,                                 // TEXT
                    MediaType.TEXT_EVENT_STREAM});                        // SERVER-SENT EVENTS (SSE)


    private ApplicationContext applicationContext;

    // This provider function for redirect mirrors what is done at the reactive version of UrlBasedViewResolver
    private Function<String, RedirectView> redirectViewProvider = url -> new RedirectView(url);

    private boolean alwaysProcessRedirectAndForward = true;

    private Class<? extends ThymeleafReactiveView> viewClass = ThymeleafReactiveView.class;
    private String[] viewNames = null;
    private String[] excludedViewNames = null;
    private int order = Integer.MAX_VALUE;

    private final Map<String, Object> staticVariables = new LinkedHashMap<String, Object>(10);


    // This will determine whether we will be throttling or not, and if so the size of the chunks that will be produced
    // by the throttled engine each time the back-pressure mechanism asks for a new "unit" (a new DataBuffer)
    //
    // The value established here will be a default value, which can be overridden by specific views at the
    // ThymeleafReactiveView class
    private int responseMaxChunkSizeBytes = ThymeleafReactiveView.DEFAULT_RESPONSE_CHUNK_SIZE_BYTES;

    private String[] fullModeViewNames = null;
    private String[] chunkedModeViewNames = null;

    private ISpringWebFluxTemplateEngine templateEngine;






    /**
     * <p>
     *   Create an instance of {@code ThymeleafReactiveViewResolver}.
     * </p>
     */
    public ThymeleafReactiveViewResolver() {
        super();
        setSupportedMediaTypes(SUPPORTED_MEDIA_TYPES);
    }




    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }




    /**
     * <p>
     *   Set the view class that should be used to create views. This must be a subclass
     *   of {@link ThymeleafReactiveView}.
     * </p>
     *
     * @param viewClass class that is assignable to the required view class
     *        (by default, ThymeleafReactiveView).
     */
    public void setViewClass(final Class<? extends ThymeleafReactiveView> viewClass) {
        if (viewClass == null || !ThymeleafReactiveView.class.isAssignableFrom(viewClass)) {
            throw new IllegalArgumentException(
                    "Given view class [" + (viewClass != null ? viewClass.getName() : null) +
                    "] is not of type [" + ThymeleafReactiveView.class.getName() + "]");
        }
        this.viewClass = viewClass;
    }
    
    
    protected Class<? extends ThymeleafReactiveView> getViewClass() {
        return this.viewClass;
    }




    /**
     * <p>
     *   Returns the Thymeleaf template engine instance
     *   (implementation of {@link ISpringWebFluxTemplateEngine} to be used for the
     *   execution of templates.
     * </p>
     *
     * @return the template engine being used for processing templates.
     */
    public ISpringWebFluxTemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }


    /**
     * <p>
     *   Set the template engine object (implementation of {@link ISpringWebFluxTemplateEngine} to be
     *   used for processing templates.
     * </p>
     *
     * @param templateEngine the template engine.
     */
    public void setTemplateEngine(final ISpringWebFluxTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }




    /**
     * <p>
     *   Return the static variables, which will be available at the context
     *   every time a view resolved by this ViewResolver is processed.
     * </p>
     * <p>
     *   These static variables are added to the context by the view resolver
     *   before every view is processed, so that they can be referenced from
     *   the context like any other context variables, for example:
     *   {@code ${myStaticVar}}.
     * </p>
     *
     * @return the map of static variables to be set into views' execution.
     */
    public Map<String,Object> getStaticVariables() {
        return Collections.unmodifiableMap(this.staticVariables);
    }


    /**
     * <p>
     *   Add a new static variable.
     * </p>
     * <p>
     *   These static variables are added to the context by the view resolver
     *   before every view is processed, so that they can be referenced from
     *   the context like any other context variables, for example:
     *   {@code ${myStaticVar}}.
     * </p>
     *
     * @param name the name of the static variable
     * @param value the value of the static variable
     */
    public void addStaticVariable(final String name, final Object value) {
        this.staticVariables.put(name, value);
    }


    /**
     * <p>
     *   Sets a set of static variables, which will be available at the context
     *   every time a view resolved by this ViewResolver is processed.
     * </p>
     * <p>
     *   This method <b>does not overwrite</b> the existing static variables, it
     *   simply adds the ones specify to any variables already registered.
     * </p>
     * <p>
     *   These static variables are added to the context by the view resolver
     *   before every view is processed, so that they can be referenced from
     *   the context like any other context variables, for example:
     *   {@code ${myStaticVar}}.
     * </p>
     *
     *
     * @param variables the set of variables to be added.
     */
    public void setStaticVariables(final Map<String, ?> variables) {
        if (variables != null) {
            for (final Map.Entry<String, ?> entry : variables.entrySet()) {
                addStaticVariable(entry.getKey(), entry.getValue());
            }
        }
    }




    /**
     * <p>
     *   Specify the order in which this view resolver will be queried.
     * </p>
     * <p>
     *   Spring Web applications can have several view resolvers configured,
     *   and this {@code order} property established the order in which
     *   they will be queried for view resolution.
     * </p>
     *
     * @param order the order in which this view resolver will be asked to resolve
     *        the view.
     */
    public void setOrder(final int order) {
        this.order = order;
    }


    /**
     * <p>
     *   Returns the order in which this view resolver will be queried.
     * </p>
     * <p>
     *   Spring Web applications can have several view resolvers configured,
     *   and this {@code order} property established the order in which
     *   they will be queried for view resolution.
     * </p>
     *
     * @return the order
     */
    public int getOrder() {
        return this.order;
    }



    /**
     * <p>
     *   Sets the provider function for creating {@link RedirectView} instances when a redirect
     *   request is passed to the view resolver.
     * </p>
     * <p>
     *   Note the parameter specified to the function will be the {@code URL} of the redirect
     *   (as specified in the view name returned by the controller, without the {@code redirect:}
     *   prefix).
     * </p>
     *
     * @param redirectViewProvider the redirect-view provider function.
     */
    public void setRedirectViewProvider(final Function<String, RedirectView> redirectViewProvider) {
        Validate.notNull(redirectViewProvider, "RedirectView provider cannot be null");
        this.redirectViewProvider = redirectViewProvider;
    }


    /**
     * <p>
     *   Returns the provider function for creating {@link RedirectView} instances when a redirect
     *   request is passed to the view resolver.
     * </p>
     * <p>
     *   Note the parameter specified to the function will be the {@code URL} of the redirect
     *   (as specified in the view name returned by the controller, without the {@code redirect:}
     *   prefix).
     * </p>
     *
     * @return the redirect-view provider function.
     */
    public Function<String, RedirectView> getRedirectViewProvider() {
        return this.redirectViewProvider;
    }




    /**
     * <p>
     *   Set whether this view resolver should always process forwards and redirects independently of the value of
     *   the {@code viewNames} property.
     * </p>
     * <p>
     *   When this flag is set to {@code true} (default value), any view name that starts with the
     *   {@code redirect:} or {@code forward:} prefixes will be resolved by this ViewResolver even if the view names
     *   would not match what is established at the {@code viewNames} property.
     * </p>
     * <p>
     *   Note that the behaviour of <em>resolving</em> view names with these prefixes is exactly the same with this
     *   flag set to {@code true} or {@code false} (perform an HTTP redirect or forward to an internal resource).
     *   The only difference is whether the prefixes have to be explicitly specified at {@code viewNames} or not.
     * </p>
     * <p>
     *   Default value is {@code true}.
     * </p>
     *
     * @param alwaysProcessRedirectAndForward true if redirects and forwards are always processed, false if this will
     *                                     depend on what is established at the viewNames property.
     */
    public void setAlwaysProcessRedirectAndForward(final boolean alwaysProcessRedirectAndForward) {
        this.alwaysProcessRedirectAndForward = alwaysProcessRedirectAndForward;
    }


    /**
     * <p>
     *   Return whether this view resolver should always process forwards and redirects independently of the value of
     *   the {@code viewNames} property.
     * </p>
     * <p>
     *   When this flag is set to {@code true} (default value), any view name that starts with the
     *   {@code redirect:} or {@code forward:} prefixes will be resolved by this ViewResolver even if the view names
     *   would not match what is established at the {@code viewNames} property.
     * </p>
     * <p>
     *   Note that the behaviour of <em>resolving</em> view names with these prefixes is exactly the same with this
     *   flag set to {@code true} or {@code false} (perform an HTTP redirect or forward to an internal resource).
     *   The only difference is whether the prefixes have to be explicitly specified at {@code viewNames} or not.
     * </p>
     * <p>
     *   Default value is {@code true}.
     * </p>
     *
     * @return whether redirects and forwards will be always processed by this view resolver or else only when they are
     *         matched by the {@code viewNames} property.
     *
     */
    public boolean getAlwaysProcessRedirectAndForward() {
        return this.alwaysProcessRedirectAndForward;
    }




    /**
     * <p>
     *   Set the maximum size (in bytes) allowed for the chunks ({@link org.springframework.core.io.buffer.DataBuffer})
     *   that are produced by the Thymeleaf engine and passed to the server as output.
     * </p>
     * <p>
     *   In Spring WebFlux applications, Thymeleaf has three modes of operation depending on whether a limit
     *   has been set for the output chunk size and/or data-driver context variables have been specified:
     * </p>
     * <ul>
     *   <li><em>FULL</em>, when no limit for max chunk size is established and no data-driver context variable
     *       has been specified. All template output will be generated in memory as a single chunk
     *       (a single {@link org.springframework.core.io.buffer.DataBuffer} object)
     *       and then sent to the server's output channels. In this mode, the Thymeleaf template engine
     *       works <em>unthrottled</em>, which may benefit performance in some scenarios with small templates,
     *       at the cost of a higher memory consumption.</li>
     *   <li><em>CHUNKED</em>, when a limit for max chunk size is established but no data-driver context
     *       variable has been specified. Template output will be generated in chunks of a size equal or less
     *       than the specified limit (in bytes) and then sent to the server's output channels. After each chunk
     *       is emitted, the template engine will stop (thanks to its <em>throttling</em> mechanism), and
     *       wait for the server to request more chunks by means of reactive <em>backpressure</em>. Note
     *       this mechanism works single-threaded. When using this execution mode, the response will be configured
     *       by this {@link ViewResolver} so that each output chunk emitted provokes a <b>flush</b> operation at the
     *       server output channels (so that partial content is sent to the browser/client).</li>
     *   <li><em>DATA-DRIVEN</em>, when a <em>data-driver</em> variable has been specified at the context
     *       (implementing {@link org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable}). This
     *       variable is expected to contain a <em>data stream</em> (usually in the shape of a
     *       {@link org.reactivestreams.Publisher} that Thymeleaf will consume, creating markup output as data
     *       is streamed from this <em>data-driver</em> and letting the output channels throttle
     *       template engine execution by means of back-pressure. When working in this mode, the response will be
     *       configured by this {@link ViewResolver} so that the server output channels are flushed after each
     *       engine execution (which will happen for each <em>buffer</em> (of configurable size) of elements
     *       collected from the <em>data-driver</em> stream. Additionally, if a value has been set for this
     *       {@code responseMaxChunkSizeBytes} property, the emitted
     *       {@link org.springframework.core.io.buffer.DataBuffer} output chunks will never exceed this size,
     *       and therefore more than one chunk could be emitted for each buffer of <em>data-driver</em> elements.</li>
     * </ul>
     * <p>
     *   Also note that the properties set by means of {@link #setFullModeViewNames(String[])} and
     *   {@link #setChunkedModeViewNames(String[])} also influence and fine-tune which templates are
     *   executed in {@code FULL} or {@code CHUNKED} mode (they have no effect on {@code DATA-DRIVEN}).
     * </p>
     * <p>
     *   If this property is set to {@code -1} or {@code Integer.MAX_VALUE}, no size limit will be used. Note also
     *   that there is no limit set by default.
     * </p>
     * <p>
     *   Also note that this parameter will be ignored when returning SSE (Server-Sent Events), as buffer size in such
     *   case will adapt to the size of each returned element (plus its SSE metadata).
     * </p>
     *
     * @param responseMaxChunkSizeBytes the maximum size in bytes for output chunks
     *                                  ({@link org.springframework.core.io.buffer.DataBuffer} objects), or
     *                                  {@code -1} or {@code Integer.MAX_VALUE} if no limit is to be used.
     */
    public void setResponseMaxChunkSizeBytes(final int responseMaxChunkSizeBytes) {
        this.responseMaxChunkSizeBytes = responseMaxChunkSizeBytes;
    }


    /**
     * <p>
     *   Return the maximum size (in bytes) allowed for the chunks
     *   ({@link org.springframework.core.io.buffer.DataBuffer}) that are produced by the Thymeleaf engine and passed
     *   to the server as output.
     * </p>
     * <p>
     *   In Spring WebFlux applications, Thymeleaf has three modes of operation depending on whether a limit
     *   has been set for the output chunk size and/or data-driver context variables have been specified:
     * </p>
     * <ul>
     *   <li><em>FULL</em>, when no limit for max chunk size is established and no data-driver context variable
     *       has been specified. All template output will be generated in memory as a single chunk
     *       (a single {@link org.springframework.core.io.buffer.DataBuffer} object)
     *       and then sent to the server's output channels. In this mode, the Thymeleaf template engine
     *       works <em>unthrottled</em>, which may benefit performance in some scenarios with small templates,
     *       at the cost of a higher memory consumption.</li>
     *   <li><em>CHUNKED</em>, when a limit for max chunk size is established but no data-driver context
     *       variable has been specified. Template output will be generated in chunks of a size equal or less
     *       than the specified limit (in bytes) and then sent to the server's output channels. After each chunk
     *       is emitted, the template engine will stop (thanks to its <em>throttling</em> mechanism), and
     *       wait for the server to request more chunks by means of reactive <em>backpressure</em>. Note
     *       this mechanism works single-threaded. When using this execution mode, the response will be configured
     *       by this {@link ViewResolver} so that each output chunk emitted provokes a <b>flush</b> operation at the
     *       server output channels (so that partial content is sent to the browser/client).</li>
     *   <li><em>DATA-DRIVEN</em>, when a <em>data-driver</em> variable has been specified at the context
     *       (implementing {@link org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable}). This
     *       variable is expected to contain a <em>data stream</em> (usually in the shape of a
     *       {@link org.reactivestreams.Publisher} that Thymeleaf will consume, creating markup output as data
     *       is streamed from this <em>data-driver</em> and letting the output channels throttle
     *       template engine execution by means of back-pressure. When working in this mode, the response will be
     *       configured by this {@link ViewResolver} so that the server output channels are flushed after each
     *       engine execution (which will happen for each <em>buffer</em> (of configurable size) of elements
     *       collected from the <em>data-driver</em> stream. Additionally, if a value has been set for this
     *       {@code responseMaxChunkSizeBytes} property, the emitted
     *       {@link org.springframework.core.io.buffer.DataBuffer} output chunks will never exceed this size,
     *       and therefore more than one chunk could be emitted for each buffer of <em>data-driver</em> elements.</li>
     * </ul>
     * <p>
     *   Also note that the properties set by means of {@link #setFullModeViewNames(String[])} and
     *   {@link #setChunkedModeViewNames(String[])} also influence and fine-tune which templates are
     *   executed in {@code FULL} or {@code CHUNKED} mode (they have no effect on {@code DATA-DRIVEN}).
     * </p>
     * <p>
     *   Also note that the properties set by means of {@link #setFullModeViewNames(String[])} and
     *   {@link #setChunkedModeViewNames(String[])} also influence and fine-tune which templates are
     *   executed in {@code FULL} or {@code CHUNKED} mode (they have no effect on {@code DATA-DRIVEN}).
     * </p>
     * <p>
     *   If this property is set to {@code -1} or {@code Integer.MAX_VALUE}, no size limit will be used. Note also
     *   that there is no limit set by default.
     * </p>
     * <p>
     *   Also note that this parameter will be ignored when returning SSE (Server-Sent Events), as buffer size in such
     *   case will adapt to the size of each returned element (plus its SSE metadata).
     * </p>
     *
     * @return the maximum size in bytes for output chunks
     *         ({@link org.springframework.core.io.buffer.DataBuffer} objects), or
     *         {@code -1} or {@code Integer.MAX_VALUE} if no limit is to be used.
     */
    public int getResponseMaxChunkSizeBytes() {
        return this.responseMaxChunkSizeBytes;
    }




    /**
     * <p>
     *   Specify a set of name patterns that will applied to determine whether a view name
     *   returned by a controller will be resolved by this resolver or not.
     * </p>
     * <p>
     *   In applications configuring several view resolvers &ndash;for example, one for Thymeleaf
     *   and another one for JSP+JSTL legacy pages&ndash;, this property establishes when
     *   a view will be considered to be resolved by this view resolver and when Spring should
     *   simply ask the next resolver in the chain &ndash;according to its {@code order}&ndash;
     *   instead.
     * </p>
     * <p>
     *   The specified view name patterns can be complete view names, but can also use
     *   the {@code *} wildcard: "{@code index.*}", "{@code user_*}", "{@code admin/*}", etc.
     * </p>
     * <p>
     *   Also note that these view name patterns are checked <em>before</em> applying any prefixes
     *   or suffixes to the view name, so they should not include these. Usually therefore, you
     *   would specify {@code orders/*} instead of {@code /WEB-INF/templates/orders/*.html}.
     * </p>
     *
     * @param viewNames the view names (actually view name patterns)
     * @see PatternMatchUtils#simpleMatch(String[], String)
     */
    public void setViewNames(final String[] viewNames) {
        this.viewNames = viewNames;
    }


    /**
     * <p>
     *   Return the set of name patterns that will applied to determine whether a view name
     *   returned by a controller will be resolved by this resolver or not.
     * </p>
     * <p>
     *   In applications configuring several view resolvers &ndash;for example, one for Thymeleaf
     *   and another one for JSP+JSTL legacy pages&ndash;, this property establishes when
     *   a view will be considered to be resolved by this view resolver and when Spring should
     *   simply ask the next resolver in the chain &ndash;according to its {@code order}&ndash;
     *   instead.
     * </p>
     * <p>
     *   The specified view name patterns can be complete view names, but can also use
     *   the {@code *} wildcard: "{@code index.*}", "{@code user_*}", "{@code admin/*}", etc.
     * </p>
     * <p>
     *   Also note that these view name patterns are checked <em>before</em> applying any prefixes
     *   or suffixes to the view name, so they should not include these. Usually therefore, you
     *   would specify {@code orders/*} instead of {@code /WEB-INF/templates/orders/*.html}.
     * </p>
     *
     * @return the view name patterns
     * @see PatternMatchUtils#simpleMatch(String[], String)
     */
    public String[] getViewNames() {
        return this.viewNames;
    }




    /**
     * <p>
     *   Specify names of views &ndash;patterns, in fact&ndash; that cannot
     *   be handled by this view resolver.
     * </p>
     * <p>
     *   These patterns can be specified in the same format as those in
     *   {@link #setViewNames(String[])}, but work as an <em>exclusion list</em>.
     * </p>
     *
     * @param excludedViewNames the view names to be excluded (actually view name patterns)
     * @see #setViewNames(String[])
     * @see PatternMatchUtils#simpleMatch(String[], String)
     */
    public void setExcludedViewNames(final String[] excludedViewNames) {
        this.excludedViewNames = excludedViewNames;
    }


    /**
     * <p>
     *   Returns the names of views &ndash;patterns, in fact&ndash; that cannot
     *   be handled by this view resolver.
     * </p>
     * <p>
     *   These patterns can be specified in the same format as those in
     *   {@link #setViewNames(String[])}, but work as an <em>exclusion list</em>.
     * </p>
     *
     * @return the excluded view name patterns
     * @see #getViewNames()
     * @see PatternMatchUtils#simpleMatch(String[], String)
     */
    public String[] getExcludedViewNames() {
        return this.excludedViewNames;
    }




    /**
     * <p>
     *   Specify a set of name patterns that be will applied to determine whether a view is to be processed
     *   in {@code FULL} mode even if a maximum response chunk size has been defined.
     * </p>
     * <p>
     *   When a response maximum chunk size has been set by means of {@link #setResponseMaxChunkSizeBytes(int)},
     *   this parameter allows the possibility to exclude some views from being applied this maximum size
     *   and therefore be executed in {@code FULL} mode, in just one template engine execution in-memory.
     * </p>
     * <p>
     *   This is useful when a maximum chunk size has been set but some pages are actually small enough to benefit
     *   from the performance gain of executing the template engine <em>unthrottled</em>, even if this means
     *   producing the entire output in memory before sending it to the output channels.
     * </p>
     * <p>
     *   When a response maximum chunk size has not been set by means of {@link #setResponseMaxChunkSizeBytes(int)},
     *   this parameter has no effect at all.
     * </p>
     * <p>
     *   When a response maximum chunk size has been set by means of {@link #setResponseMaxChunkSizeBytes(int)},
     *   but a value has also been set to the {@code chunkedModeViewNames} parameter by means of
     *   {@link #setChunkedModeViewNames(String[])} method, this parameter has no effect at all, as only the views
     *   specified in the latter parameter will be processed in {@code CHUNKED} mode.
     * </p>
     * <p>
     *   Also note that, if a view specified here to be executed as {@code FULL} is executed with a
     *   <em>data-driver</em> variable included in the model, the {@code DATA-DRIVEN} execution mode will be
     *   automatically selected instead, and output chunks will be flushed after each execution of the engine for
     *   each buffer of elements obtained from the <em>data-driver</em> stream.
     * </p>
     * <p>
     *   The specified view name patterns can be complete view names, but can also use
     *   the {@code *} wildcard: "{@code index.*}", "{@code user_*}", "{@code admin/*}", etc.
     * </p>
     * <p>
     *   Also note that these view name patterns are checked <em>before</em> applying any prefixes
     *   or suffixes to the view name, so they should not include these. Usually therefore, you
     *   would specify {@code orders/*} instead of {@code /WEB-INF/templates/orders/*.html}.
     * </p>
     *
     * @param fullModeViewNames the view names (actually view name patterns)
     * @see #setResponseMaxChunkSizeBytes(int)
     * @see #setChunkedModeViewNames(String[])
     * @see PatternMatchUtils#simpleMatch(String[], String)
     *
     * @since 3.0.8
     */
    public void setFullModeViewNames(final String[] fullModeViewNames) {
        this.fullModeViewNames = fullModeViewNames;
    }


    /**
     * <p>
     *   Returns the set of name patterns that will be applied to determine whether a view is to be processed
     *   in {@code FULL} mode even if a maximum response chunk size has been defined.
     * </p>
     * <p>
     *   When a response maximum chunk size has been set by means of {@link #setResponseMaxChunkSizeBytes(int)},
     *   this parameter allows the possibility to exclude some views from being applied this maximum size
     *   and therefore be executed in {@code FULL} mode, in just one template engine execution in-memory.
     * </p>
     * <p>
     *   This is useful when a maximum chunk size has been set but some pages are actually small enough to benefit
     *   from the performance gain of executing the template engine <em>unthrottled</em>, even if this means
     *   producing the entire output in memory before sending it to the output channels.
     * </p>
     * <p>
     *   When a response maximum chunk size has not been set by means of {@link #setResponseMaxChunkSizeBytes(int)},
     *   this parameter has no effect at all.
     * </p>
     * <p>
     *   When a response maximum chunk size has been set by means of {@link #setResponseMaxChunkSizeBytes(int)},
     *   but a value has also been set to the {@code chunkedModeViewNames} parameter by means of
     *   {@link #setChunkedModeViewNames(String[])} method, this parameter has no effect at all, as only the views
     *   specified in the latter parameter will be processed in {@code CHUNKED} mode.
     * </p>
     * <p>
     *   Also note that, if a view specified here to be executed as {@code FULL} is executed with a
     *   <em>data-driver</em> variable included in the model, the {@code DATA-DRIVEN} execution mode will be
     *   automatically selected instead, and output chunks will be flushed after each execution of the engine for
     *   each buffer of elements obtained from the <em>data-driver</em> stream.
     * </p>
     * <p>
     *   The specified view name patterns can be complete view names, but can also use
     *   the {@code *} wildcard: "{@code index.*}", "{@code user_*}", "{@code admin/*}", etc.
     * </p>
     * <p>
     *   Also note that these view name patterns are checked <em>before</em> applying any prefixes
     *   or suffixes to the view name, so they should not include these. Usually therefore, you
     *   would specify {@code orders/*} instead of {@code /WEB-INF/templates/orders/*.html}.
     * </p>
     *
     * @return the view name patterns
     * @see #setResponseMaxChunkSizeBytes(int)
     * @see #setChunkedModeViewNames(String[])
     * @see PatternMatchUtils#simpleMatch(String[], String)
     *
     * @since 3.0.8
     */
    public String[] getFullModeViewNames() {
        return this.fullModeViewNames;
    }




    /**
     * <p>
     *   Specify a set of name patterns that will be applied to determine whether a view is to be processed
     *   in {@code CHUNKED} mode (assuming a maximum response chunk size has been defined).
     * </p>
     * <p>
     *   This parameter only has effect if a maximum response chunk size has been set by means of
     *   {@link #setResponseMaxChunkSizeBytes(int)}. If that is the case, then <strong>only</strong> the views
     *   which name matches the patterns specified here will be executed in {@code CHUNKED} mode using the
     *   maximum output chunk size that has been configured. All other views will be executed in {@code FULL}
     *   mode.
     * </p>
     * <p>
     *   This is useful when a maximum chunk size has been set but it is only needed to apply for certain specific
     *   views, normally the larger templates in output size.
     * </p>
     * <p>
     *   When a response maximum chunk size has not been set by means of {@link #setResponseMaxChunkSizeBytes(int)},
     *   this parameter has no effect at all.
     * </p>
     * <p>
     *   Also note that, if a view specified here to be executed as {@code CHUNKED} is executed with a
     *   <em>data-driver</em> variable included in the model, the {@code DATA-DRIVEN} execution mode will be
     *   automatically selected instead, and output chunks will be flushed after each execution of the engine for
     *   each buffer of elements obtained from the <em>data-driver</em> stream. But in this case, the maximum chunk
     *   size will also apply and, if any of these data-driven chunks exceeds this size, it will be divided into
     *   several output chunks.
     * </p>
     * <p>
     *   The specified view name patterns can be complete view names, but can also use
     *   the {@code *} wildcard: "{@code index.*}", "{@code user_*}", "{@code admin/*}", etc.
     * </p>
     * <p>
     *   Also note that these view name patterns are checked <em>before</em> applying any prefixes
     *   or suffixes to the view name, so they should not include these. Usually therefore, you
     *   would specify {@code orders/*} instead of {@code /WEB-INF/templates/orders/*.html}.
     * </p>
     *
     * @param chunkedModeViewNames the view names (actually view name patterns)
     * @see #setResponseMaxChunkSizeBytes(int)
     * @see #setFullModeViewNames(String[])
     * @see PatternMatchUtils#simpleMatch(String[], String)
     *
     * @since 3.0.8
     */
    public void setChunkedModeViewNames(final String[] chunkedModeViewNames) {
        this.chunkedModeViewNames = chunkedModeViewNames;
    }


    /**
     * <p>
     *   Return the set of name patterns that will be applied to determine whether a view is to be processed
     *   in {@code CHUNKED} mode (assuming a maximum response chunk size has been defined).
     * </p>
     * <p>
     *   This parameter only has effect if a maximum response chunk size has been set by means of
     *   {@link #setResponseMaxChunkSizeBytes(int)}. If that is the case, then <strong>only</strong> the views
     *   which name matches the patterns specified here will be executed in {@code CHUNKED} mode using the
     *   maximum output chunk size that has been configured. All other views will be executed in {@code FULL}
     *   mode.
     * </p>
     * <p>
     *   This is useful when a maximum chunk size has been set but it is only needed to apply for certain specific
     *   views, normally the larger templates in output size.
     * </p>
     * <p>
     *   When a response maximum chunk size has not been set by means of {@link #setResponseMaxChunkSizeBytes(int)},
     *   this parameter has no effect at all.
     * </p>
     * <p>
     *   Also note that, if a view specified here to be executed as {@code CHUNKED} is executed with a
     *   <em>data-driver</em> variable included in the model, the {@code DATA-DRIVEN} execution mode will be
     *   automatically selected instead, and output chunks will be flushed after each execution of the engine for
     *   each buffer of elements obtained from the <em>data-driver</em> stream. But in this case, the maximum chunk
     *   size will also apply and, if any of these data-driven chunks exceeds this size, it will be divided into
     *   several output chunks.
     * </p>
     * <p>
     *   The specified view name patterns can be complete view names, but can also use
     *   the {@code *} wildcard: "{@code index.*}", "{@code user_*}", "{@code admin/*}", etc.
     * </p>
     * <p>
     *   Also note that these view name patterns are checked <em>before</em> applying any prefixes
     *   or suffixes to the view name, so they should not include these. Usually therefore, you
     *   would specify {@code orders/*} instead of {@code /WEB-INF/templates/orders/*.html}.
     * </p>
     *
     * @return the view name patterns
     * @see #setResponseMaxChunkSizeBytes(int)
     * @see #setFullModeViewNames(String[])
     * @see PatternMatchUtils#simpleMatch(String[], String)
     *
     * @since 3.0.8
     */
    public String[] getChunkedModeViewNames() {
        return this.chunkedModeViewNames;
    }

    
    

    protected boolean canHandle(final String viewName, @SuppressWarnings("unused") final Locale locale) {
        final String[] viewNamesToBeProcessed = getViewNames();
        final String[] viewNamesNotToBeProcessed = getExcludedViewNames();
        return ((viewNamesToBeProcessed == null || PatternMatchUtils.simpleMatch(viewNamesToBeProcessed, viewName)) &&
                (viewNamesNotToBeProcessed == null || !PatternMatchUtils.simpleMatch(viewNamesNotToBeProcessed, viewName)));
    }
    

    protected boolean shouldUseChunkedExecution(final String viewName) {

        final int viewResponseMaxChunkSizeBytes = getResponseMaxChunkSizeBytes();
        final String[] viewChunkedModeViewNames = getChunkedModeViewNames();
        final String[] viewFullModeViewNames = getFullModeViewNames();

        if (viewResponseMaxChunkSizeBytes == ThymeleafReactiveView.DEFAULT_RESPONSE_CHUNK_SIZE_BYTES) {
            // No response max chunk size has been set, so no possibility to use CHUNKED execution
            if (viewChunkedModeViewNames != null) {
                vrlogger.warn("[THYMELEAF] A set of view names to be executed in CHUNKED mode has been specified, " +
                        "but no response max chunk size has been specified, so this configuration parameter " +
                        "has no practical effect (no way to configure CHUNKED mode from the ViewResolver). Please " +
                        "fix your configuration.");
            }
            if (viewFullModeViewNames != null) {
                vrlogger.warn("[THYMELEAF] A set of view names to be executed in FULL mode has been specified, " +
                        "but no response max chunk size has been specified, so the former configuration parameter " +
                        "has no practical effect (all templates will actually be executed as FULL). Please " +
                        "fix your configuration.");
            }
            return false;
        }

        if (viewChunkedModeViewNames != null) {
            // A specific set of views to be processed in CHUNKED mode has been specified, so only that
            // set will determine whether CHUNKED should be used or not
            return PatternMatchUtils.simpleMatch(viewChunkedModeViewNames, viewName);
        }

        if (viewFullModeViewNames != null) {
            // A specific set of views to be processed in FULL mode has been specified, so we will not apply
            // CHUNKED if this view matches the names in such set
            return !PatternMatchUtils.simpleMatch(viewFullModeViewNames, viewName);
        }

        return true;

    }




    @Override
    public Mono<View> resolveViewName(final String viewName, final Locale locale) {

        // First possible call to check "viewNames": before processing redirects and forwards
        if (!this.alwaysProcessRedirectAndForward && !canHandle(viewName, locale)) {
            vrlogger.trace("[THYMELEAF] View \"{}\" cannot be handled by ThymeleafReactiveViewResolver. Passing on to the next resolver in the chain.", viewName);
            return Mono.empty();
        }
        // Process redirects (HTTP redirects)
        if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
            vrlogger.trace("[THYMELEAF] View \"{}\" is a redirect, and will not be handled directly by ThymeleafReactiveViewResolver.", viewName);
            final String redirectUrl = viewName.substring(REDIRECT_URL_PREFIX.length());
            final RedirectView view = this.redirectViewProvider.apply(redirectUrl);
            final RedirectView initializedView =
                    (RedirectView) getApplicationContext().getAutowireCapableBeanFactory().initializeBean(view, viewName);
            return Mono.just(initializedView);
        }
        // Process forwards (to JSP resources)
        if (viewName.startsWith(FORWARD_URL_PREFIX)) {
            vrlogger.trace("[THYMELEAF] View \"{}\" is a forward, and will not be handled directly by ThymeleafReactiveViewResolver.", viewName);
            // TODO * No view forwarding in Spring WebFlux yet. See https://jira.spring.io/browse/SPR-14537
            return Mono.error(new UnsupportedOperationException("Forwards are not currently supported by ThymeleafReactiveViewResolver"));
        }
        // Second possible call to check "viewNames": after processing redirects and forwards
        if (this.alwaysProcessRedirectAndForward && !canHandle(viewName, locale)) {
            vrlogger.trace("[THYMELEAF] View \"{}\" cannot be handled by ThymeleafReactiveViewResolver. Passing on to the next resolver in the chain.", viewName);
            return Mono.empty();
        }
        vrlogger.trace("[THYMELEAF] View {} will be handled by ThymeleafReactiveViewResolver and a " +
                "{} instance will be created for it", viewName, getViewClass().getSimpleName());
        return loadView(viewName, locale);

    }







    protected Mono<View> loadView(final String viewName, final Locale locale) {

        final AutowireCapableBeanFactory beanFactory = getApplicationContext().getAutowireCapableBeanFactory();

        final boolean viewBeanExists = beanFactory.containsBean(viewName);
        final Class<?> viewBeanType = viewBeanExists? beanFactory.getType(viewName) : null;

        final ThymeleafReactiveView view;
        if (viewBeanExists && viewBeanType != null && ThymeleafReactiveView.class.isAssignableFrom(viewBeanType)) {
            // AppCtx has a bean with name == viewName, and it is a View bean. So let's use it as a prototype!
            //
            // This can mean two things: if the bean has been defined with scope "prototype", we will just use it.
            // If it hasn't we will create a new instance of the view class and use its properties in order to
            // configure this view instance (so that we don't end up using the same bean from several request threads).
            //
            // Note that, if Java-based configuration is used, using @Scope("prototype") would be the only viable
            // possibility here.

            final BeanDefinition viewBeanDefinition =
                    (beanFactory instanceof ConfigurableListableBeanFactory ?
                            ((ConfigurableListableBeanFactory)beanFactory).getBeanDefinition(viewName) :
                            null);

            if (viewBeanDefinition == null || !viewBeanDefinition.isPrototype()) {
                // No scope="prototype", so we will just apply its properties. This should only happen with XML config.
                final ThymeleafReactiveView viewInstance = BeanUtils.instantiateClass(getViewClass());
                view = (ThymeleafReactiveView) beanFactory.configureBean(viewInstance, viewName);
            } else {
                // This is a prototype bean. Use it as such.
                view = (ThymeleafReactiveView) beanFactory.getBean(viewName);
            }

        } else {

            final ThymeleafReactiveView viewInstance = BeanUtils.instantiateClass(getViewClass());

            if (viewBeanExists && viewBeanType == null) {
                // AppCtx has a bean with name == viewName, but it is an abstract bean. We still can use it as a prototype.

                // The AUTOWIRE_NO mode applies autowiring only through annotations
                beanFactory.autowireBeanProperties(viewInstance, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
                // A bean with this name exists, so we apply its properties
                beanFactory.applyBeanPropertyValues(viewInstance, viewName);
                // Finally, we let Spring do the remaining initializations (incl. proxifying if needed)
                view = (ThymeleafReactiveView) beanFactory.initializeBean(viewInstance, viewName);

            } else {
                // Either AppCtx has no bean with name == viewName, or it is of an incompatible class. No prototyping done.

                // The AUTOWIRE_NO mode applies autowiring only through annotations
                beanFactory.autowireBeanProperties(viewInstance, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
                // Finally, we let Spring do the remaining initializations (incl. proxifying if needed)
                view = (ThymeleafReactiveView) beanFactory.initializeBean(viewInstance, viewName);

            }

        }

        view.setTemplateEngine(getTemplateEngine());
        view.setStaticVariables(getStaticVariables());

        // We give view beans the opportunity to specify the template name to be used
        if (view.getTemplateName() == null) {
            view.setTemplateName(viewName);
        }

        // We set the media types from the view resolver only if no value has already been set at the view def.
        if (!view.isSupportedMediaTypesSet()) {
            view.setSupportedMediaTypes(getSupportedMediaTypes());
        }

        // We set the default charset from the view resolver only if no value has already been set at the view def.
        if (!view.isDefaultCharsetSet()) {
            view.setDefaultCharset(getDefaultCharset());
        }

        // We set the locale from the view resolver only if no value has already been set at the view def.
        if (locale != null && view.getLocale() == null) {
            view.setLocale(locale);
        }

        /*
         * Set the reactive operation-related flags
         */

        // We determine if there is actually a reason for using chunked execution for this specific view name,
        // based on the ViewResolver configuration
        final boolean shouldUseChunkedExecution = shouldUseChunkedExecution(viewName);

        if (shouldUseChunkedExecution && view.getNullableResponseMaxChunkSize() == null) {
            view.setResponseMaxChunkSizeBytes(getResponseMaxChunkSizeBytes());
        }

        return Mono.just(view);

    }

}
