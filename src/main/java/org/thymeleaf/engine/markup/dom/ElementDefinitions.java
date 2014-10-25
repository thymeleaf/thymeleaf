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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.attoparser.util.TextUtil;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ElementDefinitions {



    // We need two different repositories, for HTML and XML, because one is case-sensitive and the other is not.
    // Besides, we don't want HTML-only element types like "VOID" or "RAW_TEXT" be applied to XML elements even if they have the same name.
    // Also, there is no need to add any 'standard elements' to XML because other than the synthetic block, there are none, and avoiding its
    // creation we save a repository query each time an element is asked for.
    private static final ElementDefinitionRepository HTML_ELEMENT_REPOSITORY = new ElementDefinitionRepository(false, true);
    private static final ElementDefinitionRepository XML_ELEMENT_REPOSITORY = new ElementDefinitionRepository(true, false);



    // Set containing all the standard elements, for possible external reference
    public static final Set<ElementDefinition> ALL_STANDARD_HTML_ELEMENTS;
    // Set containing all the standard element names, for possible external reference
    public static final Set<String> ALL_STANDARD_HTML_ELEMENT_NAMES;



    // Root
    static final ElementDefinition HTML = new ElementDefinition("html", ElementType.NORMAL);

    // Document metadata
    static final ElementDefinition HEAD = new ElementDefinition("head", ElementType.NORMAL);
    static final ElementDefinition TITLE = new ElementDefinition("title", ElementType.ESCAPABLE_RAW_TEXT);
    static final ElementDefinition BASE = new ElementDefinition("base", ElementType.VOID);
    static final ElementDefinition LINK = new ElementDefinition("link", ElementType.VOID);
    static final ElementDefinition META = new ElementDefinition("meta", ElementType.VOID);
    static final ElementDefinition STYLE = new ElementDefinition("style", ElementType.RAW_TEXT);

    // Scripting
    static final ElementDefinition SCRIPT = new ElementDefinition("script", ElementType.RAW_TEXT);
    static final ElementDefinition NOSCRIPT = new ElementDefinition("noscript", ElementType.NORMAL);

    // Sections
    static final ElementDefinition BODY = new ElementDefinition("body", ElementType.NORMAL);
    static final ElementDefinition ARTICLE = new ElementDefinition("article", ElementType.NORMAL);
    static final ElementDefinition SECTION = new ElementDefinition("section", ElementType.NORMAL);
    static final ElementDefinition NAV = new ElementDefinition("nav", ElementType.NORMAL);
    static final ElementDefinition ASIDE = new ElementDefinition("aside", ElementType.NORMAL);
    static final ElementDefinition H1 = new ElementDefinition("h1", ElementType.NORMAL);
    static final ElementDefinition H2 = new ElementDefinition("h2", ElementType.NORMAL);
    static final ElementDefinition H3 = new ElementDefinition("h3", ElementType.NORMAL);
    static final ElementDefinition H4 = new ElementDefinition("h4", ElementType.NORMAL);
    static final ElementDefinition H5 = new ElementDefinition("h5", ElementType.NORMAL);
    static final ElementDefinition H6 = new ElementDefinition("h6", ElementType.NORMAL);
    static final ElementDefinition HGROUP = new ElementDefinition("hgroup", ElementType.NORMAL);
    static final ElementDefinition HEADER = new ElementDefinition("header", ElementType.NORMAL);
    static final ElementDefinition FOOTER = new ElementDefinition("footer", ElementType.NORMAL);
    static final ElementDefinition ADDRESS = new ElementDefinition("address", ElementType.NORMAL);
    static final ElementDefinition MAIN = new ElementDefinition("main", ElementType.NORMAL);

    // Grouping content
    static final ElementDefinition P = new ElementDefinition("p", ElementType.NORMAL);
    static final ElementDefinition HR = new ElementDefinition("hr", ElementType.VOID);
    static final ElementDefinition PRE = new ElementDefinition("pre", ElementType.NORMAL);
    static final ElementDefinition BLOCKQUOTE = new ElementDefinition("blockquote", ElementType.NORMAL);
    static final ElementDefinition OL = new ElementDefinition("ol", ElementType.NORMAL);
    static final ElementDefinition UL = new ElementDefinition("ul", ElementType.NORMAL);
    static final ElementDefinition LI = new ElementDefinition("li", ElementType.NORMAL);
    static final ElementDefinition DL = new ElementDefinition("dl", ElementType.NORMAL);
    static final ElementDefinition DT = new ElementDefinition("dt", ElementType.NORMAL);
    static final ElementDefinition DD = new ElementDefinition("dd", ElementType.NORMAL);
    static final ElementDefinition FIGURE = new ElementDefinition("figure", ElementType.NORMAL);
    static final ElementDefinition FIGCAPTION = new ElementDefinition("figcaption", ElementType.NORMAL);
    static final ElementDefinition DIV = new ElementDefinition("div", ElementType.NORMAL);

    // Text-level semantics
    static final ElementDefinition A = new ElementDefinition("a", ElementType.NORMAL);
    static final ElementDefinition EM = new ElementDefinition("em", ElementType.NORMAL);
    static final ElementDefinition STRONG = new ElementDefinition("strong", ElementType.NORMAL);
    static final ElementDefinition SMALL = new ElementDefinition("small", ElementType.NORMAL);
    static final ElementDefinition S = new ElementDefinition("s", ElementType.NORMAL);
    static final ElementDefinition CITE = new ElementDefinition("cite", ElementType.NORMAL);
    static final ElementDefinition G = new ElementDefinition("g", ElementType.NORMAL);
    static final ElementDefinition DFN = new ElementDefinition("dfn", ElementType.NORMAL);
    static final ElementDefinition ABBR = new ElementDefinition("abbr", ElementType.NORMAL);
    static final ElementDefinition TIME = new ElementDefinition("time", ElementType.NORMAL);
    static final ElementDefinition CODE = new ElementDefinition("code", ElementType.NORMAL);
    static final ElementDefinition VAR = new ElementDefinition("var", ElementType.NORMAL);
    static final ElementDefinition SAMP = new ElementDefinition("samp", ElementType.NORMAL);
    static final ElementDefinition KBD = new ElementDefinition("kbd", ElementType.NORMAL);
    static final ElementDefinition SUB = new ElementDefinition("sub", ElementType.NORMAL);
    static final ElementDefinition SUP = new ElementDefinition("sup", ElementType.NORMAL);
    static final ElementDefinition I = new ElementDefinition("i", ElementType.NORMAL);
    static final ElementDefinition B = new ElementDefinition("b", ElementType.NORMAL);
    static final ElementDefinition U = new ElementDefinition("u", ElementType.NORMAL);
    static final ElementDefinition MARK = new ElementDefinition("mark", ElementType.NORMAL);
    static final ElementDefinition RUBY = new ElementDefinition("ruby", ElementType.NORMAL);
    static final ElementDefinition RB = new ElementDefinition("rb", ElementType.NORMAL);
    static final ElementDefinition RT = new ElementDefinition("rt", ElementType.NORMAL);
    static final ElementDefinition RTC = new ElementDefinition("rtc", ElementType.NORMAL);
    static final ElementDefinition RP = new ElementDefinition("rp", ElementType.NORMAL);
    static final ElementDefinition BDI = new ElementDefinition("bdi", ElementType.NORMAL);
    static final ElementDefinition BDO = new ElementDefinition("bdo", ElementType.NORMAL);
    static final ElementDefinition SPAN = new ElementDefinition("span", ElementType.NORMAL);
    static final ElementDefinition BR = new ElementDefinition("br", ElementType.VOID);
    static final ElementDefinition WBR = new ElementDefinition("wbr", ElementType.VOID);

    // Edits
    static final ElementDefinition INS = new ElementDefinition("ins", ElementType.NORMAL);
    static final ElementDefinition DEL = new ElementDefinition("del", ElementType.NORMAL);

    // Embedded content
    static final ElementDefinition IMG = new ElementDefinition("img", ElementType.VOID);
    static final ElementDefinition IFRAME = new ElementDefinition("iframe", ElementType.NORMAL);
    static final ElementDefinition EMBED = new ElementDefinition("embed", ElementType.VOID);
    static final ElementDefinition OBJECT = new ElementDefinition("object", ElementType.NORMAL);
    static final ElementDefinition PARAM = new ElementDefinition("param", ElementType.VOID);
    static final ElementDefinition VIDEO = new ElementDefinition("video", ElementType.NORMAL);
    static final ElementDefinition AUDIO = new ElementDefinition("audio", ElementType.NORMAL);
    static final ElementDefinition SOURCE = new ElementDefinition("source", ElementType.VOID);
    static final ElementDefinition TRACK = new ElementDefinition("track", ElementType.VOID);
    static final ElementDefinition CANVAS = new ElementDefinition("canvas", ElementType.NORMAL);
    static final ElementDefinition MAP = new ElementDefinition("map", ElementType.NORMAL);
    static final ElementDefinition AREA = new ElementDefinition("area", ElementType.VOID);

    // Tabular data
    static final ElementDefinition TABLE = new ElementDefinition("table", ElementType.NORMAL);
    static final ElementDefinition CAPTION = new ElementDefinition("caption", ElementType.NORMAL);
    static final ElementDefinition COLGROUP = new ElementDefinition("colgroup", ElementType.NORMAL);
    static final ElementDefinition COL = new ElementDefinition("col", ElementType.VOID);
    static final ElementDefinition TBODY = new ElementDefinition("tbody", ElementType.NORMAL);
    static final ElementDefinition THEAD = new ElementDefinition("thead", ElementType.NORMAL);
    static final ElementDefinition TFOOT = new ElementDefinition("tfoot", ElementType.NORMAL);
    static final ElementDefinition TR = new ElementDefinition("tr", ElementType.NORMAL);
    static final ElementDefinition TD = new ElementDefinition("td", ElementType.NORMAL);
    static final ElementDefinition TH = new ElementDefinition("th", ElementType.NORMAL);

    // Forms
    static final ElementDefinition FORM = new ElementDefinition("form", ElementType.NORMAL);
    static final ElementDefinition FIELDSET = new ElementDefinition("fieldset", ElementType.NORMAL);
    static final ElementDefinition LEGEND = new ElementDefinition("legend", ElementType.NORMAL);
    static final ElementDefinition LABEL = new ElementDefinition("label", ElementType.NORMAL);
    static final ElementDefinition INPUT = new ElementDefinition("input", ElementType.VOID);
    static final ElementDefinition BUTTON = new ElementDefinition("button", ElementType.NORMAL);
    static final ElementDefinition SELECT = new ElementDefinition("select", ElementType.NORMAL);
    static final ElementDefinition DATALIST = new ElementDefinition("datalist", ElementType.NORMAL);
    static final ElementDefinition OPTGROUP = new ElementDefinition("optgroup", ElementType.NORMAL);
    static final ElementDefinition OPTION = new ElementDefinition("option", ElementType.NORMAL);
    static final ElementDefinition TEXTAREA = new ElementDefinition("textarea", ElementType.ESCAPABLE_RAW_TEXT);
    static final ElementDefinition KEYGEN = new ElementDefinition("keygen", ElementType.VOID);
    static final ElementDefinition OUTPUT = new ElementDefinition("output", ElementType.NORMAL);
    static final ElementDefinition PROGRESS = new ElementDefinition("progress", ElementType.NORMAL);
    static final ElementDefinition METER = new ElementDefinition("meter", ElementType.NORMAL);

    // Interactive elements
    static final ElementDefinition DETAILS = new ElementDefinition("details", ElementType.NORMAL);
    static final ElementDefinition SUMMARY = new ElementDefinition("summary", ElementType.NORMAL);
    static final ElementDefinition COMMAND = new ElementDefinition("command", ElementType.NORMAL);
    static final ElementDefinition MENU = new ElementDefinition("menu", ElementType.NORMAL);
    static final ElementDefinition MENUITEM = new ElementDefinition("menuitem", ElementType.VOID);
    static final ElementDefinition DIALOG = new ElementDefinition("dialog", ElementType.NORMAL);

    // WebComponents
    static final ElementDefinition TEMPLATE = new ElementDefinition("template", ElementType.RAW_TEXT);
    static final ElementDefinition ELEMENT = new ElementDefinition("element", ElementType.NORMAL);
    static final ElementDefinition DECORATOR = new ElementDefinition("decorator", ElementType.NORMAL);
    static final ElementDefinition CONTENT = new ElementDefinition("content", ElementType.NORMAL);
    static final ElementDefinition SHADOW = new ElementDefinition("shadow", ElementType.NORMAL);


    // Thymeleaf standard dialect elements
    // We might save some time asking the non-synchronized repository for these, instead of the lock-synchronized
    // read-write one (at least if we are using the standard dialect default prefix "th").
    static final ElementDefinition THBLOCK = new ElementDefinition("th:block", ElementType.NORMAL);
    static final ElementDefinition DATATHBLOCK = new ElementDefinition("th-block", ElementType.NORMAL);





    static {

        final List<ElementDefinition> htmlElementDefinitionListAux =
                new ArrayList<ElementDefinition>(Arrays.asList(
                        new ElementDefinition[] {
                                HTML, HEAD, TITLE, BASE, LINK, META, STYLE, SCRIPT, NOSCRIPT, BODY, ARTICLE,
                                SECTION, NAV, ASIDE, H1, H2, H3, H4, H5, H6, HGROUP, HEADER, FOOTER,
                                ADDRESS, P, HR, PRE, BLOCKQUOTE, OL, UL, LI, DL, DT, DD, FIGURE,
                                FIGCAPTION, DIV, A, EM, STRONG, SMALL, S, CITE, G, DFN, ABBR, TIME,
                                CODE, VAR, SAMP, KBD, SUB, SUP, I, B, U, MARK, RUBY, RB, RT, RTC,
                                RP, BDI, BDO, SPAN, BR, WBR, INS, DEL, IMG, IFRAME, EMBED, OBJECT,
                                PARAM, VIDEO, AUDIO, SOURCE, TRACK, CANVAS, MAP, AREA, TABLE, CAPTION,
                                COLGROUP, COL, TBODY, THEAD, TFOOT, TR, TD, TH, FORM, FIELDSET, LEGEND, LABEL,
                                INPUT, BUTTON, SELECT, DATALIST, OPTGROUP, OPTION, TEXTAREA, KEYGEN, OUTPUT, PROGRESS,
                                METER, DETAILS, SUMMARY, COMMAND, MENU, MENUITEM, DIALOG, MAIN, TEMPLATE,
                                ELEMENT, DECORATOR, CONTENT, SHADOW
                        }));

        Collections.sort(htmlElementDefinitionListAux, ElementComparator.forCaseSensitive(false));


        ALL_STANDARD_HTML_ELEMENTS =
                Collections.unmodifiableSet(new LinkedHashSet<ElementDefinition>(htmlElementDefinitionListAux));


        final LinkedHashSet<String> htmlElementDefinitionNamesAux = new LinkedHashSet<String>(ALL_STANDARD_HTML_ELEMENTS.size() + 1, 1.0f);
        for (final ElementDefinition elementDefinition : ALL_STANDARD_HTML_ELEMENTS) {
            htmlElementDefinitionNamesAux.add(elementDefinition.getName());
        }

        ALL_STANDARD_HTML_ELEMENT_NAMES = Collections.unmodifiableSet(htmlElementDefinitionNamesAux);



        /*
         * Register the standard elements at the element repository, in order to initialize it
         */
        for (final ElementDefinition elementDefinition : ALL_STANDARD_HTML_ELEMENTS) {
            HTML_ELEMENT_REPOSITORY.storeStandardElement(elementDefinition);
        }

        // The Thymeleaf elements are added to the repos, but not to the standard element/names sets, as they aren't
        // truly standard HTML elements.
        HTML_ELEMENT_REPOSITORY.storeStandardElement(THBLOCK);
        HTML_ELEMENT_REPOSITORY.storeStandardElement(DATATHBLOCK);
        XML_ELEMENT_REPOSITORY.storeStandardElement(THBLOCK);
        XML_ELEMENT_REPOSITORY.storeStandardElement(DATATHBLOCK);


    }




    public static ElementDefinition forHtmlName(final String elementName) {
        if (elementName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        return HTML_ELEMENT_REPOSITORY.getElement(elementName);
    }


    public static ElementDefinition forHtmlName(final char[] elementName, final int elementNameOffset, final int elementNameLen) {
        if (elementName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return HTML_ELEMENT_REPOSITORY.getElement(elementName, elementNameOffset, elementNameLen);
    }



    public static ElementDefinition forXmlName(final String elementName) {
        if (elementName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        return XML_ELEMENT_REPOSITORY.getElement(elementName);
    }


    public static ElementDefinition forXmlName(final char[] elementName, final int elementNameOffset, final int elementNameLen) {
        if (elementName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return XML_ELEMENT_REPOSITORY.getElement(elementName, elementNameOffset, elementNameLen);
    }






    private ElementDefinitions() {
        super();
    }






    /*
     * This repository class is thread-safe. The reason for this is that it not only contains the
     * standard elements, but will also contain new instances of ElementDefinition created during processing (created
     * when asking the repository for them when they do not exist yet). As any thread can create a new element,
     * this has to be lock-protected.
     */
    static final class ElementDefinitionRepository {

        private final boolean caseSensitive;

        private final List<ElementDefinition> standardRepository; // read-only, no sync needed
        private final List<ElementDefinition> repository;  // read-write, sync will be needed

        private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
        private final Lock readLock = this.lock.readLock();
        private final Lock writeLock = this.lock.writeLock();


        ElementDefinitionRepository(final boolean caseSensitive, final boolean createStandardRepo) {
            super();
            this.caseSensitive = caseSensitive;
            this.standardRepository = (createStandardRepo ? new ArrayList<ElementDefinition>(150) : null);
            this.repository = new ArrayList<ElementDefinition>(150);
        }


        ElementDefinition getElement(final char[] text, final int offset, final int len) {

            int index;

            if (this.standardRepository != null) {
                /*
                 * We first try to find it in the repository containing the standard elements, which does not need
                 * any synchronization.
                 */
                index = binarySearch(this.caseSensitive, this.standardRepository, text, offset, len);

                if (index >= 0) {
                    return this.standardRepository.get(index);
                }
            }

            /*
             * We did not find it in the repository of standard elements, so let's try in the read+write one,
             * which does require synchronization through a readwrite lock.
             */

            this.readLock.lock();
            try {

                index = binarySearch(this.caseSensitive, this.repository, text, offset, len);

                if (index >= 0) {
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

            int index;

            if (this.standardRepository != null) {
                /*
                 * We first try to find it in the repository containing the standard elements, which does not need
                 * any synchronization.
                 */
                index = binarySearch(this.caseSensitive, this.standardRepository, text);

                if (index >= 0) {
                    return this.standardRepository.get(index);
                }
            }

            /*
             * We did not find it in the repository of standard elements, so let's try in the read+write one,
             * which does require synchronization through a readwrite lock.
             */

            this.readLock.lock();
            try {

                index = binarySearch(this.caseSensitive, this.repository, text);

                if (index >= 0) {
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
                return storeElement(text);
            } finally {
                this.writeLock.unlock();
            }

        }


        private ElementDefinition storeElement(final char[] text, final int offset, final int len) {

            final int index = binarySearch(this.caseSensitive, this.repository, text, offset, len);
            if (index >= 0) {
                // It was already added while we were waiting for the lock!
                return this.repository.get(index);
            }

            final String elementName = new String(text, offset, len);

            final ElementDefinition element =
                    new ElementDefinition((this.caseSensitive? elementName : elementName.toLowerCase()), ElementType.NORMAL);

            // binary Search returned (-(insertion point) - 1)
            this.repository.add(((index + 1) * -1), element);

            return element;

        }


        private ElementDefinition storeElement(final String text) {

            final int index = binarySearch(this.caseSensitive, this.repository, text);
            if (index >= 0) {
                // It was already added while we were waiting for the lock!
                return this.repository.get(index);
            }

            final String elementName = text;

            final ElementDefinition element =
                    new ElementDefinition((this.caseSensitive? elementName : elementName.toLowerCase()), ElementType.NORMAL);

            // binary Search returned (-(insertion point) - 1)
            this.repository.add(((index + 1) * -1), element);

            return element;

        }


        private ElementDefinition storeStandardElement(final ElementDefinition element) {

            // This method will only be called from within the HtmlElements class itself, during initialization of
            // standard elements.

            if (this.standardRepository != null) {
                this.standardRepository.add(element);
                Collections.sort(this.standardRepository, ElementComparator.forCaseSensitive(this.caseSensitive));
            }

            this.repository.add(element);
            Collections.sort(this.repository, ElementComparator.forCaseSensitive(this.caseSensitive));

            return element;

        }


        private static int binarySearch(final boolean caseSensitive, final List<ElementDefinition> values,
                                        final char[] text, final int offset, final int len) {

            int low = 0;
            int high = values.size() - 1;

            int mid, cmp;
            String midVal;

            while (low <= high) {

                mid = (low + high) >>> 1;
                midVal = values.get(mid).name;

                cmp = TextUtil.compareTo(caseSensitive, midVal, 0, midVal.length(), text, offset, len);

                if (cmp < 0) {
                    low = mid + 1;
                } else if (cmp > 0) {
                    high = mid - 1;
                } else {
                    // Found!!
                    return mid;
                }

            }

            return -(low + 1);  // Not Found!! We return (-(insertion point) - 1), to guarantee all non-founds are < 0

        }


        private static int binarySearch(final boolean caseSensitive, final List<ElementDefinition> values, final String text) {

            int low = 0;
            int high = values.size() - 1;

            int mid, cmp;
            String midVal;

            while (low <= high) {

                mid = (low + high) >>> 1;
                midVal = values.get(mid).name;

                cmp = TextUtil.compareTo(caseSensitive, midVal, text);

                if (cmp < 0) {
                    low = mid + 1;
                } else if (cmp > 0) {
                    high = mid - 1;
                } else {
                    // Found!!
                    return mid;
                }

            }

            return -(low + 1);  // Not Found!! We return (-(insertion point) - 1), to guarantee all non-founds are < 0

        }


    }


    private static class ElementComparator implements Comparator<ElementDefinition> {

        private static ElementComparator INSTANCE_CASE_SENSITIVE = new ElementComparator(true);
        private static ElementComparator INSTANCE_CASE_INSENSITIVE = new ElementComparator(false);

        private final boolean caseSensitive;

        static ElementComparator forCaseSensitive(final boolean caseSensitive) {
            return caseSensitive ? INSTANCE_CASE_SENSITIVE : INSTANCE_CASE_INSENSITIVE;
        }

        private ElementComparator(final boolean caseSensitive) {
            super();
            this.caseSensitive = caseSensitive;
        }

        public int compare(final ElementDefinition o1, final ElementDefinition o2) {
            // caseSensitive is true here because we might have
            return TextUtil.compareTo(this.caseSensitive, o1.name, o2.name);
        }
    }

}
