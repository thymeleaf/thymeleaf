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

import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
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
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.IThrottledTemplateProcessor;
import org.thymeleaf.context.IContext;
import org.thymeleaf.engine.DataDrivenTemplateIterator;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring5.context.reactive.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.reactive.SpringWebReactiveExpressionContext;
import org.thymeleaf.spring5.context.reactive.SpringWebReactiveThymeleafRequestContext;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.util.Validate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public class ThymeleafReactiveView extends AbstractView implements BeanNameAware {


    protected static final Logger logger = LoggerFactory.getLogger(ThymeleafReactiveView.class);

    public static final int DEFAULT_RESPONSE_CHUNK_SIZE_BYTES = Integer.MAX_VALUE;


    private String beanName = null;
    private ITemplateEngine templateEngine = null;
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




    protected ITemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }

    
    protected void setTemplateEngine(final ITemplateEngine templateEngine) {
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
    protected Mono<Void> renderInternal(
            final Map<String, Object> renderAttributes, final MediaType contentType, final ServerWebExchange exchange) {
        return renderFragmentInternal(this.markupSelectors, renderAttributes, contentType, exchange);
    }


    protected Mono<Void> renderFragmentInternal(
            final Set<String> markupSelectorsToRender, final Map<String, Object> renderAttributes,
            final MediaType contentType, final ServerWebExchange exchange) {

        final String viewTemplateName = getTemplateName();
        final ITemplateEngine viewTemplateEngine = getTemplateEngine();

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
        final RequestContext requestContext = new RequestContext(exchange, mergedModel, applicationContext);
        final SpringWebReactiveThymeleafRequestContext thymeleafRequestContext =
                new SpringWebReactiveThymeleafRequestContext(requestContext, exchange);

        addRequestContextAsVariable(mergedModel, SpringContextVariableNames.SPRING_REQUEST_CONTEXT, requestContext);
        // Add the Thymeleaf RequestContext wrapper that we will be using in this dialect (the bare RequestContext
        // stays in the context to for compatibility with other dialects)
        mergedModel.put(SpringContextVariableNames.THYMELEAF_REQUEST_CONTEXT, thymeleafRequestContext);


        // Expose Thymeleaf's own evaluation context as a model variable
        //
        // Note Spring's EvaluationContexts are NOT THREAD-SAFE (in exchange for SpelExpressions being thread-safe).
        // That's why we need to create a new EvaluationContext for each request / template execution, even if it is
        // quite expensive to create because of requiring the initialization of several ConcurrentHashMaps.
        final ConversionService conversionService = applicationContext.getBean(ConversionService.class);
        final ThymeleafEvaluationContext evaluationContext =
                new ThymeleafEvaluationContext(applicationContext, conversionService);
        mergedModel.put(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);

        // TODO * Throughout this integration we are going to use the non-Web implementations of IContext and IEngineContext,
        // TODO   which means that model variables will not be synchronized with the attributes map at the ServerWebExchange
        // TODO   (in the Spring MVC integration, context variables are directly synchronized with HttpServletRequest
        // TODO   attributes for better integration with other view-layer technologies that rely directly on the request)
        // TODO   Would this be an issue here? Shouldn't we be synchronizing with ServerWebExchange attributes?


        /*
         * ----------------------------------------------------------------------------------------------------------
         * INSTANTIATION OF THE CONTEXT
         * ----------------------------------------------------------------------------------------------------------
         * - Once the model has been merged, we can create the Thymeleaf context object itself.
         * - The reason it is an ExpressionContext and not a Context is that before executing the template itself,
         *   we might need to use it for computing the markup selectors (if "template :: selector" was specified).
         * - The reason it is not a WebExpressionContext is that this class is linked to the Servlet API, which
         *   might not be present in a Spring Reactive environment.
         * ----------------------------------------------------------------------------------------------------------
         */

        final IEngineConfiguration configuration = viewTemplateEngine.getConfiguration();
        final SpringWebReactiveExpressionContext context =
                new SpringWebReactiveExpressionContext(configuration, exchange, getLocale(), mergedModel);


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
                    FragmentExpression.createExecutedFragmentExpression(context, fragmentExpression, StandardExpressionExecutionContext.NORMAL);

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
            // TODO * Change this to less-artisan code if the HttpHeaders class at some point in the future allows
            // TODO   setting the Content-Language at a higher level.
            responseHeaders.set("Content-Language", templateLocale.toString());
        }

        // Get the charset from the selected content type (or use default)
        final Charset charset = getCharset(contentType).orElse(getDefaultCharset());


        /*
         * ----------------------------------------------------------------------------------------------------------
         * SET (AND RETURN) THE TEMPLATE PROCESSING Flux<DataBuffer> OBJECTS
         * ----------------------------------------------------------------------------------------------------------
         * - There are three possible processing mode, for each of which a Publisher<DataBuffer> will be created in a
         *   different way:
         *
         *     1. FULL: Output buffers not limited (templateResponseMaxChunkSizeBytes == Integer.MAX_VALUE) and
         *        no data-driven execution (no context variable of type Publisher<X> driving the template engine
         *        execution): In this case Thymeleaf will be executed unthrottled, in full mode, writing output
         *        to a single DataBuffer instanced before execution, and which will be passed to the output channels
         *        in a single onNext(buffer) call (immediately followed by onComplete()).
         *
         *     2. CHUNKED: Output buffers limited in size (responseMaxChunkSizeBytes) but no data-driven
         *        execution (no Publisher<X> driving engine execution). All model attributes are expected to be fully
         *        resolved before engine execution (except those implementing Thymeleaf's ILazyContextVariable
         *        interface, including its reactive subinterface IReactiveLazyContextVariable) and the Thymeleaf
         *        engine will execute in throttled mode, performing a full-stop each time the buffer reaches the
         *        specified size, sending it to the output channels with onNext(buffer) and then waiting until
         *        these output channels make the engine resume its work with a new request(n) call. This
         *        execution mode will request an output flush from the server after producing each buffer.
         *
         *     3. DATA-DRIVEN: one of the model attributes is a Publisher<X> wrapped inside an implementation
         *        of the IReactiveDataDriverContextVariable<?> interface. In this case, the Thymeleaf engine will
         *        execute as a response to onNext(List<X>) events triggered by this Publisher. The
         *        "dataDrivenChunkSizeElements" specified at the model attribute will define the amount of elements
         *        produced by this Publisher that will be buffered into a List<X> before triggering the template
         *        engine each time (which is why Thymeleaf will react on onNext(List<X>) and not onNext(X)). Thymeleaf
         *        will expect to find a "th:each" iteration on the data-driven variable inside the processed template,
         *        and will be executed in throttled mode for the published elements, sending the resulting DataBuffer
         *        (or DataBuffers) to the output channels via onNext(buffer) and stopping until a new onNext(List<X>)
         *        event is triggered. When execution is data-driven, a limit in size can be optionally specified for
         *        the output buffers (responseMaxChunkSizeBytes) which will make Thymeleaf never send
         *        to the output channels a buffer bigger than that (thus splitting the output generated for a List<X>
         *        of published elements into several buffers if required) and also will make Thymeleaf request
         *        an output flush from the server after producing each buffer.
         * ----------------------------------------------------------------------------------------------------------
         */

        final DataDriverSpecification dataDriverSpec;
        try {
            dataDriverSpec = findDataDriverInContext(context);
        } catch (final TemplateProcessingException e) {
            return Mono.error(e);
        }

        if (dataDriverSpec != null) {

            final String dataDriverVariableName = dataDriverSpec.getContextVariableName();
            final Publisher<Object> dataDriverStream = dataDriverSpec.getDataStream();
            final int dataDriverBufferSizeElements = dataDriverSpec.getDataStreamBufferSizeElements();

            // We will replace the data-driver ctx variable with a special throttling template iterator
            final DataDrivenTemplateIterator throttledIterator = new DataDrivenTemplateIterator();
            context.setVariable(dataDriverVariableName, throttledIterator);

            final Flux<DataBuffer> dataFlow =
                    createDataDrivenFlow(
                            templateName, viewTemplateEngine, processMarkupSelectors, context,
                            dataDriverStream,                    // data-driver, Publisher that Thymeleaf will consume
                            dataDriverBufferSizeElements,        // elements in the data-driver will be buffered in List<T> of this size
                            throttledIterator,                   // iterator in charge of throttling the engine
                            templateResponseMaxChunkSizeBytes,   // chunk max size limit (can be none: MAX_VALUE)
                            response.bufferFactory(), charset);

            // No size limit for output chunks has been set, so we will let the
            // server apply its standard behaviour ("writeWith").
            if (templateResponseMaxChunkSizeBytes == Integer.MAX_VALUE) {
                return response.writeWith(dataFlow);
            }

            // A limit for output chunks has been set, so we will use "writeAndFlushWith" in order to make
            // sure that output is flushed after each buffer.
            return response.writeAndFlushWith(dataFlow.window(1));

        }

        // At this point we know the execution is not going to be data-driven, so Thymeleaf will NOT execute
        // acting as a subscriber of a given Publisher<?>. But we will still need to check if a limit has been
        // set of the output chunk size, which would mean the server would have to be throttled.

        if (templateResponseMaxChunkSizeBytes == Integer.MAX_VALUE) {

            // No limit to be set to the size of output chunks, so the entire output will be rendered to a
            // single DataBuffer object that will be sent to the output channels.

            final Mono<DataBuffer> dataMono =
                    createFullFlow(
                            templateName, viewTemplateEngine, processMarkupSelectors, context,
                            response.bufferFactory(), charset);

            // No size limit for output chunks has been set, so we will let the
            // server apply its standard behaviour ("writeWith").
            return response.writeWith(dataMono);

        }

        // Given there is a limit in the size of the chunks to be output, we will need to create a more
        // complex stream of template output, a Flux<DataBuffer> that will publish DataBuffer objects containing
        // at most the amount of bytes set as limit.

        final Flux<DataBuffer> dataFlow =
                createChunkedFlow(
                        templateName, viewTemplateEngine, processMarkupSelectors, context,
                        templateResponseMaxChunkSizeBytes, // buffer max size limit
                        response.bufferFactory(), charset);

        // A limit for output chunks has been set, so we will use "writeAndFlushWith" in order to make
        // sure that output is flushed after each buffer.
        return response.writeAndFlushWith(dataFlow.window(1));

    }




    static IThrottledTemplateProcessor initializeThrottledProcessor(
            final String templateName, final ITemplateEngine templateEngine, final Set<String> markupSelectors, final IContext context) {

        if (logger.isDebugEnabled()) {
            logger.debug("Starting preparation of Thymeleaf template [" + templateName + "].");
        }

        final IThrottledTemplateProcessor throttledProcessor =
                templateEngine.processThrottled(templateName, markupSelectors, context);

        if (logger.isDebugEnabled()) {
            logger.debug("Finished preparation of Thymeleaf template [" + templateName + "].");
        }

        return throttledProcessor;

    }




    /*
     * Creates a Flux<DataBuffer> for processing templates non-data-driven, but with an established limit in the
     * size of the output chunks.
     */
    static Flux<DataBuffer> createChunkedFlow(
            final String templateName, final ITemplateEngine templateEngine, final Set<String> markupSelectors, final IContext context,
            final int responseMaxBufferSizeBytes, final DataBufferFactory bufferAllocator, final Charset charset) {

        return Flux.generate(
                () -> initializeThrottledProcessor(templateName, templateEngine, markupSelectors, context),
                (throttledProcessor, emitter) -> {

                    final DataBuffer buffer =
                            (responseMaxBufferSizeBytes != Integer.MAX_VALUE ?
                                    bufferAllocator.allocateBuffer(responseMaxBufferSizeBytes) :
                                    bufferAllocator.allocateBuffer());

                    try {
                        throttledProcessor.process(responseMaxBufferSizeBytes, buffer.asOutputStream(), charset);
                    } catch (final Throwable t) {
                        emitter.error(t);
                        return null;
                    }

                    emitter.next(buffer);

                    if (throttledProcessor.isFinished()) {
                        emitter.complete();
                    }

                    return throttledProcessor;

                });
    }




    /*
     * Creates a Mono<DataBuffer> for processing templates non-data-driven, and also without a limit in the size of
     * the output chunks. So a single DataBuffer object will be output.
     */
    static Mono<DataBuffer> createFullFlow(
            final String templateName, final ITemplateEngine templateEngine, final Set<String> markupSelectors, final IContext context,
            final DataBufferFactory bufferAllocator, final Charset charset) {

        return Mono.create(
                subscriber -> {

                    if (logger.isDebugEnabled()) {
                        logger.debug("Starting execution (FULL mode) of Thymeleaf template [" + templateName + "].");
                    }

                    final DataBuffer dataBuffer = bufferAllocator.allocateBuffer();
                    final OutputStreamWriter writer = new OutputStreamWriter(dataBuffer.asOutputStream(), charset);

                    try {
                        templateEngine.process(templateName, markupSelectors, context, writer);
                    } catch (final Throwable t) {
                        subscriber.error(t);
                        return;
                    }

                    if (logger.isDebugEnabled()) {
                        logger.debug("Finished execution (FULL mode) of Thymeleaf template [" + templateName + "].");
                    }

                    // This is a Mono<?>, so no need to call "next()" or "complete()"
                    subscriber.success(dataBuffer);

                });

    }




    /*
     * Creates a Flux<DataBuffer> for processing data-driven templates: a Publisher<X> variable will control
     * the engine, making it output the markup corresponding to each buffer of streamed data as a part of its own
     * data publishing flow.
     */
    static Flux<DataBuffer> createDataDrivenFlow(
            final String templateName, final ITemplateEngine templateEngine, final Set<String> markupSelectors, final IContext context,
            final Publisher<Object> dataDriverPublisher, final int dataDriverChunkSizeElements, final DataDrivenTemplateIterator dataDrivenIterator,
            final int responseMaxBufferSizeBytes, final DataBufferFactory bufferAllocator, final Charset charset) {

        // STEP 1: Create the data stream buffers
        final Flux<List<Object>> dataDrivenChunkedFlow = Flux.from(dataDriverPublisher).buffer(dataDriverChunkSizeElements);

        // STEP 2: Initialize the (throttled) template engine for each subscriber (normally there will only be one)
        final Flux<DataDrivenValuesWithContext> dataDrivenWithContextFlow =
                Flux.using(
                        () -> initializeThrottledProcessor(templateName, templateEngine, markupSelectors, context),
                        throttledProcessor ->
                                Flux.concat(
                                        dataDrivenChunkedFlow.map(values -> new DataDrivenValuesWithContext(throttledProcessor, values)),
                                        Mono.just(new DataDrivenValuesWithContext(throttledProcessor, null)) // Will process the part of the template after iteration
                                ),
                        throttledProcessor -> { /* no need to perform any cleanup operations */ });

        // STEP 3: React to each buffer of published data by creating one or many (concatMap) DataBuffers containing
        //         the result of processing only that buffer.
        return dataDrivenWithContextFlow.concatMap(
                valuesWithContext ->
                        Flux.generate(
                                () -> {

                                    final List<Object> values = valuesWithContext.getValues();
                                    if (values != null) {
                                        dataDrivenIterator.feedBuffer(values);
                                    } else {
                                        dataDrivenIterator.feedingComplete();
                                    }
                                    return valuesWithContext;

                                },
                                (vwc, emitter) -> {

                                    final List<Object> values = vwc.getValues();
                                    final IThrottledTemplateProcessor throttledProcessor = vwc.getThrottledProcessor();

                                    final DataBuffer buffer =
                                            (responseMaxBufferSizeBytes != Integer.MAX_VALUE?
                                                    bufferAllocator.allocateBuffer(responseMaxBufferSizeBytes) :
                                                    bufferAllocator.allocateBuffer());

                                    try {
                                        throttledProcessor.process(responseMaxBufferSizeBytes, buffer.asOutputStream(), charset);
                                    } catch (final Throwable t) {
                                        emitter.error(t);
                                        return null;
                                    }

                                    emitter.next(buffer);

                                    if (values != null) {
                                        if (!dataDrivenIterator.continueBufferExecution()) {
                                            emitter.complete();
                                        }
                                    } else {
                                        if (throttledProcessor.isFinished()) {
                                            emitter.complete();
                                        }
                                    }

                                    return vwc;

                                })

                );

    }



    private static Optional<Charset> getCharset(final MediaType mediaType) {
        return mediaType != null ? Optional.ofNullable(mediaType.getCharset()) : Optional.empty();
    }




    private static DataDriverSpecification findDataDriverInContext(final SpringWebReactiveExpressionContext context) {

        // In SpringWebReactiveExpressionContext, variables are backed by a Map<String,Object>. So this
        // iteration on all the names and many "get()" calls shouldn't be an issue perf-wise.
        DataDriverSpecification dataDriver = null;
        final Set<String> variableNames = context.getVariableNames();
        for (final String variableName : variableNames) {
            final Object variableValue = context.getVariable(variableName);
            if (variableValue != null && variableValue instanceof IReactiveDataDriverContextVariable<?>) {
                if (dataDriver != null) {
                    throw new TemplateProcessingException(
                            "Only one data-driver variable is allowed to be specified as a model attribute, but " +
                            "at least two have been identified: '" + dataDriver.getContextVariableName() + "' " +
                            "and '" + variableName + "'");
                }
                final IReactiveDataDriverContextVariable<Object> dataDriverContextVariable =
                        (IReactiveDataDriverContextVariable<Object>) variableValue;
                return new DataDriverSpecification(
                        variableName, dataDriverContextVariable.getDataStream(), dataDriverContextVariable.getDataStreamBufferSizeElements());
            }
        }
        return dataDriver;

    }




    protected static void addRequestContextAsVariable(
            final Map<String,Object> model, final String variableName, final RequestContext requestContext)
            throws TemplateProcessingException {

        if (model.containsKey(variableName)) {
            throw new TemplateProcessingException(
                    "Cannot expose request context in model attribute '" + variableName +
                    "' because an existing model object of the same name");
        }
        model.put(variableName, requestContext);

    }




    static final class DataDriverSpecification {

        private final String contextVariableName;
        private final Publisher<Object> dataStream;
        private final int dataStreamBufferSizeElements;

        public DataDriverSpecification(
                final String contextVariableName, final Publisher<Object> dataStream, final int dataStreamBufferSizeElements) {
            super();
            Validate.isTrue(dataStreamBufferSizeElements > 0, "Data Stream Buffer Size cannot be <= 0 for variable " + contextVariableName);
            this.contextVariableName = contextVariableName;
            this.dataStream = dataStream;
            this.dataStreamBufferSizeElements = dataStreamBufferSizeElements;
        }

        public String getContextVariableName() {
            return this.contextVariableName;
        }

        public Publisher<Object> getDataStream() {
            return this.dataStream;
        }

        public int getDataStreamBufferSizeElements() {
            return this.dataStreamBufferSizeElements;
        }

    }




    static final class DataDrivenValuesWithContext {

        private final IThrottledTemplateProcessor throttledProcessor;
        private final List<Object> values;

        public DataDrivenValuesWithContext(
                final IThrottledTemplateProcessor throttledProcessor, final List<Object> values) {
            super();
            this.throttledProcessor = throttledProcessor;
            this.values = values;
        }

        public IThrottledTemplateProcessor getThrottledProcessor() {
            return this.throttledProcessor;
        }

        public List<Object> getValues() {
            return this.values;
        }

    }



}
