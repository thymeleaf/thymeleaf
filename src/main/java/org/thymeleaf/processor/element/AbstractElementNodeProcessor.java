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

import java.util.List;

import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.engine.AttributeNames;
import org.thymeleaf.engine.ElementNames;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.INode;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractElementNodeProcessor
        extends AbstractProcessor implements IElementNodeProcessor {

    private final String dialectPrefix;
    private final String elementName;
    private final boolean prefixElementName;
    private final String attributeName;
    private final boolean prefixAttributeName;
    private MatchingElementName matchingElementName;
    private MatchingAttributeName matchingAttributeName;



    public AbstractElementNodeProcessor(
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



    public final List<INode> process(final ITemplateProcessingContext processingContext, final INode node) {

        try {

            return doProcess(processingContext, node);

        } catch (final TemplateProcessingException e) {
            if (!e.hasTemplateName()) {
                e.setTemplateName(node.getTemplateName());
            }
            if (!e.hasLineAndCol()) {
                e.setLineAndCol(node.getLine(), node.getCol());
            }
            throw e;
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error during execution of processor '" + this.getClass().getName() + "'",
                    node.getTemplateName(), node.getLine(), node.getCol(), e);
        }

    }


    protected abstract List<INode> doProcess(final ITemplateProcessingContext processingContext, final INode node);

}
