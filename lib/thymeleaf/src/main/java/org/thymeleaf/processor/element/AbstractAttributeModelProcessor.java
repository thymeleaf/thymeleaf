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
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;

/**
 * <p>
 *   Basic abstract implementation of {@link IElementModelProcessor} for processors that match element
 *   events by one of their attributes (and optionally also the element name).
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractAttributeModelProcessor extends AbstractElementModelProcessor {


    private final boolean removeAttribute;


    protected AbstractAttributeModelProcessor(
            final TemplateMode templateMode, final String dialectPrefix,
            final String elementName, final boolean prefixElementName,
            final String attributeName, final boolean prefixAttributeName,
            final int precedence, final boolean removeAttribute) {
        super(templateMode, dialectPrefix, elementName, prefixElementName, attributeName, prefixAttributeName, precedence);
        this.removeAttribute = removeAttribute;
    }



    @Override
    protected final void doProcess(
            final ITemplateContext context,
            final IModel model,
            final IElementModelStructureHandler structureHandler) {

        AttributeName attributeName = null;
        IProcessableElementTag firstEvent = null;
        try {

            attributeName = getMatchingAttributeName().getMatchingAttributeName();
            firstEvent = (IProcessableElementTag) model.get(0);

            final String attributeValue =
                    EscapedAttributeUtils.unescapeAttribute(context.getTemplateMode(), firstEvent.getAttributeValue(attributeName));

            doProcess(context, model, attributeName, attributeValue, structureHandler);

            if (this.removeAttribute) {
                final int firstEventLocation = locateFirstEventInModel(model, firstEvent);
                if (firstEventLocation >= 0) {
                    firstEvent = (IProcessableElementTag) model.get(firstEventLocation);
                    final IModelFactory modelFactory = context.getModelFactory();
                    final IProcessableElementTag newFirstEvent = modelFactory.removeAttribute(firstEvent,attributeName);
                    if (newFirstEvent != firstEvent) {
                        model.replace(firstEventLocation, newFirstEvent);
                    }
                }
            }

        } catch (final TemplateProcessingException e) {

            // We will try to add all information possible to the exception report (template name, line, col)

            if (firstEvent != null) {

                String attributeTemplateName = firstEvent.getTemplateName();
                final IAttribute attribute = firstEvent.getAttribute(attributeName);
                int attributeLine = (attribute != null? attribute.getLine() : -1);
                int attributeCol = (attribute != null? attribute.getCol() : -1);

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

            }

            throw e;

        } catch (final Exception e) {

            // We will try to add all information possible to the exception report (template name, line, col)

            String attributeTemplateName = null;
            int attributeLine = -1;
            int attributeCol = -1;

            if (firstEvent != null) {

                attributeTemplateName = firstEvent.getTemplateName();
                final IAttribute attribute = firstEvent.getAttribute(attributeName);
                attributeLine = (attribute != null? attribute.getLine() : -1);
                attributeCol = (attribute != null? attribute.getCol() : -1);
            }

            throw new TemplateProcessingException(
                    "Error during execution of processor '" + this.getClass().getName() + "'", attributeTemplateName, attributeLine, attributeCol, e);

        }

    }


    protected abstract void doProcess(
            final ITemplateContext context,
            final IModel model,
            final AttributeName attributeName,
            final String attributeValue,
            final IElementModelStructureHandler structureHandler);



    private static int locateFirstEventInModel(final IModel model, final ITemplateEvent firstEvent) {
        final int modelSize = model.size();
        // First we will try to locate the exact same event in the model
        for (int i = 0; i < modelSize; i++) {
            // We can (should, actually) use reference equality here
            if (firstEvent == model.get(i)) {
                return i;
            }
        }
        // We weren't able to locate the exact same event, so we will just consider the first one, if it can contain attrs
        if (modelSize > 0 && model.get(0) instanceof IProcessableElementTag) {
            return 0;
        }
        return -1;
    }


}
