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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.AttributeNames;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardSubstituteByTagProcessor extends AbstractStandardFragmentInsertionTagProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandardSubstituteByTagProcessor.class);

    public static final int PRECEDENCE = 100;
    public static final String ATTR_NAME = "substituteby";





    public StandardSubstituteByTagProcessor(final IProcessorDialect dialect, final TemplateMode templateMode, final String dialectPrefix) {
        super(dialect, templateMode, dialectPrefix, ATTR_NAME, PRECEDENCE, true, false);
    }


    @Override
    protected void doProcess(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final String attributeTemplateName, final int attributeLine, final int attributeCol,
            final IElementTagStructureHandler structureHandler) {

        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(
                    "[THYMELEAF][{}][{}] Deprecated attribute {} found in template {}, line {}, col {}. " +
                    "Please use {} instead, this deprecated attribute will be removed in future versions of Thymeleaf.",
                    new Object[]{
                            TemplateEngine.threadIndex(), processingContext.getTemplateResolution().getTemplateName(),
                            attributeName, attributeTemplateName, attributeLine, attributeCol, AttributeNames.forHTMLName(attributeName.getPrefix(), StandardReplaceTagProcessor.ATTR_NAME)});
        }

        super.doProcess(processingContext, tag, attributeName, attributeValue, attributeTemplateName, attributeLine, attributeCol, structureHandler);

    }

}
