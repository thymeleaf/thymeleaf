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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
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
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring4.expression.ThymeleafEvaluationContext;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.expression.StandardExpressions;
import reactor.core.publisher.Flux;
import reactor.core.util.Exceptions;


/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class ThymeleafView extends AbstractView implements BeanNameAware {

    /*
     * Name of the variable containing the map of path variables to be applied.
     */
    // TODO * Does this come anywhere in the renderAttributes, the ServerWebExchange attributes or the HttpRequest?
    // private static final String pathVariablesSelector = ...

    /**
     * <p>
     *   Default charset set to UTF-8, default for Spring Reactive.
     *   Value is "<tt>text/html;charset=UTF-8</tt>".
     * </p>
     */
    public static final MediaType DEFAULT_CONTENT_TYPE = ViewResolverSupport.DEFAULT_CONTENT_TYPE;
    // TODO * What is the use of AbstractView's List<MediaType> supportedMediaTypes in a View like this (or FreeMarker's)?
    

    private String beanName = null;
    private MediaType contentType = DEFAULT_CONTENT_TYPE;
    private boolean contentTypeSet = false;
    private String characterEncoding = null;
    private ITemplateEngine templateEngine = null;
	private String templateName = null;
    private Locale locale = null;
    private Map<String, Object> staticVariables = null;

    private Set<String> markupSelectors = null;



    // This will determine whether we will be throttling or not, and if so the size of the chunks that will be produced
    // by the throttled engine each time the back-pressure mechanism asks for a new "unit" (a new DataBuffer)
    //
    // The value established here is nullable (and null by default) because it will work as an override of the
    // value established at the ThymeleafViewResolver for the same purpose.
    private Long responseChunkSizeBytes = null;






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




    // Default is Long.MAX_VALUE, which means we will not be throttling at all
    public void setResponseChunkSize(final long responseChunkSizeBytes) {
        this.responseChunkSizeBytes = Long.valueOf(responseChunkSizeBytes);
    }


    public long getResponseChunkSize() {
        return this.responseChunkSizeBytes == null? Long.MAX_VALUE : this.responseChunkSizeBytes.longValue();
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


        final Map<String, Object> mergedModel = new HashMap<String, Object>(30);
        final Map<String, Object> templateStaticVariables = getStaticVariables();
        if (templateStaticVariables != null) {
            mergedModel.putAll(templateStaticVariables);
        }
        // TODO * Add Path Variables map to the merged model (if they exist in Spring Reactive)
        if (renderAttributes != null) {
            mergedModel.putAll(renderAttributes);
        }

        final ApplicationContext applicationContext = getApplicationContext();

        // TODO * Get the equivalent to the RequestContext from somewhere (or create a new instance) and add it
        // TODO   to the mergedModel. We need it for form binding.


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

        final IEngineConfiguration configuration = viewTemplateEngine.getConfiguration();
        final ExpressionContext context = new ExpressionContext(configuration, getLocale(), mergedModel);


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


        final MediaType templateContentType = getContentType();
        final Locale templateLocale = getLocale();
        final String templateCharacterEncoding = getCharacterEncoding();
        final long responseChunkSizeBytes = getResponseChunkSize();


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


        final HttpHeaders responseHeaders = exchange.getResponse().getHeaders();
        if (templateLocale != null) {
            // TODO * There seems to be no way to set the Content-Language HTTP header in the response, how could we
            // TODO   communicate the locale through the response?
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


        if (logger.isDebugEnabled()) {
            logger.debug("Starting preparation of Thymeleaf template [" + templateName + "].");
        }

        final ThymeleafViewWriter writer;
        final IThrottledTemplateProcessor throttledProcessor;
        try {
            writer = new ThymeleafViewWriter(responseCharset);
            throttledProcessor = viewTemplateEngine.processThrottled(templateName, processMarkupSelectors, context, writer);
        } catch (final Exception e) {
            final String message = "Could not prepare Thymeleaf template [" + templateName + "]";
            return Flux.error(new IllegalStateException(message, e));
        } catch (final Throwable e) {
            return Flux.error(e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Finished preparation of Thymeleaf template [" + templateName + "].");
        }

        return Flux.from(new ThymeleafViewPublisher(getBufferAllocator(), throttledProcessor, writer, responseChunkSizeBytes));

    }




    private static class ThymeleafViewPublisher implements Publisher<DataBuffer> {


        private final DataBufferAllocator dataBufferAllocator;
        private final IThrottledTemplateProcessor throttledProcessor;
        private final ThymeleafViewWriter writer;
        private final long responseChunkSizeBytes;


        ThymeleafViewPublisher(final DataBufferAllocator dataBufferAllocator,
                               final IThrottledTemplateProcessor throttledProcessor,
                               final ThymeleafViewWriter writer,
                               final long responseChunkSizeBytes) {
            super();
            this.dataBufferAllocator = dataBufferAllocator;
            this.throttledProcessor = throttledProcessor;
            this.writer = writer;
            this.responseChunkSizeBytes = responseChunkSizeBytes;
        }


        @Override
        public void subscribe(final Subscriber<? super DataBuffer> subscriber) {

            try {

                final ThymeleafViewSubscription subscription =
                        new ThymeleafViewSubscription(
                                subscriber,
                                this.dataBufferAllocator,
                                this.throttledProcessor,
                                this.writer,
                                this.responseChunkSizeBytes);

                subscriber.onSubscribe(subscription);

            } catch (final Throwable throwable) {
                Exceptions.throwIfFatal(throwable);
                subscriber.onError(throwable);
            }

        }


    }




    private static class ThymeleafViewSubscription implements Subscription {

        private final Subscriber<? super DataBuffer> subscriber;
        private final DataBufferAllocator dataBufferAllocator;
        private final IThrottledTemplateProcessor throttledProcessor;
        private final ThymeleafViewWriter writer;
        private final long responseChunkSizeBytes;

        volatile boolean cancelled;


        ThymeleafViewSubscription(final Subscriber<? super DataBuffer> subscriber,
                                  final DataBufferAllocator dataBufferAllocator,
                                  final IThrottledTemplateProcessor throttledProcessor,
                                  final ThymeleafViewWriter writer,
                                  final long responseChunkSizeBytes) {
            super();
            this.subscriber = subscriber;
            this.dataBufferAllocator = dataBufferAllocator;
            this.throttledProcessor = throttledProcessor;
            this.writer = writer;
            this.responseChunkSizeBytes = responseChunkSizeBytes;
        }


        @Override
        public void request(final long n) {

            if (n < 0) {
                throw new IllegalArgumentException("Number of elements requested cannot be < 0");
            }

            if (this.cancelled) {
                return;
            }

            if (this.responseChunkSizeBytes == Long.MAX_VALUE) {
                processAll();
            } else {
                int i = 0;
                while (i < n && !this.throttledProcessor.isFinished() && !this.cancelled) {
                    processOne();
                    i++;
                }
            }

        }


        void processAll() {
            final DataBuffer buffer = this.dataBufferAllocator.allocateBuffer();
            this.writer.setBuffer(buffer);
            this.throttledProcessor.processAll();
            this.subscriber.onNext(buffer);
            this.subscriber.onComplete();
        }


        void processOne() {
            final DataBuffer buffer = this.dataBufferAllocator.allocateBuffer();
            this.writer.setBuffer(buffer);
            this.throttledProcessor.process((int)this.responseChunkSizeBytes); // TODO allow a long here?
            this.subscriber.onNext(buffer);
            if (this.throttledProcessor.isFinished()) {
                this.subscriber.onComplete();
            }
        }


        @Override
        public void cancel() {
            this.cancelled = true;
        }

    }




    private static class ThymeleafViewWriter extends Writer {


        private final Charset characterEncoding;
        private Writer delegate = null;


        ThymeleafViewWriter(final Charset characterEncoding) {
            super();
            this.characterEncoding = characterEncoding;
        }


        void setBuffer(final DataBuffer buffer) {
            this.delegate = new OutputStreamWriter(buffer.asOutputStream(), this.characterEncoding);
        }



        @Override
        public void write(final int c) throws IOException {
            this.delegate.write(c);
        }


        @Override
        public void write(final String str) throws IOException {
            this.delegate.write(str);
        }


        @Override
        public void write(final String str, final int off, final int len) throws IOException {
            this.delegate.write(str, off, len);
        }


        @Override
        public void write(final char[] cbuf) throws IOException {
            this.delegate.write(cbuf);
        }


        @Override
        public void write(final char[] cbuf, final int off, final int len) throws IOException {
            this.delegate.write(cbuf, off, len);
        }



        @Override
        public void flush() throws IOException {
            this.delegate.flush();
        }


        @Override
        public void close() throws IOException {
            this.delegate.close();
        }


    }


}
