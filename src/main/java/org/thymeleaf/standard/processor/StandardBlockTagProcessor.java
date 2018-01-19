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

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardBlockTagProcessor extends AbstractElementTagProcessor {

    public static final int PRECEDENCE = 100000;
    public static final String ELEMENT_NAME = "block";



    public StandardBlockTagProcessor(final TemplateMode templateMode, final String dialectPrefix, final String elementName) {
        super(templateMode, dialectPrefix, elementName, (dialectPrefix != null), null, false, PRECEDENCE);
    }


    @Override
    protected void doProcess(final ITemplateContext context,
                             final IProcessableElementTag tag,
                             final IElementTagStructureHandler structureHandler) {

        // We are just removing the "<th:block>", leaving whichever contents (body) it might have generated.
        structureHandler.removeTags();

    }


}
