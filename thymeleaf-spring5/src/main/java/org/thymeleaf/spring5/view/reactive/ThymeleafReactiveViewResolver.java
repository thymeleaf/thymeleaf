/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.reactive.result.view.RedirectView;
import org.springframework.web.reactive.result.view.View;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.reactive.result.view.ViewResolverSupport;
import org.thymeleaf.spring5.ISpringWebReactiveTemplateEngine;
import org.thymeleaf.util.Validate;
import reactor.core.publisher.Mono;


/**
 * <p>
 *   Implementation of the Spring Web Reactive {@link ViewResolver}
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
 *
 * @see ThymeleafReactiveView
 * @see ISpringWebReactiveTemplateEngine
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public class ThymeleafReactiveViewResolver extends ViewResolverSupport implements ViewResolver {

    
    private static final Logger vrlogger = LoggerFactory.getLogger(ThymeleafReactiveViewResolver.class);


    /**
     * <p>
     *   Prefix to be used in view names (returned by controllers) for specifying an
     *   HTTP redirect.
     * </p>
     * <p>
     *   Value: <tt>redirect:</tt>
     * </p>
     */
    public static final String REDIRECT_URL_PREFIX = "redirect:";

    // TODO * Will this still exist in Spring Web Reactive? See https://jira.spring.io/browse/SPR-14537
    public static final String FORWARD_URL_PREFIX = "forward:";

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


    private ISpringWebReactiveTemplateEngine templateEngine;






    /**
     * <p>
     *   Create an instance of <tt>ThymeleafReactiveViewResolver</tt>.
     * </p>
     */
    public ThymeleafReactiveViewResolver() {
        super();
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
     *   (implementation of {@link ISpringWebReactiveTemplateEngine} to be used for the
     *   execution of templates.
     * </p>
     *
     * @return the template engine being used for processing templates.
     */
    public ISpringWebReactiveTemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }


    /**
     * <p>
     *   Set the template engine object (implementation of {@link ISpringWebReactiveTemplateEngine} to be
     *   used for processing templates.
     * </p>
     *
     * @param templateEngine the template engine.
     */
    public void setTemplateEngine(final ISpringWebReactiveTemplateEngine templateEngine) {
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
     *   <tt>${myStaticVar}</tt>.
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
     *   <tt>${myStaticVar}</tt>.
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
     *   <tt>${myStaticVar}</tt>.
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
     *   and this <tt>order</tt> property established the order in which
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
     *   and this <tt>order</tt> property established the order in which
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
     *   Note the parameter specified to the function will be the <tt>URL</tt> of the redirect
     *   (as specified in the view name returned by the controller, without the <tt>redirect:</tt>
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
     *   Note the parameter specified to the function will be the <tt>URL</tt> of the redirect
     *   (as specified in the view name returned by the controller, without the <tt>redirect:</tt>
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
     *   the <tt>viewNames</tt> property.
     * </p>
     * <p>
     *   When this flag is set to <tt>true</tt> (default value), any view name that starts with the
     *   <tt>redirect:</tt> or <tt>forward:</tt> prefixes will be resolved by this ViewResolver even if the view names
     *   would not match what is established at the <tt>viewNames</tt> property.
     * </p>
     * <p>
     *   Note that the behaviour of <em>resolving</em> view names with these prefixes is exactly the same with this
     *   flag set to <tt>true</tt> or <tt>false</tt> (perform an HTTP redirect or forward to an internal resource).
     *   The only difference is whether the prefixes have to be explicitly specified at <tt>viewNames</tt> or not.
     * </p>
     * <p>
     *   Default value is <tt>true</tt>.
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
     *   the <tt>viewNames</tt> property.
     * </p>
     * <p>
     *   When this flag is set to <tt>true</tt> (default value), any view name that starts with the
     *   <tt>redirect:</tt> or <tt>forward:</tt> prefixes will be resolved by this ViewResolver even if the view names
     *   would not match what is established at the <tt>viewNames</tt> property.
     * </p>
     * <p>
     *   Note that the behaviour of <em>resolving</em> view names with these prefixes is exactly the same with this
     *   flag set to <tt>true</tt> or <tt>false</tt> (perform an HTTP redirect or forward to an internal resource).
     *   The only difference is whether the prefixes have to be explicitly specified at <tt>viewNames</tt> or not.
     * </p>
     * <p>
     *   Default value is <tt>true</tt>.
     * </p>
     *
     * @return whether redirects and forwards will be always processed by this view resolver or else only when they are
     *         matched by the <tt>viewNames</tt> property.
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
     *   In Spring Web Reactive applications, Thymeleaf has three modes of operation depending on whether a limit
     *   has been set for the output chunk size and/or data-driver context variables have been specified:
     * </p>
     * <ul>
     *   <li><em>FULL</em>, when no limit for max chunk size is established and no data-driver context variable
     *       has been specified. All template output will be generated in memory and then sent to the server's
     *       output channels as a single {@link org.springframework.core.io.buffer.DataBuffer}.</li>
     *   <li><em>CHUNKED</em>, when a limit for max chunk size is established but no data-driver context
     *       variable has been specified. Template output will be generated in chunks of a size equal or less
     *       than the specified limit (in bytes) and then sent to the server's output channels. After each chunk
     *       is sent to output, the template engine will stop (thanks to its <em>throttling</em> mechanism), and
     *       wait for the server to request more chunks by means of reactive <em>backpressure</em>. Note all of
     *       this mechanism works single-threaded. This execution mode will also force the server to perform
     *       output flush operations after each chunk is sent from Thymeleaf.</li>
     *   <li><em>DATA-DRIVEN</em>, when a <em>data-driver</em> variable has been specified at the context
     *       (implementing {@link org.thymeleaf.spring5.context.reactive.IReactiveDataDriverContextVariable}). This
     *       variable is expected to contain a <em>data stream</em> (usually in the shape of a
     *       {@link org.reactivestreams.Publisher} that Thymeleaf will consume, creating markup output as data
     *       is streamed from this <em>data-driver</em> and letting the output channels of the server throttle
     *       template engine execution by means of back-pressure. Additionally, depending on whether a value has
     *       been specified for this property or not, Thymeleaf will never generate
     *       {@link org.springframework.core.io.buffer.DataBuffer} output chunks larger than the specified size,
     *       and will request the server to perform an output flush operation after each chunk is produced.</li>
     * </ul>
     * <p>
     *   If this property is set to <tt>-1</tt> or <tt>Integer.MAX_VALUE</tt>, no size limit will be used. Note also
     *   that there is no limit set by default.
     * </p>
     *
     * @param responseMaxChunkSizeBytes the maximum size in bytes for output chunks
     *                                  ({@link org.springframework.core.io.buffer.DataBuffer} objects), or
     *                                  <tt>-1</tt> or <tt>Integer.MAX_VALUE</tt> if no limit is to be used.
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
     *   In Spring Web Reactive applications, Thymeleaf has three modes of operation depending on whether a limit
     *   has been set for the output chunk size and/or data-driver context variables have been specified:
     * </p>
     * <ul>
     *   <li><em>FULL</em>, when no limit for max chunk size is established and no data-driver context variable
     *       has been specified. All template output will be generated in memory and then sent to the server's
     *       output channels as a single {@link org.springframework.core.io.buffer.DataBuffer}.</li>
     *   <li><em>CHUNKED</em>, when a limit for max chunk size is established but no data-driver context
     *       variable has been specified. Template output will be generated in chunks of a size equal or less
     *       than the specified limit (in bytes) and then sent to the server's output channels. After each chunk
     *       is sent to output, the template engine will stop (thanks to its <em>throttling</em> mechanism), and
     *       wait for the server to request more chunks by means of reactive <em>backpressure</em>. Note all of
     *       this mechanism works single-threaded. This execution mode will also force the server to perform
     *       output flush operations after each chunk is sent from Thymeleaf.</li>
     *   <li><em>DATA-DRIVEN</em>, when a <em>data-driver</em> variable has been specified at the context
     *       (implementing {@link org.thymeleaf.spring5.context.reactive.IReactiveDataDriverContextVariable}). This
     *       variable is expected to contain a <em>data stream</em> (usually in the shape of a
     *       {@link org.reactivestreams.Publisher} that Thymeleaf will consume, creating markup output as data
     *       is streamed from this <em>data-driver</em> and letting the output channels of the server throttle
     *       template engine execution by means of back-pressure. Additionally, depending on whether a value has
     *       been specified for this property or not, Thymeleaf will never generate
     *       {@link org.springframework.core.io.buffer.DataBuffer} output chunks larger than the specified size,
     *       and will request the server to perform an output flush operation after each chunk is produced.</li>
     * </ul>
     * <p>
     *   If this property is set to <tt>-1</tt> or <tt>Integer.MAX_VALUE</tt>, no size limit will be used. Note also
     *   that there is no limit set by default.
     * </p>
     *
     * @return the maximum size in bytes for output chunks
     *         ({@link org.springframework.core.io.buffer.DataBuffer} objects), or
     *         <tt>-1</tt> or <tt>Integer.MAX_VALUE</tt> if no limit is to be used.
     */
    public int getResponseMaxChunkSizeBytes() {
        return this.responseMaxChunkSizeBytes;
    }




    public void setViewNames(final String[] viewNames) {
        this.viewNames = viewNames;
    }


    public String[] getViewNames() {
        return this.viewNames;
    }
    
    

    
    public void setExcludedViewNames(final String[] excludedViewNames) {
        this.excludedViewNames = excludedViewNames;
    }


    public String[] getExcludedViewNames() {
        return this.excludedViewNames;
    }
    
    
    

    protected boolean canHandle(final String viewName, @SuppressWarnings("unused") final Locale locale) {
        final String[] viewNamesToBeProcessed = getViewNames();
        final String[] viewNamesNotToBeProcessed = getExcludedViewNames();
        return ((viewNamesToBeProcessed == null || PatternMatchUtils.simpleMatch(viewNamesToBeProcessed, viewName)) &&
                (viewNamesNotToBeProcessed == null || !PatternMatchUtils.simpleMatch(viewNamesNotToBeProcessed, viewName)));
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
            // TODO * No view forwarding in Spring Reactive yet. See https://jira.spring.io/browse/SPR-14537
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
        if (getResponseMaxChunkSizeBytes() != ThymeleafReactiveView.DEFAULT_RESPONSE_CHUNK_SIZE_BYTES && view.getNullableResponseMaxChunkSize() == null) {
            view.setResponseMaxChunkSizeBytes(getResponseMaxChunkSizeBytes());
        }

        return Mono.just(view);

    }



}
