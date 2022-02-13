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



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class LiteralValue implements Serializable {

    /*
     * This class is internally used during expression parsing in order to avoid
     * the interpretation of text literals like '4' or '2.' as numbers in
     * arithmetic operations.
     */
    
    private static final long serialVersionUID = -4769586410724418224L;
    
    private final String value;

    
    public LiteralValue(final String value) {
        super();
        this.value = value;
    }
    
    
    
    public String getValue() {
        return this.value;
    }
    
    
    public static Object unwrap(final Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof LiteralValue) {
            return ((LiteralValue)obj).getValue();
        }
        return obj;
    }


}
