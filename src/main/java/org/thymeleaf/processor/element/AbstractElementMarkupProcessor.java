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
import org.thymeleaf.engine.AttributeNames;
import org.thymeleaf.engine.ElementNames;
import org.thymeleaf.engine.IMarkup;
import org.thymeleaf.engine.ITemplateHandlerEvent;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractElementMarkupProcessor
        extends AbstractProcessor implements IElementMarkupProcessor {

    private final String dialectPrefix;
    private final String elementName;
    private final boolean prefixElementName;
    private final String attributeName;
    private final boolean prefixAttributeName;
    private MatchingElementName matchingElementName;
    private MatchingAttributeName matchingAttributeName;



    public AbstractElementMarkupProcessor(
            final TemplateMode templateMode,
            final String dialectPrefix,
            final String elementName, final boolean prefixElementName,
            final String attributeName, final boolean prefixAttributeName,
            final int precedence) {

        super(templateMode, precedence);

        this.dialectPrefix = dialectPrefix;

        this.elementName = elementName;
        this.prefixElementName = prefixElementName;
        this.attributeName = attributeName;
        this.prefixAttributeName = prefixAttributeName;

        this.matchingElementName =
                (this.elementName == null?
                    null :
                    MatchingElementName.forElementName(
                            getTemplateMode(), ElementNames.forName(getTemplateMode(), (this.prefixElementName? this.dialectPrefix : null), this.elementName)));
        this.matchingAttributeName =
                (this.attributeName == null?
                    null :
                    MatchingAttributeName.forAttributeName(
                            getTemplateMode(), AttributeNames.forName(getTemplateMode(), (this.prefixAttributeName? this.dialectPrefix : null), this.attributeName)));

    }


    public final MatchingElementName getMatchingElementName() {
        return this.matchingElementName;
    }


    public final MatchingAttributeName getMatchingAttributeName() {
        return this.matchingAttributeName;
    }



    public final IMarkup process(final ITemplateProcessingContext processingContext, final IMarkup markup) {

        try {

            return doProcess(processingContext, markup);

        } catch (final TemplateProcessingException e) {

            if (markup.size() > 0) { // If size is 0, we have nowhere to extract template/line/col info from
                final ITemplateHandlerEvent firstEvent = markup.get(0);
                if (firstEvent != null && firstEvent.hasLocation()) {
                    if (!e.hasTemplateName()) {
                        e.setTemplateName(firstEvent.getTemplateName());
                    }
                    if (!e.hasLineAndCol()) {
                        e.setLineAndCol(firstEvent.getLine(), firstEvent.getCol());
                    }
                }
            }
            throw e;

        } catch (final Exception e) {

            String templateName = null;
            int line = -1;
            int col = -1;

            if (markup.size() > 0) { // If size is 0, we have nowhere to extract template/line/col info from
                final ITemplateHandlerEvent firstEvent = markup.get(0);
                if (firstEvent != null && firstEvent.hasLocation()) {
                    templateName = firstEvent.getTemplateName();
                    line = firstEvent.getLine();
                    col = firstEvent.getCol();
                }
            }
            throw new TemplateProcessingException(
                    "Error during execution of processor '" + this.getClass().getName() + "'", templateName, line, col, e);

        }

    }


    protected abstract IMarkup doProcess(final ITemplateProcessingContext processingContext, final IMarkup markup);

}
