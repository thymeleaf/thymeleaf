/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring3.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.spring3.context.SpringWebContext;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;


/**
 * <p>
 *   Implementation of the Spring MVC {@link org.springframework.web.servlet.View}
 *   interface.
 * </p>
 * <p>
 *   Views represent a template being executed, after being resolved (and
 *   instantiated) by a {@link org.springframework.web.servlet.ViewResolver}.
 * </p>
 * <p>
 *   This is the view implementation resolved by {@link ThymeleafViewResolver}.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Josh Long
 * 
 * @since 1.0
 *
 */
public class ThymeleafView 
        extends WebApplicationObjectSupport 
        implements View, BeanNameAware  {
    
    /**
     * <p>
     *   Default charset set to ISO-8859-1 for compatibility reasons with Spring's AbstractView.
     *   Value is "<tt>text/html;charset=ISO-8859-1</tt>".
     * <p>
     */
    public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=ISO-8859-1";
    

    
    private String beanName = null;
    private String contentType = DEFAULT_CONTENT_TYPE;
    private boolean contentTypeSet = false;
    private String characterEncoding = null;
    private TemplateEngine templateEngine = null;
	private String templateName = null;
    private Locale locale = null;
    private Map<String, Object> staticVariables = null;


    /**
     * <p>
     *   Creates a new instance of <tt>ThymeleafView</tt>.
     * </p>
     */
	protected ThymeleafView() {
	    super();
	}


	/**
	 * <p>
	 *   Creates a new instance of <tt>ThymeleafView</tt>, specifying the
	 *   template name.
	 * </p>
	 * 
	 * @param templateName the template name.
	 */
	protected ThymeleafView(final String templateName) {
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

    
    boolean isContentTypeSet() {
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

    void setLocale(final Locale locale) {
        this.locale = locale;
    }
    
    
    
    protected TemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }

    void setTemplateEngine(final TemplateEngine templateEngine) {
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
            this.staticVariables = new HashMap<String, Object>();
        }
        this.staticVariables.put(name, value);
    }


    public void setStaticVariables(final Map<String, ?> variables) {
        if (variables != null) {
            if (this.staticVariables == null) {
                this.staticVariables = new HashMap<String, Object>();
            }
            this.staticVariables.putAll(variables);
        }
    }


    
    
    
	


    public void render(final Map<String, ?> model, final HttpServletRequest request, final HttpServletResponse response) 
            throws Exception {

        ServletContext servletContext = getServletContext() ;

        if (getTemplateName() == null) {
            throw new IllegalArgumentException("Property 'templateName' is required");
        }
        if (getLocale() == null) {
            throw new IllegalArgumentException("Property 'locale' is required");
        }
        if (getTemplateEngine() == null) {
            throw new IllegalArgumentException("Property 'templateEngine' is required");
        }
        
        final Map<String, Object> mergedModel = new HashMap<String, Object>();
        if (this.staticVariables != null) {
            mergedModel.putAll(this.staticVariables);
        }
        if (model != null) {
            mergedModel.putAll(model);
        }

        
        if (mergedModel.containsKey(SpringContextVariableNames.SPRING_REQUEST_CONTEXT)) {
            throw new ServletException(
                    "Cannot expose request context in model attribute '" + SpringContextVariableNames.SPRING_REQUEST_CONTEXT +
                    "' because of an existing model object of the same name");
        }
        mergedModel.put(SpringContextVariableNames.SPRING_REQUEST_CONTEXT,
                new RequestContext(request, response, servletContext, mergedModel));
        
        
        
        final IWebContext context = new SpringWebContext(request,servletContext , getLocale(), mergedModel, getApplicationContext());
        
        final TemplateEngine viewTemplateEngine = getTemplateEngine();
        
        final String templateContentType = getContentType();
        final Locale templateLocale = getLocale();
        final String templateCharacterEncoding = getCharacterEncoding();

        response.setLocale(templateLocale);
        if (templateContentType != null) {
            response.setContentType(templateContentType);
        } else {
            response.setContentType(DEFAULT_CONTENT_TYPE);
        }
        if (templateCharacterEncoding != null) {
            response.setCharacterEncoding(templateCharacterEncoding);
        }
        
        viewTemplateEngine.process(getTemplateName(), context, response.getWriter());
        
    }
    
    

    
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(super.toString());
		sb.append("; TemplateName [").append(getTemplateName()).append("]");
		return sb.toString();
	}

}
