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

import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class Assignation implements Serializable {

    private static final long serialVersionUID = -20278893925937213L;
    

    private final IStandardExpression left;
    private final IStandardExpression right;
         
    
    Assignation(final IStandardExpression left, final IStandardExpression right) {
        super();
        Validate.notNull(left, "Assignation left side cannot be null");
        this.left = left;
        this.right = right;
    }



    public IStandardExpression getLeft() {
        return this.left;
    }

    public IStandardExpression getRight() {
        return this.right;
    }
    
    public String getStringRepresentation() {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(this.left.getStringRepresentation());
        if (this.right != null) {
            strBuilder.append('=');
            if (this.right instanceof ComplexExpression) {
                strBuilder.append('(');
                strBuilder.append(this.right.getStringRepresentation());
                strBuilder.append(')');
            } else {
                strBuilder.append(this.right.getStringRepresentation());
            }
        }
        return strBuilder.toString();
    }


    @Override
    public String toString() {
        return getStringRepresentation();
    }
  
    

    
}
