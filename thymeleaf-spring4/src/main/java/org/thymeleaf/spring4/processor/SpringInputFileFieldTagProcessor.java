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

import org.springframework.web.servlet.support.BindStatus;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class SpringInputFileFieldTagProcessor extends AbstractSpringFieldTagProcessor {

    
    public static final String FILE_INPUT_TYPE_ATTR_VALUE = "file";
    


    
    public SpringInputFileFieldTagProcessor(final String dialectPrefix) {
        super(dialectPrefix, INPUT_TAG_NAME, INPUT_TYPE_ATTR_NAME, new String[] { FILE_INPUT_TYPE_ATTR_VALUE }, true);
    }




    @Override
    protected void doProcess(final ITemplateProcessingContext processingContext, final IProcessableElementTag tag,
                             final AttributeName attributeName, final String attributeValue,
                             final String attributeTemplateName, final int attributeLine, final int attributeCol,
                             final BindStatus bindStatus, final IElementTagStructureHandler structureHandler) {

        String name = bindStatus.getExpression();
        name = (name == null? "" : name);

        final String id = computeId(processingContext, tag, name, false);

        tag.getAttributes().setAttribute("id", id); // No need to escape: this comes from an existing 'id' or from a token
        tag.getAttributes().setAttribute("name", name); // No need to escape: this is a java-valid token

    }

    

}
