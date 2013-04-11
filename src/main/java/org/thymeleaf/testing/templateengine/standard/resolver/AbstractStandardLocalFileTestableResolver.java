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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.resolver.ITestableResolver;
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





public abstract class AbstractStandardLocalFileTestableResolver implements ITestableResolver {

    public static enum TestableType { NONE, TEST, SEQUENCE, ITERATOR, PARALLELIZER }

    public static final String INDEX_FILE_NAME = "TEST.INDEX";
    private static final Pattern INDEX_FILE_LINE_PATTERN = Pattern.compile("(.*?)(\\[(.*?)])?\\s*$");
    
    private static String TEST_FILE_SUFFIX = ".test";
    
    private static String ITERATOR_SUFFIX_PATTERN_STR = "iter-(\\d*)$";
    private static String ITERATOR_PATTERN_STR = "^(.*?)-" + ITERATOR_SUFFIX_PATTERN_STR;
    private static Pattern ITERATOR_SUFFIX_PATTERN = Pattern.compile(ITERATOR_SUFFIX_PATTERN_STR);
    private static Pattern ITERATOR_PATTERN = Pattern.compile(ITERATOR_PATTERN_STR);
    
    private static String PARALLELIZER_SUFFIX_PATTERN_STR = "parallel-(\\d*)$";
    private static String PARALLELIZER_PATTERN_STR = "^(.*?)-" + PARALLELIZER_SUFFIX_PATTERN_STR;
    private static Pattern PARALLELIZER_SUFFIX_PATTERN = Pattern.compile(PARALLELIZER_SUFFIX_PATTERN_STR);
    private static Pattern PARALLELIZER_PATTERN = Pattern.compile(PARALLELIZER_PATTERN_STR);

    
    private IStandardTestReader testReader = new StandardTestReader();
    private IStandardTestEvaluator testEvaluator = new StandardTestEvaluator();
    private IStandardTestBuilder testBuilder = new StandardTestBuilder(this);
    
    
    
    
    protected AbstractStandardLocalFileTestableResolver() {
        super();
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

        final File testableFile = getFileFromTestableName(executionId, testableName);
        if (testableFile == null) {
            return null;
        }

        return resolveFile(executionId, testableFile);

    }    

    
    
    protected TestableType computeTestableTypeFromFileName(final String fileName, final boolean isDirectory) {
        
        if (fileName == null) {
            return TestableType.NONE;
        }
        
        if (!isDirectory) {
            
            if (fileName.endsWith(TEST_FILE_SUFFIX)) {
                return TestableType.TEST;
            }
            return TestableType.NONE;
            
        }
        
        final Matcher iterMatcher = ITERATOR_PATTERN.matcher(fileName);
        if (iterMatcher.matches()) {
            return TestableType.ITERATOR;
        }
        
        final Matcher paralMatcher = PARALLELIZER_PATTERN.matcher(fileName);
        if (paralMatcher.matches()) {
            return TestableType.PARALLELIZER;
        }
        
        return TestableType.SEQUENCE;
        
    }
    
    
    protected abstract File getFileFromTestableName(final String executionId, final String testableName);

    
    
    protected final ITestable resolveFile(final String executionId, final File file) {
        
        if (file == null) {
            return null;
        }
        
        final String fileName = file.getName();
        final boolean isDirectory = file.isDirectory();
        if (!isDirectory && !file.isFile()) {
            return null;
        }
        
        final TestableType type = computeTestableTypeFromFileName(fileName, isDirectory);
        
        if (type == null) {
            return null;
        }

        switch (type) {
            case TEST: return resolveAsTest(executionId, file);
            case SEQUENCE: return resolveAsTestSequence(executionId, file);
            case ITERATOR: return resolveAsTestIterator(executionId, file);
            case PARALLELIZER: return resolveAsTestParallelizer(executionId, file);
            case NONE: return null;
        }
        
        // Should never reach here!
        throw new IllegalStateException("Cannot process testable type " + type);
        
    }

    
    
    
    
