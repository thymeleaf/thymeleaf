/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
import java.util.Map;
import java.util.Set;

import org.thymeleaf.doctype.resolution.IDocTypeResolutionEntry;
import org.thymeleaf.doctype.translation.IDocTypeTranslation;
import org.thymeleaf.processor.IProcessor;


/**
 * <p>
 *   Abstract implementation of {@link IDialect} that returns empty
 *   sets for all dialect components.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractDialect implements IDialect {


    public AbstractDialect() {
        super();
    }
    

    public Set<IProcessor> getProcessors() {
        return Collections.emptySet();
    }

    public Map<String, Object> getExecutionAttributes() {
        return Collections.emptyMap();
    }

    public Set<IDocTypeTranslation> getDocTypeTranslations() {
        return Collections.emptySet();
    }

    public Set<IDocTypeResolutionEntry> getDocTypeResolutionEntries() {
        return Collections.emptySet();
    }

    
}
