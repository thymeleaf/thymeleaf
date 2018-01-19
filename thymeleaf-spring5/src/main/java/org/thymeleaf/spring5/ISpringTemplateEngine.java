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
package org.thymeleaf.spring5;

import org.springframework.context.MessageSource;
import org.thymeleaf.ITemplateEngine;


/**
 * <p>
 *   Sub-interface of {@link ITemplateEngine} meant for Spring applications, meant to be
 *   using the {@link org.thymeleaf.spring5.dialect.SpringStandardDialect} and integrating with other
 *   Spring-bound infrastructure.
 * </p>
 * <p>
 *   The {@link SpringTemplateEngine} implementation of this interface (or a subclass) should be used
 *   in almost every case, but this interface improves testability of these artifacts.
 * </p>
 *
 * @see SpringTemplateEngine
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.3
 *
 */
public interface ISpringTemplateEngine extends ITemplateEngine {


    /**
     * <p>
     *   Sets the Spring {@link MessageSource} to be used for this template engine.
     * </p>
     * <p>
     *   Note that the {@link SpringTemplateEngine} implementation will allow this to be set
     *   automatically by implementing the {@link org.springframework.context.MessageSourceAware}
     *   interface, but in such case this method will allow to override this mechanism if needed.
     * </p>
     *
     * @param templateEngineMessageSource the message source to be used by the message resolver.
     */
    public void setTemplateEngineMessageSource(final MessageSource templateEngineMessageSource);


}
