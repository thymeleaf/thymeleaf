/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.standard.processor;

import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardNonRemovableAttributeTagProcessor extends AbstractStandardAttributeModifierTagProcessor {

    public static final int PRECEDENCE = 1000;

    // These attributes should not be removed even if their value evaluates to null or empty string
    // The reason why we don't let all these attributes to be processed by the default processor is that some other attribute
    // processors executing afterwards (e.g. th:field) might need attribute values already processed by these.
    public static final String[] ATTR_NAMES =
            new String[] {
                    "name",
                    "type"
            };


    public StandardNonRemovableAttributeTagProcessor(final String dialectPrefix, final String attrName) {
        super(TemplateMode.HTML, dialectPrefix, attrName, PRECEDENCE, false, false);
    }


}
