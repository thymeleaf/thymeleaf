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
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.spring3.SpringTemplateEngine;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class ThymeleafViewResolver 
        extends AbstractCachingViewResolver 
        implements Ordered {

    
    private static final Logger vrlogger = LoggerFactory.getLogger(ThymeleafViewResolver.class);
    
    
    public static final String REDIRECT_URL_PREFIX = "redirect:";
    public static final String FORWARD_URL_PREFIX = "forward:";

    private boolean redirectContextRelative = true;
    private boolean redirectHttp10Compatible = true;
    
    private String[] viewNames = null;
    private String[] excludedViewNames = null;
    private int order = Integer.MAX_VALUE;


    private final Map<String, Object> staticVariables = new LinkedHashMap<String, Object>();
    private String contentType = null;
    private String characterEncoding = null;
    
    private SpringTemplateEngine templateEngine;


    
    public ThymeleafViewResolver() {
        super();
    }
    
    

    
    
    public SpringTemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }


    public void setTemplateEngine(final SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    
    
    public Map<String,Object> getStaticVariables() {
        return Collections.unmodifiableMap(this.staticVariables);
    }


    public void addStaticVariable(final String name, final Object value) {
        this.staticVariables.put(name, value);
    }


    public void setStaticVariables(final Map<String, ?> variables) {
        if (variables != null) {
            for (Map.Entry<String, ?> entry : variables.entrySet()) {
                addStaticVariable(entry.getKey(), entry.getValue());
            }
        }
    }



    public void setOrder(final int order) {
        this.order = order;
    }


    public int getOrder() {
        return this.order;
    }
    
    

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }


    public String getContentType() {
        return this.contentType;
    }
    


    public void setCharacterEncoding(final String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }


    public String getCharacterEncoding() {
        return this.characterEncoding;
    }
    
    

    
    public void setRedirectContextRelative(final boolean redirectContextRelative) {
        this.redirectContextRelative = redirectContextRelative;
    }

    
    public boolean isRedirectContextRelative() {
        return this.redirectContextRelative;
    }

    
    
    
    public void setRedirectHttp10Compatible(final boolean redirectHttp10Compatible) {
        this.redirectHttp10Compatible = redirectHttp10Compatible;
    }

    
    public boolean isRedirectHttp10Compatible() {
        return this.redirectHttp10Compatible;
    }
    
    

    
    public void setViewNames(final String[] viewNames) {
        this.viewNames = viewNames;
    }


    public String[] getViewNames() {
        return this.viewNames;
    }
    
    

    
    public void setExcludedViewNames(final String[] excludedViewNames) {
        this.excludedViewNames = excludedViewNames;
    }


    public String[] getExcludedViewNames() {
        return this.excludedViewNames;
    }
    
    
    

    private boolean canHandle(final String viewName) {
        final String[] viewNamesToBeProcessed = getViewNames();
        final String[] viewNamesNotToBeProcessed = getExcludedViewNames();
        return ((viewNamesToBeProcessed == null || PatternMatchUtils.simpleMatch(viewNamesToBeProcessed, viewName)) &&
                (viewNamesNotToBeProcessed == null || !PatternMatchUtils.simpleMatch(viewNamesNotToBeProcessed, viewName)));
    }
    
    
    
    
    @Override
    protected View createView(final String viewName, final Locale locale) throws Exception {
        if (!canHandle(viewName)) {
            vrlogger.trace("[THYMELEAF] View {} cannot be handled by ThymeleafViewResolver. Passing on to the next resolver in the chain", viewName);
            return null;
        }
        if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
            vrlogger.trace("[THYMELEAF] View {} is a redirect, and will not be handled directly by ThymeleafViewResolver", viewName);
            String redirectUrl = viewName.substring(REDIRECT_URL_PREFIX.length());
            return new RedirectView(redirectUrl, isRedirectContextRelative(), isRedirectHttp10Compatible());
        }
        if (viewName.startsWith(FORWARD_URL_PREFIX)) {
            vrlogger.trace("[THYMELEAF] View {} is a forward, and will not be handled directly by ThymeleafViewResolver", viewName);
            String forwardUrl = viewName.substring(FORWARD_URL_PREFIX.length());
            return new InternalResourceView(forwardUrl);
        }
        vrlogger.trace("[THYMELEAF] View {} will be handled by ThymeleafViewResolver and a ThymeleafView instance will be created for it", viewName);
        return loadView(viewName, locale);
    }
    
    
    
    
    @Override
    protected View loadView(final String viewName, final Locale locale) throws Exception {
        
        final AutowireCapableBeanFactory beanFactory = getApplicationContext().getAutowireCapableBeanFactory();
        
        ThymeleafView view = BeanUtils.instantiateClass(ThymeleafView.class);

        if (beanFactory.containsBean(viewName)) {
            view = (ThymeleafView) beanFactory.configureBean(view, viewName);
        } else {
            view = (ThymeleafView) beanFactory.initializeBean(view, viewName);
        }

        view.setTemplateEngine(getTemplateEngine());
        view.setTemplateName(viewName);
        view.setStaticVariables(getStaticVariables());
        
        
        if (view.getContentType() == null && getContentType() != null) {
            view.setContentType(getContentType());
        }
        if (view.getLocale() == null && locale != null) {
            view.setLocale(locale);
        }
        if (view.getCharacterEncoding() == null && getCharacterEncoding() != null) {
            view.setCharacterEncoding(getCharacterEncoding());
        }
        
        return view;
        
    }

    
}
