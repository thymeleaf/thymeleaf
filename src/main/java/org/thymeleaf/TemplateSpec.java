/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.LoggingUtils;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Specification class containing everything needed by the template engine related to the
 *   template to be processed. Objects of this class are normally used as an argument to the
 *   different <tt>process(...)</tt> methods at {@link ITemplateEngine}.
 * </p>
 * <p>
 *   The only required value in a template specification is the <em>template</em>, which normally
 *   represents the <em>template name</em>, but can be the entire template contents if the template
 *   is meant to be specified as a String and resolved by a
 *   {@link org.thymeleaf.templateresolver.StringTemplateResolver}.
 * </p>
 * <p>
 *   This is not to be mistaken for the template processing <strong>context</strong>, containing
 *   the data to be used during the processing of the template (variables, locale, etc.) and
 *   modelled by the {@link org.thymeleaf.context.IContext} interface.
 * </p>
 * <p>
 *   The data contained in a Template Specification relates to and identifies the template itself,
 *   independently of any data (variables, etc.) used for processing it.
 * </p>
 * <p>
 *   Objects of this class are <strong>thread-safe</strong>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class TemplateSpec implements Serializable {

    private static final long serialVersionUID = 51214133L;

    private static Set<String> MIME_TYPES_HTML =
            new HashSet<String>(Arrays.asList("text/html", "application/xhtml+xml"));
    private static Set<String> MIME_TYPES_XML =
            new HashSet<String>(Arrays.asList("application/xml"));
    private static Set<String> MIME_TYPES_JAVASCRIPT =
            new HashSet<String>(Arrays.asList(
                    "application/javascript", "application/x-javascript", "application/ecmascript",
                    "text/javascript", "text/ecmascript", "application/json"));
    private static Set<String> MIME_TYPES_CSS =
            new HashSet<String>(Arrays.asList("text/css"));
    private static Set<String> MIME_TYPES_TEXT =
            new HashSet<String>(Arrays.asList("text/plain"));
    private static String MIME_TYPE_SSE = "text/event-stream";

    private static Set<String> MIME_TYPES_ALL;


    private final String template;
    private final Set<String> templateSelectors;
    private final TemplateMode templateMode;
    private final Map<String,Object> templateResolutionAttributes;
    private final String outputContentType;
    private final boolean outputSSE;



    static {

        MIME_TYPES_ALL = new HashSet<String>(12, 1.0f);
        MIME_TYPES_ALL.addAll(MIME_TYPES_HTML);
        MIME_TYPES_ALL.addAll(MIME_TYPES_XML);
        MIME_TYPES_ALL.addAll(MIME_TYPES_JAVASCRIPT);
        MIME_TYPES_ALL.addAll(MIME_TYPES_CSS);
        MIME_TYPES_ALL.addAll(MIME_TYPES_TEXT);
        MIME_TYPES_ALL.add(MIME_TYPE_SSE);

    }



    /**
     * <p>
     *   Build a new object of this class, specifying <em>template</em> and also <em>template mode</em>.
     * </p>
     * <p>
     *   The <em>template</em> normally represents the <em>template name</em>, but can be the entire template
     *   contents if the template is meant to be specified as a String and resolved by a
     *   {@link org.thymeleaf.templateresolver.StringTemplateResolver}.
     * </p>
     * <p>
     *   The template mode only needs to be specified in cases when we want to <em>force</em> a template
     *   mode to be used for a template, independently of the mode that is selected for it by the configured
     *   template resolvers.
     * </p>
     * <p>
     *   This constructor will set no <em>template selectors</em> or <em>template resolution attributes</em>.
     * </p>
     *
     * @param template the template (usually the template name), required.
     * @param templateMode the template mode to be forced, can be null.
     */
    public TemplateSpec(final String template, final TemplateMode templateMode) {
        this(template, null, templateMode, null, null);
    }


    /**
     * <p>
     *   Build a new object of this class, specifying <em>template</em> and also <em>output content type</em>
     *   (MIME type). Most of the times this will force a template mode for template execution
     *   (e.g. <tt>text/html</tt>, <tt>application/javascript</tt>), but not always (e.g. <tt>text/event-stream</tt>).
     * </p>
     * <p>
     *   The <em>template</em> normally represents the <em>template name</em>, but can be the entire template
     *   contents if the template is meant to be specified as a String and resolved by a
     *   {@link org.thymeleaf.templateresolver.StringTemplateResolver}.
     * </p>
     * <p>
     *   Supported relations between template mode and output content type are:
     * </p>
     * <ul>
     *     <li>HTML: <tt>text/html</tt>, <tt>application/xhtml+xml</tt></li>
     *     <li>XML: <tt>application/xml</tt></li>
     *     <li>JAVASCRIPT: <tt>application/javascript</tt>, <tt>application/x-javascript</tt>,
     *                     <tt>application/ecmascript</tt>, <tt>text/javascript</tt>,
     *                     <tt>text/ecmascript</tt>, <tt>application/json</tt></li>
     *     <li>CSS: <tt>text/css</tt></li>
     *     <li>TEXT: <tt>text/plain</tt></li>
     * </ul>
     * <p>
     *   The <tt>text/event-stream</tt> content type will also be supported, but will have no effect in
     *   forcing a template mode. Instead, it will put the engine into Server-Sent Event (SSE) output mode.
     * </p>
     * <p>
     *   Note content type parameters will be ignored (only the mime type itself will be used).
     * </p>
     * <p>
     *   This constructor will set no <em>template selectors</em> or <em>template resolution attributes</em>.
     * </p>
     *
     * @param template the template (usually the template name), required.
     * @param outputContentType the expected output content type, can be null.
     *
     * @since 3.0.6
     */
    public TemplateSpec(final String template, final String outputContentType) {
        this(template, null, null, outputContentType, null);
    }


    /**
     * <p>
     *   Build a new object of this class, specifying <em>template</em> and also <em>template mode</em>.
     * </p>
     * <p>
     *   The <em>template</em> normally represents the <em>template name</em>, but can be the entire template
     *   contents if the template is meant to be specified as a String and resolved by a
     *   {@link org.thymeleaf.templateresolver.StringTemplateResolver}.
     * </p>
     * <p>
     *   The template resolution attributes are meant to be passed to the template resolvers (see
     *   {@link org.thymeleaf.templateresolver.ITemplateResolver} during template resolution, as a way
     *   of configuring their execution for the template being processed.
     * </p>
     * <p>
     *   Note that template resolution attributes are considered a part of the <em>identifier</em> of a template,
     *   so they will be used as a part of the keys for cached templates. <strong>It is therefore
     *   required that template attribute maps contain values with valid {@link #equals(Object)}
     *   and {@link #hashCode()} implementations</strong>. Therefore, using simple (and fast)
     *   <tt>Map&lt;String,String&gt;</tt> maps is the recommended option.
     * </p>
     * <p>
     *   This constructor will set no <em>template selectors</em> or <em>forced template mode</em>.
     * </p>
     *
     * @param template the template (usually the template name), required.
     * @param templateResolutionAttributes the template resolution attributes, can be null.
     */
    public TemplateSpec(final String template, final Map<String, Object> templateResolutionAttributes) {
        this(template, null, null, null, templateResolutionAttributes);
    }


    /**
     * <p>
     *   Build a new object of this class, specifying a template mode that should be forced for template
     *   execution, ignoring the mode resolved by template resolvers.
     * </p>
     * <p>
     *   The <em>template</em> usually represents the <em>template name</em>, but can be the entire template
     *   contents if the template is meant to be specified as a String and resolved by a
     *   {@link org.thymeleaf.templateresolver.StringTemplateResolver}.
     * </p>
     * <p>
     *   Template selectors allow the possibility to process only a part of the specified template, expressing
     *   this selection in a syntax similar to jQuery, CSS or XPath selectors. Note this is only available for
     *   <em>markup template modes</em> (<tt>HTML</tt>, <tt>XML</tt>). For more info on <em>template selectors</em>
     *   syntax, have a look at <a href="http://www.attoparser.org">AttoParser</a>'s <em>markup selectors</em>
     *   documentation.
     * </p>
     * <p>
     *   The template mode only needs to be specified in cases when we want to <em>force</em> a template
     *   mode to be used for a template, independently of the mode that is selected for it by the configured
     *   template resolvers.
     * </p>
     * <p>
     *   The template resolution attributes are meant to be passed to the template resolvers (see
     *   {@link org.thymeleaf.templateresolver.ITemplateResolver} during template resolution, as a way
     *   of configuring their execution for the template being processed.
     * </p>
     * <p>
     *   Note that template resolution attributes are considered a part of the <em>identifier</em> of a template,
     *   so they will be used as a part of the keys for cached templates. <strong>It is therefore
     *   required that template attribute maps contain values with valid {@link #equals(Object)}
     *   and {@link #hashCode()} implementations</strong>. Therefore, using simple (and fast)
     *   <tt>Map&lt;String,String&gt;</tt> maps is the recommended option.
     * </p>
     *
     * @param template the template (usually the template name), required.
     * @param templateSelectors the template selectors to be applied on the template.
     * @param templateMode the template mode to be forced, can be null.
     * @param templateResolutionAttributes the template resolution attributes, can be null.
     */
    public TemplateSpec(
            final String template, final Set<String> templateSelectors, final TemplateMode templateMode,
            final Map<String, Object> templateResolutionAttributes) {
        this(template, templateSelectors, templateMode, null, templateResolutionAttributes);
    }


    /**
     * <p>
     *   Build a new object of this class, specifying an output content type (MIME type). Most of the times this
     *   will force a template mode for template execution (e.g. <tt>text/html</tt>, <tt>application/javascript</tt>),
     *   but not always (e.g. <tt>text/event-stream</tt>).
     * </p>
     * <p>
     *   The <em>template</em> usually represents the <em>template name</em>, but can be the entire template
     *   contents if the template is meant to be specified as a String and resolved by a
     *   {@link org.thymeleaf.templateresolver.StringTemplateResolver}.
     * </p>
     * <p>
     *   Template selectors allow the possibility to process only a part of the specified template, expressing
     *   this selection in a syntax similar to jQuery, CSS or XPath selectors. Note this is only available for
     *   <em>markup template modes</em> (<tt>HTML</tt>, <tt>XML</tt>). For more info on <em>template selectors</em>
     *   syntax, have a look at <a href="http://www.attoparser.org">AttoParser</a>'s <em>markup selectors</em>
     *   documentation.
     * </p>
     * <p>
     *   The template resolution attributes are meant to be passed to the template resolvers (see
     *   {@link org.thymeleaf.templateresolver.ITemplateResolver} during template resolution, as a way
     *   of configuring their execution for the template being processed.
     * </p>
     * <p>
     *   Supported relations between template mode and output content type are:
     * </p>
     * <ul>
     *     <li>HTML: <tt>text/html</tt>, <tt>application/xhtml+xml</tt></li>
     *     <li>XML: <tt>application/xml</tt></li>
     *     <li>JAVASCRIPT: <tt>application/javascript</tt>, <tt>application/x-javascript</tt>,
     *                     <tt>application/ecmascript</tt>, <tt>text/javascript</tt>,
     *                     <tt>text/ecmascript</tt>, <tt>application/json</tt></li>
     *     <li>CSS: <tt>text/css</tt></li>
     *     <li>TEXT: <tt>text/plain</tt></li>
     * </ul>
     * <p>
     *   The <tt>text/event-stream</tt> content type will also be supported, but will have no effect in
     *   forcing a template mode. Instead, it will put the engine into Server-Sent Event (SSE) output mode.
     * </p>
     * <p>
     *   Note content type parameters will be ignored (only the mime type itself will be used).
     * </p>
     * <p>
     *   Note that template resolution attributes are considered a part of the <em>identifier</em> of a template,
     *   so they will be used as a part of the keys for cached templates. <strong>It is therefore
     *   required that template attribute maps contain values with valid {@link #equals(Object)}
     *   and {@link #hashCode()} implementations</strong>. Therefore, using simple (and fast)
     *   <tt>Map&lt;String,String&gt;</tt> maps is the recommended option.
     * </p>
     *
     * @param template the template (usually the template name), required.
     * @param templateSelectors the template selectors to be applied on the template.
     * @param outputContentType the expected output content type, can be null.
     * @param templateResolutionAttributes the template resolution attributes, can be null.
     *
     * @since 3.0.6
     */
    public TemplateSpec(
            final String template, final Set<String> templateSelectors, final String outputContentType,
            final Map<String, Object> templateResolutionAttributes) {
        this(template, templateSelectors, null, outputContentType, templateResolutionAttributes);
    }


    /**
     * <p>
     *   Build a new object of this class, specifying all its attributes.
     * </p>
     *
     * @param template the template (usually the template name), required.
     * @param templateSelectors the template selectors to be applied on the template.
     * @param templateMode the template mode to be forced, can be null.
     * @param outputContentType the expected output content type, can be null.
     * @param templateResolutionAttributes the template resolution attributes, can be null.
     *
     * @since 3.0.4
     */
    TemplateSpec(
            final String template, final Set<String> templateSelectors,
            final TemplateMode templateMode, final String outputContentType,
            final Map<String, Object> templateResolutionAttributes) {

        super();

        Validate.notNull(template, "Template cannot be null");
        Validate.isTrue(templateMode == null || outputContentType == null,
                "If template mode or output content type are specified, the other one cannot");
        // templateSelectors CAN be null
        // templateMode CAN be null
        // outputContentType CAN be null
        // templateResolutionAttributes CAN be null
        // ONLY one of templateMode or outputContentType can be not-null

        this.template = template;
        if (templateSelectors != null && !templateSelectors.isEmpty()) {
            Validate.containsNoEmpties(
                    templateSelectors, "If specified, the Template Selector set cannot contain any nulls or empties");
            if (templateSelectors.size() == 1) {
                this.templateSelectors = Collections.singleton(templateSelectors.iterator().next());
            } else {
                // We will be using a TreeSet because we want the selectors to be ORDERED, so that comparison at the
                // equals(...) method works alright
                this.templateSelectors = Collections.unmodifiableSet(new TreeSet<String>(templateSelectors));
            }
        } else {
            this.templateSelectors = null;
        }

        this.templateResolutionAttributes =
                (templateResolutionAttributes != null && !templateResolutionAttributes.isEmpty()?
                        Collections.unmodifiableMap(new HashMap<String, Object>(templateResolutionAttributes)) : null);

        this.outputContentType = outputContentType;
        final String mimeType = computeMimeType(this.outputContentType);
        final TemplateMode mimeComputedTemplateMode = computeTemplateModeFromMimeType(mimeType);

        if (mimeComputedTemplateMode != null) {
            this.templateMode = mimeComputedTemplateMode;
        } else {
            this.templateMode = templateMode;
        }

        this.outputSSE = (mimeType != null && MIME_TYPE_SSE.equals(mimeType));

    }


    private String computeMimeType(final String outputContentType) {
        if (outputContentType == null || outputContentType.trim().length() == 0) {
            return null;
        }
        String mimeType = outputContentType.trim().toLowerCase();
        final int semicolonPos = mimeType.indexOf(';');
        if (semicolonPos != -1) {
            mimeType = mimeType.substring(0, semicolonPos);
        }
        if (!MIME_TYPES_ALL.contains(mimeType)) {
            // Unrecognized, won't do anything about this
            return null;
        }
        return mimeType;
    }


    private TemplateMode computeTemplateModeFromMimeType(final String mimeType) {
        if (mimeType == null) {
            return null;
        }
        if (MIME_TYPES_HTML.contains(mimeType)) {
            return TemplateMode.JAVASCRIPT;
        }
        if (MIME_TYPES_XML.contains(mimeType)) {
            return TemplateMode.XML;
        }
        if (MIME_TYPES_JAVASCRIPT.contains(mimeType)) {
            return TemplateMode.JAVASCRIPT;
        }
        if (MIME_TYPES_CSS.contains(mimeType)) {
            return TemplateMode.CSS;
        }
        if (MIME_TYPES_TEXT.contains(mimeType)) {
            return TemplateMode.TEXT;
        }
        return null;
    }



    /**
     * <p>
     *   Returns the template (usually the template name).
     * </p>
     * <p>
     *   This <em>template</em> normally represents the <em>template name</em>, but can be the entire template
     *   contents if the template is meant to be specified as a String and resolved by a
     *   {@link org.thymeleaf.templateresolver.StringTemplateResolver}.
     * </p>
     *
     * @return the template. Cannot be null.
     */
    public String getTemplate() {
        return this.template;
    }


    /**
     * <p>
     *   Returns whether this spec has template selectors specified or not.
     * </p>
     *
     * @return <tt>true</tt> of there are template selectors, <tt>false</tt> if not.
     */
    public boolean hasTemplateSelectors() {
        // Checking for null is enough, as we have already processed this in the constructor
        return this.templateSelectors != null;
    }


    /**
     * <p>
     *   Returns the template selectors, if there are any.
     * </p>
     * <p>
     *   Template selectors allow the possibility to process only a part of the specified template, expressing
     *   this selection in a syntax similar to jQuery, CSS or XPath selectors. Note this is only available for
     *   <em>markup template modes</em> (<tt>HTML</tt>, <tt>XML</tt>). For more info on <em>template selectors</em>
     *   syntax, have a look at <a href="http://www.attoparser.org">AttoParser</a>'s <em>markup selectors</em>
     *   documentation.
     * </p>
     *
     * @return the template selectors, or <tt>null</tt> if there are none.
     */
    public Set<String> getTemplateSelectors() {
        return this.templateSelectors;
    }


    /**
     * <p>
     *   Returns whether this spec has template mode specified or not.
     * </p>
     *
     * @return <tt>true</tt> of there is a template mode, <tt>false</tt> if not.
     */
    public boolean hasTemplateMode() {
        return this.templateMode != null;
    }


    /**
     * <p>
     *   Returns the template mode, if it has been specified.
     * </p>
     * <p>
     *   The template mode only needs to be specified in cases when we want to <em>force</em> a template
     *   mode to be used for a template, independently of the mode that is selected for it by the configured
     *   template resolvers.
     * </p>
     *
     * @return the template mode specified, or <tt>null</tt> if there isn't any.
     */
    public TemplateMode getTemplateMode() {
        return this.templateMode;
    }


    /**
     * <p>
     *   Returns whether this spec includes template resolution attributes or not.
     * </p>
     *
     * @return <tt>true</tt> of there are template resolution attributes, <tt>false</tt> if not.
     */
    public boolean hasTemplateResolutionAttributes() {
        // Checking for null is enough, as we have already processed this in the constructor
        return this.templateResolutionAttributes != null;
    }


    /**
     * <p>
     *   Returns the template resolution attributes, if any have been specified.
     * </p>
     * <p>
     *   The template resolution attributes are meant to be passed to the template resolvers (see
     *   {@link org.thymeleaf.templateresolver.ITemplateResolver} during template resolution, as a way
     *   of configuring their execution for the template being processed.
     * </p>
     * <p>
     *   Note that template resolution attributes are considered a part of the <em>identifier</em> of a template,
     *   so they will be used as a part of the keys for cached templates. <strong>It is therefore
     *   required that template attribute maps contain values with valid {@link #equals(Object)}
     *   and {@link #hashCode()} implementations</strong>. Therefore, using simple (and fast)
     *   <tt>Map&lt;String,String&gt;</tt> maps is the recommended option.
     * </p>
     *
     * @return the template resolution attributes.
     */
    public Map<String, Object> getTemplateResolutionAttributes() {
        return this.templateResolutionAttributes;
    }


    /**
     * <p>
     *   Returns the output content type (MIME type). Most of the times this
     *   will force a template mode for template execution (e.g. <tt>text/html</tt>, <tt>application/javascript</tt>),
     *   but not always (e.g. <tt>text/event-stream</tt>).
     * </p>
     * <p>
     *   Supported relations between template mode and output content type are:
     * </p>
     * <ul>
     *     <li>HTML: <tt>text/html</tt>, <tt>application/xhtml+xml</tt></li>
     *     <li>XML: <tt>application/xml</tt></li>
     *     <li>JAVASCRIPT: <tt>application/javascript</tt>, <tt>application/x-javascript</tt>,
     *                     <tt>application/ecmascript</tt>, <tt>text/javascript</tt>,
     *                     <tt>text/ecmascript</tt>, <tt>application/json</tt></li>
     *     <li>CSS: <tt>text/css</tt></li>
     *     <li>TEXT: <tt>text/plain</tt></li>
     * </ul>
     * <p>
     *   The <tt>text/event-stream</tt> content type will also be supported, but will have no effect in
     *   forcing a template mode. Instead, it will put the engine into Server-Sent Event (SSE) output mode.
     * </p>
     * <p>
     *   Note content type parameters will be ignored (only the mime type itself will be used).
     * </p>
     *
     * @return the output content type, or <tt>null</tt> if none was specified.
     */
    public String getOutputContentType() {
        return this.outputContentType;
    }


    /**
     * <p>
     *   Returns whether output should be Server-Sent Events (SSE) or not.
     * </p>
     * <p>
     *   Server-Sent Events mode is enabled by setting the <tt>text/event-stream</tt> mime type
     *   as *output content type* constructor argument.
     * </p>
     *
     * @return true if output is supposed to be done via Server-Sent Events (SSE), false if not.
     *
     * @since 3.0.6
     */
    public boolean isOutputSSE() {
        return this.outputSSE;
    }



    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TemplateSpec)) {
            return false;
        }
        final TemplateSpec that = (TemplateSpec) o;
        if (!this.template.equals(that.template)) {
            return false;
        }
        if (this.templateSelectors != null ? !this.templateSelectors.equals(that.templateSelectors) : that.templateSelectors != null) {
            return false;
        }
        if (this.templateMode != that.templateMode) {
            return false;
        }
        if (!this.outputContentType.equals(that.outputContentType)) {
            return false;
        }
        // Note how it is important that template resolution attribute values correctly implement equals() and hashCode()
        return !(this.templateResolutionAttributes != null ? !this.templateResolutionAttributes.equals(that.templateResolutionAttributes) : that.templateResolutionAttributes != null);
    }


    @Override
    public int hashCode() {
        int result = this.template.hashCode();
        result = 31 * result + (this.templateSelectors != null ? this.templateSelectors.hashCode() : 0);
        result = 31 * result + (this.templateMode != null ? this.templateMode.hashCode() : 0);
        result = 31 * result + (this.outputContentType != null ? this.outputContentType.hashCode() : 0);
        result = 31 * result + (this.templateResolutionAttributes != null ? this.templateResolutionAttributes.hashCode() : 0);
        return result;
    }




    @Override
    public String toString() {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(LoggingUtils.loggifyTemplateName(this.template));
        if (this.templateSelectors != null) {
            strBuilder.append("::");
            strBuilder.append(this.templateSelectors);
        }
        if (this.templateMode != null) {
            strBuilder.append(" @");
            strBuilder.append(this.templateMode);
        }
        if (this.templateResolutionAttributes != null) {
            strBuilder.append(" (");
            strBuilder.append(this.templateResolutionAttributes);
            strBuilder.append(")");
        }
        if (this.outputContentType != null) {
            strBuilder.append(" [");
            strBuilder.append(this.outputContentType);
            strBuilder.append("]");
        }
        return strBuilder.toString();
    }

}
