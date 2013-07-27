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

import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;




/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class Each implements Serializable {
    

    private static final long serialVersionUID = -4085690403057997591L;

    private static final String OPERATOR = ":";
    private static final String STAT_SEPARATOR = ",";

    
    private final Expression iterVar;
    private final Expression statusVar;
    private final Expression iterable;
         
         
    private Each(final Expression iterVar, final Expression statusVar, final Expression iterable) {
        super();
        Validate.notNull(iterVar, "Iteration variable cannot be null");
        Validate.notNull(iterable, "Iterable cannot be null");
        this.iterVar = iterVar;
        this.statusVar = statusVar;
        this.iterable = iterable;
    }

    
    public Expression getIterVar() {
        return this.iterVar;
    }
    
    public boolean hasStatusVar() {
        return this.statusVar != null;
    }

    public Expression getStatusVar() {
        return this.statusVar;
    }

    public Expression getIterable() {
        return this.iterable;
    }
    

    public String getStringRepresentation() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.iterVar);
        if (hasStatusVar()) {
            sb.append(',');
            sb.append(this.statusVar);
        }
        sb.append(" : ");
        sb.append(this.iterable);
        return sb.toString();
    }

    
    @Override
    public String toString() {
        return getStringRepresentation();
    }
    
    
    
    
    
    static Each parse(final String input) {

        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }

        final ExpressionParsingState decomposition =
                ExpressionParsingUtil.decompose(input,ExpressionParsingDecompositionConfig.DECOMPOSE_ALL_AND_UNNEST);

        if (decomposition == null) {
            return null;
        }

        return composeEach(decomposition, 0);

    }




    private static Each composeEach(final ExpressionParsingState state, final int nodeIndex) {

        if (state == null || nodeIndex >= state.size()) {
            return null;
        }

        if (state.hasExpressionAt(nodeIndex)) {
            // shouldn't happen in this case (ExpressionSequences are not Expressions). We need a string to parse!
            return null;
        }

        final String input = state.get(nodeIndex).getInput();

        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }

        // First, check whether we are just dealing with a pointer input
        int pointer = ExpressionParsingUtil.parseAsSimpleIndexPlaceholder(input);
        if (pointer != -1) {
            return composeEach(state, pointer);
        }

        final int inputLen = input.length();

        final int operatorLen = OPERATOR.length();
        final int operatorPos = input.indexOf(OPERATOR);
        if (operatorPos == -1 || operatorPos == 0 || operatorPos >= (inputLen - operatorLen)) {
            return null;
        }

        final String left = input.substring(0,operatorPos).trim();
        final String iterableStr = input.substring(operatorPos + operatorLen).trim();

        final int statPos = left.indexOf(STAT_SEPARATOR);
        final String iterVarStr;
        final String statusVarStr;
        if (statPos == -1) {
            iterVarStr = left;
            statusVarStr = null;
        } else {
            if (statPos == 0 || statPos >= (left.length() - operatorLen)) {
                return null;
            }
            iterVarStr = left.substring(0, statPos);
            statusVarStr = left.substring(statPos + operatorLen);
        }

        final Expression iterVarExpr = ExpressionParsingUtil.parseAndCompose(state, iterVarStr);
        if (iterVarStr == null) {
            return null;
        }

        final Expression statusVarExpr;
        if (statusVarStr != null) {
            statusVarExpr = ExpressionParsingUtil.parseAndCompose(state, statusVarStr);
            if (statusVarExpr == null) {
                return null;
            }
        } else {
            statusVarExpr = null;
        }

        final Expression iterableExpr = ExpressionParsingUtil.parseAndCompose(state, iterableStr);
        if (iterableExpr == null) {
            return null;
        }

        return new Each(iterVarExpr,statusVarExpr,iterableExpr);

    }



}
