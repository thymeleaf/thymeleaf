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
package org.thymeleaf.aurora.engine;

import java.util.Map;

import org.thymeleaf.aurora.context.IVariablesMap;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
interface ILocalVariableAwareVariablesMap extends IVariablesMap {

    /*
     * This package-internal interface is used for allowing the processor template handler to modify the
     * local variables in place at each moment. It is meant for internal use only so that processors (potentially
     * custom processors) cannot modify the available variables other than using the local variables mechanism,
     * or either acting directly on the HttpServletRequest object, which should have different effects
     * (request attributes managed directly on the request should not be considered markup-block-local).
     */

    void put(final String key, final Object value);
    void putAll(final Map<String, Object> map);
    void remove(final String key);

    int level();
    void increaseLevel();
    void decreaseLevel();

    void setSelectionTarget(final Object selectionTarget);

    String getStringRepresentationByLevel();

}
