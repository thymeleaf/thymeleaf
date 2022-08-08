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
import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.element.AbstractElementModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

public class NoOp2ModelProcessor extends AbstractElementModelProcessor {

    private static final int PRECEDENCE = 1100;


    public NoOp2ModelProcessor(final String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, "noop", true, null, false, PRECEDENCE);
    }


    @Override
    protected void doProcess(final ITemplateContext context, final IModel model, final IElementModelStructureHandler structureHandler) {
        // Nothing to do, that's the idea. Neither to do anything, nor to change the tag in any way
        final Boolean var = (Boolean) context.getVariable("noop-model");
        if (var == null || !var.booleanValue()) {
            throw new RuntimeException("Local variable has not reached from one no-op model operator to the next one");
        }
    }

}
