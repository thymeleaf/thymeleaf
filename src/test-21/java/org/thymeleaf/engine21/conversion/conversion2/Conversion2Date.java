/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.engine21.conversion.conversion2;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Conversion2Date extends Date {

    /*
     * This class acts as a wrapper on a standard Date object that strips the timezone from the output
     * it produces when toString() is called. This allows tests to not depend on the timezone of the
     * computer that is execting them.
     */

    private static final Pattern PATTERN;


    static {
        String exp=".*?(?:[a-z][a-z]+).*?(?:[a-z][a-z]+).*?((?:[a-z][a-z]+))";	// Word 1
        PATTERN = Pattern.compile(exp,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    }


    public Conversion2Date(final Date date) {
        super();
        this.setTime(date.getTime());
    }

    @Override
    public String toString() {
        final String standardToString = super.toString();
        final Matcher matcher = PATTERN.matcher(standardToString);
        if (matcher.find()) {
            final String timeZone = matcher.group(1);
            return standardToString.replace(" " + timeZone, "");
        }
        return standardToString;
    }

}
