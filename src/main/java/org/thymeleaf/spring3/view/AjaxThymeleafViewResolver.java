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


/**
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public class AjaxThymeleafViewResolver 
        extends ThymeleafViewResolver {

    
    private static final Logger vrlogger = LoggerFactory.getLogger(AjaxThymeleafViewResolver.class);
    
    
    
    @Override
    protected View createView(final String viewName, final Locale locale) throws Exception {

        if (!canHandle(viewName, locale)) {
            return null;
        }

        // Check for special "redirect:" prefix.
        if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
            vrlogger.trace(
                    "[THYMELEAF] View {} is a redirect. An AJAX-enabled RedirectView implementation will " +
            		"be handling the request.", viewName);
            String redirectUrl = viewName.substring(REDIRECT_URL_PREFIX.length());
            return new AjaxRedirectView(redirectUrl, isRedirectContextRelative(), isRedirectHttp10Compatible());
        }
        
        return super.createView(viewName, locale);
        
    }

    
    

    
    private static class AjaxRedirectView extends RedirectView {
        
        private static final Logger vlogger = LoggerFactory.getLogger(AjaxRedirectView.class);

        private AjaxHandler ajaxHandler = new SpringJavascriptAjaxHandler();

        public AjaxRedirectView(final String redirectUrl, final boolean redirectContextRelative, 
                final boolean redirectHttp10Compatible) {
            super(redirectUrl, redirectContextRelative, redirectHttp10Compatible);
        }

        protected void sendRedirect(final HttpServletRequest request, final HttpServletResponse response, 
                final String targetUrl, final boolean http10Compatible) 
                throws IOException {
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
