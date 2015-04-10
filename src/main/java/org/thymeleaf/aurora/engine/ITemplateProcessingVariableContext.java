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

import org.thymeleaf.aurora.context.IVariableContext;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
interface ITemplateProcessingVariableContext extends IVariableContext {

    /*
     * This internal interface adds to IVariableContext implementations the capabilities needed by the markup engine
     * to add and remove local variables depending on the markup level.
     */

    void put(final String key, final Object value);
    void putAll(final Map<String, Object> map);
    void remove(final String key);

    int level();
    void increaseLevel();
    void decreaseLevel();

    String getStringRepresentationByLevel();

}
