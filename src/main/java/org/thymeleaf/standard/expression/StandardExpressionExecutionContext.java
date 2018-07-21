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
package org.thymeleaf.standard.expression;







/**
 * <p>
 *   Context class that contains several conditions that might be of interest to the
 *   expression executor (like for instance, whether the expression comes from 
 *   preprocessing or not)
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.16
 *
 */
public final class StandardExpressionExecutionContext {

    // @since 2.1.6
    public static final StandardExpressionExecutionContext RESTRICTED = new StandardExpressionExecutionContext(true, false, false);
    public static final StandardExpressionExecutionContext RESTRICTED_FORBID_UNSAFE_EXP_RESULTS = new StandardExpressionExecutionContext(true, true, false);

    public static final StandardExpressionExecutionContext PREPROCESSING = new StandardExpressionExecutionContext(true, false, false);
    public static final StandardExpressionExecutionContext NORMAL = new StandardExpressionExecutionContext(false, false, false);
    public static final StandardExpressionExecutionContext UNESCAPED_EXPRESSION = new StandardExpressionExecutionContext(true, false, false);

    private static final StandardExpressionExecutionContext RESTRICTED_WITH_TYPE_CONVERSION = new StandardExpressionExecutionContext(true, false, true);
    private static final StandardExpressionExecutionContext RESTRICTED_FORBID_UNSAFE_EXP_RESULTS_WITH_TYPE_CONVERSION = new StandardExpressionExecutionContext(true, true, true);

    private static final StandardExpressionExecutionContext NORMAL_WITH_TYPE_CONVERSION = new StandardExpressionExecutionContext(false, false, true);
    private static final StandardExpressionExecutionContext UNESCAPED_EXPRESSION_WITH_TYPE_CONVERSION = new StandardExpressionExecutionContext(true, false, true);

    private final boolean forbidRequestParameters;
    private final boolean forbidUnsafeExpressionResults;
    private final boolean performTypeConversion;
    
    
    private StandardExpressionExecutionContext(
            final boolean forbidRequestParameters,
            final boolean forbidUnsafeExpressionResults,
            final boolean performTypeConversion) {
        super();
        this.forbidRequestParameters = forbidRequestParameters;
        this.forbidUnsafeExpressionResults = forbidUnsafeExpressionResults;
        this.performTypeConversion = performTypeConversion;
    }

    public boolean getForbidRequestParameters() {
        return this.forbidRequestParameters;
    }

    public boolean getForbidUnsafeExpressionResults() {
        return this.forbidUnsafeExpressionResults;
    }

    public boolean getPerformTypeConversion() {
        return this.performTypeConversion;
    }

    public StandardExpressionExecutionContext withoutTypeConversion() {
        if (!getPerformTypeConversion()) {
            return this;
        }
        if (this == NORMAL_WITH_TYPE_CONVERSION) {
            return NORMAL;
        }
        if (this == UNESCAPED_EXPRESSION_WITH_TYPE_CONVERSION) {
            return UNESCAPED_EXPRESSION;
        }
        if (this == RESTRICTED_WITH_TYPE_CONVERSION) {
            return RESTRICTED;
        }
        if (this == RESTRICTED_FORBID_UNSAFE_EXP_RESULTS_WITH_TYPE_CONVERSION) {
            return RESTRICTED_FORBID_UNSAFE_EXP_RESULTS;
        }
        return new StandardExpressionExecutionContext(getForbidRequestParameters(), getForbidUnsafeExpressionResults(), false);
    }

    public StandardExpressionExecutionContext withTypeConversion() {
        if (getPerformTypeConversion()) {
            return this;
        }
        if (this == NORMAL) {
            return NORMAL_WITH_TYPE_CONVERSION;
        }
        if (this == UNESCAPED_EXPRESSION) {
            return UNESCAPED_EXPRESSION_WITH_TYPE_CONVERSION;
        }
        if (this == RESTRICTED) {
            return RESTRICTED_WITH_TYPE_CONVERSION;
        }
        if (this == RESTRICTED_FORBID_UNSAFE_EXP_RESULTS) {
            return RESTRICTED_FORBID_UNSAFE_EXP_RESULTS_WITH_TYPE_CONVERSION;
        }
        return new StandardExpressionExecutionContext(getForbidRequestParameters(), getForbidUnsafeExpressionResults(), true);
    }


}
