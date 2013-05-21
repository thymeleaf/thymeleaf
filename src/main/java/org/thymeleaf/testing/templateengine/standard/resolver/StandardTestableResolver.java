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
package org.thymeleaf.testing.templateengine.standard.resolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.resolver.ITestableResolver;
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.testing.templateengine.resource.ITestResourceContainer;
import org.thymeleaf.testing.templateengine.resource.ITestResourceItem;
import org.thymeleaf.testing.templateengine.resource.ITestResourceResolver;
import org.thymeleaf.testing.templateengine.resource.StandardTestResourceResolver;
import org.thymeleaf.testing.templateengine.standard.test.builder.IStandardTestBuilder;
import org.thymeleaf.testing.templateengine.standard.test.builder.StandardTestBuilder;
import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestEvaluatedData;
import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestRawData;
import org.thymeleaf.testing.templateengine.standard.test.evaluator.IStandardTestEvaluator;
import org.thymeleaf.testing.templateengine.standard.test.evaluator.StandardTestEvaluator;
import org.thymeleaf.testing.templateengine.standard.test.reader.IStandardTestReader;
import org.thymeleaf.testing.templateengine.standard.test.reader.StandardTestReader;
import org.thymeleaf.testing.templateengine.testable.ITest;
import org.thymeleaf.testing.templateengine.testable.ITestIterator;
import org.thymeleaf.testing.templateengine.testable.ITestParallelizer;
import org.thymeleaf.testing.templateengine.testable.ITestSequence;
import org.thymeleaf.testing.templateengine.testable.ITestable;
import org.thymeleaf.testing.templateengine.testable.TestIterator;
import org.thymeleaf.testing.templateengine.testable.TestParallelizer;
import org.thymeleaf.testing.templateengine.testable.TestSequence;
import org.thymeleaf.util.Validate;





public class StandardTestableResolver implements ITestableResolver {

    public static enum TestableType { NONE, TEST, SEQUENCE, ITERATOR, PARALLELIZER }

    public static final String FOLDER_INDEX_FILE_PREFIX = "FOLDER";
    private static String TEST_FILE_SUFFIX = ".THTEST";
    private static String INDEX_FILE_SUFFIX = ".THINDEX";
    public static final String FOLDER_INDEX_FILE_NAME = FOLDER_INDEX_FILE_PREFIX + INDEX_FILE_SUFFIX;

    private static final Pattern INDEX_FILE_LINE_PATTERN = Pattern.compile("(.*?)(\\[(.*?)])?\\s*$");
    
    private static String ITERATOR_SUFFIX_PATTERN_STR = "iter-(\\d*)$";
    private static String ITERATOR_PATTERN_STR = "^(.*?)-" + ITERATOR_SUFFIX_PATTERN_STR;
    private static Pattern ITERATOR_SUFFIX_PATTERN = Pattern.compile(ITERATOR_SUFFIX_PATTERN_STR);
    private static Pattern ITERATOR_PATTERN = Pattern.compile(ITERATOR_PATTERN_STR);
    
    private static String PARALLELIZER_SUFFIX_PATTERN_STR = "parallel-(\\d*)$";
    private static String PARALLELIZER_PATTERN_STR = "^(.*?)-" + PARALLELIZER_SUFFIX_PATTERN_STR;
    private static Pattern PARALLELIZER_SUFFIX_PATTERN = Pattern.compile(PARALLELIZER_SUFFIX_PATTERN_STR);
    private static Pattern PARALLELIZER_PATTERN = Pattern.compile(PARALLELIZER_PATTERN_STR);

    
    private ITestResourceResolver testResourceResolver = StandardTestResourceResolver.UTF8_RESOLVER;
    private IStandardTestReader testReader = new StandardTestReader();
    private IStandardTestEvaluator testEvaluator = new StandardTestEvaluator();
    private IStandardTestBuilder testBuilder = new StandardTestBuilder();
    
    
    
    
    public StandardTestableResolver() {
        super();
    }

    

    
    public final ITestResourceResolver getTestResourceResolver() {
        return this.testResourceResolver;
    }
    
    public final void setTestResourceResolver(final ITestResourceResolver testResourceResolver) {
        Validate.notNull(testResourceResolver, "Test Resource Resolver cannot be null");
        this.testResourceResolver = testResourceResolver;
    }

    
    
    public IStandardTestBuilder getTestBuilder() {
        return this.testBuilder;
    }
    
    public void setTestBuilder(final IStandardTestBuilder testBuilder) {
        Validate.notNull(testBuilder, "Test Builder cannot be null");
        this.testBuilder = testBuilder;
    }
    
    
    public IStandardTestReader getTestReader() {
        return this.testReader;
    }

    public void setTestReader(final IStandardTestReader testReader) {
        Validate.notNull(testReader, "Test Reader cannot be null");
        this.testReader = testReader;
    }


    public IStandardTestEvaluator getTestEvaluator() {
        return this.testEvaluator;
    }

    public void setTestEvaluator(final IStandardTestEvaluator testEvaluator) {
        Validate.notNull(testEvaluator, "Test Evaluator cannot be null");
        this.testEvaluator = testEvaluator;
    }






