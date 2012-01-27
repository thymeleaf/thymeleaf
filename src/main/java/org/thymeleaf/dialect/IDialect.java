/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.dialect;

import java.util.Map;
import java.util.Set;

import org.thymeleaf.doctype.resolution.IDocTypeResolutionEntry;
import org.thymeleaf.doctype.translation.IDocTypeTranslation;
import org.thymeleaf.processor.IProcessor;

/**
 * <p>
 *   Interface for all Thymeleaf <b>Dialects</b>.
 * </p>
 * <p>
 *   A Dialect must define:
 * </p>
 * <ul>
 *   <li>A <b>default prefix</b> for the names of the attributes and elements that will be
 *       processed by processors in this dialect. For example, the standard dialect
 *       defines a <tt>th</tt> prefix for its <tt>th:attr</tt>, <tt>th:value</tt>, etc.
 *       attributes. This prefix is optional, so that if prefix is <tt>null</tt> all elements and
 *       attributes will be considered processable by processors in this dialect. This
 *       prefix can be overridden: the developer can select a different one when he/she adds a dialect to
 *       the template engine (note, though, that this might affect existing .DTD files for
 *       XML/XHTML validation). A prefix is not exclusive to a dialect, and several dialects
 *       can specify the same one (effectively acting as an <i>aggregate dialect</i>)</li>
 *   <li>A <b>leniency flag</b> indicating whether the existence of attributes or
 *       elements starting with the specified prefix but with no associated attribute or
 *       element processor is considered an error (non-lenient) or not (lenient). When several
 *       dialects share the same prefix, leniency is computed by prefix (if at least one
 *       dialect for a prefix is lenient, then the whole prefix is considered to be so).</li>
 *   <li>A set of <b>processors</b>, implementing the {@link IProcessor}
 *       interface, that will be able to process and apply logic to DOM nodes (mainly elements
 *       and their attributes) starting with the specified prefix.</li>
 *   <li>A map of <b>execution attributes</b>, referenced by name. These are objects that
 *       will be made available to processors during execution.</li>
 *   <li>A set of <b>DOCTYPE translations</b>, implementing the {@link IDocTypeTranslation}
 *       interface, which will be applied when processing templates.</li>
 *   <li>A set of <b>DOCTYPE resolution entries</b>, implementing the {@link IDocTypeResolutionEntry}
 *       interface, that will be fed into the {@link org.xml.sax.EntityResolver} of the XML parser 
 *       so that DTD files can be retrieved as (maybe local) resources &ndash; depending on the specific 
 *       implementation of the interface.</li>
 * </ul>
 * <p>
 *   A template engine can be specified more than one dialect (each with its processors). 
 *   In that case, dialects are first checked for conflicts so that, for example,
 *   they do not declare DOCTYPE translations or resolution entries conflicting with the ones
 *   in other dialect/s (although they can be equal). 
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public interface IDialect {

    /**
     * <p>
     *   Returns the default dialect prefix (the one that will be used if none is explicitly
     *   specified during dialect configuration).
     * </p>
     * <p>
     *   If <tt>null</tt> is returned, then every attribute
     *   and/or element is considered processable by the processors in the dialect that apply
     *   to that kind of node (elements with their attributes), and not only those that start 
     *   with a specific prefix. 
     * </p>
     * <p>
     *   Prefixes are <b>not</b> exclusive to a dialect: several dialects can declare the same
     *   prefix, effectively acting as an aggregate dialect.
     * </p>
     * 
     * @return the dialect prefix.
     */
    public String getPrefix();
    
    /**
     * <p>
     *   Returns whether the dialect is lenient or not. If the dialect is not lenient, then
     *   after execution of a template no attributes or elements should exist in the result
     *   with the prefix specified by this dialect (an error is raised if such thing happens). 
     * </p>
     * <p>
     *   For non-lenient dialects, any xmlns:{prefix} attributes in the document root or any
     *   other element will be removed from output. These attributes will <b>not</b> be removed
     *   for lenient dialects.
     * </p>
     * <p>
     *   When several dialects act on the same prefix, a prefix
     *   will be considered to be lenient if any of the dialects for that prefix is lenient.
     * </p>
     * <p>
     *   Unless it is really required (for instance, in dialects with <i>null</i> prefix), 
     *   dialects should be non-lenient.
     * </p>
     * 
     * @return <tt>true</tt> if the dialect is lenient, <tt>false</tt> if not.
     */
    public boolean isLenient();

    
    /**
     * <p>
     *   Returns the set of processors.
     * </p>
     * 
     * @return the set of processors.
     */
    public Set<IProcessor> getProcessors();

    
    /**
     * <p>
     *   Returns the execution attributes that will be set during executions
     *   of the template engine.
     * </p>
     * <p>
     *   If more than one dialect are set, all of their execution attributes
     *   will be added to the available execution attributes map.
     * </p>
     * 
     * @return the execution attributes for this dialect.
     * @since 1.1
     */
    public Map<String,Object> getExecutionAttributes();

    
    /**
     * <p>
     *   Returns the set of DOCTYPE translations.
     * </p>
     * 
     * @return the set of DOCTYPE translations.
     */
    public Set<IDocTypeTranslation> getDocTypeTranslations();
    
    
    /**
     * <p>
     *   Returns the set of DOCTYPE resolution entries.
     * </p>
     * 
     * @return the set of DOCTYPE resolution entries.
     */
    public Set<IDocTypeResolutionEntry> getDocTypeResolutionEntries();

    
}
