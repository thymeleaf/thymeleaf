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
package org.thymeleaf.spring3.processor.attr;

import org.thymeleaf.standard.processor.attr.StandardSingleRemovableAttributeModifierAttrProcessor;

/**
 * <p>
 *   Processor utility factory for 
 *   {@link org.thymeleaf.standard.processor.attr.StandardSingleRemovableAttributeModifierAttrProcessor},
 *   removing the <tt>method</tt> attribute from the list, as this will be treated at the
 *   {@link SpringMethodAttrProcessor} processor.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.3
 *
 */
public final class SpringSingleRemovableAttributeModifierAttrProcessor {
    
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

    
    
    
    private SpringSingleRemovableAttributeModifierAttrProcessor() {
        super();
    }
    
}
