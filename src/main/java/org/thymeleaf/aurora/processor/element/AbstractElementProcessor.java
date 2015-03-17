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
package org.thymeleaf.aurora.processor.element;

import org.thymeleaf.aurora.context.ITemplateProcessingContext;
import org.thymeleaf.aurora.dialect.IDialect;
import org.thymeleaf.aurora.engine.AttributeName;
import org.thymeleaf.aurora.engine.AttributeNames;
import org.thymeleaf.aurora.engine.ElementName;
import org.thymeleaf.aurora.engine.ElementNames;
import org.thymeleaf.aurora.engine.ICloseElementTag;
import org.thymeleaf.aurora.engine.ICloseElementTagActionHandler;
import org.thymeleaf.aurora.engine.IOpenElementTag;
import org.thymeleaf.aurora.engine.IOpenElementTagActionHandler;
import org.thymeleaf.aurora.engine.IStandaloneElementTag;
import org.thymeleaf.aurora.engine.IStandaloneElementTagActionHandler;
import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractElementProcessor implements IElementProcessor {

    private final int precedence;
    private final TemplateMode templateMode;
    private final String elementName;
    private final String attributeName;


    private String dialectPrefix = null;
    private IDialect dialect = null;
    private ElementName matchingElementName = null;
    private AttributeName matchingAttributeName = null;



    public AbstractElementProcessor(
            final TemplateMode templateMode,
            final String elementName, final String attributeName,
            final int precedence) {

        super();

        Validate.notNull(templateMode, "Template mode cannot be null");

        this.templateMode = templateMode;
        this.precedence = precedence;
        this.elementName = elementName;
        this.attributeName = attributeName;

    }


    public final ElementName getMatchingElementName() {
        return this.matchingElementName;
    }


    public final AttributeName getMatchingAttributeName() {
        return this.matchingAttributeName;
    }


    public final TemplateMode getTemplateMode() {
        return this.templateMode;
    }


    public final int getPrecedence() {
        return this.precedence;
    }


    public final void setDialect(final IDialect dialect) {
        this.dialect = dialect;
    }


    public final void setDialectPrefix(final String dialectPrefix) {
        this.dialectPrefix = dialectPrefix;
        if (this.elementName != null) {
            this.matchingElementName = ElementNames.forName(this.templateMode, this.dialectPrefix, this.elementName);
        }
        if (this.attributeName != null) {
            this.matchingAttributeName = AttributeNames.forName(this.templateMode, this.dialectPrefix, this.attributeName);
        }
    }


    public final String getDialectPrefix() {
        return dialectPrefix;
    }


    public final IDialect getDialect() {
        return dialect;
    }


    // Default implementation - meant to be overridden by subclasses if needed
    public IStandaloneElementTag processStandaloneElementTag(
            final ITemplateProcessingContext processingContext,
            final IStandaloneElementTag standaloneElementTag,
            final IStandaloneElementTagActionHandler actionHandler) {
        return standaloneElementTag;
    }


    // Default implementation - meant to be overridden by subclasses if needed
    public IOpenElementTag processOpenElementTag(
            final ITemplateProcessingContext processingContext,
            final IOpenElementTag openElementTag,
            final IOpenElementTagActionHandler actionHandler) {
        return openElementTag;
    }


    // Default implementation - meant to be overridden by subclasses if needed
    public ICloseElementTag processCloseElementTag(
            final ITemplateProcessingContext processingContext,
            final ICloseElementTag closeElementTag,
            final ICloseElementTagActionHandler actionHandler) {
        return closeElementTag;
    }

}
