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
package org.thymeleaf.spring5.expression;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring5.dialect.SpringStandardDialect;


/**
 * <p>
 *   Utility class for the easy obtention of objects relevant to the parsing and execution of Thymeleaf
 *   Spring-Standard Expressions (Thymeleaf Standard Expressions based using Spring EL as a base expression language).
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.3
 *
 */
public final class SpringStandardExpressions {


    /**
     * Name used for registering whether <i>Spring EL compilation</i> should be enabled if available or not.
     */
    public static final String ENABLE_SPRING_EL_COMPILER_ATTRIBUTE_NAME = "EnableSpringELCompiler";




    private SpringStandardExpressions() {
        super();
    }


    /**
     * <p>
     *   Check whether compilation of Spring EL expressions should be enabled or not.
     * </p>
     * <p>
     *   This is done through configuration methods at the {@link SpringStandardDialect}
     *   instance being used, and its value is offered to the engine as an <em>execution attribute</em>.
     * </p>
     *
     * @param configuration the configuration object for the current template execution environment.
     * @return {@code true} if the SpEL compiler should be enabled if available, {@code false} if not.
     */
    public static boolean isSpringELCompilerEnabled(final IEngineConfiguration configuration) {
        final Object enableSpringELCompiler =
                configuration.getExecutionAttributes().get(ENABLE_SPRING_EL_COMPILER_ATTRIBUTE_NAME);
        if (enableSpringELCompiler == null) {
            return false;
        }
        if (!(enableSpringELCompiler instanceof Boolean)) {
            throw new TemplateProcessingException(
                    "A value for the \"" + ENABLE_SPRING_EL_COMPILER_ATTRIBUTE_NAME + "\" execution attribute " +
                    "has been specified, but it is not of the required type Boolean. " +
                    "(" + enableSpringELCompiler.getClass().getName() + ")");
        }
        return ((Boolean) enableSpringELCompiler).booleanValue();
    }


}
