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
package org.thymeleaf.util;




/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public enum NumberPointType {


    POINT("POINT"),
    COMMA("COMMA"),
    WHITESPACE("WHITESPACE"),
    NONE("NONE"),
    DEFAULT("DEFAULT");
    

    
    private final String name;

    
    
    public static NumberPointType match(final String name) {
        if ("NONE".equals(name)) {
            return NONE;
        }
        if ("DEFAULT".equals(name)) {
            return DEFAULT;
        }
        if ("POINT".equals(name)) {
            return POINT;
        }
        if ("COMMA".equals(name)) {
            return COMMA;
        }
        if ("WHITESPACE".equals(name)) {
            return WHITESPACE;
        }
        return null;
    }

    
    
    
    private NumberPointType(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
    
}