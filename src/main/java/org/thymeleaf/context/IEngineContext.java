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
package org.thymeleaf.context;

import java.util.Map;

import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.inline.IInliner;

/**
 * <p>
 *   Mostly-internal interface implemented by all classes containing the context required for
 *   template processing inside the engine itself.
 * </p>
 * <p>
 *   This interface extends {@link ITemplateContext} by adding a series of methods required internally
 *   by the template engine for processing, which should <strong>not be used from users' code</strong>.
 *   Calling these methods directly from custom processors or other extensions could have undesirable
 *   effects on template processing.
 * </p>
 * <p>
 *   Contexts used during template processing by the engine are always implementations of this interface.
 *   If the Template Engine is called with an implementation of this {@link IEngineContext} as
 *   <tt>context</tt>, the same object will be used (so that users can actually provide their own implementations).
 *   On the other side, if the <tt>context</tt> specified to the Template Engine is not an implementation of this
 *   interface, an implementation of {@link IEngineContext} will be internally created by the engine, the original
 *   context's variables and other info will be cloned, and used instead.
 * </p>
 * <p>
 *   Again note that, besides providing custom-made implementations of this interface (which is a very complex
 *   operation, not recommended in most scenarios) there should be no reason why this interface should ever be
 *   used in users' code.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public interface IEngineContext extends ITemplateContext {


    public void setVariable(final String name, final Object value);
    public void setVariables(final Map<String, Object> variables);
    public void removeVariable(final String name);

    public void setSelectionTarget(final Object selectionTarget);

    public void setInliner(final IInliner inliner);

    public void setTemplateData(final TemplateData template);

    // These is meant to determine whether a specific variable was there from level 0 or was defined afterwards
    // (e.g. in an iteration) - this info is needed when checking possible overrides of originally-bound variables.
    public boolean isVariableLocal(final String name);

    public void increaseLevel();
    public void decreaseLevel();


}
