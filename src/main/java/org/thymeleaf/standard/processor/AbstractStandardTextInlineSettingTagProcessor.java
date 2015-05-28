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
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IElementStructureHandler;
import org.thymeleaf.inline.ITextInliner;
import org.thymeleaf.model.IProcessableElementTag;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractStandardTextInlineSettingTagProcessor extends AbstractStandardExpressionAttributeTagProcessor {


    public static final String TEXT_INLINE = "text";
    public static final String JAVASCRIPT_INLINE = "javascript";
    public static final String DART_INLINE = "dart";
    public static final String NONE_INLINE = "none";



    protected AbstractStandardTextInlineSettingTagProcessor(
            final String dialectPrefix, final String attrName, final int precedence) {
        super(dialectPrefix, attrName, precedence);
    }



    @Override
    protected final void doProcess(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue, final Object expressionResult,
            final IElementStructureHandler structureHandler) {

        final ITextInliner inliner = getTextInliner(processingContext, tag, attributeName, attributeValue, expressionResult);
        structureHandler.setTextInliner(inliner);

        tag.getAttributes().removeAttribute(attributeName);

    }





    protected abstract ITextInliner getTextInliner(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue, final Object expressionResult);



}
