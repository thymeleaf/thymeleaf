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
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IElementAttributes;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.NoOpToken;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;
import org.unbescape.html.HtmlEscape;

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



    public StandardDefaultAttributesTagProcessor(final TemplateMode templateMode, final String dialectPrefix) {
        super(templateMode, PRECEDENCE);
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
            final ITemplateContext context,
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

                    // We will process each 'default' attribute separately
                    processDefaultAttribute(context, tag, attributeName);

                }
            }

        }

    }



    private static void processDefaultAttribute(
            final ITemplateContext context,
            final IProcessableElementTag tag, final AttributeName attributeName) {

        try {

            final String attributeValue =
                    EscapedAttributeUtils.unescapeAttribute(
                            context.getTemplateMode(), tag.getAttributes().getValue(attributeName));


            /*
             * Compute the new attribute name (i.e. the same, without the prefix)
             */
            final String originalCompleteAttributeName = tag.getAttributes().getCompleteName(attributeName);
            final String canonicalAttributeName = attributeName.getAttributeName();

            final String newAttributeName;
            if (TextUtil.endsWith(true, originalCompleteAttributeName, canonicalAttributeName)) {
                newAttributeName = canonicalAttributeName; // We avoid creating a new String instance
            } else {
                newAttributeName =
                        originalCompleteAttributeName.substring(originalCompleteAttributeName.length() - canonicalAttributeName.length());
            }


            /*
             * Obtain the parser
             */
            final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());

            /*
             * Execute the expression, handling nulls in a way consistent with the rest of the Standard Dialect
             */
            final Object expressionResult;
            if (attributeValue != null) {

                final IStandardExpression expression = expressionParser.parseExpression(context, attributeValue);

                if (expression != null && expression instanceof FragmentExpression) {
                    // This is merely a FragmentExpression (not complex, not combined with anything), so we can apply a shortcut
                    // so that we don't require a "null" result for this expression if the template does not exist. That will
                    // save a call to resource.exists() which might be costly.

                    final FragmentExpression.ExecutedFragmentExpression executedFragmentExpression =
                            FragmentExpression.createExecutedFragmentExpression(context, (FragmentExpression) expression, StandardExpressionExecutionContext.NORMAL);

                    expressionResult =
                            FragmentExpression.resolveExecutedFragmentExpression(context, executedFragmentExpression, true);

                } else {

                    expressionResult = expression.execute(context);

                }

            } else {
                expressionResult = null;
            }

            /*
             * If the result of this expression is NO-OP, there is nothing to execute
             */
            if (expressionResult == NoOpToken.VALUE) {
                tag.getAttributes().removeAttribute(attributeName);
                return;
            }

            /*
             * Compute the new attribute value
             */
            final String newAttributeValue = HtmlEscape.escapeHtml4Xml(expressionResult == null ? null : expressionResult.toString());

            /*
             * Set the new value, removing the attribute completely if the expression evaluated to null
             */
            if (newAttributeValue == null || newAttributeValue.length() == 0) {
                // We are removing the equivalent attribute name, without the prefix...
                tag.getAttributes().removeAttribute(newAttributeName);
                tag.getAttributes().removeAttribute(attributeName);
            } else {
                // We are setting the equivalent attribute name, without the prefix...
                tag.getAttributes().replaceAttribute(attributeName, newAttributeName, (newAttributeValue == null? "" : newAttributeValue));
            }

        } catch (final TemplateProcessingException e) {
            // This is a nice moment to check whether the execution raised an error and, if so, add location information
            // Note this is similar to what is done at the superclass AbstractElementTagProcessor, but we can be more
            // specific because we know exactly what attribute was being executed and caused the error
            if (!e.hasTemplateName()) {
                e.setTemplateName(tag.getTemplateName());
            }
            if (!e.hasLineAndCol()) {
                e.setLineAndCol(tag.getAttributes().getLine(attributeName), tag.getAttributes().getCol(attributeName));
            }
            throw e;
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error during execution of processor '" + StandardDefaultAttributesTagProcessor.class.getName() + "'",
                    tag.getTemplateName(), tag.getAttributes().getLine(attributeName), tag.getAttributes().getCol(attributeName), e);
        }

    }



}
