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
package org.thymeleaf.extras.springsecurity4.auth;

import java.util.Map;

import org.springframework.expression.EvaluationContext;
import org.thymeleaf.Arguments;
import org.thymeleaf.spring4.expression.SpelVariableExpressionEvaluator;
import org.thymeleaf.spring4.expression.ThymeleafEvaluationContextWrapper;
import org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.1.1
 *
 */
final class Spring4VersionSpecificUtility implements ISpringVersionSpecificUtility {




    Spring4VersionSpecificUtility() {
        super();
    }



    public EvaluationContext wrapEvaluationContext(
            final EvaluationContext evaluationContext, final Map<String,Object> contextVariables) {
        return new ThymeleafEvaluationContextWrapper(evaluationContext, contextVariables);
    }



    public Map<String,Object> computeExpressionObjectsFromExpressionEvaluator(
            final Arguments arguments, final IStandardVariableExpressionEvaluator expressionEvaluator) {

        final SpelVariableExpressionEvaluator spelExprEval =
                ((expressionEvaluator != null && expressionEvaluator instanceof SpelVariableExpressionEvaluator)?
                        (SpelVariableExpressionEvaluator) expressionEvaluator :
                        SpelVariableExpressionEvaluator.INSTANCE);
        return  spelExprEval.computeExpressionObjects(arguments.getConfiguration(), arguments);

    }


}
