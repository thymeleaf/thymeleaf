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
package org.thymeleaf.templateparser.html;

import java.util.List;

import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;

/**
 * <p>
 *   Document parser implementation for non-XML HTML documents.
 * </p>
 * 
 * @since 2.0.0
 * 
 * @author Daniel Fern&aacute;ndez
 */
public final class LegacyHtml5TemplateParser extends AbstractHtmlTemplateParser {
    

    private static final String FRAGMENT_WRAP_START = "<!DOCTYPE html>\n<html><head></head><body><div>";
    private static final String FRAGMENT_WRAP_END = "</div></body></html>";
    
    
    public LegacyHtml5TemplateParser(final String templateModeName, int poolSize) {
        super(templateModeName, poolSize);
    }



    @Override
    protected String wrapFragment(final String fragment) {
        return FRAGMENT_WRAP_START + fragment + FRAGMENT_WRAP_END;
    }

    
    @Override
    protected List<Node> unwrapFragment(final Document document) {
        return ((NestableNode)document.getFirstElementChild().getChildren().get(1)).getFirstElementChild().getChildren();
    }

    
}
