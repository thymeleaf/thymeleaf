/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
import java.util.Properties;

import org.thymeleaf.Template;
import org.thymeleaf.dom.Node;


/**
 * <p>
 *   Common interface for all cache manager implementations.
 * </p>
 * <p>
 *   This class is in charge of providing the corresponding cache objects to
 *   the template engine. Every call to each of the <tt>getXCache()</tt>
 *   methods must always return the XCache object (i.e. only one cache object
 *   should be ever created for each type of cache, and returned every time it
 *   is requested).
 * </p>
 * <p>
 *   Four caches are predefined:
 * </p>
 * <ul>
 *   <li>A <b>template cache</b>, used for storing parsed templates referenced
 *       by their <i>template name</i>.</li>
 *   <li>A <b>fragment cache</b>, used for storing the parsed DOM tree correspondence
 *       of <i>fragments</i>: pieces of template code that need to be parsed before being
 *       added to the template tree, like for example messages coming from
 *       <tt>.properties</tt> files with HTML tags that are included in results using
 *       <tt>th:utext</tt> processors.</li>
 *   <li>A <b>message cache</b>, used for storing messages (usually from internationalization
 *       files) referenced by template name and locale (like "home_gl_ES").</li>
 *   <li>An <b>expression cache</b>, used for storing expression evaluation artifacts
 *       (for example, {@link org.thymeleaf.standard.expression.Expression} parsed trees,
 *       OGNL/Spring EL parsed trees, etc). Given that this cache can usually store objects
 *       of different classes (referenced by their String representation), prefixes are
 *       normally applied to the String keys in order to being able to differentiate these
 *       differente classes when retrieving cache entries.</li>
 * </ul>
 * <p>
 *   Only these four caches are needed by the template engine when the <i>standard</i> dialects
 *   are being used, but users might want to define new dialects and use new types of caches,
 *   which can be provided by the cache manager using the {@link #getSpecificCache(String)}
 *   method.
 * </p>
 * <p>
 *   <b>Any of these methods could return null</b>, in which case the engine will consider that
 *   no cache must be applied for that specific function</b>.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
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
    public ICache<String,Template> getTemplateCache();
    
    /**
     * <p>
     *   Returns the cache of template code fragments. These fragments are pieces of
     *   template code that need to be parsed before adding them to the template DOM
     *   being processed.
     * </p>
     * <p>
     *   Typical examples of these fragments are externalized/internationalized messages like:
     * </p>
     * <code>
     *   home.header=Welcome to the &lt;i&gt;fruit market&lt;/i&gt;!
     * </code>
     * <p>
     *   ...which are used in templates like <tt>th:utext="#{home.header}"</tt>, and therefore
     *   need parsing in order to be converted to a DOM subtree (because that "&lt;i&gt;" should
     *   be a DOM element by itself).
     * </p>
     * <p>
     *   Keys in this cache are the String representation of fragments themselves along with
     *   the template mode used for such parsing (like 
     *   <tt>"{HTML5}Welcome to the &lt;i&gt;fruit market&lt;/i&gt;"</tt>), and values
     *   are the list of DOM {@link Node}s that correspond to parsing each fragment.
     * </p>
     * <p>
     *   Important: this fragments are <i>not</i> related to <tt>th:fragment</tt> processors. 
     * </p>
     * 
     * @return the cache of parsed template code fragments
     */
    public ICache<String,List<Node>> getFragmentCache();
    
    /**
     * <p>
     *   Returns the cache used for externalized/internationalized messages.
     * </p>
     * <p>
     *   This cache uses as keys the template names (as specified at
     *   {@link org.thymeleaf.TemplateEngine#process(String, org.thymeleaf.context.IContext)})
     *   along with the locale the messages refer to (like "main_gl_ES"), and
     *   as values the <tt>Properties</tt> object containing the messages.
     * </p>
     * 
     * @return the message cache
     */
    public ICache<String,Properties> getMessageCache();
    
    
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
     *   a prefix that is normally used for identifying the nature of the object being
     *   cached (for example <tt>"{OGNL}person.name"</tt>).
     * </p>
     * 
     * @return the cache of expression artifacts
     */
    public ICache<String,Object> getExpressionCache();

    
    /**
     * <p>
     *   Returns a specific (non-default) cache, by its name.
     * </p>
     * <p>
     *   User-defined dialects might make use of additional caches (besides <i>template</i>,
     *   <i>fragment</i>, <i>message</i> and <i>expression</i>) defined at custom-made
     *   implementations of this interface, and they should use this method
     *   to retrieve them by their name.
     * </p>
     * 
     * @param name the name of the needed cache
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
