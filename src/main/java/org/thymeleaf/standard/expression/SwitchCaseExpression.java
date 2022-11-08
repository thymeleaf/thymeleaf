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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/**
 * <p>
 * Switch/case complex expression (Thymeleaf Standard Expressions). The text
 * before the operator is a switch variable. The sequence after the '??'
 * contains pairs of possible matches to the switch variable and their values to
 * be assigned in case of match.
 * </p>
 * 
 * @author Kristina Sukhomlina
 * 
 * @since 3.1.0
 * 
 */
public final class SwitchCaseExpression extends ComplexExpression {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4265295833340208144L;

	private static final Logger logger = LoggerFactory.getLogger(SwitchCaseExpression.class);

	private static final String OPERATOR = "??";
	private static final char CASE_SEPARATOR_CHAR = ':';

	static final String[] OPERATORS = { OPERATOR, String.valueOf(CASE_SEPARATOR_CHAR) };

	private final Expression switchConditionExpression;

	private final CaseSequence caseSequence;

	public SwitchCaseExpression(final Expression switchConditionExpression, final CaseSequence cases) {
		super();
		Validate.notNull(switchConditionExpression, "Switch condition cannot be null");
		this.switchConditionExpression = switchConditionExpression;
		this.caseSequence = cases;
	}

	public boolean hasCases() {
		return this.caseSequence != null && this.caseSequence.size() > 0;
	}

	@Override
	public String getStringRepresentation() {
		final StringBuilder sb = new StringBuilder();

		if (this.switchConditionExpression instanceof ComplexExpression) {
			sb.append(Expression.NESTING_START_CHAR);
			sb.append(this.switchConditionExpression);
			sb.append(Expression.NESTING_END_CHAR);
		} else {
			sb.append(this.switchConditionExpression);
		}

		sb.append(OPERATOR);

		if (this.hasCases()) {
			sb.append(this.caseSequence.getStringRepresentation());
		}
		return sb.toString();
	}

	public Expression getSwitchConditionExpression() {
		return this.switchConditionExpression;
	}

	public CaseSequence getCases() {
		return caseSequence;
	}

	public static ExpressionParsingState composeSwitchCaseExpression(final ExpressionParsingState state,
			final int nodeIndex) {

		final String input = state.get(nodeIndex).getInput();
		if (StringUtils.isEmptyOrWhitespace(input)) {
			return null;
		}

		// Trying to fail quickly...
		final int operatorPos = input.indexOf(OPERATOR);
		if (operatorPos == -1) {
			return state;
		}

		final String switchStr = input.substring(0, operatorPos).trim();
		final String remainder = input.substring(operatorPos + 1).trim();

		// Check the switch condition and fail quickly if it is not valid
		final Expression switchExpr = ExpressionParsingUtil.parseAndCompose(state, switchStr);
		if (switchExpr == null) {
			return null;
		}

		// remainder string should represent the case sequence
		// condition: at least 1 case should exist

		CaseSequence sequence = CaseUtils.composeSequence(remainder);

		if (sequence == null) {
			return null;
		}

		final SwitchCaseExpression switchCaseExpressionResult = new SwitchCaseExpression(switchExpr, sequence);
		state.setNode(nodeIndex, switchCaseExpressionResult);

		return state;
	}

	public static Object executeSwitchCase(IExpressionContext context, SwitchCaseExpression expression,
			StandardExpressionExecutionContext expContext) {

		if (logger.isTraceEnabled()) {
			logger.trace("[THYMELEAF][{}] Evaluating switch expression: \"{}\"", TemplateEngine.threadIndex(),
					expression.getStringRepresentation());
		}

		// obtain switch condition
		final Object switchObj = expression.getSwitchConditionExpression().execute(context, expContext);
		Object defaultCase = null;

		// compare each case 'key' with switch variable
		// if the switch variable matches on of the cases, return its value
		for (Case caseExpr : expression.getCases()) {
			Object caseObj = caseExpr.getCaseExpr().execute(context, expContext);

			// consider default value to be returned in case of no better match
			if (StringUtils.equalsIgnoreCase(caseObj, "default")) {
				defaultCase = caseExpr.getCaseExprValue().execute(context, expContext);
			}

			if (StringUtils.equalsIgnoreCase(switchObj, caseObj)) {
				return caseExpr.getCaseExprValue().execute(context, expContext);
			}
		}

		return defaultCase;
	}
}
