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
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.inliner.ITextInliner;
import org.thymeleaf.processor.attr.AbstractTextInlinerAttrProcessor;
import org.thymeleaf.standard.inliner.StandardDartInliner;
import org.thymeleaf.standard.inliner.StandardJavaScriptInliner;
import org.thymeleaf.standard.inliner.StandardTextInliner;
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
public abstract class AbstractStandardTextInlinerAttrProcessor 
        extends AbstractTextInlinerAttrProcessor {
    
    
    public static final String TEXT_INLINE = "text";
    public static final String JAVASCRIPT_INLINE = "javascript";
    public static final String DART_INLINE = "dart";
    public static final String NONE_INLINE = "none";

    
    
    
    public AbstractStandardTextInlinerAttrProcessor() {
        super();
    }

    


    @Override
    protected ITextInliner getTextInliner(final Arguments arguments,
            final TemplateResolution templateResolution, final Document document,
            final Element element, final Attr attribute, final String attributeName,
            final String attributeValue) {
        
        if (JAVASCRIPT_INLINE.equals(attributeValue.toLowerCase())) {
            return StandardJavaScriptInliner.INSTANCE;
        } else if (DART_INLINE.equals(attributeValue.toLowerCase())) {
            return StandardDartInliner.INSTANCE;
        } else if (TEXT_INLINE.equals(attributeValue.toLowerCase())) {
            return StandardTextInliner.INSTANCE;
        } else if (NONE_INLINE.equals(attributeValue.toLowerCase())) {
            return null;
        }
        
        throw new TemplateProcessingException(
                "Cannot recognize value for \"" + attributeName + "\". Allowed values are " +
                "\"" + TEXT_INLINE + "\", \"" + JAVASCRIPT_INLINE + "\", " +
                "\"" + DART_INLINE + "\" and \"" + NONE_INLINE + "\"");
        
    }
    
    

    
}
