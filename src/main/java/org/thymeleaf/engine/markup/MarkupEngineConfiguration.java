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
package org.thymeleaf.engine.markup;

import java.util.ArrayList;
import java.util.List;

import org.attoparser.markup.html.elements.HtmlElements;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class MarkupEngineConfiguration {


    private final IMarkupParser parser;
    private final IMarkupTextRepository textRepository;


    public MarkupEngineConfiguration(
            final IMarkupParser parser, final IMarkupTextRepository textRepository) {
        super();
        this.parser = parser;
        this.textRepository = textRepository;
    }


    public IMarkupParser getParser() {
        return this.parser;
    }

    public IMarkupTextRepository getTextRepository() {
        return textRepository;
    }



    public static MarkupEngineConfiguration createBaseConfiguration() {

        final List<String> unremovableTexts  = new ArrayList<String>();
        unremovableTexts.addAll(HtmlElements.ALL_ELEMENT_NAMES);
        unremovableTexts.add(" ");
        unremovableTexts.add("\n");
        unremovableTexts.add("\n  ");
        unremovableTexts.add("\n    ");
        unremovableTexts.add("\n      ");
        unremovableTexts.add("\n        ");
        unremovableTexts.add("\n          ");
        unremovableTexts.add("\n            ");
        unremovableTexts.add("\n              ");
        unremovableTexts.add("\n                ");
        unremovableTexts.add("\n\t");
        unremovableTexts.add("\n\t\t");
        unremovableTexts.add("\n\t\t\t");
        unremovableTexts.add("\n\t\t\t\t");

        // Size = 10MBytes (1 char = 2 bytes)
        final IMarkupTextRepository textRepository =
                new MarkupTextRepository(5242880, unremovableTexts.toArray(new String[unremovableTexts.size()]));


        // Pool size is set to 40, and buffer size to 2K chars = 4K bytes
        // This should be enough for processing 20 templates at a time, each one being able to include external
        // fragments and/or parsable content (2 buffers per template).
        final IMarkupParser parser = new StandardHtmlParser(2048,40);

        return new MarkupEngineConfiguration(parser, textRepository);

    }

}
