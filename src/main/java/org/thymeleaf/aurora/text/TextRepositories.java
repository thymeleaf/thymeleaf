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
package org.thymeleaf.aurora.text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.thymeleaf.aurora.engine.AttributeDefinitions;
import org.thymeleaf.aurora.engine.ElementDefinitions;
import org.thymeleaf.aurora.util.TextUtil;

/**
*
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class TextRepositories {

    // Size = 20Mbytes
    public static final int DEFAULT_TEXT_REPOSITORY_SIZE_BYTES = 20971520;


    public static ITextRepository createDefault() {

        final List<String> unremovableTexts  = new ArrayList<String>();

        unremovableTexts.addAll(ElementDefinitions.ALL_DEFAULT_CONFIGURED_ELEMENT_NAMES);
        unremovableTexts.addAll(AttributeDefinitions.ALL_DEFAULT_CONFIGURED_ATTRIBUTE_NAMES);
        unremovableTexts.add("");
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

        // (1 char = 2 bytes), thus we divide by 2 the default size in bytes
        final ITextRepository textRepository =
                new StandardTextRepository(DEFAULT_TEXT_REPOSITORY_SIZE_BYTES / 2, unremovableTexts.toArray(new String[unremovableTexts.size()]));

        return textRepository;

    }



    private TextRepositories() {
        super();
    }

}
