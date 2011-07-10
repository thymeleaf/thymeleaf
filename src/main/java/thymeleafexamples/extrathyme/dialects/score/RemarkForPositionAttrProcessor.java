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

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.Arguments;
import org.thymeleaf.processor.applicability.AttrApplicability;
import org.thymeleaf.processor.attr.AbstractTextChildModifierAttrProcessor;
import org.thymeleaf.processor.value.IValueProcessor;
import org.thymeleaf.standard.processor.value.StandardValueProcessor;
import org.thymeleaf.standard.syntax.StandardSyntax;
import org.thymeleaf.standard.syntax.StandardSyntax.Value;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import thymeleafexamples.extrathyme.business.entities.Remark;
import thymeleafexamples.extrathyme.business.util.RemarkUtil;

public class RemarkForPositionAttrProcessor 
        extends AbstractTextChildModifierAttrProcessor {

    
    public RemarkForPositionAttrProcessor() {
        super();
    }

    
    
    public Set<AttrApplicability> getAttributeApplicabilities() {
        return AttrApplicability.createSetForAttrName("remarkforposition");
    }

    
    
    public Integer getPrecedence() {
        return Integer.valueOf(12000);
    }


    @Override
    public Set<Class<? extends IValueProcessor>> getValueProcessorDependencies() {
        final Set<Class<? extends IValueProcessor>> dependencies = new HashSet<Class<? extends IValueProcessor>>();
        dependencies.add(StandardValueProcessor.class);
        return dependencies;
    }
    

    

    @Override
    protected String getText(final Arguments arguments,
            final TemplateResolution templateResolution, final Document document,
            final Element element, final Attr attribute, final String attributeName,
            final String attributeValue) {

        final StandardValueProcessor valueProcessor =
            arguments.getConfiguration().getValueProcessorByClass(this,StandardValueProcessor.class);
        
        final Value positionValue = 
            StandardSyntax.parseValue(attributeValue, valueProcessor, arguments, templateResolution);
        
        final Integer position = 
            (Integer) valueProcessor.getValue(arguments, templateResolution, positionValue); 
        
        final Remark remark = RemarkUtil.getRemarkForPosition(position);
        
        if (remark == null) {
            return "";
        }
        
        return getMessage(arguments, templateResolution, "remarks." + remark.toString(), new Object[0]); 
        
    }
    



}
