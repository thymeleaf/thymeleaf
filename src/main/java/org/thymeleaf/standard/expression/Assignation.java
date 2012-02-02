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
    

    private static final char ASSIGNATION_CHAR = '=';

    private final Token left;
    private final Expression right;
         
    
    private Assignation(final Token left, final Expression right) {
        super();
        Validate.notNull(left, "Assignation left side cannot be null");
        Validate.notNull(right, "Assignation right side cannot be null");
        this.left = left;
        this.right = right;
    }
    

    public Token getLeft() {
        return this.left;
    }

    public Expression getRight() {
        return this.right;
    }
    
    public String getStringRepresentation() {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(this.left.getStringRepresentation());
        strBuilder.append(ASSIGNATION_CHAR);
        if (this.right instanceof ComplexExpression) {
            strBuilder.append('(');
            strBuilder.append(this.right.getStringRepresentation());
            strBuilder.append(')');
        } else {
            strBuilder.append(this.right.getStringRepresentation());
        }
        return strBuilder.toString();
    }

    @Override
    public String toString() {
        return getStringRepresentation();
    }
  
    
    
    
    static List<ExpressionParsingNode> composeAssignation(
            final List<ExpressionParsingNode> inputExprs, final int inputIndex) {

        if (inputExprs == null || inputExprs.size() == 0 || inputIndex >= inputExprs.size()) {
            return null;
        }

        final String input = inputExprs.get(inputIndex).getInput();

        
        final StringBuilder inputWithPlaceholders = new StringBuilder();
        StringBuilder fragment = new StringBuilder();
        final List<ExpressionParsingNode> fragments = new ArrayList<ExpressionParsingNode>();
        
        int tokenIndex = inputExprs.size();
        int expressionIndex = tokenIndex + 1;
        
        Token token = null;

        final int inputLen = input.length();
        for (int i = 0; i < inputLen; i++) {
            
            final char c = input.charAt(i);

            if (c == ASSIGNATION_CHAR && token == null) {
                
                inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
                inputWithPlaceholders.append(String.valueOf(tokenIndex));
                inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
                token = Token.parse(fragment.toString());
                if (token == null) {
                    return null;
                }
                fragments.add(new ExpressionParsingNode(token));
                fragment = new StringBuilder();
                
            } else {
                
                fragment.append(c);
               
            }
            
            
        }
        
        if (token == null) {
            return null;
        }

        inputWithPlaceholders.append(ASSIGNATION_CHAR);
        inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
        inputWithPlaceholders.append(String.valueOf(expressionIndex));
        inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
        fragments.add(new ExpressionParsingNode(fragment.toString()));

        
        List<ExpressionParsingNode> result = inputExprs;
        result.set(inputIndex, new ExpressionParsingNode(inputWithPlaceholders.toString()));
        result.addAll(fragments);
        
        result = SimpleExpression.addNumberLiteralDecomposition(result, expressionIndex);
        if (result == null) {
            return null;
        }
        
        result = Expression.unnest(result, expressionIndex);
        if (result == null) {
            return null;
        }

        
        if (!result.get(expressionIndex).isExpression()) {
            result = ComplexExpression.composeComplexExpressions(result, expressionIndex);
            if (result == null) {
                return null;
            }
            if (!result.get(expressionIndex).isExpression()) {
                return null;
            }
        }
        

        final Assignation assignation = new Assignation(token, result.get(expressionIndex).getExpression());
        result.set(inputIndex, new ExpressionParsingNode(assignation));
        
        return result; 
        
    }
    
    
    
}