    protected final ITest resolveAsTest(final String executionId, final File file) {
        
        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(file, "Test document file cannot be null");
        
        final String documentName = file.getName();

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
        
        
        /*
         * Initialization: create a java.io.Reader on the document file
         */
        final Reader documentReader;
        try {
            documentReader = new FileReader(file);
        } catch (final FileNotFoundException e) {
            throw new TestEngineExecutionException( 
                    "Test file \"" + file.getAbsolutePath() + "\" does not exist");
        }
        
        final StandardTestRawData rawData;
        try {
            rawData = reader.readTestDocument(executionId, documentName, documentReader);
        } catch (final IOException e) {
            throw new TestEngineExecutionException("Error reading document \"" + documentName + "\"", e);
        }
        
        final StandardTestEvaluatedData evaluatedData = evaluator.evaluateTestData(executionId, rawData);
        
        return builder.buildTest(executionId, evaluatedData);
        
    }
    

    
    
    
    protected ITestSequence resolveAsTestSequence(final String executionId, final File file) {
        
        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(file, "File cannot be null");
        
        final String fileName = file.getName();
        
        final TestSequence testSequence = new TestSequence();
        testSequence.setName(fileName);
        
        if (!file.isDirectory()) {
            return null;
        }
        
        File indexFile = null;
        for (final File fileInFolder : file.listFiles()) {
            if (INDEX_FILE_NAME.equalsIgnoreCase(fileInFolder.getName())) {
                indexFile = fileInFolder;
                break;
            }
        }
        
        if (indexFile == null) {
            for (final File fileInFolder : file.listFiles()) {
                final ITestable testable = resolveFile(executionId, fileInFolder);
                if (testable != null) {
                    testSequence.addElement(testable);
                }
            }
            return testSequence;
        }
        
        
        BufferedReader reader = null; 
        try {
            
            reader = new BufferedReader(new FileReader(indexFile));
            String line = null;
            while ((line = reader.readLine()) != null) {
                
                if (line.trim().equals("")) {
                    continue;
                }
                
                final String[] lineComponents = parseTestIndexLine(line);
                if (lineComponents == null) {
                    throw new TestEngineExecutionException(
                            "Error parsing test index file line: '" + line + "'"); 
                }
                
                final String testFileName = lineComponents[0];
                final String testSpec = lineComponents[1];
                
                final File testFile = 
                        new File(
                            (indexFile.getParentFile() != null? indexFile.getParentFile().getAbsolutePath() : "") +
                            File.separator + testFileName); 

                ITestable testable = resolveFile(executionId, testFile);
                if (testable == null) {
                    throw new TestEngineExecutionException(
                            "Error resolving file '" + testFileName + "' " +
                    		"specified in test index file: '" + indexFile.getAbsolutePath() + "'"); 
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
                                    "Error resolving file '" + testFileName + "' " +
                                    "specified in test index file: '" + indexFile.getAbsolutePath() + "'. " +
                            		"Unrecognized specification '[" + testSpec + "]'");
                            
                        }
                        
                    }
                    
                }
                
                testSequence.addElement(testable);
                
            }   
            
            return testSequence;
            
        } catch (final Exception e) {
            throw new TestEngineExecutionException(
                    "Exception raised while reading test index file '" + indexFile.getAbsolutePath() + "'", e);
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
    
    
    
    protected ITestIterator resolveAsTestIterator(final String executionId, final File file) {
        
        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(file, "File cannot be null");
        
        final String fileName = file.getName();
        final Matcher iterMatcher = ITERATOR_PATTERN.matcher(fileName);
        if (!iterMatcher.matches()) {
            throw new TestEngineExecutionException(
                    "Cannot match \"" + fileName + "\" as a valid folder name for an iterator");
        }
        
        final int iterations = Integer.parseInt(iterMatcher.group(2));

        final ITestSequence iteratedSequence = resolveAsTestSequence(executionId, file);
        
        return new TestIterator(iteratedSequence, iterations);
        
    }
    
    
    
    protected ITestParallelizer resolveAsTestParallelizer(final String executionId, final File file) {
        
        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(file, "File cannot be null");
        
        final String fileName = file.getName();
        final Matcher parMatcher = PARALLELIZER_PATTERN.matcher(fileName);
        if (!parMatcher.matches()) {
            throw new TestEngineExecutionException(
                    "Cannot match \"" + fileName + "\" as a valid folder name for a parallelizer");
        }
        final int numThreads = Integer.parseInt(parMatcher.group(2));

        final ITestSequence iteratedSequence = resolveAsTestSequence(executionId, file);
        
        return new TestParallelizer(iteratedSequence, numThreads);
        
    }
    
    
}
