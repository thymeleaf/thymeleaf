/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.dialect;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.thymeleaf.Standards;
import org.thymeleaf.doctype.resolution.IDocTypeResolutionEntry;


/**
 * <p>
 *   Abstract implementation of {@link IDialect} that returns empty
 *   sets for all dialect components except for the DOCTYPE resolution entries, which
 *   return a set of standard DTD definitions for XHTML 1.0, XHTML 1.1 and HTML5.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractXHTMLEnabledDialect extends AbstractDialect {

    
    public AbstractXHTMLEnabledDialect() {
        super();
    }
    

    
    @Override
    public final Set<IDocTypeResolutionEntry> getDocTypeResolutionEntries() {
        final Set<IDocTypeResolutionEntry> entries = new LinkedHashSet<IDocTypeResolutionEntry>();
        entries.addAll(Standards.ALL_XHTML_1_STRICT_RESOLUTION_ENTRIES);
        entries.addAll(Standards.ALL_XHTML_1_TRANSITIONAL_RESOLUTION_ENTRIES);
        entries.addAll(Standards.ALL_XHTML_1_FRAMESET_RESOLUTION_ENTRIES);
        entries.addAll(Standards.ALL_XHTML_11_RESOLUTION_ENTRIES);
        entries.addAll(Standards.ALL_HTML_5_RESOLUTION_ENTRIES);
        final Set<IDocTypeResolutionEntry> specificEntries = getSpecificDocTypeResolutionEntries();
        if (specificEntries != null) {
            entries.addAll(getSpecificDocTypeResolutionEntries());
        }
        return Collections.unmodifiableSet(entries);
    }
    
    
    protected Set<IDocTypeResolutionEntry> getSpecificDocTypeResolutionEntries() {
        return Collections.emptySet();
    }
    
}
