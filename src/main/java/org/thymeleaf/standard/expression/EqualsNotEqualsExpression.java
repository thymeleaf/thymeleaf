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
public abstract class EqualsNotEqualsExpression extends BinaryOperationExpression {

    private static final long serialVersionUID = -8648395536336588140L;
    
    
    protected static final String EQUALS_OPERATOR = "==";
    protected static final String EQUALS_OPERATOR_2 = "eq";
    protected static final String NOT_EQUALS_OPERATOR = "!=";
    protected static final String NOT_EQUALS_OPERATOR_2 = "neq";
    protected static final String NOT_EQUALS_OPERATOR_3 = "ne";


    private static final String[] OPERATORS = 
        new String[] { EQUALS_OPERATOR, NOT_EQUALS_OPERATOR, EQUALS_OPERATOR_2, NOT_EQUALS_OPERATOR_2, NOT_EQUALS_OPERATOR_3 };
    private static final boolean[] LENIENCIES = new boolean[] { false, false, false, false, false };
    
    @SuppressWarnings("unchecked")
    private static final Class<? extends BinaryOperationExpression>[] OPERATOR_CLASSES = 
        (Class<? extends BinaryOperationExpression>[]) new Class<?>[] { 
            EqualsExpression.class, NotEqualsExpression.class, EqualsExpression.class, NotEqualsExpression.class, NotEqualsExpression.class };

    
    
    protected EqualsNotEqualsExpression(final Expression left, final Expression right) {
        super(left, right);
    }

    
    
    
    protected static List<ExpressionParsingNode> composeEqualsNotEqualsExpression(
            final List<ExpressionParsingNode> decomposition, int inputIndex) {
        return composeBinaryOperationExpression(
                decomposition, inputIndex, OPERATORS, LENIENCIES, OPERATOR_CLASSES);
    }
    
    
    

    
}
