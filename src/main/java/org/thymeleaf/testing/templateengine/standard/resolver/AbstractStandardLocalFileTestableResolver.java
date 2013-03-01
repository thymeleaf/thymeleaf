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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.resolver.ITestableResolver;
import org.thymeleaf.testing.templateengine.standard.data.StandardTestDocumentData;
import org.thymeleaf.testing.templateengine.standard.directive.StandardTestDirectiveSpecSet;
import org.thymeleaf.testing.templateengine.standard.directive.StandardTestDirectiveUtils;
import org.thymeleaf.testing.templateengine.standard.reader.StandardTestReaderUtils;
import org.thymeleaf.testing.templateengine.standard.testbuilder.IStandardTestBuilder;
import org.thymeleaf.testing.templateengine.standard.testbuilder.StandardTestBuilder;
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

    private static String TEST_FILE_SUFFIX = ".test";
    private static Pattern ITERATOR_PATTERN = Pattern.compile("^(.*?)-iter-(\\d*)$");
    private static Pattern PARALLELIZER_PATTERN = Pattern.compile("^(.*?)-parallel-(\\d*)$");

    private static final IStandardTestBuilder DEFAULT_TEST_BUILDER = new StandardTestBuilder();
    
    
    protected AbstractStandardLocalFileTestableResolver() {
        super();
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

    
    
    protected TestableType computeTestableType(
            @SuppressWarnings("unused") final String executionId, final File file) {
        
        if (file == null) {
            return TestableType.NONE;
        }
        
        final String fileName = file.getName();
        if (!file.isDirectory()) {
            
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
        
        final TestableType type = computeTestableType(executionId, file);
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
        
        final String documentName = file.getName();
        
        final Reader documentReader;
        try {
            documentReader = new FileReader(file);
        } catch (final FileNotFoundException e) {
            throw new TestEngineExecutionException( 
                    executionId, "Test file \"" + file.getAbsolutePath() + "\" does not exist");
        }
        
        final StandardTestDocumentData data = 
                StandardTestReaderUtils.readDocument(executionId, documentName, documentReader);

        final StandardTestDirectiveSpecSet directiveSpecSet = getTestDirectiveSpecSet();
        if (directiveSpecSet == null) {
            throw new TestEngineExecutionException(
                    executionId, "A null directive spec set has been specified");
        }
        
        final Map<String,Map<String,Object>> dataByDirectiveAndQualifier =
                StandardTestDirectiveUtils.resolveDirectiveValues(executionId, data, directiveSpecSet);
        
        final IStandardTestBuilder builder = getTestBuilder();
        if (builder == null) {
            throw new TestEngineExecutionException(
                    executionId, "A null test builder has been specified");
        }
        
        return builder.buildTest(executionId, documentName, dataByDirectiveAndQualifier);
        
    }
    
    
    /*
     * Meant to be overriden
     */
    protected StandardTestDirectiveSpecSet getTestDirectiveSpecSet() {
        return StandardTestDirectiveSpecSet.STANDARD_DIRECTIVES_SPEC_SET;
    }

    
    /*
     * Meant to be overriden
     */
    protected IStandardTestBuilder getTestBuilder() {
        return DEFAULT_TEST_BUILDER;
    }
    
    
    
    protected final ITestSequence resolveAsTestSequence(final String executionId, final File file) {
        
        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(file, "File cannot be null");
        
        final String fileName = file.getName();
        
        final TestSequence testSequence = new TestSequence();
        testSequence.setName(fileName);
        
        final List<File> orderedFiles = 
                orderFolderFilesInSequence(Arrays.asList(file.listFiles()));
        
        for (final File fileInFolder : orderedFiles) {
            final ITestable testable = resolveFile(executionId, fileInFolder);
            if (testable != null) {
                testSequence.addElement(testable);
            }
        }
        
        return testSequence;
        
    }
    
    
    /*
     * Meant to be overriden
     */
    protected List<File> orderFolderFilesInSequence(final List<File> fileList) {
        return fileList;
    }
    
    
    
    protected ITestIterator resolveAsTestIterator(final String executionId, final File file) {
        
        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(file, "File cannot be null");
        
        final String fileName = file.getName();
        final Matcher iterMatcher = ITERATOR_PATTERN.matcher(fileName);
        if (!iterMatcher.matches()) {
            throw new TestEngineExecutionException(
                    executionId, "Cannot match \"" + fileName + "\" as a valid folder name for an iterator");
        }
        final int iterations = Integer.parseInt(iterMatcher.group(2));

        final ITestSequence iteratedSequence = resolveAsTestSequence(executionId, file);
        
        return new TestIterator(iteratedSequence, iterations);
        
    }
    
    
    
    protected ITestParallelizer resolveAsTestParallelizer(final String executionId, final File file) {
        
        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(file, "File cannot be null");
        
        final String fileName = file.getName();
        final Matcher iterMatcher = PARALLELIZER_PATTERN.matcher(fileName);
        if (!iterMatcher.matches()) {
            throw new TestEngineExecutionException(
                    executionId, "Cannot match \"" + fileName + "\" as a valid folder name for a parallelizer");
        }
        final int numThreads = Integer.parseInt(iterMatcher.group(2));

        final ITestSequence iteratedSequence = resolveAsTestSequence(executionId, file);
        
        return new TestParallelizer(iteratedSequence, numThreads);
        
    }

    
    
}
