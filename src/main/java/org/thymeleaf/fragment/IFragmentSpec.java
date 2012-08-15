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
package org.thymeleaf.fragment;

import java.util.List;

import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Node;




/**
 * <p>
 *   Common interface for all <i>fragment specifications</i>.
 * </p>
 * <p>
 *   <i>Fragment specifications</i> are able of extracting specific sections
 *   or <i>subtrees</i> from a DOM node tree, according to the logic defined 
 *   at the specific implementation.  
 * </p>
 * <p> 
 *   Fragment specs have multiple uses. One of them is allowing the 
 *   {@link org.thymeleaf.TemplateEngine} to select a fragment of a template
 *   to be processed (once read and parsed), discarding the rest of the template
 *   and reducing the processing effort to executing just the wanted parts. See 
 *   {@link org.thymeleaf.TemplateEngine#process(String, org.thymeleaf.context.IContext, IFragmentSpec)}
 *   for more info.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public interface IFragmentSpec {

    /**
     * <p>
     *   Executes the fragment specification against a list of nodes representing a DOM tree
     *   (or a set of trees), returning the extracted nodes.
     * </p>
     * 
     * @param configuration the configuration object.
     * @param nodes the list of nodes on which to apply extraction.
     * @return the extracted nodes.
     */
    public List<Node> extractFragment(final Configuration configuration, final List<Node> nodes);
    
}

