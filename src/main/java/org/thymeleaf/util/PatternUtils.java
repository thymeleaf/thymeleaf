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
package org.thymeleaf.util;

import java.util.regex.Pattern;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class PatternUtils {
    
    

    public static Pattern strPatternToPattern(final String pattern) {
        final String pat = 
            pattern.replace(".", "\\.").replace("(", "\\(").replace(")", "\\)").
                replace("[","\\[").replace("]","\\]").replace("?","\\?").replace("$","\\$").replace("+","\\+").
                replace("*","(?:.*?)");
        return Pattern.compile('^' + pat + '$');
    }
    
    
    
    private PatternUtils() {
        super();
    }
    
}
