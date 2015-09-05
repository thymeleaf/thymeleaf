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
package org.thymeleaf.spring3.processor;

import java.util.Map;

import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring3.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.html.HtmlEscape;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class  SpringActionTagProcessor extends AbstractStandardExpressionAttributeTagProcessor {


    public static final int ATTR_PRECEDENCE = 1000;
    public static final String ATTR_NAME = "action";



    public SpringActionTagProcessor(final String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, ATTR_NAME, ATTR_PRECEDENCE, true);
    }



    @Override
    protected final void doProcess(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final String attributeTemplateName, final int attributeLine, final int attributeCol,
            final Object expressionResult,
            final IElementTagStructureHandler structureHandler) {

        String newAttributeValue = HtmlEscape.escapeHtml4Xml(expressionResult == null ? "" : expressionResult.toString());

        // But before setting the 'action' attribute, we need to verify the 'method' attribute and let the
        // RequestDataValueProcessor act on it.
        final String httpMethod = tag.getAttributes().getValue("method");

        // Let RequestDataValueProcessor modify the attribute value if needed
        newAttributeValue = RequestDataValueProcessorUtils.processAction(processingContext, newAttributeValue, httpMethod);

        // Set the 'action' attribute
        tag.getAttributes().setAttribute(ATTR_NAME, (newAttributeValue == null? "" : newAttributeValue));

        // If this th:action is in a <form> tag, we might need to add a hidden field (depending on Spring configuration)
        if ("form".equalsIgnoreCase(tag.getElementName())) {

            final Map<String,String> extraHiddenFields =
                    RequestDataValueProcessorUtils.getExtraHiddenFields(processingContext);

            if (extraHiddenFields != null && extraHiddenFields.size() > 0) {

                final IModel extraHiddenElementTags = processingContext.getModelFactory().createModel();

                for (final Map.Entry<String,String> extraHiddenField : extraHiddenFields.entrySet()) {

                    final IStandaloneElementTag extraHiddenElementTag =
                            processingContext.getModelFactory().createStandaloneElementTag("input", true);

                    extraHiddenElementTag.getAttributes().setAttribute("type", "hidden");
                    extraHiddenElementTag.getAttributes().setAttribute("name", extraHiddenField.getKey());
                    extraHiddenElementTag.getAttributes().setAttribute("value", extraHiddenField.getValue()); // no need to re-apply the processor here

                    extraHiddenElementTags.add(extraHiddenElementTag);

                }

                structureHandler.insertAfter(extraHiddenElementTags, false);

            }

        }

    }



}
