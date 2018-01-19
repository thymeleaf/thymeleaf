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
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * <p>
 *   Basic abstract implementation of {@link IElementTagProcessor} for processors that match element
 *   events by their element name (i.e. without looking at any attributes).
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractElementTagProcessor
        extends AbstractProcessor implements IElementTagProcessor {

    private final String dialectPrefix;
    private final MatchingElementName matchingElementName;
    private final MatchingAttributeName matchingAttributeName;



    public AbstractElementTagProcessor(
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
            final IProcessableElementTag tag,
            final IElementTagStructureHandler structureHandler) {

        try {

            doProcess(context, tag, structureHandler);

        } catch (final TemplateProcessingException e) {
            // This is a nice moment to check whether the execution raised an error and, if so, add location information
            if (tag.hasLocation()) {
                if (!e.hasTemplateName()) {
                    e.setTemplateName(tag.getTemplateName());
                }
                if (!e.hasLineAndCol()) {
                    e.setLineAndCol(tag.getLine(), tag.getCol());
                }
            }
            throw e;
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error during execution of processor '" + this.getClass().getName() + "'",
                    tag.getTemplateName(), tag.getLine(), tag.getCol(), e);
        }

    }



    protected abstract void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final IElementTagStructureHandler structureHandler);



}
