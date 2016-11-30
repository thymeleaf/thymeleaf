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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.reactive.result.view.View;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.reactive.result.view.ViewResolverSupport;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.spring5.SpringWebReactiveTemplateEngine;
import reactor.core.publisher.Mono;


/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public class ThymeleafReactiveViewResolver extends ViewResolverSupport implements ViewResolver {

    
    private static final Logger vrlogger = LoggerFactory.getLogger(ThymeleafReactiveViewResolver.class);
    
    
    public static final String REDIRECT_URL_PREFIX = "redirect:";
    
    public static final String FORWARD_URL_PREFIX = "forward:";

    private boolean redirectContextRelative = true;
    private boolean redirectHttp10Compatible = true;

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


    private ITemplateEngine templateEngine;






    public ThymeleafReactiveViewResolver() {
        super();
    }
    
    


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
    



    public ITemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }

    /**
     * <p>
     *   Set the template engine object (implementation of {@link ITemplateEngine} to be
     *   used for processing templates.
     * </p>
     * <p>
     *   Note that this view resolver allows any implementation of {@link ITemplateEngine} to be used, but
     *   in most scenarios this will be an instance of
     *   {@link SpringWebReactiveTemplateEngine}.
     * </p>
     *
     * @param templateEngine the template engine, usually {@link SpringWebReactiveTemplateEngine}.
     */
    public void setTemplateEngine(final ITemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }
    



    public Map<String,Object> getStaticVariables() {
        return Collections.unmodifiableMap(this.staticVariables);
    }


    public void addStaticVariable(final String name, final Object value) {
        this.staticVariables.put(name, value);
    }


    public void setStaticVariables(final Map<String, ?> variables) {
        if (variables != null) {
            for (final Map.Entry<String, ?> entry : variables.entrySet()) {
                addStaticVariable(entry.getKey(), entry.getValue());
            }
        }
    }




    public void setOrder(final int order) {
        this.order = order;
    }


    public int getOrder() {
        return this.order;
    }




    public void setRedirectContextRelative(final boolean redirectContextRelative) {
        this.redirectContextRelative = redirectContextRelative;
    }

    
    public boolean isRedirectContextRelative() {
        return this.redirectContextRelative;
    }

    
    

    public void setRedirectHttp10Compatible(final boolean redirectHttp10Compatible) {
        this.redirectHttp10Compatible = redirectHttp10Compatible;
    }

    
    public boolean isRedirectHttp10Compatible() {
        return this.redirectHttp10Compatible;
    }
    



    public void setAlwaysProcessRedirectAndForward(final boolean alwaysProcessRedirectAndForward) {
        this.alwaysProcessRedirectAndForward = alwaysProcessRedirectAndForward;
    }


    public boolean getAlwaysProcessRedirectAndForward() {
        return this.alwaysProcessRedirectAndForward;
    }




    public void setResponseMaxChunkSizeBytes(final int responseMaxChunkSizeBytes) {
        this.responseMaxChunkSizeBytes = responseMaxChunkSizeBytes;
    }


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
            return null;
        }
        // Process redirects (HTTP redirects)
        if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
            vrlogger.trace("[THYMELEAF] View \"{}\" is a redirect, and will not be handled directly by ThymeleafReactiveViewResolver.", viewName);
            // TODO * No "RedirectView" implementation in Spring Reactive yet
            throw new UnsupportedOperationException("Redirects are not currently supported by ThymeleafReactiveViewResolver");
        }
        // Process forwards (to JSP resources)
        if (viewName.startsWith(FORWARD_URL_PREFIX)) {
            vrlogger.trace("[THYMELEAF] View \"{}\" is a forward, and will not be handled directly by ThymeleafReactiveViewResolver.", viewName);
            // TODO * No view forwarding in Spring Reactive yet
            throw new UnsupportedOperationException("Forwards are not currently supported by ThymeleafReactiveViewResolver");
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
