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
package org.thymeleaf.standard.expression;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.thymeleaf.util.StringUtils;


public class FragmentSelectionTest {


    public FragmentSelectionTest() {
        super();
    }



    @Test
    public void testFragmentSelection() throws Exception {
        checkFragmentSelection("template", "template", null, null);
        checkFragmentSelection("template::f", "template", "f", null);
        checkFragmentSelection("template::frag", "template", "frag", null);
        checkFragmentSelection("template :: frag", "template", "frag", null);
        checkFragmentSelection("  template :: frag   ", "template", "frag", null);
        checkFragmentSelection("   :: frag   ", null, "frag", null);
        checkFragmentSelection("::frag   ", null, "frag", null);
        checkFragmentSelection("::frag", null, "frag", null);
        checkFragmentSelection("this::frag", "this", "frag", null);
        checkFragmentSelection(" this   ::frag", "this", "frag", null);
        checkFragmentSelection(" this   :: frag", "this", "frag", null);
        checkFragmentSelection(" ${lala slatr} + 'ele'   :: 'index_' + 2 * 2", "${lala slatr} + 'ele'", "'index_' + (2 * 2)", null);
        checkFragmentSelection(" ${lala slatr} + 'ele'   :: ('index_' + 2 * 2)", "${lala slatr} + 'ele'", "'index_' + (2 * 2)", null);
        // This is wrong but it is how it should work, as the last () are considered parameters
        checkFragmentSelection(" ${lala slatr} + 'ele'   :: 'index_' + (2 * 2)", "${lala slatr} + 'ele'", "'\\'index_\\' +'", "'_arg0'=(2 * 2)");
        checkFragmentSelection(" ${lala slatr} + 'ele'   :: ('index_' + (2 * 2)) (somePar)", "${lala slatr} + 'ele'", "'index_' + (2 * 2)", "'_arg0'=somePar");
        checkFragmentSelection(" ${lala slatr} + 'ele'   :: ('index_' + (2 * 2)) (a='something')", "${lala slatr} + 'ele'", "'index_' + (2 * 2)", "a='something'");
        checkFragmentSelection(" ${lala slatr} + 'ele'   :: ('index_' + (2 * 2)) (a='something',b=4123)", "${lala slatr} + 'ele'", "'index_' + (2 * 2)", "a='something',b=4123");
        checkFragmentSelection(" ${lala slatr} + 'ele'   :: ('index_' + (2 * 2)) (a=('something'),b=4123)", "${lala slatr} + 'ele'", "'index_' + (2 * 2)", "a='something',b=4123");
        checkFragmentSelection(" ${lala slatr} + ('ele')   :: ('index_' + (2 * 2)) (a=('something'),b=4123)", "${lala slatr} + 'ele'", "'index_' + (2 * 2)", "a='something',b=4123");
        checkFragmentSelection(" ${lala slatr} + ('ele')   :: ('index_' + (2 * 2)) (a=('something' + 23),b=4123)", "${lala slatr} + 'ele'", "'index_' + (2 * 2)", "a=('something' + 23),b=4123");
        checkFragmentSelection(" ${lala slatr}+'ele'   :: ('index_'+(2*2)) (a=('something'+23),b=4123)", "${lala slatr} + 'ele'", "'index_' + (2 * 2)", "a=('something' + 23),b=4123");
        checkFragmentSelection(" ${lala slatr}+'ele'   :: ('index_'+(2*2)) (${name}=('something'+23),b=4123)", "${lala slatr} + 'ele'", "'index_' + (2 * 2)", "${name}=('something' + 23),b=4123");
        checkFragmentSelection(" ${lala slatr}+'ele'   :: ('index_'+(2*2)) ((${name} + 0)=('something'+23),b=4123)", "${lala slatr} + 'ele'", "'index_' + (2 * 2)", "${name} + 0=('something' + 23),b=4123");
        checkFragmentSelection("C:\\Program Files\\apps\\templates\\WEB-INF\\temp.html", "'C:\\Program Files\\apps\\templates\\WEB-INF\\temp.html'",null, null);
        checkFragmentSelection("C:\\Program Files\\apps\\templates\\WEB-INF\\temp.html :: 'fragment number one'", "'C:\\Program Files\\apps\\templates\\WEB-INF\\temp.html'","'fragment number one'", null);
        checkFragmentSelection("/home/user/apps/templates/WEB-INF/temp.html :: 'fragment number one'", "'/home/user/apps/templates/WEB-INF/temp.html'","'fragment number one'", null);
        checkFragmentSelection("home/user :: 'fragment number one'", "'home/user'","'fragment number one'", null);
    }


    private static void checkFragmentSelection(final String fragmentSelectionSpec,
            final String templateExpression, final String fragmentExpression, final String parametersExpression) {

        final FragmentSelection fragmentSelection =
                FragmentSelectionUtils.internalParseFragmentSelection(fragmentSelectionSpec);

        final IStandardExpression parsedTemplateExpression = fragmentSelection.getTemplateName();
        final IStandardExpression parsedFragmentExpression = fragmentSelection.getFragmentSelector();
        final AssignationSequence parsedParameters = fragmentSelection.getParameters();

        final String parsedTempalteExpressionStr =
                (parsedTemplateExpression == null? null : parsedTemplateExpression.getStringRepresentation());
        final String parsedFragmentExpressionStr =
                (parsedFragmentExpression == null? null : parsedFragmentExpression.getStringRepresentation());
        final String parsedParametersStr =
                (parsedParameters == null? null : parsedParameters.getStringRepresentation());

        Assert.assertEquals(templateExpression, parsedTempalteExpressionStr);
        Assert.assertEquals(fragmentExpression, parsedFragmentExpressionStr);
        Assert.assertEquals(parametersExpression, parsedParametersStr);

    }


    @Test
    public void testAssignationSequenceNamed() throws Exception {
        checkVariableNamesSpec("a,b", false);
        checkVariableNamesSpec("_arg0,b", false);
        checkVariableNamesSpec("_arg0,_arg1", true);
    }




    private static void checkVariableNamesSpec(final String variableNamesSpec, final boolean synth) {

        final Set<String> variableNames =
                new HashSet<String>(Arrays.asList(StringUtils.split(variableNamesSpec, ",")));
        final boolean computedNamed = FragmentSelectionUtils.parameterNamesAreSynthetic(variableNames);

        Assert.assertEquals(Boolean.valueOf(synth), Boolean.valueOf(computedNamed));

    }

}
