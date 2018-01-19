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
package org.thymeleaf.processor;

import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Base class for all processors (objects implementing the {@link IProcessor} interface).
 * </p>
 * <p>
 *   Note a class with this name existed since 2.0.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractProcessor implements IProcessor {

    private final int precedence;
    private final TemplateMode templateMode;



    public AbstractProcessor(final TemplateMode templateMode, final int precedence) {

        super();

        Validate.notNull(templateMode, "Template mode cannot be null");

        this.templateMode = templateMode;
        this.precedence = precedence;

    }


    public final TemplateMode getTemplateMode() {
        return this.templateMode;
    }


    public final int getPrecedence() {
        return this.precedence;
    }


}
