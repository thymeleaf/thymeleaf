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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    private static final Pattern EACH_PATTERN = 
        Pattern.compile("^\\s*(.*?)\\s*(?:,\\s*(.+?)\\s*)?\\:\\s*(.+?)\\s*$", Pattern.DOTALL);

    
    
    private final Token iterVar;
    private final Token statusVar;
    private final Expression iterable;
         
         
    private Each(final Token iterVar, final Token statusVar, final Expression iterable) {
        super();
        Validate.notNull(iterVar, "Iteration variable cannot be null");
        Validate.notNull(iterable, "Iterable cannot be null");
        this.iterVar = iterVar;
        this.statusVar = statusVar;
        this.iterable = iterable;
    }

    
    public Token getIterVar() {
        return this.iterVar;
    }
    
    public boolean hasStatusVar() {
        return this.statusVar != null && !StringUtils.isEmptyOrWhitespace(this.statusVar.getValue());
    }

    public Token getStatusVar() {
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
        
        final String trimmedInput = input.trim();
        
        final Matcher matcher = EACH_PATTERN.matcher(trimmedInput);
        if (!matcher.matches()) {
            return null;
        }
        
        final String iterVar = matcher.group(1);
        final String statusVar = matcher.group(2);
        final String iterable = matcher.group(3);
        
        if (StringUtils.isEmptyOrWhitespace(iterVar)) {
            return null;
        }
        if (StringUtils.isEmptyOrWhitespace(iterable)) {
            return null;
        }
        final Expression iterableExpression = Expression.parse(iterable);
        if (iterableExpression == null) {
            return null;
        }
        
        final Token iterVarToken = Token.parse(iterVar);
        if (iterVarToken == null) {
            return null;
        }
        
        Each each = null;
        if (!StringUtils.isEmptyOrWhitespace(statusVar)) {
            final Token statusVarToken = Token.parse(statusVar);
            if (statusVarToken == null) {
                return null;
            }
            each = new Each(iterVarToken, statusVarToken, iterableExpression);
        } else {
            each = new Each(iterVarToken, null, iterableExpression);
        }
        
        return each;
        
    }
    
    
    
    
}
