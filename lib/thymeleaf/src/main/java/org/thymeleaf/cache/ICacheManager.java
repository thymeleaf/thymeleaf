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
package org.thymeleaf.cache;

import java.util.List;

import org.thymeleaf.engine.TemplateModel;


/**
 * <p>
 *   Common interface for all cache manager implementations.
 * </p>
 * <p>
 *   This class is in charge of providing the corresponding cache objects to
 *   the template engine. Every call to each of the {@code getXCache()}
 *   methods must always return the XCache object (i.e. only one cache object
 *   should be ever created for each type of cache, and returned every time it
 *   is requested).
 * </p>
 * <p>
 *   These caches are predefined:
 * </p>
 * <ul>
 *   <li>A <b>template cache</b>, used for storing parsed templates referenced
 *       by their <i>template name</i> and other resolution info (see {@link TemplateCacheKey}).</li>
 *   <li>An <b>expression cache</b>, used for storing expression evaluation artifacts
 *       (for example, {@link org.thymeleaf.standard.expression.Expression} parsed trees,
 *       OGNL/Spring EL parsed trees, etc). Given that this cache can usually store objects
 *       of different classes (referenced by their String representation), prefixes are
 *       normally applied to the String keys in order to being able to differentiate these
 *       classes when retrieving cache entries.</li>
 * </ul>
 * <p>
 *   Only the caches listed above are needed by the template engine when the <i>standard</i> dialects
 *   are being used, but users might want to define new dialects and use new types of caches,
 *   which can be provided by the cache manager using the {@link #getSpecificCache(String)}
 *   method.
 * </p>
 * <p>
 *   <b>Any of these methods could return null</b>, in which case the engine will consider that
 *   no cache must be applied for that specific function.
 * </p>
 * <p>
 *   Note a class with this name existed since 2.0.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public interface ICacheManager {
    
    /**
     * <p>
     *   Returns the cache of parsed templates. Keys are the <i>template names</i>,
     *   as specified at the {@link org.thymeleaf.TemplateEngine#process(String, org.thymeleaf.context.IContext)}
     *   method.
     * </p>
     * 
     * @return the cache of parsed templates.
     */
    public ICache<TemplateCacheKey,TemplateModel> getTemplateCache();

    
    /**
     * <p>
     *   Returns the cache of expression evaluation artifacts.
     * </p>
     * <p>
     *   This cache is meant to store artifacts of diverse nature needed along the
     *   process of parsing and executing expressions in the several languages
     *   available: Standard expressions, OGNL expressions, Spring EL expressions...
     * </p>
     * <p>
     *   Parsing these expressions usually results in some kind of syntax tree object
     *   that represents the expression, and this is what this cache usually stores.
     * </p>
     * <p>
     *   Keys are the expressions themselves (their String representation), along with
     *   a type that is normally used for identifying the nature of the object being
     *   cached (for example {@code {"ognl","person.name"}}).
     * </p>
     * 
     * @return the cache of expression artifacts
     */
    public ICache<ExpressionCacheKey,Object> getExpressionCache();

    
    /**
     * <p>
     *   Returns a specific (non-default) cache, by its name.
     * </p>
     * <p>
     *   User-defined dialects might make use of additional caches (besides <i>template</i>,
     *   and <i>expression</i>) defined at custom-made implementations of this interface, and
     *   they should use this method to retrieve them by their name.
     * </p>
     * <p>
     *   Note the default {@link StandardCacheManager} will return {@code null} for every
     *   call to this method, as it should be custom implementations of this interface (or
     *   extensions of {@link AbstractCacheManager} or extensions {@link StandardCacheManager})
     *   who implement these <i>specific caches</i> and offer their names through the
     *   {@link #getAllSpecificCacheNames()} method.
     * </p>
     *
     * @param name the name of the needed cache
     * @param <K> the type of the cache keys
     * @param <V> the type of the cache values
     * @return the required cache
     */
    public <K,V> ICache<K,V> getSpecificCache(final String name);

    
    /**
     * <p>
     *   Returns a list with the names of all the specific caches
     *   managed by this implementation.
     * </p>
     * <p>
     *   Might return null if no specific caches are managed.
     * </p>
     * <p>
     *   Note the default {@link StandardCacheManager} will return {@code null}, as it should
     *   be custom implementations of this interface (or
     *   extensions of {@link AbstractCacheManager} or extensions {@link StandardCacheManager})
     *   who implement these <i>specific caches</i> and offer their names through the
     *   {@link #getAllSpecificCacheNames()} method.
     * </p>
     *
     * @return a list with all the names of the "specific caches"
     * @since 2.0.16
     */
    public List<String> getAllSpecificCacheNames();

    
    /**
     * <p>
     *   Clears all the caches managed by this cache manager instance.
     * </p>
     * <p>
     *   This method is mainly intended for use from external tools that
     *   might need to clean all caches completely, without having to worry
     *   about implementation details.  
     * </p>
     * 
     * @since 2.0.16
     */
    public void clearAllCaches();
    
    
}
