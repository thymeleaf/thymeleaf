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
package org.thymeleaf.spring5.webflow.view;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.js.ajax.AjaxHandler;
import org.springframework.js.ajax.SpringJavascriptAjaxHandler;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;


/**
 * <p>
 *   Subclass of {@link ThymeleafViewResolver} adding compatibility with AJAX-based events
 *   (redirects) in Spring WebFlow.
 * </p>
 * <p>
 *   <b>Important</b>: Spring WebFlow dependencies are OPTIONAL. If you are not using WebFlow
 *   in your application, then you should be using {@link ThymeleafViewResolver} directly.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.3
 *
 */
public class AjaxThymeleafViewResolver 
        extends ThymeleafViewResolver {

    
    private static final Logger vrlogger = LoggerFactory.getLogger(AjaxThymeleafViewResolver.class);
    

    private AjaxHandler ajaxHandler = new SpringJavascriptAjaxHandler();



    public AjaxThymeleafViewResolver() {
        super();
    }


    /**
     * <p>
     *   Return the AJAX handler (from Spring Javascript) used
     *   to determine whether a request is an AJAX request or not 
     *   in views resolved by this resolver.
     * </p>
     * <p>
     *   An instance of {@link SpringJavascriptAjaxHandler} is set by default.
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
     *   to determine whether a request is an AJAX request or not 
     *   in views resolved by this resolver.
     * </p>
     * <p>
     *   An instance of {@link SpringJavascriptAjaxHandler} is set by default.
     * </p>
     * 
     * @param ajaxHandler the AJAX handler.
     */
    public void setAjaxHandler(final AjaxHandler ajaxHandler) {
        this.ajaxHandler = ajaxHandler;
    }



    
    @Override
    protected View createView(final String viewName, final Locale locale) throws Exception {

        if (!canHandle(viewName, locale)) {
            return null;
        }
        
        if (this.ajaxHandler == null) {
            throw new ConfigurationException("[THYMELEAF] AJAX Handler set into " +
                    AjaxThymeleafViewResolver.class.getSimpleName() + " instance is null.");
        }

        // Check for special "redirect:" prefix.
        if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
            vrlogger.trace(
                    "[THYMELEAF] View {} is a redirect. An AJAX-enabled RedirectView implementation will " +
            		"be handling the request.", viewName);
            final String redirectUrl = viewName.substring(REDIRECT_URL_PREFIX.length());
            return new AjaxRedirectView(
                    this.ajaxHandler, redirectUrl, isRedirectContextRelative(), isRedirectHttp10Compatible());
        }
        
        final View view = super.createView(viewName, locale);
        
        if (view instanceof AjaxEnabledView) {
            // Set the AJAX handler into view, if it is an AjaxThymeleafView.

            final AjaxEnabledView ajaxEnabledView = (AjaxEnabledView) view;
            
            if (ajaxEnabledView.getAjaxHandler() == null && getAjaxHandler() != null) {
                ajaxEnabledView.setAjaxHandler(getAjaxHandler());
            }
            
        }
        
        return view;
        
    }

    
    

    
    private static class AjaxRedirectView extends RedirectView {
        
        private static final Logger vlogger = LoggerFactory.getLogger(AjaxRedirectView.class);

        private AjaxHandler ajaxHandler = new SpringJavascriptAjaxHandler();

        AjaxRedirectView(final AjaxHandler ajaxHandler, final String redirectUrl,
                final boolean redirectContextRelative, final boolean redirectHttp10Compatible) {
            super(redirectUrl, redirectContextRelative, redirectHttp10Compatible);
            this.ajaxHandler = ajaxHandler;
        }

        @Override
        protected void sendRedirect(final HttpServletRequest request, final HttpServletResponse response,
                final String targetUrl, final boolean http10Compatible) 
                throws IOException {
            
            if (this.ajaxHandler == null) {
                throw new ConfigurationException("[THYMELEAF] AJAX Handler set into " +
                        AjaxThymeleafViewResolver.class.getSimpleName() + " instance is null.");
            }
            
            if (this.ajaxHandler.isAjaxRequest(request, response)) {
                if (vlogger.isTraceEnabled()) { 
                    vlogger.trace(
                            "[THYMELEAF] RedirectView for URL \"{}\" is an AJAX request. AjaxHandler of class {} " +
                            "will be in charge of processing the request.", targetUrl, this.ajaxHandler.getClass().getName());
                }
                this.ajaxHandler.sendAjaxRedirect(targetUrl, request, response, false);
            } else {
                vlogger.trace(
                        "[THYMELEAF] RedirectView for URL \"{}\" is not an AJAX request. Request will be handled " +
                        "as a normal redirect", targetUrl);
                super.sendRedirect(request, response, targetUrl, http10Compatible);
            }
        }

    }
    
 
    
}
