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
package org.thymeleaf.processor.element;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Basic abstract implementation of {@link IElementTagProcessor} for processors that match element
 *   events by one of their attributes (and optionally also the element name).
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractAttributeTagProcessor extends AbstractElementTagProcessor {


    private final boolean removeAttribute;


    protected AbstractAttributeTagProcessor(
            final TemplateMode templateMode, final String dialectPrefix,
            final String elementName, final boolean prefixElementName,
            final String attributeName, final boolean prefixAttributeName,
            final int precedence, final boolean removeAttribute) {
        super(templateMode, dialectPrefix, elementName, prefixElementName, attributeName, prefixAttributeName, precedence);
        Validate.notEmpty(attributeName, "Attribute name cannot be null or empty in Attribute Tag Processor");
        this.removeAttribute = removeAttribute;
    }



    @Override
    protected final void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final IElementTagStructureHandler structureHandler) {

        AttributeName attributeName = null;
        try {

            attributeName = getMatchingAttributeName().getMatchingAttributeName();

            final String attributeValue =
                    EscapedAttributeUtils.unescapeAttribute(context.getTemplateMode(), tag.getAttributeValue(attributeName));

            doProcess(
                    context, tag,
                    attributeName, attributeValue,
                    structureHandler);

            if (this.removeAttribute) {
                structureHandler.removeAttribute(attributeName);
            }

        } catch (final TemplateProcessingException e) {
            // This is a nice moment to check whether the execution raised an error and, if so, add location information
            // Note this is similar to what is done at the superclass AbstractElementTagProcessor, but we can be more
            // specific because we know exactly what attribute was being executed and caused the error
            if (tag.hasLocation()) {
                if (!e.hasTemplateName()) {
                    e.setTemplateName(tag.getTemplateName());
                }
                if (!e.hasLineAndCol()) {
                    if (attributeName == null) {
                        // We don't have info about the specific attribute provoking the error
                        e.setLineAndCol(tag.getLine(), tag.getCol());
                    } else {
                        final IAttribute attribute = tag.getAttribute(attributeName);
                        if (attribute != null) {
                            e.setLineAndCol(attribute.getLine(), attribute.getCol());
                        }
                    }
                }
            }
            throw e;
        } catch (final Exception e) {
            int line = tag.getLine();
            int col = tag.getCol();
            if (attributeName != null) {
                // We don't have info about the specific attribute provoking the error
                final IAttribute attribute = tag.getAttribute(attributeName);
                if (attribute != null) {
                    line = attribute.getLine();
                    col = attribute.getCol();
                }
            }
            throw new TemplateProcessingException(
                    "Error during execution of processor '" + this.getClass().getName() + "'",
                    tag.getTemplateName(), line, col, e);
        }

    }


    protected abstract void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName,
            final String attributeValue,
            final IElementTagStructureHandler structureHandler);



}
