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
package org.thymeleaf.inline;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IText;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class NoOpInliner implements IInliner {

    public static final NoOpInliner INSTANCE = new NoOpInliner();


    private NoOpInliner() {
        super();
    }

    public String getName() {
        return "NOOP";
    }

    public CharSequence inline(final ITemplateContext context, final IText text) {
        // Nothing to do. Anyway, this should never end up being executed...
        return null;
    }

    public CharSequence inline(final ITemplateContext context, final ICDATASection cdataSection) {
        // Nothing to do. Anyway, this should never end up being executed...
        return null;
    }

    public CharSequence inline(final ITemplateContext context, final IComment comment) {
        // Nothing to do. Anyway, this should never end up being executed...
        return null;
    }


}
