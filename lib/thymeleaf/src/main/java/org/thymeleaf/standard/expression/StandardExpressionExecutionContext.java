/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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
 * <p>
 *   Note a class with this name existed since 2.0.16, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class StandardExpressionExecutionContext {

    public static final StandardExpressionExecutionContext RESTRICTED =
            new StandardExpressionExecutionContext(true, true, false,  false, false);
    public static final StandardExpressionExecutionContext RESTRICTED_FORBID_UNSAFE_EXP_RESULTS =
            new StandardExpressionExecutionContext(true, true, true,  false, false);
    /**
     * Restricted execution context in which the parameters of link expressions (but not their base)
     * are executed in {@link #NORMAL} mode, as applied to URL attributes such as {@code th:href}
     * or {@code th:src}.
     *
     * @since 3.1.6
     */
    public static final StandardExpressionExecutionContext RESTRICTED_WITH_NORMAL_LINK_PARAMETERS =
            new StandardExpressionExecutionContext(true, true, false,  false, true);
    public static final StandardExpressionExecutionContext NORMAL =
            new StandardExpressionExecutionContext(false, false, false, false, false);

    private static final StandardExpressionExecutionContext RESTRICTED_WITH_TYPE_CONVERSION =
            new StandardExpressionExecutionContext(true, true, false,true, false);
    private static final StandardExpressionExecutionContext RESTRICTED_FORBID_UNSAFE_EXP_RESULTS_WITH_TYPE_CONVERSION =
            new StandardExpressionExecutionContext(true, true, true, true, false);
    private static final StandardExpressionExecutionContext RESTRICTED_WITH_NORMAL_LINK_PARAMETERS_WITH_TYPE_CONVERSION =
            new StandardExpressionExecutionContext(true, true, false, true, true);
    private static final StandardExpressionExecutionContext NORMAL_WITH_TYPE_CONVERSION =
            new StandardExpressionExecutionContext(false, false, false, true, false);

    private final boolean restrictVariableAccess;
    private final boolean restrictExternalAccess;
    private final boolean forbidUnsafeExpressionResults;
    private final boolean performTypeConversion;
    private final boolean normalLinkParameters;

    
    private StandardExpressionExecutionContext(
            final boolean restrictVariableAccess,
            final boolean restrictExternalAccess,
            final boolean forbidUnsafeExpressionResults,
            final boolean performTypeConversion,
            final boolean normalLinkParameters) {
        super();
        this.restrictVariableAccess = restrictVariableAccess;
        this.restrictExternalAccess = restrictExternalAccess;
        this.forbidUnsafeExpressionResults = forbidUnsafeExpressionResults;
        this.performTypeConversion = performTypeConversion;
        this.normalLinkParameters = normalLinkParameters;
    }

    public boolean getRestrictVariableAccess() {
        return this.restrictVariableAccess;
    }

    /**
     * Restricts whether this execution context restricts the instantiation of new objects and the
     * access to static classes.
     *
     * @return Whether this restriction should be applied or not.
     * @since 3.0.12
     */
    public boolean getRestrictExternalAccess() {
        return this.restrictExternalAccess;
    }

    public boolean getForbidUnsafeExpressionResults() {
        return this.forbidUnsafeExpressionResults;
    }

    public boolean getPerformTypeConversion() {
        return this.performTypeConversion;
    }

    StandardExpressionExecutionContext forLinkParameters() {
        if (this.normalLinkParameters || (!this.restrictVariableAccess && !this.restrictExternalAccess)) {
            return NORMAL;
        }
        return RESTRICTED;
    }

    public StandardExpressionExecutionContext withoutTypeConversion() {
        if (!getPerformTypeConversion()) {
            return this;
        }
        if (this == NORMAL_WITH_TYPE_CONVERSION) {
            return NORMAL;
        }
        if (this == RESTRICTED_WITH_TYPE_CONVERSION) {
            return RESTRICTED;
        }
        if (this == RESTRICTED_FORBID_UNSAFE_EXP_RESULTS_WITH_TYPE_CONVERSION) {
            return RESTRICTED_FORBID_UNSAFE_EXP_RESULTS;
        }
        if (this == RESTRICTED_WITH_NORMAL_LINK_PARAMETERS_WITH_TYPE_CONVERSION) {
            return RESTRICTED_WITH_NORMAL_LINK_PARAMETERS;
        }
        return new StandardExpressionExecutionContext(
                getRestrictVariableAccess(), getRestrictExternalAccess(), getForbidUnsafeExpressionResults(), false,
                this.normalLinkParameters);
    }

    public StandardExpressionExecutionContext withTypeConversion() {
        if (getPerformTypeConversion()) {
            return this;
        }
        if (this == NORMAL) {
            return NORMAL_WITH_TYPE_CONVERSION;
        }
        if (this == RESTRICTED) {
            return RESTRICTED_WITH_TYPE_CONVERSION;
        }
        if (this == RESTRICTED_FORBID_UNSAFE_EXP_RESULTS) {
            return RESTRICTED_FORBID_UNSAFE_EXP_RESULTS_WITH_TYPE_CONVERSION;
        }
        if (this == RESTRICTED_WITH_NORMAL_LINK_PARAMETERS) {
            return RESTRICTED_WITH_NORMAL_LINK_PARAMETERS_WITH_TYPE_CONVERSION;
        }
        return new StandardExpressionExecutionContext(
                getRestrictVariableAccess(), getRestrictExternalAccess(), getForbidUnsafeExpressionResults(), true,
                this.normalLinkParameters);
    }


}
