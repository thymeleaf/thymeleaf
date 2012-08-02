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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.js.ajax.AjaxHandler;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.fragment.DOMSelectorFragmentSpec;
import org.thymeleaf.fragment.ElementAndAttributeNameFragmentSpec;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.spring3.dialect.SpringStandardDialect;
import org.thymeleaf.standard.processor.attr.StandardFragmentAttrProcessor;


/**
 * <p>
 *   Subclass of {@link ThymeleafView} adding compatibility with AJAX events in
 *   Spring JavaScript (part of Spring WebFlow). This allows this View implementation
 *   to be able to return only <i>fragments</i> of the page.
 * </p>
 * <p>
 *   These rendering of fragments is used, for example, in Spring WebFlow's &lt;render&gt;
 *   instructions (though not only).
 * </p>
 * <p>
 *   This view searches for a comma-separated list of <i>fragment names</i> in a request
 *   parameter called <kbd>fragments</kbd>, and for each of them:
 * </p>
 * <ul>
 *   <li>If it is not a DOM selector, a <kbd>th:fragment</kdb> attribute will be used
 *       for designing the fragment to be rendered.</li>
 *   <li>If it is a DOM selector, it will be used for selecting the fragment to be rendered,
 *       without looking for any attributes. DOM Selectors are specified between brackets, 
 *       like <kbd>[//div[@class='changeit']]</kbd></li>
 * </ul>
*
* @author Daniel Fern&aacute;ndez
* 
* @since 2.0.11
*
*/
public class AjaxThymeleafView extends ThymeleafView {

    
    private static final Logger vlogger = LoggerFactory.getLogger(AjaxThymeleafView.class);

    
    private static final String FRAGMENTS_PARAM = "fragments";
    

    private AjaxHandler ajaxHandler = null;



    
    public AjaxThymeleafView() {
        super();
    }
    
    
    

    /**
     * <p>
     *   Return the AJAX handler (from Spring Javascript) used
     *   to determine whether a request is an AJAX request or not.
     * </p>
     * <p>
     *   This view class should be used with an instance of
     *   {@link AjaxThymeleafViewResolver} or any of its subclasses,
     *   so that {@link #setAjaxHandler(AjaxHandler)} can be called by
     *   the resolver when resolving the view, setting the default
     *   AJAX handler being used.
     * </p>
     * 
     * @return the AJAX handler.
     */
    public AjaxHandler getAjaxHandler() {
        return this.ajaxHandler;
    }

    
    /**
     * <p>
     *   Sets the AJAX handler (from Spring Javascript) used
     *   to determine whether a request is an AJAX request or not.
     * </p>
     * <p>
     *   This view class should be used with an instance of
     *   {@link AjaxThymeleafViewResolver} or any of its subclasses,
     *   so that {@link #setAjaxHandler(AjaxHandler)} can be called by
     *   the resolver when resolving the view, setting the default
     *   AJAX handler being used.
     * </p>
     * 
     * @param ajaxHandler the AJAX handler.
     */
    public void setAjaxHandler(final AjaxHandler ajaxHandler) {
        this.ajaxHandler = ajaxHandler;
    }




    @Override
    public void render(
            final Map<String, ?> model, final HttpServletRequest request, final HttpServletResponse response) 
            throws Exception {
        

        final AjaxHandler templateAjaxHandler = getAjaxHandler();
        
        if (templateAjaxHandler == null) {
            throw new ConfigurationException("[THYMELEAF] AJAX Handler set into " +
                    AjaxThymeleafView.class.getSimpleName() + " instance for template " +
                    getTemplateName() + " is null.");
        }
        
        if (templateAjaxHandler.isAjaxRequest(request, response)) {
            
            final String[] fragmentsToRender = getRenderFragments(model, request, response);
            if (fragmentsToRender.length == 0) {
                vlogger.warn("[THYMELEAF] An Ajax request was detected, but no fragments were specified to be re-rendered.  "
                        + "Falling back to full page render.  This can cause unpredictable results when processing "
                        + "the ajax response on the client.");
                super.render(model, request, response);
                return;
            }

            if (getTemplateEngine() == null) {
                throw new IllegalArgumentException("Property 'templateEngine' is required");
            }

            final TemplateEngine templateEngine = getTemplateEngine();
            final String fragmentAttributeName = getFragmentAttributeName(templateEngine);
            
            for (final String fragmentToRender : fragmentsToRender) {
                
                if (fragmentToRender != null) {
                    
                    IFragmentSpec fragmentSpec = null;
                    if (fragmentToRender.length() > 2 && 
                            fragmentToRender.charAt(0) == '[' &&
                            fragmentToRender.charAt(fragmentToRender.length() - 1) == ']') {
                        // Fragment is a DOM selector
                        
                        fragmentSpec =
                                new DOMSelectorFragmentSpec(
                                        fragmentToRender.substring(1, fragmentToRender.length() - 1).trim());
                        
                    } else {
                        // Fragment is not a DOM selector, therefore it is a fragment name
                        
                        fragmentSpec =
                                new ElementAndAttributeNameFragmentSpec(null, fragmentAttributeName, fragmentToRender);
                        
                    }
                    
                    super.renderFragment(fragmentSpec, model, request, response);
                    
                }
                
            }
            
        } else {
            
            super.render(model, request, response);
            
        }
        
    }
    
    
    
    

    @SuppressWarnings({ "rawtypes", "unused" })
    protected String[] getRenderFragments(
            final Map model, final HttpServletRequest request, final HttpServletResponse response) {
        final String fragmentsParam = request.getParameter(FRAGMENTS_PARAM);
        final String[] renderFragments = StringUtils.commaDelimitedListToStringArray(fragmentsParam);
        return StringUtils.trimArrayElements(renderFragments);
    }
    
    
    
    
    
    
    private static String getStandardDialectPrefix(final TemplateEngine templateEngine) {
        
        for (final Map.Entry<String,IDialect> dialectByPrefix : templateEngine.getDialectsByPrefix().entrySet()) {
            final IDialect dialect = dialectByPrefix.getValue();
            if (SpringStandardDialect.class.isAssignableFrom(dialect.getClass())) {
                return dialectByPrefix.getKey();
            }
        }
        
        throw new ConfigurationException(
                "StandardDialect dialect has not been found. In order to use AjaxThymeleafView, you should configure " +
                "the " + SpringStandardDialect.class.getName() + " dialect at your Template Engine");
        
    }
    

    
    private static String getFragmentAttributeName(final TemplateEngine templateEngine) {
        // In most cases: "th:fragment"
        return getStandardDialectPrefix(templateEngine) + ":" + StandardFragmentAttrProcessor.ATTR_NAME;
    }
    

}
