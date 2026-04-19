/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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
import org.thymeleaf.standard.util.StandardExpressionUtils;


public final class StandardExpressionUtilsTest {



    @Test
    public void testcontainsExternalAccess() {

        Assertions.assertFalse(StandardExpressionUtils.containsExternalAccess("abcnew"));
        Assertions.assertFalse(StandardExpressionUtils.containsExternalAccess("abcnew "));
        Assertions.assertFalse(StandardExpressionUtils.containsExternalAccess("abc3new "));
        Assertions.assertFalse(StandardExpressionUtils.containsExternalAccess("abc_new "));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("abc$new "));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("abc-new "));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("abc new "));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("abc.new "));
        Assertions.assertFalse(StandardExpressionUtils.containsExternalAccess("abc newnew"));
        Assertions.assertFalse(StandardExpressionUtils.containsExternalAccess("abcnew ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("abc new ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("abc new w ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("abc new w ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("abc (new )w ewnew"));
        Assertions.assertFalse(StandardExpressionUtils.containsExternalAccess("abc (new)w ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("abc +new )w ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("new "));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("new "));
        Assertions.assertFalse(StandardExpressionUtils.containsExternalAccess("newnew"));
        Assertions.assertFalse(StandardExpressionUtils.containsExternalAccess("ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("new ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("new w ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("new w ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("(new )w ewnew"));
        Assertions.assertFalse(StandardExpressionUtils.containsExternalAccess("(new)w ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("+new )w ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("!new )w ewnew"));

        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("@@"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("@a@"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("@a.b.SomeClass@"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("@a.b.SomenewClass@"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("@a.b.Some Class@"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("@a.b.Some newClass@"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("@a.b.Some new Class@"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("@a.b.Some newClass@new"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("@a.b.Some newClass@new "));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("new@a.b.Some newClass@new"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("a@a.b.Some newClass@a"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("a @a.b.Some newClass@ a"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess(" a@a.b.Some newClass@a "));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("a@a.b.SomeClass@a"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("a @a.b.SomeClass@ a"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess(" a@a.b.SomeClass@a "));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("a @a.b.SomeClass@ a @a.b.Some Class@"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("a @a.b.Some Class@ a @a.b.SomeClass@"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("a @a.b.Some Class@ a @a.b.Some Class@"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("a @a.b.SomeClass@ @"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("a @a.b.SomeClass@@"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("a @  a.b.SomeClass@@"));


        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess("param.a"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess(" param.a"));
        Assertions.assertTrue(StandardExpressionUtils.containsExternalAccess(" param['a']"));
        Assertions.assertFalse(StandardExpressionUtils.containsExternalAccess("_param['a']"));
        Assertions.assertFalse(StandardExpressionUtils.containsExternalAccess(" param_a"));

    }

}
