/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2022, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.util;

import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring5.dialect.SpringStandardDialect;

public final class SpringStandardDialectUtils {




    public static IDialect createSpringStandardDialectInstance() {
        return createSpringStandardDialectInstance(false, false);
    }



    public static IDialect createSpringStandardDialectInstance(final boolean compiledSpEL, final boolean renderHiddenMarkersBeforeCheckboxes) {
        final SpringStandardDialect dialect = new SpringStandardDialect();
        if (renderHiddenMarkersBeforeCheckboxes) {
            dialect.setRenderHiddenMarkersBeforeCheckboxes(renderHiddenMarkersBeforeCheckboxes);
        }
        if (compiledSpEL) {
            dialect.setEnableSpringELCompiler(compiledSpEL);
        }
        return dialect;
    }



    private SpringStandardDialectUtils() {
        super();
    }

    
}
