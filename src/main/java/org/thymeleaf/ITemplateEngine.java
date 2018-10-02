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
package org.thymeleaf;

import java.io.Writer;
import java.util.Set;

import org.thymeleaf.context.IContext;


/**
 * <p>
 *   Interface implemented by Thymeleaf Template Engines.
 * </p>
 * <p>
 *   Only one implementation of this interface is provided out-of-the-box: {@link TemplateEngine}.
 *   This interface is meant to be used for mocking or prototyping purposes.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @see TemplateEngine
 *
 * @since 3.0.0
 *
 */
public interface ITemplateEngine {


    /**
     * <p>
     * Obtain the {@link IEngineConfiguration} the template engine is using (or will be using)
     * for processing templates.
     * </p>
     * <p>
     * Note that calling this method on a {@link TemplateEngine} implementation will effectively
     * <em>initialize</em> the engine object, and therefore any modifications to the configuration
     * will be forbidden from that moment.
     * </p>
     *
     * @return the engine configuration object.
     */
    public IEngineConfiguration getConfiguration();


    /**
     * <p>
     * Process the specified template (usually the template name). Output will be written
     * into a {@link String} that will be returned from calling this method, once template processing
     * has finished.
     * </p>
     * <p>
     * This is actually a convenience method that will internally create a {@link TemplateSpec} and then
     * call {@link #process(TemplateSpec, IContext)}.
     * </p>
     *
     * @param template the template; depending on the template resolver this might be a template name or even
     *                 the template contents (e.g. StringTemplateResolver).
     * @param context  the context.
     * @return a String containing the result of evaluating the specified template
     * with the provided context.
     */
    public String process(final String template, final IContext context);


    /**
     * <p>
     * Process the specified template (usually the template name) applying a set of
     * <em>template selectors</em>. Output will be written into a {@link String}
     * that will be returned from calling this method, once template processing has finished.
     * </p>
     * <p>
     * Template selectors allow the possibility to process only a part of the specified template, expressing
     * this selection in a syntax similar to jQuery, CSS or XPath selectors. Note this is only available for
     * <em>markup template modes</em> ({@code HTML}, {@code XML}). For more info on <em>template selectors</em>
     * syntax, have a look at <a href="http://www.attoparser.org">AttoParser</a>'s <em>markup selectors</em>
     * documentation.
     * </p>
     * <p>
     * This is actually a convenience method that will internally create a {@link TemplateSpec} and then
     * call {@link #process(TemplateSpec, IContext)}.
     * </p>
     *
     * @param template          the template; depending on the template resolver this might be a template name or even
     *                          the template contents (e.g. StringTemplateResolver).
     * @param templateSelectors the selectors to be used, defining the fragments that should be processed
     * @param context           the context.
     * @return a String containing the result of evaluating the specified template
     * with the provided context.
     */
    public String process(final String template, final Set<String> templateSelectors, final IContext context);


    /**
     * <p>
     * Process a template starting from a {@link TemplateSpec}. Output will be written into a {@link String}
     * that will be returned from calling this method, once template processing has finished.
     * </p>
     * <p>
     * The template specification will be used as input for the template resolvers, queried in chain
     * until one of them resolves the template, which will then be executed.
     * </p>
     * <p>
     * The context will contain the variables that will be available for the execution of
     * expressions inside the template.
     * </p>
     *
     * @param templateSpec the template spec containing the template to be resolved (usually its name only),
     *                     template selectors if they are to be applied, a template mode if it should be forced
     *                     (instead of computing it at resolution time), and other attributes.
     * @param context      the context.
     * @return a String containing the result of evaluating the specified template
     * with the provided context.
     */
    public String process(final TemplateSpec templateSpec, final IContext context);


    /**
     * <p>
     * Process the specified template (usually the template name). Output will be written
     * to the specified writer as it is generated from processing the template. This is specially
     * useful for web environments (using {@link javax.servlet.http.HttpServletResponse#getWriter()}).
     * </p>
     * <p>
     * This is actually a convenience method that will internally create a {@link TemplateSpec} and then
     * call {@link #process(TemplateSpec, IContext, Writer)}.
     * </p>
     *
     * @param template the template; depending on the template resolver this might be a template name or even
     *                 the template contents (e.g. StringTemplateResolver).
     * @param context  the context.
     * @param writer   the writer the results will be output to.
     */
    public void process(final String template, final IContext context, final Writer writer);


    /**
     * <p>
     * Process the specified template (usually the template name) applying a set of
     * <em>template selectors</em>. Output will be written to the specified writer as it is generated
     * from processing the template. This is specially useful for web environments
     * (using {@link javax.servlet.http.HttpServletResponse#getWriter()}).
     * </p>
     * <p>
     * Template selectors allow the possibility to process only a part of the specified template, expressing
     * this selection in a syntax similar to jQuery, CSS or XPath selectors. Note this is only available for
     * <em>markup template modes</em> ({@code HTML}, {@code XML}). For more info on <em>template selectors</em>
     * syntax, have a look at <a href="http://www.attoparser.org">AttoParser</a>'s <em>markup selectors</em>
     * documentation.
     * </p>
     * <p>
     * This is actually a convenience method that will internally create a {@link TemplateSpec} and then
     * call {@link #process(TemplateSpec, IContext, Writer)}.
     * </p>
     *
     * @param template          the template; depending on the template resolver this might be a template name or even
     *                          the template contents (e.g. StringTemplateResolver).
     * @param templateSelectors the selectors to be used, defining the fragments that should be processed. Can be null.
     * @param context           the context.
     * @param writer            the writer the results will be output to.
     */
    public void process(final String template, Set<String> templateSelectors, final IContext context, final Writer writer);


