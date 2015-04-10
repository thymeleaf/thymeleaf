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
package org.thymeleaf.aurora.processor.doctype;

import org.thymeleaf.aurora.context.ITemplateProcessingContext;
import org.thymeleaf.aurora.model.IDocType;
import org.thymeleaf.aurora.processor.AbstractProcessor;
import org.thymeleaf.aurora.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractDocTypeProcessor
        extends AbstractProcessor implements IDocTypeProcessor {



    public AbstractDocTypeProcessor(final TemplateMode templateMode, final int precedence) {
        super(templateMode, precedence);
    }


    // Default implementation - meant to be overridden by subclasses if needed
    public IDocType process(final ITemplateProcessingContext processingContext, final IDocType docType) {
        return docType;
    }


}
