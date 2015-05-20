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
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.spring4.naming.SpringContextVariableNames;
import org.thymeleaf.spring4.util.FieldUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.html.HtmlEscape;

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
public abstract class AbstractSpringFieldTagProcessor extends AbstractElementTagProcessor {

    
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
            final String elementName, final String discriminatorAttrName, final String[] discriminatorAttrValues) {
        super(TemplateMode.HTML, elementName, false, ATTR_NAME, true, ATTR_PRECEDENCE);
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
    protected void doProcess(final ITemplateProcessingContext processingContext, final IProcessableElementTag tag,
                             final IElementStructureHandler structureHandler) {

        /*
         * First thing to check is whether this processor really matches, because so far we have asked the engine only
         * to match per attribute (th:field) and host tag (input, select, option...) but we still don't know if the
         * match is complete because we might still need to assess for example that the 'type' attribute has the
         * correct value. For example, the same processor will not be executing on <input type="text" th:field="*{a}"/>
         * and on <input type="checkbox" th:field="*{a}"/>
         */
        if (!matchesDiscriminator(tag)) {
            return;
        }

        AttributeName attributeName = null;
        try {

            attributeName = getMatchingAttributeName().getMatchingAttributeName();
            final String attributeValue = HtmlEscape.unescapeHtml(tag.getAttributes().getValue(attributeName));

            final BindStatus bindStatus = FieldUtils.getBindStatus(processingContext, attributeValue);

            doProcess(processingContext, tag, attributeName, attributeValue, bindStatus, structureHandler);

            structureHandler.setLocalVariable(SpringContextVariableNames.SPRING_FIELD_BIND_STATUS, bindStatus);

            tag.getAttributes().removeAttribute(attributeName);

        } catch (final TemplateProcessingException e) {
            // This is a nice moment to check whether the execution raised an error and, if so, add location information
            if (!e.hasTemplateName()) {
                e.setTemplateName(tag.getTemplateName());
            }
            if (!e.hasLineAndCol()) {
                if (attributeName != null) {
                    final int line = tag.getAttributes().getLine(attributeName);
                    final int col = tag.getAttributes().getCol(attributeName);
                    e.setLineAndCol(line, col);
                } else {
                    // We don't have info about the specific attribute provoking the error
                    e.setLineAndCol(tag.getLine(), tag.getCol());
                }
            }
            throw e;
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error during execution of processor '" + this.getClass().getName() + "'",
                    tag.getTemplateName(), tag.getLine(), tag.getCol(), e);
        }

    }




    protected abstract void doProcess(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final AttributeName attributeName,
            final String attributeValue,
            final BindStatus bindStatus,
            final IElementStructureHandler structureHandler);


    
    
    
    // This method is designed to be overridable
    protected String computeId(
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
