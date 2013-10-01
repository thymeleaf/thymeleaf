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
package org.thymeleaf.engine20.dom.dialect;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

public class AddLocalVariableToResult extends AbstractAttrProcessor {

    
    public AddLocalVariableToResult() {
        super("add-local-variable-to-result");
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

        final Map<String,Object> localVariables = new HashMap<String,Object>();
        for (final Assignation assignation : assignationSequence.getAssignations()) {

            final String varName = assignation.getLeft().getValue();
            final Expression varValueExpr = assignation.getRight();

            final Object varValue = StandardExpressionProcessor.executeExpression(arguments, varValueExpr);

            localVariables.put(varName, varValue);

        }
        
        element.removeAttribute(attributeName);
        
        return ProcessorResult.setLocalVariables(localVariables);
        
    }



}
