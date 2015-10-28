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
package org.thymeleaf.inline;

import org.thymeleaf.context.ITemplateContext;

/**
 * <p>
 *   Interface to be implemented by all <em>inliner</em> implementations.
 * </p>
 * <p>
 *   <em>Inliners</em> are objects in charge of processing logic appearing on <em>Text</em> nodes
 *   (as opposed to on <em>elements</em>). For example, inlined output expressions (<tt>[[${...}]]</tt>),
 *   javascript inlining artifacts, etc.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public interface IInliner {

    public String getName();

    public CharSequence inline(final ITemplateContext context, final CharSequence text);

}
