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
package org.thymeleaf.cache;


import org.thymeleaf.engine.ParsedFragmentModel;

/**
 * 
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.0.0
 *
 */
public final class StandardParsedFragmentEntryValidator
        implements ICacheEntryValidityChecker<FragmentCacheKey,ParsedFragmentModel> {

    private static final long serialVersionUID = -5853535204141790247L;

    public StandardParsedFragmentEntryValidator() {
        super();
    }

    public boolean checkIsValueStillValid(
            final FragmentCacheKey key, final ParsedFragmentModel value, final long entryCreationTimestamp) {
        return value.getValidity().isCacheStillValid();
    }
    
}