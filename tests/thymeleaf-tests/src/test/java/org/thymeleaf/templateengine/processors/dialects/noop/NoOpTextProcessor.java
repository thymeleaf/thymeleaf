/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.templateengine.processors.dialects.noop;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IText;
import org.thymeleaf.processor.text.AbstractTextProcessor;
import org.thymeleaf.processor.text.ITextStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

public class NoOpTextProcessor extends AbstractTextProcessor {

    private static final int PRECEDENCE = 1100;


    public NoOpTextProcessor() {
        super(TemplateMode.HTML, PRECEDENCE);
    }

    @Override
    protected void doProcess(final ITemplateContext context, final IText text, final ITextStructureHandler structureHandler) {
        if (text.getText().equals("...")) {
            return;
        }
        final Boolean var = (Boolean) (context.containsVariable("noop-tag")? context.getVariable("noop-tag") : context.getVariable("noop-model"));
        if (var == null || !var.booleanValue()) {
            throw new RuntimeException("Local variable has not reached from one no-op operator to the body text");
        }
        structureHandler.setText("processed!");
    }
}
