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

import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferAllocator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.view.AbstractView;
import org.springframework.web.reactive.view.ViewResolverSupport;
import org.springframework.web.server.ServerWebExchange;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.IThrottledTemplateProcessor;
import org.thymeleaf.context.ExpressionContext;
import org.thymeleaf.context.IContext;
import org.thymeleaf.engine.DataDrivenTemplateIterator;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring4.expression.ThymeleafEvaluationContext;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class ThymeleafView extends AbstractView implements BeanNameAware {


    protected static final Logger logger = LoggerFactory.getLogger(ThymeleafView.class);

    public static final int DEFAULT_RESPONSE_BUFFER_SIZE_BYTES = Integer.MAX_VALUE;
    public static final int DEFAULT_DATA_DRIVEN_CHUNK_SIZE_ELEMENTS = 100;

    /*
     * Name of the variable containing the map of path variables to be applied.
     */
    // TODO * Add Path Variable management when it's available in Spring Reactive
    // private static final String pathVariablesSelector = ...

    /**
     * <p>
     *   Default charset set to UTF-8, default for Spring Reactive.
     *   Value is "<tt>text/html;charset=UTF-8</tt>".
     * </p>
     */
    public static final MediaType DEFAULT_CONTENT_TYPE = ViewResolverSupport.DEFAULT_CONTENT_TYPE;
    // TODO * Application of AbstractView's List<MediaType> supportedMediaTypes is still unclear in Spring Reactive
    

    private String beanName = null;
    private MediaType contentType = DEFAULT_CONTENT_TYPE;
    private boolean contentTypeSet = false;
    private String characterEncoding = null;
    private ITemplateEngine templateEngine = null;
	private String templateName = null;
    private Locale locale = null;
    private Map<String, Object> staticVariables = null;

    private String dataDrivenVariableName = null;
    private Integer dataDrivenChunkSizeElements = null;

    private Set<String> markupSelectors = null;



    // This will determine whether we will be throttling or not, and if so the maximum size of the buffers that will be
    // produced by the throttled engine each time the back-pressure mechanism asks for a new "unit" (a new DataBuffer)
    //
    // The value established here is nullable (and null by default) because it will work as an override of the
    // value established at the ThymeleafViewResolver for the same purpose.
    //
    private Integer responseMaxBufferSizeBytes = null;






	public ThymeleafView() {
	    super();
	}



    public String getMarkupSelector() {
        return (this.markupSelectors == null || this.markupSelectors.size() == 0? null : this.markupSelectors.iterator().next());
    }


    public void setMarkupSelector(final String markupSelector) {
        this.markupSelectors =
                (markupSelector == null || markupSelector.trim().length() == 0? null : Collections.singleton(markupSelector.trim()));
    }

	


    public MediaType getContentType() {
        return this.contentType;
    }


    public void setContentType(final MediaType contentType) {
        this.contentType = contentType;
        this.contentTypeSet = true;
    }


    public void setContentType(final String contentType) {
        this.contentType = MediaType.valueOf(contentType);
        this.contentTypeSet = true;
    }


    protected boolean isContentTypeSet() {
        return this.contentTypeSet;
    }


    
	
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }


    public void setCharacterEncoding(final String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }




    public String getDataDrivenVariableName() {
        return dataDrivenVariableName;
    }


    public void setDataDrivenVariableName(final String dataDrivenVariableName) {
        this.dataDrivenVariableName = dataDrivenVariableName;
    }




    // Default is DEFAULT_DATA_DRIVEN_CHUNK_SIZE_ELEMENTS
    public int getDataDrivenChunkSizeElements() {
        return this.dataDrivenChunkSizeElements == null? DEFAULT_DATA_DRIVEN_CHUNK_SIZE_ELEMENTS : this.dataDrivenChunkSizeElements.intValue();
    }


    // We need this one at the ViewResolver to determine if a value has been set at all
    Integer getNullableDataDrivenBufferSize() {
        return this.dataDrivenChunkSizeElements;
    }


    public void setDataDrivenChunkSizeElements(final int dataDrivenChunkSizeElements) {
        this.dataDrivenChunkSizeElements = Integer.valueOf(dataDrivenChunkSizeElements);
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




    // Default is Integer.MAX_VALUE, which means we will not be throttling at all
    public int getResponseMaxBufferSizeBytes() {
        return this.responseMaxBufferSizeBytes == null? DEFAULT_RESPONSE_BUFFER_SIZE_BYTES : this.responseMaxBufferSizeBytes.intValue();
    }


    // We need this one at the ViewResolver to determine if a value has been set at all
    Integer getNullableResponseMaxChunkSize() {
        return this.responseMaxBufferSizeBytes;
    }


    public void setResponseMaxBufferSizeBytes(final int responseMaxBufferSizeBytes) {
        this.responseMaxBufferSizeBytes = Integer.valueOf(responseMaxBufferSizeBytes);
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
    protected Flux<DataBuffer> renderInternal(final Map<String, Object> renderAttributes, final ServerWebExchange exchange) {
        return renderFragmentInternal(this.markupSelectors, renderAttributes, exchange);
    }


    protected Flux<DataBuffer> renderFragmentInternal(
            final Set<String> markupSelectorsToRender, final Map<String, Object> renderAttributes, final ServerWebExchange exchange) {

        final String viewTemplateName = getTemplateName();
        final ITemplateEngine viewTemplateEngine = getTemplateEngine();

        if (viewTemplateName == null) {
            throw new IllegalArgumentException("Property 'templateName' is required");
        }
        if (getLocale() == null) {
            throw new IllegalArgumentException("Property 'locale' is required");
        }
        if (viewTemplateEngine == null) {
            throw new IllegalArgumentException("Property 'thymeleafTemplateEngine' is required");
        }


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
        final Map<String, Object> templateStaticVariables = getStaticVariables();
        if (templateStaticVariables != null) {
            mergedModel.putAll(templateStaticVariables);
        }
        // TODO * Add Path Variables map to the merged model (when they exist in Spring Reactive)
        if (renderAttributes != null) {
            mergedModel.putAll(renderAttributes);
        }

        final ApplicationContext applicationContext = getApplicationContext();

        // TODO * Apply RequestContext equivalent (still does not exist in Spring Reactive)


        // Expose Thymeleaf's own evaluation context as a model variable
        //
        // Note Spring's EvaluationContexts are NOT THREAD-SAFE (in exchange for SpelExpressions being thread-safe).
        // That's why we need to create a new EvaluationContext for each request / template execution, even if it is
        // quite expensive to create because of requiring the initialization of several ConcurrentHashMaps.
        // TODO * In SpringMVC we were obtaining the ConversionService from the request, because it had been placed there
        // TODO   by SpringMVC's infrastructure. Now we are getting it from the ApplicationContext. Might this be an issue?
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
        final ExpressionContext context = new ExpressionContext(configuration, getLocale(), mergedModel);


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
                throw new IllegalArgumentException("Invalid template name specification: '" + viewTemplateName + "'");
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
                    throw new IllegalArgumentException(
                            "Parameters in a view specification must be named (non-synthetic): '" + viewTemplateName + "'");
                }

                context.setVariables(nameFragmentParameters);

            }


        }

        final Set<String> processMarkupSelectors;
        if (markupSelectors != null && markupSelectors.size() > 0) {
            if (markupSelectorsToRender != null && markupSelectorsToRender.size() > 0) {
                throw new IllegalArgumentException(
                        "A markup selector has been specified (" + Arrays.asList(markupSelectors) + ") for a view " +
                        "that was already being executed as a fragment (" + Arrays.asList(markupSelectorsToRender) + "). " +
                        "Only one fragment selection is allowed.");
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
         *   template (locale, encoding, buffer and chunk sizes, etc.)
         * ----------------------------------------------------------------------------------------------------------
         */

        final MediaType templateContentType = getContentType();
        final Locale templateLocale = getLocale();
        final String templateCharacterEncoding = getCharacterEncoding();
        final int templateResponseMaxBufferSizeBytes = getResponseMaxBufferSizeBytes();
        final String templateDataDrivenVariableName = getDataDrivenVariableName();
        final int templateDataDrivenChunkSizeElements = getDataDrivenChunkSizeElements();


        final HttpHeaders responseHeaders = exchange.getResponse().getHeaders();
        if (templateLocale != null) {
            // TODO * Change this to less-artisan code if the HttpHeaders class at some point in the future allows
            // TODO   setting the Content-Language at a higher level.
            responseHeaders.set("Content-Language", templateLocale.toString());
        }
        Charset responseCharset = null;
        String responseContentType = null;
        if (templateContentType != null) {
            responseCharset = templateContentType.getCharSet();
            responseContentType = templateContentType.toString();
        } else {
            responseCharset = DEFAULT_CONTENT_TYPE.getCharSet();
            responseContentType = DEFAULT_CONTENT_TYPE.toString();
        }
        if (templateCharacterEncoding != null) {
            responseCharset = Charset.forName(templateCharacterEncoding);
            final int separatorPos = responseContentType.indexOf(';');
            if (separatorPos < 0) {
                responseContentType = responseContentType + ";charset=" + responseCharset.toString();
            } else {
                responseContentType = responseContentType.substring(0, separatorPos) + ";charset=" + responseCharset.toString();
            }
        }
        responseHeaders.setContentType(MediaType.valueOf(responseContentType));

        final Charset charset = responseCharset;


        /*
         * ----------------------------------------------------------------------------------------------------------
         * SET (AND RETURN) THE TEMPLATE PROCESSING Flux<DataBuffer> OBJECTS
         * ----------------------------------------------------------------------------------------------------------
         * - There are three possible processing mode, for each of which a Flux<DataBuffer> will be created in a
         *   different way:
         *
         *     1. NORMAL: Output buffers not limited (templateResponseMaxBufferSizeBytes == Integer.MAX_VALUE) and
         *        no data-driven execution (no context variable of type Publisher<X> driving the template engine
         *        execution): In this case Thymeleaf will be executed unthrottled, in normal mode, writing output
         *        to a single DataBuffer instanced before execution, and which will be passed to the output channels
         *        in a single onNext(buffer) call (immediately followed by onComplete()).
         *
         *     2. BUFFERED: Output buffers limited in size (templateResponseMaxBufferSizeBytes) but no data-driven
         *        execution (no Publisher<X> driving engine execution). All model attributes are expected to be fully
         *        resolved before engine execution (except those implementing Thymeleaf's ILazyContextVariable
         *        interface) and the Thymeleaf engine will execute in throttled mode, performing a full-stop each time
         *        the buffer reaches the specified size, sending it to the output channels with onNext(buffer) and
         *        then waiting until these output channels make the engine resume its work with a new request(n) call.
         *
         *     3. DATA-DRIVEN: one of the model attributes is a Publisher<X> which name is established at
         *        the "dataDrivenVariableName" configuration parameter at the View or ViewResolver. In this case,
         *        the Thymeleaf engine will execute as a response to onNext(List<X>) events triggered by this
         *        Publisher. A related parameter, "dataDrivenChunkSizeElements" will define the amount of elements
         *        produced by this Publisher that will be buffered into a List<X> before triggering the template
         *        engine each time (which is why Thymeleaf will react on onNext(List<X>) and not onNext(X)). Thymeleaf
         *        will expect to find a "th:each" iteration on the data-driven variable inside the processed template,
         *        and will be executed in throttled mode for the published elements, sending the resulting DataBuffer
         *        (or DataBuffers) to the output channels via onNext(buffer) and stopping until a new onNext(List<X>)
         *        event is triggered. When execution is data-driven, a limit in size can be optionally specified for
         *        the output buffers (templateResponseMaxBufferSizeBytes) which will make Thymeleaf never send
         *        to the output channels a buffer bigger than that (thus splitting the output generated for a List<X>
         *        of published elements into several buffers if required).
         * ----------------------------------------------------------------------------------------------------------
         */

        if (!StringUtils.isEmptyOrWhitespace(templateDataDrivenVariableName)) {
            final Object dataDrivenVariableValue = context.getVariable(templateDataDrivenVariableName);
            if (dataDrivenVariableValue != null && dataDrivenVariableValue instanceof Publisher<?>) {

                final DataDrivenTemplateIterator throttledIterator = new DataDrivenTemplateIterator();
                context.setVariable(templateDataDrivenVariableName, throttledIterator);

                final Publisher<Object> contextBoundPublisher = (Publisher<Object>) dataDrivenVariableValue;

                return createDataDrivenFlow(
                        templateName, viewTemplateEngine, processMarkupSelectors, context,
                        contextBoundPublisher, templateDataDrivenChunkSizeElements, throttledIterator,
                        templateResponseMaxBufferSizeBytes, getBufferAllocator(), charset);

            }
        }


        if (templateResponseMaxBufferSizeBytes == Integer.MAX_VALUE) {
            return createNormalOutputDrivenFlow(templateName, viewTemplateEngine, processMarkupSelectors, context, getBufferAllocator(), charset);
        }
        return createBufferedOutputDrivenFlow(templateName, viewTemplateEngine, processMarkupSelectors, context, templateResponseMaxBufferSizeBytes, getBufferAllocator(), charset);

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
     * size of the output buffers.
     */
    static Flux<DataBuffer> createBufferedOutputDrivenFlow(
            final String templateName, final ITemplateEngine templateEngine, final Set<String> markupSelectors, final IContext context,
            final int responseMaxBufferSizeBytes, final DataBufferAllocator bufferAllocator, final Charset charset) {

        return Flux.generate(
                () -> initializeThrottledProcessor(templateName, templateEngine, markupSelectors, context),
                (throttledProcessor, emitter) -> {
                    final DataBuffer buffer =
                            (responseMaxBufferSizeBytes != Integer.MAX_VALUE ?
                                    bufferAllocator.allocateBuffer(responseMaxBufferSizeBytes) :
                                    bufferAllocator.allocateBuffer());
                    throttledProcessor.process(responseMaxBufferSizeBytes, buffer.asOutputStream(), charset);
                    emitter.tryEmit(buffer);
                    if (throttledProcessor.isFinished()) {
                        emitter.complete();
                    }
                    return throttledProcessor;
                });
    }




    /*
     * Creates a Flux<DataBuffer> for processing templates non-data-driven, and also without a limit in the size of
     * the output buffers.
     */
    static Flux<DataBuffer> createNormalOutputDrivenFlow(
            final String templateName, final ITemplateEngine templateEngine, final Set<String> markupSelectors, final IContext context,
            final DataBufferAllocator bufferAllocator, final Charset charset) {

        return Flux.create(
                subscriber -> {

                    if (logger.isDebugEnabled()) {
                        logger.debug("Starting full execution (unbuffered) of Thymeleaf template [" + templateName + "].");
                    }

                    final DataBuffer dataBuffer = bufferAllocator.allocateBuffer();
                    final OutputStreamWriter writer = new OutputStreamWriter(dataBuffer.asOutputStream(), charset);

                    templateEngine.process(templateName, markupSelectors, context, writer);

                    if (logger.isDebugEnabled()) {
                        logger.debug("Finished full execution (unbuffered) of Thymeleaf template [" + templateName + "].");
                    }

                    subscriber.onNext(dataBuffer);
                    subscriber.onComplete();

                });

    }




    /*
     * Creates a Flux<DataBuffer> for processing data-driven templates: a Publisher<X> variable will control
     * the engine, making it output the markup corresponding to each chunk of published data as a part of its own
     * data publishing flow.
     */
    static Flux<DataBuffer> createDataDrivenFlow(
            final String templateName, final ITemplateEngine templateEngine, final Set<String> markupSelectors, final IContext context,
            final Publisher<Object> dataDriverPublisher, final int dataDriverChunkSizeElements, final DataDrivenTemplateIterator dataDrivenIterator,
            final int responseMaxBufferSizeBytes, final DataBufferAllocator bufferAllocator, final Charset charset) {

        // STEP 1: Create the chunks (flow buffering)
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

        // STEP 3: React to each chunk of published data by creating one or many (concatMap) DataBuffers containing
        //         the result of processing only that chunk.
        return dataDrivenWithContextFlow.concatMap(
                valuesWithContext -> {

                    final IThrottledTemplateProcessor throttledProcessor = valuesWithContext.getThrottledProcessor();
                    final List<Object> values = valuesWithContext.getValues();
                    if (values != null) {
                        dataDrivenIterator.feedBuffer(values);
                    } else {
                        dataDrivenIterator.feedingComplete();
                    }

                    return Flux.create(
                                subscriber -> {

                                    final DataBuffer buffer =
                                            (responseMaxBufferSizeBytes != Integer.MAX_VALUE?
                                                    bufferAllocator.allocateBuffer(responseMaxBufferSizeBytes) :
                                                    bufferAllocator.allocateBuffer());
                                    throttledProcessor.process(responseMaxBufferSizeBytes, buffer.asOutputStream(), charset);
                                    subscriber.onNext(buffer);
                                    if (values != null) {
                                        if (!dataDrivenIterator.continueBufferExecution()) {
                                            subscriber.onComplete();
                                        }
                                    } else {
                                        if (throttledProcessor.isFinished()) {
                                            subscriber.onComplete();
                                        }
                                    }
                                });

                });

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
