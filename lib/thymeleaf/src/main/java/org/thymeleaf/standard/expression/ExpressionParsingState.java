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

import java.util.ArrayList;

import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 2.1.0
 *
 */
final class ExpressionParsingState extends ArrayList<ExpressionParsingNode> {


    private static final long serialVersionUID = 3972191269638891028L;
    

    ExpressionParsingState() {
        super();
    }


    public void addNode(final String semiParsedString) {
        Validate.notNull(semiParsedString, "String cannot be null");
        this.add(new ExpressionParsingNode(semiParsedString));
    }

    public void addNode(final Expression parsedExpression) {
        Validate.notNull(parsedExpression, "Expression cannot be null");
        this.add(new ExpressionParsingNode(parsedExpression));
    }


    public void insertNode(final int pos, final String semiParsedString) {
        Validate.notNull(semiParsedString, "String cannot be null");
        this.add(pos, new ExpressionParsingNode(semiParsedString));
    }

    public void insertNode(final int pos, final Expression parsedExpression) {
        Validate.notNull(parsedExpression, "Expression cannot be null");
        this.add(pos, new ExpressionParsingNode(parsedExpression));
    }

    public void setNode(final int pos, final String semiParsedString) {
        Validate.notNull(semiParsedString, "String cannot be null");
        this.set(pos, new ExpressionParsingNode(semiParsedString));
    }

    public void setNode(final int pos, final Expression parsedExpression) {
        Validate.notNull(parsedExpression, "Expression cannot be null");
        this.set(pos, new ExpressionParsingNode(parsedExpression));
    }


    public boolean hasStringRoot() {
        return hasStringAt(0);
    }

    public boolean hasExpressionRoot() {
        return hasExpressionAt(0);
    }

    public boolean hasStringAt(final int pos) {
        return this.size() > pos && this.get(pos).isInput();
    }

    public boolean hasExpressionAt(final int pos) {
        return this.size() > pos && this.get(pos).isExpression();
    }


}
