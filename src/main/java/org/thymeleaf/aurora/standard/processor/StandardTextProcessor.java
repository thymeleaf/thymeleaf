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
package org.thymeleaf.aurora.standard.processor;

import org.thymeleaf.aurora.context.ITemplateProcessingContext;
import org.thymeleaf.aurora.dialect.IDialect;
import org.thymeleaf.aurora.engine.AttributeName;
import org.thymeleaf.aurora.engine.AttributeNames;
import org.thymeleaf.aurora.engine.ElementName;
import org.thymeleaf.aurora.engine.ICloseElementTag;
import org.thymeleaf.aurora.engine.ICloseElementTagActionHandler;
import org.thymeleaf.aurora.engine.IOpenElementTag;
import org.thymeleaf.aurora.engine.IOpenElementTagActionHandler;
import org.thymeleaf.aurora.engine.IStandaloneElementTag;
import org.thymeleaf.aurora.engine.IStandaloneElementTagActionHandler;
import org.thymeleaf.aurora.processor.element.IElementProcessor;
import org.thymeleaf.aurora.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class StandardTextProcessor implements IElementProcessor {

    private TemplateMode templateMode = null;
    private String dialectPrefix = null;
    private IDialect dialect = null;

    public StandardTextProcessor() {
        super();
        this.templateMode = TemplateMode.HTML;
    }

    public ElementName getMatchingElementName() {
        return null;
    }

    public AttributeName getMatchingAttributeName() {
        return AttributeNames.forName(this.templateMode, this.dialectPrefix, "text");
    }

    public IStandaloneElementTag processStandaloneElementTag(final ITemplateProcessingContext processingContext, final IStandaloneElementTag standaloneElementTag, final IStandaloneElementTagActionHandler actionHandler) {
        return null;
    }

    public IOpenElementTag processOpenElementTag(final ITemplateProcessingContext processingContext, final IOpenElementTag openElementTag, final IOpenElementTagActionHandler actionHandler) {
        return null;
    }

    public ICloseElementTag processCloseElementTag(final ITemplateProcessingContext processingContext, final ICloseElementTag closeElementTag, final ICloseElementTagActionHandler actionHandler) {
        return null;
    }

    public void setDialect(final IDialect dialect) {
        this.dialect = dialect;
    }

    public void setDialectPrefix(final String dialectPrefix) {
        this.dialectPrefix = dialectPrefix;
    }

    public TemplateMode getTemplateMode() {
        return this.templateMode;
    }

    public int getPrecedence() {
        return 100;
    }
}
