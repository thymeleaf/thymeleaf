/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.EngineEventUtils;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.inline.NoOpInliner;
import org.thymeleaf.model.IText;
import org.thymeleaf.processor.text.AbstractTextProcessor;
import org.thymeleaf.processor.text.ITextStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 */
public final class StandardInliningTextProcessor extends AbstractTextProcessor {

    public static final int PRECEDENCE = 1000;

    public StandardInliningTextProcessor(final TemplateMode templateMode) {
        super(templateMode, PRECEDENCE);
    }


    @Override
    protected void doProcess(
            final ITemplateContext context,
            final IText text, final ITextStructureHandler structureHandler) {

        if (EngineEventUtils.isWhitespace(text)) {
            // Fail fast - a whitespace text is never inlineable. And templates tend to have a lot of white space blocks
            // NOTE we are not using isInlineable() here because before doing so the template mode would have to be
            // checked (so that th:inline works alright). But white spaces are a safe bet.
            return;
        }

        final IInliner inliner = context.getInliner();

        if (inliner == null || inliner == NoOpInliner.INSTANCE) {
            return;
        }

        final CharSequence inlined = inliner.inline(context, text);
        if (inlined != null && inlined != text) {
            structureHandler.setText(inlined);
        }

    }

}
