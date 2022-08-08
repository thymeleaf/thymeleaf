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
package org.thymeleaf.templateparser.markup;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.TestTemplateEngineConfigurationBuilder;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.engine.OutputTemplateHandler;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.StringTemplateResource;

/*
 *
 * @author Daniel Fernandez
 * @since 2.0.0
 */
public class HtmlBlockSelectorMarkupHandlerTest {

    private static final String RESOURCES_FOLDER = "templateparser/htmlblockselector/";
    private static final IEngineConfiguration TEMPLATE_ENGINE_CONFIGURATION = TestTemplateEngineConfigurationBuilder.build();

    public HtmlBlockSelectorMarkupHandlerTest() {
        super();
    }


    @Test
    public void test() throws Exception {

        final HTMLTemplateParser parser = new HTMLTemplateParser(2, 4096);
        final IEngineConfiguration templateEngineContext = TestTemplateEngineConfigurationBuilder.build();

        final URL resourcesFolderURL = Thread.currentThread().getContextClassLoader().getResource(RESOURCES_FOLDER);
        Assertions.assertNotNull(resourcesFolderURL);

        final File resourcesFolder = new File(resourcesFolderURL.toURI());
        Assertions.assertTrue(resourcesFolder.isDirectory());

        final List<File> resourceFolderFiles = new ArrayList<File>(Arrays.asList(resourcesFolder.listFiles()));
        Collections.sort(resourceFolderFiles, new Comparator<File>() {
            public int compare(final File o1, final File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        final List<File> testFiles = new ArrayList<File>();
        final List<File> resultFiles = new ArrayList<File>();

        for (final File resourceFolderFile : resourceFolderFiles) {
            if (resourceFolderFile.getName().startsWith("test") && resourceFolderFile.getName().endsWith(".html")) {
                testFiles.add(resourceFolderFile);
            } else if (resourceFolderFile.getName().startsWith("result") && resourceFolderFile.getName().endsWith(".html")) {
                resultFiles.add(resourceFolderFile);
            }
        }

        for (int i = 0; i < testFiles.size(); i++) {

            final File testFile = testFiles.get(i);
            final File resultFile = resultFiles.get(i);

            final List<String> testFileLines =
                    IOUtils.readLines(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
            final String testFileContents = StringUtils.join(testFileLines,'\n');

            final List<String> resultFileLines =
                    IOUtils.readLines(new InputStreamReader(new FileInputStream(resultFile), "UTF-8"));
            final String blockSelector = resultFileLines.get(0);
            resultFileLines.remove(0);
            final String resultFileContents = StringUtils.join(resultFileLines,'\n');

            final Set<String> blockSelectors = new HashSet<String>(Arrays.asList(StringUtils.split(blockSelector,",")));

            check(parser, templateEngineContext, testFile.getName(), testFileContents, resultFileContents, blockSelectors);

        }


    }





    private static void check(
            final HTMLTemplateParser parser, final IEngineConfiguration templateEngineContext,
            final String templateName, final String input, final String output, final Set<String> blockSelectors)
            throws Exception{

        final StringWriter writer = new StringWriter();
        final ITemplateHandler handler = new OutputTemplateHandler(writer);

        parser.parseStandalone(templateEngineContext, templateName, templateName, blockSelectors, new StringTemplateResource(input), TemplateMode.HTML, false, handler);

        Assertions.assertEquals("Test failed for file: " + templateName, output, writer.toString());

    }



}
