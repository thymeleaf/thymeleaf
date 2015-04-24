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
import org.thymeleaf.engine.ITextStructureHandler;
import org.thymeleaf.model.IText;
import org.thymeleaf.processor.text.AbstractTextProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardInliningTextProcessor extends AbstractTextProcessor {


    public StandardInliningTextProcessor() {
        super(TemplateMode.HTML, 1000);
    }


    @Override
    public void process(final ITemplateProcessingContext processingContext, final IText text,
                        final ITextStructureHandler structureHandler) {

        boolean candidate = false;
        int n = text.length();
        while (n-- != 0) {
            if (text.charAt(n) == '}') {
//                System.out.println("Found a text that can be an INLINING CANDIDATE:\n---\n" + text.getText() + "\n---");
                candidate = true;
            }
        }

        if (candidate) {
            if (1 == 0) {
                System.out.println("WAT");
            }
        }

    }

}
