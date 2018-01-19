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
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.ITemplateEnd;
import org.thymeleaf.model.ITemplateStart;
import org.thymeleaf.processor.templateboundaries.AbstractTemplateBoundariesProcessor;
import org.thymeleaf.processor.templateboundaries.ITemplateBoundariesStructureHandler;
import org.thymeleaf.standard.inline.StandardCSSInliner;
import org.thymeleaf.standard.inline.StandardHTMLInliner;
import org.thymeleaf.standard.inline.StandardJavaScriptInliner;
import org.thymeleaf.standard.inline.StandardTextInliner;
import org.thymeleaf.standard.inline.StandardXMLInliner;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardInlineEnablementTemplateBoundariesProcessor extends AbstractTemplateBoundariesProcessor {

    public static final int PRECEDENCE = 10;

    public StandardInlineEnablementTemplateBoundariesProcessor(final TemplateMode templateMode) {
        super(templateMode, PRECEDENCE);
    }


    @Override
    public void doProcessTemplateStart(
            final ITemplateContext context,
            final ITemplateStart templateStart, final ITemplateBoundariesStructureHandler structureHandler) {

        switch (getTemplateMode()) {

            case HTML:
                structureHandler.setInliner(new StandardHTMLInliner(context.getConfiguration()));
                break;
            case XML:
                structureHandler.setInliner(new StandardXMLInliner(context.getConfiguration()));
                break;
            case TEXT:
                structureHandler.setInliner(new StandardTextInliner(context.getConfiguration()));
                break;
            case JAVASCRIPT:
                structureHandler.setInliner(new StandardJavaScriptInliner(context.getConfiguration()));
                break;
            case CSS:
                structureHandler.setInliner(new StandardCSSInliner(context.getConfiguration()));
                break;
            case RAW:
                // No inliner for RAW template mode. We could use the Raw, but anyway it would be of no use
                // because in RAW mode the text processor that looks for the inliner to apply does not exist...
                structureHandler.setInliner(null);
                break;
            default:
                throw new TemplateProcessingException(
                        "Unrecognized template mode: " + getTemplateMode() + ", cannot initialize inlining!");

        }

    }


    @Override
    public void doProcessTemplateEnd(
            final ITemplateContext context,
            final ITemplateEnd templateEnd, final ITemplateBoundariesStructureHandler structureHandler) {

        // Empty - nothing to be done on template end

    }

}
