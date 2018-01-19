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
package org.thymeleaf.util;

import java.math.BigDecimal;
import java.util.List;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * @deprecated in 3.0.0 for naming homogeneity reasons. Use the equivalent {@link EvaluationUtils} class instead.
 *             This class will be removed in 3.1.
 * @since 2.1.0
 *
 */
@Deprecated
public final class EvaluationUtil {


    @Deprecated
    public static boolean evaluateAsBoolean(final Object condition) {
        return EvaluationUtils.evaluateAsBoolean(condition);
    }

    @Deprecated
    public static BigDecimal evaluateAsNumber(final Object object) {
        return EvaluationUtils.evaluateAsNumber(object);
    }

    @Deprecated
    public static List<Object> evaluateAsList(final Object value) {
        return EvaluationUtils.evaluateAsList(value);
    }

    @Deprecated
    public static Object[] evaluateAsArray(final Object value) {
        return EvaluationUtils.evaluateAsArray(value);
    }


    private EvaluationUtil() {
        super();
    }

}
