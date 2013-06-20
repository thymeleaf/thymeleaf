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
package org.thymeleaf.templateparser.xmlsax;

import java.util.List;

import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Node;

/**
 * <p>
 *   Parses XML documents, using a standard SAX parser.
 * </p>
 * 
 * <p>
 *   This implementation populates tree nodes with detailed location 
 *   information (document name and line number).
 * </p>
 * 
 * @since 2.0.0
 * 
 * @author Guven Demir
 * @author Daniel Fern&aacute;ndez
 * 
 */
public final class XmlNonValidatingSAXTemplateParser extends AbstractNonValidatingSAXTemplateParser {


    // Even if we are using validating XML templates, fragments cannot be validated due to the lack of
    // context, so we simply add enough information for a non-validating parser to consider it alright
    // (a document root element declaration).
    private static final String FRAGMENT_WRAP_START = "<!DOCTYPE thymeleaf-xml-fragment>\n<thymeleaf-xml-fragment>";
    private static final String FRAGMENT_WRAP_END = "</thymeleaf-xml-fragment>";

    
    
    public XmlNonValidatingSAXTemplateParser(final int poolSize) {
        super(poolSize);
    }


    @Override
    protected String wrapFragment(final String fragment) {
        return FRAGMENT_WRAP_START + fragment + FRAGMENT_WRAP_END;
    }

    
    @Override
    protected List<Node> unwrapFragment(final Document document) {
        return document.getFirstElementChild().getChildren();
    }
    
}
