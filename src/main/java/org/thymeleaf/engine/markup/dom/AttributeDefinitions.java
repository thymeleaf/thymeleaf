/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.engine.markup.dom;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class AttributeDefinitions {


    private static final ConcurrentHashMap<String,AttributeDefinition> ATTRIBUTE_DEFINITIONS =
            new ConcurrentHashMap<String, AttributeDefinition>(195);
    private static final int ATTRIBUTE_DEFINITIONS_MAX_SIZE = 1000; // Just in case some crazy markup appears


    // Set containing all the standard attributes, for posible external reference
    public static final Set<AttributeDefinition> ALL_STANDARD_ATTRIBUTES;
    // Set containing all the standard attribute names, for posible external reference
    public static final Set<String> ALL_STANDARD_ATTRIBUTE_NAMES;




    static {

        ALL_STANDARD_ATTRIBUTE_NAMES =
                Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
                        new String[] {
                                "abbr", "accept", "accept-charset", "accesskey", "action", "align", "alt", "archive",
                                "autocomplete", "autofocus", "autoplay", "axis", "border", "cellpadding", "cellspacing",
                                "challenge", "char", "charoff", "charset", "checked", "cite", "class", "classid",
                                "codebase", "codetype", "cols", "colspan", "command", "content", "contenteditable",
                                "contextmenu", "controls", "coords", "data", "datetime", "declare", "default",
                                "defer", "dir", "disabled", "draggable", "dropzone", "enctype", "for", "form",
                                "formaction", "formenctype", "formmethod", "formnovalidate", "formtarget",
                                "frame", "headers", "height", "hidden", "high", "href", "hreflang", "http-equiv",
                                "icon", "id", "ismap", "keytype", "kind", "label", "lang", "list", "longdesc",
                                "loop", "low", "max", "maxlength", "media", "method", "min", "multiple", "muted",
                                "name", "nohref", "novalidate", "onabort", "onafterprint", "onbeforeprint",
                                "onbeforeunload", "onblur", "oncanplay", "oncanplaythrough", "onchange",
                                "onclick", "oncontextmenu", "oncuechange", "ondblclick", "ondrag", "ondragend",
                                "ondragenter", "ondragleave", "ondragover", "ondragstart", "ondrop",
                                "ondurationchange", "onemptied", "onended", "onerror", "onfocus",
                                "onformchange", "onforminput", "onhaschange", "oninput", "oninvalid", "onkeydown",
                                "onkeypress", "onkeyup", "onload", "onloadeddata", "onloadedmetadata",
                                "onloadstart", "onmessage", "onmousedown", "onmousemove", "onmouseout", "onmouseover",
                                "onmouseup", "onmousewheel", "onoffline", "ononline", "onpagehide", "onpageshow",
                                "onpause", "onplay", "onplaying", "onpopstate", "onprogress", "onratechange",
                                "onredo", "onreset", "onresize", "onscroll", "onseeked", "onseeking",
                                "onselect", "onstalled", "onstorage", "onsubmit", "onsuspend", "ontimeupdate",
                                "onundo", "onunload", "onvolumechange", "onwaiting", "open", "optimum", "pattern",
                                "placeholder", "poster", "preload", "profile", "radiogroup", "readonly", "rel",
                                "required", "rev", "rows", "rowspan", "rules", "scheme", "scope", "selected",
                                "shape", "size", "span", "spellcheck", "src", "srclang", "standby", "style", "summary",
                                "tabindex", "title", "translate", "type", "usemap", "valign", "value", "valuetype",
                                "width", "xml:lang", "xml:space", "xmlns"
                        })));


        /*
         * Create and register the standard attributes at the attribute repository, in order to initialize it
         */
        for (final String standardAttributeName : ALL_STANDARD_ATTRIBUTE_NAMES) {
            final AttributeDefinition attribute = new AttributeDefinition(standardAttributeName);
            ATTRIBUTE_DEFINITIONS.put(attribute.getName(), attribute);
        }

        ALL_STANDARD_ATTRIBUTES =
                Collections.unmodifiableSet(new LinkedHashSet<AttributeDefinition>(ATTRIBUTE_DEFINITIONS.values()));


    }




    public static AttributeDefinition forName(final boolean caseSensitive, final String attributeName) {

        if (attributeName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }

        // We first try without executing toLowerCase(), in order to avoid unnecessary load when most
        // of the requests will be for the already normalized (lower-cased) version.
        AttributeDefinition definition = ATTRIBUTE_DEFINITIONS.get(attributeName);
        if (definition != null) {
            return definition;
        }

        if (!caseSensitive) {
            definition = ATTRIBUTE_DEFINITIONS.get(attributeName.toLowerCase());
            if (definition != null) {
                return definition;
            }
            definition = new AttributeDefinition(attributeName.toLowerCase());
        } else {
            definition = new AttributeDefinition(attributeName);
        }

        if (ATTRIBUTE_DEFINITIONS.size() < ATTRIBUTE_DEFINITIONS_MAX_SIZE) {
            ATTRIBUTE_DEFINITIONS.putIfAbsent(definition.getName(), definition);
        }

        return definition;

    }




    private AttributeDefinitions() {
        super();
    }



}
