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
package org.thymeleaf.templateengine.features.elementstack;

import java.util.List;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IText;
import org.thymeleaf.processor.text.AbstractTextProcessor;
import org.thymeleaf.processor.text.ITextStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

public class ElementStackTextProcessor extends AbstractTextProcessor {


    public ElementStackTextProcessor(final String dialectPrefix) {
        super(TemplateMode.HTML, 10000);
    }


    @Override
    protected void doProcess(final ITemplateContext context, final IText text, final ITextStructureHandler structureHandler) {

        final List<IProcessableElementTag> stack = context.getElementStack();

        final StringBuilder stringBuilder = new StringBuilder();
        for (final IProcessableElementTag stackTag : stack) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(stackTag.getElementCompleteName());
            for (final IAttribute attribute : stackTag.getAllAttributes()) {
                stringBuilder.append(" ");
                stringBuilder.append(attribute.getAttributeCompleteName());
            }
        }

        final String t = text.getText();

        structureHandler.setText(t + " " + stringBuilder.toString());

    }
}
