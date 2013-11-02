/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.templateparser;

import java.io.Reader;
import java.util.List;

import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Node;

/**
 * <p>
 *   Common interface for template parsers, in charge of reading and converting a 
 *   resolved template into a DOM tree.
 * </p>
 * <p>
 *   All implementations of this interface must be <b>thread-safe</b>.  
 * </p>
 * 
 * @since 2.0.0
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Guven Demir
 * 
 */
public interface ITemplateParser {
    

    /**
     * <p>
     *   Parses the document contained in the given <tt>Reader</tt> object.
     * </p>
     * 
     * @param configuration the Configuration object
     * @param documentName the document name (optional).
     * @param source the Reader on the document to be parsed (required).
     * @return the parsed Document.
     */
    public Document parseTemplate(final Configuration configuration, final String documentName, final Reader source);
    

    /**
     * <p>
     *   Parses the document fragment contained in the given <tt>String</tt> object.
     * </p>
     * 
     * @param configuration the Configuration object
     * @param fragment the String containing the fragment
     * @return the resulting list of nodes.
     */
    public List<Node> parseFragment(final Configuration configuration, final String fragment);
    
}
