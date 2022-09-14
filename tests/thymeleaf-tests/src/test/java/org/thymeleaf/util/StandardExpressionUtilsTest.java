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
import org.thymeleaf.standard.util.StandardExpressionUtils;


public final class StandardExpressionUtilsTest {



    @Test
    public void testMightNeedExpressionObjects() {

        Assertions.assertTrue(StandardExpressionUtils.mightNeedExpressionObjects("${execInfo}"));
        Assertions.assertTrue(StandardExpressionUtils.mightNeedExpressionObjects("${exexecInfo}"));
        Assertions.assertTrue(StandardExpressionUtils.mightNeedExpressionObjects("${exexecInfofo}"));
        Assertions.assertFalse(StandardExpressionUtils.mightNeedExpressionObjects("alasdasdisad lj"));
        Assertions.assertFalse(StandardExpressionUtils.mightNeedExpressionObjects("alasdasdisad\n"));
        Assertions.assertFalse(StandardExpressionUtils.mightNeedExpressionObjects("alasdasdisad\nexecInf"));
        Assertions.assertFalse(StandardExpressionUtils.mightNeedExpressionObjects("alasdasdisad\naxecInfo\na"));
        Assertions.assertTrue(StandardExpressionUtils.mightNeedExpressionObjects("alasdasdisad\nexecInfo\na"));
        Assertions.assertTrue(StandardExpressionUtils.mightNeedExpressionObjects("alasdasdisad#\n"));
        Assertions.assertTrue(StandardExpressionUtils.mightNeedExpressionObjects("alasdasd#isad\nexecInf"));
        Assertions.assertTrue(StandardExpressionUtils.mightNeedExpressionObjects("alasdasdisad\n#axecInfo\na"));

    }


    @Test
    public void testcontainsOGNLInstantiationOrStaticOrParam() {

        Assertions.assertFalse(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("abcnew"));
        Assertions.assertFalse(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("abcnew "));
        Assertions.assertFalse(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("abc3new "));
        Assertions.assertFalse(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("abc_new "));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("abc$new "));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("abc-new "));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("abc new "));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("abc.new "));
        Assertions.assertFalse(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("abc newnew"));
        Assertions.assertFalse(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("abcnew ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("abc new ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("abc new w ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("abc new w ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("abc (new )w ewnew"));
        Assertions.assertFalse(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("abc (new)w ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("abc +new )w ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("new "));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("new "));
        Assertions.assertFalse(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("newnew"));
        Assertions.assertFalse(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("new ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("new w ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("new w ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("(new )w ewnew"));
        Assertions.assertFalse(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("(new)w ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("+new )w ewnew"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("!new )w ewnew"));

        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("@@"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("@a@"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("@a.b.SomeClass@"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("@a.b.SomenewClass@"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("@a.b.Some Class@"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("@a.b.Some newClass@"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("@a.b.Some new Class@"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("@a.b.Some newClass@new"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("@a.b.Some newClass@new "));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("new@a.b.Some newClass@new"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("a@a.b.Some newClass@a"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("a @a.b.Some newClass@ a"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam(" a@a.b.Some newClass@a "));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("a@a.b.SomeClass@a"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("a @a.b.SomeClass@ a"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam(" a@a.b.SomeClass@a "));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("a @a.b.SomeClass@ a @a.b.Some Class@"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("a @a.b.Some Class@ a @a.b.SomeClass@"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("a @a.b.Some Class@ a @a.b.Some Class@"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("a @a.b.SomeClass@ @"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("a @a.b.SomeClass@@"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("a @  a.b.SomeClass@@"));


        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("param.a"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam(" param.a"));
        Assertions.assertTrue(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam(" param['a']"));
        Assertions.assertFalse(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam("_param['a']"));
        Assertions.assertFalse(StandardExpressionUtils.containsOGNLInstantiationOrStaticOrParam(" param_a"));

    }

}
