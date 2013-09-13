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
import org.thymeleaf.dom.DOMSelector;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.fragment.DOMSelectorFragmentSpec;
import org.thymeleaf.fragment.IFragmentSpec;


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
public class AjaxThymeleafView extends ThymeleafView implements AjaxEnabledView {

    
    private static final Logger vlogger = LoggerFactory.getLogger(AjaxThymeleafView.class);

    
    private static final String FRAGMENTS_PARAM = "fragments";
    

    private AjaxHandler ajaxHandler = null;



    
    public AjaxThymeleafView() {
        super();
    }
    
    
    

    public AjaxHandler getAjaxHandler() {
        return this.ajaxHandler;
    }

    
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
            final DOMSelector.INodeReferenceChecker nodeReferenceChecker =
                    createFragmentSignatureReferenceChecker(templateEngine.getConfiguration());

            for (final String fragmentToRender : fragmentsToRender) {
                
                if (fragmentToRender != null) {

                    String fragmentSelector = fragmentToRender;

                    if (fragmentSelector.length() > 3 &&
                            fragmentSelector.charAt(0) == '[' && fragmentSelector.charAt(fragmentSelector.length() - 1) == ']' &&
                            fragmentSelector.charAt(fragmentSelector.length() - 2) != '\'') {
                        // For legacy compatibility reasons, we allow fragment DOM Selector expressions to be specified
                        // between brackets. Just remove them.
                        fragmentSelector = fragmentSelector.substring(1, fragmentSelector.length() - 1);
                    }


                    final IFragmentSpec fragmentSpec = new DOMSelectorFragmentSpec(fragmentSelector, nodeReferenceChecker);

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
    
    
    


}
