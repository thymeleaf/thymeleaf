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
public final class StandardDOMEventAttributeModifierAttrProcessor 
        extends AbstractStandardSingleAttributeModifierAttrProcessor {

    public static final Integer ATTR_PRECEDENCE = Integer.valueOf(1000);
    
    public static final String[] ATTR_NAMES = 
        new String[] {
                "onabort",
                "onafterprint",
                "onbeforeprint",
                "onbeforeunload",
                "onblur",
                "oncanplay",
                "oncanplaythrough",
                "onchange",
                "onclick",
                "oncontextmenu",
                "ondblclick",
                "ondrag",
                "ondragend",
                "ondragenter",
                "ondragleave",
                "ondragover",
                "ondragstart",
                "ondrop",
                "ondurationchange",
                "onemptied",
                "onended",
                "onerror",
                "onfocus",
                "onformchange",
                "onforminput",
                "onhashchange",
                "oninput",
                "oninvalid",
                "onkeydown",
                "onkeypress",
                "onkeyup",
                "onload",
                "onloadeddata",
                "onloadedmetadata",
                "onloadstart",
                "onmessage",
                "onmousedown",
                "onmousemove",
                "onmouseout",
                "onmouseover",
                "onmouseup",
                "onmousewheel",
                "onoffline",
                "ononline",
                "onpause",
                "onplay",
                "onplaying",
                "onpopstate",
                "onprogress",
                "onratechange",
                "onreadystatechange",
                "onredo",
                "onreset",
                "onresize",
                "onscroll",
                "onseeked",
                "onseeking",
                "onselect",
                "onshow",
                "onstalled",
                "onstorage",
                "onsubmit",
                "onsuspend",
                "ontimeupdate",
                "onundo",
                "onunload",
                "onvolumechange",
                "onwaiting"
        };
    
    
    
    public StandardDOMEventAttributeModifierAttrProcessor() {
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
