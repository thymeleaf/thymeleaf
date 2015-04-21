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
package org.thymeleaf.aurora.processor;

import org.thymeleaf.aurora.dialect.IDialect;
import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.0.0 (reimplemented in 3.0.0)
 *
 */
public abstract class AbstractProcessor implements IProcessor {

    private final int precedence;
    private final TemplateMode templateMode;


    private String dialectPrefix = null;
    private IDialect dialect = null;


    // TODO Add the "getMessage()" methods in the old AbstractProcessor that allow processors to easily access the i18n infrastructure... or maybe they should go to the processing context?


    public AbstractProcessor(final TemplateMode templateMode, final int precedence) {

        super();

        Validate.notNull(templateMode, "Template mode cannot be null");

        this.templateMode = templateMode;
        this.precedence = precedence;

    }


    public TemplateMode getTemplateMode() {
        return this.templateMode;
    }


    public int getPrecedence() {
        return this.precedence;
    }


    public void setDialect(final IDialect dialect) {
        this.dialect = dialect;
    }


    public void setDialectPrefix(final String dialectPrefix) {
        this.dialectPrefix = dialectPrefix;
    }


    public String getDialectPrefix() {
        return dialectPrefix;
    }


    public IDialect getDialect() {
        return dialect;
    }


}
