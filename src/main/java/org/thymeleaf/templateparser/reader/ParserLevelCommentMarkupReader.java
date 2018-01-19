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
package org.thymeleaf.templateparser.reader;

import java.io.Reader;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class ParserLevelCommentMarkupReader extends BlockAwareReader {

    private final static char[] PREFIX = "<!--/*".toCharArray();
    private final static char[] SUFFIX = "*/-->".toCharArray();


    public ParserLevelCommentMarkupReader(final Reader reader) {
        super(reader, BlockAction.DISCARD_ALL, PREFIX, SUFFIX);
    }

}
