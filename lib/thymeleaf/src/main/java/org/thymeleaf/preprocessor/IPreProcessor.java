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
package org.thymeleaf.preprocessor;

import org.thymeleaf.dialect.IPreProcessorDialect;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * <p>
 *   Interface defining pre-processors.
 * </p>
 * <p>
 *   Pre-processors are implementations of {@link ITemplateHandler} meant to be executed
 *   on template model events after parsing (or retrieving from cache) and <em>before</em> these events go
 *   through processing by all the applicable processors (implementations of
 *   {@link org.thymeleaf.processor.IProcessor}).
 * </p>
 * <p>
 *   Pre-processors can be used to re-shape the template model just before it is processed.
 * </p>
 * <p>
 *   Most of the times, the {@link PreProcessor} implementation will be used for registering pre-processors.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public interface IPreProcessor {

    /**
     * <p>
     *   Returns the template mode this pre-processor should be executed for. A pre-processor can only be linked
     *   to a specific template mode.
     * </p>
     *
     * @return the template mode.
     */
    public TemplateMode getTemplateMode();

    /**
     * <p>
     *   Returns the precedence that should be applied to this pre-processor. This will determine the order in which
     *   it will be executed in relation to any other pre-processors (note that the dialect precedence determined
     *   by {@link IPreProcessorDialect#getDialectPreProcessorPrecedence()} will be applied first).
     * </p>
     *
     * @return the pre-processor precedence.
     */
    public int getPrecedence();

    /**
     * <p>
     *   Returns the handler class for this pre-processor, the {@link ITemplateHandler} that implements the
     *   real logic to be executed.
     * </p>
     * <p>
     *   In order for pre-processors to work correctly, they need to implement correctly all the
     *   {@link ITemplateHandler} contract. In order to make this easier, extending
     *   {@link org.thymeleaf.engine.AbstractTemplateHandler} is recommended.
     * </p>
     *
     * @return the handler class.
     */
    public Class<? extends ITemplateHandler> getHandlerClass();

}
