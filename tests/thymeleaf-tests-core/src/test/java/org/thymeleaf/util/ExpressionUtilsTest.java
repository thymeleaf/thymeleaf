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
package org.thymeleaf.util;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.thymeleaf.util.ExpressionUtils.*;


public final class ExpressionUtilsTest {

    @Test
    public void testNormalizeExpression() {
        Assertions.assertNull(normalize(null));
        Assertions.assertEquals("", normalize(""));
        final String exp00 = "${something}";
        Assertions.assertSame(exp00, normalize(exp00));
        Assertions.assertEquals("${some thing}", normalize("${some thing}"));
        Assertions.assertEquals("${some \nthing}", normalize("${some \nthing}"));
        Assertions.assertEquals("${some thing}", normalize("${some \0thing}"));
        Assertions.assertEquals("${some thing}", normalize("${some \tthing}"));
        Assertions.assertEquals("${some thing}", normalize("${some t\t\thing}"));
        Assertions.assertEquals("${some thing}", normalize("\t${some t\t\thing}"));
        Assertions.assertEquals("${some thing}", normalize("\t${some thing}"));
        Assertions.assertEquals("${some thing}", normalize("\t${some t\t\thing}\t"));
        Assertions.assertEquals("${some thing}", normalize("\t${some thing}\t"));
        Assertions.assertEquals("${some thing}", normalize("${some t\t\thing}\t"));
    }


}
