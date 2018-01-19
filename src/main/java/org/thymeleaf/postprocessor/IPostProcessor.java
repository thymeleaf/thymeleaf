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
package org.thymeleaf.postprocessor;

import org.thymeleaf.dialect.IPostProcessorDialect;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * <p>
 *   Interface defining post-processors.
 * </p>
 * <p>
 *   Post-processors are implementations of {@link ITemplateHandler} meant to be executed
 *   on template model events <em>after</em> these events have gone through processing by all the applicable
 *   processors (implementations of {@link org.thymeleaf.processor.IProcessor}).
 * </p>
 * <p>
 *   Post-processors can be used to re-shape the template processing output just before output is really produced.
 * </p>
 * <p>
 *   Most of the times, the {@link PostProcessor} implementation will be used for registering post-processors.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public interface IPostProcessor {

    /**
     * <p>
     *   Returns the template mode this post-processor should be executed for. A post-processor can only be linked
     *   to a specific template mode.
     * </p>
     *
     * @return the template mode.
     */
    public TemplateMode getTemplateMode();

    /**
     * <p>
     *   Returns the precedence that should be applied to this post-processor. This will determine the order in which
     *   it will be executed in relation to any other post-processors (note that the dialect precedence determined
     *   by {@link IPostProcessorDialect#getDialectPostProcessorPrecedence()} will be applied first).
     * </p>
     *
     * @return the post-processor precedence.
     */
    public int getPrecedence();

    /**
     * <p>
     *   Returns the handler class for this post-processor, the {@link ITemplateHandler} that implements the
     *   real logic to be executed.
     * </p>
     * <p>
     *   In order for post-processors to work correctly, they need to implement correctly all the
     *   {@link ITemplateHandler} contract. In order to make this easier, extending
     *   {@link org.thymeleaf.engine.AbstractTemplateHandler} is recommended.
     * </p>
     *
     * @return the handler class.
     */
    public Class<? extends ITemplateHandler> getHandlerClass();

}
