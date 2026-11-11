/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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
package org.thymeleaf.spring5.dialect;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.spring5.processor.SpringFormActionTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;


public class SpringStandardDialectFormActionTest {


    @Test
    public void testSingleFormActionProcessor() {

        final List<IProcessor> processors =
                SpringStandardDialect.createSpringStandardProcessorsSet("th", false).stream()
                        .filter(p -> p.getTemplateMode() == TemplateMode.HTML)
                        .filter(p -> p instanceof IElementTagProcessor)
                        .filter(p -> {
                            final MatchingAttributeName name = ((IElementTagProcessor) p).getMatchingAttributeName();
                            return name != null && name.getMatchingAttributeName() != null &&
                                    "formaction".equals(name.getMatchingAttributeName().getAttributeName());
                        })
                        .collect(Collectors.toList());

        Assertions.assertEquals(1, processors.size());
        Assertions.assertTrue(processors.get(0) instanceof SpringFormActionTagProcessor);

    }


}
