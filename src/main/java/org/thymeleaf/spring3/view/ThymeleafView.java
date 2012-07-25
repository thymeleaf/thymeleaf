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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.fragment.ChainedFragmentSpec;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.spring3.context.SpringWebContext;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;


/**
 * <p>
 *   Base implementation of the Spring MVC {@link org.springframework.web.servlet.View}
 *   interface.
 * </p>
 * <p>
 *   Views represent a template being executed, after being resolved (and
 *   instantiated) by a {@link org.springframework.web.servlet.ViewResolver}.
 * </p>
 * <p>
 *   This is the default view implementation resolved by {@link ThymeleafViewResolver}.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class ThymeleafView 
        extends AbstractThymeleafView {

    private IFragmentSpec fragmentSpec = null;


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
	    super(templateName);
	}

	
    
    
    /**
     * <p>
     *   Returns the fragment specification ({@link IFragmentSpec}) defining the part
     *   of the template that should be processed.
     * </p>
     * <p>
     *   This fragment spec will be used for selecting the section of the template
     *   that should be processed, discarding the rest of the template. If null,
     *   the whole template will be processed.
     * </p>
     * <p>
     *   Subclasses of {@link ThymeleafView} might choose not to honor this parameter,
     *   disallowing the processing of template fragments.
     * </p>
     * 
     * @return the fragment spec currently set, or null of no fragment has been
     *         specified yet.
     * 
     * @since 2.0.11
     */
    public IFragmentSpec getFragmentSpec() {
        return this.fragmentSpec;
    }

    

    /**
     * <p>
     *   Sets the fragment specification ({@link IFragmentSpec}) defining the part
     *   of the template that should be processed.
     * </p>
     * <p>
     *   This fragment spec will be used for selecting the section of the template
     *   that should be processed, discarding the rest of the template. If null,
     *   the whole template will be processed.
     * </p>
     * <p>
     *   Subclasses of {@link ThymeleafView} might choose not to honor this parameter,
     *   disallowing the processing of template fragments.
     * </p>
     * 
     * @param fragmentSpec the fragment specification to be set.
     *        
     * @since 2.0.11
     */
    public void setFragmentSpec(final IFragmentSpec fragmentSpec) {
        this.fragmentSpec = fragmentSpec;
    }

    
    
    
    


    public void render(final Map<String, ?> model, final HttpServletRequest request, final HttpServletResponse response) 
            throws Exception {
        renderFragment(null, model, request, response);
    }
	


    protected void renderFragment(final IFragmentSpec fragmentSpecToRender, final Map<String, ?> model, final HttpServletRequest request, 
            final HttpServletResponse response) 
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
        final Map<String, Object> templateStaticVariables = getStaticVariables();
        if (templateStaticVariables != null) {
            mergedModel.putAll(templateStaticVariables);
        }
        if (model != null) {
            mergedModel.putAll(model);
        }

        

        final RequestContext requestContext = 
                new RequestContext(request, response, getServletContext(), mergedModel);
        
        // For compatibility with ThymeleafView
        addRequestContextAsVariable(mergedModel, SpringContextVariableNames.SPRING_REQUEST_CONTEXT, requestContext);
        // For compatibility with AbstractTemplateView
        addRequestContextAsVariable(mergedModel, AbstractTemplateView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE, requestContext);
        

        
        final IWebContext context = 
                new SpringWebContext(request, response, servletContext , getLocale(), mergedModel, getApplicationContext());
        
        final TemplateEngine viewTemplateEngine = getTemplateEngine();
        
        final String templateContentType = getContentType();
        final Locale templateLocale = getLocale();
        final String templateCharacterEncoding = getCharacterEncoding();
        
        IFragmentSpec templateFragmentSpec = null;
        final IFragmentSpec viewFragmentSpec = getFragmentSpec();
        if (viewFragmentSpec == null) {
            templateFragmentSpec = fragmentSpecToRender;
        } else {
            if (fragmentSpecToRender == null) {
                templateFragmentSpec = viewFragmentSpec;
            } else {
                templateFragmentSpec =
                    new ChainedFragmentSpec(viewFragmentSpec, fragmentSpecToRender);
            }
        }
        

        response.setLocale(templateLocale);
        if (templateContentType != null) {
            response.setContentType(templateContentType);
        } else {
            response.setContentType(DEFAULT_CONTENT_TYPE);
        }
        if (templateCharacterEncoding != null) {
            response.setCharacterEncoding(templateCharacterEncoding);
        }
        
        viewTemplateEngine.process(getTemplateName(), context, templateFragmentSpec, response.getWriter());
        
    }



}
