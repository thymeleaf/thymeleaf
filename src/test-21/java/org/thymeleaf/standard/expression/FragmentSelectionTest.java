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

import org.junit.Assert;
import org.junit.Test;


public class FragmentSelectionTest {


    public FragmentSelectionTest() {
        super();
    }



    @Test
    public void testFragmentSelection() throws Exception {
        checkFragmentSelection("template", "'template'", null, null);
        checkFragmentSelection("template::f", "'template'", "'f'", null);
        checkFragmentSelection("template::frag", "'template'", "'frag'", null);
        checkFragmentSelection("template :: frag", "'template'", "'frag'", null);
        checkFragmentSelection("  template :: frag   ", "'template'", "'frag'", null);
        checkFragmentSelection("   :: frag   ", null, "'frag'", null);
        checkFragmentSelection("::frag   ", null, "'frag'", null);
        checkFragmentSelection("::frag", null, "'frag'", null);
        checkFragmentSelection("this::frag", null, "'frag'", null);
        checkFragmentSelection(" this   ::frag", null, "'frag'", null);
        checkFragmentSelection(" this   :: frag", null, "'frag'", null);
    }


    private static void checkFragmentSelection(final String fragmentSelectionSpec,
            final String templateExpression, final String fragmentExpression, final String parametersExpression) {

        final FragmentSelection fragmentSelection = FragmentSelection.parse(fragmentSelectionSpec);

        final Expression parsedTemplateExpression = fragmentSelection.getTemplateName();
        final Expression parsedFragmentExpression = fragmentSelection.getFragmentSelector();
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
        checkAssignationSequenceNamed("a=23,b=${lalele}", true);
        checkAssignationSequenceNamed("_arg0=23,b=${lalele}", true);
        checkAssignationSequenceNamed("_arg0=23,_arg1=${lalele}", false);
    }




    private static void checkAssignationSequenceNamed(final String assignationSequenceSpec, final boolean named) {

        final AssignationSequence assignationSequence = AssignationSequence.parse(assignationSequenceSpec, false);
        final boolean computedNamed = FragmentSelection.isAssignationNamed(assignationSequence);

        Assert.assertEquals(Boolean.valueOf(named), Boolean.valueOf(computedNamed));

    }

}
