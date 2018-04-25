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
package org.thymeleaf.spring3;

import java.util.Set;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.spring3.dialect.SpringStandardDialect;
import org.thymeleaf.spring3.messageresolver.SpringMessageResolver;




/**
 * <p>
 *   Implementation of {@link ISpringTemplateEngine}, meant for Spring applications,
 *   that establishes by default an instance of {@link SpringStandardDialect}
 *   as a dialect (instead of an instance of {@link org.thymeleaf.standard.StandardDialect}.
 * </p>
 * <p>
 *   It also configures a {@link SpringMessageResolver} as message resolver, and
 *   implements the {@link MessageSourceAware} interface in order to let Spring 
 *   automatically setting the {@link MessageSource} used at the application
 *   (bean needs to have id <tt>"messageSource"</tt>). If this Spring standard setting
 *   needs to be overridden, the {@link #setTemplateEngineMessageSource(MessageSource)} can
 *   be used. 
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class SpringTemplateEngine
        extends TemplateEngine
        implements ISpringTemplateEngine, MessageSourceAware {

    
    private static final SpringStandardDialect SPRINGSTANDARD_DIALECT = new SpringStandardDialect();

    private MessageSource messageSource = null;
    private MessageSource templateEngineMessageSource = null;
    
    
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
     *   the one with id <tt>"messageSource"</tt>.
     * </p>
     * <p>
     *   This property <b>should not be set manually</b> in most scenarios (see 
     *   {@link #setTemplateEngineMessageSource(MessageSource)} instead).
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
     * 
     * @param templateEngineMessageSource the message source to be used by the message resolver
     * @since 2.0.15
     */
    @Override
    public void setTemplateEngineMessageSource(final MessageSource templateEngineMessageSource) {
        this.templateEngineMessageSource = templateEngineMessageSource;
    }




    @Override
    protected final void initializeSpecific() {

        // First of all, give the opportunity to subclasses to apply their own configurations
        initializeSpringSpecific();

        // Once the subclasses have had their opportunity, compute configurations belonging to SpringTemplateEngine
        super.initializeSpecific();

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




    /**
     * <p>
     *   Returns whether the <tt>&lt;input type="hidden" ...&gt;</tt> marker tags rendered to signal the presence
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
     *   A number of CSS frameworks and style guides assume that the <tt>&lt;label ...&gt;</tt> for a checkbox
     *   will appear in markup just after the <tt>&lt;input type="checkbox" ...&gt;</tt> tag itself, and so the
     *   default behaviour of rendering an <tt>&lt;input type="hidden" ...&gt;</tt> after the checkbox can lead to
     *   bad application of styles. By tuning this flag, developers can modify this behaviour and make the hidden
     *   tag appear before the checkbox (and thus allow the lable to truly appear right after the checkbox).
     * </p>
     * <p>
     *   Note this hidden field is introduced in order to signal the existence of the field in the form being sent,
     *   even if the checkbox is unchecked (no URL parameter is added for unchecked check boxes).
     * </p>
     * <p>
     *   This flag is set to <tt>false</tt> by default.
     * </p>
     *
     * @return <tt>true</tt> if hidden markers should be rendered before the checkboxes, <tt>false</tt> if not.
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
     *   Sets whether the <tt>&lt;input type="hidden" ...&gt;</tt> marker tags rendered to signal the presence
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
     *   A number of CSS frameworks and style guides assume that the <tt>&lt;label ...&gt;</tt> for a checkbox
     *   will appear in markup just after the <tt>&lt;input type="checkbox" ...&gt;</tt> tag itself, and so the
     *   default behaviour of rendering an <tt>&lt;input type="hidden" ...&gt;</tt> after the checkbox can lead to
     *   bad application of styles. By tuning this flag, developers can modify this behaviour and make the hidden
     *   tag appear before the checkbox (and thus allow the lable to truly appear right after the checkbox).
     * </p>
     * <p>
     *   Note this hidden field is introduced in order to signal the existence of the field in the form being sent,
     *   even if the checkbox is unchecked (no URL parameter is added for unchecked check boxes).
     * </p>
     * <p>
     *   This flag is set to <tt>false</tt> by default.
     * </p>
     *
     * @param renderHiddenMarkersBeforeCheckboxes <tt>true</tt> if hidden markers should be rendered
     *                                            before the checkboxes, <tt>false</tt> if not.
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
     *   This method performs additional initializations required for a
     *   <tt>SpringTemplateEngine</tt> subclass instance. This method
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
     *   for being overridden by subclasses of <tt>SpringTemplateEngine</tt>.
     * </p>
     */
    protected void initializeSpringSpecific() {
        // Nothing to be executed here. Meant for extension
    }


}
