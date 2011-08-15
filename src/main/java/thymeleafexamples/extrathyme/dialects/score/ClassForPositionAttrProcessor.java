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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.Arguments;
import org.thymeleaf.processor.applicability.AttrApplicability;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import thymeleafexamples.extrathyme.business.entities.Remark;
import thymeleafexamples.extrathyme.business.util.RemarkUtil;

public class ClassForPositionAttrProcessor 
        extends AbstractAttributeModifierAttrProcessor {

    
    public ClassForPositionAttrProcessor() {
        super();
    }

    
    
    public Set<AttrApplicability> getAttributeApplicabilities() {
        return AttrApplicability.createSetForAttrName("classforposition");
    }

    
    
    public Integer getPrecedence() {
        return Integer.valueOf(12000);
    }
    
    

    @Override
    protected Map<String, String> getNewAttributeValues(final Arguments arguments,
            final TemplateResolution templateResolution, final Document document,
            final Element element, final Attr attribute, final String attributeName,
            final String attributeValue) {

        /*
         * Process (parse and execute) the attribute value, specified as a
         * Thymeleaf Standard Expression.
         */
        final Integer position =
            (Integer) StandardExpressionProcessor.processExpression(
                    arguments, templateResolution, attributeValue);

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
            final TemplateResolution templateResolution, final Document document,
            final Element element, final Attr attribute, final String attributeName,
            final String attributeValue, final String newAttributeName) {
        // Just in case there already is a value set for the 'class' attribute in the
        // tag, we will append our new value (using a whitespace separator) instead
        // of simply substituting it.
        return ModificationType.APPEND_WITH_SPACE;
    }

    
    
    @Override
    protected boolean removeAttributeIfEmpty(final Arguments arguments,
            final TemplateResolution templateResolution, final Document document,
            final Element element, final Attr attribute, final String attributeName,
            final String attributeValue, final String newAttributeName) {
        // If the resulting 'class' attribute is empty, do not show it at all.
        return true;
    }
    



}
