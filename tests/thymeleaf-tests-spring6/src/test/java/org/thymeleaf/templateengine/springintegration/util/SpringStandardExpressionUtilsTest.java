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
package org.thymeleaf.templateengine.springintegration.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.util.SpringStandardExpressionUtils;


public final class SpringStandardExpressionUtilsTest {



    @Test
    public void testcontainsExternalAccess() {

        Assertions.assertFalse(SpringStandardExpressionUtils.containsExternalAccess("abcnew"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsExternalAccess("abcnew "));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsExternalAccess("abc3new "));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsExternalAccess("abc_new "));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("abc$new "));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("abc-new "));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("abc new "));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("abc.new "));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsExternalAccess("abc newnew"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsExternalAccess("abcnew ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("abc new ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("abc new w ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("abc new w ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("abc (new )w ewnew"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsExternalAccess("abc (new)w ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("abc +new )w ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("new "));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("new "));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsExternalAccess("newnew"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsExternalAccess("ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("new ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("new w ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("new w ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("(new )w ewnew"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsExternalAccess("(new)w ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("+new )w ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("!new )w ewnew"));

        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("T()"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("T(a)"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("T(a.b.SomeClass)"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("T(a.b.SomenewClass)"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("T(a.b.Some Class)"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("T(a.b.Some newClass)"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("T(a.b.Some new Class)"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("T(a.b.Some newClass)new"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("T(a.b.Some newClass)new "));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsExternalAccess("newT(a.b.Some newClass)new"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsExternalAccess("aT(a.b.Some newClass)a"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("a T(a.b.Some newClass) a"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsExternalAccess(" aT(a.b.Some newClass)a "));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("a-T(a.b.SomeClass)a"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsExternalAccess("aT(a.b.SomeClass)a"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("a T(a.b.SomeClass) a"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsExternalAccess(" aT(a.b.SomeClass)a "));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess(" a T(a.b.SomeClass)a "));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("a T(a.b.SomeClass) a T(a.b.Some Class)"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("a T(a.b.Some Class) a T(a.b.SomeClass)"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("a T(a.b.Some Class) a T(a.b.Some Class)"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("a T(a.b.SomeClass) )"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("a T(a.b.SomeClass))"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("a T(a.b.Some(Class) )"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("a T(a.b.Some)Class) )"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("a T(a.b.Som(e)Class) )"));


        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess("param.a"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess(" param.a"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsExternalAccess(" param['a']"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsExternalAccess("_param['a']"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsExternalAccess(" param_a"));

    }

}
