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
package org.thymeleaf.processor.templateboundaries;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.ITemplateEnd;
import org.thymeleaf.model.ITemplateStart;
import org.thymeleaf.processor.IProcessor;

/**
 * <p>
 *   Base interface for all processors that execute on template boundaries (i.e. <em>template start</em> and
 *   <em>template end</em> events).
 * </p>
 * <p>
 *   Template Boundaries Processors are a kind of processors that execute on the <em>template start</em> and
 *   <em>template end</em> events fired during template processing. They allow to perform any kind of
 *   initialization or disposal of resources at beginning or end of the template processing operation. Note
 *   that these events are only fired for the first-level template, and not for each of the fragments
 *   that might be parsed and/or included into the template being processed.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @see AbstractTemplateBoundariesProcessor
 * @see ITemplateBoundariesStructureHandler
 * @since 3.0.0
 * 
 */
public interface ITemplateBoundariesProcessor extends IProcessor {

    /**
     * <p>
     *   Execute the processor for a {@link ITemplateStart} event.
     * </p>
     * <p>
     *   The {@link ITemplateStart} object argument is immutable, so all modifications to this object or any
     *   instructions to be given to the engine should be done through the specified
     *   {@link ITemplateBoundariesStructureHandler} handler.
     * </p>
     *
     * @param context the execution context.
     * @param templateStart the event this processor is executing on.
     * @param structureHandler the handler that will centralise modifications and commands to the engine.
     */
    public void processTemplateStart(
            final ITemplateContext context,
            final ITemplateStart templateStart, final ITemplateBoundariesStructureHandler structureHandler);

    /**
     * <p>
     *   Execute the processor for a {@link ITemplateEnd} event.
     * </p>
     * <p>
     *   The {@link ITemplateEnd} object argument is immutable, so all modifications to this object or any
     *   instructions to be given to the engine should be done through the specified
     *   {@link ITemplateBoundariesStructureHandler} handler.
     * </p>
     *
     * @param context the execution context.
     * @param templateEnd the event this processor is executing on.
     * @param structureHandler the handler that will centralise modifications and commands to the engine.
     */
    public void processTemplateEnd(
            final ITemplateContext context,
            final ITemplateEnd templateEnd, final ITemplateBoundariesStructureHandler structureHandler);

}
