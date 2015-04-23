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
package org.thymeleaf.aurora.context;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public interface IVariablesMap extends IContext {

    /*
     * There is no need to make VariablesMap instances implement java.util.Map or extend from HashMap. Such thing
     * would give us no advantage when expression languages execute expressions on them, because we need to
     * specify property accessors anyway (both for SpringEL and OGNL/MVEL). And besides, we don't need write support
     * on the variable maps (even if they have to allow local variables and also be aware of any changes performed
     * on underlying data storage structures like HttpServletRequest).
     *
     * Also, note any SECURITY RESTRICTIONS (like e.g. not allowing access to request parameters from unescaped
     * or preprocessing expressions) should be managed at the property accessors themselves (in fact, SpringEL
     * PropertyAccessors have a 'canRead' method that is the point where these restrictions should be applied).
     */


    // Selection target works as a local variable, but is used so often that it has its own methods in order to allow
    // specific performance improvements to be designed for them
    public boolean hasSelectionTarget();
    public Object getSelectionTarget();

    // Text inlining works as a local variable, but is used so often that it has its own methods in order to allow
    // specific performance improvements to be designed for them
    public boolean isTextInliningActive();

}
