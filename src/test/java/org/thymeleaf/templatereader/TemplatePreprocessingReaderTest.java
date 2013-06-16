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
package org.thymeleaf.templatereader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.thymeleaf.templateparser.TemplatePreprocessingReader;
import org.thymeleaf.util.StringUtils;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public class TemplatePreprocessingReaderTest extends TestCase {

    private static final String TEMPLATE_TEST1_FILENAME = "templatereader/htmlTemplateReaderTest01";
    private static final String TEMPLATE_TEST2_FILENAME = "templatereader/htmlTemplateReaderTest02";
    private static final String TEMPLATE_TEST3_FILENAME = "templatereader/htmlTemplateReaderTest03";
    private static final String TEMPLATE_TEST4_FILENAME = "templatereader/htmlTemplateReaderTest04";
    private static final String TEMPLATE_TEST5_FILENAME = "templatereader/htmlTemplateReaderTest05";
    private static final String TEMPLATE_TEST6_FILENAME = "templatereader/htmlTemplateReaderTest06";
    private static final String TEMPLATE_TEST7_FILENAME = "templatereader/htmlTemplateReaderTest07";
    private static final String TEMPLATE_TEST8_FILENAME = "templatereader/htmlTemplateReaderTest08";
    private static final String TEMPLATE_TEST9_FILENAME = "templatereader/htmlTemplateReaderTest09";
    private static final String TEMPLATE_TEST10_FILENAME = "templatereader/htmlTemplateReaderTest10";
    private static final String TEMPLATE_TEST11_FILENAME = "templatereader/htmlTemplateReaderTest11";
    private static final String TEMPLATE_TEST12_FILENAME = "templatereader/htmlTemplateReaderTest12";
    private static final String TEMPLATE_TEST13_FILENAME = "templatereader/htmlTemplateReaderTest13";
    private static final Map<String,String> expectedResults = new HashMap<String, String>();
    
    private static final int[] BUFFER_SIZES = new int[] {512, 1024, 2048, 4096, 8192, 16384, 32768};
    
    public TemplatePreprocessingReaderTest() {
        super();
    }
    
    
    public void testHTMLReader() throws Exception {

        for (int i = 0; i < 100; i++) {
            for (int readerBufferSize : BUFFER_SIZES) {
                for (int bufferSize : BUFFER_SIZES) {
                    check(TEMPLATE_TEST1_FILENAME, readerBufferSize, bufferSize);
                    check(TEMPLATE_TEST2_FILENAME, readerBufferSize, bufferSize);
                    check(TEMPLATE_TEST3_FILENAME, readerBufferSize, bufferSize);
                    check(TEMPLATE_TEST4_FILENAME, readerBufferSize, bufferSize);
                    check(TEMPLATE_TEST5_FILENAME, readerBufferSize, bufferSize);
                    check(TEMPLATE_TEST6_FILENAME, readerBufferSize, bufferSize);
                    check(TEMPLATE_TEST7_FILENAME, readerBufferSize, bufferSize);
                    check(TEMPLATE_TEST8_FILENAME, readerBufferSize, bufferSize);
                    check(TEMPLATE_TEST9_FILENAME, readerBufferSize, bufferSize);
                    check(TEMPLATE_TEST10_FILENAME, readerBufferSize, bufferSize);
                    check(TEMPLATE_TEST11_FILENAME, readerBufferSize, bufferSize);
                    check(TEMPLATE_TEST12_FILENAME, readerBufferSize, bufferSize);
                    check(TEMPLATE_TEST13_FILENAME, readerBufferSize, bufferSize);
                }
            }
        }

    }
    
    
    private static void check(
            final String name, final int readerBufferSize, final int bufferSize) 
            throws Exception{
        
        final InputStream is = getFile(name + ".html");
        final TemplatePreprocessingReader reader = 
            new TemplatePreprocessingReader(new InputStreamReader(is, "UTF-8"), readerBufferSize);
        
        final StringBuilder strBuilder = new StringBuilder();
        
        char[] buffer = new char[bufferSize];
        int read;
        
        final List<Integer> offsets = new ArrayList<Integer>();
        final List<Integer> lens = new ArrayList<Integer>();
        
        final Random random = new Random();
        
        int off = 0;
        int len = 0;
        try {
            
            off = random.nextInt(bufferSize);
            len = random.nextInt(bufferSize - off);
            if (len == 0) len = 1;
            
            while ((read = reader.read(buffer, off, len)) > 0) {
                final char[] readResult = new char[read];
                for (int i = off; i < off + read; i++) {
                    if (buffer[i] == '\uFFF8') {
                        buffer[i] = '^';
                    }
                }
                System.arraycopy(buffer, off, readResult, 0, read);
                strBuilder.append(readResult);
                offsets.add(Integer.valueOf(off));
                lens.add(Integer.valueOf(len));
                off = random.nextInt(bufferSize);
                len = random.nextInt(bufferSize - off);
                if (len == 0) len = 1;
            }
            
        } catch (final Exception e) {
            throw new RuntimeException(
                    "Error reading template \"" + name + "\" with readerBufferSize=" + 
                    readerBufferSize + ", bufferSize=" + bufferSize + ", off=" + off +
                    ", len=" + len + ", total read until now=" + strBuilder.length() +
                    ", offsets until now=" + offsets + ", lens until now=" + lens, e);
        }
        
        reader.close();
        is.close();
        
        
        final String result = strBuilder.toString();
        
        String expectedResult = expectedResults.get(name);
        if (expectedResult == null) {
            final InputStream isResult = getFile(name + "-result.html");
            final List<String> lines = IOUtils.readLines(new InputStreamReader(isResult,"UTF-8"));
            expectedResult = StringUtils.join(lines, "\n");
            expectedResults.put(name, expectedResult);
        }
        
        Assert.assertEquals(expectedResult, result);
        
    }
    
    
    
    private static InputStream getFile(final String name) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
    }
    
    
}
