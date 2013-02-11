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
package org.thymeleaf.testing.templateengine.engine;

import org.junit.Test;
import org.thymeleaf.testing.templateengine.builder.ITestSequenceBuilder;
import org.thymeleaf.testing.templateengine.standard.builder.ClassPathFolderStandardTestSequenceBuilder;
import org.thymeleaf.testing.templateengine.test.ITestSequence;
import org.thymeleaf.testing.templateengine.test.ITestSuite;
import org.thymeleaf.testing.templateengine.test.TestSuite;





public class TestExecutorTest {
    
    
    
    
    public TestExecutorTest() {
        super();
    }
    
    
    
    
    
    
    
    @Test
    public void testExecutor() throws Exception {
        
        try {
            

            final ITestSequenceBuilder sequenceBuilder = 
                    new ClassPathFolderStandardTestSequenceBuilder("test", true, ".test");
            
            final ITestSequence seq = sequenceBuilder.buildTestSequence("001");
            final ITestSuite suite = new TestSuite(seq);
            
            
            final TestExecutor executor = new TestExecutor();
            executor.execute(suite);
            
            
//            final String text =
//                    "%NAME_RESOLVER org.thymeleaf.testing.templateengine.engine.TestExecutorTest$OneNameStandardDirectiveResolver\n" +
//                    "%NAME this is a sample test!\n" +
//                    "%INPUT \n<!DOCTYPE html>\n<html>\n  <body>\n    <h1>Hello!</h1>\n  </body>\n</html>\n" +
//                    "%OUTPUT \n<!DOCTYPE html>\n<html>\n  <body>\n    <h1>Hello!</h1>\n  </body>\n</html>\n";
//            
//            final Reader reader = new StringReader(text);
            
//            final StandardTestDocumentData data = 
//                    StandardTestIOUtils.readTestDocument("001", "testf", reader);
//            
//            System.out.println(data.getAllDirectiveValues());
//            
//            final Map<String,Object> values =
//                    StandardTestDocumentResolutionUtils.resolveTestDocumentData("001", data, StandardTestDirectiveSpecs.STANDARD_DIRECTIVES_SET_SPEC);
//            
//            System.out.println(values);
//            System.out.println(((ITestResource)values.get(StandardTestDirectiveSpecs.INPUT_DIRECTIVE_SPEC.getName())).read());
            
//            final IStandardTestBuilder testBuilder = new FileStandardTestBuilder();
            
            
//            final ITestResource res0 = new StringTestResource("hello!");
//            final ITestResource res1 = new StringTestResource("goodbye!");
//            final ITestResource res2 = new StringTestResource("<span th:text=\"${'hey!'}\">cucu</span>");
//            final ITestResource res3 = new StringTestResource("<span>hey!</span>");
            
//            final ITest test0 = new SuccessExpectedTest(res1, true, res0);
//            final ITest test1 = new SuccessExpectedTest(res2, false, res0);
//            final ITest test2 = new SuccessExpectedTest(res2, false, res3);
//            
//            final ITestIterator iter2 = new TestIterator(test2, 2);
            
//            final ITestSuite testSuite = new TestSuite("testing01", test0, test2, test1, test2, iter2);
//            final ITestSuite testSuite = new TestSuite(iter2);

            
            
//
//System.out.println("ClasspathHelper.forClassLoader(Thread.currentThread().getContextClassLoader())\n");
//System.out.println(ClasspathHelper.forClassLoader(Thread.currentThread().getContextClassLoader()));
//System.out.println("\n\n");
//System.out.println("ClasspathHelper.forClassLoader(ClassLoaderUtils.getClassLoader(FolderStandardTestSequenceBuilder.class))\n");
//System.out.println(ClasspathHelper.forClassLoader(ClassLoaderUtils.getClassLoader(LocalFolderStandardTestSequenceBuilder.class)));
//System.out.println("\n\n");
//System.out.println("ClasspathHelper.forClassLoader(ClassLoaderUtils.getClassLoader(TestExecutorTest.class))\n");
//System.out.println(ClasspathHelper.forClassLoader(ClassLoaderUtils.getClassLoader(TestExecutorTest.class)));
//System.out.println("\n\n");
//System.out.println("ClasspathHelper.forClass(FolderStandardTestSequenceBuilder.class)\n");
//System.out.println(ClasspathHelper.forClass(LocalFolderStandardTestSequenceBuilder.class));
//System.out.println("\n\n");
//System.out.println("ClasspathHelper.forClass(TestExecutorTest.class)\n");
//System.out.println(ClasspathHelper.forClass(TestExecutorTest.class));
//System.out.println("\n\n");
//
//final ClassLoader cl = ClassLoaderUtils.getClassLoader(LocalFolderStandardTestSequenceBuilder.class);
//
//final URL url = cl.getResource("test");
//
//final File f = new File(url.toURI());
//
//System.out.println(f.getAbsolutePath());
//
////final InputStream is = cl.getResourceAsStream("test/cucu/test.html");
////final InputStreamReader isr = new InputStreamReader(is);
////final char[] buf = new char[10];
////isr.read(buf);
////System.out.println("\n\n---\n" + new String(buf) + "\n---\n\n");
//
//            final Reflections reflections = 
//                    new Reflections(
//                            new ConfigurationBuilder().
//                                setUrls(ClasspathHelper.forClassLoader(Thread.currentThread().getContextClassLoader())).
//                                setScanners(new ResourcesScanner()));
//
//            final Pattern pattern1 = Pattern.compile("(.*)");
//            final Pattern pattern2 = Pattern.compile("test(.*)");
//            Set<String> files = reflections.getResources(pattern1);
//            for (final String file : files) {
//                final boolean matches = pattern2.matcher(file).matches();
//                if (matches) System.out.println("\"" + file + "\" " + matches);
//            }
//            
            
            
        } catch (final Throwable t) {
            t.printStackTrace();
        }
        
        
    }
    
    
    
}
