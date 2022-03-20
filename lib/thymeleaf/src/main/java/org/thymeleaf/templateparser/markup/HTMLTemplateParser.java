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
package org.thymeleaf.templateparser.markup;

import org.attoparser.config.ParseConfiguration;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class HTMLTemplateParser extends AbstractMarkupTemplateParser {


    static final ParseConfiguration MARKUP_PARSING_CONFIGURATION;

    
    
    static {

        /*
         * We don't need the underlying parser to perform many of the available validations, but we will
         * enable AUTO_CLOSE in order to have HTML-compliant auto-closing of tags.
         */
        MARKUP_PARSING_CONFIGURATION = ParseConfiguration.htmlConfiguration();
        MARKUP_PARSING_CONFIGURATION.setElementBalancing(ParseConfiguration.ElementBalancing.AUTO_CLOSE);
        MARKUP_PARSING_CONFIGURATION.setCaseSensitive(false);
        MARKUP_PARSING_CONFIGURATION.setNoUnmatchedCloseElementsRequired(false);
        MARKUP_PARSING_CONFIGURATION.setUniqueAttributesInElementRequired(true);
        MARKUP_PARSING_CONFIGURATION.setXmlWellFormedAttributeValuesRequired(false);
        MARKUP_PARSING_CONFIGURATION.setUniqueRootElementPresence(ParseConfiguration.UniqueRootElementPresence.NOT_VALIDATED);
        MARKUP_PARSING_CONFIGURATION.getPrologParseConfiguration().setDoctypePresence(ParseConfiguration.PrologPresence.ALLOWED);
        MARKUP_PARSING_CONFIGURATION.getPrologParseConfiguration().setRequireDoctypeKeywordsUpperCase(false);
        MARKUP_PARSING_CONFIGURATION.getPrologParseConfiguration().setValidateProlog(false);
        MARKUP_PARSING_CONFIGURATION.getPrologParseConfiguration().setXmlDeclarationPresence(ParseConfiguration.PrologPresence.ALLOWED);

    }
    
    
    
    public HTMLTemplateParser(final int bufferPoolSize, final int bufferSize) {
        super(MARKUP_PARSING_CONFIGURATION, bufferPoolSize, bufferSize);
    }


    
    
}
