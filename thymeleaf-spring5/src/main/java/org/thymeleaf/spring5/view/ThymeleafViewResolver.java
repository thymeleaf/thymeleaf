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
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.spring5.ISpringTemplateEngine;


/**
 * <p>
 *   Implementation of the Spring WebMVC {@link org.springframework.web.servlet.ViewResolver}
 *   interface.
 * </p>
 * <p>
 *   View resolvers execute after the controller ends its execution. They receive the name
 *   of the view to be processed and are in charge of creating (and configuring) the
 *   corresponding {@link View} object for it.
 * </p>
 * <p>
 *   The {@link View} implementations managed by this class are subclasses of 
 *   {@link AbstractThymeleafView}. By default, {@link ThymeleafView} is used.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.3
 *
 */
public class ThymeleafViewResolver 
        extends AbstractCachingViewResolver 
        implements Ordered {

    
    private static final Logger vrlogger = LoggerFactory.getLogger(ThymeleafViewResolver.class);
    
    
    /**
     * <p>
     *   Prefix to be used in view names (returned by controllers) for specifying an
     *   HTTP redirect.
     * </p>
     * <p>
     *   Value: {@code redirect:}
     * </p>
     */
    public static final String REDIRECT_URL_PREFIX = "redirect:";
    
    /**
     * <p>
     *   Prefix to be used in view names (returned by controllers) for specifying an
     *   HTTP forward.
     * </p>
     * <p>
     *   Value: {@code forward:}
     * </p>
     */
    public static final String FORWARD_URL_PREFIX = "forward:";

    private boolean redirectContextRelative = true;
    private boolean redirectHttp10Compatible = true;

    private boolean alwaysProcessRedirectAndForward = true;

    private boolean producePartialOutputWhileProcessing = AbstractThymeleafView.DEFAULT_PRODUCE_PARTIAL_OUTPUT_WHILE_PROCESSING;

    private Class<? extends AbstractThymeleafView> viewClass = ThymeleafView.class;   
    private String[] viewNames = null;
    private String[] excludedViewNames = null;
    private int order = Integer.MAX_VALUE;


    private final Map<String, Object> staticVariables = new LinkedHashMap<String, Object>(10);
    private String contentType = null;
    private boolean forceContentType = false;
    private String characterEncoding = null;
    
    private ISpringTemplateEngine templateEngine;



    /**
     * <p>
     *   Create an instance of ThymeleafViewResolver.
     * </p>
     */
    public ThymeleafViewResolver() {
        super();
    }
    
    


    /**
     * <p>
     *   Set the view class that should be used to create views. This must be a subclass
     *   of {@link AbstractThymeleafView}. The default value is {@link ThymeleafView}.
     * </p>
     * 
     * @param viewClass class that is assignable to the required view class
     *        (by default, {@link ThymeleafView}).
     */
    public void setViewClass(final Class<? extends AbstractThymeleafView> viewClass) {
        if (viewClass == null || !AbstractThymeleafView.class.isAssignableFrom(viewClass)) {
            throw new IllegalArgumentException(
                    "Given view class [" + (viewClass != null ? viewClass.getName() : null) +
                    "] is not of type [" + AbstractThymeleafView.class.getName() + "]");
        }
        this.viewClass = viewClass;
    }
    
    
    /**
     * <p>
     *   Return the view class to be used to create views.
     * </p>
     * 
     * @return the view class.
     */
    protected Class<? extends AbstractThymeleafView> getViewClass() {
        return this.viewClass;
    }
    
    
    /**
     * <p>
     *   Returns the Thymeleaf template engine instance to be used for the 
     *   execution of templates.
     * </p>
     *
     * @return the template engine being used for processing templates.
     */
    public ISpringTemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }


    /**
     * <p>
     *   Sets the Template Engine instance to be used for processing
     *   templates.
     * </p>
     *
     * @param templateEngine the template engine to be used
     */
    public void setTemplateEngine(final ISpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }
    

    
    /**
     * <p>
     *   Return the static variables, which will be available at the context
     *   every time a view resolved by this ViewResolver is processed.
     * </p>
     * <p>
     *   These static variables are added to the context by the view resolver
     *   before every view is processed, so that they can be referenced from
     *   the context like any other context variables, for example:
     *   {@code ${myStaticVar}}.
     * </p>
     * 
     * @return the map of static variables to be set into views' execution.
     */
    public Map<String,Object> getStaticVariables() {
        return Collections.unmodifiableMap(this.staticVariables);
    }


    /**
     * <p>
     *   Add a new static variable.
     * </p>
     * <p>
     *   These static variables are added to the context by the view resolver
     *   before every view is processed, so that they can be referenced from
     *   the context like any other context variables, for example:
     *   {@code ${myStaticVar}}.
     * </p>
     * 
     * @param name the name of the static variable
     * @param value the value of the static variable
     */
    public void addStaticVariable(final String name, final Object value) {
        this.staticVariables.put(name, value);
    }


    /**
     * <p>
     *   Sets a set of static variables, which will be available at the context
     *   every time a view resolved by this ViewResolver is processed.
     * </p>
     * <p>
     *   This method <b>does not overwrite</b> the existing static variables, it
     *   simply adds the ones specify to any variables already registered.
     * </p>
     * <p>
     *   These static variables are added to the context by the view resolver
     *   before every view is processed, so that they can be referenced from
     *   the context like any other context variables, for example:
     *   {@code ${myStaticVar}}.
     * </p>
     * 
     * 
     * @param variables the set of variables to be added.
     */
    public void setStaticVariables(final Map<String, ?> variables) {
        if (variables != null) {
            for (final Map.Entry<String, ?> entry : variables.entrySet()) {
                addStaticVariable(entry.getKey(), entry.getValue());
            }
        }
    }



    /**
     * <p>
     *   Specify the order in which this view resolver will be queried.
     * </p>
     * <p>
     *   Spring Web applications can have several view resolvers configured,
     *   and this {@code order} property established the order in which
     *   they will be queried for view resolution.
     * </p>
     * 
     * @param order the order in which this view resolver will be asked to resolve
     *        the view.
     */
    public void setOrder(final int order) {
        this.order = order;
    }


    /**
     * <p>
     *   Returns the order in which this view resolver will be queried.
     * </p>
     * <p>
     *   Spring Web applications can have several view resolvers configured,
     *   and this {@code order} property established the order in which
     *   they will be queried for view resolution.
     * </p>
     * 
     * @return the order
     */
    public int getOrder() {
        return this.order;
    }
    
    

    /**
     * <p>
     *   Sets the content type to be used when rendering views.
     * </p>
     * <p>
     *   This content type acts as a <i>default</i>, so that every view
     *   resolved by this resolver will use this content type unless there
     *   is a bean defined for such view that specifies a different content type.
     * </p>
     * <p>
     *   Therefore, individual views are allowed to specify their own content type
     *   regardless of the <i>application-wide</i> setting established here.
     * </p>
     * <p>
     *   If a content type is not specified (either here or at a specific view definition),
     *   {@link AbstractThymeleafView#DEFAULT_CONTENT_TYPE} will be used.
     * </p>
     * 
     * @param contentType the content type to be used.
     */
    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }


    
    /**
     * <p>
     *   Returns the content type that will be set into views resolved by this
     *   view resolver.
     * </p>
     * <p>
     *   This content type acts as a <i>default</i>, so that every view
     *   resolved by this resolver will use this content type unless there
     *   is a bean defined for such view that specifies a different content type.
     * </p>
     * <p>
     *   Therefore, individual views are allowed to specify their own content type
     *   regardless of the <i>application-wide</i> setting established here.
     * </p>
     * <p>
     *   If a content type is not specified (either at the view resolver or at a specific 
     *   view definition), {@link AbstractThymeleafView#DEFAULT_CONTENT_TYPE} will be used.
     * </p>
     * 
     * @return the content type currently configured
     */
    public String getContentType() {
        return this.contentType;
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
    }



    
    /**
     * <p>
     *   Specifies the character encoding to be set into the response when
     *   the view is rendered.
     * </p>
     * <p>
     *   Many times, character encoding is specified as a part of the <i>content
     *   type</i>, using the {@link #setContentType(String)} or 
     *   {@link AbstractThymeleafView#setContentType(String)}, but this is not mandatory,
     *   and it could be that only the MIME type is specified that way, thus allowing
     *   to set the character encoding using this method.
     * </p>
     * <p>
     *   As with {@link #setContentType(String)}, the value specified here acts as a 
     *   default in case no character encoding has been specified at the view itself.
     *   If a view bean exists with the name of the view to be processed, and this
     *   view has been set a value for its {@link AbstractThymeleafView#setCharacterEncoding(String)}
     *   method, the value specified at the view resolver has no effect.
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
     *   Returns the character encoding set to be used for all views resolved by
     *   this view resolver.
     * </p>
     * <p>
     *   Many times, character encoding is specified as a part of the <i>content
     *   type</i>, using the {@link #setContentType(String)} or 
     *   {@link AbstractThymeleafView#setContentType(String)}, but this is not mandatory,
     *   and it could be that only the MIME type is specified that way, thus allowing
     *   to set the character encoding using the {@link #setCharacterEncoding(String)}
     *   counterpart of this getter method.
     * </p>
     * <p>
     *   As with {@link #setContentType(String)}, the value specified here acts as a 
     *   default in case no character encoding has been specified at the view itself.
     *   If a view bean exists with the name of the view to be processed, and this
     *   view has been set a value for its {@link AbstractThymeleafView#setCharacterEncoding(String)}
     *   method, the value specified at the view resolver has no effect.
     * </p>
     * 
     * @return the character encoding to be set at a view-resolver-wide level.
     */
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }
    
    

    /**
     * <p>
     *   Set whether to interpret a given redirect URL that starts with a slash ("/") 
     *   as relative to the current ServletContext, i.e. as relative to the web application root.
     * </p>
     * <p>
     *   Default is <b>{@code true}</b>: A redirect URL that starts with a slash will be interpreted
     *   as relative to the web application root, i.e. the context path will be prepended to the URL.
     * </p>
     * <p>
     *   Redirect URLs can be specified via the {@code "redirect:"} prefix. e.g.:
     *   {@code "redirect:myAction.do"}.
     * </p>
     * 
     * @param redirectContextRelative whether redirect URLs should be considered context-relative or not.
     * @see RedirectView#setContextRelative(boolean)
     */
    public void setRedirectContextRelative(final boolean redirectContextRelative) {
        this.redirectContextRelative = redirectContextRelative;
    }

    
    /**
     * <p>
     *   Return whether to interpret a given redirect URL that starts with a slash ("/") 
     *   as relative to the current ServletContext, i.e. as relative to the web application root. 
     * </p>
     * <p>
     *   Default is <b>{@code true}</b>.
     * </p>
     *
     * @return true if redirect URLs will be considered relative to context, false if not.
     * @see RedirectView#setContextRelative(boolean)
     */
    public boolean isRedirectContextRelative() {
        return this.redirectContextRelative;
    }

    
    
    /**
     * <p>
     *   Set whether redirects should stay compatible with HTTP 1.0 clients.
     * </p>
     * <p>
     *   In the default implementation (default is <b>{@code true}</b>), this will enforce HTTP status
     *   code 302 in any case, i.e. delegate to 
     *   {@link javax.servlet.http.HttpServletResponse#sendRedirect(String)}. Turning this off 
     *   will send HTTP status code 303, which is the correct code for HTTP 1.1 clients, but not understood 
     *   by HTTP 1.0 clients.
     * </p>
     * <p>
     *   Many HTTP 1.1 clients treat 302 just like 303, not making any difference. However, some clients 
     *   depend on 303 when redirecting after a POST request; turn this flag off in such a scenario.
     * </p>
     * <p>
     *   Redirect URLs can be specified via the {@code "redirect:"} prefix. e.g.:
     *   {@code "redirect:myAction.do"}
     * </p>
     * 
     * @param redirectHttp10Compatible true if redirects should stay compatible with HTTP 1.0 clients,
     *        false if not.
     * @see RedirectView#setHttp10Compatible(boolean)
     */
    public void setRedirectHttp10Compatible(final boolean redirectHttp10Compatible) {
        this.redirectHttp10Compatible = redirectHttp10Compatible;
    }

    
    /**
     * <p>
     *   Return whether redirects should stay compatible with HTTP 1.0 clients.
     * </p>
     * <p>
     *   Default is <b>{@code true}</b>.
     * </p>
     * 
     * @return whether redirect responses should stay compatible with HTTP 1.0 clients.
     * @see RedirectView#setHttp10Compatible(boolean)
     */
    public boolean isRedirectHttp10Compatible() {
        return this.redirectHttp10Compatible;
    }
    
    

    /**
     * <p>
     *   Set whether this view resolver should always process forwards and redirects independently of the value of
     *   the {@code viewNames} property.
     * </p>
     * <p>
     *   When this flag is set to {@code true} (default value), any view name that starts with the
     *   {@code redirect:} or {@code forward:} prefixes will be resolved by this ViewResolver even if the view names
     *   would not match what is established at the {@code viewNames} property.
     * </p>
     * <p>
     *   Note that the behaviour of <em>resolving</em> view names with these prefixes is exactly the same with this
     *   flag set to {@code true} or {@code false} (perform an HTTP redirect or forward to an internal JSP resource).
     *   The only difference is whether the prefixes have to be explicitly specified at {@code viewNames} or not.
     * </p>
     * <p>
     *   Default value is {@code true}.
     * </p>
     *
     * @param alwaysProcessRedirectAndForward true if redirects and forwards are always processed, false if this will
     *                                     depend on what is established at the viewNames property.
     */
    public void setAlwaysProcessRedirectAndForward(final boolean alwaysProcessRedirectAndForward) {
        this.alwaysProcessRedirectAndForward = alwaysProcessRedirectAndForward;
    }


    /**
     * <p>
     *   Return whether this view resolver should always process forwards and redirects independently of the value of
     *   the {@code viewNames} property.
     * </p>
     * <p>
     *   When this flag is set to {@code true} (default value), any view name that starts with the
     *   {@code redirect:} or {@code forward:} prefixes will be resolved by this ViewResolver even if the view names
     *   would not match what is established at the {@code viewNames} property.
     * </p>
     * <p>
     *   Note that the behaviour of <em>resolving</em> view names with these prefixes is exactly the same with this
     *   flag set to {@code true} or {@code false} (perform an HTTP redirect or forward to an internal JSP resource).
     *   The only difference is whether the prefixes have to be explicitly specified at {@code viewNames} or not.
     * </p>
     * <p>
     *   Default value is {@code true}.
     * </p>
     *
     * @return whether redirects and forwards will be always processed by this view resolver or else only when they are
     *         matched by the {@code viewNames} property.
     *
     */
    public boolean getAlwaysProcessRedirectAndForward() {
        return this.alwaysProcessRedirectAndForward;
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
    }




    /**
     * <p>
     *   Specify a set of name patterns that will applied to determine whether a view name
     *   returned by a controller will be resolved by this resolver or not.
     * </p>
     * <p>
     *   In applications configuring several view resolvers &ndash;for example, one for Thymeleaf 
     *   and another one for JSP+JSTL legacy pages&ndash;, this property establishes when
     *   a view will be considered to be resolved by this view resolver and when Spring should
     *   simply ask the next resolver in the chain &ndash;according to its {@code order}&ndash;
     *   instead.
     * </p>
     * <p>
     *   The specified view name patterns can be complete view names, but can also use
     *   the {@code *} wildcard: "{@code index.*}", "{@code user_*}", "{@code admin/*}", etc.
     * </p>
     * <p>
     *   Also note that these view name patterns are checked <i>before</i> applying any prefixes
     *   or suffixes to the view name, so they should not include these. Usually therefore, you
     *   would specify {@code orders/*} instead of {@code /WEB-INF/templates/orders/*.html}.
     * </p>
     * 
     * @param viewNames the view names (actually view name patterns)
     * @see PatternMatchUtils#simpleMatch(String[], String)
     */
    public void setViewNames(final String[] viewNames) {
        this.viewNames = viewNames;
    }


    /**
     * <p>
     *   Return the set of name patterns that will applied to determine whether a view name
     *   returned by a controller will be resolved by this resolver or not.
     * </p>
     * <p>
     *   In applications configuring several view resolvers &ndash;for example, one for Thymeleaf 
     *   and another one for JSP+JSTL legacy pages&ndash;, this property establishes when
     *   a view will be considered to be resolved by this view resolver and when Spring should
     *   simply ask the next resolver in the chain &ndash;according to its {@code order}&ndash;
     *   instead.
     * </p>
     * <p>
     *   The specified view name patterns can be complete view names, but can also use
     *   the {@code *} wildcard: "{@code index.*}", "{@code user_*}", "{@code admin/*}", etc.
     * </p>
     * <p>
     *   Also note that these view name patterns are checked <i>before</i> applying any prefixes
     *   or suffixes to the view name, so they should not include these. Usually therefore, you
     *   would specify {@code orders/*} instead of {@code /WEB-INF/templates/orders/*.html}.
     * </p>
     * 
     * @return the view name patterns
     * @see PatternMatchUtils#simpleMatch(String[], String)
     */
    public String[] getViewNames() {
        return this.viewNames;
    }
    
    

    
    /**
     * <p>
     *   Specify names of views &ndash;patterns, in fact&ndash; that cannot 
     *   be handled by this view resolver.
     * </p>
     * <p>
     *   These patterns can be specified in the same format as those in
     *   {@link #setViewNames(String[])}, but work as an <i>exclusion list</i>.
     * </p>
     * 
     * @param excludedViewNames the view names to be excluded (actually view name patterns)
     * @see ThymeleafViewResolver#setViewNames(String[])
     * @see PatternMatchUtils#simpleMatch(String[], String)
     */
    public void setExcludedViewNames(final String[] excludedViewNames) {
        this.excludedViewNames = excludedViewNames;
    }


    /**
     * <p>
     *   Returns the names of views &ndash;patterns, in fact&ndash; that cannot 
     *   be handled by this view resolver.
     * </p>
     * <p>
     *   These patterns can be specified in the same format as those in
     *   {@link #setViewNames(String[])}, but work as an <i>exclusion list</i>.
     * </p>
     * 
     * @return the excluded view name patterns
     * @see ThymeleafViewResolver#getViewNames()
     * @see PatternMatchUtils#simpleMatch(String[], String)
     */
    public String[] getExcludedViewNames() {
        return this.excludedViewNames;
    }
    
    
    

    protected boolean canHandle(final String viewName, @SuppressWarnings("unused") final Locale locale) {
        final String[] viewNamesToBeProcessed = getViewNames();
        final String[] viewNamesNotToBeProcessed = getExcludedViewNames();
        return ((viewNamesToBeProcessed == null || PatternMatchUtils.simpleMatch(viewNamesToBeProcessed, viewName)) &&
                (viewNamesNotToBeProcessed == null || !PatternMatchUtils.simpleMatch(viewNamesNotToBeProcessed, viewName)));
    }
    
    
    
    
    @Override
    protected View createView(final String viewName, final Locale locale) throws Exception {
        // First possible call to check "viewNames": before processing redirects and forwards
        if (!this.alwaysProcessRedirectAndForward && !canHandle(viewName, locale)) {
            vrlogger.trace("[THYMELEAF] View \"{}\" cannot be handled by ThymeleafViewResolver. Passing on to the next resolver in the chain.", viewName);
            return null;
        }
        // Process redirects (HTTP redirects)
        if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
            vrlogger.trace("[THYMELEAF] View \"{}\" is a redirect, and will not be handled directly by ThymeleafViewResolver.", viewName);
            final String redirectUrl = viewName.substring(REDIRECT_URL_PREFIX.length(), viewName.length());
            final RedirectView view = new RedirectView(redirectUrl, isRedirectContextRelative(), isRedirectHttp10Compatible());
            return (View) getApplicationContext().getAutowireCapableBeanFactory().initializeBean(view, viewName);
        }
        // Process forwards (to JSP resources)
        if (viewName.startsWith(FORWARD_URL_PREFIX)) {
            // The "forward:" prefix will actually create a Servlet/JSP view, and that's precisely its aim per the Spring
            // documentation. See http://docs.spring.io/spring-framework/docs/4.2.4.RELEASE/spring-framework-reference/html/mvc.html#mvc-redirecting-forward-prefix
            vrlogger.trace("[THYMELEAF] View \"{}\" is a forward, and will not be handled directly by ThymeleafViewResolver.", viewName);
            final String forwardUrl = viewName.substring(FORWARD_URL_PREFIX.length(), viewName.length());
            return new InternalResourceView(forwardUrl);
        }
        // Second possible call to check "viewNames": after processing redirects and forwards
        if (this.alwaysProcessRedirectAndForward && !canHandle(viewName, locale)) {
            vrlogger.trace("[THYMELEAF] View \"{}\" cannot be handled by ThymeleafViewResolver. Passing on to the next resolver in the chain.", viewName);
            return null;
        }
        vrlogger.trace("[THYMELEAF] View {} will be handled by ThymeleafViewResolver and a " +
                        "{} instance will be created for it", viewName, getViewClass().getSimpleName());
        return loadView(viewName, locale);
    }
    
    
    
    
    @Override
    protected View loadView(final String viewName, final Locale locale) throws Exception {
        
        final AutowireCapableBeanFactory beanFactory = getApplicationContext().getAutowireCapableBeanFactory();
        
        final boolean viewBeanExists = beanFactory.containsBean(viewName);
        final Class<?> viewBeanType = viewBeanExists? beanFactory.getType(viewName) : null;

        final AbstractThymeleafView view;
        if (viewBeanExists && viewBeanType != null && AbstractThymeleafView.class.isAssignableFrom(viewBeanType)) {
            // AppCtx has a bean with name == viewName, and it is a View bean. So let's use it as a prototype!
            //
            // This can mean two things: if the bean has been defined with scope "prototype", we will just use it.
            // If it hasn't we will create a new instance of the view class and use its properties in order to
            // configure this view instance (so that we don't end up using the same bean from several request threads).
            //
            // Note that, if Java-based configuration is used, using @Scope("prototype") would be the only viable
            // possibility here.

            final BeanDefinition viewBeanDefinition =
                    (beanFactory instanceof ConfigurableListableBeanFactory ?
                            ((ConfigurableListableBeanFactory)beanFactory).getBeanDefinition(viewName) :
                            null);

            if (viewBeanDefinition == null || !viewBeanDefinition.isPrototype()) {
                // No scope="prototype", so we will just apply its properties. This should only happen with XML config.
                final AbstractThymeleafView viewInstance = BeanUtils.instantiateClass(getViewClass());
                view = (AbstractThymeleafView) beanFactory.configureBean(viewInstance, viewName);
            } else {
                // This is a prototype bean. Use it as such.
                view = (AbstractThymeleafView) beanFactory.getBean(viewName);
            }

        } else {

            final AbstractThymeleafView viewInstance = BeanUtils.instantiateClass(getViewClass());

            if (viewBeanExists && viewBeanType == null) {
                // AppCtx has a bean with name == viewName, but it is an abstract bean. We still can use it as a prototype.

                // The AUTOWIRE_NO mode applies autowiring only through annotations
                beanFactory.autowireBeanProperties(viewInstance, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
                // A bean with this name exists, so we apply its properties
                beanFactory.applyBeanPropertyValues(viewInstance, viewName);
                // Finally, we let Spring do the remaining initializations (incl. proxifying if needed)
                view = (AbstractThymeleafView) beanFactory.initializeBean(viewInstance, viewName);

            } else {
                // Either AppCtx has no bean with name == viewName, or it is of an incompatible class. No prototyping done.

                // The AUTOWIRE_NO mode applies autowiring only through annotations
                beanFactory.autowireBeanProperties(viewInstance, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
                // Finally, we let Spring do the remaining initializations (incl. proxifying if needed)
                view = (AbstractThymeleafView) beanFactory.initializeBean(viewInstance, viewName);

            }

        }

        view.setTemplateEngine(getTemplateEngine());
        view.setStaticVariables(getStaticVariables());


        // We give view beans the opportunity to specify the template name to be used
        if (view.getTemplateName() == null) {
            view.setTemplateName(viewName);
        }

        if (!view.isForceContentTypeSet()) {
            view.setForceContentType(getForceContentType());
        }
        if (!view.isContentTypeSet() && getContentType() != null) {
            view.setContentType(getContentType());
        }
        if (view.getLocale() == null && locale != null) {
            view.setLocale(locale);
        }
        if (view.getCharacterEncoding() == null && getCharacterEncoding() != null) {
            view.setCharacterEncoding(getCharacterEncoding());
        }
        if (!view.isProducePartialOutputWhileProcessingSet()) {
            view.setProducePartialOutputWhileProcessing(getProducePartialOutputWhileProcessing());
        }
        
        return view;
        
    }

    
}
