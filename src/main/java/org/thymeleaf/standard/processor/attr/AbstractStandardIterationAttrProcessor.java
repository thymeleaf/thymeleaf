/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.standard.processor.attr;

import org.thymeleaf.Arguments;
import org.thymeleaf.processor.attr.AbstractIterationAttrProcessor;
import org.thymeleaf.standard.expression.Each;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractStandardIterationAttrProcessor 
        extends AbstractIterationAttrProcessor {

    
    

    
    
    public AbstractStandardIterationAttrProcessor() {
        super();
    }






    @Override
    protected final IterationSpec getIterationSpec(
            final Arguments arguments, final TemplateResolution templateResolution,
            final Document document, final Element element, final Attr attribute,
            final String attributeName, final String attributeValue) {

        
        final Each each = StandardExpressionProcessor.parseEach(arguments, templateResolution, attributeValue);

        final Expression iterableExpression = each.getIterable();
        
        final Object iteratedObject = StandardExpressionProcessor.executeExpression(arguments, templateResolution, iterableExpression);
        
        if (each.hasStatusVar()) {
            return new IterationSpec(each.getIterVar().getValue(), each.getStatusVar().getValue(), iteratedObject);
        }
        return new IterationSpec(each.getIterVar().getValue(), null, iteratedObject);
        
    }



    
    
}
