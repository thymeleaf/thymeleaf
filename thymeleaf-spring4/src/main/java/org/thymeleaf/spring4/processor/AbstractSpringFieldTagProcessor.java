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
package org.thymeleaf.spring4.processor;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.BindStatus;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IElementStructureHandler;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.spring4.naming.SpringContextVariableNames;
import org.thymeleaf.spring4.util.FieldUtils;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Binds an input property with the value in the form's backing bean.
 * <p>
 * Values for <tt>th:field</tt> attributes must be selection expressions
 * <tt>(*{...})</tt>, as they will be evaluated on the form backing bean and not
 * on the context variables (model attributes in Spring MVC jargon).
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 */
public abstract class AbstractSpringFieldTagProcessor extends AbstractAttributeTagProcessor {

    
    public static final int ATTR_PRECEDENCE = 1200;
    public static final String ATTR_NAME = "field";
    
    
    protected static final String INPUT_TAG_NAME = "input";
    protected static final String SELECT_TAG_NAME = "select";
    protected static final String OPTION_TAG_NAME = "option";
    protected static final String TEXTAREA_TAG_NAME = "textarea";
    
    protected static final String INPUT_TYPE_ATTR_NAME = "type";


    private final String discriminatorAttrName;
    private final String[] discriminatorAttrValues;


    public AbstractSpringFieldTagProcessor(
            final String dialectPrefix, final String elementName, final String discriminatorAttrName, final String[] discriminatorAttrValues) {
        super(TemplateMode.HTML, dialectPrefix, elementName, false, ATTR_NAME, true, ATTR_PRECEDENCE);
        this.discriminatorAttrName = discriminatorAttrName;
        this.discriminatorAttrValues = discriminatorAttrValues;
    }



    private boolean matchesDiscriminator(final IProcessableElementTag tag) {

        if (this.discriminatorAttrName == null) {
            return true;
        }
        if (this.discriminatorAttrValues == null || this.discriminatorAttrValues.length == 0) {
            return tag.getAttributes().hasAttribute(this.discriminatorAttrName);
        }
        final String discriminatorTagValue = tag.getAttributes().getValue(this.discriminatorAttrName);
        for (int i = 0; i < this.discriminatorAttrValues.length; i++) {
            if (this.discriminatorAttrValues[i].equals(discriminatorTagValue)) {
                return true;
            }
        }
        return false;

    }



    @Override
    protected void doProcess(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IElementStructureHandler structureHandler) {

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

        final BindStatus bindStatus = FieldUtils.getBindStatus(processingContext, attributeValue);
        structureHandler.setLocalVariable(SpringContextVariableNames.SPRING_FIELD_BIND_STATUS, bindStatus);

        doProcess(processingContext, tag, attributeName, attributeValue, bindStatus, structureHandler);

    }




    protected abstract void doProcess(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final AttributeName attributeName,
            final String attributeValue,
            final BindStatus bindStatus,
            final IElementStructureHandler structureHandler);


    
    
    
    // This method is designed to be called from the diverse subclasses
    protected final String computeId(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final String name, final boolean sequence) {
        
        String id = tag.getAttributes().getValue("id");
        if (!org.thymeleaf.util.StringUtils.isEmptyOrWhitespace(id)) {
            return (StringUtils.hasText(id) ? id : null);
        }

        id = FieldUtils.idFromName(name);
        if (sequence) {
            final Integer count = processingContext.getIdentifierSequences().getAndIncrementIDSeq(id);
            return id + count.toString();
        }
        return id;
        
    }
    

    

}
