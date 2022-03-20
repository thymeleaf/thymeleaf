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
package org.thymeleaf.standard.expression;


import java.lang.reflect.Method;

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


    static final String[] OPERATORS = new String[] { EQUALS_OPERATOR, NOT_EQUALS_OPERATOR, EQUALS_OPERATOR_2, NOT_EQUALS_OPERATOR_2, NOT_EQUALS_OPERATOR_3 };
    private static final boolean[] LENIENCIES = new boolean[] { false, false, false, false, false };

    @SuppressWarnings("unchecked")
    private static final Class<? extends BinaryOperationExpression>[] OPERATOR_CLASSES =
            (Class<? extends BinaryOperationExpression>[]) new Class<?>[] {
                    EqualsExpression.class, NotEqualsExpression.class, EqualsExpression.class, NotEqualsExpression.class, NotEqualsExpression.class };

    private static final Method LEFT_ALLOWED_METHOD;
    private static final Method RIGHT_ALLOWED_METHOD;


    static {
        try {
            LEFT_ALLOWED_METHOD = EqualsNotEqualsExpression.class.getDeclaredMethod("isLeftAllowed", IStandardExpression.class);
            RIGHT_ALLOWED_METHOD = EqualsNotEqualsExpression.class.getDeclaredMethod("isRightAllowed", IStandardExpression.class);
        } catch (final NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }


    
    
    protected EqualsNotEqualsExpression(final IStandardExpression left, final IStandardExpression right) {
        super(left, right);
    }



    static boolean isRightAllowed(final IStandardExpression right) {
        return true;
    }

    static boolean isLeftAllowed(final IStandardExpression left) {
        return true;
    }

    
    
    
    protected static ExpressionParsingState composeEqualsNotEqualsExpression(
            final ExpressionParsingState state, final int nodeIndex) {
        return composeBinaryOperationExpression(
                state, nodeIndex, OPERATORS, LENIENCIES, OPERATOR_CLASSES, LEFT_ALLOWED_METHOD, RIGHT_ALLOWED_METHOD);
    }
    
    
    

    
}
