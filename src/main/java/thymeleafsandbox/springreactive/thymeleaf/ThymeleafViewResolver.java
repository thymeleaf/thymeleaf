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
package thymeleafsandbox.springreactive.thymeleaf;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.http.MediaType;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.reactive.result.view.View;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.reactive.result.view.ViewResolverSupport;
import org.thymeleaf.ITemplateEngine;
import reactor.core.publisher.Mono;


/**
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public class ThymeleafViewResolver extends ViewResolverSupport implements ViewResolver {

    
    private static final Logger vrlogger = LoggerFactory.getLogger(ThymeleafViewResolver.class);
    
    
    public static final String REDIRECT_URL_PREFIX = "redirect:";
    
    public static final String FORWARD_URL_PREFIX = "forward:";

    private boolean redirectContextRelative = true;
    private boolean redirectHttp10Compatible = true;

    private boolean alwaysProcessRedirectAndForward = true;

    private Class<? extends ThymeleafView> viewClass = ThymeleafView.class;
    private String[] viewNames = null;
    private String[] excludedViewNames = null;
    private int order = Integer.MAX_VALUE;

    private String dataDrivenVariableName = null;
    private int dataDrivenChunkSizeElements = ThymeleafView.DEFAULT_DATA_DRIVEN_CHUNK_SIZE_ELEMENTS;

    private final Map<String, Object> staticVariables = new LinkedHashMap<String, Object>(10);
    private String contentType = null;
    private String characterEncoding = null;


    // This will determine whether we will be throttling or not, and if so the size of the buffers that will be produced
    // by the throttled engine each time the back-pressure mechanism asks for a new "unit" (a new DataBuffer)
    //
    // The value established here will be a default value, which can be overridden by specific views at the
    // ThymeleafView class
    private int responseMaxBufferSizeBytes = ThymeleafView.DEFAULT_RESPONSE_BUFFER_SIZE_BYTES;
    
    private ITemplateEngine templateEngine;






    public ThymeleafViewResolver() {
        super();
    }
    
    


    public void setViewClass(final Class<? extends ThymeleafView> viewClass) {
        if (viewClass == null || !ThymeleafView.class.isAssignableFrom(viewClass)) {
            throw new IllegalArgumentException(
                    "Given view class [" + (viewClass != null ? viewClass.getName() : null) +
                    "] is not of type [" + ThymeleafView.class.getName() + "]");
        }
        this.viewClass = viewClass;
    }
    
    
    protected Class<? extends ThymeleafView> getViewClass() {
        return this.viewClass;
    }
    



    public ITemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }


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


    

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }


    public String getContentType() {
        return this.contentType;
    }
    


    
    public void setCharacterEncoding(final String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }


    public String getCharacterEncoding() {
        return this.characterEncoding;
    }




    public String getDataDrivenVariableName() {
        return dataDrivenVariableName;
    }


    public void setDataDrivenVariableName(final String dataDrivenVariableName) {
        this.dataDrivenVariableName = dataDrivenVariableName;
    }




    // Default is DEFAULT_DATA_DRIVEN_CHUNK_SIZE_ELEMENTS
    public int getDataDrivenChunkSizeElements() {
        return this.dataDrivenChunkSizeElements;
    }


    public void setDataDrivenChunkSizeElements(final int dataDrivenChunkSizeElements) {
        this.dataDrivenChunkSizeElements = dataDrivenChunkSizeElements;
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




    // Default is Integer.MAX_VALUE, which means we will write the whole response in a single buffer
    public void setResponseMaxBufferSizeBytes(final int responseMaxBufferSizeBytes) {
        this.responseMaxBufferSizeBytes = responseMaxBufferSizeBytes;
    }


    public int getResponseMaxBufferSizeBytes() {
        return this.responseMaxBufferSizeBytes;
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
            vrlogger.trace("[THYMELEAF] View \"{}\" cannot be handled by ThymeleafViewResolver. Passing on to the next resolver in the chain.", viewName);
            return null;
        }
        // Process redirects (HTTP redirects)
        if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
            vrlogger.trace("[THYMELEAF] View \"{}\" is a redirect, and will not be handled directly by ThymeleafViewResolver.", viewName);
            // TODO * No "ReactiveView" implementation in Spring Reactive yet
            throw new UnsupportedOperationException("Redirects are not currently supported by ThymeleafViewResolver");
        }
        // Process forwards (to JSP resources)
        if (viewName.startsWith(FORWARD_URL_PREFIX)) {
            vrlogger.trace("[THYMELEAF] View \"{}\" is a forward, and will not be handled directly by ThymeleafViewResolver.", viewName);
            // TODO * No view forwarding in Spring Reactive yet
            throw new UnsupportedOperationException("Forwards are not currently supported by ThymeleafViewResolver");
        }
        // Second possible call to check "viewNames": after processing redirects and forwards
        if (this.alwaysProcessRedirectAndForward && !canHandle(viewName, locale)) {
            vrlogger.trace("[THYMELEAF] View \"{}\" cannot be handled by ThymeleafViewResolver. Passing on to the next resolver in the chain.", viewName);
            return Mono.empty();
        }
        vrlogger.trace("[THYMELEAF] View {} will be handled by ThymeleafViewResolver and a " +
                "{} instance will be created for it", viewName, this.viewClass.getSimpleName());
        return loadView(viewName, locale);

    }







    protected Mono<View> loadView(final String viewName, final Locale locale) {

        final AutowireCapableBeanFactory beanFactory = getApplicationContext().getAutowireCapableBeanFactory();

        ThymeleafView view = BeanUtils.instantiateClass(getViewClass());

        final boolean viewBeanExists = beanFactory.containsBean(viewName);
        final Class<?> viewBeanType = viewBeanExists? beanFactory.getType(viewName) : null;

        if (viewBeanExists && viewBeanType != null && ThymeleafView.class.isAssignableFrom(viewBeanType)) {
            // AppCtx has a bean with name == viewName, and it is a View bean. So let's use it as a prototype!

            view = (ThymeleafView) beanFactory.configureBean(view, viewName);

        } else if (viewBeanExists && viewBeanType == null) {
            // AppCtx has a bean with name == viewName, but it is an abstract bean. We still can use it as a prototype.

            // The AUTOWIRE_NO mode applies autowiring only through annotations
            beanFactory.autowireBeanProperties(view, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
            // A bean with this name exists, so we apply its properties
            beanFactory.applyBeanPropertyValues(view, viewName);
            // Finally, we let Spring do the remaining initializations (incl. proxifying if needed)
            view = (ThymeleafView) beanFactory.initializeBean(view, viewName);

        } else {
            // Either AppCtx has no bean with name == viewName, or it is of an incompatible class. No prototyping done.

            // The AUTOWIRE_NO mode applies autowiring only through annotations
            beanFactory.autowireBeanProperties(view, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
            // Finally, we let Spring do the remaining initializations (incl. proxifying if needed)
            view = (ThymeleafView) beanFactory.initializeBean(view, viewName);

        }


        view.setTemplateEngine(getTemplateEngine());
        view.setStaticVariables(getStaticVariables());

        // We give view beans the opportunity to specify the template name to be used
        if (view.getTemplateName() == null) {
            view.setTemplateName(viewName);
        }

        if (getContentType() != null && !view.isContentTypeSet()) {
            view.setContentType(getContentType());
        }
        if (locale != null && view.getLocale() == null) {
            view.setLocale(locale);
        }
        if (getCharacterEncoding() != null && view.getCharacterEncoding() == null) {
            view.setCharacterEncoding(getCharacterEncoding());
        }
        if (!viewMediaTypesAreDefaultOrEmpty(getSupportedMediaTypes()) && viewMediaTypesAreDefaultOrEmpty(view.getSupportedMediaTypes())) {
            view.setSupportedMediaTypes(getSupportedMediaTypes());
        }

        // Once all content-type-related info has been set, compute the definitive supported media types to be used
        // for content negotiation at the view level. Note it is important that this is performed now, before
        // the "render" methods are called on the view itself by the framework (by then, all content negotiation
        // would be already done).
        view.initializeMediaTypes();


        if (getResponseMaxBufferSizeBytes() != ThymeleafView.DEFAULT_RESPONSE_BUFFER_SIZE_BYTES && view.getNullableResponseMaxChunkSize() == null) {
            view.setResponseMaxBufferSizeBytes(getResponseMaxBufferSizeBytes());
        }
        if (getDataDrivenVariableName() != null && view.getDataDrivenVariableName() == null) {
            view.setDataDrivenVariableName(getDataDrivenVariableName());
        }
        if (getDataDrivenChunkSizeElements() != ThymeleafView.DEFAULT_DATA_DRIVEN_CHUNK_SIZE_ELEMENTS && view.getNullableDataDrivenBufferSize() == null) {
            view.setDataDrivenChunkSizeElements(getDataDrivenChunkSizeElements());
        }

        return Mono.just(view);

    }



    private static boolean viewMediaTypesAreDefaultOrEmpty(final List<MediaType> mediaTypes) {
        if (mediaTypes == null || mediaTypes.size() == 0) {
            return true;
        }
        if (mediaTypes.size() > 1) {
            return false;
        }
        final MediaType firstMediaType = mediaTypes.get(0);
        return firstMediaType == null || firstMediaType.equals(ViewResolverSupport.DEFAULT_CONTENT_TYPE);
    }


}
