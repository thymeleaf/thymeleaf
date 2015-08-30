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
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IElementStructureHandler;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.standard.inline.StandardInlineMode;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractStandardTextInlineSettingTagProcessor extends AbstractAttributeTagProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStandardTextInlineSettingTagProcessor.class);

    /*
     * NOTE This class does not extend AbstractStandardExpressionAttributeTagProcessor because expressions are
     *      actually NOT ALLOWED as values of a th:inline attribute, so that parsing-time event preprocessors like
     *      org.thymeleaf.templateparser.text.InlinedOutputExpressionProcessorTextHandler and
     *      org.thymeleaf.templateparser.markup.InlinedOutputExpressionProcessorMarkupHandler can
     *      do their job when the standard dialects are enabled, without the need to execute any expressions (and
     *      therefore without the need to pass a context to the PARSING phase of the execution, which should not
     *      depend on any execution context in order to be perfectly CACHEABLE).
     */



    protected AbstractStandardTextInlineSettingTagProcessor(
            final TemplateMode templateMode, final String dialectPrefix, final String attrName, final int precedence) {
        super(templateMode, dialectPrefix, null, false, attrName, true, precedence);
    }



    @Override
    protected final void doProcess(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IElementStructureHandler structureHandler) {

        // Check DART inlining, deprecated in 3.0.0 (defaults to JAVASCRIPT)
        final String normalizedAttributeValue = checkDartInline(attributeValue);

        // Note we are NOT executing the attributeValue as a Standard Expression: we are expecting a literal (see comment above)
        final IInliner inliner = getInliner(StandardInlineMode.parse(normalizedAttributeValue));
        structureHandler.setInliner(inliner);

        tag.getAttributes().removeAttribute(attributeName);

    }





    protected abstract IInliner getInliner(final StandardInlineMode inlineMode);




    /**
     * Check for the pre-3.0 "dart" inlining mode, which was removed when inlining modes were matched to template modes,
     * due to the inexistence of a (unneeded) "DART" template mode.
     *
     * NOTE this method is expressed here separately only in order to mark it as DEPRECATED and make it easily locatable
     *      as such.
     *
     * @param inliner the name of the inliner we are asking for
     * @return StandardJavaScriptInliner.INSTANCE if the inliner being asked for is "dart", null otherwise
     * @deprecated in 3.0.0, "javascript" inlining should be used instead (since 3.0.0 all inlining modes refer to
     *             template modes, and there is no "DART" template mode). Support for this default operation will be
     *             removed in 3.1
     */
    @Deprecated
    private static String checkDartInline(final String inliner) {
        if ("dart".equalsIgnoreCase(inliner)) {
            LOGGER.warn(
                    "[THYMELEAF][{}] Found inline call with value \"dart\", which has been deprecated as no " +
                    "corresponding template mode exists for it. Inline will be redirected to \"javascript\", which " +
                    "should now be used instead. This redirection will be removed in future versions of Thymeleaf.",
                    TemplateEngine.threadIndex());
            return "JAVASCRIPT";
        }
        return inliner;
    }

}
