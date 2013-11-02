/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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


    protected AbstractDialect() {
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


    /**
     * <p>
     *   Returns whether the dialect is lenient or not. If the dialect is not lenient, then
     *   after execution of a template no attributes or elements should exist in the result
     *   with the prefix specified by this dialect (an error is raised if such thing happens).
     * </p>
     * <p>
     *   For non-lenient dialects, any xmlns:{prefix} attributes in the document root or any
     *   other element will be removed from output. These attributes will <b>not</b> be removed
     *   for lenient dialects.
     * </p>
     * <p>
     *   When several dialects act on the same prefix, a prefix
     *   will be considered to be lenient if any of the dialects for that prefix is lenient.
     * </p>
     * <p>
     *   Unless it is really required (for instance, in dialects with <i>null</i> prefix),
     *   dialects should be non-lenient.
     * </p>
     *
     * @return <tt>true</tt> if the dialect is lenient, <tt>false</tt> if not.
     *
     * @deprecated The leniency flag is not used anymore since 2.1.0. Will be removed in 3.0. This
     *             method was removed from the IDialect interface and added here as a default implementation
     *             in order to allow backwards-compatibility of dialects.
     */
    @Deprecated
    public boolean isLenient() {
        return true;
    }

}