    public final ITestable resolve(final String executionId, final String testableName) {

        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(testableName, "Testable name cannot be null");

        final ITestResource resource = this.testResourceResolver.resolve(testableName);
        if (resource == null) {
            return null;
        }

        return resolveResource(executionId, resource);

    }    
    

    public final ITestable resolveRelative(
            final String executionId, final String testableName, final ITestResource relativeTo) {

        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(testableName, "Testable name cannot be null");
        
        if (relativeTo == null) {
            return resolve(executionId, testableName);
        }

        final ITestResource resource = 
                this.testResourceResolver.resolveRelative(testableName, relativeTo);
        if (resource == null) {
            return null;
        }

        return resolveResource(executionId, resource);

    }    

    
    
    protected TestableType computeTestableType(final ITestResource resource) {
        
        if (resource == null) {
            return TestableType.NONE;
        }

        final String resourceName = resource.getName();
        if (resourceName == null) {
            return null;
        }
        
        if (resource instanceof ITestResourceItem) {
        
            if (resourceName.toUpperCase().endsWith(TEST_FILE_SUFFIX)) {
                return TestableType.TEST;
            }
            if (resourceName.toUpperCase().endsWith(INDEX_FILE_SUFFIX)) {
                return TestableType.SEQUENCE;
            }
            return TestableType.NONE;
            
        }
        
        if (resource instanceof ITestResourceContainer) {
            
            final Matcher iterMatcher = ITERATOR_PATTERN.matcher(resourceName);
            if (iterMatcher.matches()) {
                return TestableType.ITERATOR;
            }
            
            final Matcher paralMatcher = PARALLELIZER_PATTERN.matcher(resourceName);
            if (paralMatcher.matches()) {
                return TestableType.PARALLELIZER;
            }
            
            return TestableType.SEQUENCE;
            
        }
        
        // Should never reach here!
        throw new IllegalStateException("Unknown resource type " + resource.getClass().getName());
        
    }
    
    
    
    protected final ITestable resolveResource(final String executionId, final ITestResource resource) {
        
        if (resource == null) {
            return null;
        }
        
        final TestableType type = computeTestableType(resource);
        
        if (type == null) {
            return null;
        }

        switch (type) {
            case TEST: return resolveAsTest(executionId, resource);
            case SEQUENCE: return resolveAsTestSequence(executionId, resource);
            case ITERATOR: return resolveAsTestIterator(executionId, resource);
            case PARALLELIZER: return resolveAsTestParallelizer(executionId, resource);
            case NONE: return null;
        }
        
        // Should never reach here!
        throw new IllegalStateException("Cannot process testable type " + type);
        
    }

    
    
    
    
    protected final ITest resolveAsTest(final String executionId, final ITestResource resource) {
        
        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(resource, "Test resource cannot be null");
        
        final IStandardTestReader reader = getTestReader();
        if (reader == null) {
            throw new TestEngineExecutionException("A null test reader has been configured");
        }
        final IStandardTestEvaluator evaluator = getTestEvaluator();
        if (evaluator == null) {
            throw new TestEngineExecutionException("A null test evaluator has been configured");
        }
        final IStandardTestBuilder builder = getTestBuilder();
        if (builder == null) {
            throw new TestEngineExecutionException("A null test builder has been configured");
        }
        final ITestResourceResolver resolver = getTestResourceResolver();
        if (resolver == null) {
            throw new TestEngineExecutionException("A null test resource resolver has been configured");
        }
        
        final StandardTestRawData rawData;
        try {
            rawData = reader.readTestResource(executionId, resource, this);
        } catch (final IOException e) {
            throw new TestEngineExecutionException("Error reading resource \"" + resource.getName() + "\"", e);
        }
        
        final StandardTestEvaluatedData evaluatedData = evaluator.evaluateTestData(executionId, rawData, this);
        
        return builder.buildTest(executionId, evaluatedData, this);
        
    }
    

    
    
    
    protected ITestSequence resolveAsTestSequence(final String executionId, final ITestResource resource) {
        
        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(resource, "Test resource be null");
        
        final String fileName = resource.getName();
        
        final TestSequence testSequence = new TestSequence();
        testSequence.setName(fileName);

        ITestResourceItem index = null;
        
        if (resource instanceof ITestResourceItem) {
            if (resource.getName().toUpperCase().endsWith(INDEX_FILE_SUFFIX)) {
                index = (ITestResourceItem) resource;
            } else {
                return null;
            }
        }
        
        if (resource instanceof ITestResourceContainer) {

            final ITestResourceContainer containerResource = (ITestResourceContainer) resource;
            
            if (index == null) {
                for (final ITestResource resourceInFolder : containerResource.getContainedResources()) {
                    if (resourceInFolder instanceof ITestResourceItem) {
                        final String resourceInFolderName = resourceInFolder.getName();
                        if (resourceInFolderName != null && 
                                resourceInFolderName.toUpperCase().endsWith(FOLDER_INDEX_FILE_NAME)) {
                            index = (ITestResourceItem) resourceInFolder;
                            break;
                        }
                    }
                }
            }
            
            if (index == null) {
                for (final ITestResource resourceInFolder : containerResource.getContainedResources()) {
                    final ITestable testable = resolveResource(executionId, resourceInFolder);
                    if (testable != null) {
                        testSequence.addElement(testable);
                    }
                }
                return testSequence;
            }
            
        }
        

        readIndex(executionId, index, testSequence); 

        return testSequence;
        
    }
    
    
    
