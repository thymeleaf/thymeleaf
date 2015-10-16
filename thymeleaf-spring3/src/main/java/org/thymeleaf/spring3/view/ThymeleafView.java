/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebExpressionContext;
import org.thymeleaf.spring3.expression.ThymeleafEvaluationContext;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;
import org.thymeleaf.standard.expression.FragmentSelectionUtils;
import org.thymeleaf.standard.expression.ParsedFragmentSelection;
import org.thymeleaf.standard.expression.ProcessedFragmentSelection;


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

    /*
     * If this is not null, we are using Spring 3.1+ and there is the possibility
     * to automatically add @PathVariable's to models. This will be computed at class
     * initialization time.
     */
    private static final String pathVariablesSelector;
    
    private String[] markupSelectors = null;


    
    static {
        
        /*
         * Compute whether we can obtain @PathVariable's from the request and add them
         * automatically to the model (Spring 3.1+) 
         */
        
        String pathVariablesSelectorValue = null;
        try {
            // We are looking for the value of the View.PATH_VARIABLES constant, which is a String
            final Field pathVariablesField =  View.class.getDeclaredField("PATH_VARIABLES");
            pathVariablesSelectorValue = (String) pathVariablesField.get(null);
        } catch (final NoSuchFieldException ignored) {
            pathVariablesSelectorValue = null;
        } catch (final IllegalAccessException ignored) {
            pathVariablesSelectorValue = null;
        }
        pathVariablesSelector = pathVariablesSelectorValue;
    }
    
    
    

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
     *   Returns the markup selector defining the part of the template that should
     *   be processed.
     * </p>
     * <p>
     *   This selector will be used for selecting the section of the template
     *   that should be processed, discarding the rest of the template. If null,
     *   the whole template will be processed.
     * </p>
     * <p>
     *   Subclasses of {@link ThymeleafView} might choose not to honor this parameter,
     *   disallowing the processing of template fragments.
     * </p>
     * 
     * @return the markup selector currently set, or null of no fragment has been
     *         specified yet.
     *
     * @since 3.0.0
     */
    public String getMarkupSelector() {
        return (this.markupSelectors == null || this.markupSelectors.length == 0? null : this.markupSelectors[0]);
    }

    

    /**
     * <p>
     *   Sets the markup selector defining the part of the template that should
     *   be processed.
     * </p>
     * <p>
     *   This selector will be used for selecting the section of the template
     *   that should be processed, discarding the rest of the template. If null,
     *   the whole template will be processed.
     * </p>
     * <p>
     *   Subclasses of {@link ThymeleafView} might choose not to honor this parameter,
     *   disallowing the processing of template fragments.
     * </p>
     * 
     * @param markupSelector the markup selector to be set.
     *        
     * @since 3.0.0
     */
    public void setMarkupSelector(final String markupSelector) {
        this.markupSelectors =
                (markupSelector == null || markupSelector.trim().length() == 0? null : new String[] { markupSelector.trim() });
    }

    
    
    
    


    public void render(final Map<String, ?> model, final HttpServletRequest request, final HttpServletResponse response) 
            throws Exception {
        renderFragment(this.markupSelectors, model, request, response);
    }
	


    protected void renderFragment(final String[] markupSelectorsToRender, final Map<String, ?> model, final HttpServletRequest request,
            final HttpServletResponse response) 
            throws Exception {

        final ServletContext servletContext = getServletContext() ;
        final String viewTemplateName = getTemplateName();
        final ITemplateEngine viewTemplateEngine = getTemplateEngine();

        if (viewTemplateName == null) {
            throw new IllegalArgumentException("Property 'templateName' is required");
        }
        if (getLocale() == null) {
            throw new IllegalArgumentException("Property 'locale' is required");
        }
        if (viewTemplateEngine == null) {
            throw new IllegalArgumentException("Property 'templateEngine' is required");
        }


        final Map<String, Object> mergedModel = new HashMap<String, Object>(30);
        final Map<String, Object> templateStaticVariables = getStaticVariables();
        if (templateStaticVariables != null) {
            mergedModel.putAll(templateStaticVariables);
        }
        if (pathVariablesSelector != null) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> pathVars = (Map<String, Object>) request.getAttribute(pathVariablesSelector);
            if (pathVars != null) {
                mergedModel.putAll(pathVars);
            }
        }
        if (model != null) {
            mergedModel.putAll(model);
        }

        final ApplicationContext applicationContext = getApplicationContext();

        final RequestContext requestContext = 
                new RequestContext(request, response, getServletContext(), mergedModel);
        
        // For compatibility with ThymeleafView
        addRequestContextAsVariable(mergedModel, SpringContextVariableNames.SPRING_REQUEST_CONTEXT, requestContext);
        // For compatibility with AbstractTemplateView
        addRequestContextAsVariable(mergedModel, AbstractTemplateView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE, requestContext);


        // Expose Thymeleaf's own evaluation context as a model variable
        //
        // Note Spring's EvaluationContexts are NOT THREAD-SAFE (in exchange for SpelExpressions being thread-safe).
        // That's why we need to create a new EvaluationContext for each request / template execution, even if it is
        // quite expensive to create because of requiring the initialization of several ConcurrentHashMaps.
        final ConversionService conversionService =
                (ConversionService) request.getAttribute(ConversionService.class.getName()); // might be null!
        final ThymeleafEvaluationContext evaluationContext =
                new ThymeleafEvaluationContext(applicationContext, conversionService);
        mergedModel.put(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);

        
        final WebExpressionContext context =
                new WebExpressionContext(viewTemplateEngine.getConfiguration(), request, response, servletContext, getLocale(), mergedModel);


        final String templateName;
        final String[] markupSelectors;
        if (!viewTemplateName.contains("::")) {
            // No fragment specified at the template name

            templateName = viewTemplateName;
            markupSelectors = null;

        } else {
            // Template name contains a fragment name, so we should parse it as such

            final ParsedFragmentSelection parsedFragmentSelection =
                    FragmentSelectionUtils.parseFragmentSelection(context, viewTemplateName);
            if (parsedFragmentSelection == null) {
                throw new IllegalArgumentException("Invalid template name specification: '" + viewTemplateName + "'");
            }

            final ProcessedFragmentSelection processedFragmentSelection =
                    FragmentSelectionUtils.processFragmentSelection(context, parsedFragmentSelection);

            templateName = processedFragmentSelection.getTemplateName();
            markupSelectors = new String[] {processedFragmentSelection.getFragmentSelector()};
            final Map<String,Object> nameFragmentParameters = processedFragmentSelection.getFragmentParameters();

            if (nameFragmentParameters != null) {

                if (FragmentSelectionUtils.parameterNamesAreSynthetic(nameFragmentParameters.keySet())) {
                    // We cannot allow synthetic parameters because there is no way to specify them at the template
                    // engine execution!
                    throw new IllegalArgumentException(
                            "Parameters in a view specification must be named (non-synthetic): '" + viewTemplateName + "'");
                }

                context.setVariables(nameFragmentParameters);

            }


        }


        final String templateContentType = getContentType();
        final Locale templateLocale = getLocale();
        final String templateCharacterEncoding = getCharacterEncoding();


        final String[] processMarkupSelectors;
        if (markupSelectors != null && markupSelectors.length > 0) {
            if (markupSelectorsToRender != null && markupSelectorsToRender.length > 0) {
                throw new IllegalArgumentException(
                        "A markup selector has been specified (" + Arrays.asList(markupSelectors) + ") for a view " +
                        "that was already being executed as a fragment (" + Arrays.asList(markupSelectorsToRender) + "). " +
                        "Only one fragment selection is allowed.");
            }
            processMarkupSelectors = markupSelectors;
        } else {
            if (markupSelectorsToRender != null && markupSelectorsToRender.length > 0) {
                processMarkupSelectors = markupSelectorsToRender;
            } else {
                processMarkupSelectors = null;
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
        
        viewTemplateEngine.process(templateName, processMarkupSelectors, context, response.getWriter());
        
    }



}
