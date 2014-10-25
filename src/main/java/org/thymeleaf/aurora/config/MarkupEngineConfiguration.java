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
package org.thymeleaf.aurora.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.thymeleaf.aurora.parser.HtmlTemplateParser;
import org.thymeleaf.aurora.parser.ITemplateParser;
import org.thymeleaf.aurora.text.IMarkupTextRepository;
import org.thymeleaf.aurora.text.StandardMarkupTextRepository;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class MarkupEngineConfiguration {


    private static Set<String> ALL_STANDARD_ELEMENT_NAMES =
            Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays.asList(new String[]{
                    "a", "abbr", "address", "area", "article", "aside",
                    "audio", "b", "base", "bdi", "bdo", "blockquote",
                    "body", "br", "button", "canvas", "caption", "cite",
                    "code", "col", "colgroup", "command", "content", "datalist",
                    "dd", "decorator", "del", "details", "dfn", "dialog",
                    "div", "dl", "dt", "element", "em", "embed",
                    "fieldset", "figcaption", "figure", "footer", "form", "g",
                    "h1", "h2", "h3", "h4", "h5", "h6",
                    "head", "header", "hgroup", "hr", "html", "i",
                    "iframe", "img", "input", "ins", "kbd", "keygen",
                    "label", "legend", "li", "link", "main", "map",
                    "mark", "menu", "menuitem", "meta", "meter", "nav",
                    "noscript", "object", "ol", "optgroup", "option", "output",
                    "p", "param", "pre", "progress", "rb", "rp",
                    "rt", "rtc", "ruby", "s", "samp", "script",
                    "section", "select", "shadow", "small", "source", "span",
                    "strong", "style", "sub", "summary", "sup", "table",
                    "tbody", "td", "template", "textarea", "tfoot", "th",
                    "thead", "time", "title", "tr", "track", "u",
                    "ul", "var", "video", "wbr"
            })));

    private static Set<String> ALL_STANDARD_ATTRIBUTE_NAMES =
            Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays.asList(new String[]{
                    "abbr","accept","accept-charset","accesskey","action","align",
                    "alt","archive","autocomplete","autofocus","autoplay","axis",
                    "border","cellpadding","cellspacing","challenge","char","charoff",
                    "charset","checked","cite","class","classid","codebase",
                    "codetype","cols","colspan","command","content","contenteditable",
                    "contextmenu","controls","coords","data","datetime","declare",
                    "default","defer","dir","disabled","draggable","dropzone",
                    "enctype","for","form","formaction","formenctype","formmethod",
                    "formnovalidate","formtarget","frame","headers","height","hidden",
                    "high","href","hreflang","http-equiv","icon","id",
                    "ismap","keytype","kind","label","lang","list",
                    "longdesc","loop","low","max","maxlength","media",
                    "method","min","multiple","muted","name","nohref",
                    "novalidate","onabort","onafterprint","onbeforeprint","onbeforeunload","onblur",
                    "oncanplay","oncanplaythrough","onchange","onclick","oncontextmenu","oncuechange",
                    "ondblclick","ondrag","ondragend","ondragenter","ondragleave","ondragover",
                    "ondragstart","ondrop","ondurationchange","onemptied","onended","onerror",
                    "onfocus","onformchange","onforminput","onhaschange","oninput","oninvalid",
                    "onkeydown","onkeypress","onkeyup","onload","onloadeddata","onloadedmetadata",
                    "onloadstart","onmessage","onmousedown","onmousemove","onmouseout","onmouseover",
                    "onmouseup","onmousewheel","onoffline","ononline","onpagehide","onpageshow",
                    "onpause","onplay","onplaying","onpopstate","onprogress","onratechange",
                    "onredo","onreset","onresize","onscroll","onseeked","onseeking",
                    "onselect","onstalled","onstorage","onsubmit","onsuspend","ontimeupdate",
                    "onundo","onunload","onvolumechange","onwaiting","open","optimum",
                    "pattern","placeholder","poster","preload","profile","radiogroup",
                    "readonly","rel","required","rev","rows","rowspan",
                    "rules","scheme","scope","selected","shape","size",
                    "span","spellcheck","src","srclang","standby","style",
                    "summary","tabindex","title","translate","type","usemap",
                    "valign","value","valuetype","width","xml:lang","xml:space",
                    "xmlns"
            })));



    private final ITemplateParser parser;
    private final IMarkupTextRepository textRepository;


    public MarkupEngineConfiguration(
            final ITemplateParser parser, final IMarkupTextRepository textRepository) {
        super();
        this.parser = parser;
        this.textRepository = textRepository;
    }


    public ITemplateParser getParser() {
        return this.parser;
    }

    public IMarkupTextRepository getTextRepository() {
        return textRepository;
    }



    public static MarkupEngineConfiguration createBaseConfiguration() {

        final List<String> unremovableTexts  = new ArrayList<String>();
        unremovableTexts.addAll(ALL_STANDARD_ELEMENT_NAMES);
        unremovableTexts.addAll(ALL_STANDARD_ATTRIBUTE_NAMES);
        unremovableTexts.add(" ");
        unremovableTexts.add("\n");
        unremovableTexts.add("\n  ");
        unremovableTexts.add("\n    ");
        unremovableTexts.add("\n      ");
        unremovableTexts.add("\n        ");
        unremovableTexts.add("\n          ");
        unremovableTexts.add("\n            ");
        unremovableTexts.add("\n              ");
        unremovableTexts.add("\n                ");
        unremovableTexts.add("\n\t");
        unremovableTexts.add("\n\t\t");
        unremovableTexts.add("\n\t\t\t");
        unremovableTexts.add("\n\t\t\t\t");

        // Size = 10MBytes (1 char = 2 bytes)
        final IMarkupTextRepository textRepository =
                new StandardMarkupTextRepository(5242880, unremovableTexts.toArray(new String[unremovableTexts.size()]));


        // Pool size is set to 40, and buffer size to 2K chars = 4K bytes
        // This should be enough for processing 20 templates at a time, each one being able to include external
        // fragments and/or parsable content (2 buffers per template).
        final ITemplateParser parser = new HtmlTemplateParser(40,2048);

        return new MarkupEngineConfiguration(parser, textRepository);

    }

}
