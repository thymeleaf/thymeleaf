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

import org.thymeleaf.inline.IInliner;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public interface ILocalVariableAwareVariablesMap extends IVariablesMap {

    /*
     * This interface is used for allowing the processor template handler to modify the
     * local variables in place at each moment. It is meant for internal use only so that processors (potentially
     * custom processors) cannot modify the available variables other than using the local variables mechanism,
     * or either acting directly on the HttpServletRequest object, which should have different effects
     * (request attributes managed directly on the request should not be considered markup-block-local).
     */

    public void put(final String key, final Object value);
    public void putAll(final Map<String, Object> map);
    public void remove(final String key);

    // These is meant to determine whether a specific variable was there from level 0 or was defined afterwards
    // (e.g. in an iteration) - this info is needed when checking possible overrides of originally-bound variables.
    public boolean isVariableLocal(final String name);

    public int level();
    public void increaseLevel();
    public void decreaseLevel();

    public void setSelectionTarget(final Object selectionTarget);

    public void setInliner(final IInliner inliner);


}
