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
package org.thymeleaf.testing.templateprocess.spec.test.standard;

import java.io.Reader;

import org.thymeleaf.context.IContext;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.testing.templateprocess.spec.test.ITestSpec;



public class StandardTestSpec implements ITestSpec {


    private ITestNameSpec testNameSpec = IndexTestNameSpec.INSTANCE;
    private ITestIterationSpec testIterationSpec = OneTestIterationSpec.INSTANCE;
    private ITestTemplateModeSpec testTemplateModeSpec = Html5TestTemplateModeSpec.INSTANCE;
    private ITestFragmentSpec testFragmentSpec = NoTestFragmentSpec.INSTANCE;
    private ITestContextSpec testContextSpec = null;
    private ITestInputSpec testInputSpec = null;
    private ITestOutputSpec testOutputSpec = null;

    
    public StandardTestSpec() {
        super();
    }
    
    
    public ITestNameSpec getTestNameSpec() {
        return this.testNameSpec;
    }

    public void setTestNameSpec(final ITestNameSpec testNameSpec) {
        this.testNameSpec = testNameSpec;
    }


    public ITestIterationSpec getTestIterationSpec() {
        return this.testIterationSpec;
    }

    public void setTestIterationSpec(final ITestIterationSpec testIterationSpec) {
        this.testIterationSpec = testIterationSpec;
    }


    public ITestTemplateModeSpec getTestTemplateModeSpec() {
        return this.testTemplateModeSpec;
    }

    public void setTestTemplateModeSpec(final ITestTemplateModeSpec testTemplateModeSpec) {
        this.testTemplateModeSpec = testTemplateModeSpec;
    }

    
    public ITestFragmentSpec getTestFragmentSpec() {
        return this.testFragmentSpec;
    }

    public void setTestFragmentSpec(final ITestFragmentSpec testFragmentSpec) {
        this.testFragmentSpec = testFragmentSpec;
    }


    public ITestContextSpec getTestContextSpec() {
        return this.testContextSpec;
    }

    public void setTestContextSpec(final ITestContextSpec testContextSpec) {
        this.testContextSpec = testContextSpec;
    }


    public ITestInputSpec getTestInputSpec() {
        return this.testInputSpec;
    }

    public void setTestInputSpec(final ITestInputSpec testInputSpec) {
        this.testInputSpec = testInputSpec;
    }


    public ITestOutputSpec getTestOutputSpec() {
        return this.testOutputSpec;
    }

    public void setTestOutputSpec(final ITestOutputSpec testOutputSpec) {
        this.testOutputSpec = testOutputSpec;
    }




    public boolean isValid() {
        return this.testNameSpec != null &&
               this.testIterationSpec != null &&
               this.testTemplateModeSpec != null &&
               this.testFragmentSpec != null &&
               this.testContextSpec != null &&
               this.testInputSpec != null &&
               this.testOutputSpec != null;
    }
    
    
    public String getTestName(final String testSetName, final int testIndex) {
        return this.testNameSpec.getTestName(testSetName, testIndex);
    }
    
    
    public int getIterations(final String testSetName, final String testName) {
        return this.testIterationSpec.getIterations(testSetName, testName);
    }
    
    
    public IContext getContext(final String testSetName, final String testName, final int iteration) {
        return this.testContextSpec.getContext(testSetName, testName, iteration);
    }
    
    
    public IFragmentSpec getFragmentSpec(final String testSetName, final String testName, final int iteration) {
        return this.testFragmentSpec.getFragmentSpec(testSetName, testName, iteration);
    }
    
    
    public String getTemplateMode(final String testSetName, final String testName, final int iteration) {
        return this.testTemplateModeSpec.getTemplateMode(testSetName, testName, iteration);
    }
    
    
    public Reader getInputReader(final String testSetName, final String testName, final int iteration) {
        return this.testInputSpec.getInputReader(testSetName, testName, iteration);
    }

    
    public boolean shouldFail(final String testSetName, final String testName, final int iteration) {
        return this.testOutputSpec.shouldFail(testSetName, testName, iteration);
    }

    public String getDesiredOutput(final String testSetName, final String testName, final int iteration) {
        return this.testOutputSpec.getDesiredOutput(testSetName, testName, iteration);
    }

    public String getErrorMessage(final String testSetName, final String testName, final int iteration) {
        return this.testOutputSpec.getErrorMessage(testSetName, testName, iteration);
    }

    public int getMaxExecutionTimeMilliseconds(final String testSetName,
            final String testName, final int iteration) {
        return this.testOutputSpec.getMaxExecutionTimeMilliseconds(testSetName, testName, iteration);
    }

    
}
