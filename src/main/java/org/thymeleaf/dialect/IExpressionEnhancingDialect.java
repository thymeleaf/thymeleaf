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

import java.util.Map;

import org.thymeleaf.context.IProcessingContext;

/**
 * <p>
 *   Feature-specifier interface for {@link IDialect} implementations.
 * </p>
 * <p>
 *   Implementing this interface allows dialects to specify a series of objects that will be added
 *   to the context as <i>utility objects</i> during expression evaluations.
 * </p>
 * <p>
 *   This means, for example, that a dialect could add a <tt>util</tt> object so that it could be
 *   used in OGNL or SpringEL expression evaluations like <tt>${#util.doThis(obj)}</tt>. 
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.12
 *
 */
public interface IExpressionEnhancingDialect extends IDialect {


    /**
     * <p>
     *   Returns the objects that should be added to expression evaluation contexts.
     * </p>
     * <p>
     *   This means, for example, that a dialect could add a <tt>util</tt> object so that it could be
     *   used in OGNL or SpringEL expression evaluations like <tt>${#util.doThis(obj)}</tt>. 
     * </p>
     * 
     * @param processingContext the processing context on which the expression evaluation will be performed.
     * @return the Map of objects to be added to the expression evaluation context.
     */
    public Map<String,Object> getAdditionalExpressionObjects(final IProcessingContext processingContext);

}
