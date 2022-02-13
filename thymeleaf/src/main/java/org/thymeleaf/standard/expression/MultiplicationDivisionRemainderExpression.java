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
public abstract class MultiplicationDivisionRemainderExpression extends BinaryOperationExpression {


    private static final long serialVersionUID = -1364531602981256885L;
    
    
    protected static final String MULTIPLICATION_OPERATOR = "*";
    protected static final String DIVISION_OPERATOR = "/";
    protected static final String DIVISION_OPERATOR_2 = "div";
    protected static final String REMAINDER_OPERATOR = "%";
    protected static final String REMAINDER_OPERATOR_2 = "mod";


    static final String[] OPERATORS = new String[] {
        MULTIPLICATION_OPERATOR, DIVISION_OPERATOR, DIVISION_OPERATOR_2, REMAINDER_OPERATOR, REMAINDER_OPERATOR_2 };
    private static final boolean[] LENIENCIES = new boolean[] { false, false, false, false, false };

    @SuppressWarnings("unchecked")
    private static final Class<? extends BinaryOperationExpression>[] OPERATOR_CLASSES =
            (Class<? extends BinaryOperationExpression>[]) new Class<?>[] {
                    MultiplicationExpression.class, DivisionExpression.class, DivisionExpression.class,
                    RemainderExpression.class, RemainderExpression.class };

    private static final Method LEFT_ALLOWED_METHOD;
    private static final Method RIGHT_ALLOWED_METHOD;



    static {
        try {
            LEFT_ALLOWED_METHOD = MultiplicationDivisionRemainderExpression.class.getDeclaredMethod("isLeftAllowed", IStandardExpression.class);
            RIGHT_ALLOWED_METHOD = MultiplicationDivisionRemainderExpression.class.getDeclaredMethod("isRightAllowed", IStandardExpression.class);
        } catch (final NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }


    
    protected MultiplicationDivisionRemainderExpression(final IStandardExpression left, final IStandardExpression right) {
        super(left, right);
    }




    static boolean isRightAllowed(final IStandardExpression right) {
        return right != null &&
                !(right instanceof Token && !(right instanceof NumberTokenExpression)) &&
                !(right instanceof TextLiteralExpression);
    }

    static boolean isLeftAllowed(final IStandardExpression left) {
        return left != null && !(left instanceof Token &&
                !(left instanceof NumberTokenExpression)) &&
                !(left instanceof TextLiteralExpression);
    }

    
    
    
    static ExpressionParsingState composeMultiplicationDivisionRemainderExpression(
            final ExpressionParsingState state, final int nodeIndex) {
        
        return composeBinaryOperationExpression(
                state, nodeIndex, OPERATORS, LENIENCIES, OPERATOR_CLASSES, LEFT_ALLOWED_METHOD, RIGHT_ALLOWED_METHOD);
        
    }
    

    
    
}
