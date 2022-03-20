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
package org.thymeleaf.cache;


import org.thymeleaf.engine.TemplateModel;

/**
 * 
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.0.0
 *
 */
public final class StandardParsedTemplateEntryValidator
        implements ICacheEntryValidityChecker<TemplateCacheKey,TemplateModel> {
    
    private static final long serialVersionUID = -185355204140990247L;

    public StandardParsedTemplateEntryValidator() {
        super();
    }

    public boolean checkIsValueStillValid(
            final TemplateCacheKey key, final TemplateModel value, final long entryCreationTimestamp) {
        return value.getTemplateData().getValidity().isCacheStillValid();
    }
    
}