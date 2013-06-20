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
import java.util.Collections;
import java.util.Iterator;
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
public final class ExpressionSequence implements Iterable<Expression>, Serializable {

    private static final long serialVersionUID = -6069208208568731809L;
    

    private static final char SEQUENCE_SEPARATOR_CHAR = ',';
    
    private final List<Expression> expressions;
         
    public ExpressionSequence(final List<? extends Expression> expressions) {
        super();
        Validate.notNull(expressions, "Expression list cannot be null");
        Validate.containsNoNulls(expressions, "Expression list cannot contain any nulls");
        this.expressions = Collections.unmodifiableList(expressions);
    }

    
    public List<Expression> getExpression() {
        return this.expressions;
    }
  
    public int size() {
        return this.expressions.size();
    }
    
    public Iterator<Expression> iterator() {
        return this.expressions.iterator();
    }

    public String getStringRepresentation() {
        final StringBuilder sb = new StringBuilder();
        if (this.expressions.size() > 0) {
            sb.append(this.expressions.get(0));
            for (int i = 1; i < this.expressions.size(); i++) {
                sb.append(SEQUENCE_SEPARATOR_CHAR);
                sb.append(this.expressions.get(i));
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getStringRepresentation();
    }
    
    
    
    

    
    static ExpressionSequence parse(final String input) {
        
        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }

        List<ExpressionParsingNode> result = 
            SimpleExpression.decomposeSimpleExpressions(input);

        if (result == null) {
            return null;
        }
        
        result = composeSequence(result, 0);
        
        if (result == null || !result.get(0).isExpressionSequence()) {
            return null;
        }
        
        return result.get(0).getExpressionSequence();
        
    }
        
    

    private static List<ExpressionParsingNode> composeSequence(
            final List<ExpressionParsingNode> inputExprs, final int inputIndex) {

        
        if (inputExprs == null || inputExprs.size() == 0 || inputIndex >= inputExprs.size()) {
            return null;
        }

        final String input = inputExprs.get(inputIndex).getInput();

        final StringBuilder inputWithPlaceholders = new StringBuilder();
        StringBuilder fragment = new StringBuilder();
        final List<ExpressionParsingNode> fragments = new ArrayList<ExpressionParsingNode>();
        int currentIndex = inputExprs.size();
        
        final List<Integer> expressionIndexes = new ArrayList<Integer>();
        
        final int inputLen = input.length();
        for (int i = 0; i < inputLen; i++) {
            
            final char c = input.charAt(i);

            if (c == SEQUENCE_SEPARATOR_CHAR) {
                // end assignation
                if (fragments.size() > 0) {
                    inputWithPlaceholders.append(SEQUENCE_SEPARATOR_CHAR);
                }
                expressionIndexes.add(Integer.valueOf(currentIndex));
                inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
                inputWithPlaceholders.append(String.valueOf(currentIndex++));
                inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
                fragments.add(new ExpressionParsingNode(fragment.toString()));
                fragment = new StringBuilder();
                
            } else {
                
                fragment.append(c);
               
            }
            
            
        }

        if (fragments.size() > 0) {
            inputWithPlaceholders.append(SEQUENCE_SEPARATOR_CHAR);
        }
        expressionIndexes.add(Integer.valueOf(currentIndex));
        inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
        inputWithPlaceholders.append(String.valueOf(currentIndex++));
        inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
        fragments.add(new ExpressionParsingNode(fragment.toString()));

        
        List<ExpressionParsingNode> result = inputExprs;
        result.set(inputIndex, new ExpressionParsingNode(inputWithPlaceholders.toString()));
        result.addAll(fragments);

        final List<Expression> expressions = new ArrayList<Expression>();
        for (final Integer expressionIndex : expressionIndexes) {
            
            final int expressionIdx = expressionIndex.intValue();
            
            result = Expression.unnest(result, expressionIdx);
            if (result == null) {
                return null;
            }

            if (!result.get(expressionIdx).isExpression()) {
                result = ComplexExpression.composeComplexExpressions(result, expressionIdx);
                if (result == null) {
                    return null;
                }
                if (!result.get(expressionIdx).isExpression()) {
                    return null;
                }
            }
            
            expressions.add(result.get(expressionIdx).getExpression());
            
        }
        
        
        final ExpressionSequence expressionSequence = new ExpressionSequence(expressions);
        result.set(inputIndex, new ExpressionParsingNode(expressionSequence));
        
        return result; 
        
    }
    
    
    
}
