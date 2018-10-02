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
 * <p>
 *   Interface to be implemented by all <em>inliner</em> implementations.
 * </p>
 * <p>
 *   <em>Inliners</em> are objects in charge of processing logic appearing on textual-oriented nodes
 *   ({@link IText}, {@link ICDATASection} and {@link IComment}), as opposed to on <em>elements</em>.
 *   For example, inlined output expressions ({@code [[${...}]]}), javascript inlining artifacts, etc.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public interface IInliner {

    /**
     * <p>
     *   Returns an identifiable name for the inliner
     * </p>
     *
     * @return the name of the inliner.
     */
    public String getName();

    /**
     * <p>
     *   Perform the inlining operation on an {@link IText} node.
     * </p>
     *
     * @param context the template context.
     * @param text the event to be inlined.
     * @return the modified event (or the same event if no modifications were required)
     */
    public CharSequence inline(final ITemplateContext context, final IText text);

    /**
     * <p>
     *   Perform the inlining operation on an {@link ICDATASection} node.
     * </p>
     *
     * @param context the template context.
     * @param cdataSection the event to be inlined.
     * @return the modified event (or the same event if no modifications were required)
     */
    public CharSequence inline(final ITemplateContext context, final ICDATASection cdataSection);

    /**
     * <p>
     *   Perform the inlining operation on an {@link IComment} node.
     * </p>
     *
     * @param context the template context.
     * @param comment the event to be inlined.
     * @return the modified event (or the same event if no modifications were required)
     */
    public CharSequence inline(final ITemplateContext context, final IComment comment);

}
