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
package thymeleafexamples.extrathyme.dialects.score;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractTextChildModifierAttrProcessor;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import thymeleafexamples.extrathyme.business.entities.Remark;
import thymeleafexamples.extrathyme.business.util.RemarkUtil;

public class RemarkForPositionAttrProcessor 
        extends AbstractTextChildModifierAttrProcessor {

    
    public RemarkForPositionAttrProcessor() {
        super("remarkforposition");
    }

    
    
    
    public int getPrecedence() {
        return 12000;
    }
    

    

    @Override
    protected String getText(final Arguments arguments,
            final Element element, String attributeName) {

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
         * If no remark is to be applied, just return an empty message 
         */
        if (remark == null) {
            return "";
        }
        
        /*
         * Message should be internationalized, so we ask the engine to resolve the message
         * 'remarks.{REMARK}' (e.g. 'remarks.RELEGATION'). No parameters are needed for this
         * message.
         */
        return getMessage(arguments, "remarks." + remark.toString(), new Object[0]); 
        
    }



}
