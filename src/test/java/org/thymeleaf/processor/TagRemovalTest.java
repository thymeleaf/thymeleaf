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
package org.thymeleaf.processor;

import org.thymeleaf.AbstractDocumentProcessingTest;
import org.w3c.dom.Document;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public class TagRemovalTest extends AbstractDocumentProcessingTest {

    public void testTagRemoval() throws Exception {
        Document doc = processTemplate("TagRemovalTest");

        // no op
        assertNodeExists(doc, "/html/body/div[@id='0']");

        // th:remove = "tag"
        assertNodeDoesNotExist(doc, "/html/body/div[@id='1']");
        assertNodeExists(doc, "/html/body/p[@id='1.1']");

        // th:remove = "body"
        assertNodeExists(doc, "/html/body/div[@id='2']");
        assertNodeDoesNotExist(doc, "/html/body/div[@id='2']/p");

        // th:remove = "all"
        assertNodeDoesNotExist(doc, "/html/body/div[@id='3']");
    }

    public void testTagRemovalWithVariables() throws Exception {
        Document doc = processTemplate("TagRemovalWithVariablesTest");

        // th:remove = "tag"
        assertNodeDoesNotExist(doc, "/html/body/div[@id='1']");
        assertNodeExists(doc, "/html/body/p[@id='1.1']");

    }

}