    private static final String[] parseTestIndexLine(final String line) {
        
        if (line == null) {
            return null;
        }

        final Matcher m;
        synchronized (INDEX_FILE_LINE_PATTERN) {
            m = INDEX_FILE_LINE_PATTERN.matcher(line);
        }
        if (m == null || !m.matches()) {
            return null;
        }
        final String fileName = m.group(1);
        final String lineSpec = m.group(3);
        if (fileName == null || fileName.trim().equals("")) {
            return null;
        }
        final String[] result = new String[2];
        result[0] = fileName.trim();
        result[1] = (lineSpec == null? null : lineSpec.trim());
        return result;
        
    }
    
    
    
    protected ITestIterator resolveAsTestIterator(final String executionId, final ITestResource resource) {
        
        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(resource, "Test resource cannot be null");
        
        final String fileName = resource.getName();
        final Matcher iterMatcher = ITERATOR_PATTERN.matcher(fileName);
        if (!iterMatcher.matches()) {
            throw new TestEngineExecutionException(
                    "Cannot match \"" + fileName + "\" as a valid folder name for an iterator");
        }
        
        final int iterations = Integer.parseInt(iterMatcher.group(2));

        final ITestSequence iteratedSequence = resolveAsTestSequence(executionId, resource);
        
        return new TestIterator(iteratedSequence, iterations);
        
    }
    
    
    
    protected ITestParallelizer resolveAsTestParallelizer(final String executionId, final ITestResource resource) {
        
        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(resource, "Test resource cannot be null");
        
        final String fileName = resource.getName();
        final Matcher parMatcher = PARALLELIZER_PATTERN.matcher(fileName);
        if (!parMatcher.matches()) {
            throw new TestEngineExecutionException(
                    "Cannot match \"" + fileName + "\" as a valid folder name for a parallelizer");
        }
        final int numThreads = Integer.parseInt(parMatcher.group(2));

        final ITestSequence iteratedSequence = resolveAsTestSequence(executionId, resource);
        
        return new TestParallelizer(iteratedSequence, numThreads);
        
    }
    
    
    
    
    private void readIndex(final String executionId, final ITestResourceItem indexResource, final TestSequence testSequence) {

        
        BufferedReader reader = null; 
        try {
            
            final String indexContents = indexResource.readAsText();
            
            reader = new BufferedReader(new StringReader(indexContents));
            String line = null;
            while ((line = reader.readLine()) != null) {
                
                if (line.trim().equals("") || line.startsWith("#")) {
                    // Empty or commented out line
                    continue;
                }
                
                final String[] lineComponents = parseTestIndexLine(line);
                if (lineComponents == null) {
                    throw new TestEngineExecutionException(
                            "Error parsing test index file line: '" + line + "'"); 
                }
                
                final String testResourceName = lineComponents[0];
                final String testSpec = lineComponents[1];
                
                final ITestResource testResource =
                        this.testResourceResolver.resolveRelative(testResourceName, indexResource);

                ITestable testable = resolveResource(executionId, testResource);
                if (testable == null) {
                    throw new TestEngineExecutionException(
                            "Error resolving file '" + testResourceName + "' " +
                            "specified in test index file: '" + indexResource.getName() + "'"); 
                }
                
                if (testSpec != null) {
                    
                    final Matcher iterMatcher = ITERATOR_SUFFIX_PATTERN.matcher(testSpec);
                    if (iterMatcher.matches()) {
                        
                        final int iterations = Integer.parseInt(iterMatcher.group(1));
                        final TestIterator testIterator = new TestIterator(testable, iterations);
                        testable = testIterator;
                        
                    } else {
                    
                        final Matcher parMatcher = PARALLELIZER_SUFFIX_PATTERN.matcher(testSpec);
                        if (parMatcher.matches()) {
                            
                            final int numThreads = Integer.parseInt(parMatcher.group(1));
                            final TestParallelizer testParallelizer = new TestParallelizer(testable, numThreads);
                            testable = testParallelizer;
                            
                        } else {
                            
                            throw new TestEngineExecutionException(
                                    "Error resolving file '" + testResourceName + "' " +
                                    "specified in test index file: '" + indexResource.getName() + "'. " +
                                    "Unrecognized specification '[" + testSpec + "]'");
                            
                        }
                        
                    }
                    
                }
                
                testSequence.addElement(testable);
                
            }   
        
            
        } catch (final TestEngineExecutionException e) {
            throw e;
        } catch (final Exception e) {
            throw new TestEngineExecutionException(
                    "Exception raised while reading test index file '" + indexResource.getName() + "'", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (final Throwable ignored) {
                    // ignored
                }
            }
        }
        
        
    }
    
    
}
