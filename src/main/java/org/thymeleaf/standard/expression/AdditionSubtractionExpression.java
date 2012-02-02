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
package org.thymeleaf.standard.expression;

import java.util.List;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public abstract class AdditionSubtractionExpression extends BinaryOperationExpression {

    private static final long serialVersionUID = -7977102096580376925L;
    
    
    protected static final String ADDITION_OPERATOR = "+";
    protected static final String SUBTRACTION_OPERATOR = "-";

    private static final String[] OPERATORS = new String[] { ADDITION_OPERATOR, SUBTRACTION_OPERATOR };
    private static final boolean[] LENIENCIES = new boolean[] { false, true };
    
    @SuppressWarnings("unchecked")
    private static final Class<? extends BinaryOperationExpression>[] OPERATOR_CLASSES = 
        (Class<? extends BinaryOperationExpression>[]) new Class<?>[] { 
            AdditionExpression.class, SubtractionExpression.class };
    
    protected AdditionSubtractionExpression(final Expression left, final Expression right) {
        super(left, right);
    }
    

    
    
    
    static List<ExpressionParsingNode> composeAdditionSubtractionExpression(
            final List<ExpressionParsingNode> decomposition, int inputIndex) {
        return composeBinaryOperationExpression(
                decomposition, inputIndex, OPERATORS, LENIENCIES, OPERATOR_CLASSES);
    }
    

    
}
