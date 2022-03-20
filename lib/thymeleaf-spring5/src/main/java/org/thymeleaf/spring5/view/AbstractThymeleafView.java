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
package org.thymeleaf.spring5.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.spring5.ISpringTemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;


/**
 * <p>
 *   Abstract implementation class of the Spring MVC {@link org.springframework.web.servlet.View}
 *   interface for Thymeleaf.
 * </p>
 * <p>
 *   Views represent a template being executed, after being resolved (and
 *   instantiated) by a {@link org.springframework.web.servlet.ViewResolver}.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Josh Long
 * 
 * @since 3.0.3
 *
 */
public abstract class AbstractThymeleafView 
        extends WebApplicationObjectSupport 
        implements View, BeanNameAware  {

    
    /**
     * <p>
     *   Default charset set to ISO-8859-1 for compatibility reasons with Spring's AbstractView.
     *   Value is {@code "text/html;charset=ISO-8859-1"}.
     * </p>
     */
    public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=ISO-8859-1";

    /**
     * <p>
     *   By default Thymeleaf will not wait until a template is fully processed and rendered before
     *   starting to output its results. Instead, it will start producing output as soon as possible
     *   while the template is still being processed. Value is {@code true}.
     * </p>
     */
    public static final boolean DEFAULT_PRODUCE_PARTIAL_OUTPUT_WHILE_PROCESSING = true;


    private String beanName = null;
    private String contentType = DEFAULT_CONTENT_TYPE;
    private boolean contentTypeSet = false;
    private boolean forceContentType = false;
    private boolean forceContentTypeSet = false;
    private String characterEncoding = null;
    private boolean producePartialOutputWhileProcessing = DEFAULT_PRODUCE_PARTIAL_OUTPUT_WHILE_PROCESSING;
    private boolean producePartialOutputWhileProcessingSet = false;
    private ISpringTemplateEngine templateEngine = null;
	private String templateName = null;
    private Locale locale = null;
    private Map<String, Object> staticVariables = null;


    /**
     * <p>
     *   Creates a new instance of {@code ThymeleafView}.
     * </p>
     */
	protected AbstractThymeleafView() {
	    super();
	}


	/**
	 * <p>
	 *   Creates a new instance of {@code ThymeleafView}, specifying the
	 *   template name.
	 * </p>
	 * 
	 * @param templateName the template name.
	 */
	protected AbstractThymeleafView(final String templateName) {
	    super();
		this.templateName = templateName;
	}

	
	


    /**
     * <p>
     *   Returns the content type that will used for this view.
     * </p>
     * <p>
     *   Content type will be computed this way:
     * </p>
     * <ul>
     *   <li>If a value is specified calling {@link #setContentType(String)} on
     *       this object, that value will be used.</li>
     *   <li>If a value is specified at the <i>view resolver</i> by calling
     *       {@link ThymeleafViewResolver#setContentType(String)}, that one will
     *       be used.</li>
     *   <li>If none of the above is true, the {@link #DEFAULT_CONTENT_TYPE} constant
     *       with value {@value #DEFAULT_CONTENT_TYPE} will be used.</li>
     * </ul>
     * 
     * @return the content type
     * @see ThymeleafViewResolver#getContentType()
     */
    public String getContentType() {
        return this.contentType;
    }


    /**
     * <p>
     *   Sets the content type that will used for this view.
     * </p>
     * <p>
     *   Content type will be computed this way:
     * </p>
     * <ul>
     *   <li>If a value is specified calling this method, that value will be used.</li>
     *   <li>If a value is specified at the <i>view resolver</i> by calling
     *       {@link ThymeleafViewResolver#setContentType(String)}, that one will
     *       be used.</li>
     *   <li>If none of the above is true, the {@link #DEFAULT_CONTENT_TYPE} constant
     *       with value {@value #DEFAULT_CONTENT_TYPE} will be used.</li>
     * </ul>
     * 
     * @param contentType the content type to be used.
     * @see ThymeleafViewResolver#setContentType(String)
     */
    public void setContentType(final String contentType) {
        this.contentType = contentType;
        this.contentTypeSet = true;
    }

    
    /*
     * Internally used (by ThymeleafViewResolver) in order to know whether a value
     * for the content type has been explicitly set or not.
     */
    protected boolean isContentTypeSet() {
        return this.contentTypeSet;
    }



    /**
     * <p>
     *   Returns whether the configured content type should be forced instead of attempting
     *   a <em>smart</em> content type application based on template name.
     * </p>
     * <p>
     *   When forced, the configured content type ({@link #setForceContentType(boolean)})  will
     *   be applied even if the template name ends in a known suffix:
     *   {@code .html}, {@code .htm}, {@code .xhtml},
     *   {@code .xml}, {@code .js}, {@code .json},
     *   {@code .css}, {@code .rss}, {@code .atom}, {@code .txt}.
     * </p>
     * <p>Default value is <b>{@code false}</b></p>.
     *
     * @return whether the content type will be forced or not.
     * @since 3.0.6
     */
    public boolean getForceContentType() {
        return this.forceContentType;
    }


    /**
     * <p>
     *   Sets whether the configured content type should be forced instead of attempting
     *   a <em>smart</em> content type application based on template name.
     * </p>
     * <p>
     *   When forced, the configured content type ({@link #setForceContentType(boolean)})  will
     *   be applied even if the template name ends in a known suffix:
     *   {@code .html}, {@code .htm}, {@code .xhtml},
     *   {@code .xml}, {@code .js}, {@code .json},
     *   {@code .css}, {@code .rss}, {@code .atom}, {@code .txt}.
     * </p>
     * <p>Default value is <b>{@code false}</b></p>.
     *
     * @param forceContentType whether the configured template mode should be forced or not.
     * @since 3.0.6
     */
    public void setForceContentType(final boolean forceContentType) {
        this.forceContentType = forceContentType;
        this.forceContentTypeSet = true;
    }


    /*
     * Internally used (by ThymeleafViewResolver) in order to know whether a value
     * for the "force content type" flag has been explicitly set or not.
     * @since 3.0.6
     */
    protected boolean isForceContentTypeSet() {
        return this.forceContentTypeSet;
    }

    
	
    /**
     * <p>
     *   Returns the character encoding set to be used for rendering this view.
     * </p>
     * <p>
     *   Many times, character encoding is specified as a part of the <i>content
     *   type</i> using the {@link #setContentType(String)} method, but this is not mandatory,
     *   and it could be that only the MIME type is specified that way, thus allowing
     *   to set the character encoding using the {@link #setCharacterEncoding(String)}
     *   counterpart of this getter method.
     * </p>
     * 
     * @return the character encoding.
     */
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    

    /**
     * <p>
     *   Specifies the character encoding to be set into the response when
     *   the view is rendered.
     * </p>
     * <p>
     *   Many times, character encoding is specified as a part of the <i>content
     *   type</i> using the {@link #setContentType(String)} method, but this is not mandatory,
     *   and it could be that only the MIME type is specified that way, thus allowing
     *   to set the character encoding using this method.
     * </p>
     * 
     * @param characterEncoding the character encoding to be used (e.g. {@code UTF-8},
     *        {@code ISO-8859-1}, etc.)
     */
    public void setCharacterEncoding(final String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }


    /**
     * <p>
     *   Returns whether Thymeleaf should start producing output &ndash;and sending it to the web server's output
     *   buffers&ndash; as soon as possible, outputting partial results while processing as they become available so
     *   that they can potentially be sent to the client (browser) before processing of the whole template has
     *   completely finished.
     * </p>
     * <p>
     *   If set to {@code false}, no fragments of template result will be sent to the web server's
     *   output buffers until Thymeleaf completely finishes processing the template and generating
     *   the corresponding output. Only once finished will output start to be written to the web server's
     *   output buffers, and therefore sent to the clients.
     * </p>
     * <p>
     *   Note that setting this to {@code false} is <strong>not recommended for most
     *   scenarios</strong>, as it can (very) significantly increase the amount of memory used per
     *   template execution. Only modify this setting if you know what you are doing. A typical
     *   scenario in which setting this to {@code false} could be of use is when an application is
     *   suffering from UI rendering issues (flickering) at the browser due to incremental
     *   rendering of very large templates.
     * </p>
     * <p>
     *   Default value is {@code true}.
     * </p>
     *
     * @return whether to start producing output as soon as possible while processing or not (default: {@code true}).
     * @since 3.0.10
     */
    public boolean getProducePartialOutputWhileProcessing() {
        return this.producePartialOutputWhileProcessing;
    }


    /**
     * <p>
     *   Sets whether Thymeleaf should start producing output &ndash;and sending it to the web server's output
     *   buffers&ndash; as soon as possible, outputting partial results while processing as they become available so
     *   that they can potentially be sent to the client (browser) before processing of the whole template has
     *   completely finished.
     * </p>
     * <p>
     *   If set to {@code false}, no fragments of template result will be sent to the web server's
     *   output buffers until Thymeleaf completely finishes processing the template and generating
     *   the corresponding output. Only once finished will output start to be written to the web server's
     *   output buffers, and therefore sent to the clients.
     * </p>
     * <p>
     *   Note that setting this to {@code false} is <strong>not recommended for most
     *   scenarios</strong>, as it can (very) significantly increase the amount of memory used per
     *   template execution. Only modify this setting if you know what you are doing. A typical
     *   scenario in which setting this to {@code false} could be of use is when an application is
     *   suffering from UI rendering issues (flickering) at the browser due to incremental
     *   rendering of very large templates.
     * </p>
     * <p>
     *   Default value is {@code true}.
     * </p>
     *
     * @param producePartialOutputWhileProcessing whether to start producing output as soon as possible while
     *                                            processing or not (default: {@code true}).
     * @since 3.0.10
     */
    public void setProducePartialOutputWhileProcessing(final boolean producePartialOutputWhileProcessing) {
        this.producePartialOutputWhileProcessing = producePartialOutputWhileProcessing;
        this.producePartialOutputWhileProcessingSet = true;
    }


    /*
     * Internally used (by ThymeleafViewResolver) in order to know whether a value
     * for the "producePartialOutputWhileProcessing" flag has been explicitly set or not.
     * @since 3.0.10
     */
    protected boolean isProducePartialOutputWhileProcessingSet() {
        return this.producePartialOutputWhileProcessingSet;
    }


    
    
    /**
     * <p>
     *   Returns the bean name.
     * </p>
     * 
     * @return the bean name.
     */
    public String getBeanName() {
        return this.beanName;
    }

    
    /**
     * <p>
     *   Sets the bean name.
     * </p>
     * 
     * @param beanName the new bean name.
     */
    public void setBeanName(final String beanName) {
        this.beanName = beanName;
    }

    

    /**
     * <p>
     *   Returns the name of the template being processed by this view object.
     * </p>
     * <p>
     *   This name will be specified in the same shape it will be resolved by the
     *   template resolvers (i.e. as it is returned by controllers, without any
     *   prefixes/suffixes).
     * </p>
     * 
     * @return the template name.
     */
    public String getTemplateName() {
        return this.templateName;
    }
	
    
    /**
     * <p>
     *   Sets the name of the template to be processed by this view object.
     * </p>
     * <p>
     *   This name will be specified in the same shape it will be resolved by the
     *   template resolvers (i.e. as it is returned by controllers, without any
     *   prefixes/suffixes).
     * </p>
     * 
     * @param templateName the template name
     */
	public void setTemplateName(final String templateName) {
		this.templateName = templateName;
	}

	

	/**
	 * <p>
	 *   Returns the locale to be used for template processing.
	 * </p>
	 * 
	 * @return the locale
	 */
    protected Locale getLocale() {
        return this.locale;
    }

    
    /**
     * <p>
     *   Sets the locale to be used for template processing. Usually,
     *   the View Resolver will set this automatically from user session
     *   / application data.
     * </p>
     * 
     * @param locale the locale to be used.
     */
    protected void setLocale(final Locale locale) {
        this.locale = locale;
        
    }
    
    
    
    /**
     * <p>
     *   Returns the template engine instance &ndash;a {@link SpringTemplateEngine} instance,
     *   specifically&ndash; to be used for processing the template specified by this view object.
     * </p>
     * 
     * @return the template engine instance
     */
    protected ISpringTemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }

    
    /**
     * <p>
     *   Sets the template engine instance &ndash;a {@link SpringTemplateEngine} instance,
     *   specifically&ndash; to be used for processing the template specified by this view object.
     * </p>
     * 
     * @param templateEngine the template engine instance to be used
     */
    protected void setTemplateEngine(final ISpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }


    

    /**
     * <p>
     *   Return the static variables, which will be available at the context
     *   every time this view is processed.
     * </p>
     * <p>
     *   These static variables are added to the context before the view is 
     *   processed, so that they can be referenced from the context like any 
     *   other context variables, for example: {@code ${myStaticVar}}.
     * </p>
     * 
     * @return the map of static variables to be set into view's execution.
     */
    public Map<String,Object> getStaticVariables() {
        if (this.staticVariables == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(this.staticVariables);
    }


    /**
     * <p>
     *   Add a new static variable.
     * </p>
     * <p>
     *   These static variables are added to the context before this view 
     *   is processed, so that they can be referenced from
     *   the context like any other context variables, for example:
     *   {@code ${myStaticVar}}.
     * </p>
     * 
     * @param name the name of the static variable
     * @param value the value of the static variable
     */
    public void addStaticVariable(final String name, final Object value) {
        if (this.staticVariables == null) {
            this.staticVariables = new HashMap<String, Object>(3, 1.0f);
        }
        this.staticVariables.put(name, value);
    }


    /**
     * <p>
     *   Sets a set of static variables, which will be available at the context
     *   when this view is processed.
     * </p>
     * <p>
     *   This method <b>does not overwrite</b> the existing static variables, it
     *   simply adds the ones specify to any variables already registered.
     * </p>
     * <p>
     *   These static variables are added to the context before this view is 
     *   processed, so that they can be referenced from
     *   the context like any other context variables, for example:
     *   {@code ${myStaticVar}}.
     * </p>
     * 
     * 
     * @param variables the set of variables to be added.
     */
    public void setStaticVariables(final Map<String, ?> variables) {
        if (variables != null) {
            if (this.staticVariables == null) {
                this.staticVariables = new HashMap<String, Object>(3, 1.0f);
            }
            this.staticVariables.putAll(variables);
        }
    }


    
    
    
    
    

    protected static void addRequestContextAsVariable(
            final Map<String,Object> model, final String variableName, final RequestContext requestContext) 
            throws ServletException {
        
        if (model.containsKey(variableName)) {
            throw new ServletException(
                    "Cannot expose request context in model attribute '" + variableName +
                    "' because an existing model object of the same name");
        }
        model.put(variableName, requestContext);
        
    }
    

    

}
