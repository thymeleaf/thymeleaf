/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.engine21.dom.dialect;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

public class AddLocalVariableToNode extends AbstractAttrProcessor {

    
    public AddLocalVariableToNode() {
        super("add-local-variable-to-node");
    }

    
    
    
    @Override
    public int getPrecedence() {
        return 1000;
    }
    

    

    @Override
    protected ProcessorResult processAttribute(final Arguments arguments,
            final Element element, final String attributeName) {

        final String attributeValue = element.getAttributeValue(attributeName);

        final AssignationSequence assignationSequence = 
                StandardExpressionProcessor.parseAssignationSequence(arguments, attributeValue, false);

        for (final Assignation assignation : assignationSequence.getAssignations()) {
            
            final Expression varNameExpr = assignation.getLeft();
            final Expression varValueExpr = assignation.getRight();

            final Object varName = StandardExpressionProcessor.executeExpression(arguments, varNameExpr);
            final Object varValue = StandardExpressionProcessor.executeExpression(arguments, varValueExpr);
            
            element.setNodeLocalVariable((varName == null? null : varName.toString()), varValue);
            
        }
        
        element.removeAttribute(attributeName);
        
        return ProcessorResult.OK;
        
    }



}
