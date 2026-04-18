/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2025 Thymeleaf (http://www.thymeleaf.org)
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
package org.thymeleaf.spring5;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.spring5.dialect.SpringStandardDialect;
import org.thymeleaf.spring5.messageresolver.SpringMessageResolver;


/**
 * <p>
 *   Implementation of {@link ISpringTemplateEngine} meant for Spring-enabled applications,
 *   that establishes by default an instance of {@link SpringStandardDialect}
 *   as a dialect (instead of an instance of {@link org.thymeleaf.standard.StandardDialect}.
 * </p>
 * <p>
 *   It also configures a {@link SpringMessageResolver} as message resolver (backed by
 *   Spring's own {@link MessageSource} infrastructure), and implements the
 *   {@link MessageSourceAware} interface in order to let Spring automatically set the
 *   {@link MessageSource} used at the application (bean needs to have id
 *   {@code "messageSource"}). If this Spring standard setting needs to be overridden,
 *   the {@link #setTemplateEngineMessageSource(MessageSource)} method can be used.
 * </p>
 * <p>
 *   <b>Message resolver vs. message source &mdash; precedence rule:</b> if a custom message
 *   resolver is explicitly configured on this engine by calling
 *   {@link #setMessageResolver(IMessageResolver)}, {@link #setMessageResolvers(Set)}, or
 *   {@link #addMessageResolver(IMessageResolver)}, then the automatic setup of
 *   {@link SpringMessageResolver} is skipped entirely, and any value set via
 *   {@link #setMessageSource(MessageSource)} or
 *   {@link #setTemplateEngineMessageSource(MessageSource)} is ignored. Explicitly
 *   configured message resolvers always take precedence over message sources.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public class SpringTemplateEngine
        extends TemplateEngine
        implements ISpringTemplateEngine, MessageSourceAware {


    private static final SpringStandardDialect SPRINGSTANDARD_DIALECT = new SpringStandardDialect();

    private MessageSource messageSource = null;
    private MessageSource templateEngineMessageSource = null;
    private Collection<Class<?>> allowedClassOverridesForViews = Collections.emptyList();
    private boolean messageResolverSetByUser = false;




    public SpringTemplateEngine() {
        super();
        // This will set the SpringStandardDialect, overriding the Standard one set in the super constructor
        super.setDialect(SPRINGSTANDARD_DIALECT);
    }



    /**
     * <p>
     *   Implementation of the {@link MessageSourceAware#setMessageSource(MessageSource)}
     *   method at the {@link MessageSourceAware} interface, provided so that
     *   Spring is able to automatically set the currently configured {@link MessageSource} into
     *   this template engine.
     * </p>
     * <p>
     *   If several {@link MessageSource} implementation beans exist, Spring will inject here
     *   the one with id {@code "messageSource"}.
     * </p>
     * <p>
     *   This property <b>should not be set manually</b> in most scenarios (see
     *   {@link #setTemplateEngineMessageSource(MessageSource)} instead).
     * </p>
     * <p>
     *   Note that this setting is only used when no explicit message resolver has been configured
     *   via {@link #setMessageResolver(IMessageResolver)}, {@link #setMessageResolvers(Set)}, or
     *   {@link #addMessageResolver(IMessageResolver)}. If an explicit resolver is set, this
     *   message source is ignored (see class-level Javadoc for the full precedence rule).
     * </p>
     *
     * @param messageSource the message source to be used by the message resolver
     */
    @Override
    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }



    /**
     * <p>
     *   Convenience method for setting the message source that will
     *   be used by this template engine, overriding the one automatically set by
     *   Spring at the {@link #setMessageSource(MessageSource)} method.
     * </p>
     * <p>
     *   Note that this setting is only used when no explicit message resolver has been configured
     *   via {@link #setMessageResolver(IMessageResolver)}, {@link #setMessageResolvers(Set)}, or
     *   {@link #addMessageResolver(IMessageResolver)}. If an explicit resolver is set, this
     *   message source is ignored (see class-level Javadoc for the full precedence rule).
     * </p>
     *
     * @param templateEngineMessageSource the message source to be used by the message resolver
     */
    @Override
    public void setTemplateEngineMessageSource(final MessageSource templateEngineMessageSource) {
        this.templateEngineMessageSource = templateEngineMessageSource;
    }




    @Override
    public Collection<Class<?>> getAllowedClassOverridesForViews() {
        return this.allowedClassOverridesForViews;
    }


    @Override
    public void setAllowedClassOverridesForViews(final Collection<Class<?>> allowedClassOverridesForViews) {
        if (allowedClassOverridesForViews == null) {
            this.allowedClassOverridesForViews = Collections.emptyList();
        } else {
            this.allowedClassOverridesForViews =
                    Collections.unmodifiableList(new ArrayList<>(allowedClassOverridesForViews));
        }
    }


    /**
     * <p>
     *   Returns whether the SpringEL compiler should be enabled in SpringEL expressions or not.
     * </p>
     * <p>
     *   (This is just a convenience method, equivalent to calling
     *   {@link SpringStandardDialect#getEnableSpringELCompiler()} on the dialect instance itself. It is provided
     *   here in order to allow users to enable the SpEL compiler without
     *   having to directly create instances of the {@link SpringStandardDialect})
     * </p>
     * <p>
     *   Expression compilation can significantly improve the performance of Spring EL expressions, but
     *   might not be adequate for every environment. Read
     *   <a href="http://docs.spring.io/spring/docs/current/spring-framework-reference/html/expressions.html#expressions-spel-compilation">the
     *   official Spring documentation</a> for more detail.
     * </p>
     * <p>
     *   Also note that although Spring includes a SpEL compiler since Spring 4.1, most expressions
     *   in Thymeleaf templates will only be able to properly benefit from this compilation step when at least
     *   Spring Framework version 4.2.4 is used.
     * </p>
     * <p>
     *   This flag is set to {@code false} by default.
     * </p>
     *
     * @return {@code true} if SpEL expressions should be compiled if possible, {@code false} if not.
     */
    public boolean getEnableSpringELCompiler() {
        final Set<IDialect> dialects = getDialects();
        for (final IDialect dialect : dialects) {
            if (dialect instanceof SpringStandardDialect) {
                return ((SpringStandardDialect) dialect).getEnableSpringELCompiler();
            }
        }
        return false;
    }


    /**
     * <p>
     *   Sets whether the SpringEL compiler should be enabled in SpringEL expressions or not.
     * </p>
     * <p>
     *   (This is just a convenience method, equivalent to calling
     *   {@link SpringStandardDialect#setEnableSpringELCompiler(boolean)} on the dialect instance itself. It is provided
     *   here in order to allow users to enable the SpEL compiler without
     *   having to directly create instances of the {@link SpringStandardDialect})
     * </p>
     * <p>
     *   Expression compilation can significantly improve the performance of Spring EL expressions, but
     *   might not be adequate for every environment. Read
     *   <a href="http://docs.spring.io/spring/docs/current/spring-framework-reference/html/expressions.html#expressions-spel-compilation">the
     *   official Spring documentation</a> for more detail.
     * </p>
     * <p>
     *   Also note that although Spring includes a SpEL compiler since Spring 4.1, most expressions
     *   in Thymeleaf templates will only be able to properly benefit from this compilation step when at least
     *   Spring Framework version 4.2.4 is used.
     * </p>
     * <p>
     *   This flag is set to {@code false} by default.
     * </p>
     *
     * @param enableSpringELCompiler {@code true} if SpEL expressions should be compiled if possible, {@code false} if not.
     */
    public void setEnableSpringELCompiler(final boolean enableSpringELCompiler) {
        final Set<IDialect> dialects = getDialects();
        for (final IDialect dialect : dialects) {
            if (dialect instanceof SpringStandardDialect) {
                ((SpringStandardDialect) dialect).setEnableSpringELCompiler(enableSpringELCompiler);
            }
        }
    }




    /**
     * <p>
     *   Returns whether the {@code <input type="hidden" ...>} marker tags rendered to signal the presence
     *   of checkboxes in forms when unchecked should be rendered <em>before</em> the checkbox tag itself,
     *   or after (default).
     * </p>
     * <p>
     *   (This is just a convenience method, equivalent to calling
     *   {@link SpringStandardDialect#getRenderHiddenMarkersBeforeCheckboxes()} on the dialect instance
     *   itself. It is provided here in order to allow users to modify this behaviour without
     *   having to directly create instances of the {@link SpringStandardDialect})
     * </p>
     * <p>
     *   A number of CSS frameworks and style guides assume that the {@code <label ...>} for a checkbox
     *   will appear in markup just after the {@code <input type="checkbox" ...>} tag itself, and so the
     *   default behaviour of rendering an {@code <input type="hidden" ...>} after the checkbox can lead to
     *   bad application of styles. By tuning this flag, developers can modify this behaviour and make the hidden
     *   tag appear before the checkbox (and thus allow the lable to truly appear right after the checkbox).
     * </p>
     * <p>
     *   Note this hidden field is introduced in order to signal the existence of the field in the form being sent,
     *   even if the checkbox is unchecked (no URL parameter is added for unchecked check boxes).
     * </p>
     * <p>
     *   This flag is set to {@code false} by default.
     * </p>
     *
     * @return {@code true} if hidden markers should be rendered before the checkboxes, {@code false} if not.
     *
     * @since 3.0.10
     */
    public boolean getRenderHiddenMarkersBeforeCheckboxes() {
        final Set<IDialect> dialects = getDialects();
        for (final IDialect dialect : dialects) {
            if (dialect instanceof SpringStandardDialect) {
                return ((SpringStandardDialect) dialect).getRenderHiddenMarkersBeforeCheckboxes();
            }
        }
        return false;
    }


    /**
     * <p>
     *   Sets whether the {@code <input type="hidden" ...>} marker tags rendered to signal the presence
     *   of checkboxes in forms when unchecked should be rendered <em>before</em> the checkbox tag itself,
     *   or after (default).
     * </p>
     * <p>
     *   (This is just a convenience method, equivalent to calling
     *   {@link SpringStandardDialect#setRenderHiddenMarkersBeforeCheckboxes(boolean)} on the dialect instance
     *   itself. It is provided here in order to allow users to modify this behaviour without
     *   having to directly create instances of the {@link SpringStandardDialect})
     * </p>
     * <p>
     *   A number of CSS frameworks and style guides assume that the {@code <label ...>} for a checkbox
     *   will appear in markup just after the {@code <input type="checkbox" ...>} tag itself, and so the
     *   default behaviour of rendering an {@code <input type="hidden" ...>} after the checkbox can lead to
     *   bad application of styles. By tuning this flag, developers can modify this behaviour and make the hidden
     *   tag appear before the checkbox (and thus allow the lable to truly appear right after the checkbox).
     * </p>
     * <p>
     *   Note this hidden field is introduced in order to signal the existence of the field in the form being sent,
     *   even if the checkbox is unchecked (no URL parameter is added for unchecked check boxes).
     * </p>
     * <p>
     *   This flag is set to {@code false} by default.
     * </p>
     *
     * @param renderHiddenMarkersBeforeCheckboxes {@code true} if hidden markers should be rendered
     *                                            before the checkboxes, {@code false} if not.
     *
     * @since 3.0.10
     */
    public void setRenderHiddenMarkersBeforeCheckboxes(final boolean renderHiddenMarkersBeforeCheckboxes) {
        final Set<IDialect> dialects = getDialects();
        for (final IDialect dialect : dialects) {
            if (dialect instanceof SpringStandardDialect) {
                ((SpringStandardDialect) dialect).setRenderHiddenMarkersBeforeCheckboxes(renderHiddenMarkersBeforeCheckboxes);
            }
        }
    }




    /**
     * <p>
     *   Sets a single message resolver, suppressing the automatic configuration of
     *   {@link SpringMessageResolver} or {@link StandardMessageResolver} that would
     *   otherwise happen during engine initialization.
     * </p>
     * <p>
     *   Calling this method means any value set via {@link #setMessageSource(MessageSource)} or
     *   {@link #setTemplateEngineMessageSource(MessageSource)} will be ignored. See the
     *   class-level Javadoc for the full precedence rule.
     * </p>
     *
     * @param messageResolver the message resolver to be set.
     */
    @Override
    public void setMessageResolver(final IMessageResolver messageResolver) {
        this.messageResolverSetByUser = true;
        super.setMessageResolver(messageResolver);
    }


    /**
     * <p>
     *   Sets the full set of message resolvers, suppressing the automatic configuration of
     *   {@link SpringMessageResolver} or {@link StandardMessageResolver} that would
     *   otherwise happen during engine initialization.
     * </p>
     * <p>
     *   Calling this method means any value set via {@link #setMessageSource(MessageSource)} or
     *   {@link #setTemplateEngineMessageSource(MessageSource)} will be ignored. See the
     *   class-level Javadoc for the full precedence rule.
     * </p>
     *
     * @param messageResolvers the set of message resolvers to be set.
     */
    @Override
    public void setMessageResolvers(final Set<IMessageResolver> messageResolvers) {
        this.messageResolverSetByUser = true;
        super.setMessageResolvers(messageResolvers);
    }


    /**
     * <p>
     *   Adds a message resolver to the existing set, suppressing the automatic configuration of
     *   {@link SpringMessageResolver} or {@link StandardMessageResolver} that would
     *   otherwise happen during engine initialization.
     * </p>
     * <p>
     *   Calling this method means any value set via {@link #setMessageSource(MessageSource)} or
     *   {@link #setTemplateEngineMessageSource(MessageSource)} will be ignored. See the
     *   class-level Javadoc for the full precedence rule.
     * </p>
     *
     * @param messageResolver the message resolver to be added.
     */
    @Override
    public void addMessageResolver(final IMessageResolver messageResolver) {
        this.messageResolverSetByUser = true;
        super.addMessageResolver(messageResolver);
    }




    @Override
    protected final void initializeSpecific() {

        // Only apply the automatic message resolver if the user has not explicitly configured one.
        // This allows users to override the default SpringMessageResolver/StandardMessageResolver by
        // calling setMessageResolver(), setMessageResolvers() or addMessageResolver() on this engine.
        if (!this.messageResolverSetByUser) {

            final MessageSource messageSource =
                    this.templateEngineMessageSource == null ? this.messageSource : this.templateEngineMessageSource;

            final IMessageResolver messageResolver;
            if (messageSource != null) {
                final SpringMessageResolver springMessageResolver = new SpringMessageResolver();
                springMessageResolver.setMessageSource(messageSource);
                messageResolver = springMessageResolver;
            } else {
                messageResolver = new StandardMessageResolver();
            }

            super.setMessageResolver(messageResolver);

        }

        // Lastly, give the opportunity to subclasses to apply their own configurations
        initializeSpringSpecific();

    }



    /**
     * <p>
     *   This method performs additional initializations required for a
     *   {@code SpringTemplateEngine} subclass instance. This method
     *   is called before the first execution of
     *   {@link TemplateEngine#process(String, org.thymeleaf.context.IContext)}
     *   or {@link TemplateEngine#processThrottled(String, org.thymeleaf.context.IContext)}
     *   in order to create all the structures required for a quick execution of
     *   templates.
     * </p>
     * <p>
     *   THIS METHOD IS INTERNAL AND SHOULD <b>NEVER</b> BE CALLED DIRECTLY.
     * </p>
     * <p>
     *   The implementation of this method does nothing, and it is designed
     *   for being overridden by subclasses of {@code SpringTemplateEngine}.
     * </p>
     */
    protected void initializeSpringSpecific() {
        // Nothing to be executed here. Meant for extension
    }


}
