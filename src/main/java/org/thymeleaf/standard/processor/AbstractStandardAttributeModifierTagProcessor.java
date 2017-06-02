/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractStandardAttributeModifierTagProcessor
            extends AbstractStandardExpressionAttributeTagProcessor
            implements IAttributeDefinitionsAware {


    private final boolean removeIfEmpty;
    private final String targetAttrCompleteName;

    private AttributeDefinition targetAttributeDefinition;




    protected AbstractStandardAttributeModifierTagProcessor(
            final TemplateMode templateMode,
            final String dialectPrefix, final String attrName,
            final int precedence, final boolean removeIfEmpty) {
        this(templateMode, dialectPrefix, attrName, attrName, precedence, removeIfEmpty);
    }


    protected AbstractStandardAttributeModifierTagProcessor(
            final TemplateMode templateMode, final String dialectPrefix,
            final String attrName, final String targetAttrCompleteName,
            final int precedence, final boolean removeIfEmpty) {

        super(templateMode, dialectPrefix, attrName, precedence, false);

        Validate.notNull(targetAttrCompleteName, "Complete name of target attribute cannot be null");

        this.targetAttrCompleteName = targetAttrCompleteName;
        this.removeIfEmpty = removeIfEmpty;

    }




    public void setAttributeDefinitions(final AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        // We precompute the AttributeDefinition of the target attribute in order to being able to use much
        // faster methods for setting/replacing attributes on the ElementAttributes implementation
        this.targetAttributeDefinition = attributeDefinitions.forName(getTemplateMode(), this.targetAttrCompleteName);
    }




    @Override
    protected final void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final Object expressionResult,
            final IElementTagStructureHandler structureHandler) {

        final String newAttributeValue =
                EscapedAttributeUtils.escapeAttribute(getTemplateMode(), expressionResult == null ? null : expressionResult.toString());

        // These attributes might be "removable if empty", in which case we would simply remove the target attribute...
        if (this.removeIfEmpty && (newAttributeValue == null || newAttributeValue.length() == 0)) {

            // We are removing the equivalent attribute name, without the prefix...
            structureHandler.removeAttribute(this.targetAttributeDefinition.getAttributeName());
            structureHandler.removeAttribute(attributeName);

        } else {

            // We are setting the equivalent attribute name, without the prefix...
            StandardProcessorUtils.replaceAttribute(
                    structureHandler, attributeName, this.targetAttributeDefinition, this.targetAttrCompleteName, (newAttributeValue == null ? "" : newAttributeValue));

        }

    }


}
