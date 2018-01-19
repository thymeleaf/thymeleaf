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
import org.thymeleaf.engine.AttributeNames;
import org.thymeleaf.engine.ElementNames;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * <p>
 *   Basic abstract implementation of {@link IElementModelProcessor} for processors that match element
 *   events by their element name (i.e. without looking at any attributes).
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractElementModelProcessor
        extends AbstractProcessor implements IElementModelProcessor {

    private final String dialectPrefix;
    private final MatchingElementName matchingElementName;
    private final MatchingAttributeName matchingAttributeName;



    public AbstractElementModelProcessor(
            final TemplateMode templateMode, final String dialectPrefix,
            final String elementName, final boolean prefixElementName,
            final String attributeName, final boolean prefixAttributeName,
            final int precedence) {

        super(templateMode, precedence);

        this.dialectPrefix = dialectPrefix;

        this.matchingElementName =
                (elementName == null?
                    null :
                    MatchingElementName.forElementName(
                            templateMode, ElementNames.forName(templateMode, (prefixElementName? this.dialectPrefix : null), elementName)));
        this.matchingAttributeName =
                (attributeName == null?
                    null :
                    MatchingAttributeName.forAttributeName(
                            templateMode, AttributeNames.forName(templateMode, (prefixAttributeName? this.dialectPrefix : null), attributeName)));

    }

    protected final String getDialectPrefix() {
        return this.dialectPrefix;
    }

    public final MatchingElementName getMatchingElementName() {
        return this.matchingElementName;
    }


    public final MatchingAttributeName getMatchingAttributeName() {
        return this.matchingAttributeName;
    }



    public final void process(
            final ITemplateContext context,
            final IModel model, final IElementModelStructureHandler structureHandler) {

        ITemplateEvent firstEvent = null;
        try {

            firstEvent = model.get(0);

            doProcess(context, model, structureHandler);

        } catch (final TemplateProcessingException e) {

            // We will try to add all information possible to the exception report (template name, line, col)

            if (firstEvent != null) {

                String modelTemplateName = firstEvent.getTemplateName();
                int modelLine = firstEvent.getLine();
                int modelCol = firstEvent.getCol();

                if (modelTemplateName != null) {
                    if (!e.hasTemplateName()) {
                        e.setTemplateName(modelTemplateName);
                    }
                }
                if (modelLine != -1 && modelCol != -1) {
                    if (!e.hasLineAndCol()) {
                        e.setLineAndCol(modelLine, modelCol);
                    }
                }

            }

            throw e;

        } catch (final Exception e) {

            // We will try to add all information possible to the exception report (template name, line, col)

            String modelTemplateName = null;
            int modelLine = -1;
            int modelCol = -1;

            if (firstEvent != null) {

                modelTemplateName = firstEvent.getTemplateName();
                modelLine = firstEvent.getLine();
                modelCol = firstEvent.getCol();
            }

            throw new TemplateProcessingException(
                    "Error during execution of processor '" + this.getClass().getName() + "'", modelTemplateName, modelLine, modelCol, e);

        }

    }


    protected abstract void doProcess(
            final ITemplateContext context,
            final IModel model,
            final IElementModelStructureHandler structureHandler);

}
