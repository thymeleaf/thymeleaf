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
package org.thymeleaf.standard.processor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FragmentInsertionExpressionTest {


    public FragmentInsertionExpressionTest() {
        super();
    }



    @Test
    public void testFragmentExpressionSelection() throws Exception {
        checkExpression("template", true);
        checkExpression("template::f", true);
        checkExpression("template::frag", true);
        checkExpression("template :: frag", true);
        checkExpression("  template :: frag   ", true);
        checkExpression("   :: frag   ", true);
        checkExpression("::frag   ", true);
        checkExpression("::frag", true);
        checkExpression("this::frag", true);
        checkExpression(" this   ::frag", true);
        checkExpression(" this   :: frag", true);
        checkExpression(" ${lala slatr} + 'ele'   :: 'index_' + 2 * 2", true);
        checkExpression(" ${lala slatr} + 'ele'   :: ('index_' + 2 * 2)", true);
        checkExpression(" ${lala slatr} + 'ele'   :: ('index_' + (2 * 2)) (somePar)", true);
        checkExpression(" ${lala slatr} + 'ele'   :: ('index_' + (2 * 2)) (a='something')", true);
        checkExpression(" ${lala slatr} + 'ele'   :: ('index_' + (2 * 2)) (a='something',b=4123)", true);
        checkExpression(" ${lala slatr} + 'ele'   :: ('index_' + (2 * 2)) (a=('something'),b=4123)", true);
        checkExpression(" ${lala slatr} + ('ele')   :: ('index_' + (2 * 2)) (a=('something'),b=4123)", true);
        checkExpression(" ${lala slatr} + ('ele')   :: ('index_' + (2 * 2)) (a=('something' + 23),b=4123)", true);
        checkExpression(" ${lala slatr}+'ele'   :: ('index_'+(2*2)) (a=('something'+23),b=4123)", true);
        checkExpression(" ${lala slatr}+'ele'   :: ('index_'+(2*2)) (${name}=('something'+23),b=4123)", true);
        checkExpression(" ${lala slatr}+'ele'   :: ('index_'+(2*2)) ((${name} + 0)=('something'+23),b=4123)", true);
        checkExpression("C:\\Program Files\\apps\\templates\\WEB-INF\\temp.html", true);
        checkExpression("C:\\Program Files\\apps\\templates\\WEB-INF\\temp.html :: 'fragment number one'", true);
        checkExpression("/home/user/apps/templates/WEB-INF/temp.html :: 'fragment number one'", true);
        checkExpression("home/user :: 'fragment number one'", true);
        checkExpression("${something}", true);
        checkExpression("${this} :: ${that}", true);
        checkExpression("~{whatever}", false);
        checkExpression("${cond} ? ~{this} : ~{that}", false);
        checkExpression("${something} :: /div", true);
        checkExpression("template :: f (~{some})", true);
        checkExpression("folder/template :: f (~{some})", true);
        checkExpression("folder/template :: f (~{some})", true);
        checkExpression("~folder/template :: f (~{some})", true);
        checkExpression("~/folder/template :: f (~{some})", true);
        checkExpression("${~{impossible}} :: f (~{some})", true);
        checkExpression("'~{impossible}' :: f (~{some})", true);
        checkExpression("folder/template (title=~{some})", true);
        checkExpression("(~{some})", false);
        checkExpression("(${cond}) ? (~{this}) : (~{that})", false);
        checkExpression("folder/template (title='one',body=~{that})", true);
        checkExpression("folder/template (title=(~{some}))", true);
        checkExpression("folder/template (title=('one'),body=(~{that}))", true);
        checkExpression("folder/template (title=('one'))", true);
        checkExpression("folder/template (body=~{(that)})", true);
        checkExpression("folder/template\n (body=~{(that)})", true);
        checkExpression("~{folder/template :: f (~{some})}", false);
        checkExpression("     ~{folder/template :: f (~{some})}   ", false);
        // We could think that in this case a "true" should be returned, but actually the expression below is completely
        // invalid because we cannot call a template without fragment specification using synthetic parameters.
        checkExpression("folder/template (~{some})", false);
    }




    private static void checkExpression(final String expression, final boolean result) {
        if (result) {
            Assertions.assertTrue(AbstractStandardFragmentInsertionTagProcessor.shouldBeWrappedAsFragmentExpression(expression));
        } else {
            Assertions.assertFalse(AbstractStandardFragmentInsertionTagProcessor.shouldBeWrappedAsFragmentExpression(expression));
        }
    }

}
