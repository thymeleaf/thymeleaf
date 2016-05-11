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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.thymeleaf.util.Validate;

/**
 * <p>
 * The CaseSequence contains pairs of cases of the SwitchCaseExpression
 * </p>
 * 
 * @author Kristina Sukhomlina
 * 
 * @since 3.1
 *
 */
public class CaseSequence implements Iterable<Case>, Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 8409266543995365130L;
	private final List<Case> cases;

	CaseSequence(final List<Case> cases) {
		super();
		Validate.notNull(cases, "Case list cannot be null");
		Validate.containsNoNulls(cases, "Case list cannot contain any nulls");
		this.cases = Collections.unmodifiableList(cases);
	}

	@Override
	public Iterator<Case> iterator() {
		return this.cases.iterator();
	}

	public List<Case> getCases() {
		return this.cases;
	}

	public int size() {
		return this.cases.size();
	}

	public String getStringRepresentation() {
		final StringBuilder sb = new StringBuilder();
		if (this.cases.size() > 0) {
			sb.append(this.cases.get(0));
			for (int i = 1; i < this.cases.size(); i++) {
				sb.append(',');
				sb.append('\n');
				sb.append(this.cases.get(i));
			}
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return getStringRepresentation();
	}
}
