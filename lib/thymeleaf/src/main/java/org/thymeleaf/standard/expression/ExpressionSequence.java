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

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class ExpressionSequence implements Iterable<IStandardExpression>, Serializable {

    private static final long serialVersionUID = -6069208208568731809L;
    

    private final List<IStandardExpression> expressions;
         
    public ExpressionSequence(final List<? extends IStandardExpression> expressions) {
        super();
        Validate.notNull(expressions, "Expression list cannot be null");
        Validate.containsNoNulls(expressions, "Expression list cannot contain any nulls");
        this.expressions = Collections.unmodifiableList(expressions);
    }

    
    public List<IStandardExpression> getExpressions() {
        return this.expressions;
    }
  
    public int size() {
        return this.expressions.size();
    }
    
    public Iterator<IStandardExpression> iterator() {
        return this.expressions.iterator();
    }

    public String getStringRepresentation() {
        final StringBuilder sb = new StringBuilder();
        if (this.expressions.size() > 0) {
            sb.append(this.expressions.get(0));
            for (int i = 1; i < this.expressions.size(); i++) {
                sb.append(',');
                sb.append(this.expressions.get(i));
            }
        }
        return sb.toString();
    }



    @Override
    public String toString() {
        return getStringRepresentation();
    }

    
}
