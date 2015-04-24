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
package org.thymeleaf.text;

import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.ElementDefinitions;

/**
*
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class TextRepositories {

    // Size = 10Mbytes
    public static final int DEFAULT_TEXT_REPOSITORY_CACHE_SIZE_BYTES = 10485760;

    private static final NoCacheTextRepository NO_CACHE_INSTANCE = new NoCacheTextRepository();


    public static ITextRepository createNoCacheRepository() {
        return NO_CACHE_INSTANCE;
    }


    public static ITextRepository createLimitedSizeCacheRepository() {
        return createLimitedSizeCacheRepository(DEFAULT_TEXT_REPOSITORY_CACHE_SIZE_BYTES);
    }


    public static ITextRepository createLimitedSizeCacheRepository(final int cacheSizeBytes) {

        if (cacheSizeBytes <= 0) {
            throw new IllegalArgumentException("Cache size in bytes must be greater than zero");
        }

        final List<String> unremovableTexts  = new ArrayList<String>();

        unremovableTexts.addAll(ElementDefinitions.ALL_STANDARD_HTML_ELEMENT_NAMES);
        unremovableTexts.addAll(AttributeDefinitions.ALL_STANDARD_HTML_ATTRIBUTE_NAMES);
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
                new LimitedSizeCacheTextRepository(cacheSizeBytes / 2, unremovableTexts.toArray(new String[unremovableTexts.size()]));

        return textRepository;

    }



    private TextRepositories() {
        super();
    }

}
