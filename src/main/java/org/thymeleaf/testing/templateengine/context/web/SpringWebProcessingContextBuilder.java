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
package org.thymeleaf.testing.templateengine.context.web;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.testable.ITest;



public class SpringWebProcessingContextBuilder extends WebProcessingContextBuilder {

    /**
     * @deprecated Not needed anymore. All candidate objects in model will now be automatically bound.
     */
    @Deprecated
    public final static String DEFAULT_BINDING_VARIABLE_NAME = "binding";

    public final static String DEFAULT_BINDING_MODEL_VARIABLE_NAME = "model";
    
    public final static String DEFAULT_APPLICATION_CONTEXT_CONFIG_LOCATION = "classpath:applicationContext.xml";


    private String applicationContextConfigLocation = DEFAULT_APPLICATION_CONTEXT_CONFIG_LOCATION;

    private boolean shareAppContextForAllTests = false;
    private String sharedContextConfigLocation = null;
    private WebApplicationContext sharedApplicationContext = null;
    





    public SpringWebProcessingContextBuilder() {
        super();
    }





    public String getApplicationContextConfigLocation() {
        return this.applicationContextConfigLocation;
    }

    public void setApplicationContextConfigLocation(final String applicationContextConfigLocation) {
        this.applicationContextConfigLocation = applicationContextConfigLocation;
    }




    public boolean getShareAppContextForAllTests() {
        return shareAppContextForAllTests;
    }

    public void setShareAppContextForAllTests(final boolean shareAppContextForAllTests) {
        this.shareAppContextForAllTests = shareAppContextForAllTests;
    }




    @Override
    protected final void doAdditionalVariableProcessing(
            final ITest test,
            final HttpServletRequest request, final HttpServletResponse response, final ServletContext servletContext,
            final Locale locale, final Map<String,Object> variables) {

        /*
         * APPLICATION CONTEXT
         */
        final WebApplicationContext appCtx =
                createApplicationContext(
                        test, request, response, servletContext, locale, variables);
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, appCtx);


        /*
         * CONVERSION SERVICE
         */
        final ConversionService conversionService = getConversionService(appCtx); // can be null!

        /*
         * REQUEST CONTEXT
         */
        final RequestContext requestContext =
                new RequestContext(request, response, servletContext, variables);
        variables.put(AbstractTemplateView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE, requestContext);


        /*
         * SPRING-VERSION-SPECIFIC INITIALIZATIONS
         */
        SpringVersionSpecificContextInitialization.
                versionSpecificAdditionalVariableProcessing(appCtx, conversionService, variables);


        /*
         * INITIALIZE VARIABLE BINDINGS (Add BindingResults when needed)
         */
        initializeBindingResults(test, conversionService, locale, variables);


