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
package org.thymeleaf.dom;

import org.junit.Assert;
import org.junit.Test;


public final class ElementTest {



    @Test
    public void testGetUnprefixedElementName() throws Exception {
        Assert.assertEquals("assert", Element.getUnprefixedElementName("th:assert"));
        Assert.assertEquals("src", Element.getUnprefixedElementName("src"));
        Assert.assertEquals("th", Element.getUnprefixedElementName("xmlns:th"));
        Assert.assertEquals("assert", Element.getUnprefixedElementName("th-assert"));
        Assert.assertEquals("th-assert", Element.getUnprefixedElementName("data-th-assert"));
        Assert.assertEquals("something", Element.getUnprefixedElementName("data-something"));
    }


    @Test
    public void testGetPrefix() throws Exception {

        Assert.assertEquals("th", Element.getPrefixFromElementName("th:assert"));
        Assert.assertEquals(null, Element.getPrefixFromElementName("src"));
        Assert.assertEquals("xmlns", Element.getPrefixFromElementName("xmlns:th"));
        Assert.assertEquals("data", Element.getPrefixFromElementName("data-th-assert"));
        Assert.assertEquals("data", Element.getPrefixFromElementName("data-something"));
        Assert.assertEquals("th", Element.getPrefixFromElementName("th-assert"));

    }

}
