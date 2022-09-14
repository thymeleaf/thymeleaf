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
package org.thymeleaf.templateparser.markup;

import org.attoparser.IMarkupParser;
import org.attoparser.MarkupParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.markup.decoupled.DecoupledTemplateLogic;
import org.thymeleaf.templateparser.markup.decoupled.DecoupledTemplateLogicUtils;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.templateresource.ITemplateResource;


public class ParsingDecoupled01Test {

    private static final int DEFAULT_PARSER_POOL_SIZE = 40;
    private static final int DEFAULT_PARSER_BLOCK_SIZE = 2048;

    private static final IMarkupParser htmlParser;
    private static final IMarkupParser xmlParser;


    static {
        htmlParser = new MarkupParser(HTMLTemplateParser.MARKUP_PARSING_CONFIGURATION, DEFAULT_PARSER_POOL_SIZE,DEFAULT_PARSER_BLOCK_SIZE);
        xmlParser = new MarkupParser(XMLTemplateParser.MARKUP_PARSING_CONFIGURATION,DEFAULT_PARSER_POOL_SIZE, DEFAULT_PARSER_BLOCK_SIZE);
    }


    public ParsingDecoupled01Test() {
        super();
    }
    


    
    @Test
    public void testParsingDecoupled() throws Exception {

        testParsingDecoupled(
                "parsingdecoupled01", TemplateMode.HTML,
                "{//form=[th:class=\"greatclass\"], //form//div[0]/label=[thefirstlabel], //form//div[1]//label=[th:text=\"${'MegaCovered'}\"], //form/fieldset=[id=\"fset\"]}");
        testParsingDecoupled(
                "parsingdecoupled02", TemplateMode.HTML,
                "{//abbr[1]/a=[id=\"fset\"], //abbr[1]/a/lele=[lala=\"oe\", lala2=\"122\"], //form=[th:class=\"greatclass\", this='that', whatever=those, th:another=\"${lala}\"], //form//.block/div[a='23']//label=[th:text=\"${'MegaCovered'}\"], //form//div[0]/label=[thefirstlabel]}");
        testParsingDecoupled(
                "parsingdecoupled03", TemplateMode.HTML,
                "{//abbr[1]/a=[id=\"fset\"], //abbr[1]/a/lele=[lala=\"oe\", lala2=\"122\"], //form=[th:class=\"greatclass\", this='that', whatever=those, th:another=\"${lala}\"], //form//.block/div[a='23']//label=[th:text=\"${'MegaCovered'}\"], //form//div[0]/label=[thefirstlabel]}");

    }








    private static void testParsingDecoupled(
            final String decoupledTemplate, final TemplateMode templateMode, final String expectedResult)
            throws Exception {

        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templateparser/markup/");
        templateResolver.setSuffix(templateMode == TemplateMode.HTML? ".html" : ".xml");
        templateResolver.setTemplateMode(templateMode);

        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        // We only to this in order to initialize the engine
        templateEngine.process("parsingdecoupled", new Context());


        final IEngineConfiguration configuration = templateEngine.getConfiguration();

        final TemplateResolution templateResolution = templateResolver.resolveTemplate(configuration, null, decoupledTemplate, null);
        final ITemplateResource templateResource = templateResolution.getTemplateResource();

        final DecoupledTemplateLogic decoupledTemplateLogic =
                DecoupledTemplateLogicUtils.computeDecoupledTemplateLogic(
                        configuration, null, decoupledTemplate, null, templateResource, templateMode, (templateMode == TemplateMode.HTML? htmlParser : xmlParser));

        Assertions.assertEquals(expectedResult, decoupledTemplateLogic.toString());

    }


}
