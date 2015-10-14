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

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
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
public abstract class AbstractAttributeTagProcessor extends AbstractElementTagProcessor {


    private final boolean removeAttribute;


    protected AbstractAttributeTagProcessor(
            final IProcessorDialect dialect, final TemplateMode templateMode, final String dialectPrefix,
            final String elementName, final boolean prefixElementName,
            final String attributeName, final boolean prefixAttributeName,
            final int precedence, final boolean removeAttribute) {
        super(dialect, templateMode, dialectPrefix, elementName, prefixElementName, attributeName, prefixAttributeName, precedence);
        this.removeAttribute = removeAttribute;
    }



    @Override
    protected final void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final String tagTemplateName, final int tagLine, final int tagCol,
            final IElementTagStructureHandler structureHandler) {

        AttributeName attributeName = null;
        int attributeLine = -1;
        int attributeCol = -1;
        try {

            attributeName = getMatchingAttributeName().getMatchingAttributeName();
            attributeLine = tag.getAttributes().getLine(attributeName);
            attributeCol = tag.getAttributes().getCol(attributeName);

            final String attributeValue =
                    EscapedAttributeUtils.unescapeAttribute(context.getTemplateMode(), tag.getAttributes().getValue(attributeName));

            if (this.removeAttribute) {
                tag.getAttributes().removeAttribute(attributeName);
            }

            doProcess(
                    context, tag,
                    attributeName, attributeValue,
                    tagTemplateName, attributeLine, attributeCol, structureHandler);

        } catch (final TemplateProcessingException e) {
            // This is a nice moment to check whether the execution raised an error and, if so, add location information
            // Note this is similar to what is done at the superclass AbstractElementTagProcessor, but we can be more
            // specific because we know exactly what attribute was being executed and caused the error
            if (tag.hasLocation()) {
                if (!e.hasTemplateName()) {
                    e.setTemplateName(tagTemplateName);
                }
                if (!e.hasLineAndCol()) {
                    if (attributeName == null) {
                        // We don't have info about the specific attribute provoking the error
                        attributeLine = tag.getLine();
                        attributeCol = tag.getCol();
                    }
                    e.setLineAndCol(attributeLine, attributeCol);
                }
            }
            throw e;
        } catch (final Exception e) {
            if (attributeName == null) {
                // We don't have info about the specific attribute provoking the error
                attributeLine = tag.getLine();
                attributeCol = tag.getCol();
            }
            throw new TemplateProcessingException(
                    "Error during execution of processor '" + this.getClass().getName() + "'",
                    tagTemplateName, attributeLine, attributeCol, e);
        }

    }


    protected abstract void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName,
            final String attributeValue,
            final String attributeTemplateName, final int attributeLine, final int attributeCol,
            final IElementTagStructureHandler structureHandler);



}
