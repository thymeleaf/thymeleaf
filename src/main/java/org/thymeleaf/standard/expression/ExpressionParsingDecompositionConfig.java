/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
 * @since 2.1.0
 *
 */
public final class ExpressionParsingDecompositionConfig {


    public static final ExpressionParsingDecompositionConfig DECOMPOSE_ALL_AND_UNNEST =
            new ExpressionParsingDecompositionConfig(true,true,true,true,true,true,true,true,true, true);
    public static final ExpressionParsingDecompositionConfig DECOMPOSE_ALL_AND_NOT_UNNEST =
            new ExpressionParsingDecompositionConfig(true,true,true,true,true,true,true,true,true, false);
    public static final ExpressionParsingDecompositionConfig DECOMPOSE_NO_LITERALS_NO_TOKENS_AND_UNNEST =
            new ExpressionParsingDecompositionConfig(false,false,false,false,false,true,true,true,true, true);
    public static final ExpressionParsingDecompositionConfig DECOMPOSE_NO_LITERALS_NO_TOKENS_AND_NOT_UNNEST =
            new ExpressionParsingDecompositionConfig(false,false,false,false,false,true,true,true,true, false);


    private final boolean decomposeTextLiterals;

    private final boolean decomposeNumberTokens;
    private final boolean decomposeBooleanTokens;
    private final boolean decomposeNullTokens;
    private final boolean decomposeGenericTokens;

    private final boolean decomposeVariableExpressions;
    private final boolean decomposeSelectionVariableExpressions;
    private final boolean decomposeLinkExpressions;
    private final boolean decomposeMessageExpressions;

    private final boolean unnest;


    public ExpressionParsingDecompositionConfig(
            final boolean decomposeTextLiterals,
            final boolean decomposeNumberTokens,
            final boolean decomposeBooleanTokens,
            final boolean decomposeNullTokens,
            final boolean decomposeGenericTokens,
            final boolean decomposeVariableExpressions,
            final boolean decomposeSelectionVariableExpressions,
            final boolean decomposeLinkExpressions,
            final boolean decomposeMessageExpressions,
            final boolean unnest) {
        super();
        this.decomposeTextLiterals = decomposeTextLiterals;
        this.decomposeNumberTokens = decomposeNumberTokens;
        this.decomposeBooleanTokens = decomposeBooleanTokens;
        this.decomposeNullTokens = decomposeNullTokens;
        this.decomposeGenericTokens = decomposeGenericTokens;
        this.decomposeVariableExpressions = decomposeVariableExpressions;
        this.decomposeSelectionVariableExpressions = decomposeSelectionVariableExpressions;
        this.decomposeLinkExpressions = decomposeLinkExpressions;
        this.decomposeMessageExpressions = decomposeMessageExpressions;
        this.unnest = unnest;
    }


    public boolean getDecomposeTextLiterals() {
        return this.decomposeTextLiterals;
    }

    public boolean getDecomposeNumberTokens() {
        return this.decomposeNumberTokens;
    }

    public boolean getDecomposeBooleanTokens() {
        return this.decomposeBooleanTokens;
    }

    public boolean getDecomposeNullTokens() {
        return this.decomposeNullTokens;
    }

    public boolean getDecomposeGenericTokens() {
        return this.decomposeGenericTokens;
    }

    public boolean getDecomposeVariableExpressions() {
        return this.decomposeVariableExpressions;
    }

    public boolean getDecomposeSelectionVariableExpressions() {
        return this.decomposeSelectionVariableExpressions;
    }

    public boolean getDecomposeLinkExpressions() {
        return this.decomposeLinkExpressions;
    }

    public boolean getDecomposeMessageExpressions() {
        return this.decomposeMessageExpressions;
    }

    public boolean getUnnest() {
        return this.unnest;
    }

    public boolean getDecomposeAnyTokens() {
        return this.decomposeBooleanTokens || this.decomposeNumberTokens || this.decomposeNullTokens || this.decomposeGenericTokens;
    }


    @Override
    public String toString() {
        return "ExpressionParsingDecompositionConfig{" +
                "decomposeTextLiterals=" + this.decomposeTextLiterals +
                ", decomposeNumberTokens=" + this.decomposeNumberTokens +
                ", decomposeBooleanTokens=" + this.decomposeBooleanTokens +
                ", decomposeNullTokens=" + this.decomposeNullTokens +
                ", decomposeGenericTokens=" + this.decomposeGenericTokens +
                ", decomposeVariableExpressions=" + this.decomposeVariableExpressions +
                ", decomposeSelectionVariableExpressions=" + this.decomposeSelectionVariableExpressions +
                ", decomposeLinkExpressions=" + this.decomposeLinkExpressions +
                ", decomposeMessageExpressions=" + this.decomposeMessageExpressions +
                ", unnest=" + this.decomposeMessageExpressions +
                '}';
    }
}
