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
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ElementDefinitions {


    private static final ConcurrentHashMap<String,ElementDefinition> ELEMENT_DEFINITIONS =
            new ConcurrentHashMap<String, ElementDefinition>(115);
    private static final int ELEMENT_DEFINITIONS_MAX_SIZE = 1000; // Just in case some crazy markup appears



    // Set containing all the standard elements, for posible external reference
    public static final Set<ElementDefinition> ALL_STANDARD_ELEMENTS;
    // Set containing all the standard element names, for posible external reference
    public static final Set<String> ALL_STANDARD_ELEMENT_NAMES;



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
    public static final ElementDefinition RB = new ElementDefinition("rb", ElementType.NORMAL);
    public static final ElementDefinition RTC = new ElementDefinition("rtc", ElementType.NORMAL);
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
                                CODE, VAR, SAMP, KBD, SUB, SUP, I, B, U, MARK, RUBY, RB, RT, RTC,
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
            ELEMENT_DEFINITIONS.put(element.getName(), element);
        }


        ALL_STANDARD_ELEMENT_NAMES =
                Collections.unmodifiableSet(new LinkedHashSet<String>(ELEMENT_DEFINITIONS.keySet()));

    }




    public static ElementDefinition forName(final boolean caseSensitive, final String elementName) {

        if (elementName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }

        // We first try without executing toLowerCase(), in order to avoid unnecessary load when most
        // of the requests will be for the already normalized (lower-cased) version.
        ElementDefinition definition = ELEMENT_DEFINITIONS.get(elementName);
        if (definition != null) {
            return definition;
        }

        if (!caseSensitive) {
            definition = ELEMENT_DEFINITIONS.get(elementName.toLowerCase());
            if (definition != null) {
                return definition;
            }
            definition = new ElementDefinition(elementName.toLowerCase(), ElementType.NORMAL);
        } else {
            definition = new ElementDefinition(elementName, ElementType.NORMAL);
        }

        if (ELEMENT_DEFINITIONS.size() < ELEMENT_DEFINITIONS_MAX_SIZE) {
            ELEMENT_DEFINITIONS.putIfAbsent(definition.getName(), definition);
        }

        return definition;

    }




    private ElementDefinitions() {
        super();
    }



}
