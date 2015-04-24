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
package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.engine.IElementStructureHandler;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class StandardDefaultAttributesTagProcessor
        extends AbstractProcessor implements IElementTagProcessor {

    private MatchingElementName matchingElementName = null;
    private MatchingAttributeName matchingAttributeName = null;



    public StandardDefaultAttributesTagProcessor() {
        super(TemplateMode.HTML, Integer.MAX_VALUE);
    }


    public final MatchingElementName getMatchingElementName() {
        return this.matchingElementName;
    }


    public final MatchingAttributeName getMatchingAttributeName() {
        return this.matchingAttributeName;
    }


    public final void setDialectPrefix(final String dialectPrefix) {
        super.setDialectPrefix(dialectPrefix);
        this.matchingElementName = null;
        this.matchingAttributeName = MatchingAttributeName.forAllAttributesWithPrefix(getTemplateMode(), dialectPrefix);
    }



    // Default implementation - meant to be overridden by subclasses if needed
    public void process(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final IElementStructureHandler structureHandler) {

//        System.out.println("DEFAULT ACTING ON ELEMENT: " + tag.toString());

    }


}
