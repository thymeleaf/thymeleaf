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
package thymeleafexamples.extrathyme.dialects.score;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import thymeleafexamples.extrathyme.business.entities.Remark;
import thymeleafexamples.extrathyme.business.util.RemarkUtil;

public class ClassForPositionAttrProcessor 
        extends AbstractAttributeModifierAttrProcessor {

    
    public ClassForPositionAttrProcessor() {
        super("classforposition");
    }


    
    
    public int getPrecedence() {
        return 12000;
    }
    
    

    @Override
    protected Map<String, String> getModifiedAttributeValues(
            final Arguments arguments, final Element element,
            final String attributeName) {

        final Configuration configuration = arguments.getConfiguration();

        /*
         * Obtain the attribute value
         */
        final String attributeValue = element.getAttributeValue(attributeName);

        /*
         * Obtain the Thymeleaf Standard Expression parser
         */
        final IStandardExpressionParser parser =
                StandardExpressions.getExpressionParser(configuration);

        /*
         * Parse the attribute value as a Thymeleaf Standard Expression
         */
        final IStandardExpression expression =
                parser.parseExpression(configuration, arguments, attributeValue);

        /*
         * Execute the expression just parsed
         */
        final Integer position =
                (Integer) expression.execute(configuration, arguments);

        /*
         * Obtain the remark corresponding to this position in the league table.
         */
        final Remark remark = RemarkUtil.getRemarkForPosition(position);
        
        /*
         * Apply the corresponding CSS class to the element.
         */
        final Map<String,String> values = new HashMap<String, String>();
        if (remark != null) {
            switch (remark) {
                case WORLD_CHAMPIONS_LEAGUE:  
                    values.put("class", "wcl");
                    break;
                case CONTINENTAL_PLAYOFFS:  
                    values.put("class", "cpo");
                    break;
                case RELEGATION:  
                    values.put("class", "rel");
                    break;
            }
        }
        
        return values;
        
    }

    
    



    @Override
    protected ModificationType getModificationType(final Arguments arguments,
            final Element element, final String attributeName,
            final String newAttributeName) {
        // Just in case there already is a value set for the 'class' attribute in the
        // tag, we will append our new value (using a whitespace separator) instead
        // of simply substituting it.
        return ModificationType.APPEND_WITH_SPACE;
    }



    @Override
    protected boolean removeAttributeIfEmpty(final Arguments arguments,
            final Element element, final String attributeName,
            final String newAttributeName) {
        // If the resulting 'class' attribute is empty, do not show it at all.
        return true;
    }



    @Override
    protected boolean recomputeProcessorsAfterExecution(final Arguments arguments,
            final Element element, final String attributeName) {
        // There is no need to recompute the element after this processor has executed 
        return false;
    }
    



}
