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

import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Basic implementation of {@link IPreProcessor}.
 * </p>
 * <p>
 *   This implementation will suffice for most of the scenarios in which specifying a pre-processor at
 *   a dialect is needed.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class PreProcessor implements IPreProcessor {

    private final TemplateMode templateMode;
    private final Class<? extends ITemplateHandler> handlerClass;
    private final int precedence;



    public PreProcessor(
            final TemplateMode templateMode, final Class<? extends ITemplateHandler> handlerClass, final int precedence) {

        super();

        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.notNull(handlerClass, "Handler class cannot be null");

        this.templateMode = templateMode;
        this.handlerClass = handlerClass;
        this.precedence = precedence;

    }


    public final TemplateMode getTemplateMode() {
        return this.templateMode;
    }


    public final int getPrecedence() {
        return this.precedence;
    }


    public final Class<? extends ITemplateHandler> getHandlerClass() {
        return this.handlerClass;
    }

}
