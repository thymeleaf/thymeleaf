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
public final class Each implements Serializable {
    

    private static final long serialVersionUID = -4085690403057997591L;

    
    private final IStandardExpression iterVar;
    private final IStandardExpression statusVar;
    private final IStandardExpression iterable;
         
         
    public Each(final IStandardExpression iterVar, final IStandardExpression statusVar, final IStandardExpression iterable) {
        super();
        Validate.notNull(iterVar, "Iteration variable cannot be null");
        Validate.notNull(iterable, "Iterable cannot be null");
        this.iterVar = iterVar;
        this.statusVar = statusVar;
        this.iterable = iterable;
    }

    
    public IStandardExpression getIterVar() {
        return this.iterVar;
    }
    
    public boolean hasStatusVar() {
        return this.statusVar != null;
    }

    public IStandardExpression getStatusVar() {
        return this.statusVar;
    }

    public IStandardExpression getIterable() {
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
    
    

}
