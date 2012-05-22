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

import java.util.Set;

import org.thymeleaf.Arguments;
import org.thymeleaf.processor.applicability.AttrApplicability;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.PrefixUtils;
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
public final class StandardSingleRemovableAttributeModifierAttrProcessor 
        extends AbstractStandardSingleAttributeModifierAttrProcessor {

    public static final Integer ATTR_PRECEDENCE = Integer.valueOf(1000);
    
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
                "value",
                "valuetype",
                "vspace",
                "width",
                "wrap"
        };
    
    
    
    public StandardSingleRemovableAttributeModifierAttrProcessor() {
        super();
    }

    
    
    public Set<AttrApplicability> getAttributeApplicabilities() {
        return AttrApplicability.createSetForAttrNames(ATTR_NAMES);
    }

    public Integer getPrecedence() {
        return ATTR_PRECEDENCE;
    }



    @Override
    protected String getTargetAttributeName(
            final Arguments arguments, final TemplateResolution templateResolution, final Document document, 
            final Element element, final Attr attribute, final String attributeName, final String attributeValue) {
        return PrefixUtils.getUnprefixed(attributeName);
    }

    
    @Override
    protected ModificationType getModificationType(
            final Arguments arguments, final TemplateResolution templateResolution, 
            final Document document, final Element element, 
            final Attr attribute, 
            final String attributeName, final String attributeValue,
            final String newAttributeName) {
        return ModificationType.SUBSTITUTION;
    }



    @Override
    protected boolean removeAttributeIfEmpty(
            final Arguments arguments, final TemplateResolution templateResolution, 
            final Document document, final Element element, 
            final Attr attribute, 
            final String attributeName, final String attributeValue,
            final String newAttributeName) {
        return true;
    }


    
}
