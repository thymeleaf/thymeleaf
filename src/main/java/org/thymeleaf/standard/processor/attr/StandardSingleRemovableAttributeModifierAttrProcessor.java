/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class StandardSingleRemovableAttributeModifierAttrProcessor 
        extends AbstractStandardSingleAttributeModifierAttrProcessor {

    public static final int ATTR_PRECEDENCE = 1000;
    
    public static final String[] ATTR_NAMES = 
        new String[] {
                "abbr",
                "accept",
                "accept-charset",
                "accesskey",
                "align",
                "alt",
                "archive",
                "audio",
                "autocomplete",
                "axis",
                "background",
                "bgcolor",
                "border",
                "cellpadding",
                "cellspacing",
                "challenge",
                "charset",
                "cite",
                "class",
                "classid",
                "codebase",
                "codetype",
                "cols",
                "colspan",
                "compact",
                "content",
                "contenteditable",
                "contextmenu",
                "data",
                "datetime",
                "dir",
                "draggable",
                "dropzone",
                "enctype",
                "for",
                "form",
                "formaction",
                "formenctype",
                "formmethod",
                "formtarget",
                "frame",
                "frameborder",
                "headers",
                "height",
                "high",
                "hreflang",
                "hspace",
                "http-equiv",
                "icon",
                "id",
                "keytype",
                "kind",
                "label",
                "lang",
                "list",
                "longdesc",
                "low",
                "manifest",
                "marginheight",
                "marginwidth",
                "max",
                "maxlength",
                "media",
                "method",
                "min",
                "optimum",
                "pattern",
                "placeholder",
                "poster",
                "preload",
                "radiogroup",
                "rel",
                "rev",
                "rows",
                "rowspan",
                "rules",
                "sandbox",
                "scheme",
                "scope",
                "scrolling",
                "size",
                "sizes",
                "span",
                "spellcheck",
                "standby",
                "style",
                "srclang",
                "start",
                "step",
                "summary",
                "tabindex",
                "target",
                "title",
                "usemap",
                "valuetype",
                "vspace",
                "width",
                "wrap"
        };
    
    
    public static final StandardSingleRemovableAttributeModifierAttrProcessor[] PROCESSORS;
    

    
    static {
        
        PROCESSORS = new StandardSingleRemovableAttributeModifierAttrProcessor[ATTR_NAMES.length];
        for (int i = 0; i < PROCESSORS.length; i++) {
            PROCESSORS[i] = new StandardSingleRemovableAttributeModifierAttrProcessor(ATTR_NAMES[i]);
        }
        
    }

    
    
    
    public StandardSingleRemovableAttributeModifierAttrProcessor(final String attributeName) {
        super(attributeName);
    }

    
    
    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }



    @Override
    protected String getTargetAttributeName(
            final Arguments arguments, final Element element, final String attributeName) {
        return Attribute.getUnprefixedAttributeName(attributeName);
    }

    
    @Override
    protected ModificationType getModificationType(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return ModificationType.SUBSTITUTION;
    }



    @Override
    protected boolean removeAttributeIfEmpty(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return true;
    }


    
}
