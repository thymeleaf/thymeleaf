/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.processor.element;

import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IMarkup;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractAttributeMarkupProcessor extends AbstractElementMarkupProcessor {


    private final boolean removeAttribute;


    protected AbstractAttributeMarkupProcessor(
            final TemplateMode templateMode, final String dialectPrefix,
            final String elementName, final boolean prefixElementName,
            final String attributeName, final boolean prefixAttributeName,
            final int precedence, final boolean removeAttribute) {
        super(templateMode, dialectPrefix, elementName, prefixElementName, attributeName, prefixAttributeName, precedence);
        this.removeAttribute = removeAttribute;
    }



    @Override
    protected final void doProcess(
            final ITemplateProcessingContext processingContext,
            final IMarkup markup,
            final String markupTemplateName, final int markupLine, final int markupCol) {

        String attributeTemplateName = null;
        int attributeLine = -1;
        int attributeCol = -1;
        try {

            final AttributeName attributeName = getMatchingAttributeName().getMatchingAttributeName();

            final IProcessableElementTag firstEvent = (IProcessableElementTag) markup.get(0);
            attributeTemplateName = firstEvent.getTemplateName();
            attributeLine = firstEvent.getAttributes().getLine(attributeName);
            attributeCol = firstEvent.getAttributes().getCol(attributeName);

            final String attributeValue =
                    EscapedAttributeUtils.unescapeAttribute(processingContext.getTemplateMode(), firstEvent.getAttributes().getValue(attributeName));

            if (this.removeAttribute) {
                firstEvent.getAttributes().removeAttribute(attributeName);
            }

            doProcess(processingContext, markup, attributeName, attributeValue, attributeTemplateName, attributeLine, attributeCol);

        } catch (final TemplateProcessingException e) {

            if (attributeTemplateName != null) {
                if (!e.hasTemplateName()) {
                    e.setTemplateName(attributeTemplateName);
                }
            }
            if (attributeLine != -1 && attributeCol != -1) {
                if (!e.hasLineAndCol()) {
                    e.setLineAndCol(attributeLine, attributeCol);
                }
            }
            throw e;

        } catch (final Exception e) {

            throw new TemplateProcessingException(
                    "Error during execution of processor '" + this.getClass().getName() + "'", attributeTemplateName, attributeLine, attributeCol, e);

        }

    }


    protected abstract void doProcess(
            final ITemplateProcessingContext processingContext,
            final IMarkup markup,
            final AttributeName attributeName,
            final String attributeValue,
            final String attributeTemplateName, final int attributeLine, final int attributeCol);



}
