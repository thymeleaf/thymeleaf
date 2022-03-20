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




/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
final class ExpressionParsingNode {
    
    private final String input;
    private final Expression expression;

    ExpressionParsingNode(final String input) {
        super();
        // INPUT SHOULD NEVER BE NULL!!!
        this.input = input.trim();
        this.expression = null;
    }
    
    ExpressionParsingNode(final Expression expression) {
        super();
        this.expression = expression;
        this.input = null;
    }

    boolean isInput() {
        return this.input != null;
    }

    boolean isExpression() {
        return this.expression != null;
    }
    
    boolean isSimpleExpression() {
        return this.expression != null && this.expression instanceof SimpleExpression;
    }
    
    boolean ComplexExpression() {
        return this.expression != null && this.expression instanceof ComplexExpression;
    }
    
    String getInput() {
        return this.input;
    }
    
    Expression getExpression() {
        return this.expression;
    }

    @Override
    public String toString() {
        return (isExpression()? 
                    "[" + this.expression.getStringRepresentation() + "]" : this.input);
    }
    
}
