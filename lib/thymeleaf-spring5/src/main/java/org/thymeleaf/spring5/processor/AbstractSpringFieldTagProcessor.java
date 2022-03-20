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
package org.thymeleaf.spring5.processor;

import org.springframework.util.StringUtils;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;
import org.thymeleaf.spring5.util.FieldUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 * Binds an input property with the value in the form's backing bean.
 * <p>
 * Values for {@code th:field} attributes must be selection expressions
 * {@code (*{...})}, as they will be evaluated on the form backing bean and not
 * on the context variables (model attributes in Spring MVC jargon).
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.3
 */
public abstract class AbstractSpringFieldTagProcessor
        extends AbstractAttributeTagProcessor
        implements IAttributeDefinitionsAware {


    public static final int ATTR_PRECEDENCE = 1700;
    public static final String ATTR_NAME = "field";

    private static final TemplateMode TEMPLATE_MODE = TemplateMode.HTML;

    protected static final String INPUT_TAG_NAME = "input";
    protected static final String SELECT_TAG_NAME = "select";
    protected static final String OPTION_TAG_NAME = "option";
    protected static final String TEXTAREA_TAG_NAME = "textarea";

    protected static final String ID_ATTR_NAME = "id";
    protected static final String TYPE_ATTR_NAME = "type";
    protected static final String NAME_ATTR_NAME = "name";
    protected static final String VALUE_ATTR_NAME = "value";
    protected static final String CHECKED_ATTR_NAME = "checked";
    protected static final String SELECTED_ATTR_NAME = "selected";
    protected static final String DISABLED_ATTR_NAME = "disabled";
    protected static final String MULTIPLE_ATTR_NAME = "multiple";

    private AttributeDefinition discriminatorAttributeDefinition;
    protected AttributeDefinition idAttributeDefinition;
    protected AttributeDefinition typeAttributeDefinition;
    protected AttributeDefinition nameAttributeDefinition;
    protected AttributeDefinition valueAttributeDefinition;
    protected AttributeDefinition checkedAttributeDefinition;
    protected AttributeDefinition selectedAttributeDefinition;
    protected AttributeDefinition disabledAttributeDefinition;
    protected AttributeDefinition multipleAttributeDefinition;





    private final String discriminatorAttrName;
    private final String[] discriminatorAttrValues;
    private final boolean removeAttribute;


    public AbstractSpringFieldTagProcessor(
            final String dialectPrefix, final String elementName,
            final String discriminatorAttrName, final String[] discriminatorAttrValues,
            final boolean removeAttribute) {
        super(TEMPLATE_MODE, dialectPrefix, elementName, false, ATTR_NAME, true, ATTR_PRECEDENCE, false);
        this.discriminatorAttrName = discriminatorAttrName;
        this.discriminatorAttrValues = discriminatorAttrValues;
        this.removeAttribute = removeAttribute;
    }




    public void setAttributeDefinitions(final AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        // We precompute the AttributeDefinitions in order to being able to use much
        // faster methods for setting/replacing attributes on the ElementAttributes implementation
        this.discriminatorAttributeDefinition =
                (this.discriminatorAttrName != null? attributeDefinitions.forName(TEMPLATE_MODE, this.discriminatorAttrName) : null);
        this.idAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, ID_ATTR_NAME);
        this.typeAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, TYPE_ATTR_NAME);
        this.nameAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, NAME_ATTR_NAME);
        this.valueAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, VALUE_ATTR_NAME);
        this.checkedAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, CHECKED_ATTR_NAME);
        this.selectedAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, SELECTED_ATTR_NAME);
        this.disabledAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, DISABLED_ATTR_NAME);
        this.multipleAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, MULTIPLE_ATTR_NAME);
    }




    private boolean matchesDiscriminator(final IProcessableElementTag tag) {

        if (this.discriminatorAttrName == null) {
            return true;
        }
        final boolean hasDiscriminatorAttr = tag.hasAttribute(this.discriminatorAttributeDefinition.getAttributeName());
        if (this.discriminatorAttrValues == null || this.discriminatorAttrValues.length == 0) {
            return hasDiscriminatorAttr;
        }
        final String discriminatorTagValue =
                (hasDiscriminatorAttr? tag.getAttributeValue(this.discriminatorAttributeDefinition.getAttributeName()) : null);
        for (int i = 0; i < this.discriminatorAttrValues.length; i++) {
            final String discriminatorAttrValue = this.discriminatorAttrValues[i];
            if (discriminatorAttrValue == null) {
                if (!hasDiscriminatorAttr || discriminatorTagValue == null) {
                    return true;
                }
            } else if (discriminatorAttrValue.equals(discriminatorTagValue)) {
                return true;
            }
        }
        return false;

    }



    @Override
    protected void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IElementTagStructureHandler structureHandler) {

        /*
         * First thing to check is whether this processor really matches, because so far we have asked the engine only
         * to match per attribute (th:field) and host tag (input, select, option...) but we still don't know if the
         * match is complete because we might still need to assess for example that the 'type' attribute has the
         * correct value. For example, the same processor will not be executing on <input type="text" th:field="*{a}"/>
         * and on <input type="checkbox" th:field="*{a}"/>
         */
        if (!matchesDiscriminator(tag)) {
            // Note in this case we do not have to remove the th:field attribute because the correct processor is still
            // to be executed!
            return;
        }

        if (this.removeAttribute) {
            structureHandler.removeAttribute(attributeName);
        }

        final IThymeleafBindStatus bindStatus = FieldUtils.getBindStatus(context, attributeValue);

        if (bindStatus == null) {
            throw new TemplateProcessingException(
                    "Cannot process attribute '" + attributeName + "': no associated BindStatus could be found for " +
                    "the intended form binding operations. This can be due to the lack of a proper management of the " +
                    "Spring RequestContext, which is usually done through the ThymeleafView or ThymeleafReactiveView");
        }

        // We set the BindStatus into a local variable just in case we have more BindStatus-related processors to
        // be applied for the same tag, like for example a th:errorclass
        structureHandler.setLocalVariable(SpringContextVariableNames.THYMELEAF_FIELD_BIND_STATUS, bindStatus);

        doProcess(context, tag, attributeName, attributeValue, bindStatus, structureHandler);

    }




    protected abstract void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName,
            final String attributeValue,
            final IThymeleafBindStatus bindStatus,
            final IElementTagStructureHandler structureHandler);





    // This method is designed to be called from the diverse subclasses
    protected final String computeId(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final String name, final boolean sequence) {

        String id = tag.getAttributeValue(this.idAttributeDefinition.getAttributeName());
        if (!org.thymeleaf.util.StringUtils.isEmptyOrWhitespace(id)) {
            return (StringUtils.hasText(id) ? id : null);
        }

        id = FieldUtils.idFromName(name);
        if (sequence) {
            final Integer count = context.getIdentifierSequences().getAndIncrementIDSeq(id);
            return id + count.toString();
        }
        return id;

    }




}
