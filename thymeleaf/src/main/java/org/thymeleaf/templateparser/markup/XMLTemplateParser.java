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
public final class XMLTemplateParser extends AbstractMarkupTemplateParser {


    static final ParseConfiguration MARKUP_PARSING_CONFIGURATION;



    static {

        /*
         * We will require well-formed XML, but the parser will be configured in order to allow the maximum
         * level of flexibility regarding the parsing of just fragments of XML (and not complete XML documents)
         */
        MARKUP_PARSING_CONFIGURATION = ParseConfiguration.xmlConfiguration();
        MARKUP_PARSING_CONFIGURATION.setElementBalancing(ParseConfiguration.ElementBalancing.REQUIRE_BALANCED);
        MARKUP_PARSING_CONFIGURATION.setCaseSensitive(true);
        MARKUP_PARSING_CONFIGURATION.setNoUnmatchedCloseElementsRequired(true);
        MARKUP_PARSING_CONFIGURATION.setUniqueAttributesInElementRequired(true);
        MARKUP_PARSING_CONFIGURATION.setXmlWellFormedAttributeValuesRequired(true);
        MARKUP_PARSING_CONFIGURATION.setUniqueRootElementPresence(ParseConfiguration.UniqueRootElementPresence.NOT_VALIDATED);
        MARKUP_PARSING_CONFIGURATION.getPrologParseConfiguration().setDoctypePresence(ParseConfiguration.PrologPresence.ALLOWED);
        MARKUP_PARSING_CONFIGURATION.getPrologParseConfiguration().setRequireDoctypeKeywordsUpperCase(false);
        MARKUP_PARSING_CONFIGURATION.getPrologParseConfiguration().setValidateProlog(false);
        MARKUP_PARSING_CONFIGURATION.getPrologParseConfiguration().setXmlDeclarationPresence(ParseConfiguration.PrologPresence.ALLOWED);

    }



    public XMLTemplateParser(final int bufferPoolSize, final int bufferSize) {
        super(MARKUP_PARSING_CONFIGURATION, bufferPoolSize, bufferSize);
    }

    
}
