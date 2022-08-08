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
package org.thymeleaf.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.TestTemplateEngineConfigurationBuilder;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.markup.HTMLTemplateParser;
import org.thymeleaf.templateresource.FileTemplateResource;

public class HtmlBulkTester {

    private static final HTMLTemplateParser PARSER = new HTMLTemplateParser(2, 4096);
    private static final IEngineConfiguration TEMPLATE_ENGINE_CONFIGURATION = TestTemplateEngineConfigurationBuilder.build();



    public static void main(final String[] args) throws Exception {

        if (args.length != 1) {
            System.err.println("Syntax: java " + HtmlBulkTester.class.getName() + " [test_folder]");
            System.exit(1);
        }

        final String testFolderName = args[0];

        final File testFolder = new File(testFolderName);

        if (!testFolder.exists() || !testFolder.isDirectory()) {
            System.err.println("Folder " + testFolderName + " does not exist or is not a folder");
            System.exit(1);
        }

        System.out.println("Using temporary folder for output: " + System.getProperty("java.io.tmpdir"));

        final File[] filesInTestFolder = testFolder.listFiles();
        for (final File fileInTestFolder : filesInTestFolder) {

            if (!fileInTestFolder.getName().endsWith(".html")) {
                continue;
            }

            final String fileInTestFolderName = fileInTestFolder.getName();
            final FileInputStream fileInTestFolderStream = new FileInputStream(fileInTestFolder);


            final File testOutput = File.createTempFile("thymeleaf-testing-" + fileInTestFolderName + "-", ".html");
            testOutput.deleteOnExit();
            final FileOutputStream testOutputStream = new FileOutputStream(testOutput);
            final OutputStreamWriter testOutputWriter = new OutputStreamWriter(testOutputStream, "UTF-8");

            final ITemplateHandler handler = new OutputTemplateHandler(testOutputWriter);

            System.out.print(fileInTestFolderName);

            System.out.print("[PARSING]");

            PARSER.parseStandalone(TEMPLATE_ENGINE_CONFIGURATION, fileInTestFolderName, fileInTestFolderName, null, new FileTemplateResource(fileInTestFolder, "UTF-8"), TemplateMode.HTML, false, handler);

            // Input stream will be closed by parser
            testOutputWriter.close();
            testOutputStream.close();

            System.out.print("[PARSED]");

            final FileInputStream testOutputCheckStream = new FileInputStream(testOutput);
            final List<String> outputLines = IOUtils.readLines(testOutputCheckStream, "UTF-8");

            final FileInputStream testInputCheckStream = new FileInputStream(fileInTestFolder);
            final List<String> inputLines = IOUtils.readLines(testInputCheckStream, "UTF-8");

            System.out.print("[CHECKING]");

            if (outputLines.equals(inputLines)) {
                System.out.print("[OK]");
            } else {
                System.out.print("[KO]");
            }

            System.out.println();

        }


    }





    private HtmlBulkTester() {
        super();
    }
    
}