    /**
     * <p>
     * Process a template starting from a {@link TemplateSpec}. Output will be written to the
     * specified writer as it is generated from processing the template. This is specially useful for
     * web environments (using {@link javax.servlet.http.HttpServletResponse#getWriter()}).
     * </p>
     * <p>
     * The template specification will be used as input for the template resolvers, queried in chain
     * until one of them resolves the template, which will then be executed.
     * </p>
     * <p>
     * The context will contain the variables that will be available for the execution of
     * expressions inside the template.
     * </p>
     *
     * @param templateSpec the template spec containing the template to be resolved (usually its name only),
     *                     template selectors if they are to be applied, a template mode if it should be forced
     *                     (instead of computing it at resolution time), and other attributes.
     * @param context      the context.
     * @param writer       the writer the results will be output to.
     */
    public void process(final TemplateSpec templateSpec, final IContext context, final Writer writer);


    /**
     * <p>
     * Process the specified template (usually the template name). Output will be generated from processing the
     * template as dictated by the returned {@link IThrottledTemplateProcessor}, and will be written to the output
     * means specified to this throttled processor's methods. This is specially useful for scenarios such as
     * reactive architectures in which the production of output could be regulated by a back-pressure mechanism.
     * </p>
     * <p>
     * This is actually a convenience method that will internally create a {@link TemplateSpec} and then
     * call {@link #process(TemplateSpec, IContext, Writer)}.
     * </p>
     *
     * @param template the template; depending on the template resolver this might be a template name or even
     *                 the template contents (e.g. StringTemplateResolver).
     * @param context  the context.
     * @return the IThrottledTemplateProcessor object in charge of dictating the engine when to process the template and
     *         how much output should be produced.
     */
    public IThrottledTemplateProcessor processThrottled(final String template, final IContext context);


    /**
     * <p>
     * Process the specified template (usually the template name) applying a set of
     * <em>template selectors</em>. Output will be generated from processing the
     * template as dictated by the returned {@link IThrottledTemplateProcessor}, and will be written to the output
     * means specified to this throttled processor's methods. This is specially useful for scenarios such as
     * reactive architectures in which the production of output could be regulated by a back-pressure mechanism.
     * </p>
     * <p>
     * Template selectors allow the possibility to process only a part of the specified template, expressing
     * this selection in a syntax similar to jQuery, CSS or XPath selectors. Note this is only available for
     * <em>markup template modes</em> ({@code HTML}, {@code XML}). For more info on <em>template selectors</em>
     * syntax, have a look at <a href="http://www.attoparser.org">AttoParser</a>'s <em>markup selectors</em>
     * documentation.
     * </p>
     * <p>
     * This is actually a convenience method that will internally create a {@link TemplateSpec} and then
     * call {@link #process(TemplateSpec, IContext, Writer)}.
     * </p>
     *
     * @param template          the template; depending on the template resolver this might be a template name or even
     *                          the template contents (e.g. StringTemplateResolver).
     * @param templateSelectors the selectors to be used, defining the fragments that should be processed. Can be null.
     * @param context           the context.
     * @return the IThrottledTemplateProcessor object in charge of dictating the engine when to process the template and
     *         how much output should be produced.
     */
    public IThrottledTemplateProcessor processThrottled(final String template, final Set<String> templateSelectors, final IContext context);


    /**
     * <p>
     * Process a template starting from a {@link TemplateSpec}. Output will be generated from processing the
     * template as dictated by the returned {@link IThrottledTemplateProcessor}, and will be written to the output
     * means specified to this throttled processor's methods. This is specially useful for scenarios such as
     * reactive architectures in which the production of output could be regulated by a back-pressure mechanism.
     * </p>
     * <p>
     * The template specification will be used as input for the template resolvers, queried in chain
     * until one of them resolves the template, which will then be executed.
     * </p>
     * <p>
     * The context will contain the variables that will be available for the execution of
     * expressions inside the template.
     * </p>
     *
     * @param templateSpec the template spec containing the template to be resolved (usually its name only),
     *                     template selectors if they are to be applied, a template mode if it should be forced
     *                     (instead of computing it at resolution time), and other attributes.
     * @param context      the context.
     * @return the IThrottledTemplateProcessor object in charge of dictating the engine when to process the template and
     *         how much output should be produced.
     */
    public IThrottledTemplateProcessor processThrottled(final TemplateSpec templateSpec, final IContext context);

}
