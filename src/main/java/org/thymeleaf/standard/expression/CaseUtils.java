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

import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.util.StringUtils;

/**
 * <p>
 * The class composes a sequence of pairs for the SwitchCaseExpression
 * </p>
 * 
 * 
 * @author Kristina Sukhomlina
 * 
 * @since 3.1.0
 *
 */
public final class CaseUtils {

	private CaseUtils() {
		super();
	}

	static CaseSequence composeSequence(final String sequenceStr) {

		if (StringUtils.isEmpty(sequenceStr)) {
			return null;
		}

		final int indexOfCaseSep = sequenceStr.indexOf(',');

		// consider having just one case
		if (indexOfCaseSep == -1) {
			final int indexOfInnerCaseSep = sequenceStr.indexOf(':');

			// case format is invalid
			if (indexOfInnerCaseSep == -1) {
				return null;
			}

			final List<Case> cases = new ArrayList<Case>(1);
			final Case caseExpr = composeCase(sequenceStr);
			cases.add(caseExpr);
			return new CaseSequence(cases);
		}

		String[] input = sequenceStr.split(",");
		final List<Case> cases = new ArrayList<Case>(input.length);
		for (final String inputPart : input) {
			// if some case expression is invalid, consider leaving the cycle
			int partSep = inputPart.indexOf(':');
			int lastPartSep = inputPart.lastIndexOf(':');
			// two or more ':' are not allowed
			if (partSep != lastPartSep) {
				return null;
			}
			if (partSep == -1) {
				return null;
			}
			cases.add(composeCase(inputPart.trim()));
		}

		return new CaseSequence(cases);

	}

	static Case composeCase(final String input) {

		final int inputLen = input.length();
		if (inputLen == 1) {
			return null;
		}
		final int operatorPos = input.indexOf(':');

		final String leftInput = input.substring(0, operatorPos).trim();
		final String rightInput = input.substring(operatorPos + 1).trim();

		if (StringUtils.isEmptyOrWhitespace(leftInput)) {
			return null;
		}

		final Expression leftExpr = ExpressionParsingUtil.parseAndCompose(ExpressionParsingUtil.decompose(leftInput),
				leftInput);
		if (leftExpr == null) {
			return null;
		}

		final Expression rightExpr;
		if (!StringUtils.isEmptyOrWhitespace(rightInput)) {
			rightExpr = ExpressionParsingUtil.parseAndCompose(ExpressionParsingUtil.decompose(rightInput), rightInput);
			if (rightExpr == null) {
				return null;
			}
		} else {
			rightExpr = null;
		}

		return new Case(leftExpr, rightExpr);

	}
}
