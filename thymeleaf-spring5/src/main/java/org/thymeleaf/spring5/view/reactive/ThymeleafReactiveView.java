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

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.result.view.AbstractView;
import org.springframework.web.reactive.result.view.RequestContext;
import org.springframework.web.server.ServerWebExchange;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring5.ISpringWebFluxTemplateEngine;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxExpressionContext;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxThymeleafRequestContext;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * <p>
 *   Base implementation of the Spring WebFlux {@link org.springframework.web.reactive.result.view.View}
 *   interface.
 * </p>
 * <p>
 *   Views represent a template being executed, after being resolved (and
 *   instantiated) by a {@link org.springframework.web.reactive.result.view.ViewResolver}.
 * </p>
 * <p>
 *   This is the default view implementation resolved by {@link ThymeleafReactiveViewResolver}.
 * </p>
 * <p>
 *   This view needs a {@link ISpringWebFluxTemplateEngine} for execution, and it will call its
 *   {@link ISpringWebFluxTemplateEngine#processStream(String, Set, IContext, DataBufferFactory, MediaType, Charset, int)}
 *   method to create the reactive data streams to be used for processing the template. See the documentation
 *   of this class to know more about the different operation modes available.
 * </p>
 *
 * @see ThymeleafReactiveViewResolver
 * @see ISpringWebFluxTemplateEngine
 * @see ReactiveDataDriverContextVariable
 * @see IReactiveDataDriverContextVariable
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public class ThymeleafReactiveView extends AbstractView implements BeanNameAware {


    protected static final Logger logger = LoggerFactory.getLogger(ThymeleafReactiveView.class);

    /**
     * By default, no max response chunk size is set. Value = {@link Integer#MAX_VALUE}
     */
    public static final int DEFAULT_RESPONSE_CHUNK_SIZE_BYTES = Integer.MAX_VALUE;


    /**
     * <p>
     *   This prefix should be used in order to allow dialects to provide reactive stream objects
     *   that should be resolved (in an unblocked manner) just before the execution of the view. The idea is to allow
     *   these streams to be included in the standard reactive Spring view model resolution mechanisms so that Thymeleaf
     *   does not have to block during the execution of the view in order to obtain the value. The result will be as
     *   if reactive stream objects had been added by the controller methods.
     * </p>
     * <p>
     *   The name of the attributes being added to the Model will be the name of the execution attribute minus the
     *   prefix. So {@code ThymeleafReactiveModelAdditions:somedata} will result in a Model attribute called
     *   {@code somedata}.
     * </p>
     * <p>
     *   Values of these execution attributes are allowed to be:
     * </p>
     * <ul>
     *     <li>{@code Publisher<?>} (including {@code Flux<?>} and {@code Mono<?>}).</li>
     *     <li>{@code Supplier<? extends Publisher<?>>}: The supplier will be called at {@code View}
     *          rendering time and the result will be added to the Model.</li>
     *     <li>{@code Function<ServerWebExchange,? extends Publisher<?>>}: The function will be called
     *          at {@code View} rendering time and the result will be added to the Model.</li>
     * </ul>
     * <p>
     *     Value: {@code "ThymeleafReactiveModelAdditions:"}
     * </p>
     *
     * @since 3.0.10
     */
    public static final String REACTIVE_MODEL_ADDITIONS_EXECUTION_ATTRIBUTE_PREFIX = "ThymeleafReactiveModelAdditions:";

    private static final String WEBFLUX_CONVERSION_SERVICE_NAME = "webFluxConversionService";


    private String beanName = null;
    private ISpringWebFluxTemplateEngine templateEngine = null;
	private String templateName = null;
    private Locale locale = null;
    private Map<String, Object> staticVariables = null;

    // These two flags are meant to determine if these fields have been specifically set a
    // value for this View object, so that we know that the ViewResolver should not be
    // overriding them with its own view-resolution-wide values.
    private boolean defaultCharsetSet = false;
    private boolean supportedMediaTypesSet = false;

    private Set<String> markupSelectors = null;



    // This will determine whether we will be throttling or not, and if so the maximum size of the chunks that will be
    // produced by the throttled engine each time the back-pressure mechanism asks for a new "unit" (a new DataBuffer)
    //
    // The value established here is nullable (and null by default) because it will work as an override of the
    // value established at the ThymeleafReactiveViewResolver for the same purpose.
    private Integer responseMaxChunkSizeBytes = null;






	public ThymeleafReactiveView() {
	    super();
	}



    public String getMarkupSelector() {
        return (this.markupSelectors == null || this.markupSelectors.size() == 0? null : this.markupSelectors.iterator().next());
    }


    public void setMarkupSelector(final String markupSelector) {
        this.markupSelectors =
                (markupSelector == null || markupSelector.trim().length() == 0? null : Collections.singleton(markupSelector.trim()));
    }



    // This flag is used from the ViewResolver in order to determine if it has to push its own
    // configuration to the View (which it will do until the View has been specifically configured).
    boolean isDefaultCharsetSet() {
        return this.defaultCharsetSet;
    }


    // Implemented at AbstractView, but overridden here in order to set the flag
    @Override
    public void setDefaultCharset(final Charset defaultCharset) {
        super.setDefaultCharset(defaultCharset);
        this.defaultCharsetSet = true;
    }




    // This flag is used from the ViewResolver in order to determine if it has to push its own
    // configuration to the View (which it will do until the View has been specifically configured).
    boolean isSupportedMediaTypesSet() {
        return this.supportedMediaTypesSet;
    }


    // Implemented at AbstractView, but overridden here in order to set the flag
    @Override
    public void setSupportedMediaTypes(final List<MediaType> supportedMediaTypes) {
        super.setSupportedMediaTypes(supportedMediaTypes);
        this.supportedMediaTypesSet = true;
    }




    public String getBeanName() {
        return this.beanName;
    }

    
    public void setBeanName(final String beanName) {
        this.beanName = beanName;
    }

    


    public String getTemplateName() {
        return this.templateName;
    }
	
    
	public void setTemplateName(final String templateName) {
		this.templateName = templateName;
	}

	


    protected Locale getLocale() {
        return this.locale;
    }

    
    protected void setLocale(final Locale locale) {
        this.locale = locale;
        
    }




    // Default is Integer.MAX_VALUE, which means no explicit limit (note there can still be a limit in
    // the size of the chunks if execution is data driven, as output will be sent to the server after
    // the processing of each data-driver buffer).
    public int getResponseMaxChunkSizeBytes() {
        return this.responseMaxChunkSizeBytes == null?
                DEFAULT_RESPONSE_CHUNK_SIZE_BYTES : this.responseMaxChunkSizeBytes.intValue();
    }


    // We need this one at the ViewResolver to determine if a value has been set at all
    Integer getNullableResponseMaxChunkSize() {
        return this.responseMaxChunkSizeBytes;
    }


    public void setResponseMaxChunkSizeBytes(final int responseMaxBufferSizeBytes) {
        this.responseMaxChunkSizeBytes = Integer.valueOf(responseMaxBufferSizeBytes);
    }




    protected ISpringWebFluxTemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }

    
    protected void setTemplateEngine(final ISpringWebFluxTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }


    

    public Map<String,Object> getStaticVariables() {
        if (this.staticVariables == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(this.staticVariables);
    }


    public void addStaticVariable(final String name, final Object value) {
        if (this.staticVariables == null) {
            this.staticVariables = new HashMap<String, Object>(3, 1.0f);
        }
        this.staticVariables.put(name, value);
    }


    public void setStaticVariables(final Map<String, ?> variables) {
        if (variables != null) {
            if (this.staticVariables == null) {
                this.staticVariables = new HashMap<String, Object>(3, 1.0f);
            }
            this.staticVariables.putAll(variables);
        }
    }




    @Override
    public Mono<Void> render(final Map<String, ?> model, final MediaType contentType, final ServerWebExchange exchange) {
	    // We will prepare the model for rendering by checking if the configured dialects have specified any execution
        // attributes to be added to the model during preparation (e.g. reactive streams that will need to be previously
        // resolved)

        final ISpringWebFluxTemplateEngine viewTemplateEngine = getTemplateEngine();

        if (viewTemplateEngine == null) {
            return Mono.error(new IllegalArgumentException("Property 'thymeleafTemplateEngine' is required"));
        }

        final IEngineConfiguration configuration = viewTemplateEngine.getConfiguration();
        final Map<String,Object> executionAttributes = configuration.getExecutionAttributes();

        // Process the execution attributes and look for possible reactive objects that should be added for resolution

        Map<String,Object> enrichedModel = null;
        for (final String executionAttributeName : executionAttributes.keySet()) {

            if (executionAttributeName != null && executionAttributeName.startsWith(REACTIVE_MODEL_ADDITIONS_EXECUTION_ATTRIBUTE_PREFIX)) {
                // This execution attribute defines a reactive stream object that should be added to the model for
                // non-blocking resolution at view rendering time

                final Object executionAttributeValue = executionAttributes.get(executionAttributeName);
                final String modelAttributeName =
                        executionAttributeName.substring(REACTIVE_MODEL_ADDITIONS_EXECUTION_ATTRIBUTE_PREFIX.length());
                Publisher<?> modelAttributeValue = null;

                if (executionAttributeValue != null) {
                    if (executionAttributeValue instanceof Publisher<?>) {
                        modelAttributeValue = (Publisher<?>) executionAttributeValue;
                    } else if (executionAttributeValue instanceof Supplier<?>){
                        final Supplier<Publisher<?>> supplier = (Supplier<Publisher<?>>) executionAttributeValue;
                        modelAttributeValue = supplier.get();
                    } else if (executionAttributeValue instanceof Function<?,?>) {
                        final Function<ServerWebExchange, Publisher<?>> function = (Function<ServerWebExchange, Publisher<?>>) executionAttributeValue;
                        modelAttributeValue = function.apply(exchange);
                    }
                }

                if (enrichedModel == null) {
                    enrichedModel = new LinkedHashMap<>(model);
                }
                enrichedModel.put(modelAttributeName, modelAttributeValue);

            }

        }

        enrichedModel = (enrichedModel != null ? enrichedModel : (Map<String,Object>)model);

        return super.render(enrichedModel, contentType, exchange);

    }



    @Override
    protected Mono<Void> renderInternal(
            final Map<String, Object> renderAttributes, final MediaType contentType, final ServerWebExchange exchange) {
        return renderFragmentInternal(this.markupSelectors, renderAttributes, contentType, exchange);
    }


    protected Mono<Void> renderFragmentInternal(
            final Set<String> markupSelectorsToRender, final Map<String, Object> renderAttributes,
            final MediaType contentType, final ServerWebExchange exchange) {

        final String viewTemplateName = getTemplateName();
        final ISpringWebFluxTemplateEngine viewTemplateEngine = getTemplateEngine();

        if (viewTemplateName == null) {
            return Mono.error(new IllegalArgumentException("Property 'templateName' is required"));
        }
        if (getLocale() == null) {
            return Mono.error(new IllegalArgumentException("Property 'locale' is required"));
        }
        if (viewTemplateEngine == null) {
            return Mono.error(new IllegalArgumentException("Property 'thymeleafTemplateEngine' is required"));
        }

        final ServerHttpResponse response = exchange.getResponse();

        /*
         * ----------------------------------------------------------------------------------------------------------
         * GATHERING OF THE MERGED MODEL
         * ----------------------------------------------------------------------------------------------------------
         * - The merged model is the map that will be used for initialising the Thymelef IContext. This context will
         *   contain all the data accessible by the template during its execution.
         * - The base of the merged model is the ModelMap created by the Controller, but there are some additional
         *   things
         * ----------------------------------------------------------------------------------------------------------
         */

        final Map<String, Object> mergedModel = new HashMap<>(30);
        // First of all, set all the static variables into the mergedModel
        final Map<String, Object> templateStaticVariables = getStaticVariables();
        if (templateStaticVariables != null) {
            mergedModel.putAll(templateStaticVariables);
        }
        // Add path variables to merged model (if there are any)
        final Map<String, Object> pathVars =
                (Map<String, Object>) exchange.getAttributes().get(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVars != null) {
            mergedModel.putAll(pathVars);
        }
        // Simply dump all the renderAttributes (model coming from the controller) into the merged model
        if (renderAttributes != null) {
            mergedModel.putAll(renderAttributes);
        }

        final ApplicationContext applicationContext = getApplicationContext();

        // Initialize RequestContext (reactive version) and add it to the model as another attribute,
        // so that it can be retrieved from elsewhere.
        final RequestContext requestContext = createRequestContext(exchange, mergedModel);
        final SpringWebFluxThymeleafRequestContext thymeleafRequestContext =
                new SpringWebFluxThymeleafRequestContext(requestContext, exchange);

        mergedModel.put(SpringContextVariableNames.SPRING_REQUEST_CONTEXT, requestContext);
        // Add the Thymeleaf RequestContext wrapper that we will be using in this dialect (the bare RequestContext
        // stays in the context to for compatibility with other dialects)
        mergedModel.put(SpringContextVariableNames.THYMELEAF_REQUEST_CONTEXT, thymeleafRequestContext);


        // Expose Thymeleaf's own evaluation context as a model variable
        //
        // Note Spring's EvaluationContexts are NOT THREAD-SAFE (in exchange for SpelExpressions being thread-safe).
        // That's why we need to create a new EvaluationContext for each request / template execution, even if it is
        // quite expensive to create because of requiring the initialization of several ConcurrentHashMaps.
        final ConversionService conversionService =
                applicationContext.containsBean(WEBFLUX_CONVERSION_SERVICE_NAME)?
                        (ConversionService)applicationContext.getBean(WEBFLUX_CONVERSION_SERVICE_NAME): null;
        final ThymeleafEvaluationContext evaluationContext =
                new ThymeleafEvaluationContext(applicationContext, conversionService);
        mergedModel.put(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);


        // Determine if we have a data-driver variable, and therefore will need to configure flushing of output chunks
        final boolean dataDriven = isDataDriven(mergedModel);


        /*
         * ----------------------------------------------------------------------------------------------------------
         * INSTANTIATION OF THE CONTEXT
         * ----------------------------------------------------------------------------------------------------------
         * - Once the model has been merged, we can create the Thymeleaf context object itself.
         * - The reason it is an ExpressionContext and not a Context is that before executing the template itself,
         *   we might need to use it for computing the markup selectors (if "template :: selector" was specified).
         * - The reason it is not a WebExpressionContext is that this class is linked to the Servlet API, which
         *   might not be present in a Spring WebFlux environment.
         * ----------------------------------------------------------------------------------------------------------
         */

        final IEngineConfiguration configuration = viewTemplateEngine.getConfiguration();
        final SpringWebFluxExpressionContext context =
                new SpringWebFluxExpressionContext(
                        configuration, exchange, getReactiveAdapterRegistry(), getLocale(), mergedModel);


        /*
         * ----------------------------------------------------------------------------------------------------------
         * COMPUTATION OF (OPTIONAL) MARKUP SELECTORS
         * ----------------------------------------------------------------------------------------------------------
         * - If view name has been specified with a template selector (in order to execute only a fragment of
         *   the template) like "template :: selector", we will extract it and compute it.
         * ----------------------------------------------------------------------------------------------------------
         */

        final String templateName;
        final Set<String> markupSelectors;
        if (!viewTemplateName.contains("::")) {
            // No fragment specified at the template name

            templateName = viewTemplateName;
            markupSelectors = null;

        } else {
            // Template name contains a fragment name, so we should parse it as such

            final IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);

            final FragmentExpression fragmentExpression;
            try {
                // By parsing it as a standard expression, we might profit from the expression cache
                fragmentExpression = (FragmentExpression) parser.parseExpression(context, "~{" + viewTemplateName + "}");
            } catch (final TemplateProcessingException e) {
                return Mono.error(
                        new IllegalArgumentException("Invalid template name specification: '" + viewTemplateName + "'"));
            }

            final FragmentExpression.ExecutedFragmentExpression fragment =
                    FragmentExpression.createExecutedFragmentExpression(context, fragmentExpression);

            templateName = FragmentExpression.resolveTemplateName(fragment);
            markupSelectors = FragmentExpression.resolveFragments(fragment);
            final Map<String,Object> nameFragmentParameters = fragment.getFragmentParameters();

            if (nameFragmentParameters != null) {

                if (fragment.hasSyntheticParameters()) {
                    // We cannot allow synthetic parameters because there is no way to specify them at the template
                    // engine execution!
                    return Mono.error(new IllegalArgumentException(
                            "Parameters in a view specification must be named (non-synthetic): '" + viewTemplateName + "'"));
                }

                context.setVariables(nameFragmentParameters);

            }

        }

        final Set<String> processMarkupSelectors;
        if (markupSelectors != null && markupSelectors.size() > 0) {
            if (markupSelectorsToRender != null && markupSelectorsToRender.size() > 0) {
                return Mono.error(new IllegalArgumentException(
                        "A markup selector has been specified (" + Arrays.asList(markupSelectors) + ") for a view " +
                        "that was already being executed as a fragment (" + Arrays.asList(markupSelectorsToRender) + "). " +
                        "Only one fragment selection is allowed."));
            }
            processMarkupSelectors = markupSelectors;
        } else {
            if (markupSelectorsToRender != null && markupSelectorsToRender.size() > 0) {
                processMarkupSelectors = markupSelectorsToRender;
            } else {
                processMarkupSelectors = null;
            }
        }


        /*
         * ----------------------------------------------------------------------------------------------------------
         * COMPUTATION OF TEMPLATE PROCESSING PARAMETERS AND HTTP HEADERS
         * ----------------------------------------------------------------------------------------------------------
         * - At this point we will compute the final values of the different parameters needed for processing the
         *   template (locale, encoding, buffer sizes, etc.)
         * ----------------------------------------------------------------------------------------------------------
         */

        final int templateResponseMaxChunkSizeBytes = getResponseMaxChunkSizeBytes();

        final HttpHeaders responseHeaders = exchange.getResponse().getHeaders();
        final Locale templateLocale = getLocale();
        if (templateLocale != null) {
            responseHeaders.setContentLanguage(templateLocale);
        }

        // Get the charset from the selected content type (or use default)
        final Charset charset = getCharset(contentType).orElse(getDefaultCharset());


        /*
         * -----------------------------------------------------------------------------------------------------------
         * SET (AND RETURN) THE TEMPLATE PROCESSING Flux<DataBuffer> OBJECTS
         * -----------------------------------------------------------------------------------------------------------
         * - There are three possible processing modes, for each of which a Publisher<DataBuffer> will be created in a
         *   different way:
         *
         *     1. FULL: Output chunks not limited in size (templateResponseMaxChunkSizeBytes == Integer.MAX_VALUE) and
         *        no data-driven execution (no context variable of type Publisher<X> driving the template engine
         *        execution): In this case Thymeleaf will be executed unthrottled, in full mode, writing output
         *        to a single DataBuffer chunk instanced before execution, and which will be passed to the output
         *        channels in a single onNext(buffer) call (immediately followed by onComplete()).
         *
         *     2. CHUNKED: Output chunks limited in size (responseMaxChunkSizeBytes) but no data-driven
         *        execution (no Publisher<X> driving engine execution). All model attributes are expected to be
         *        fully resolved (in a non-blocking fashion) by WebFlux before engine execution and the Thymeleaf
         *        engine will execute in throttled mode, performing a full-stop each time the chunk reaches the
         *        specified size, sending it to the output channels with onNext(chunk) and then waiting until
         *        these output channels make the engine resume its work with a new request(n) call. This
         *        execution mode will request an output flush from the server after producing each chunk.
         *
         *     3. DATA-DRIVEN: one of the model attributes is a Publisher<X> wrapped inside an implementation
         *        of the IReactiveDataDriverContextVariable<?> interface. In this case, the Thymeleaf engine will
         *        execute as a response to onNext(List<X>) events triggered by this Publisher. The
         *        "bufferSizeElements" specified at the model attribute will define the amount of elements
         *        produced by this Publisher that will be buffered into a List<X> before triggering the template
         *        engine each time (which is why Thymeleaf will react on onNext(List<X>) and not onNext(X)). Thymeleaf
         *        will expect to find a "th:each" iteration on the data-driven variable inside the processed template,
         *        and will be executed in throttled mode for the published elements, sending the resulting DataBuffer
         *        output chunks to the output channels via onNext(chunk) and stopping until a new onNext(List<X>)
         *        event is triggered. When execution is data-driven, a limit in size can be optionally specified for
         *        the output chunks (responseMaxChunkSizeBytes) which will make Thymeleaf never send
         *        to the output channels a chunk bigger than that (thus splitting the output generated for a List<X>
         *        of published elements into several chunks if required). When executing in DATA-DRIVEN mode,
         *        Thymeleaf will always request flushing of the output channels after producing each chunk.
         * ----------------------------------------------------------------------------------------------------------
         */


        final Publisher<DataBuffer> stream =
                viewTemplateEngine.processStream(
                        templateName, processMarkupSelectors, context, response.bufferFactory(), contentType, charset,
                        templateResponseMaxChunkSizeBytes); // FULL/DATADRIVEN if MAX_VALUE, CHUNKED/DATADRIVEN if other

        if (templateResponseMaxChunkSizeBytes == Integer.MAX_VALUE && !dataDriven) {

            // No size limit for output chunks has been set (FULL mode), so we will let the
            // server apply its standard behaviour ("writeWith").
            return response.writeWith(stream);

        }

        // Either we are in DATA-DRIVEN mode or a limit for output chunks has been set (CHUNKED mode), so we will
        // use "writeAndFlushWith" in order to make sure that output is flushed after each buffer.
        return response.writeAndFlushWith(Flux.from(stream).window(1));

    }


    

    private static Optional<Charset> getCharset(final MediaType mediaType) {
        return mediaType != null ? Optional.ofNullable(mediaType.getCharset()) : Optional.empty();
    }




    private static boolean isDataDriven(final Map<String,Object> mergedModel) {
        if (mergedModel == null || mergedModel.size() == 0) {
            return false;
        }
        for (final Object value : mergedModel.values()) {
            if (value instanceof IReactiveDataDriverContextVariable) {
                return true;
            }
        }
        return false;
    }




    private ReactiveAdapterRegistry getReactiveAdapterRegistry() {

	    final ApplicationContext applicationContext = getApplicationContext();
	    if (applicationContext == null) {
	        return null;
        }

        if (applicationContext != null) {
            try {
                return applicationContext.getBean(ReactiveAdapterRegistry.class);
            } catch (final NoSuchBeanDefinitionException ignored) {
                // No registry, but note that we can live without it (though limited to Flux and Mono)
            }
        }
        return null;

    }


}
