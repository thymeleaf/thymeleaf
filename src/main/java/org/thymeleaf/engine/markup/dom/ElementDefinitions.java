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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ElementDefinitions {


    private static final ElementDefinitionRepository ELEMENT_DEFINITIONS = new ElementDefinitionRepository();



    // Set containing all the standard elements, for posible external reference
    public static final Set<ElementDefinition> ALL_STANDARD_ELEMENTS;
    // Set containing all the standard element names, for posible external reference
    public static final Set<String> ALL_STANDARD_ELEMENT_NAMES;
    // Set containing all the standard attribute names, for posible external reference
    public static final Set<String> ALL_STANDARD_ATTRIBUTE_NAMES;



    // Root
    public static final ElementDefinition HTML = new ElementDefinition("html", ElementType.NORMAL);

    // Document metadata
    public static final ElementDefinition HEAD = new ElementDefinition("head", ElementType.NORMAL);
    public static final ElementDefinition TITLE = new ElementDefinition("title", ElementType.ESCAPABLE_RAW_TEXT);
    public static final ElementDefinition BASE = new ElementDefinition("base", ElementType.VOID);
    public static final ElementDefinition LINK = new ElementDefinition("link", ElementType.VOID);
    public static final ElementDefinition META = new ElementDefinition("meta", ElementType.VOID);
    public static final ElementDefinition STYLE = new ElementDefinition("style", ElementType.RAW_TEXT);

    // Scripting
    public static final ElementDefinition SCRIPT = new ElementDefinition("script", ElementType.RAW_TEXT);
    public static final ElementDefinition NOSCRIPT = new ElementDefinition("noscript", ElementType.NORMAL);

    // Sections
    public static final ElementDefinition BODY = new ElementDefinition("body", ElementType.NORMAL);
    public static final ElementDefinition ARTICLE = new ElementDefinition("article", ElementType.NORMAL);
    public static final ElementDefinition SECTION = new ElementDefinition("section", ElementType.NORMAL);
    public static final ElementDefinition NAV = new ElementDefinition("nav", ElementType.NORMAL);
    public static final ElementDefinition ASIDE = new ElementDefinition("aside", ElementType.NORMAL);
    public static final ElementDefinition H1 = new ElementDefinition("h1", ElementType.NORMAL);
    public static final ElementDefinition H2 = new ElementDefinition("h2", ElementType.NORMAL);
    public static final ElementDefinition H3 = new ElementDefinition("h3", ElementType.NORMAL);
    public static final ElementDefinition H4 = new ElementDefinition("h4", ElementType.NORMAL);
    public static final ElementDefinition H5 = new ElementDefinition("h5", ElementType.NORMAL);
    public static final ElementDefinition H6 = new ElementDefinition("h6", ElementType.NORMAL);
    public static final ElementDefinition HGROUP = new ElementDefinition("hgroup", ElementType.NORMAL);
    public static final ElementDefinition HEADER = new ElementDefinition("header", ElementType.NORMAL);
    public static final ElementDefinition FOOTER = new ElementDefinition("footer", ElementType.NORMAL);
    public static final ElementDefinition ADDRESS = new ElementDefinition("address", ElementType.NORMAL);
    public static final ElementDefinition MAIN = new ElementDefinition("main", ElementType.NORMAL);

    // Grouping content
    public static final ElementDefinition P = new ElementDefinition("p", ElementType.NORMAL);
    public static final ElementDefinition HR = new ElementDefinition("hr", ElementType.VOID);
    public static final ElementDefinition PRE = new ElementDefinition("pre", ElementType.NORMAL);
    public static final ElementDefinition BLOCKQUOTE = new ElementDefinition("blockquote", ElementType.NORMAL);
    public static final ElementDefinition OL = new ElementDefinition("ol", ElementType.NORMAL);
    public static final ElementDefinition UL = new ElementDefinition("ul", ElementType.NORMAL);
    public static final ElementDefinition LI = new ElementDefinition("li", ElementType.NORMAL);
    public static final ElementDefinition DL = new ElementDefinition("dl", ElementType.NORMAL);
    public static final ElementDefinition DT = new ElementDefinition("dt", ElementType.NORMAL);
    public static final ElementDefinition DD = new ElementDefinition("dd", ElementType.NORMAL);
    public static final ElementDefinition FIGURE = new ElementDefinition("figure", ElementType.NORMAL);
    public static final ElementDefinition FIGCAPTION = new ElementDefinition("figcaption", ElementType.NORMAL);
    public static final ElementDefinition DIV = new ElementDefinition("div", ElementType.NORMAL);

    // Text-level semantics
    public static final ElementDefinition A = new ElementDefinition("a", ElementType.NORMAL);
    public static final ElementDefinition EM = new ElementDefinition("em", ElementType.NORMAL);
    public static final ElementDefinition STRONG = new ElementDefinition("strong", ElementType.NORMAL);
    public static final ElementDefinition SMALL = new ElementDefinition("small", ElementType.NORMAL);
    public static final ElementDefinition S = new ElementDefinition("s", ElementType.NORMAL);
    public static final ElementDefinition CITE = new ElementDefinition("cite", ElementType.NORMAL);
    public static final ElementDefinition G = new ElementDefinition("g", ElementType.NORMAL);
    public static final ElementDefinition DFN = new ElementDefinition("dfn", ElementType.NORMAL);
    public static final ElementDefinition ABBR = new ElementDefinition("abbr", ElementType.NORMAL);
    public static final ElementDefinition TIME = new ElementDefinition("time", ElementType.NORMAL);
    public static final ElementDefinition CODE = new ElementDefinition("code", ElementType.NORMAL);
    public static final ElementDefinition VAR = new ElementDefinition("var", ElementType.NORMAL);
    public static final ElementDefinition SAMP = new ElementDefinition("samp", ElementType.NORMAL);
    public static final ElementDefinition KBD = new ElementDefinition("kbd", ElementType.NORMAL);
    public static final ElementDefinition SUB = new ElementDefinition("sub", ElementType.NORMAL);
    public static final ElementDefinition SUP = new ElementDefinition("sup", ElementType.NORMAL);
    public static final ElementDefinition I = new ElementDefinition("i", ElementType.NORMAL);
    public static final ElementDefinition B = new ElementDefinition("b", ElementType.NORMAL);
    public static final ElementDefinition U = new ElementDefinition("u", ElementType.NORMAL);
    public static final ElementDefinition MARK = new ElementDefinition("mark", ElementType.NORMAL);
    public static final ElementDefinition RUBY = new ElementDefinition("ruby", ElementType.NORMAL);
    public static final ElementDefinition RT = new ElementDefinition("rt", ElementType.NORMAL);
    public static final ElementDefinition RP = new ElementDefinition("rp", ElementType.NORMAL);
    public static final ElementDefinition BDI = new ElementDefinition("bdi", ElementType.NORMAL);
    public static final ElementDefinition BDO = new ElementDefinition("bdo", ElementType.NORMAL);
    public static final ElementDefinition SPAN = new ElementDefinition("span", ElementType.NORMAL);
    public static final ElementDefinition BR = new ElementDefinition("br", ElementType.VOID);
    public static final ElementDefinition WBR = new ElementDefinition("wbr", ElementType.VOID);

    // Edits
    public static final ElementDefinition INS = new ElementDefinition("ins", ElementType.NORMAL);
    public static final ElementDefinition DEL = new ElementDefinition("del", ElementType.NORMAL);

    // Embedded content
    public static final ElementDefinition IMG = new ElementDefinition("img", ElementType.VOID);
    public static final ElementDefinition IFRAME = new ElementDefinition("iframe", ElementType.NORMAL);
    public static final ElementDefinition EMBED = new ElementDefinition("embed", ElementType.VOID);
    public static final ElementDefinition OBJECT = new ElementDefinition("object", ElementType.NORMAL);
    public static final ElementDefinition PARAM = new ElementDefinition("param", ElementType.VOID);
    public static final ElementDefinition VIDEO = new ElementDefinition("video", ElementType.NORMAL);
    public static final ElementDefinition AUDIO = new ElementDefinition("audio", ElementType.NORMAL);
    public static final ElementDefinition SOURCE = new ElementDefinition("source", ElementType.VOID);
    public static final ElementDefinition TRACK = new ElementDefinition("track", ElementType.VOID);
    public static final ElementDefinition CANVAS = new ElementDefinition("canvas", ElementType.NORMAL);
    public static final ElementDefinition MAP = new ElementDefinition("map", ElementType.NORMAL);
    public static final ElementDefinition AREA = new ElementDefinition("area", ElementType.VOID);

    // Tabular data
    public static final ElementDefinition TABLE = new ElementDefinition("table", ElementType.NORMAL);
    public static final ElementDefinition CAPTION = new ElementDefinition("caption", ElementType.NORMAL);
    public static final ElementDefinition COLGROUP = new ElementDefinition("colgroup", ElementType.NORMAL);
    public static final ElementDefinition COL = new ElementDefinition("col", ElementType.VOID);
    public static final ElementDefinition TBODY = new ElementDefinition("tbody", ElementType.NORMAL);
    public static final ElementDefinition THEAD = new ElementDefinition("thead", ElementType.NORMAL);
    public static final ElementDefinition TFOOT = new ElementDefinition("tfoot", ElementType.NORMAL);
    public static final ElementDefinition TR = new ElementDefinition("tr", ElementType.NORMAL);
    public static final ElementDefinition TD = new ElementDefinition("td", ElementType.NORMAL);
    public static final ElementDefinition TH = new ElementDefinition("th", ElementType.NORMAL);

    // Forms
    public static final ElementDefinition FORM = new ElementDefinition("form", ElementType.NORMAL);
    public static final ElementDefinition FIELDSET = new ElementDefinition("fieldset", ElementType.NORMAL);
    public static final ElementDefinition LEGEND = new ElementDefinition("legend", ElementType.NORMAL);
    public static final ElementDefinition LABEL = new ElementDefinition("label", ElementType.NORMAL);
    public static final ElementDefinition INPUT = new ElementDefinition("input", ElementType.VOID);
    public static final ElementDefinition BUTTON = new ElementDefinition("button", ElementType.NORMAL);
    public static final ElementDefinition SELECT = new ElementDefinition("select", ElementType.NORMAL);
    public static final ElementDefinition DATALIST = new ElementDefinition("datalist", ElementType.NORMAL);
    public static final ElementDefinition OPTGROUP = new ElementDefinition("optgroup", ElementType.NORMAL);
    public static final ElementDefinition OPTION = new ElementDefinition("option", ElementType.NORMAL);
    public static final ElementDefinition TEXTAREA = new ElementDefinition("textarea", ElementType.ESCAPABLE_RAW_TEXT);
    public static final ElementDefinition KEYGEN = new ElementDefinition("keygen", ElementType.VOID);
    public static final ElementDefinition OUTPUT = new ElementDefinition("output", ElementType.NORMAL);
    public static final ElementDefinition PROGRESS = new ElementDefinition("progress", ElementType.NORMAL);
    public static final ElementDefinition METER = new ElementDefinition("meter", ElementType.NORMAL);

    // Interactive elements
    public static final ElementDefinition DETAILS = new ElementDefinition("details", ElementType.NORMAL);
    public static final ElementDefinition SUMMARY = new ElementDefinition("summary", ElementType.NORMAL);
    public static final ElementDefinition COMMAND = new ElementDefinition("command", ElementType.NORMAL);
    public static final ElementDefinition MENU = new ElementDefinition("menu", ElementType.NORMAL);
    public static final ElementDefinition MENUITEM = new ElementDefinition("menuitem", ElementType.VOID);
    public static final ElementDefinition DIALOG = new ElementDefinition("dialog", ElementType.NORMAL);





    static {

        ALL_STANDARD_ELEMENTS =
                Collections.unmodifiableSet(new LinkedHashSet<ElementDefinition>(Arrays.asList(
                        new ElementDefinition[]{
                                HTML, HEAD, TITLE, BASE, LINK, META, STYLE, SCRIPT, NOSCRIPT, BODY, ARTICLE,
                                SECTION, NAV, ASIDE, H1, H2, H3, H4, H5, H6, HGROUP, HEADER, FOOTER,
                                ADDRESS, P, HR, PRE, BLOCKQUOTE, OL, UL, LI, DL, DT, DD, FIGURE,
                                FIGCAPTION, DIV, A, EM, STRONG, SMALL, S, CITE, G, DFN, ABBR, TIME,
                                CODE, VAR, SAMP, KBD, SUB, SUP, I, B, U, MARK, RUBY, RT,
                                RP, BDI, BDO, SPAN, BR, WBR, INS, DEL, IMG, IFRAME, EMBED, OBJECT,
                                PARAM, VIDEO, AUDIO, SOURCE, TRACK, CANVAS, MAP, AREA, TABLE, CAPTION,
                                COLGROUP, COL, TBODY, THEAD, TFOOT, TR, TD, TH, FORM, FIELDSET, LEGEND, LABEL,
                                INPUT, BUTTON, SELECT, DATALIST, OPTGROUP, OPTION, TEXTAREA, KEYGEN, OUTPUT, PROGRESS,
                                METER, DETAILS, SUMMARY, COMMAND, MENU, MENUITEM, DIALOG, MAIN
                        })));

        /*
         * Register the standard elements at the element repository, in order to initialize it
         */
        for (final ElementDefinition element : ALL_STANDARD_ELEMENTS) {
            ELEMENT_DEFINITIONS.storeElement(element);
        }


        final Set<String> allStandardElementNamesAux = new LinkedHashSet<String>(ALL_STANDARD_ELEMENTS.size() + 3);
        for (final ElementDefinition element : ALL_STANDARD_ELEMENTS) {
            allStandardElementNamesAux.add(element.getNormalizedName());
        }
        ALL_STANDARD_ELEMENT_NAMES = Collections.unmodifiableSet(allStandardElementNamesAux);


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


    }




    public static ElementDefinition forName(final char[] nameBuffer, final int nameOffset, final int nameLen) {
        if (nameBuffer == null) {
            throw new IllegalArgumentException("Buffer cannot be null");
        }
        return ELEMENT_DEFINITIONS.getElement(nameBuffer, nameOffset, nameLen);
    }



    public static ElementDefinition forName(final String elementName) {
        if (elementName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        return ELEMENT_DEFINITIONS.getElement(elementName);
    }




    private ElementDefinitions() {
        super();
    }



    // TODO at this level, we will NEVER use char[] buffers but Strings, so we could just use a ConcurrentHashMap
    // and make IMarkupHandlers receive an ElementDefinition as argument instead of String normalizedName's.



    /*
     * <p>
     *     This class is <strong>thread-safe</strong>. The reason for this is that it not only contains the
     *     standard elements, but will also contain new instances of ElementDefinition created during processing
     *     (created when asking the repository for them when they do not exist yet). As any thread can create a new
     *     element, this has to be lock-protected.
     * </p>
     * <p>
     *     NOTE this structure is equivalent to the one used at org.attoparser.markup.html.elements.HtmlElements
     *     to keep track of the different HTML elements involved. So any bugs that might be found here should
     *     be also fixed at the corresponding attoparser class.
     * </p>
     */
    static final class ElementDefinitionRepository {

        private final List<ElementDefinition> repository;

        private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
        private final Lock readLock = this.lock.readLock();
        private final Lock writeLock = this.lock.writeLock();


        ElementDefinitionRepository() {
            this.repository = new ArrayList<ElementDefinition>(40);
        }



        ElementDefinition getElement(final char[] text, final int offset, final int len) {

            this.readLock.lock();
            try {

                final int index = binarySearch(this.repository, text, offset, len);

                if (index != -1) {
                    return this.repository.get(index);
                }

            } finally {
                this.readLock.unlock();
            }


            /*
             * NOT FOUND. We need to obtain a write lock and store the text
             */
            this.writeLock.lock();
            try {
                return storeElement(text, offset, len);
            } finally {
                this.writeLock.unlock();
            }

        }


        ElementDefinition getElement(final String text) {

            final String normalizedText = text.toLowerCase();

            this.readLock.lock();
            try {

                final int index = binarySearch(this.repository, normalizedText);

                if (index != -1) {
                    return this.repository.get(index);
                }

            } finally {
                this.readLock.unlock();
            }


            /*
             * NOT FOUND. We need to obtain a write lock and store the text
             */
            this.writeLock.lock();
            try {
                return storeElement(normalizedText);
            } finally {
                this.writeLock.unlock();
            }

        }


        private ElementDefinition storeElement(final char[] text, final int offset, final int len) {

            final int index = binarySearch(this.repository, text, offset, len);
            if (index != -1) {
                // It was already added while we were waiting for the lock!
                return this.repository.get(index);
            }

            // Every element that we do not know beforehand (because it is an HTML5 standard element
            // will be considered of type NORMAL. In most cases, these will be XML elements, so this is the
            // correct option.
            final ElementDefinition element =
                    new ElementDefinition(new String(text, offset, len).toLowerCase(), ElementType.NORMAL);


            this.repository.add(element);
            Collections.sort(this.repository, ElementComparator.INSTANCE);

            return element;

        }


        private ElementDefinition storeElement(final String text) {

            final int index = binarySearch(this.repository, text);
            if (index != -1) {
                // It was already added while we were waiting for the lock!
                return this.repository.get(index);
            }

            // Every element that we do not know beforehand (because it is an HTML5 standard element
            // will be considered of type NORMAL. In most cases, these will be XML elements, so this is the
            // correct option.
            final ElementDefinition element =
                    new ElementDefinition(text, ElementType.NORMAL); // Has already been lower-cased at getElement()

            this.repository.add(element);
            Collections.sort(this.repository,ElementComparator.INSTANCE);

            return element;

        }


        private ElementDefinition storeElement(final ElementDefinition element) {

            // This method will only be called from within the HtmlElements class itself, during initialization of
            // standard elements.

            this.repository.add(element);
            Collections.sort(this.repository,ElementComparator.INSTANCE);

            return element;

        }



        private static int binarySearch(final List<ElementDefinition> values,
                                        final String text) {

            int low = 0;
            int high = values.size() - 1;

            while (low <= high) {

                final int mid = (low + high) >>> 1;
                final String midVal = values.get(mid).getNormalizedName();

                final int cmp = midVal.compareTo(text);

                if (cmp < 0) {
                    low = mid + 1;
                } else if (cmp > 0) {
                    high = mid - 1;
                } else {
                    // Found!!
                    return mid;
                }

            }

            return -1;  // Not Found!!

        }


        private static int binarySearch(final List<ElementDefinition> values,
                                        final char[] text, final int offset, final int len) {

            int low = 0;
            int high = values.size() - 1;

            while (low <= high) {

                final int mid = (low + high) >>> 1;
                final String midVal = values.get(mid).getNormalizedName();

                final int cmp = compare(midVal, text, offset, len);

                if (cmp < 0) {
                    low = mid + 1;
                } else if (cmp > 0) {
                    high = mid - 1;
                } else {
                    // Found!!
                    return mid;
                }

            }

            return -1;  // Not Found!!

        }


        private static int compare(final String ncr, final char[] text, final int offset, final int len) {
            final int maxCommon = Math.min(ncr.length(), len);
            int i;
            for (i = 0; i < maxCommon; i++) {
                final char tc = Character.toLowerCase(text[offset + i]);
                if (ncr.charAt(i) < tc) {
                    return -1;
                } else if (ncr.charAt(i) > tc) {
                    return 1;
                }
            }
            if (ncr.length() > i) {
                return 1;
            }
            if (len > i) {
                return -1;
            }
            return 0;
        }


        private static class ElementComparator implements Comparator<ElementDefinition> {

            private static ElementComparator INSTANCE = new ElementComparator();

            public int compare(final ElementDefinition o1, final ElementDefinition o2) {
                return o1.getNormalizedName().compareTo(o2.getNormalizedName());
            }
        }

    }


}
