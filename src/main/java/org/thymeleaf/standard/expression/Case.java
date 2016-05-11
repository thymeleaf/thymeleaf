/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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

import org.thymeleaf.util.Validate;

/**
 * <p>
 * Case is pair of switch variable matching expression and its value
 * </p>
 * 
 * @author Kristina Sukhomlina
 * 
 * @since 3.1
 *
 */
public final class Case implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 3851314623222001988L;
	private final IStandardExpression caseExpr;
	private final IStandardExpression caseExprValue;

	Case(final IStandardExpression caseExpr, final IStandardExpression caseExprValue) {
		super();
		Validate.notNull(caseExpr, "Case expression condition cannot be null");
		this.caseExpr = caseExpr;
		this.caseExprValue = caseExprValue;
	}

	public IStandardExpression getCaseExpr() {
		return this.caseExpr;
	}

	public IStandardExpression getCaseExprValue() {
		return this.caseExprValue;
	}

	public String getStringRepresentation() {
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(this.caseExpr.getStringRepresentation());
		if (this.caseExprValue != null) {
			strBuilder.append(" : ");
			if (this.caseExprValue instanceof ComplexExpression) {
				strBuilder.append('(');
				strBuilder.append(this.caseExprValue.getStringRepresentation());
				strBuilder.append(')');
			} else {
				strBuilder.append(this.caseExprValue.getStringRepresentation());
			}
		}
		return strBuilder.toString();
	}

	@Override
	public String toString() {
		return getStringRepresentation();
	}
}
