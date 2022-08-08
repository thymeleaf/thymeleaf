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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/*
 *
 * @author Daniel Fernandez
 * @since 2.0.0
 */
public class TemplateFragmentMarkupReferenceResolverTest {


    @Test
    public void testHtml() throws Exception {

        final TemplateFragmentMarkupReferenceResolver rr01 = TemplateFragmentMarkupReferenceResolver.forPrefix(true, null);
        final TemplateFragmentMarkupReferenceResolver rr02 = TemplateFragmentMarkupReferenceResolver.forPrefix(true, "th");

        final TemplateFragmentMarkupReferenceResolver rr03 = TemplateFragmentMarkupReferenceResolver.forPrefix(true, null);
        final TemplateFragmentMarkupReferenceResolver rr04 = TemplateFragmentMarkupReferenceResolver.forPrefix(true, "th");
        Assertions.assertSame(rr01, rr03);
        Assertions.assertSame(rr02, rr04);

        final TemplateFragmentMarkupReferenceResolver rr05 = TemplateFragmentMarkupReferenceResolver.forPrefix(true, "q");

        final String result01 = rr01.resolveSelectorFromReference("abc");
        final String result02 = rr01.resolveSelectorFromReference("abc");
        Assertions.assertEquals("/[ref='abc' or data-ref='abc' or fragment='abc' or data-fragment='abc' or fragment^='abc(' or data-fragment^='abc(' or fragment^='abc (' or data-fragment^='abc (']", result01);
        Assertions.assertSame(result01, result02);

        final String result03 = rr02.resolveSelectorFromReference("abc");
        final String result04 = rr02.resolveSelectorFromReference("abc");
        Assertions.assertEquals("/[th:ref='abc' or data-th-ref='abc' or th:fragment='abc' or data-th-fragment='abc' or th:fragment^='abc(' or data-th-fragment^='abc(' or th:fragment^='abc (' or data-th-fragment^='abc (']", result03);
        Assertions.assertSame(result03, result04);

        final String result05 = rr05.resolveSelectorFromReference("abc");
        final String result06 = rr05.resolveSelectorFromReference("abc");
        Assertions.assertEquals("/[q:ref='abc' or data-q-ref='abc' or q:fragment='abc' or data-q-fragment='abc' or q:fragment^='abc(' or data-q-fragment^='abc(' or q:fragment^='abc (' or data-q-fragment^='abc (']", result05);
        Assertions.assertSame(result05, result06);

        final String result07 = rr02.resolveSelectorFromReference("abc");
        final String result08 = rr02.resolveSelectorFromReference("abc");
        Assertions.assertEquals("/[th:ref='abc' or data-th-ref='abc' or th:fragment='abc' or data-th-fragment='abc' or th:fragment^='abc(' or data-th-fragment^='abc(' or th:fragment^='abc (' or data-th-fragment^='abc (']", result07);
        Assertions.assertSame(result07, result08);

    }


    @Test
    public void testXml() throws Exception {

        final TemplateFragmentMarkupReferenceResolver rr01 = TemplateFragmentMarkupReferenceResolver.forPrefix(false, null);
        final TemplateFragmentMarkupReferenceResolver rr02 = TemplateFragmentMarkupReferenceResolver.forPrefix(false, "th");

        final TemplateFragmentMarkupReferenceResolver rr03 = TemplateFragmentMarkupReferenceResolver.forPrefix(false, null);
        final TemplateFragmentMarkupReferenceResolver rr04 = TemplateFragmentMarkupReferenceResolver.forPrefix(false, "th");
        Assertions.assertSame(rr01, rr03);
        Assertions.assertSame(rr02, rr04);

        final TemplateFragmentMarkupReferenceResolver rr05 = TemplateFragmentMarkupReferenceResolver.forPrefix(false, "q");

        final String result01 = rr01.resolveSelectorFromReference("abc");
        final String result02 = rr01.resolveSelectorFromReference("abc");
        Assertions.assertEquals("/[ref='abc' or fragment='abc' or fragment^='abc(' or fragment^='abc (']", result01);
        Assertions.assertSame(result01, result02);

        final String result03 = rr02.resolveSelectorFromReference("abc");
        final String result04 = rr02.resolveSelectorFromReference("abc");
        Assertions.assertEquals("/[th:ref='abc' or th:fragment='abc' or th:fragment^='abc(' or th:fragment^='abc (']", result03);
        Assertions.assertSame(result03, result04);

        final String result05 = rr05.resolveSelectorFromReference("abc");
        final String result06 = rr05.resolveSelectorFromReference("abc");
        Assertions.assertEquals("/[q:ref='abc' or q:fragment='abc' or q:fragment^='abc(' or q:fragment^='abc (']", result05);
        Assertions.assertSame(result05, result06);

        final String result07 = rr02.resolveSelectorFromReference("abc");
        final String result08 = rr02.resolveSelectorFromReference("abc");
        Assertions.assertEquals("/[th:ref='abc' or th:fragment='abc' or th:fragment^='abc(' or th:fragment^='abc (']", result07);
        Assertions.assertSame(result07, result08);

    }

}
