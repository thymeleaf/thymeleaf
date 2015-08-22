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
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.inline.NoOpInliner;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.standard.inline.StandardCSSInliner;
import org.thymeleaf.standard.inline.StandardHTMLInliner;
import org.thymeleaf.standard.inline.StandardJavaScriptInliner;
import org.thymeleaf.standard.inline.StandardTextInliner;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardInlineHTMLTagProcessor extends AbstractStandardTextInlineSettingTagProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandardInlineHTMLTagProcessor.class);

    public static final int PRECEDENCE = 1000;
    public static final String ATTR_NAME = "inline";




    public StandardInlineHTMLTagProcessor(final String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, ATTR_NAME, PRECEDENCE);
    }



    @Override
    protected IInliner getInliner(
            final ITemplateProcessingContext processingContext, final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue, final Object expressionResult) {

        final String inliner = (expressionResult == null? null : expressionResult.toString().toLowerCase());

        if (inliner != null) {

            if (INLINE_MODE_NONE.equals(inliner)) {
                return NoOpInliner.INSTANCE;
            } else if (INLINE_MODE_HTML.equals(inliner)) {
                return StandardHTMLInliner.INSTANCE;
            } else if (INLINE_MODE_TEXT.equals(inliner)) {
                return StandardTextInliner.INSTANCE;
            } else if (INLINE_MODE_JAVASCRIPT.equals(inliner)) {
                return StandardJavaScriptInliner.INSTANCE;
            } else if (INLINE_MODE_CSS.equals(inliner)) {
                return StandardCSSInliner.INSTANCE;
            }

            // Check DART inlining, deprecated in 3.0.0 (defaults to JAVASCRIPT)
            final IInliner dartInliner = checkDartInline(inliner, tag.getLine(), tag.getCol());
            if (dartInliner != null) {
                return dartInliner;
            }

        }

        throw new TemplateProcessingException(
                "Cannot recognize value for \"" + attributeName + "\". Allowed inline modes in template mode " +
                getTemplateMode() + " are: " +
                "\"" + INLINE_MODE_HTML + "\", \"" + INLINE_MODE_TEXT + "\", " +
                "\"" + INLINE_MODE_JAVASCRIPT + "\", \"" + INLINE_MODE_CSS + "\" and " +
                "\"" + INLINE_MODE_NONE + "\"");

    }


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
    private static IInliner checkDartInline(final String inliner, final int line, final int col) {
        if ("dart".equalsIgnoreCase(inliner)) {
            LOGGER.warn(
                    "[THYMELEAF][{}] Found inline call with value \"dart\", which has been deprecated as no " +
                    "corresponding template mode exists for it. Inline will be redirected to \"javascript\", which " +
                    "should now be used instead. This redirection will be removed in future versions of Thymeleaf.",
                    TemplateEngine.threadIndex());
            return StandardJavaScriptInliner.INSTANCE;
        }
        return null;
    }


}
