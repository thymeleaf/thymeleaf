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
import org.thymeleaf.engine.IElementStructureHandler;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractElementTagProcessor
        extends AbstractProcessor implements IElementTagProcessor {

    private final String elementName;
    private final boolean prefixElementName;
    private final String attributeName;
    private final boolean prefixAttributeName;


    private MatchingElementName matchingElementName = null;
    private MatchingAttributeName matchingAttributeName = null;



    public AbstractElementTagProcessor(
            final TemplateMode templateMode,
            final String elementName, final boolean prefixElementName,
            final String attributeName, final boolean prefixAttributeName,
            final int precedence) {

        super(templateMode, precedence);

        this.elementName = elementName;
        this.prefixElementName = prefixElementName;
        this.attributeName = attributeName;
        this.prefixAttributeName = prefixAttributeName;

    }


    public final MatchingElementName getMatchingElementName() {
        return this.matchingElementName;
    }


    public final MatchingAttributeName getMatchingAttributeName() {
        return this.matchingAttributeName;
    }


    public final void setDialectPrefix(final String dialectPrefix) {
        super.setDialectPrefix(dialectPrefix);
        if (this.elementName != null) {
            this.matchingElementName =
                    MatchingElementName.forElementName(
                            getTemplateMode(), ElementNames.forName(getTemplateMode(), (this.prefixElementName? getDialectPrefix() : null), this.elementName));
        }
        if (this.attributeName != null) {
            this.matchingAttributeName =
                    MatchingAttributeName.forAttributeName(
                            getTemplateMode(), AttributeNames.forName(getTemplateMode(), (this.prefixAttributeName? getDialectPrefix() : null), this.attributeName));
        }
    }



    // Default implementation - meant to be overridden by subclasses if needed
    public void process(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final IElementStructureHandler structureHandler) {
        // Nothing to do here -- empty action
    }


}
