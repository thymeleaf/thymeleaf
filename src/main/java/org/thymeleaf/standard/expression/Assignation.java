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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class Assignation implements Serializable {

    private static final long serialVersionUID = -20278893925937213L;
    

    private static final char OPERATOR = '=';
    // Future proof, just in case in the future we add other tokens as operators
    static final String[] OPERATORS = new String[] {String.valueOf(OPERATOR)};

    private final Expression left;
    private final Expression right;
         
    
    Assignation(final Expression left, final Expression right) {
        super();
        Validate.notNull(left, "Assignation left side cannot be null");
        this.left = left;
        this.right = right;
    }



    public Expression getLeft() {
        return this.left;
    }

    public Expression getRight() {
        return this.right;
    }
    
    public String getStringRepresentation() {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(this.left.getStringRepresentation());
        if (this.right != null) {
            strBuilder.append(OPERATOR);
            if (this.right instanceof ComplexExpression) {
                strBuilder.append('(');
                strBuilder.append(this.right.getStringRepresentation());
                strBuilder.append(')');
            } else {
                strBuilder.append(this.right.getStringRepresentation());
            }
        }
        return strBuilder.toString();
    }


    @Override
    public String toString() {
        return getStringRepresentation();
    }
  
    
    
    
    static Assignation composeAssignation(
            final ExpressionParsingState state, final int nodeIndex, final boolean allowParametersWithoutValue) {

        if (state == null || nodeIndex >= state.size()) {
            return null;
        }

        if (state.hasExpressionAt(nodeIndex)) {
            if (!allowParametersWithoutValue) {
                return null;
            }
            // could happen if we are traversing pointers recursively, so we will consider it a no-value assignation
            return new Assignation(state.get(nodeIndex).getExpression(),null);
        }

        final String input = state.get(nodeIndex).getInput();

        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }

        // First, check whether we are just dealing with a pointer input
        int pointer = ExpressionParsingUtil.parseAsSimpleIndexPlaceholder(input);
        if (pointer != -1) {
            return composeAssignation(state, pointer, allowParametersWithoutValue);
        }

        final int inputLen = input.length();
        final int operatorPos = input.indexOf(OPERATOR);

        final String leftInput =
                (operatorPos == -1? input.trim() : input.substring(0,operatorPos).trim());
        final String rightInput =
                (operatorPos == -1 || operatorPos == (inputLen - 1) ? null : input.substring(operatorPos + 1).trim());

        if (StringUtils.isEmptyOrWhitespace(leftInput)) {
            return null;
        }

        final Expression leftExpr = ExpressionParsingUtil.parseAndCompose(state, leftInput);
        if (leftExpr == null) {
            return null;
        }

        final Expression rightExpr;
        if (!StringUtils.isEmptyOrWhitespace(rightInput)) {
            rightExpr = ExpressionParsingUtil.parseAndCompose(state, rightInput);
            if (rightExpr == null) {
                return null;
            }
        } else if (!allowParametersWithoutValue) {
            return null;
        } else {
            rightExpr = null;
        }

        return new Assignation(leftExpr, rightExpr);

    }
    
    
    
}
