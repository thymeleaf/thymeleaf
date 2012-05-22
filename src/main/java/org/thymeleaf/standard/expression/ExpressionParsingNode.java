/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
    private final Token token;
    private final Expression expression;
    private final Assignation assignation;
    private final AssignationSequence assignationSequence;
    private final ExpressionSequence expressionSequence;
    
    ExpressionParsingNode(final String input) {
        super();
        // INPUT SHOULD NEVER BE NULL!!!
        this.input = input.trim();
        this.token = null;
        this.expression = null;
        this.assignation = null;
        this.assignationSequence = null;
        this.expressionSequence = null;
    }
    
    ExpressionParsingNode(final Expression expression) {
        super();
        this.expression = expression;
        this.token = null;
        this.input = null;
        this.assignation = null;
        this.assignationSequence = null;
        this.expressionSequence = null;
    }
    
    ExpressionParsingNode(final Token token) {
        super();
        this.token = token;
        this.expression = null;
        this.input = null;
        this.assignation = null;
        this.assignationSequence = null;
        this.expressionSequence = null;
    }
    
    ExpressionParsingNode(final Assignation assignation) {
        super();
        this.assignation = assignation;
        this.token = null;
        this.expression = null;
        this.input = null;
        this.assignationSequence = null;
        this.expressionSequence = null;
    }
    
    ExpressionParsingNode(final AssignationSequence assignationSequence) {
        super();
        this.assignationSequence = assignationSequence;
        this.assignation = null;
        this.token = null;
        this.expression = null;
        this.input = null;
        this.expressionSequence = null;
    }
    
    ExpressionParsingNode(final ExpressionSequence expressionSequence) {
        super();
        this.expressionSequence = expressionSequence;
        this.assignationSequence = null;
        this.assignation = null;
        this.token = null;
        this.expression = null;
        this.input = null;
    }
    
    boolean isInput() {
        return this.input != null;
    }
    
    boolean isAssignation() {
        return this.assignation != null;
    }
    
    boolean isAssignationSequence() {
        return this.assignationSequence != null;
    }
    
    boolean isExpressionSequence() {
        return this.expressionSequence != null;
    }
    
    boolean isToken() {
        return this.token != null;
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
    
    Token getToken() {
        return this.token;
    }
    
    Assignation getAssignation() {
        return this.assignation;
    }
    
    AssignationSequence getAssignationSequence() {
        return this.assignationSequence;
    }
    
    ExpressionSequence getExpressionSequence() {
        return this.expressionSequence;
    }
    
    @Override
    public String toString() {
        return (isExpression()? 
                    "[" + this.expression.getStringRepresentation() + "]" : 
                    (isToken()? 
                        this.token.getStringRepresentation() : 
                        (isAssignation()? 
                            this.assignation.getStringRepresentation() : 
                            (isAssignationSequence()? 
                                this.assignationSequence.getStringRepresentation() :
                                (isExpressionSequence()?
                                    this.expressionSequence.getStringRepresentation() :
                                    this.input)))));
    }
    
}
