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
package org.thymeleaf.templateengine.springintegration.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.util.SpringStandardExpressionUtils;


public final class SpringStandardExpressionUtilsTest {



    @Test
    public void testcontainsSpELInstantiationOrStaticOrParam() {

        Assertions.assertFalse(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("abcnew"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("abcnew "));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("abc3new "));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("abc_new "));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("abc$new "));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("abc-new "));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("abc new "));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("abc.new "));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("abc newnew"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("abcnew ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("abc new ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("abc new w ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("abc new w ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("abc (new )w ewnew"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("abc (new)w ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("abc +new )w ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("new "));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("new "));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("newnew"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("new ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("new w ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("new w ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("(new )w ewnew"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("(new)w ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("+new )w ewnew"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("!new )w ewnew"));

        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("T()"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("T(a)"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("T(a.b.SomeClass)"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("T(a.b.SomenewClass)"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("T(a.b.Some Class)"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("T(a.b.Some newClass)"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("T(a.b.Some new Class)"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("T(a.b.Some newClass)new"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("T(a.b.Some newClass)new "));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("newT(a.b.Some newClass)new"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("aT(a.b.Some newClass)a"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("a T(a.b.Some newClass) a"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam(" aT(a.b.Some newClass)a "));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("a-T(a.b.SomeClass)a"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("aT(a.b.SomeClass)a"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("a T(a.b.SomeClass) a"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam(" aT(a.b.SomeClass)a "));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam(" a T(a.b.SomeClass)a "));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("a T(a.b.SomeClass) a T(a.b.Some Class)"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("a T(a.b.Some Class) a T(a.b.SomeClass)"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("a T(a.b.Some Class) a T(a.b.Some Class)"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("a T(a.b.SomeClass) )"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("a T(a.b.SomeClass))"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("a T(a.b.Some(Class) )"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("a T(a.b.Some)Class) )"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("a T(a.b.Som(e)Class) )"));


        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("param.a"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam(" param.a"));
        Assertions.assertTrue(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam(" param['a']"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam("_param['a']"));
        Assertions.assertFalse(SpringStandardExpressionUtils.containsSpELInstantiationOrStaticOrParam(" param_a"));

    }

}