        /*
         * FURTHER SCENARIO-SPECIFIC INITIALIZATIONS
         */
        initSpring(appCtx, test, request, response, servletContext, locale, variables);
        
    }



    protected IWebContext doCreateWebContextInstance(
            final ITest test,
            final HttpServletRequest request, final HttpServletResponse response, final ServletContext servletContext,
            final Locale locale, final Map<String,Object> variables) {

        final ApplicationContext appCtx =
                (ApplicationContext) servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        return SpringVersionSpecificContextInitialization.
                versionSpecificCreateContextInstance(appCtx, request, response, servletContext, locale, variables);

    }



    /**
     * <p>
     *   Returns the name of the variables that must be considered "binding models", usually
     *   those that serve as form-backing beans.
     * </p>
     * <p>
     *   Default behaviour is:
     * </p>
     * <ul>
     *   <li>Look for a context variable called <tt>binding</tt>. If this variable exists,
     *       it will be considered to contain the name (single-valued) or names (list) of
     *       the binding variables (as literal/s).</li>
     *   <li>If <tt>binding</tt> does not exist, look for a context variable called <tt>model</tt>.
     *       The object contained in that variable will be considered to be the binding model itself.</li>
     * </ul>
     * 
     * @return the binding variable names
     * @deprecated Not needed anymore. All valid candidate objects in model will now be automatically bound.
     */
    @Deprecated
    @SuppressWarnings("unused")
    protected List<String> getBindingVariableNames(
            final ITest test,
            final HttpServletRequest request, final HttpServletResponse response, final ServletContext servletContext,
            final Locale locale, final Map<String,Object> variables) {
        
        final Object bindingObj = variables.get(DEFAULT_BINDING_VARIABLE_NAME);
        
        if (bindingObj == null) {
            return Collections.singletonList(DEFAULT_BINDING_MODEL_VARIABLE_NAME);
        }
        
        if (bindingObj instanceof List) {
            final List<String> variableNames = new ArrayList<String>();
            for (final Object bindingObjValue : ((List<?>)bindingObj)) {
                variableNames.add(bindingObjValue != null? bindingObjValue.toString() : null);
            }
            return variableNames;
        }
        
        return Collections.singletonList(bindingObj.toString());
        
    }
    
    
    
    @SuppressWarnings("unused")
    protected void initBinder(
            final String bindingVariableName, final Object bindingObject,
            final ITest test, final DataBinder dataBinder, final Locale locale, 
            final Map<String,Object> variables) {
        // Nothing to be done. Meant to be overridden.
    }
    
    
    
    @SuppressWarnings("unused")
    protected void initBindingResult(
            final String bindingVariableName, final Object bindingObject,
            final ITest test, final BindingResult bindingResult, final Locale locale, 
            final Map<String,Object> variables) {
        // Nothing to be done. Meant to be overridden.
    }
    
    
    @SuppressWarnings("unused")
    protected WebApplicationContext createApplicationContext(
            final ITest test,
            final HttpServletRequest request, final HttpServletResponse response, final ServletContext servletContext,
            final Locale locale, final Map<String,Object> variables) {

        final String nullSafeConfigLocation =
                this.applicationContextConfigLocation == null? "null" : this.applicationContextConfigLocation;

        if (this.shareAppContextForAllTests) {
            if (this.sharedContextConfigLocation != null) {
                if (!this.sharedContextConfigLocation.equals(nullSafeConfigLocation)) {
                    throw new RuntimeException(
                            "Invalid configuration for context builder. Builder is configured to share Spring " +
                            "application context across executions, but more than one different context config " +
                            "locations are being used, so this option cannot be used.");
                }
                return this.sharedApplicationContext;
            }
        }


        if (this.applicationContextConfigLocation == null) {
            final WebApplicationContext appCtx = createEmptyStaticApplicationContext(servletContext);
            if (this.shareAppContextForAllTests) {
                this.sharedContextConfigLocation = nullSafeConfigLocation;
                this.sharedApplicationContext = appCtx;
            }
            return appCtx;
        }
        
        final XmlWebApplicationContext appCtx = new XmlWebApplicationContext();
        
        appCtx.setServletContext(servletContext);
        appCtx.setConfigLocation(this.applicationContextConfigLocation);
        
        try {
            appCtx.refresh();
        } catch (final BeanDefinitionStoreException e) {
            if (e.getCause() != null && (e.getCause() instanceof FileNotFoundException)) {
                throw new TestEngineExecutionException(
                        "Cannot find ApplicationContext config location " +
                        "\"" + this.applicationContextConfigLocation + "\". If your tests don't need " +
                        "to define any Spring beans, set the 'applicationContextConfigLocation' field of " +
                        "your ProcessingContext builder to null.", e);
            }
            throw e;
        }

        if (this.shareAppContextForAllTests) {
            this.sharedContextConfigLocation = nullSafeConfigLocation;
            this.sharedApplicationContext = appCtx;
        }

        return appCtx;

    }
    
    
    
    private static WebApplicationContext createEmptyStaticApplicationContext(final ServletContext servletContext) {
        final StaticWebApplicationContext applicationContext = new StaticWebApplicationContext();
        applicationContext.setServletContext(servletContext);
        return applicationContext;
    }
    
    
    
    
    
    @SuppressWarnings("unused")
    protected void initSpring(
            final ApplicationContext applicationContext,
            final ITest test,
            final HttpServletRequest request, final HttpServletResponse response, final ServletContext servletContext,
            final Locale locale, final Map<String,Object> variables) {
        // Nothing to be done. Meant to be overridden.
    }
    



    private ConversionService getConversionService(final ApplicationContext applicationContext) {

        if (applicationContext == null) {
            return null;
        }

        final Map<String, ConversionService> conversionServices =
                applicationContext.getBeansOfType(ConversionService.class);

        if (conversionServices.size() == 0) {
            return null;
        }

        return (ConversionService) conversionServices.values().toArray()[0];

    }



    private void initializeBindingResults(
            final ITest test, final ConversionService conversionService,
            final Locale locale, final Map<String,Object> variables) {

        /*
         * This method tries to mirror (more or less) what is made at the Spring
         * "ModelFactory.updateBindingResult(...)" method, which transparently adds BindingResult objects to the
         * model before handling it to the View.
         *
         * Without this, every object would have to be specifically bound in order to make conversion / form binding
         * available for it.
         *
         * All this is needed in order to replicate Spring MVC model behaviours in an offline environment like the
         * testing framework.
         */

        final List<String> variableNames = new ArrayList<String>(variables.keySet());
        for (final String variableName : variableNames) {
            final Object bindingObject = variables.get(variableName);
            if (isBindingCandidate(variableName, bindingObject)) {
                final String bindingVariableName = BindingResult.MODEL_KEY_PREFIX + variableName;
                if (!variables.containsKey(bindingVariableName)) {
                    final WebDataBinder dataBinders =
                            createBinding(
                                    test, variableName, bindingVariableName, bindingObject,
                                    conversionService, locale, variables);
                    variables.put(bindingVariableName, dataBinders.getBindingResult());
                }
            }
        }

    }



    private static boolean isBindingCandidate(final String variableName, final Object bindingObject) {
        if (variableName.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
            return false;
        }
        return (bindingObject != null && !bindingObject.getClass().isArray() && !(bindingObject instanceof Collection) &&
                !(bindingObject instanceof Map) && !BeanUtils.isSimpleValueType(bindingObject.getClass()));
    }


    private WebDataBinder createBinding(
            final ITest test,
            final String variableName, final String bindingVariableName, final Object bindingObject,
            final ConversionService conversionService, final Locale locale, final Map<String,Object> variables) {

        final WebDataBinder dataBinder = new WebDataBinder(bindingObject, bindingVariableName);
        dataBinder.setConversionService(conversionService);

        /*
         * The following are thymeleaf-testing specific calls in order to allow further customizations of the binders
         * being created.
         */
        final Map<String,Object> unmodifiableVariables =
                Collections.unmodifiableMap(variables); // We are iterating it!
        initBinder(variableName, bindingObject, test, dataBinder, locale, unmodifiableVariables);
        initBindingResult(variableName, bindingObject, test, dataBinder.getBindingResult(), locale, unmodifiableVariables);

        return dataBinder;

    }

    
}
