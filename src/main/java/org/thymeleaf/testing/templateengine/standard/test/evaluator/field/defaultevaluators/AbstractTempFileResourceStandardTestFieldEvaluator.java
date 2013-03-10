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
package org.thymeleaf.testing.templateengine.standard.test.evaluator.field.defaultevaluators;

import java.io.File;
import java.io.FileWriter;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.resource.FileTestResource;
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestEvaluatedField;
import org.thymeleaf.util.Validate;


public abstract class AbstractTempFileResourceStandardTestFieldEvaluator extends AbstractStandardTestFieldEvaluator {

    
    protected AbstractTempFileResourceStandardTestFieldEvaluator() {
        super(ITestResource.class);
    }


    @Override
    protected final StandardTestEvaluatedField getValue(final String executionId, final String documentName, 
            final String fieldName, final String fieldQualifier, final String fieldValue) {

        if (fieldValue == null || fieldValue.trim().equals("")) {
            return StandardTestEvaluatedField.forNoValue();
        }

        return StandardTestEvaluatedField.forSpecifiedValue(
                    createResource(executionId, getFileSuffix(), fieldValue));      
        
    }

    
    
    
    protected ITestResource createResource(final String executionId, final String fileSuffix, final String contents) {

        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(fileSuffix, "File suffix cannot be null");
        Validate.notNull(contents, "Contents cannot be null");
        
        try {

            final String prefix = 
                    "thymeleaf-testing" + 
                    (executionId != null? ("-" + executionId) : "") + 
                    (fileSuffix != null? ("-" + fileSuffix) : "") + "-";
            
            final File tempFile = File.createTempFile(prefix, null);
            tempFile.deleteOnExit();
            
            FileWriter writer = null;
            try {
                writer = new FileWriter(tempFile, false);
                writer.write(contents);
            } catch (final Throwable t) {
                throw new TestEngineExecutionException( 
                        "Could not write contents of temporary file for execution \"" + executionId + "\"", t);
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (final Throwable ignored) {
                    // ignored
                }
            }
            
            return new FileTestResource(tempFile);
            
        } catch (final TestEngineExecutionException e) {
            throw e;
        } catch (final Throwable t) {
            throw new TestEngineExecutionException( 
                    "Could not create temporary file for execution \"" + executionId + "\"", t);
        }
        
    }
    
    
    
    
    protected abstract String getFileSuffix();
    
}
