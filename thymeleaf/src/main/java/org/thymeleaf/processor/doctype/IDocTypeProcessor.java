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
package org.thymeleaf.processor.doctype;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.processor.IProcessor;

/**
 * <p>
 *   Base interface for all processors that execute on DOCTYPE events ({@link IDocType}).
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @see AbstractDocTypeProcessor
 * @see IDocTypeStructureHandler
 * @since 3.0.0
 * 
 */
public interface IDocTypeProcessor extends IProcessor {

    /**
     * <p>
     *   Execute the processor.
     * </p>
     * <p>
     *   The {@link IDocType} object argument is immutable, so all modifications to this object or any
     *   instructions to be given to the engine should be done through the specified
     *   {@link IDocTypeStructureHandler} handler.
     * </p>
     *
     * @param context the execution context.
     * @param docType the event this processor is executing on.
     * @param structureHandler the handler that will centralise modifications and commands to the engine.
     */
    public void process(
            final ITemplateContext context,
            final IDocType docType, final IDocTypeStructureHandler structureHandler);

}
