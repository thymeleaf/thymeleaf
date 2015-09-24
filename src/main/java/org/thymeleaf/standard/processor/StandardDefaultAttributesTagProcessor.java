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

import java.util.List;

import org.attoparser.util.TextUtil;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IElementAttributes;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardDefaultAttributesTagProcessor
        extends AbstractProcessor implements IElementTagProcessor {


    // Setting to Integer.MAX_VALUE is alright - we will always be limited by the dialect precedence anyway
    public static final int PRECEDENCE = Integer.MAX_VALUE;


    private final String dialectPrefix;
    private final MatchingAttributeName matchingAttributeName;



    public StandardDefaultAttributesTagProcessor(final IProcessorDialect dialect, final TemplateMode templateMode, final String dialectPrefix) {
        super(dialect, templateMode, PRECEDENCE);
        this.dialectPrefix = dialectPrefix;
        this.matchingAttributeName = MatchingAttributeName.forAllAttributesWithPrefix(getTemplateMode(), dialectPrefix);
    }


    public final MatchingElementName getMatchingElementName() {
        return null;
    }


    public final MatchingAttributeName getMatchingAttributeName() {
        return this.matchingAttributeName;
    }



    // Default implementation - meant to be overridden by subclasses if needed
    public void process(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final IElementTagStructureHandler structureHandler) {


        final TemplateMode templateMode = getTemplateMode();
        final IElementAttributes attributes = tag.getAttributes();
        final List<AttributeName> attributeNames = attributes.getAllAttributeNames();

        // Should be no problem in performing modifications during iteration, as the attributeNames list
        // should not be affected by modifications on the original tag attribute set
        for (final AttributeName attributeName : attributeNames) {

            if (attributeName.isPrefixed()) {
                if (TextUtil.equals(templateMode.isCaseSensitive(), attributeName.getPrefix(), this.dialectPrefix)) {



                }
            }

        }



//        final IEngineConfiguration configuration = processingContext.getConfiguration();
//        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
//
//        final IStandardExpression expression = expressionParser.parseExpression(processingContext, attributeValue);
//        final Object expressionResult = expression.execute(processingContext);

        System.out.println("DEFAULT ACTING ON ELEMENT: " + tag.toString());

    }



}
